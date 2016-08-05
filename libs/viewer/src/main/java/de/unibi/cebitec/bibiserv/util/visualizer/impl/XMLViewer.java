/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010,2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010,2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.dev"
 *
 * Contributor(s):
 *
 */
package de.unibi.cebitec.bibiserv.util.visualizer.impl;

import de.unibi.cebitec.bibiserv.util.visualizer.AbstractVisualizer;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Thomas Gatter - tgatter(at)techfak.uni-bielefel.de
 * 
 * XML is a visualizer that takes in an xml object and displays it as highlighted xml. 
 * 
 */
public class XMLViewer extends AbstractVisualizer {

    /**
     * 
     * showThis takes all kinds of data and creates an html-block containing the
     * plain-text-representation of the data.
     * 
     * @param data data to be displayed
     * @return html-div-container-block containing the text-data
     * @throws Exception does not throw exceptions.
     */
    @Override
    public String showThis(Object data) throws Exception {

        StringBuilder html = new StringBuilder();
        String xml;
        if (data instanceof String) {
            xml = data.toString();
        } else {
            // normal usecase, the object if correct type and needs to converter unmarshalled to string
            JAXBContext jaxbc = JAXBContext.newInstance(data.getClass());
            Marshaller m = jaxbc.createMarshaller();
            m.setProperty("jaxb.formatted.output", true);
            StringWriter w = new StringWriter();
            m.marshal(data, w);
            xml = w.toString();
        }

        // ad all scripts and styles for highlighting
        html.append("<script type=\"text/javascript\" src=\"/resources/viewer/XmlViewer/shCore.js\"></script>").
                append("<script type=\"text/javascript\" src=\"/resources/viewer/XmlViewer/shBrushXml.js\"></script>").
                append("<link href=\"/resources/viewer/XmlViewer/shCore.css\" rel=\"stylesheet\" type=\"text/css\" />").
                append("<link href=\"/resources/viewer/XmlViewer/shThemeDefault.css\" rel=\"stylesheet\" type=\"text/css\" />");

        // the content
        html.append("<script type=\"syntaxhighlighter\" class=\"brush: xml\"><![CDATA[").append(xml).
                append("]]></script>");
        
        // start up code
        html.append("<script type=\"text/javascript\">SyntaxHighlighter.all()</script>");
        
        return html.toString();
    }
}
