/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.streamvalidate;

import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * A class that already contains the generation of a buffered reader from
 * strings.
 *
 * @author Thomas Gatter - tgatter@cebitec.uni-bielefeld.de
 */
public abstract class AbstractXMLStreamValidator extends AbstractStreamValidator {

    @Override
    public ValidationResult validateThis(Object data) {
        stringWriter = new StringWriter();
        if (data instanceof String) {
            return validateThis(new BufferedReader(new StringReader((String) data)), new BufferedWriter(stringWriter));
        } else if (data instanceof BufferedReader) {
            return validateThis((BufferedReader) data, new BufferedWriter(stringWriter));
        } else {
            try {
                JAXBContext jaxbc = JAXBContext.newInstance(Class.forName(getXMLImplementation()));
                Marshaller m = jaxbc.createMarshaller();
                m.setProperty("jaxb.formatted.output", true);
                StringWriter w = new StringWriter();
                m.marshal(data, w);
                return validateThis(new BufferedReader(new StringReader(w.toString())), new BufferedWriter(stringWriter));
            } catch (JAXBException | ClassNotFoundException ex) {
                return new ValidationResult(false, "Unsupported input type.");
            }
        }
    }

    abstract public String getXMLImplementation();
}
