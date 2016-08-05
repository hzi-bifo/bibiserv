/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.validate;

import de.unibi.cebitec.bibiserv.util.validate.exception.JAXBManagerException;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class JAXBManager {
    
    private String packageName;
    private Object rootElement;
    
    public JAXBManager(String packageName)
    {
        this.packageName = packageName;
    }
    
    /**
     *  Converts a String (maybe read from a file) to a DOM Object
     *  with set className as Class and validates it.
     * 
     * Using Schema for further validation is not needed/wanted,
     * since content is validated separately.
     * 
     * @param documentasstring XML to convert to DOM
     * @throws JAXBManagerException if String is not proper XML
     */
    public void setDom(String documentasstring) throws JAXBManagerException {
        try {
          
            JAXBContext jc = JAXBContext.newInstance(packageName);
            Unmarshaller u = jc.createUnmarshaller();
            Object o = u.unmarshal(new StringReader(documentasstring));
            
              if (o instanceof JAXBElement) {
                rootElement = ((JAXBElement)o).getValue();
            } else {
                rootElement = o;
            }

        }  catch (JAXBException ex) {
            throw new JAXBManagerException("JAXBException, exception was: " + ex.getLocalizedMessage(),ex);
        } 
    }
    
    /**
     * Validates Dom-Tree in rootElement by trying to marshal it as SAX.
     * 
     * Using Schema for further validation is not needed/wanted,
     * since content is validated separately.
     * 
     * @throws JAXBManagerException Dom-Tree in rootElement is not valid.
     */
    public void validateDom() throws JAXBManagerException {
       try
       {
        
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(rootElement, new DefaultHandler());

       } catch (JAXBException ex) {
            throw new JAXBManagerException("JAXBException, exception was: " + ex.getLocalizedMessage(),ex);
       }
        
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Object getJaxbRootElement() {
        return rootElement;
    }

    public void setJaxbRootElement(Object rootElement) {
        this.rootElement = rootElement;
    }
    
    
    
    
}
