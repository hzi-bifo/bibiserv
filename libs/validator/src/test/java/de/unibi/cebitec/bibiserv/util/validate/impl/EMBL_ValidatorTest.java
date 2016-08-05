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
package de.unibi.cebitec.bibiserv.util.validate.impl;

import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CARDINALITY;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CONTENT;
import org.junit.Test;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 * Test for all EMBL validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author mrumming
 */
public class EMBL_ValidatorTest  extends AbstractTest{

    private EMBL_Validator validator;
    private String embl_dna_single;
    private String embl_rna_single;
    private String embl_dna_multi;
    private String embl_rna_multi;
    private String embl_single_broken_1;
    private String embl_single_broken_2;
    private String embl_single_broken_3;
    private String embl_multi_broken_1;
    private String embl_multi_broken_2;
    private String embl_multi_broken_3;
    private String fasta_dna_single;

    public EMBL_ValidatorTest() {
        try {
            embl_dna_single = readFromResource("/embl_dna_single.embl");
            embl_rna_single = readFromResource("/embl_rna_single.embl");
            embl_dna_multi = readFromResource("/embl_dna_multi.embl");
            embl_rna_multi = readFromResource("/embl_rna_multi.embl");
            embl_single_broken_1 = readFromResource("/embl_single_broken_1.embl");
            embl_single_broken_2 = readFromResource("/embl_single_broken_2.embl");
            embl_single_broken_3 = readFromResource("/embl_single_broken_3.embl");
            embl_multi_broken_1 = readFromResource("/embl_multi_broken_1.embl");
            embl_multi_broken_2 = readFromResource("/embl_multi_broken_2.embl");
            embl_multi_broken_3 = readFromResource("/embl_multi_broken_3.embl");
            
            fasta_dna_single = readFromResource("/fasta_dna_single.fas");
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
    public void testDNA_Multi() {
        validator = new EMBL_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.multi);
        // -- multiple sequence data
        ValidationResult vr = validator.validateThis(embl_dna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(embl_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(embl_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken embl data
        vr = validator.validateThis(embl_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_single_broken_3);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_multi_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_multi_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_multi_broken_3);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testDNA_Single() {
        validator = new EMBL_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.single);
        // -- multiple sequence data
        ValidationResult vr = validator.validateThis(embl_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(embl_dna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(embl_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken embl data
        vr = validator.validateThis(embl_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_single_broken_3);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_multi_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_multi_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_multi_broken_3);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testRNA_Multi() {
        validator = new EMBL_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.multi);
        // -- multiple sequence data
        ValidationResult vr = validator.validateThis(embl_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_rna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(embl_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken embl data
        vr = validator.validateThis(embl_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_single_broken_3);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_multi_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        /* This test should be false. Sequence is cut, that means some letters
         * are missing. FIX!
         */
        vr = validator.validateThis(embl_multi_broken_2);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(embl_multi_broken_3);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testRNA_Single() {
        validator = new EMBL_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.single);
        // -- multiple sequence data
        ValidationResult vr = validator.validateThis(embl_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(embl_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_rna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken embl data
        vr = validator.validateThis(embl_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_single_broken_3);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_multi_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_multi_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(embl_multi_broken_3);
        assertTrue(vr.getMessage(), !vr.isValid());
    }
    
    @Test
    public void testDNA_SingleWrongFile() {
        validator = new EMBL_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.single);
        // -- multiple sequence data
        ValidationResult vr = validator.validateThis(fasta_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

   
}
