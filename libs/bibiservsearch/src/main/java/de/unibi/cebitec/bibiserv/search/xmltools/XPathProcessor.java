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
 * Contributor(s): Benjamin Paassen
 *
 */ 
package de.unibi.cebitec.bibiserv.search.xmltools;

import de.unibi.cebitec.bibiserv.search.BiBiServSearch;
import de.unibi.techfak.bibiserv.xml.NamespaceContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is an API for using XPath expressions on DOM trees with BiBiServ
 * namespaces.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class XPathProcessor {

    private XPath xpathInstance;

    /**
     * Generates a new XPathProcessor.
     */
    public XPathProcessor() {
        //generate xpath instance.
        xpathInstance = XPathFactory.newInstance().newXPath();
        NamespaceContext nsc = new NamespaceContext();

        //set bibiserv namespaces.
        nsc.addNamespace("http://www.w3.org/XML/1998/namespace", "xml");
        nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms.microhtml", "microhtml");
        nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms.minihtml", "minihtml");
        nsc.addNamespace("bibiserv:de.unibi.techfak.bibiserv.cms", "cms");

        xpathInstance.setNamespaceContext(nsc);
    }

    /**
     * evaluates the xpath expression for the given path and tries to return
     * the result as node list.
     *
     * @param doc xml node to run the expression on.
     * @param path path for the xpath expression.
     * @return Nodelist with results for the given expression.
     */
    public NodeList runxpath(Node doc, String path) {
        try {

            // XPath Query for showing all nodes value
            XPathExpression expr = xpathInstance.compile(path);

            //try to evaluate the compiled expression.
            return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(BiBiServSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        // if it did not work, return an empty node list.
        return new NodeList() {

            @Override
            public Node item(int i) {
                return null;
            }

            @Override
            public int getLength() {
                return 0;
            }
        };
    }
}
