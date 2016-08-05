/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.convert.impl;

import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
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
 * Test for connect to vienna conversion.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class CT2DOTBRACKETTest {

    public CT2DOTBRACKETTest() {
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

    @Test
    public void testConversion() throws ConversionException{
        CT2DOTBRACKET converter = new CT2DOTBRACKET();
        //test single structure.
        String testCTData = readTestFile("src/test/resources/ct_valid_structure.ct");
        String testDotBracketData = (String) converter.convert(testCTData);
        assertTrue(testDotBracketData.contains("33 dG =     -1.95 MB-6FAM"));
        assertTrue(testDotBracketData.contains("CCUCUCCGUGUCUUGUACUUCCCGUCAGAGAGG"));
        assertTrue(testDotBracketData.contains("((((((.....................))))))"));
        //test multiple structures
        testCTData = readTestFile("src/test/resources/ct_valid_multiple_structures.ct");
        testDotBracketData = (String) converter.convert(testCTData);
        assertTrue(testDotBracketData.contains("33 dG =     -1.95 MB-6FAM"));
        assertTrue(testDotBracketData.contains("CCUCUCCGUGUCUUGUACUUCCCGUCAGAGAGG"));
        assertTrue(testDotBracketData.contains("((((((...(......)..........))))))"));
        assertTrue(testDotBracketData.contains("((((((...([{..(.)...)]}....))))))"));
        
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
