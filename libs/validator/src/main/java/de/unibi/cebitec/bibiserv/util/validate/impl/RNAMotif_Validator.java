/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.validate.impl;

import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.AbstractXMLValidator;
import de.unibi.cebitec.bibiserv.util.validate.JAXBManager;

/**
 * General RNAMotif_Validator. Just a simple XML validation.
 * 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de 
 */
public class RNAMotif_Validator extends AbstractXMLValidator {

     @Override
    public JAXBManager getXMLInstance() {
         
      return new JAXBManager("de.unibi.techfak.bibiserv.rnaeditor");
        
    }

    @Override
    public ValidationResult validateThis(Object data) {
        
        ValidationResult vr = super.validateThis(data);        
        return vr;
    }

   
}
