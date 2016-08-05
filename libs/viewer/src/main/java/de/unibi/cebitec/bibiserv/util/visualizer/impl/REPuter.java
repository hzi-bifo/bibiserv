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
 * "Portions Copyrighted 2010-2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.dev"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.visualizer.impl;

import de.unibi.cebitec.bibiserv.util.visualizer.AbstractVisualizer;
import de.unibi.techfak.bibiserv.BiBiTools;
import java.io.InputStream;

/**
 *
 * @author Jan Krueger
 * 
 * Implements a viewer  for the REPuter Web Viusalizer.
 * 
 */

public class REPuter extends AbstractVisualizer {

    /**
     * 
     * Please note! It is imperative that the RNAMovies2.05.jar is available in
     * the same folder as the final html-page that implements the given
     * div-container.
     * 
     * @param data REPuter specific data
     * @return html-div-container containing a call to the REPuter_WebView-Application
     * 
     * @throws Exception throws different Exceptions given by the WebService
     */
    
    @Override
    public String showThis(Object data) throws Exception {

       StringBuilder xhtml = new StringBuilder();

        InputStream runnableItem = this.getClass().getResourceAsStream("runnables/REPuter.bs2");
        BiBiTools tool = initialize(runnableItem);

        tool.writeSpoolFile("input.rep", data.toString());

        xhtml.append("<div>\n");
        xhtml.append("This visualizer is a java webstart application and should start automatically.");

        xhtml.append("<script type=\"text/javascript\">window.location=\"/jnlp?viewerid=REPuter&resultid=")
                .append(tool.getStatus().getId()).append("&resultname=input.rep\"").append("</script>");

        xhtml.append("</div>\n");
        return xhtml.toString();
    }
}
