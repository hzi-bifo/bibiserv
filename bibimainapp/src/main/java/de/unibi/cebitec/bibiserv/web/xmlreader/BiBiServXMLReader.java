/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2012 BiBiServ Curator Team]"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.xmlreader;

import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.xml.NamespaceContext;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * BiBiServXMLReader Class makes XPATH Queries to the specified XML Document in
 * the DERBY DATABASE
 *
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de Jan Krueger -
 * jkrueger(at)cebitec.uni-bielefeld.de
 */
public class BiBiServXMLReader {

    private XPath xpath;
    private XPathExpression expr;
    private Document structure;
    private Document item;
    private NodeList nodes;
    private Object result;
    private HashMap<String, ItemContent> itemContentHash;
    private static final Logger log = Logger.getLogger(BiBiServXMLReader.class);
    private URL url;
    public static final String xmlNS = "http://www.w3.org/XML/1998/namespace";
    public static final String microhtmlNS = "bibiserv:de.unibi.techfak.bibiserv.cms.microhtml";
    public static final String minihtmlNS = "bibiserv:de.unibi.techfak.bibiserv.cms.minihtml";
    public static final String cmsNS = "bibiserv:de.unibi.techfak.bibiserv.cms";
    public static final String jnlpNS = "http://java.sun.com/dtd/jnlp-6.0.dtd";
    
    public static final String cmsNS_location="http://bibiserv.techfak.uni-bielefeld.de/xsd/bibiserv2/BiBiServAbstraction.xsd";

    public BiBiServXMLReader() {
        XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        NamespaceContext nsc = new de.unibi.techfak.bibiserv.xml.NamespaceContext();

        /**
         * Set Namespaces
         */
        nsc.addNamespace(xmlNS, "xml");
        nsc.addNamespace(microhtmlNS, "microhtml");
        nsc.addNamespace(minihtmlNS, "minihtml");
        nsc.addNamespace(cmsNS, "cms");
        nsc.addNamespace(jnlpNS, "jnlp");
        xpath.setNamespaceContext(nsc);
    }

    /**
     * Returns hash containing current items (runnable + linked + normal)
     *
     * @return Returns hash containing current items (runnable + linked +
     * normal)
     */
    public HashMap<String, ItemContent> getItemContentHash() {
        if (this.itemContentHash == null) {
            this.createItemContentHash();
        }
        return itemContentHash;
    }

    /**
     * Add a new item (as DOM document) with given id to current item hash.
     * Existing item with same id will be overwritten.
     *
     * @param id - id of item
     * @param item - DOM document of item
     */
    public void addItemtoItemContentHash(String id, Document item) {
        getItemContentHash().put(id, createItemContent(item));
    }

    /**
     * Remove an item with given id from current item hash.
     *
     * @param id - id of item
     */
    public void removeItemfromItemContentHash(String id) {
        getItemContentHash().remove(id);
    }

    public void refresh() {
        itemContentHash = null;
        structure = null;
    }

    /**
     * initializes the structure document from DB table
     */
    private void initStructure() {
        if (structure == null) {
            String dbquery = "SELECT CONTENT FROM STRUCTURE WHERE TIME=(SELECT MAX(TIME) FROM STRUCTURE)";
            structure = requestDocument(dbquery);
        }
    }

    /**
     * Method for retrieving Structure from DB as String
     *
     * @return
     */
    public String getCompleteXMLStructure() {
        String retval = null;
        try {
            //initialize structure document from DB
            initStructure();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StreamResult result_tmp = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(structure);
            transformer.transform(source, result_tmp);
            retval = result_tmp.getWriter().toString();
        } catch (TransformerException ex) {
            log.fatal("The following exception occured when calling getCompleteXMLStructure in BiBiServXmlReader...", ex);
        }
        return retval;
    }

    /**
     * Method for writing structure to database
     *
     * @param xml
     * @return
     */
    public boolean insertNewXMLStructure(String xml) {
        Connection con = null;
        try {
            
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("insert into STRUCTURE(TIME, CONTENT) values(?, ?)");
            java.util.Date date = new java.util.Date();
            SimpleDateFormat datform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            datform.setTimeZone(TimeZone.getDefault());
            pstmnt.setString(1, datform.format(date));
            pstmnt.setString(2, xml);
            pstmnt.executeUpdate();
            //refresh this object's hashes to make the inserted data visible to everyone else
            refresh();
            pstmnt.close();
            return true;
        } catch (Exception ex) {
            log.fatal("Any exception occured when calling update in BiBiServXmlReader...", ex);
            return false;
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                log.fatal(null, ex);
            }
        }
    }

    /**
     * Request a document from database. !!! ----------- Attention
     * --------------- !!! - SQL query must select the xml structure as first
     * element - the xml structure MUST be stored as CLOB !!! -----------
     * Attention --------------- !!!
     *
     * JK 10.03.2010
     *
     * @param dbquery
     * @return dom document
     */
    private Document requestDocument(String dbquery) {
        Document retval = null;
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            
            //Statement for retrieving xml from DB...
            PreparedStatement pstmnt = con.prepareStatement(dbquery);
            ResultSet rset = pstmnt.executeQuery();
            log.debug("ExecuteQuery '" + dbquery + "' ...");
            if (rset.next()) {
                Clob clob = rset.getClob(1);
                retval = createDoc(clob.getCharacterStream());
            }
            rset.close();
            pstmnt.close();
        } catch (DBConnectionException | SQLException ex) {
            //CATCHALL EXCEPTION
            log.fatal("The following exception occured when calling requestDocument in BiBiServXmlReader...:", ex);
        } finally {
            try {
               if (con != null) {
                    con.close();
               }
            } catch (SQLException ex) {
                log.fatal("The following SQL - exception occured when trying to close the connection to database in BiBiServXmlReader...:", ex);
            }
        }
        return retval;
    }

    /** 
     *  Return document root element of bibiserv structure - should be a category node.
     * 
     * @return 
     */
    public Node getRootElement(){
        return structure.getDocumentElement();
    }
    
    /**
     * Public Method that can be called from classes having an instance of
     * BiBiServXMLReader Producing NodeList of DOM-Nodes
     *
     * @param xpathquery
     * @return
     */
    public NodeList getNodeListStructure(String xpathquery) {
        initStructure();
        return getNodeList(structure, xpathquery);
    }

    /**
     *
     * @param xmldocument a xml document where you want to search in...
     * @param query an xpath query
     * @return a nodelist according to the xpath expression...
     */
    private NodeList getNodeList(Document xmldocument, String query) {
        try {
            expr = xpath.compile(query);
            result = expr.evaluate(xmldocument, XPathConstants.NODESET);
            nodes = (NodeList) result;
            /*
             * print warning if evaluation returns no value ...
             */
            if (nodes == null || nodes.getLength() == 0) {
                log.warn("Evaluate expression '" + query + "' returns no result ! ");
            }
            return nodes;
        } catch (XPathExpressionException ex) {
            log.fatal(ex);
        } catch (Exception e) {
            log.fatal(e);
        }
        return null;
    }

    /**
     * Method creating an Hashmap that contains all items contained in db
     */
    private void createItemContentHash() {
        itemContentHash = new HashMap<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            //Statement for retreiving xml from DB...
            PreparedStatement pstmnt = con.prepareStatement("SELECT ID,ITEM FROM ITEM");
            ResultSet rs = pstmnt.executeQuery();
            while (rs.next()) {
                String itemid = rs.getString(1);
                Clob clob = rs.getClob(2);
                Document doc = createDoc(clob.getCharacterStream());
                itemContentHash.put(itemid, createItemContent(doc));
            }
            rs.close();
        } catch (Exception ex) {
            //CATCHALL EXCEPTION
            log.fatal("The following exception occured when calling createItemContentHash in BiBiServXmlReader...", ex);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                log.fatal("The following exception occured when calling requestDocument in BiBiServXmlReader...", ex);
            }
        }
    }

    private ItemContent createItemContent(Document item) {
        try {
            if (item != null) {
                NodeList xmlresult;
                String queryRunnableName = " //cms:runnableItem/cms:name\n";
                expr = xpath.compile(queryRunnableName);
                xmlresult = (NodeList) expr.evaluate(item, XPathConstants.NODESET);

                /*
                 * ============== Check for runnable item ===================
                 */
                if (xmlresult != null && xmlresult.getLength() > 0) {
                    // helper
                    boolean submission = false;
                    /**
                     * Indicator if 'run in the cloud' is needed. (Only needed
                     * if submission is selected too.
                     */
                    boolean runInTheCloud = false;

                    RunnableItemContent itemContent = new RunnableItemContent();

                    // set item Name in itemContent-Object
                    itemContent.setItemName(xmlresult.item(0).getTextContent());
                    // set docuemnt representation
                    itemContent.setDoc(item);

                    /* Search for all views, create an viuew object and add it to a map of views. */
                    String queryViewType = "//cms:runnableItem/cms:view\n";
                    expr = xpath.compile(queryViewType);

                    NodeList viewResult = (NodeList) expr.evaluate(item, XPathConstants.NODESET);
                    List<View> views = itemContent.getViews();

                    for (int it = 0; it < viewResult.getLength(); it++) {
                        View view = createViewfromElement((Element) (viewResult.item(it)));
                        views.add(view);
                        if (view.getType().equals("submission")) { // check for submission -> add reset_submission later
                            submission = true;
                            /**
                             * If submission is needed, RITC also.
                             */
                            runInTheCloud = true;
                        }
                    }

                    /*
                     * The manual is NOT declared as view, but if its defined the item
                     * has an manual view
                     */
                    if (item.getElementsByTagNameNS(cmsNS, "manual").getLength() > 0) {
                        View manual = createViewfromElement((Element) (item.getElementsByTagNameNS(cmsNS, "manual").item(0)), "manual");
                        views.add(manual);
                    }

                    /**
                     * The FAQ ist NOT declared as view, but if faq elements are
                     * defined every item has a faq view
                     */
                    if (item.getElementsByTagNameNS(cmsNS, "faq").getLength() > 0) {
                        View faq = createViewfromElement((Element) (item.getElementsByTagNameNS(cmsNS, "faq").item(0)), "faq");
                        views.add(faq);
                    }

                    /*
                     * The reference is NOT declared as view, but if references defined 
                     * every item has  a reference view, so add it manually.
                     */
                    if (item.getElementsByTagNameNS(cmsNS, "references").getLength() > 0) {
                        View references = createViewfromElement((Element) (item.getElementsByTagNameNS(cmsNS, "references").item(0)), "references");
                        views.add(references);
                    }

                    /**
                     * Query for functions IDS of all executables
                     */
                    String querySubType = "//cms:runnableItem/cms:executable/cms:function/@id\n";
                    expr = xpath.compile(querySubType);

                    NodeList subResult = (NodeList) expr.evaluate(item, XPathConstants.NODESET);
                    List<String> functions = itemContent.getFunctions();

                    for (int it = 0; it < subResult.getLength(); it++) {
                        String type = subResult.item(it).getNodeValue();
                        functions.add(type);
                    }

                    /**
                     * query for webstart ids
                     */
                    // get all webstart elements and add them to webstart map
                    NodeList webstartList = item.getElementsByTagNameNS(cmsNS, "webstart");
                    Map<String, Element> webstarts = itemContent.getWebstart();

                    // add all webstart id's to item content
                    for (int it = 0; it < webstartList.getLength(); ++it) {
                        Element webstart = (Element) webstartList.item(it);
                        // get id from webstart

                        String webstartid = webstart.getAttribute("id");
                        // add viewtype for webstart -- webstart elements should be order 
                        // directly after submision or first subType 

                        if (views.contains(new View("submission"))) {
                            views.add(1, createViewfromElement(webstart, "webstart"));
                        } else {
                            views.add(0, createViewfromElement(webstart, "webstart"));
                        }

                        // get reference to jnlp node
                        Element jnlp = (Element) webstart.getElementsByTagNameNS(jnlpNS, "jnlp").item(0);

                        webstarts.put(webstartid, jnlp);
                    }

                    /*
                     * in the case of a existing submission view ...
                     */
                    if (submission) {
                        /* ... add all reset_session view to the end */
                        views.add(new View("reset_session", "reset_session", true, null));
                        /* ... and add ritc (RunInTheCloud) view after submission view
                        Attention : if ritc view is rendered depends on BiBiServ Property "ritc"
                        See NaviBean.getViewLinks 
                        */
                        views.add(1, new View("RITC", "ritc", true, null));
                        
                    }

                    return itemContent;  
                }

                /*
                 * ============== Check for item ==============
                 */
                String queryItemName = "//cms:item/cms:name\n";
                expr = xpath.compile(queryItemName);
                xmlresult = (NodeList) expr.evaluate(item, XPathConstants.NODESET);
                ///check if request for item had a result..
                if (xmlresult != null && xmlresult.getLength() > 0) {
                    TextItemContent itemContent = new TextItemContent();
                    // set item name
                    itemContent.setItemName(xmlresult.item(0).getTextContent());
                    // set xml representation
                    itemContent.setDoc(item);

                    return itemContent;
                }


                /*
                 * ===================== Check for linked item =====================           
                 * The idea of an linkeditem is to redirect the request to a given server, 
                 * an external or internal URI
                 */
                String queryLinkedItemName = "//cms:linkedItem/cms:name\n";
                expr = xpath.compile(queryLinkedItemName);
                xmlresult = (NodeList) expr.evaluate(item, XPathConstants.NODESET);

                if (xmlresult != null && xmlresult.getLength() > 0) {
                    LinkedItemContent itemContent = new LinkedItemContent();

                    // set item name
                    itemContent.setItemName(xmlresult.item(0).getTextContent());
                    // set xml representation
                    itemContent.setDoc(item);

                    ///requesting url-element from the current linked item...
                    String queryLinkedItemUrl = "//cms:linkedItem/cms:url\n";
                    expr = xpath.compile(queryLinkedItemUrl);
                    xmlresult = (NodeList) expr.evaluate(item, XPathConstants.NODESET);
                    if (xmlresult != null && xmlresult.getLength() > 0) {
                        itemContent.setURL(xmlresult.item(0).getTextContent());
                        try {
                            // check if have a real (non relative) URL is given
                            url = new URL(xmlresult.item(0).getTextContent());
                            // set protocol
                            itemContent.setProtocol(url.getProtocol());
                            // set host
                            itemContent.setServer(url.getHost());
                            // set port
                            if (url.getPort() != -1) {//case when no explicit port has been entered
                                itemContent.setPort(url.getPort());
                            } else {
                                itemContent.setPort(url.getDefaultPort());
                            }
                        } catch (MalformedURLException ex) {
                            // if url isn't a full URL, it might be a relative URL
                            itemContent.setLocal(true);

                        }
                    }
                    return itemContent;
                }
            }
        } catch (XPathExpressionException ex) {
            log.fatal("The following exception occured when calling requestDocument in BiBiServXmlReader...", ex);
        }
        return null;
    }

    /**
     * Method creating a document from an inputstream
     *
     * @param InputStream in
     * @return xml Document
     */
    private Document createDoc(Reader in) {

        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();

            //Workaround to prevent UTF-8 malformed sequence exception
            //Create an UTF-8 String from given InputStream and convert String
            //back to InputSource.
//            StringBuilder sb = new StringBuilder();
//            String line;
//            try {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line);
//                }
//            } finally {
//                in.close();
//            }
            return builder.parse(new InputSource(new BufferedReader(in)));
        } catch (SAXException ex) {
            log.error("SAX-Exception in createDoc.", ex);
        } catch (IOException ex) {
            log.error("IO-Exception in createDoc.", ex);
        } catch (ParserConfigurationException ex) {
            log.error("ParserConfigurationException in createDoc. Message: ", ex);
        }
        return null;
    }

    /**
     * Create a view object from all kind of view elements. Supported are :
     * submission, manual, other, webstart, download
     *
     * @param view
     * @return
     */
    private View createViewfromElement(Element view) {
        return createViewfromElement(view, null);
    }

    /**
     * Create a view object from all kind of view elements.
     *
     * Supported are : submission, manual, other, webstart, download
     *
     *
     * @param view
     * @param type - hard coded view type
     * @return
     */
    private View createViewfromElement(Element view, String type) {

        // create new view element
        View vt = new View();

        //set id from element
        if (view.hasAttribute("id")) {
            vt.setId(view.getAttribute("id"));
        }

        //set all views visible by default
        vt.setVisible(true);

        // type, subtype and hidden attribute could be  optional
        if (view.hasAttribute("type")) {
            vt.setType(view.getAttribute("type"));
        }

        if (view.hasAttribute("subtype")) {
            vt.setSubtype(view.getAttribute("subtype"));
        }
        if (view.hasAttribute("hidden")) {
            if (view.getAttribute("hidden").equalsIgnoreCase("true")) {
                vt.setVisible(false);
            } else {
                vt.setVisible(true);
            }
        }

        // type argument overwrites attribute
        if (type != null) {
            vt.setType(type);
        }

        // if id is null set type as id
        if (vt.getId() == null) {
            vt.setId(vt.getType());
        }

        return vt;
    }
}
