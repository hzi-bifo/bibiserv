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
import de.unibi.techfak.bibiserv.BiBiTools;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Sven Hartmeier
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 * 
 */
public class RAEDA extends AbstractVisualizer {

    /**
     * 
     * showThis takes alignment-data and creates an html-block referring to the
     * RAEDA-applet.
     * Please note! It is imperative that the RAEDA.jar is avaliable in the same
     * folder as the final html-page that implements the given div-container.
     * 
     * @param data alignment-data to be displayed
     * @return html-div-container-block calling the RAEDA-application.
     * @throws Exception either simple FileNotFoundException (runnables) or
     * BiBiTool-Exception. Normally, no exception is thrown because no user-
     * interaction is happening.
     * Invalid data only throws exceptions later on, when the java-applet-page
     * of RAEDA is created.
     */
    
    @Override
    public String showThis(Object data) throws Exception {

        
        StringBuilder xhtml = new StringBuilder();

        InputStream runnableItem = this.getClass().getResourceAsStream("runnables/raeda.bs2");
        BiBiTools tool = initialize(runnableItem);

        // test if already a string
        if(data instanceof String) {
            tool.writeSpoolFile("input.xml", data.toString());
        } else {

            // normal usecase, the object if correct type and needs to converter unmarshalled to string
            JAXBContext jaxbc = JAXBContext.newInstance("net.sourceforge.hobit.xsds._20060602.alignmentml");
            Marshaller m = jaxbc.createMarshaller();
            m.setProperty("jaxb.formatted.output", true);
            StringWriter w = new StringWriter();
            m.marshal(data,w);
            tool.writeSpoolFile("input.xml", w.toString());
        }

        xhtml.append("<div>\n");
        xhtml.append("This visualizer is a java webstart application and should start automatically.");

        xhtml.append("<script type=\"text/javascript\">window.location=\"/jnlp?viewerid=RAEDA&resultid=")
                .append(tool.getStatus().getId()).append("&resultname=input.xml\"").append("</script>");

        xhtml.append("</div>\n");
        return xhtml.toString();
    }
}
