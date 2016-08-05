/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted 2010-2013 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.server.manager;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import de.unibi.cebitec.bibiserv.server.manager.context.BiBiWebApplicationContext;
import de.unibi.cebitec.bibiserv.server.manager.utilities.AdminModClassLoader;
import de.unibi.cebitec.bibiserv.server.manager.utilities.AdminModClassLoaderImpl;
import de.unibi.cebitec.bibiserv.server.manager.utilities.AdminRootClassLoader;
import de.unibi.cebitec.bibiserv.server.manager.utilities.AppClassLoader;
import de.unibi.cebitec.bibiserv.server.manager.utilities.AppClassLoaderImpl;
import de.unibi.cebitec.bibiserv.server.manager.utilities.BarConfigFilter;
import de.unibi.cebitec.bibiserv.server.manager.utilities.RootClassLoader;
import de.unibi.cebitec.bibiserv.server.manager.utilities.StringUtil;
import de.unibi.cebitec.bibiserv.server.manager.utilities.ZIPTool;
import de.unibi.cebitec.bibiserv.web.menu.ToolMenuCreator;
import de.unibi.cebitec.bibiserv.web.xmlreader.BiBiServXMLReader;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.cms.ObjectFactory;
import de.unibi.techfak.bibiserv.cms.Tcategory;
import de.unibi.techfak.bibiserv.cms.Titem;
import de.unibi.techfak.bibiserv.cms.Tperson;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import de.unibi.techfak.bibiserv.util.ontoaccess.PnPOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPerson;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.BiBiPersonImplementation;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Core Implementation of the Manager component. The Core class should
 * <b>never</b> instantiated directly. Use method getInstanceOf() instead.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de (search
 * impl.)
 */
public class Core {

    //private ConfigurableListableBeanFactory clbf = null;
    private ClassLoader wcl = null;
    private DataSource datasource = null;
    private File rootdir = null;
    private File moduleRootDir = null;
    private RootClassLoader rcl = null;
    private BiBiWebApplicationContext ctx = null;

    private static Core singleton;
    private final static Logger LOG = Logger.getLogger("de.unibi.cebitec.bibiserv.server.manager");
    private BiBiServXMLReader xmlrequest;
    public final String MODULE_ROOT_DIR = "admin";
    // fields used for dynamic JAXRS reload extensions
    private final Set<ServletContainer> sc_set = new HashSet();
    private final Map<String, Set<Class>> restclazz_map = new HashMap();

    /**
     * Simple Constructor. Should never be called directly. Use static method
     * getInstanceOf() instead.
     *
     * @throws Exception
     */
    public Core() throws Exception {
        /*
         * initialize DataSource connection
         */
        datasource = BiBiTools.getDataSource();
    }

    /**
     * Deploy an application (as BAR archive).
     *
     * @param input - zipped byte[] of BAR
     * @throws ManagerException
     */
    public void deploy(byte[] input) throws ManagerException {
        deploy(null, input);
    }

    /**
     * Deploy an application (as BAR archive).
     *
     * @param name - Name(ID), this name is used instead of the default id
     * (coded in the BAR).
     * @param input - zipped byte [] of BAR
     *
     * @throws ManagerException in case of Exception
     */
    public void deploy(String name, byte[] input) throws ManagerException {


        /*
         * Get runnableitem.xml from BAR ...
         */
        byte[] runnableitem = null;  //@TODO : geht vermutlich einfacher, s.o.
        try {
            runnableitem = ZIPTool.extractNamedEntryfromZippedBuffer(input, "config/runnableitem.xml");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new ManagerException("Exception extracting tool description from archive!", e);
        }


        /*
         * ... and create a DOM document of the tooldescription to get the tool id and 
         * hand out to the messagesource for property parsing.
         */
        Document document = null;
        LOG.debug("Trying to create DOM Document of description for app '" + name + "'");
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(runnableitem);
            document = builder.parse(byteArrayInputStream);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error(e.getMessage());
            throw new ManagerException("Tool description can't be parsed as DOM document.", e);
        }

        // if name is unset, extract name/id from tooldescription
        if (name == null) {
            // Sourcen aus dem alten ManagerClient 
            Node id_attr = document.getDocumentElement().getAttributeNode("id");

            if (id_attr == null) {
                throw new ManagerException("BiBiServ Archive does not contain a valid tooldescription (unique tool id is missing)!");
            }
            name = id_attr.getNodeValue();
        }

        /*
         * this method tries a number of necessary steps for deployment of a new
         * app from a bar file. It bails out on any errors, throwing
         * ManagerException. ToDo: It should restore the old status on errors!
         */
        checkRootDir();

        final File webappdir = new File(rootdir, name);

        /*
         * check if an webapp with same name exists ...
         */
        if (webappdir.exists()) {
            /*
             * ... and undeploy it WITHOUT REMOVING IT FROM MENUES
             */
            undeploy(name, false);
        }

        /*
         * create new webappdir
         */
        webappdir.mkdirs();

        /*
         * Unzip byte array to previous created webappdir/name
         */
        try {
            ZIPTool.unzipbytearray(input, webappdir);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new ManagerException(e);
        }

        //dirty Hack for SSWAP JK
        //AppClassLoader acl2 = null; JK

        /*
         * Finally add an AppClassLoader for this app ...
         */
        LOG.debug("Adding a new AppClassLoader for app '" + name + "'");
        try {
            AppClassLoader acl = new AppClassLoaderImpl(wcl, webappdir);
            // acl2 = acl; JK
            rcl.addAppClassLoader(name, acl);
        } catch (Exception e) {
            LOG.error("Could not add a new AppClassLoader for app '" + name + "'. Message was: '" + e.getLocalizedMessage() + "'.");
            throw new ManagerException(e);
        }

        /*
         * create new toolcontext for this app
         */
        LOG.debug("Trying to create new toolcontext for app '" + name + "'");
        GenericWebApplicationContext nct;
        try {
            nct = createToolContext(webappdir, rcl);
        } catch (Exception e) {
            LOG.error("Could not create new toolcontext for app '" + name + "'. Message was: '" + e.getLocalizedMessage() + "'.", e);
            throw new ManagerException(e);
        }

        /*
         * add the tool to the main context
         */
        LOG.debug("Trying to add tool '" + name + "' to main context.");
        try {
            ctx.addRunnableItem(name, nct, document, webappdir);
        } catch (Exception e) {
            LOG.error("Could not add tool '" + name + "' to main context. Message was: '" + e.getLocalizedMessage() + "'.", e);
            throw new ManagerException(e);
        }

        /*
         * Store the tools data in database ...
         */
        LOG.debug("Trying to add tool '" + name + "' into database.");
        try {
            //write tool description to database
            insertItem(name, runnableitem, input, StringUtil.getHex(ZIPTool.md5sum(input)));
            //and refresh the DB access layer's caches and menu entries
            xmlrequest.refresh();
            ((ToolMenuCreator) ctx.getBean("toolMenuCreator")).init();
        } catch (Exception e) {
            LOG.error("Error on inserting tool into DB: " + e.getLocalizedMessage(), e);
            throw new ManagerException(e);
        }


        /*
         * Add tool's references, authors to Ontology system
         */
        TrunnableItem ri;
        try {
            Unmarshaller um = JAXBContext.newInstance("de.unibi.techfak.bibiserv.cms").createUnmarshaller();
            JAXBElement<TrunnableItem> jaxbe = (JAXBElement<TrunnableItem>) um.unmarshal(new ByteArrayInputStream(runnableitem));
            ri = jaxbe.getValue();
            addReferenceToOntology(name, ri);
            addAuthorsToOntology(name, ri);
        } catch (JAXBException e) {
            LOG.fatal("While adding tool's reference to the ontology, the given dom document couldn't be parsed as tool description XML. Error was:\n" + e.getLocalizedMessage(), e);

        }

        /*
         * Get all REST annotated classes
         */
        try {
            Set<Class> tmp = RESTAnnotationParser.parse(rcl, new File(webappdir, "classes"));
            if (!tmp.isEmpty()) {
                addRESTClazzes(name, tmp);
                reloadRestServletContainerConfiguration();
            }
        } catch (Exception e) {
            LOG.fatal("While parsing " + new File(webappdir, "classes").toString() + " for REST(Path) annotated classes an exception occurred.", e);
        }

        /*
         * update log4j configuration
         */
        try {
            configureLog4j(new FileReader(new File(webappdir.toString() + "/config/log4j-tool.properties")), name, "../logs/"); //@ToDo : Choose better log dir
        } catch (IOException e) {
            LOG.error("Log4J tool configuration failed." + e.getMessage(), e);
            // throw no ManagerException because this is a non fatal error, the tool
            // can log using the root logger ...
        }
    }

    /**
     * Undeploy an application
     *
     * @param name - Name(ID) of app
     * @param removeFromMenu - switch to indicate REAL undeployment and deletion
     * @throws ManagerException
     */
    public void undeploy(final String name, final boolean removeFromMenu) throws ManagerException {
        LOG.info("call undeploy '" + name + "'");
        checkRootDir();
        final File webappdir = new File(rootdir, name);
        if (!webappdir.exists()) {
            throw new ManagerException("App '" + name + "' is not installed!");
        }

        // remove publication entries from Ontology
        PnPOntoQuestioner.removeBibsFromRunnableItem(name);
        // remove author entries from Ontology
        PnPOntoQuestioner.removeAuthors(name);

        // remove REST classes
        if (removeRESTClazzes(name) != null) {
            // and reload Configuration if necessary
            reloadRestServletContainerConfiguration();
        }

        // remove app files and directory
        cleanDir(webappdir);

        // remove appclassloader
        rcl.removeAppClassLoader(name);
        // remove tool from main context
        ctx.removeRunnableItem(name);

        // remove db entry
        try {
            deleteItem(name);
            //remove references to this tool from the menu
            if (removeFromMenu) {
                removeItemFromMenu(name);
            }
            //and refresh the DB access layer's caches and menu entries
            xmlrequest.refresh();
            ((ToolMenuCreator) ctx.getBean("toolMenuCreator")).init();

        } catch (SQLException e) {
            LOG.error("Problem on deleting item from DB: " + e.getLocalizedMessage());
            throw new ManagerException(e);
        }
    }

    private GenericWebApplicationContext createToolContext(File webappdir, ClassLoader cl) {
        GenericWebApplicationContext toolcontext = new GenericWebApplicationContext();
        toolcontext.setClassLoader(cl);

        // Create a new XmlBeanDefinitionReader for the given BeanFactory.
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(toolcontext);

        // Configure the bean definition reader with its context's
        // resource loading environment.
        beanDefinitionReader.setResourceLoader(toolcontext);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(toolcontext));
        beanDefinitionReader.setBeanClassLoader(cl);
        // proceed with actually loading the bean definitions.
        //get list of all files called *Config.xml and load definitions from them!
        try {
            String configlocation;
            final File configdir = new File(webappdir + "/config");
            if (configdir.isDirectory()) { //sanity check 1
                final File[] configfiles = configdir.listFiles(new BarConfigFilter());
                for (File file : configfiles) {
                    if (file.isFile()) { //sanity check 2
                        configlocation = "file://" + configdir + File.separator + file.getName();
                        LOG.debug("Trying to load bar bean config from '" + configlocation + "'");
                        beanDefinitionReader.loadBeanDefinitions(configlocation);
                        LOG.debug("---now we have " + beanDefinitionReader.getBeanFactory().getBeanDefinitionCount() + " beandefs!");
                    }
                }

                //and some diagnostic information if we are debugging:
                if (LOG.getLevel() == Level.DEBUG) {
                    LOG.debug("-----BeanDefNames are:");
                    for (String defname : toolcontext.getBeanDefinitionNames()) {
                        LOG.debug(defname);
                    }
                    LOG.debug("-----BeanDefNames end");
                }
            } else {
                LOG.fatal("no configuration files found in '" + configdir.getCanonicalPath() + "'!");
            }
        } catch (IOException ex) {
            LOG.fatal(ex.getLocalizedMessage());
        }
        toolcontext.refresh();
        return toolcontext;
    }

    /**
     * #######################################################################
     * getter/setter functions
     * #######################################################################
     */
    /**
     * Set the application context
     *
     * @param applicationcontext - applicationcontext to be set.
     * @throws Exception
     */
    public void setApplicationContext(ApplicationContext applicationcontext) throws Exception {
        if (ctx == null) {
            LOG.debug("Setting Applicationcontext for Core");
            ctx = (BiBiWebApplicationContext) applicationcontext;
            wcl = ctx.getClassLoader().getParent();
            rcl = (RootClassLoader) ctx.getClassLoader();
            rootdir = new File(ctx.getServletContext().getRealPath("applications"));
            rootdir.mkdirs();
            moduleRootDir = new File(ctx.getServletContext().getRealPath(MODULE_ROOT_DIR));
            moduleRootDir.mkdirs();

            //and some diagnostic information if we are debugging:
            if (LOG.isDebugEnabled()) {
                LOG.debug("-----BeanDefNames in BiBiWebApplicationContext are:");
                for (String defname : ctx.getBeanDefinitionNames()) {
                    LOG.debug(defname);
                }
                LOG.debug("-----BeanDefNames end");
            }
            xmlrequest = ((BiBiServXMLReader) ctx.getBean("bibiservXmlReader"));
        } else {
            LOG.debug("ApplicationContext for Core already set - just using it now.");
        }
    }

    /**
     * Returns current a reference to current ApplicationContext.
     *
     * @return Returns current a reference to current ApplicationContext.
     */
    public ApplicationContext getApplicationContext() {
        return ctx;
    }

    /**
     * Initialize previous deployed apps from internal database. Add some info
     * logging to determine database problems on BiBiServ2.
     *
     * @throws SQLException
     * @throws IOException
     * @throws ManagerException
     */
    public void initalizeFromDB() throws SQLException, IOException, ManagerException {

        LOG.info("Initialize apps from database!");

        checkRootDir();

        Connection conn = null;

        try {

            conn = datasource.getConnection();
            Statement stmt = conn.createStatement();

            // get ALL items (= item, linkeditem and runnableitem) from database
            ResultSet rs = stmt.executeQuery("SELECT ID,ITEM,BAR,TYPE from ITEM");

            while (rs.next()) {
                // get item id
                String id = rs.getString("ID");
                // get item type
                String type = rs.getString("TYPE");

                // log info 
                LOG.info("found \"" + id + "\" (" + type + ")");

                // get xml description
                Document document = null;
                Reader runnableItemStream = rs.getClob("ITEM").getCharacterStream();

                if (runnableItemStream != null) {
                    try {
                        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                        domFactory.setNamespaceAware(true);
                        DocumentBuilder builder = domFactory.newDocumentBuilder();
                        document = builder.parse(new InputSource(runnableItemStream));
                    } catch (Exception ex) {
                        LOG.fatal("Exception occurred while create DOM docuemnt from db stream!", ex);
                    }
                } else {
                    LOG.fatal("Could not load Item from DB: runnableItemStream WAS NULL!");
                }

                // 
                if (type.equalsIgnoreCase("runnable")) {
                    File appdir = new File(rootdir, id);

                    //Remove already existing direcories of previous unpackaging (necessary for a successful redeploy of mainapp)
                    if (appdir.exists()) {
                        cleanDir(appdir);
                    }

                    Blob blob = rs.getBlob("BAR");
                    GenericWebApplicationContext tc;

                    /*
                     * create subdir
                     */
                    appdir.mkdir();
                    /*
                     * unzip blob, casting the length to int without check should be
                     * save, because this check happend during transmitting the bar
                     * archive to the manager application using the deploy (ws)
                     * function
                     */
                    ZIPTool.unzipbytearray(blob.getBytes(1, (int) blob.length()), appdir);
                    /*
                     * Add an AppClassLoader for this app ...
                     */
                    AppClassLoader acl = new AppClassLoaderImpl(wcl, appdir);

                    rcl.addAppClassLoader(id, acl);
                    tc = createToolContext(appdir, rcl);

                    /*
                     * Get all REST annotated classes
                     */
                    try {
                        Set<Class> tmp = RESTAnnotationParser.parse(rcl, new File(appdir, "classes"));
                        if (!tmp.isEmpty()) {
                            addRESTClazzes(id, tmp);
                            reloadRestServletContainerConfiguration();
                        }
                    } catch (Exception e) {
                        LOG.fatal("While parsing " + new File(appdir, "classes").toString() + " for REST(Path) annotated classes an exception occurred.", e);
                    }

                    /*
                     * add the tool to the main context
                     */
                    ctx.addRunnableItem(id, tc, document, appdir);

                    /*
                     * Add tool's references to Ontology system
                     */
                    TrunnableItem ri = null;
                    try {
                        Unmarshaller um = JAXBContext.newInstance("de.unibi.techfak.bibiserv.cms").createUnmarshaller();
                        JAXBElement<TrunnableItem> jaxbe = (JAXBElement<TrunnableItem>) um.unmarshal(new DOMSource(document.getDocumentElement()));
                        ri = jaxbe.getValue();
                        addReferenceToOntology(id, ri);
                        addAuthorsToOntology(id, ri);
                    } catch (Exception e) {
                        LOG.fatal("While adding tool's reference to the ontology, the given dom document couldn't be parsed as tool description XML. Error was:\n" + e.getLocalizedMessage(), e);
                    }

                    /*
                     * update log4j configuration
                     */
                    try {
                        configureLog4j(new FileReader(new File(appdir.toString() + "/config/log4j-tool.properties")), id, "../logs/"); //@ToDo : Choose better log dir
                    } catch (IOException e) {
                        LOG.error("Log4J tool configuration failed for '" + id + "'. Message : " + e.getMessage());
                    // throw no ManagerException because this is a non fatal
                        // error, the tool can log using the root logger ...
                    }

                    // Otherwise we have a default or linked item 
                } else {
                    ctx.addItem(id, document);

                }

            }
            stmt.close();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        LOG.info("... finished!");
    }

    /**
     * Returns an instance (singleton) of Core class
     *
     * @return Returns an instance of Core.
     */
    public static Core getInstance() {
        try {
            if (singleton == null) {
                singleton = new Core();
            }
            return singleton;
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    /**
     * Add an RestServletContainer to current ServletContainer set
     *
     * @param sc - ServletContainer to be added
     */
    public void addRestServletContainer(ServletContainer sc) {
        synchronized (sc_set) {
            sc_set.add(sc);
        }
    }

    /**
     * Remove a RestServletContainer from currrent ServletContainer set
     *
     * @param sc - ServletContainer to be removed
     */
    public void removeRestServletContainer(ServletContainer sc) {
        synchronized (sc_set) {
            sc_set.remove(sc);
        }
    }

    /**
     * Reload ServletContainerConfiguration
     *
     */
    public void reloadRestServletContainerConfiguration() {
        for (ServletContainer sc : sc_set) {
            sc.reload();
        }
    }

    /**
     * Add classes to the REST class set
     *
     */
    public void addRESTClazzes(String id, Set<Class> c_set) {
        synchronized (restclazz_map) {
            restclazz_map.put(id, c_set);
        }
    }

    /**
     * void remove all RESTclazzes belonging to a tool
     *
     */
    public Set<Class> removeRESTClazzes(String id) {
        synchronized (restclazz_map) {
            return restclazz_map.remove(id);
        }
    }

    /**
     * Return all RESTclasses as Set.
     *
     * @return
     */
    public Set<Class<?>> getRESTClazzes() {
        Set<Class<?>> tmp_set = new HashSet();
        for (String k : restclazz_map.keySet()) {
            //tmp_set.addAll(restclazz_map.get(k)); --> doesn't work 
            for (Class c : restclazz_map.get(k)) {
                tmp_set.add(c);
            }

        }
        return tmp_set;
    }

    /**
     * #######################################################################
     * private functions
     * #######################################################################
     */
    /**
     * Check if RootDir is null, throws an exception in that case
     *
     * @throws ManagerException if rootdir is null
     */
    private void checkRootDir() throws ManagerException {
        if (rootdir == null) {
            throw new ManagerException("RootDir contains a  'null' value. This occurs in the case the servletcontext"
                    + "can't determine the real webapp path ... simple solution use another appserver :-)");
        }
    }

    /**
     * Check if ModuleRootDir is null, throws an exception in that case
     *
     * @throws ManagerException if ModuleRootDir is null
     */
    private void checkModuleRootDir() throws ManagerException {
        if (moduleRootDir == null) {
            throw new ManagerException("the servletcontext can't determine the real webapp path");
        }
    }

    private void cleanDir(final File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            final File[] file_list = fileOrDirectory.listFiles();
            if (file_list != null) {
                for (File file : file_list) {
                    cleanDir(file);
                }
            }
            LOG.debug("CLEAN DIR ::" + fileOrDirectory);
        } else {
            LOG.debug("clean file ::" + fileOrDirectory);
        }
        fileOrDirectory.delete();
    }

    /**
     * private helper function
     *
     * @param itemId
     * @param ITEM
     * @param BAR
     * @param MD5
     * @throws SQLException
     */
    private void insertItem(String itemId, byte[] ITEM, byte[] BAR, String MD5) throws SQLException {
        Connection conn = null;
        try {
            conn = datasource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO ITEM (ID,ITEM,TIME,BAR,MD5) VALUES (? , ?, CURRENT_TIMESTAMP, ?, ?)");
            stmt.setString(1, itemId);
            stmt.setClob(2, new InputStreamReader(new ByteArrayInputStream(ITEM),Charset.forName("UTF-8")));
            stmt.setBlob(3, new ByteArrayInputStream(BAR));
            stmt.setString(4, MD5);
            stmt.execute();
            stmt.close();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * private helper function
     *
     * @param itemId
     * @throws SQLException
     */
    private void deleteItem(String itemId) throws SQLException {
        Connection conn = null;
        try {
            conn = datasource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM ITEM WHERE ID = ?");
            pstmt.setString(1, itemId);
            pstmt.execute();
            pstmt.close();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * private helper function; read tool specific log4j properties from reader,
     * replace file location with logdir and update log4j configuration.
     *
     * @param reader
     * @param itemId
     * @param logdir
     * @throws IOException
     */
    private void configureLog4j(Reader reader, String itemId, String logdir) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);

        // get File___ property
        String value = properties.getProperty("log4j.appender." + itemId + "_file.File");
        if (value == null) {
            LOG.error("Property 'log4j.appender." + itemId + "_file.File' not set in log4j-tool.properties for tool '" + itemId + "'.");
            return;
        }
        //replace [LOGDIR] within properties with logdir
        properties.setProperty("log4j.appender." + itemId + "_file.File", value.replaceAll("<LOGDIR>", logdir));

        //update log4j configuration
        PropertyConfigurator.configure(properties);
    }

    /**
     * Removes an item's references from the server's structure XML in the
     * database
     *
     * @param name - name of the tool to be de-referenced
     */
    //@TODO: ERROR CHECKING.
    //       IDENTIFY PROBLEMS!
    //       MOVE TO ADMINBEAN!!
    //       - SH, 20100813
    private void removeItemFromMenu(final String name) {

        //load and parse the current structure fomr the DB
        JAXBElement jaxbObject = null;
        try {
            JAXBContext jc = JAXBContext.newInstance("de.unibi.techfak.bibiserv.cms");
            Unmarshaller um = jc.createUnmarshaller();
            jaxbObject = (JAXBElement) um.unmarshal(new StreamSource(new StringReader(xmlrequest.getCompleteXMLStructure())));
        } catch (Exception ex) {
            LOG.error(ex);
        }
        //get the JAXB object
        Tcategory rootCategory = (Tcategory) jaxbObject.getValue();

        //find and delete all itemRef entries for the item with the given name
        removeit(rootCategory, name);

        // Save changed xml structure in the JAXB objects into database.
        try {
            //marshall to String
            JAXBContext jc = JAXBContext.newInstance("de.unibi.techfak.bibiserv.cms");
            Marshaller m = jc.createMarshaller();
            StringWriter sw = new StringWriter();
            Result output = new StreamResult(sw);
            ObjectFactory obj = new ObjectFactory();
            m.marshal(obj.createCategory(rootCategory), output);
            //insert into db
            xmlrequest.insertNewXMLStructure(sw.toString());
        } catch (Exception ex) {
            LOG.error(ex);

        }
    }

    private void removeit(final Tcategory cat, final String name) {
        //recursively iterate over all child categories
        if (cat.isSetCategoryOrItemRef()) {
            List <Object> removeList = new ArrayList();
        
            for (Object categoryOrItemRef : cat.getCategoryOrItemRef()) {
                if (categoryOrItemRef instanceof Tcategory) {
                    removeit((Tcategory)categoryOrItemRef,name);
                }
                
                /* ToDo: Check if this works ... I'm not sure if I check for correct Type */
                if (categoryOrItemRef instanceof Titem) {
                    Titem tmp  = (Titem)categoryOrItemRef;
                    if (tmp.getId().equals(name)) {
                        removeList.add(tmp);
                    }
                }
            }
            cat.getCategoryOrItemRef().removeAll(removeList);
            
        }
        
        /* if ((cat.getCategory().isEmpty() || cat.getItemRef().size() > 0) && (cat.getItemRef().contains(name))) {
            cat.getItemRef().remove(name);
        } */
    }

    /**
     * @see
     * de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager.IModuleManager#deployModule()
     * @param id
     * @throws ManagerException
     * @throws SQLException
     */
    public void deployModule(String id) throws ManagerException, SQLException {
        LOG.info("call deploy for mod '" + id + "'");
        byte[] archive = null;
        PreparedStatement pst = null;
        Connection conn = null;
        try {
            conn = datasource.getConnection();
            LOG.debug("Connection to Database in ModuleManager");
            // if id is null deploy everything
            if (id == null) {
                pst = conn.prepareStatement("SELECT FILE FROM MODULES");
            } else {
                pst = conn.prepareStatement("SELECT FILE FROM MODULES WHERE ID='" + id + "'");
            }
            ResultSet rset = pst.executeQuery();
            if (rset.next()) {
                Blob blob = rset.getBlob(1);
                archive = blob.getBytes(1L, (int) blob.length());
            }
            rset.close();
            pst.close();
        } catch (SQLException ex) {
            LOG.fatal("The following exception occured while "
                    + "providing Info-Document of Module '" + id + "':" + ex);
            throw new SQLException(ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        if (archive != null) {
            checkModuleRootDir();
            File modDir = new File(moduleRootDir, id);
            if (modDir.exists()) {
                undeployModule(id);
            }
            modDir.mkdirs();
            try {
                ZIPTool.unzipbytearray(archive, modDir);
            } catch (IOException e) {
                LOG.error(e.getMessage());
                throw new ManagerException(e);
            }

            AdminModClassLoader amcl = null;
            LOG.debug("Adding a new AdminModClassLoader for app '" + id + "'");
            try {
                amcl = new AdminModClassLoaderImpl(wcl, modDir);
                AdminRootClassLoader arcl = (AdminRootClassLoader) rcl.getAppClassLoader(BiBiWebApplicationContext.ADMIN_RCL_NAME);
                arcl.addAdminModClassLoader(id, amcl);
            } catch (Exception e) {
                LOG.error("Could not add a new AdminModClassLoader for id '" + id + "'. Message was: '" + e.getLocalizedMessage() + "'.");
                throw new ManagerException(e);
            }

            /*
             * create new module context
             */
            LOG.debug("Trying to create new module context '" + id + "'");
            GenericWebApplicationContext modCtx;
            try {
                modCtx = createModuleContext(modDir, rcl);
            } catch (Exception e) {
                LOG.error("Could not create new module context '" + id + "'. Message was: '" + e.getLocalizedMessage() + "'.", e);
                throw new ManagerException(e);
            }

            /*
             * add the module to the main context
             */
            LOG.debug("Trying to add module '" + id + "' to main context.");
            try {
                ctx.addModuleContext(id, modCtx);
            } catch (Exception e) {
                LOG.error("Could not add module '" + id + "' to main context. Message was: '" + e.getLocalizedMessage() + "'.", e);
                throw new ManagerException(e);
            }

        } else {
            LOG.error("Could not load module archive from db with id '" + id + "'");
        }

    }

    /**
     * @see
     * de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager.IModuleManager#undeployModule()
     * @param id
     * @throws ManagerException
     */
    public void undeployModule(String id) throws ManagerException {
        LOG.info("call undeploy for mod '" + id + "'");
        checkModuleRootDir();
        File modDir = new File(moduleRootDir, id);
        if (modDir.exists()) {
            cleanDir(modDir);
        }

        AdminRootClassLoader arcl = (AdminRootClassLoader) rcl.getAppClassLoader(BiBiWebApplicationContext.ADMIN_RCL_NAME);
        arcl.removeAdminModClassLoader(id);
        ctx.removeModuleContext(id);
    }

    /**
     * Deploys all modules that are existent in the database. Add some for info
     * logging to determine runtime problems with database.
     */
    public void initializeModulesFromDb() {
        LOG.info("Initialize modules from database!");
        PreparedStatement pst = null;
        Connection conn = null;
        List<String> list_of_ids = new ArrayList<>();
        try {
            conn = datasource.getConnection();
            pst = conn.prepareStatement("SELECT ID FROM MODULES");
            ResultSet rset = pst.executeQuery();
            while (rset.next()) {
                /*
                 * iterates over moduleids from database and deploys each module
                 * separately.
                 */
                list_of_ids.add(rset.getString(1));
                
            }
            rset.close();
            pst.close();  
        } catch (SQLException  e) {
            LOG.error("Error while getting modules id from database");
        } 
        //
        for (String id : list_of_ids) {
            try {
                deployModule(id);
            } catch(SQLException | ManagerException  e) {
                LOG.error("Error while deploy model '"+id+"'");
            }
        }
        
        LOG.info("... finished!");
    }

    private GenericWebApplicationContext createModuleContext(File modDir, ClassLoader cl) {
        GenericWebApplicationContext modCtx = new GenericWebApplicationContext();
        modCtx.setClassLoader(cl);

        // Create a new XmlBeanDefinitionReader for the given BeanFactory.
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(modCtx);

        // Configure the bean definition reader with its context's
        // resource loading environment.
        beanDefinitionReader.setResourceLoader(modCtx);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(modCtx));
        beanDefinitionReader.setBeanClassLoader(cl);

        String configlocation;
        final File beanDefsFile = new File(modDir + "" + File.separator + "web" + File.separator + "WEB-INF" + File.separator + "beans.xml");
        if (beanDefsFile.isFile()) {
            String beanDefsFilePath;
            try {
                beanDefsFilePath = beanDefsFile.toURI().toURL().toString();
            } catch (MalformedURLException me) {
                LOG.error("Error while locating beanDefsFile for module path '" + beanDefsFile + "': " + me);
                return null;
            }
            LOG.debug("Trying to load module bean config from '" + beanDefsFile + "'");
            beanDefinitionReader.loadBeanDefinitions(beanDefsFilePath);
            LOG.debug("---now we have " + beanDefinitionReader.getBeanFactory().getBeanDefinitionCount() + " mod beandefs!");
        }
        //and some diagnostic information if we are debugging:
        if (LOG.getLevel() == Level.DEBUG) {
            LOG.debug("-----ModuleBeanDefNames are:");
            for (String defname : modCtx.getBeanDefinitionNames()) {
                LOG.debug(defname);
            }
            LOG.debug("-----ModuleBeanDefNames end");
        }
        modCtx.refresh();
        return modCtx;
    }

    /**
     * private helper method
     *
     * @param name - tool name/id
     * @param ri - Runnableitem containing tool description
     */
    private void addReferenceToOntology(String id, TrunnableItem ri) {
        if (ri.isSetReferences() && ri.getReferences().isSetReference() && !ri.getReferences().getReference().isEmpty()) { // ... see below
            try {
                LOG.info("add References for runnable item [" + id + "] ... ");
                PnPOntoQuestioner.addBibsFromRunnableItem(id, ri.getReferences().getReference());
                LOG.info("... successful!");
            } catch (Exception e) {
                LOG.error("Some went wrong when adding references for RunnableItem [" + id + "]", e);
            }
        } else {
            LOG.warn("No reference defined within RunnableItem [" + id + "] ... "); // should never occurre, since schema defines a reference requirement
        }
    }

    /**
     * private helper that convert Tperson to BiBiPerson and add it to the
     * PnPOntoQuestioner
     *
     * @param id
     * @param ri
     */
    private void addAuthorsToOntology(String id, TrunnableItem ri) {
        // add responsible author
        if (ri.isSetResponsibleAuthor()) {
            Tperson tp = ri.getResponsibleAuthor();

            BiBiPerson bp = new BiBiPersonImplementation();
            if (tp.isSetEmail()) {
                bp.setEmail(tp.getEmail());
            }
            bp.setGivenname(tp.getFirstname());
            bp.setFamily_name(tp.getLastname());
            PnPOntoQuestioner.addAuthor(id, bp);
        }
        // add other (optional) authors)
        if (ri.isSetAuthor() && !ri.getAuthor().isEmpty()) {
            for (Tperson tp : ri.getAuthor()) {
                BiBiPerson bp = new BiBiPersonImplementation();
                if (tp.isSetEmail()) {
                    bp.setEmail(tp.getEmail());
                }
                bp.setGivenname(tp.getFirstname());
                bp.setFamily_name(tp.getLastname());
                PnPOntoQuestioner.addAuthor(id, bp);
            }
        }
    }
}
