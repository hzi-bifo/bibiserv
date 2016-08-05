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

/**
 * Contains possible xslt scripts than can be used by the XSLT Processor.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public enum XSLTScriptEnum {

    BIBISERVCATEGORIES("extractCategories.xsl"),
    BIBISERCDESCRIPTIONS("extractDescription.xsl"),
    BIBISERVITEMS("extractItem.xsl"),
    BIBISERVTEXT("extractText.xsl");
    /**
     * The actual script filename.
     */
    private final String fileName;

    private XSLTScriptEnum(String fileName) {
        this.fileName = fileName;
    }

    protected String getFileName() {
        return fileName;
    }
}
