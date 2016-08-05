/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team,
 * http://bibiserv.cebitec.uni-bielefeld.de/, All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.unibi.cebitec.bibiserv.server.manager.context;

import de.unibi.cebitec.bibiserv.search.BiBiServSearch;
import de.unibi.cebitec.bibiserv.server.manager.utilities.AdminRootClassLoaderImpl;
import de.unibi.cebitec.bibiserv.server.manager.utilities.RootClassLoader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.w3c.dom.Document;

/**
 * This class implements a special kind of WebApplicationContext for the
 * BiBiServ Environment. It acts like a standard WebApplicationContext which is
 * additionally able to add and remove bean definitions for bar-file packaged
 * tools running in a BiBiServ-based environment.
 *
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 * Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de (search impl.)
 */
public class BiBiWebApplicationContext extends GenericWebApplicationContext implements ConfigurableWebApplicationContext {

    private static Logger log = Logger.getLogger("de.unibi.cebitec.bibiserv.server.manager.context");
    public static final String ADMIN_RCL_NAME = "adminrootclassloader";

    public BiBiWebApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
        RootClassLoader rcl = new RootClassLoader(getClassLoader());
        rcl.addAppClassLoader(ADMIN_RCL_NAME, new AdminRootClassLoaderImpl(rcl));
        setClassLoader(rcl);
    }

    public BiBiWebApplicationContext() {
        RootClassLoader rcl = new RootClassLoader(getClassLoader());
        rcl.addAppClassLoader(ADMIN_RCL_NAME, new AdminRootClassLoaderImpl(rcl));
        setClassLoader(rcl);
    }
    //own functions and properties
    private HashMap<String, GenericApplicationContext> toolContexts = new HashMap<String, GenericApplicationContext>();
    private HashMap<String, GenericApplicationContext> moduleContexts = new HashMap<String, GenericApplicationContext>();
    private MessageSource messageSource;

    /**
     * add a tool (aka runnable item) to this context,
     * extracting its Bean definitions from its own context to this one
     * add tool description to message source.
     *
     * BP: This also adds the item to the search instance.
     * JK : remove any checks, method should only be called with valid values
     */
    public void addRunnableItem(String id, GenericWebApplicationContext tc,
            Document runnableItemDoc, File appDir) {

        //first, check if we already have this tool, and remove it
        //if necessary
        if (toolContexts.containsKey(id)) {
            removeRunnableItem(id);
        }

        String[] beans = tc.getBeanDefinitionNames();
        for (String string : beans) {
            this.registerBeanDefinition(string, tc.getBeanDefinition(string));
        }
        //add toolcontext reference to administrative hashmap
        toolContexts.put(id, tc);

        //add tool to search.
        BiBiServSearch search = BiBiServSearch.getInstance();
        search.addItem(runnableItemDoc, appDir);

        //add tooldescription to messagesource
        ((BiBiMessageSource) messageSource).addToolDescription(id, runnableItemDoc);
    }

    /**
     * Add a Item (also linkedItem) to message source and search index.
     *
     * @param id
     * @param runnableItemDoc
     */
    public void addItem(String id, Document itemDoc) {
        // remove probably previous installed item from message source
        ((BiBiMessageSource) messageSource).removeToolDescription(id);
        // and install new one 
        ((BiBiMessageSource) messageSource).addToolDescription(id, itemDoc);
        //do the same for the search instance.
        BiBiServSearch search = BiBiServSearch.getInstance();
        search.removeItem(itemDoc);
        search.addItem(itemDoc, null);
    }

    /**
     * remove a runnableItem from this context (delete its Bean definition,
     * remove it from message source and deletes it in search index).
     */
    public void removeRunnableItem(String id) {
        if (toolContexts.containsKey(id)) {
            GenericWebApplicationContext gc = (GenericWebApplicationContext) toolContexts.get(id);
            String[] beans = gc.getBeanDefinitionNames();
            for (String string : beans) {
                removeBeanDefinition(string);
            }
            ((BiBiMessageSource) messageSource).removeToolDescription(id);
            gc.close();
            gc.destroy();
            //remove it from search.
            BiBiServSearch search = BiBiServSearch.getInstance();
            search.removeItem(id);
        }
        toolContexts.remove(id);
    }

    /**
     * Remove a Item from context (remove it from message source and from
     * search.)
     *
     * @param id
     */
    public void removeItem(String id) {
        ((BiBiMessageSource) messageSource).removeToolDescription(id);
        //remove it from search.
        BiBiServSearch search = BiBiServSearch.getInstance();
        search.removeItem(id);
    }

    public void addModuleContext(String id, GenericWebApplicationContext context) {

        /*
         * check for NULL in case the context could not be created
         */
        if (context != null) {

            //first, check if we already have this module, and remove it
            if (moduleContexts.containsKey(id)) {
                removeModuleContext(id);
            }

            String[] beans = context.getBeanDefinitionNames();
            for (String bean : beans) {
                this.registerBeanDefinition(bean, context.getBeanDefinition(bean));
            }
            //add module reference to administrative hashmap
            moduleContexts.put(id, context);
        }


    }

    public void removeModuleContext(String id) {
        log.debug("------called removeModuleContext: '" + id + "'");

        if (moduleContexts.containsKey(id)) {
            GenericWebApplicationContext c = (GenericWebApplicationContext) moduleContexts.get(id);
            String[] beans = c.getBeanDefinitionNames();
            for (String string : beans) {
                removeBeanDefinition(string);
            }
            c.close();
            c.destroy();
        }
        moduleContexts.remove(id);
    }
    /*
     * -----------------------------------------------------------
     */
    //necessary stuff for interface implementation
    /*
     * -----------------------------------------------------------
     */
    /*
     * -----------------------------------------------------------
     */
    /**
     * Servlet config that this context runs in, if any
     */
    private ServletConfig servletConfig;
    /**
     * Servlet context that this context runs in
     */
    private ServletContext servletContext;

    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
        if (servletConfig != null && this.servletContext == null) {
            this.servletContext = servletConfig.getServletContext();
        }
    }

    public ServletConfig getServletConfig() {
        return this.servletConfig;
    }
    /*
     * -----------------------------------------------------------
     */
    /*
     * -----------------------------------------------------------
     */
    /**
     * Namespace of this context, or
     * <code>null</code> if root
     */
    private String namespace;

    public void setNamespace(String namespace) {
        this.namespace = namespace;
        if (namespace != null) {
            setDisplayName("WebApplicationContext for namespace '" + namespace + "'");
        }
    }

    public String getNamespace() {
        return this.namespace;
    }
    /*
     * -----------------------------------------------------------
     */
    private String[] configLocations;
    /**
     * Default config location for the root context
     */
    public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";
    /**
     * Default prefix for building a config location for a namespace
     */
    public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";
    /**
     * Default suffix for building a config location for a namespace
     */
    public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".xml";

    /**
     * Set the config locations for this application context in init-param
     * style, i.e. with distinct locations separated by commas, semicolons or
     * whitespace. <p>If not set, the implementation may use a default as
     * appropriate.
     */
    public void setConfigLocation(String location) {
        setConfigLocations(StringUtils.tokenizeToStringArray(location, CONFIG_LOCATION_DELIMITERS));
    }

    /**
     * Set the config locations for this application context. <p>If not set, the
     * implementation may use a default as appropriate.
     */
    public void setConfigLocations(String[] locations) {
        if (locations != null) {
            Assert.noNullElements(locations, "Config locations must not be null");
            this.configLocations = new String[locations.length];
            for (int i = 0; i < locations.length; i++) {
                this.configLocations[i] = resolvePath(locations[i]).trim();
            }
            //load configuration from given locations
            try {
                this.loadBeanDefinitions((DefaultListableBeanFactory) getBeanFactory());
            } catch (IOException ex) {
                log.debug(ex.getLocalizedMessage());
            }
        } else {
            this.configLocations = null;
        }
    }

    /**
     * Return an array of resource locations, referring to the XML bean
     * definition files that this context should be built with. Can also include
     * location patterns, which will get resolved via a ResourcePatternResolver.
     * <p>The default implementation returns
     * <code>null</code>. Subclasses can override this to provide a set of
     * resource locations to load bean definitions from.
     *
     * @return an array of resource locations, or
     * <code>null</code> if none
     * @see #getResources
     * @see #getResourcePatternResolver
     */
    public String[] getConfigLocations() {
        return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
    }

    protected String[] getDefaultConfigLocations() {
        if (getNamespace() != null) {
            return new String[]{DEFAULT_CONFIG_LOCATION_PREFIX + getNamespace() + DEFAULT_CONFIG_LOCATION_SUFFIX};
        } else {
            return new String[]{DEFAULT_CONFIG_LOCATION};
        }
    }

    /**
     * Resolve the given path, replacing placeholders with corresponding system
     * property values if necessary. Applied to config locations.
     *
     * @param path the original file path
     * @return the resolved file path
     * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders
     */
    protected String resolvePath(String path) {
        return SystemPropertyUtils.resolvePlaceholders(path);
    }

    /**
     * Loads the bean definitions via an XmlBeanDefinitionReader.
     *
     * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
     * @see #initBeanDefinitionReader
     * @see #loadBeanDefinitions
     */
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException {
        log.debug("called 'loadBeanDefinitions(beanFactory)' in BiBiWebAppContext");
        // Create a new XmlBeanDefinitionReader for the given BeanFactory.
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // Configure the bean definition reader with this context's
        // resource loading environment.
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // proceed with actually loading the bean definitions.
        loadBeanDefinitions(beanDefinitionReader);
    }

    /**
     * Load the bean definitions with the given XmlBeanDefinitionReader. <p>The
     * lifecycle of the bean factory is handled by the refreshBeanFactory
     * method; therefore this method is just supposed to load and/or register
     * bean definitions. <p>Delegates to a ResourcePatternResolver for resolving
     * location patterns into Resource instances.
     *
     * @throws org.springframework.beans.BeansException in case of bean
     * registration errors
     * @throws java.io.IOException if the required XML document isn't found
     * @see #refreshBeanFactory
     * @see #getConfigLocations
     * @see #getResources
     * @see #getResourcePatternResolver
     */
    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
        log.debug("called 'loadBeanDefinitions(XmlBeanDefinitionReader)' in BiBiWebAppContext");
        String[] configLocations_ = getConfigLocations();
        if (configLocations_ != null) {
            for (int i = 0; i < configLocations_.length; i++) {
                reader.loadBeanDefinitions(configLocations_[i]);
            }
        }
    }

    /**
     * MessageSource stuff. Mostly copied from AbstractApplicationContext.
     */
    /**
     * Initialize the MessageSource. Use parent's if none defined in this
     * context.
     */
    @Override
    protected void initMessageSource() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
            this.messageSource = (MessageSource) beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
            // Make MessageSource aware of parent MessageSource.
            if (this.getParent() != null && this.messageSource instanceof HierarchicalMessageSource) {
                HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
                if (hms.getParentMessageSource() == null) {
                    // Only set parent context as parent MessageSource if no parent MessageSource
                    // registered already.
                    hms.setParentMessageSource(getInternalParentMessageSource());
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Using MessageSource [" + this.messageSource + "]");
            }
        } else {
            // Use empty MessageSource to be able to accept getMessage calls.
            DelegatingMessageSource dms = new DelegatingMessageSource();
            dms.setParentMessageSource(getInternalParentMessageSource());
            this.messageSource = dms;
            beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to locate MessageSource with name '" + MESSAGE_SOURCE_BEAN_NAME
                        + "': using default [" + this.messageSource + "]");
            }
        }
    }

    //---------------------------------------------------------------------
    // Implementation of MessageSource interface
    //---------------------------------------------------------------------
    @Override
    public String getMessage(String code, Object args[], String defaultMessage, Locale locale) {
        return getMessageSource().getMessage(code, args, defaultMessage, locale);
    }

    @Override
    public String getMessage(String code, Object args[], Locale locale) throws NoSuchMessageException {
        return getMessageSource().getMessage(code, args, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return getMessageSource().getMessage(resolvable, locale);
    }

    /**
     * Return the internal MessageSource used by the context.
     *
     * @return the internal MessageSource (never
     * <code>null</code>)
     * @throws IllegalStateException if the context has not been initialized yet
     */
    private MessageSource getMessageSource() throws IllegalStateException {
        if (this.messageSource == null) {
            throw new IllegalStateException("MessageSource not initialized - "
                    + "call 'refresh' before accessing messages via the context: " + this);
        }
        return this.messageSource;
    }
}
