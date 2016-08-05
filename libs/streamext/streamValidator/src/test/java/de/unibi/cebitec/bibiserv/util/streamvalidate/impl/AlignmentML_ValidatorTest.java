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
package de.unibi.cebitec.bibiserv.util.streamvalidate.impl;


import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CONTENT;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.STRICTNESS;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for all AlignmentML validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class AlignmentML_ValidatorTest extends Abstract_ValidatorTest{

    public String aml_aa;
    public String aml_rna;
    public String aml_dna;
    private AlignmentML_Validator validator;

    public AlignmentML_ValidatorTest() {
        try {


            aml_aa = readFromResource("/aml_aa.xml");
            aml_rna = readFromResource("/aml_rna.xml");
            aml_dna = readFromResource("/aml_dna.xml");


        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
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
    public void test_AminoAcid() throws FileNotFoundException {
        validator = new AlignmentML_Validator();
        validator.setStrictness(STRICTNESS.strict);
        validator.setContent(CONTENT.AA);
        ValidationResult vr = validator.validateThis(aml_aa);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(aml_rna);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(aml_dna);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/aml_aa2.xml"))));
        assertTrue(vr.getMessage(), vr.isValid());
        
        
        // extended
        vr = validator.validateThis(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/aml_aa_broken1.xml"))));
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/aml_aa_broken2.xml"))));
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/aml_aa_broken3.xml"))));
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/aml_aa_broken4.xml"))));
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/aml_aa_broken5.xml"))));
        assertTrue(vr.getMessage(), !vr.isValid());
      
    }
    
        @Test
    public void test_AminoAcid_Large() throws IOException {
        validator = new AlignmentML_Validator();
        validator.setStrictness(STRICTNESS.strict);
        validator.setContent(CONTENT.AA);

        ValidationResult  vr = validator.validateThis(new BufferedReader(new InputStreamReader(new GZIPInputStream(getClass().getResourceAsStream("/aml_aa_large.xml.gz")))));
        assertTrue(vr.getMessage(), vr.isValid());
        
    }

    @Test
    public void test_RNA() {
        validator = new AlignmentML_Validator();
        validator.setStrictness(STRICTNESS.strict);
        validator.setContent(CONTENT.RNA);
        ValidationResult vr = validator.validateThis(aml_aa);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(aml_rna);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(aml_dna);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void test_DNA() {
        validator = new AlignmentML_Validator();
        validator.setStrictness(STRICTNESS.strict);
        validator.setContent(CONTENT.DNA);
        ValidationResult vr = validator.validateThis(aml_aa);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(aml_rna);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(aml_dna);
        assertTrue(vr.getMessage(), vr.isValid());
    }

   
}
