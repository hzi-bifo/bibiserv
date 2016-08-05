package de.unibi.cebitec.bibiserv.sequenceparser;

import de.unibi.cebitec.bibiserv.sequenceparser.parser.impl.ConnectParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class tests .ct (connect) file parsing.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class CTParserTest {

    public CTParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Tests parsing with validation of a multiple structure .ct file.
     */
    @Test
    public void testCTParsing() {
        String testcontent = readTestFile("src/test/testdata/ct_conversionoutput.ct");
        ConnectParser parser = new ConnectParser(testcontent, true);
        parser.parseData();
        assertTrue(parser.getValidationMessage(),parser.isValid());
    }

    private String readTestFile(String path) {
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fr = new FileReader(path);
            BufferedReader r = new BufferedReader(fr);
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
        return sb.toString();
    }
}
