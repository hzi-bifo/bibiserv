/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.validate.impl;

import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
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
 * Test for connect validator.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class Connect_ValidatorTest extends AbstractTest{

    public Connect_ValidatorTest() {
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
    public void testInvalidData() throws IOException{
        // test null content.
        Connect_Validator validator = new Connect_Validator();
        String testData = null;
        ValidationResult result = validator.validateThis(testData);
        assertFalse(result.isValid());
        // test empty content.
        testData = "";
        result = validator.validateThis(testData);
        assertFalse(result.isValid());
        // test whitespace content.
        testData = "    ";
        result = validator.validateThis(testData);
        assertFalse(result.isValid());
        // test content without correct energy information.
        testData = "    \n73 =     -17.50    S.cerevisiae_tRNA-PHE\n";
        result = validator.validateThis(testData);
        assertFalse(result.isValid());
        // test content with invalid base partner.
        testData = readFromResource("/ct_structure_invalid_base_partner.ct");
        result = validator.validateThis(testData);
        assertFalse(result.isValid());
        // test content with invalid index.
        testData = readFromResource("/ct_structure_invalid_index.ct");
        result = validator.validateThis(testData);
        assertFalse(result.isValid());
        // test content with invalid base.
        testData = readFromResource("/ct_structure_invalid_base.ct");
        result = validator.validateThis(testData);
        assertFalse(result.isValid());
        // test content with missing base partner.
        testData = readFromResource("/ct_structure_invalid_base_partner2.ct");
        result = validator.validateThis(testData);
        assertFalse(result.isValid());
    }

    @Test
    public void testValidData() throws IOException{
        //test single structure
        String validContent = readFromResource("/ct_valid_structure.ct");
        Connect_Validator validator = new Connect_Validator();
        ValidationResult result = validator.validateThis(validContent);
        assertTrue(result.getMessage(),result.isValid());
        //test multiple structures
        validContent = readFromResource("/ct_valid_multiple_structures.ct");
        result = validator.validateThis(validContent);
        assertTrue(result.getMessage(),result.isValid());
    }


}
