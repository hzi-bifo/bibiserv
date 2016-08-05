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

/**
 *
 * @author bpaassen
 * 
 * PlainText is a very basic visualizer which just takes the given data,
 * converts it into a String and returns a html-block with a div-container
 * containing this String.
 * 
 */
public class PlainText extends AbstractVisualizer {

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

        String html = "<pre>\n";
        html += data.toString();
        html += "</pre>\n";

        return html;
    }
}
