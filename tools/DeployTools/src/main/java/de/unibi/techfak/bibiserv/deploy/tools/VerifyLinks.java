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
 * "Portions Copyrighted 2010-2012 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.deploy.tools;

import de.unibi.techfak.bibiserv.xml.NamespaceContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * VerifyImage is small tool class that compares all href/src links from the
 * tool description with
 *
 *
 * This class extends the ant Task class and overwrites its execute class.
 *
 *
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 */
public class VerifyLinks extends AbstractVerifyTask {

    List<Node> node_list = new ArrayList<Node>();
    List<String> resource_dir_list = new ArrayList<String>();
    List<Ignore> ignore_list = new ArrayList<Ignore>();
    private File resourcedir;
    private File out;

    /**
     * Default Constructor
     */
    public VerifyLinks() {
    }

    public VerifyLinks(File runnableitemfile, File resourcedir) throws Exception {
        setRunnableitemfile(runnableitemfile);
        setResourcedir(resourcedir);

    }

    @Override
    public void execute() throws BuildException {

        super.execute();
        
        
        // add ignore elements from nested ignore elements
        if (ignore_list != null && !ignore_list.isEmpty()) {
            System.out.println("Ignore list : ");
            for (Ignore i : ignore_list){
                System.out.print( i + " ");
            }
            System.out.println("");
        }

        // check if image dir is set , a directory and readable
        if (resourcedir == null || !resourcedir.isDirectory() || !resourcedir.canRead()) {
            throw new BuildException("ImageDir '" + resourcedir + "' must be set, a directory and readable!");
        }
        // get all file entries 
        VerifyDownload.getDirEntries(resourcedir, resource_dir_list);

        Document runnableitem;
        try {
            runnableitem = getRunnableitemAsDocument();
        } catch (Exception e) {
            throw new BuildException("An Exception occurred while converting a JAXB representation to W3C DOM document." + e.getMessage());
        }

        
        
        // get all image entries
        try {
            node_list = VerifyLinks.getImagesEntries(runnableitem, ignore_list);
            System.out.println("Found images (XML): " + list2list(node_list));
        } catch (XPathExpressionException e) {
            throw new BuildException(e);
        }

        List<String> result_list = new ArrayList<String>();
        List<String> image_xml_list = list2list(node_list);
        if (!compareEntries(image_xml_list, resource_dir_list, result_list)) {
            image_xml_list.removeAll(result_list);
            throw new BuildException("Entr(y|ies) (" + VerifyDownload.printList(image_xml_list) + ") missed in resource dir (" + resourcedir + ")!");
        }

        if (out == null) {
            System.out.println("Attribute 'out' not set, don't modify " + getRunnableitemfile() + " !");
        } else {
            addPrefix(node_list, "applications/" + ((Attr) (runnableitem.getDocumentElement().getAttributes().getNamedItem("id"))).getValue() + "/resources/");
            try {
                NodetoStream(runnableitem, new FileOutputStream(out));
            } catch (FileNotFoundException e) {
                throw new BuildException(e);
            }
            System.out.println("Update runnableitem <img @src> entries ...");
        }
    }



    public final void setResourcedir(String resourcedir){
        this.resourcedir = new File(resourcedir);
    }
    
    
    public final void setResourcedir(File resourcedir) {
        this.resourcedir =resourcedir;
    }
    
    public void setIgnorepattern(String pattern) {
        ignore_list.add(new Ignore(pattern));
    }

 

 

    public void setOut(File out) {
        this.out = out;
    }

    public void setOut(String out) {
        this.out = new File(out);
    }

    public List<String> getImagesDirList() {
        return resource_dir_list;
    }

    /**
     * Return a list of nodes (DOM) describing an image source (AttributeNode).
     *
     * @param runnableitem - Tooldescriptiion to be searched.
     * @return Return a list of nodes (DOM)
     * @throws XPathExpressionException
     */
    public static List<Node> getImagesEntries(Document runnableitem) throws XPathExpressionException {
        return getImagesEntries(runnableitem, null);
    }

    /**
     * Return a list of nodes (DOM) describing an image source (AttributeNodes)
     * and not matching a pattern from the ingnorelist.
     *
     * @param runnableitem - Tooldescriptiion to be searched.
     * @param ignorelist - List of regexp pattern
     * @return Return a list of nodes (DOM)
     * @throws XPathExpressionException
     */
    public static List<Node> getImagesEntries(Document runnableitem, List<Ignore> ignorelist) throws XPathExpressionException {


        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        NamespaceContext nsc = new NamespaceContext();

        /**
         * Set Namespaces
         */
        nsc.addNamespace("http://www.w3.org/XML/1998/namespace", "xml");
        nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms.microhtml", "microhtml");
        nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms.minihtml", "minihtml");
        nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms", "cms");


        xpath.setNamespaceContext(nsc);
        XPathExpression expr_src = xpath.compile("//*/@src"); //images
        XPathExpression expr_href = xpath.compile("//*/@href"); // all other href


        //minihtml
        List<Node> nodelist = nodelist2list((NodeList) expr_src.evaluate(runnableitem, XPathConstants.NODESET));
        //microhtml
        nodelist.addAll(nodelist2list((NodeList) expr_href.evaluate(runnableitem, XPathConstants.NODESET)));

        // if ignorelist is empty then we are finished and return the nodelist
        if ((ignorelist == null) || ignorelist.isEmpty()) {
            return nodelist;
        }

        // otherwise if have to check each node against the pattern from the ignorelist

        List<Node> resultlist = new ArrayList<Node>();

        for (Node n : nodelist) {
            boolean t = true;
            for (Ignore ignore : ignorelist) {

                if (n.getTextContent().matches(ignore.getRegexp())) {
                    t = false;
                    break;
                }


            }
            if (t) {
                resultlist.add(n);
            }
        }

        return resultlist;
    }

    /**
     * Take care that all list elements (Strings) from entries_from_xml are
     * contained in entries_from_dir, but not vice versa. All found elements in
     * stored in the resultlist.
     *
     *
     * @param entries_from_xml
     * @param entries_from_dir
     * @param resultlist - EMPTY! list
     * @return
     */
    public static boolean compareEntries(List<String> entries_from_xml, List<String> entries_from_dir, List<String> resultlist) {
        for (String entry_xml : entries_from_xml) {
            if (inList(entries_from_dir, entry_xml)) {
                resultlist.add(entry_xml);
            }
        }
        return entries_from_xml.containsAll(resultlist) && entries_from_xml.size() == resultlist.size();
    }

    private static boolean inList(List<String> l, String s) {
        for (String e : l) {
            if (s.equals(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a prefix for each node of the nodelist.
     *
     * @param nl
     * @param prefix
     */
    public static void addPrefix(List<Node> nl, String prefix) {
        for (Node n : nl) {
            n.setTextContent(prefix + n.getTextContent());
        }
    }

    public static void NodetoStream(Node node, OutputStream out) {
        try {
            Source source = new DOMSource(node);

            Result result = new StreamResult(out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static String NodeToString(Node node) {
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * private helper method, convert a unmodifyable nodelist (DOM) to a List of
     * Nodes. \
     *
     * @param nl - nodelist (DOM)
     * @return Return a list of nodes
     */
    private static List<Node> nodelist2list(NodeList nl) {
        List<Node> tmp = new ArrayList<Node>();
        for (int i = 0; i < nl.getLength(); ++i) {
            tmp.add(nl.item(i));
        }
        return tmp;
    }

    /**
     * private Helper method . Converts a list of Nodes to a list of Strings
     * (Node textcontent)
     *
     * @param nl
     * @return
     */
    public static List<String> list2list(List<Node> nl) {
        List<String> r = new ArrayList<String>();
        for (Node n : nl) {
            r.add(n.getTextContent());
        }
        return r;
    }
    
    
    /**
     * Own static inner class for nested ignore elements
     */
    public static class Ignore {
        
        private String regexp;

        public Ignore(){
            
        }
        
        public Ignore(String regexp){
            this.regexp = regexp;
        }
        
        public void setRegexp(String rexexp) {
            this.regexp = rexexp;
        }

        public String getRegexp() {
            return regexp;
        }

        @Override
        public String toString() {
            return regexp;
        }
        
        
        
    }
    
    
    
    public Ignore createIgnore(){
        Ignore ignore = new Ignore();
        ignore_list.add(ignore);
        return ignore;
        
    }
}
