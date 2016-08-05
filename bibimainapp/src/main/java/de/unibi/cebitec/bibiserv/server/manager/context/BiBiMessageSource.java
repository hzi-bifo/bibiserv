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
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.xml.NamespaceContext;
import java.io.*;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class provides a MessageSource for the BiBiServ System which is aware of
 * basic message property files, reads the messages for the server structure
 * from the database and can be updated at runtime with the definitions for
 * messages from bar-file packaged tools. It loads resources on demand and
 * caches messages as far as possible to speed up answer time.
 *
 * When asked to resolve a message, it will check it's message in the following
 * order: first the Property files definitions then the structural messages for
 * the current Locale then the structural messages for the english Locale then
 * the structural messages defined without any language information (de- fined
 * as being the default language-less messages)
 *
 * If none of these static sources of properties yielded any result, this class
 * will then check the Tool-Dependent message propoerties, again beginning with
 * the current Locale, then english, then no-language.
 *
 * Note: This class is a partial re-implementation of
 * org.springframework.context.support.ReloadableResourceBundleMessageSource,
 * minus the auto-refreshing and with awareness of BiBiServ Tool properties.
 * Therefore, most of the code is just copied and stripped of unnecessary parts
 * from the source of that class, and then tool-awareness code bolted onto the
 * corpse :-). This is the reason for the Apache license of this file.
 *
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 * Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de (fix some bug, code
 * optimization)
 * Benjamin Paassen - bpaassen(at)cebitec.uni-bielefeld.de (added struct xml to
 * search index)
 */
public class BiBiMessageSource extends AbstractMessageSource implements ResourceLoaderAware {

    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final String XML_SUFFIX = ".xml";
    private String[] basenames = new String[0];
    /**
     * Cache to hold filename lists per Locale
     */
    private final Map cachedFilenames = new HashMap();
    /**
     * Cache to hold already loaded properties per filename
     */
    private final Map cachedProperties = new HashMap();
    private Document structXML = null;
    /**
     * Cache to hold already loaded properties from structure table in DB per
     * locale
     */
    private final Map<Locale, PropertiesHolder> cachedStructureProperties = new HashMap<>();
    /**
     * a special propertiesholder object for default structure properties
     */
    private PropertiesHolder defaultStructureProperties = null;
    /**
     * Cache to hold already loaded properties from bar files DB per locale and
     * tool
     */
    private final Map<Locale, Map<String, PropertiesHolder>> cachedToolProperties = new HashMap();
    private final Map<String, PropertiesHolder> defaultToolProperties = new HashMap();
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
    private String defaultEncoding;
    private Properties fileEncodings;
    //Connection to our datasource
    Connection con = null;
    //logger for this class
    private static Logger log = Logger.getLogger(BiBiMessageSource.class);
    //source of the XSLT script to remove namespaces from mini- and micro-html descriptions
    private static final Source xsltSource = new StreamSource(BiBiMessageSource.class.getResourceAsStream("/removeNamespace.xsl"));
    //Transformer 
    Transformer trans = null;
    //XPath
    XPath xpath = null;

    /**
     * Default Constructor, set up Transformer and xpath object;
     *
     */
    public BiBiMessageSource() {
        // setup tranformer (if not allready done) --  this can maybe done within the Constructor ...

        try {
            XPathFactory factory = XPathFactory.newInstance();
            xpath = factory.newXPath();
            NamespaceContext nsc = new de.unibi.techfak.bibiserv.xml.NamespaceContext();

            /**
             * Set Namespaces
             */
            nsc.addNamespace("http://www.w3.org/XML/1998/namespace", "xml");
            nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms.microhtml", "microhtml");
            nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms.minihtml", "minihtml");
            nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms", "cms");

            xpath.setNamespaceContext(nsc);

            //setup transformer
            TransformerFactory transFact = TransformerFactory.newInstance();
            trans = transFact.newTransformer(xsltSource);
            //dont attach XML-DECLARATION, the transformed code will be put into an already existing page
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "no");
        } catch (TransformerConfigurationException ex) {
            log.fatal(ex.getMessage(), ex);
        }
    }

    
    /**
     * Force a reload/reinitialization of the structure properties ...  
     * 
     * jkrueger, 10/8/2015
     */
    public void refresh(){
        structXML = null;
        defaultStructureProperties = null;
        cachedStructureProperties.clear();
    }
    
    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        //check messages from property file(s),
        //using the provided or cached filenames as keys into
        //the property cache
        for (int i = 0; i < this.basenames.length; i++) {
            List filenames = calculateAllFilenames(this.basenames[i], locale);
            for (int j = 0; j < filenames.size(); j++) {
                String filename = (String) filenames.get(j);
                PropertiesHolder propHolder = getProperties(filename);
                MessageFormat result = propHolder.getMessageFormat(code, locale);
                if (result != null) {
                    return result;
                }
            }
        }

        // check messages from structure with locale based keys
        PropertiesHolder propHolder = getStructureProperties(locale);
        MessageFormat result = propHolder.getMessageFormat(code, locale);
        if (result != null) {
            return result;
        }

        //if we've found nothing, we try the english locale
        Locale l = new Locale("en");
        propHolder = getStructureProperties(l);
        result = propHolder.getMessageFormat(code, l);
        if (result != null) {
            return result;
        }

        //if we've still found nothing, we try the default structure propholder
        propHolder = getDefaultStructureProperties();
        result = propHolder.getMessageFormat(code, locale);
        if (result != null) {
            return result;
        }

        //check messages from bar-files, first current locale
        Map m = cachedToolProperties.get(locale);
        if (m != null) {
            log.debug("trying bar prop '" + code + "' in locale '" + locale + "'");
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                propHolder = (PropertiesHolder) ((Map.Entry) it.next()).getValue();
                result = propHolder.getMessageFormat(code, locale);
                if (result != null) {
                    return result;
                }
            }
        }
//        //then english
//        m = cachedToolProperties.get(new Locale("en"));
//        if (m != null) {
//            System.err.println("trying bar prop '" + code + "' in locale 'en'");
//            Iterator it = m.entrySet().iterator();
//            while (it.hasNext()) {
//                propHolder = (PropertiesHolder) ((Map.Entry) it.next()).getValue();
//                result = propHolder.getMessageFormat(code, locale);
//                if (result != null) {
//                    return result;
//                }
//            }
//        }
        //check messages from default (language-less) bar properties
        Iterator it = defaultToolProperties.entrySet().iterator();
        while (it.hasNext()) {
            propHolder = (PropertiesHolder) ((Map.Entry) it.next()).getValue();
            result = propHolder.getMessageFormat(code, locale);
            if (result != null) {
                return result;
            }
        }
        //if we're still here, we did not find anything. Return null!
        return null;
    }

    /**
     * Resolves the given message code as key in the retrieved bundle files or
     * our internal cache structures for BiBiServ, returning the value found in
     * the bundle as-is (without MessageFormat parsing).
     */
    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        //check messages from property file(s)
        for (int i = 0; i < this.basenames.length; i++) {
            List filenames = calculateAllFilenames(this.basenames[i], locale);

            for (int j = 0; j < filenames.size(); j++) {
                String filename = (String) filenames.get(j);
                PropertiesHolder propHolder = getProperties(filename);
                String result = propHolder.getProperty(code);
                if (result != null) {
                    return result;
                }
            }
        }
        // check messages from structure with locale based keys
        PropertiesHolder propHolder = getStructureProperties(locale);
        String result = propHolder.getProperty(code);
        if (result != null) {
            return result;
        }
        //if we've found nothing, we try the english locale
        propHolder = getStructureProperties(new Locale("en"));
        result = propHolder.getProperty(code);
        if (result != null) {
            return result;
        }

        //if we've still found nothing, we try the default structure propholder
        propHolder = getDefaultStructureProperties();
        result = propHolder.getProperty(code);
        if (result != null) {
            return result;
        }

        //check messages from bar-files, first current locale
        Map m = cachedToolProperties.get(locale);
        if (m != null) {
            log.debug("trying bar prop '" + code + "' in locale '" + locale + "'");
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                propHolder = (PropertiesHolder) ((Map.Entry) it.next()).getValue();
                result = propHolder.getProperty(code);
                if (result != null) {
                    log.debug("Hit!");
                    return result;
                }
            }
        }

        //then english locale
        m = cachedToolProperties.get(new Locale("en"));
        if (m != null) {
            log.debug("trying bar prop '" + code + "' in fallback locale 'en'");
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                propHolder = (PropertiesHolder) ((Map.Entry) it.next()).getValue();
                result = propHolder.getProperty(code);
                if (result != null) {
                    log.debug("Hit!");
                    return result;
                }
            }
        }

        //check messages from default bar properties
        Iterator it = defaultToolProperties.entrySet().iterator();
        log.debug("trying bar prop '" + code + "' in default props");
        while (it.hasNext()) {
            propHolder = (PropertiesHolder) ((Map.Entry) it.next()).getValue();
            result = propHolder.getProperty(code);
            if (result != null) {
                log.debug("Hit!");
                return result;
            }
        }
        //if we're still here, we did not find anything. Return null!
        return null;
    }

    /**
     * Set a single basename, following the basic ResourceBundle convention of
     * not specifying file extension or language codes, but in contrast to
     * {@link ResourceBundleMessageSource} referring to a Spring resource
     * location: e.g. "WEB-INF/messages" for "WEB-INF/messages.properties",
     * "WEB-INF/messages_en.properties", etc. <p>As of Spring 1.2.2, XML
     * properties files are also supported: e.g. "WEB-INF/messages" will find
     * and load "WEB-INF/messages.xml", "WEB-INF/messages_en.xml", etc as well.
     * Note that this will only work on JDK 1.5+.
     *
     * @param basename the single basename
     * @see #setBasenames
     * @see org.springframework.core.io.ResourceEditor
     * @see java.util.ResourceBundle
     */
    public void setBasename(String basename) {
        setBasenames(new String[]{basename});
    }

    /**
     * Set an array of basenames, each following the basic ResourceBundle
     * convention of not specifying file extension or language codes, but in
     * contrast to
     * {@link ResourceBundleMessageSource} referring to a Spring resource
     * location: e.g. "WEB-INF/messages" for "WEB-INF/messages.properties",
     * "WEB-INF/messages_en.properties", etc. <p>XML properties files are also
     * supported: e.g. "WEB-INF/messages" will find and load
     * "WEB-INF/messages.xml", "WEB-INF/messages_en.xml", etc as well. Note that
     * this will only work on JDK 1.5+. <p>The associated resource bundles will
     * be checked sequentially when resolving a message code. Note that message
     * definitions in a <i>previous</i> resource bundle will override ones in a
     * later bundle, due to the sequential lookup.
     *
     * @param basenames an array of basenames
     * @see #setBasename
     * @see java.util.ResourceBundle
     */
    public void setBasenames(String[] basenames) {
        if (basenames != null) {
            this.basenames = new String[basenames.length];
            for (int i = 0; i < basenames.length; i++) {
                final String basename = basenames[i];
                Assert.hasText(basename, "Basename must not be empty");
                this.basenames[i] = basename.trim();
            }
        } else {
            this.basenames = new String[0];
        }
    }

    /**
     * Set the default charset to use for parsing properties files. Used if no
     * file-specific charset is specified for a file. <p>Default is none, using
     * the
     * <code>java.util.Properties</code> default encoding. <p>Only applies to
     * classic properties files, not to XML files.
     *
     * @param defaultEncoding the default charset
     * @see #setFileEncodings
     * @see org.springframework.util.PropertiesPersister#load
     */
    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * Set per-file charsets to use for parsing properties files. <p>Only
     * applies to classic properties files, not to XML files.
     *
     * @param fileEncodings Properties with filenames as keys and charset names
     * as values. Filenames have to match the basename syntax, with optional
     * locale-specific appendices: e.g. "WEB-INF/messages" or
     * "WEB-INF/messages_en".
     * @see #setBasenames
     * @see org.springframework.util.PropertiesPersister#load
     */
    public void setFileEncodings(Properties fileEncodings) {
        this.fileEncodings = fileEncodings;
    }

    /**
     * Calculate all filenames for the given bundle basename and Locale. Will
     * calculate filenames for the given Locale, the system Locale (if
     * applicable), and the default file.
     *
     * @param basename the basename of the bundle
     * @param locale the locale
     * @return the List of filenames to check
     * @see #setFallbackToSystemLocale
     * @see #calculateFilenamesForLocale
     */
    protected List calculateAllFilenames(String basename, Locale locale) {
        synchronized (this.cachedFilenames) {
            Map localeMap = (Map) this.cachedFilenames.get(basename);
            if (localeMap != null) {
                List filenames = (List) localeMap.get(locale);
                if (filenames != null) {
                    return filenames;
                }
            }
            List filenames = new ArrayList(4);
            filenames.addAll(calculateFilenamesForLocale(basename, locale));
            filenames.add(basename);
            if (localeMap != null) {
                localeMap.put(locale, filenames);
            } else {
                localeMap = new HashMap();
                localeMap.put(locale, filenames);
                this.cachedFilenames.put(basename, localeMap);
            }
            return filenames;
        }
    }

    /**
     * Calculate the filenames for the given bundle basename and Locale,
     * appending language code, country code, and variant code. E.g.: basename
     * "messages", Locale "de_AT_oo" -> "messages_de_AT_OO", "messages_de_AT",
     * "messages_de".
     *
     * @param basename the basename of the bundle
     * @param locale the locale
     * @return the List of filenames to check
     */
    protected List calculateFilenamesForLocale(String basename, Locale locale) {
        List result = new ArrayList(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder temp = new StringBuilder(basename);

        if (language.length() > 0) {
            temp.append('_').append(language);
            result.add(0, temp.toString());
        }

        if (country.length() > 0) {
            temp.append('_').append(country);
            result.add(0, temp.toString());
        }

        if (variant.length() > 0) {
            temp.append('_').append(variant);
            result.add(0, temp.toString());
        }

        return result;
    }

    /**
     * Get a PropertiesHolder for the given filename, either from the cache or
     * freshly loaded.
     *
     * @param filename the bundle filename (basename + Locale)
     * @return the current PropertiesHolder for the bundle
     */
    protected PropertiesHolder getProperties(String filename) {
        synchronized (this.cachedProperties) {
            PropertiesHolder propHolder = (PropertiesHolder) this.cachedProperties.get(filename);
            //if we already have a propholder, just return it
            if (propHolder != null) {
                // up to date
                return propHolder;
            }
            //otherwise, create a new propHolder from disk
            return refreshProperties(filename, propHolder);
        }
    }

    /**
     * Get a PropertiesHolder for the given filename, either from the cache or
     * freshly loaded.
     *
     * @param filename the bundle filename (basename + Locale)
     * @return the current PropertiesHolder for the bundle
     */
    protected PropertiesHolder getStructureProperties(Locale locale) {
        synchronized (this.cachedStructureProperties) {
            PropertiesHolder propHolder = cachedStructureProperties.get(locale);
            if (propHolder != null) {
                // up to date
                return propHolder;
            }
            return refreshStructureProperties(locale, propHolder);
        }
    }

    protected PropertiesHolder getDefaultStructureProperties() {
        if (defaultStructureProperties == null) {
            //parse the content of the structure document and create correct properties
            //initialize our new property object
            Properties properties = parse2properties(getStructXML(), null);
            defaultStructureProperties = new PropertiesHolder(properties);
        }
        return defaultStructureProperties;
    }

    PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
        Resource resource = this.resourceLoader.getResource(filename + PROPERTIES_SUFFIX);
        if (!resource.exists()) {
            resource = this.resourceLoader.getResource(filename + XML_SUFFIX);
        }
        if (resource.exists()) {
            try {
                Properties props = loadProperties(resource, filename);
                propHolder = new PropertiesHolder(props);
            } catch (IOException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Could not parse properties file [" + resource.getFilename() + "]", ex);
                }
                // Empty holder representing "not valid".
                propHolder = new PropertiesHolder();
            }
        } else {
            // Resource does not exist.
            if (logger.isDebugEnabled()) {
                logger.debug("No properties file found for [" + filename + "] - neither plain properties nor XML");
            }
            // Empty holder representing "not found".
            propHolder = new PropertiesHolder();
        }

        this.cachedProperties.put(filename, propHolder);
        return propHolder;
    }

    /**
     * Refresh the PropertiesHolder for the given locale in Structures The
     * holder can be
     * <code>null</code> if not cached before.
     *
     * @param locale - the Locale for the cache key
     * @param propHolder the current PropertiesHolder for the bundle
     * @return the propholder
     */
    protected PropertiesHolder refreshStructureProperties(Locale locale, PropertiesHolder propHolder) {
        /*
         * @TODO: Check for discrepancy between Locale in call and usage of only
         * language part in properties parsing. Discuss repercussions... SH,
         * 20100518
         */

        //parse the content of the structure document and create correct properties
        propHolder = new PropertiesHolder(parse2properties(getStructXML(), locale.getLanguage()));

        //add property holder to cache
        this.cachedStructureProperties.put(locale, propHolder);

        return propHolder;
    }

    /**
     * Load the properties from the given resource.
     *
     * @param resource the resource to load from
     * @param filename the original bundle filename (basename + Locale)
     * @return the populated Properties instance
     * @throws IOException if properties loading failed
     */
    protected Properties loadProperties(Resource resource, String filename) throws IOException {
        InputStream is = resource.getInputStream();
        Properties props = new Properties();
        try {
            if (resource.getFilename().endsWith(XML_SUFFIX)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Loading properties [" + resource.getFilename() + "]");
                }
                this.propertiesPersister.loadFromXml(props, is);
            } else {
                String encoding = null;
                if (this.fileEncodings != null) {
                    encoding = this.fileEncodings.getProperty(filename);
                }
                if (encoding == null) {
                    encoding = this.defaultEncoding;
                }
                if (encoding != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Loading properties [" + resource.getFilename() + "] with encoding '" + encoding + "'");
                    }
                    this.propertiesPersister.load(props, new InputStreamReader(is, encoding));
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Loading properties [" + resource.getFilename() + "]");
                    }
                    this.propertiesPersister.load(props, is);
                }
            }
            return props;
        } finally {
            is.close();
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + ": basenames=[" + StringUtils.arrayToCommaDelimitedString(this.basenames) + "]";
    }

    /**
     * Set the ResourceLoader to use for loading bundle properties files. <p>The
     * default is a DefaultResourceLoader. Will get overridden by the
     * ApplicationContext if running in a context, as it implements the
     * ResourceLoaderAware interface. Can be manually overridden when running
     * outside of an ApplicationContext.
     *
     * @see org.springframework.core.io.DefaultResourceLoader
     * @see org.springframework.context.ResourceLoaderAware
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
    }

    private Document getStructXML() {
        if (structXML == null) {
            //first, load structure XML from DB and parse it into a W3C DOM Document
            try {
                con = BiBiTools.getDataSource().getConnection();           
                //Statement for retrieving xml from DB with structure...
                PreparedStatement pstmnt = con.prepareStatement("SELECT CONTENT FROM STRUCTURE WHERE TIME=(SELECT MAX(TIME) FROM STRUCTURE)");
                ResultSet dbresult = pstmnt.executeQuery();
                if (dbresult != null) {
                    log.debug("got result from database when searching a structure");
                    //parsing strings to xml document in private method
                    if (dbresult.next()) {
                        Clob clob = dbresult.getClob(1);
                        structXML = createDoc(clob.getAsciiStream());
                    }
                    //add structure xml to the search index.
                    BiBiServSearch.getInstance().addItem(structXML, null);
                    //cleanup result resource
                    dbresult.close();
                }
                //cleanup statement resource
                pstmnt.close();
            } catch (SQLException ex) {
                log.fatal("Caught SQL Exception: " + ex.getLocalizedMessage());
            } catch (DBConnectionException ex) {
                log.fatal("Caught Naming Exception: " + ex.getLocalizedMessage());
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        log.fatal(ex.getMessage());
                    }
                }
            }
        }
        return structXML;
    }

    private Document createDoc(InputStream in) {

        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();


            //Workaround to prevent UTF-8 malformed sequence exception
            //Create an UTF-8 String from given InputStream and convert String
            //back to InputSource.
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                in.close();
            }
            return builder.parse(new InputSource(new StringReader(sb.toString())));
        } catch (SAXException ex) {
            log.error("SAX-Exception in createDoc.", ex);
        } catch (IOException ex) {
            log.error("IO-Exception in createDoc.", ex);
        } catch (ParserConfigurationException ex) {
            log.error("ParserConfigurationException in createDoc. Message: " + ex);
        }
        return null;
    }

    /*
     * parse the structure XML into single properties for the given language
     */
    private Properties parse2properties(Document doc, String language) {



        String langtest;

        if (language == null) {
            langtest = "not(@xml:lang)";
        } else {
            langtest = "@xml:lang='" + language + "'";
        }


        //initialize our new property object
        Properties properties = new Properties();

        // storage object for xpath results
        Object result;
        // variable for different parsing expressions
        XPathExpression expr;
        // variable for resulting nodes
        NodeList nodes;


        try {

            /**
             * Catch all name elements that contains no lang attribute
             */
            nodes = (NodeList) xpath.compile("//*[(@id)]/cms:name[" + langtest + "]").evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                // createNameProperty(i, nodes, properties);
                String key = nodes.item(i).getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "_name";
                properties.setProperty(key, nodes.item(i).getTextContent());
            }

            /**
             * Catch all name elements that belong to an enumParam and contains
             * no lang attribute
             */
            nodes = (NodeList) xpath.compile("//cms:enumParam[(@id)]/cms:values/cms:name[" + langtest + "]").evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                NodeList keyValueList = nodes.item(i).getParentNode().getChildNodes();
                String key = "";
                for (int j = 0; j < keyValueList.getLength(); j++) {
                    if (keyValueList.item(j).getNodeName().contains("key")) {
                        key = nodes.item(i).getParentNode().getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "_" + keyValueList.item(j).getTextContent();
                    }
                }
                if (!key.isEmpty()) {
                    properties.setProperty(key, nodes.item(i).getTextContent());
                }
            }


            /**
             * Query for shortDescriptions and their parent ID
             */
            expr = xpath.compile("//*[(@id)]/cms:shortDescription[" + langtest + "]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                properties.setProperty(nodes.item(i).getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "_shortDescription", nodes.item(i).getTextContent());
            }

            /**
             * Query for categoryDescriptions and their parent ID
             */
            expr = xpath.compile("//*[(@id)]/cms:categoryDescription[" + langtest + "]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                properties.setProperty(nodes.item(i).getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "_categoryDescription", nodes.item(i).getTextContent());
            }

            /**
             * Query for Category Title and their parent ID
             */
            expr = xpath.compile("//*[(@id)]/cms:title[" + langtest + "]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                properties.setProperty(nodes.item(i).getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "_title", nodes.item(i).getTextContent());
            }

            /**
             * Query for Keywords and their parent ID
             */
            expr = xpath.compile("//*[(@id)]/cms:keywords[" + langtest + "]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                properties.setProperty(nodes.item(i).getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "_keywords", nodes.item(i).getTextContent());
            }

            /**
             * Query for description...
             */
            expr = xpath.compile("//*[(@id)]/cms:description[" + langtest + "]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                properties.setProperty(nodes.item(i).getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "_description", transformWithXslt(nodes.item(i).getChildNodes()));
            }
            /**
             * Query for customContent
             */
            expr = xpath.compile("//*[(@id)]/cms:customContent[" + langtest + "]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                properties.setProperty(nodes.item(i).getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "_customContent", transformWithXslt(nodes.item(i).getChildNodes()));
            }
            expr = xpath.compile("//*[(@id)]/*[not(@id)]/cms:customContent[" + langtest + "]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                properties.setProperty(nodes.item(i).getParentNode().getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "_customContent", transformWithXslt(nodes.item(i).getChildNodes()));
            }


            /**
             * Query for : introductoryText
             */
            expr = xpath.compile("//*[(@id)]/cms:introductoryText[" + langtest + "]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                properties.setProperty(nodes.item(i).getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "_introductoryText", transformWithXslt(nodes.item(i).getChildNodes()));
            }
        } catch (XPathExpressionException ex) {
            log.error("Got XPathExpressionException: " + ex, ex);
        }

        return properties;
    }

    /**
     * This method gets a W3C DOM-Node that is transformed with an XSLT Script
     * The script eliminates namespace prefixes from all elements. This method
     * should only be called to remove microhtml/minihtml prefixes that are used
     * in customContent
     *
     * @param node
     * @return String representation of the processed XML...
     */
    private String transformWithXslt(Node node) {
        StringWriter sw = new StringWriter();
        StreamResult resultstr = new StreamResult(sw);

        try {
            DOMSource dsource = new DOMSource(node);
            trans.transform(dsource, resultstr);
            return sw.toString();
        } catch (TransformerException ex) {
            log.fatal(ex.getMessage());
        }
        return sw.toString();
    }

    /**
     * This method gets a W3C DOM-Nodelist. Each node from this list is
     * transformed with an XSLT Script. See transformWithXslt(Node node) for a
     * description.
     *
     * @param nl
     * @return linked String representation of all Nodes
     */
    private String transformWithXslt(NodeList nl) {
        String ret = "";
        for (int counter = 0; counter < nl.getLength(); ++counter) {
            ret = ret + transformWithXslt(nl.item(counter));
        }
        return ret;
    }

    /**
     * Function to add a Tool Description's message-relevant content to this
     * messagesource by identifying the contained languages and handing it over
     * to the parser for creation of properties
     *
     * @param name
     * @param runnableItemDoc
     */
    public void addToolDescription(String name, Document runnableItemDoc) {
        log.debug("---entered addToolDescription for tool '" + name + "'");
        XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        NamespaceContext nsc = new de.unibi.techfak.bibiserv.xml.NamespaceContext();
        // storage object for xpath results
        Object result;
        // variable for different parsing expressions
        XPathExpression expr;
        // variable for resulting nodes
        NodeList nodes;
        /**
         * Set Namespaces
         */
        nsc.addNamespace("http://www.w3.org/XML/1998/namespace", "xml");
        nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms.microhtml", "microhtml");
        nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms.minihtml", "minihtml");
        nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms", "cms");
        xpath.setNamespaceContext(nsc);

        //find all languages in document
        HashMap<String, String> languages = new HashMap<String, String>();
        try {
            expr = xpath.compile("//@xml:lang");
            result = expr.evaluate(runnableItemDoc, XPathConstants.NODESET);
            nodes = (NodeList) result;

            for (int i = 0; i < nodes.getLength(); i++) {
                languages.put(nodes.item(i).getNodeValue(), "");
            }

            //loop over languages in document
            Set<String> list_of_languages = languages.keySet();
            for (String language : list_of_languages) {
                //create locale for this language as key for caching
                Locale l = new Locale(language);
                //try and find thise map for this locale in the cache:
                synchronized (this.cachedToolProperties) {
                    log.debug("----trying to get map for language '" + language + "'");
                    Map<String, PropertiesHolder> m = cachedToolProperties.get(l);
                    if (m == null) { //no map in existence, Create new one
                        log.debug("----did not find map. Creating new one!");
                        m = new HashMap<>();
                        // parse properties for current language and add to map
                        m.put(name, new PropertiesHolder(parse2properties(runnableItemDoc, language)));
                        //add new map to cache
                        cachedToolProperties.put(l, m);
                    } else { //map exists. Use and modify it!
                        if (log.getLevel() == Level.DEBUG) {
                            log.debug("----Got Map. Contains:");
                            for (String defname : m.keySet()) {
                                log.debug(defname);
                            }
                            log.debug("-----keylist end.");
                        }
                        // parse properties for the current language and add the properties to
                        // the map, thereby keeping the other tools infos
                        m.put(name, new PropertiesHolder(parse2properties(runnableItemDoc, language)));
                    }
                }
            }
            //add default (language-less) properties
            synchronized (this.defaultToolProperties) {
                defaultToolProperties.put(name, new PropertiesHolder(parse2properties(runnableItemDoc, null)));
            }
        } catch (XPathExpressionException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void removeToolDescription(String name) {
        //iterate over all languages and remove map for tool 'name'

        //log.debug("---Entered removeToolDescription for tool '" + name + "'");
        Set<Locale> list_of_languages = this.cachedToolProperties.keySet();
        for (Locale l : list_of_languages) {
            //log.debug("----working on Locale '" + l + "'");
            synchronized (this.cachedToolProperties) {
                Map<String, PropertiesHolder> m = cachedToolProperties.get(l);

                //and some diagnostic information if we are debugging:
                if (log.getLevel() == Level.DEBUG) {
                    log.debug("----got Map '" + m + "'. It contains these key(s):");
                    for (String defname : m.keySet()) {
                        log.debug(defname);
                    }
                    log.debug("-----keylist end. Now trying to remove '" + name + "'");
                }
                m.remove(name);
                if (log.getLevel() == Level.DEBUG) {
                    log.debug("----After remove. Map '" + m + "' now contains these key(s):");
                    for (String defname : m.keySet()) {
                        log.debug(defname);
                    }
                    log.debug("-----keylist end.");
                }
            }
        }
        synchronized (this.defaultToolProperties) {
            defaultToolProperties.remove(name);






        }
    }

    /**
     * PropertiesHolder for caching.
     */
    protected class PropertiesHolder {

        private Properties properties;
        /**
         * Cache to hold already generated MessageFormats per message code
         */
        private final Map cachedMessageFormats = new HashMap();

        public PropertiesHolder(Properties properties) {
            this.properties = properties;
        }

        public PropertiesHolder() {
        }

        public Properties getProperties() {
            return properties;
        }

        public String getProperty(String code) {
            if (this.properties == null) {
                return null;
            }
            return this.properties.getProperty(code);
        }

        public MessageFormat getMessageFormat(String code, Locale locale) {
            if (this.properties == null) {
                return null;
            }
            synchronized (this.cachedMessageFormats) {
                Map localeMap = (Map) this.cachedMessageFormats.get(code);
                if (localeMap != null) {
                    MessageFormat result = (MessageFormat) localeMap.get(locale);
                    if (result != null) {
                        return result;
                    }
                }
                String msg = this.properties.getProperty(code);
                if (msg != null) {
                    if (localeMap == null) {
                        localeMap = new HashMap();
                        this.cachedMessageFormats.put(code, localeMap);
                    }
                    MessageFormat result = createMessageFormat(msg, locale);
                    localeMap.put(locale, result);
                    return result;
                }
                return null;
            }
        }
    }
}
