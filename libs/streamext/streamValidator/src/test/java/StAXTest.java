/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.SequenceValidator;
import java.io.FileNotFoundException;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author gatter
 */
public class StAXTest {

    public StAXTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void staxTest() throws XMLStreamException, FileNotFoundException {
        XMLInputFactory xmlFactory = null;

        try {
            XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
            XMLValidationSchema xsd = sf.createSchema(new URL("https://bibiserv.cebitec.uni-bielefeld.de/xsd/net/sourceforge/hobit/20060602/alignmentML.xsd"));

            XMLInputFactory2 ifact = (XMLInputFactory2) XMLInputFactory.newInstance();
            XMLStreamReader2 sr = (XMLStreamReader2) ifact.createXMLStreamReader(getClass().getResourceAsStream("/aml_aa.xml"));

            sr.validateAgainst(xsd);

            while (sr.hasNext()) {

                int event = sr.next();

                if(event == XMLStreamConstants.START_ELEMENT && sr.getAttributeCount()>0){
                    System.out.println("Attr.: "+sr.getAttributeLocalName(0));
                    System.out.println(sr.getAttributeValue(0));
                }
                
                if (sr.hasName()) {
                    System.out.println(event + ":Name " + sr.getName());
                    System.out.println("Local: "+sr.getLocalName());
                }
                if (sr.hasText()) {
                    String st = sr.getText();
                     System.out.println(SequenceValidator.validate(PatternType.BT_gappedAmbiguousAminoAcidOneLetterSequence, st));
                    System.out.println(event + ":Text " + sr.getText());
                }
                System.out.println(event+"---");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
//
//    @Test
//    public void buildLargeFile() throws IOException {
//        File newF = new File("aml_aa_large2.xml");
//
//        BufferedWriter w = new BufferedWriter(new FileWriter(newF));
//
//        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<alignmentML xmlns=\"http://hobit.sourceforge.net/xsds/20060602/alignmentML\"\n"
//                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
//                + "    xsi:schemaLocation=\"http://hobit.sourceforge.net/xsds/20060602/alignmentML http://bibiserv/xsd/net/sourceforge/hobit/20060602/alignmentML.xsd\">\n"
//                + "    <alignment>\n"
//                + "        <sequence seqID=\"sample1\">\n"
//                + "            <alignedAminoAcidSequence>");
//        for (int i = 1; i < 3000000; i++) {
//            w.write("VILSTDSQYVRQGITQWIHNWKKRGWKTADKKPVKNVD-LWQR");
//        }
//
//        w.write("</alignedAminoAcidSequence>\n"
//                + "        </sequence>"
//                + "        <sequence seqID=\"sample2\">\n"
//                + "            <alignedAminoAcidSequence>");
//        for (int i = 1; i < 3000000; i++) {
//            w.write("VILSTDSQYVRQGITQWIHNWKKRGWKTADKKPVKNVD-LWQR");
//        }
//        w.write("</alignedAminoAcidSequence>"
//                + "        </sequence>"
//                + "    </alignment>"
//                + "</alignmentML>");
//        w.close();
//    }
}
