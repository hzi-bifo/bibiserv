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

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author bpaassen
 * 
 * 
 * Searches for the WSPVOut_URL-Tag while parsing a WebService-Response of the
 * PseudoViewer-WebService.
 * 
 */
public class PseudoViewerParser extends DefaultHandler {
    
    private PseudoViewer pseudoViewer;
    private String currentValue;
    
    PseudoViewerParser(PseudoViewer pseudoViewer) {
        
        this.pseudoViewer = pseudoViewer;
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        
        int i = start;
        
        currentValue = new String(ch, start, length);
    }
    
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        
        if (localName.equals("WSPVOut_URL")) {
            pseudoViewer.setImageURL(currentValue);
        }
        
    }
}
