/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Benjamin Paassen, CeBiTec,
 * http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 Benjamin Paassen"
 *
 * Contributor(s): Benjamin Paassen, Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.search;

import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.index.SuffixTreeAppendSession;
import de.unibi.cebitec.bibiserv.search.util.Tuple;
import de.unibi.cebitec.bibiserv.search.xmltools.XPathProcessor;
import de.unibi.cebitec.bibiserv.search.xmltools.XSLTProcessor;
import de.unibi.cebitec.bibiserv.search.xmltools.XSLTScriptEnum;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Extends the SearchClass with special support for BiBiServ2 xml files,
 * especially all kind of items : Items, RunnableItems and LinkedItems
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class BiBiServSearch extends Search {

    private static BiBiServSearch bibiservsearch = null;

    public static BiBiServSearch getInstance() {
        if (bibiservsearch == null) {
            bibiservsearch = new BiBiServSearch();
        }
        return bibiservsearch;
    }

    private BiBiServSearch() {
    }

    /**
     * Add an item to index. All items defined by
     * http://bibiserv.techfak.uni-bielefeld.de/xsd/bibiserv2/BiBiServAbstraction.xsd
     * are supported (aka item, linkedItem, runnableItem, category)
     *
     * @param item - DOM document containing a item
     * @param appDir This only is used if the item is a runnable item.
     * This should be the server directory where the runnable item is stored.
     */
    public void addItem(final Document item, final File appDir) {
        //get a session object.
        final SuffixTreeAppendSession session = new SuffixTreeAppendSession();
        try {
            //start append session for the suffix tree.
            session.open();
        } catch (SuffixTreeException ex) {
            handleException(ex);
            return;
        }
        try {
            //xpath processor
            final XPathProcessor processor = new XPathProcessor();
            //String writer for xslt results.
            StringWriter xsltresult;
            // extract id from item
            final String id = item.getDocumentElement().getAttribute("id");
            // determine item type
            switch (item.getDocumentElement().getLocalName()) {
                case "runnableItem": {
                    //list for actual downloadables in runnable items.
                    final ArrayList<Tuple<String, File>> downloadables = new ArrayList<>();
                    // runnable item must handle with care, for each view a unique URL exists
                    // welcome (everything but views and executable)
                    xsltresult = new StringWriter();
                    XSLTProcessor.runXSLTScript(XSLTScriptEnum.BIBISERVITEMS,
                            new DOMSource(item), xsltresult);
                    addDocument("/" + id,
                            new BufferedReader(new StringReader(xsltresult.toString())),
                            session);
                    // references
                    NodeList nl = processor.runxpath(item, "//cms:references");
                    if (nl.getLength() > 0) {
                        xsltresult = new StringWriter();
                        XSLTProcessor.runXSLTScript(XSLTScriptEnum.BIBISERVTEXT,
                                new DOMSource(nl.item(0)), xsltresult);
                        addDocument("/" + id + "?viewType=references",
                                new BufferedReader(new StringReader(xsltresult.toString())),
                                session);
                    }
                    //manual
                    nl = processor.runxpath(item, "//cms:manual");
                    if (nl.getLength() > 0) {
                        xsltresult = new StringWriter();
                        XSLTProcessor.runXSLTScript(XSLTScriptEnum.BIBISERCDESCRIPTIONS,
                                new DOMSource(nl.item(0)), xsltresult);
                        addDocument("/" + id + "?viewType=manual",
                                new BufferedReader(new StringReader(xsltresult.toString())),
                                session);
                    }
                    // views
                    StringBuilder sb = new StringBuilder();
                    nl = processor.runxpath(item, "//cms:view");
                    for (int i = 0; i < nl.getLength(); ++i) {

                        // cleanup StringBuilder
                        sb.setLength(0);

                        Element element = (Element) nl.item(i);
                        String viewtype = element.getAttribute("type");
                        switch (viewtype) {
                            case "download":
                                // download = download view + downloadable
                                NodeList nl_d = processor.runxpath(item, "//cms:downloadable");
                                for (int nodeIndex = 0; nodeIndex < nl_d.getLength(); ++nodeIndex) {
                                    Node node = nl_d.item(nodeIndex);
                                    // extract text from downloadable
                                    xsltresult = new StringWriter();

                                    XSLTProcessor.runXSLTScript(XSLTScriptEnum.BIBISERVTEXT,
                                            new DOMSource(node), xsltresult);

                                    sb.append(xsltresult.toString());
                                    //add downloadable itself.
                                    if (appDir.exists() && appDir.isDirectory()) {
                                        //get file name
                                        NodeList fileNameList = processor.runxpath(node, "//cms:filename");
                                        if (fileNameList.getLength() > 0) {
                                            String fileName = fileNameList.item(0).getChildNodes().item(0).getNodeValue();
                                            //create the file itself
                                            File downloadable = new File(appDir, "resources/downloads/" + fileName);
                                            if (downloadable.exists()) {
                                                //add it to the list of downloadables.
                                                String ident = "/applications/" + id + "/resources/downloads/" + fileName;
                                                downloadables.add(new Tuple<>(ident, downloadable));
                                            }
                                        }
                                    }
                                }
                                break;
                            case "submission":
                                // submission = submission view + executable
                                xsltresult = new StringWriter();
                                //retrieve executables using xpath.
                                XSLTProcessor.runXSLTScript(XSLTScriptEnum.BIBISERCDESCRIPTIONS,
                                        new DOMSource(processor.runxpath(item, "//cms:executable").item(0)),
                                        xsltresult);
                                sb.append(xsltresult.toString());
                                break;


                        }

                        // extract text from downloadable
                        xsltresult = new StringWriter();

                        XSLTProcessor.runXSLTScript(XSLTScriptEnum.BIBISERVTEXT,
                                new DOMSource(element), xsltresult);

                        sb.append(xsltresult.toString());

                        addDocument("/" + id + "?viewType=" + viewtype,
                                new BufferedReader(new StringReader(sb.toString())),
                                session);
                    }

                    //call the super class for the downloadables.
                    String[] idents = new String[downloadables.size()];
                    InputStream[] streams = new InputStream[downloadables.size()];
                    int index = 0;
                    for (Tuple<String, File> downloadable : downloadables) {
                        idents[index] = downloadable.getFirst();
                        try {
                            streams[index] = new FileInputStream(downloadable.getSecond());
                            index++;
                        } catch (IOException ex) {
                            handleException(ex);
                        }
                    }
                    addDocuments(idents, streams, session);
                    break;
                }
                case "category": {
                    // category is something special ... each category is represented by its own url
                    // retrieve the categories with the respective XSLT script.

                    DOMResult result = new DOMResult();
                    XSLTProcessor.runXSLTScript(XSLTScriptEnum.BIBISERVCATEGORIES,
                            new DOMSource(item), result);
                    /*
                     * then iterate of list of categories.
                     * 
                     * the list is the first (and only) child element of the 
                     * result root node, hence this line of code: We get
                     * the roots first child and the child of this node are
                     * the categories that have been found.
                     */
                    NodeList nl = result.getNode().getChildNodes().item(0).getChildNodes();
                    for (int i = 0; i < nl.getLength(); ++i) {
                        Element elem = (Element) nl.item(i);
                        // get id  (= url)
                        String elemID = elem.getAttribute("id");
                        if (elemID.equals(id)) {
                            elemID = "/";
                        } else {
                            elemID = "/" + elemID;
                        }
                        addDocument(elemID,
                                new BufferedReader(new StringReader(elem.getTextContent())),
                                session);
                    }
                    break;
                }
                default:
                    //if no category is matched, just run xslt in item mode.
                    xsltresult = new StringWriter();
                    XSLTProcessor.runXSLTScript(XSLTScriptEnum.BIBISERVITEMS,
                            new DOMSource((Node) item.getDocumentElement()), xsltresult);
                    addDocument("/" + id,
                            new BufferedReader(new StringReader(xsltresult.toString())),
                            session);
                    break;

            }
        } catch (TransformerException | DOMException e) {
            handleException(e);
        } finally {
            //close append session.
            try {
                //end append session for suffix tree
                session.close();
            } catch (SuffixTreeException ex) {
                handleException(ex);
            }
        }
    }

    /**
     * Remove any BiBiServ2 item from database
     *
     * @param Document item
     */
    public void removeItem(Document item) {

        if (item.getDocumentElement().getLocalName().equals("category")) {
            // each (sub)category is represenet by its own url

            //create result object for xslt output
            DOMResult result = new DOMResult();
            //get source object for xslt transformation
            DOMSource domSource = new DOMSource((Node) item.getDocumentElement());
            try {
                //run xslt
                XSLTProcessor.runXSLTScript(XSLTScriptEnum.BIBISERVCATEGORIES, domSource, result);
            } catch (TransformerException ex) {
                handleException(ex);
            }

            // then iterate of list of categories

            NodeList nl = result.getNode().getChildNodes();

            for (int i = 0; i < nl.getLength(); ++i) {
                Element e = (Element) nl.item(i);
                // get id  (= url)           
                removeItem(e.getAttribute("id"));
            }

        } else {
            removeItem(item.getDocumentElement().getAttribute("id"));
        }
    }

    /**
     * Remove any BiBiServ2 item from database
     *
     * @param id - id of item
     */
    public void removeItem(String id) {
        removeAllDocument("/" + id);
    }
}
