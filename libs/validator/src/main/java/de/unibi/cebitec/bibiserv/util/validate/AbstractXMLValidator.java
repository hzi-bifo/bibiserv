/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */
package de.unibi.cebitec.bibiserv.util.validate;

import de.unibi.cebitec.bibiserv.util.validate.exception.JAXBManagerException;


/**
 * Abstract Class AbstractXMLValidator is the abstract base class for all XXXML 
 * formats for Validation and Convertation.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * @author Richard - rmadsack(at)techfak.uni-bielefeld.de
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractXMLValidator implements Validator {

    private JAXBManager xml;
    
    private final String className = getXMLInstance().getClass().getName();

    /**
     * Abstract method that must be implemented by non abstract subclasses.
     *
     * @return Returns an non abstract biodom class.
     */
    public abstract JAXBManager getXMLInstance();

    protected JAXBManager getXMl() {
        return xml;
    }

    @Override
    public ValidationResult validateThis(Object data) {

        if (data instanceof String) {
            try {
                xml = getXMLInstance();
                xml.setDom((String) data);
            } catch (JAXBManagerException e) {
                return new ValidationResult(false, e.getMessage());
            }

        } else {
            try {
            
                xml = getXMLInstance();
                xml.setJaxbRootElement(data);
                xml.validateDom();

            } catch (JAXBManagerException ex) {
                return new ValidationResult(false, "Input could not be converted due to an BioDOMException! Exception message was: " + ex.getLocalizedMessage());
            } catch (ClassCastException ex) {
                return new ValidationResult(false, "Inputtype '" + data.getClass().getName() + "' could not be cast to correct type '" + className + "'");
            }
        }

        //check if the data is a non-null object
        if (xml == null) {
            return new ValidationResult(false, "Input was 'null'");
        } 
        
         return new ValidationResult(true, "Data correctly validated as '" + className);
    }
}
