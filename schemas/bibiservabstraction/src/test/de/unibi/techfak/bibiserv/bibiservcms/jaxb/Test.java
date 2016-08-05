/*
 * created 13 March 2008
 */
package test.de.unibi.techfak.bibiserv.bibiservcms.jaxb;

import de.unibi.techfak.bibiserv.cms.Tcategory;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;


/**
 * This is a simple test script, to show easy a xml file can be processed using 
 * tha JAXB API.
 * 
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 */
public class Test {

    public static void main(String[] args) {
        try {
            /* read XML File into the JAXB objects using the JAXB Marshaller */
            JAXBContext jaxbc = JAXBContext.newInstance("de.unibi.techfak.bibiserv.cms");
            Unmarshaller um = jaxbc.createUnmarshaller();
            JAXBElement<Tcategory> jaxbe = (JAXBElement)um.unmarshal(new File(args[0]));
            Tcategory category = jaxbe.getValue();
            /* now we can work on JAXB objects */
            System.out.println ("The file "+args[0]+"contains a category \""+category.getName().get(0).getValue()+"\" as root element!");
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
