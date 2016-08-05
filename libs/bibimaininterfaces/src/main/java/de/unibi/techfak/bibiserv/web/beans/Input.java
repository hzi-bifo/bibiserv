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
package de.unibi.techfak.bibiserv.web.beans;

import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Class Input is an container for input data used by the bibiserv webinterface
 * tool bean classes to store information about submitted input.
 *
 *
 * @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class Input {
    
    private Object input = null;
    private String source = null;
    private String message = null;
    private List<OntoRepresentation> representations = null;
    private OntoRepresentation chosen;
    private boolean skipValidation = false;
    

    public void setInput(Object input) {
        this.input = input;
    }

    /**
     * Returns the set input converted to implementationtype of chosen if possible.
     * @return Set Input Object possibly converted to XML type.
     */
    public Object getInput() {
        if(getChosen()==null) {
            return "";
        }
        if(input instanceof String && getChosen().getType() == OntoRepresentation.representationType.XML) {
            try {
                return string2Jaxb((String) input, Class.forName(getChosen().getImplementationType()));
            } catch (JAXBException | ClassNotFoundException ex) {
               return "";
            }
        }
        
        return input;
    }
    
        /**
     * Converts a string to a jaxb element using the given jaxbclass.
     *
     * @param content - String to be converted to jaxb element
     * @param jaxbclass - class of the jaxbrootelement
     * @return Return the jaxb element (as object)
     *
     * @throws throws an JAXBException in the case of an error.
     */
    private static Object string2Jaxb(String content, Class jaxbclass) throws JAXBException {
        JAXBContext jaxbc = JAXBContext.newInstance(jaxbclass);
        Unmarshaller um = jaxbc.createUnmarshaller();
        
        /* hack to fix strange unmarshall handling, sometimes the object itsself is returned 
         * and othertimes the object is encapsuled within an JAXBElement. In think this
         * depends how the JAXB classes are created ... must check this. JK,4.7.13 */
        Object o = um.unmarshal(new StringReader(content));
        if (o instanceof JAXBElement ) {  
            JAXBElement jaxbe = (JAXBElement) um.unmarshal(new StringReader(content));
            return jaxbe.getValue();
        } else {
            return o;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<OntoRepresentation> getRepresentations() {
        return representations;
    }

    public void setRepresentations(List<OntoRepresentation> representations) {
        this.representations = representations;
    }

    public OntoRepresentation getChosen() {
        return chosen;
    }

    public void setChosen(OntoRepresentation chosen) {
        this.chosen = chosen;
    }

    /**
     * Returns if validation and conversion for this input should be disabled.
     * @return 
     */
    public boolean isSkipValidation() {
        return skipValidation;
    }

    public void setSkipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
    }
    
}
