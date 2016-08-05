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


import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CARDINALITY;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CONTENT;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.STRICTNESS;
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
 * Test for all NBRF validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author mrumming
 */
public class NBRF_ValidatorTest extends Abstract_ValidatorTest{

    private NBRF_Validator validator;
    private String nbrf_aa_alignment;
    private String nbrf_aa_amb_alignment;
    private String nbrf_dna_alignment;
    private String nbrf_rna_alignment;
    private String nbrf_aa_single;
    private String nbrf_dna_single;
    private String nbrf_rna_single;
    private String nbrf_aa_multi;
    private String nbrf_dna_multi;
    private String nbrf_rna_multi;
    private String nbrf_single_broken;
    private String nbrf_multi_broken;
    private String fasta_aa_alignment;

    public NBRF_ValidatorTest() {
        try {
            nbrf_aa_alignment = readFromResource("/nbrf_aa_alignment.nbrf");
            nbrf_aa_amb_alignment = readFromResource("/nbrf_aa_amb_alignment.nbrf");
            nbrf_dna_alignment = readFromResource("/nbrf_dna_alignment.nbrf");
            nbrf_rna_alignment = readFromResource("/nbrf_rna_alignment.nbrf");
            nbrf_aa_single = readFromResource("/nbrf_aa_single.nbrf");
            nbrf_dna_single = readFromResource("/nbrf_dna_single.nbrf");
            nbrf_rna_single = readFromResource("/nbrf_rna_single.nbrf");
            nbrf_aa_multi = readFromResource("/nbrf_aa_multi.nbrf");
            nbrf_dna_multi = readFromResource("/nbrf_dna_multi.nbrf");
            nbrf_rna_multi = readFromResource("/nbrf_rna_multi.nbrf");
            nbrf_single_broken = readFromResource("/nbrf_single_broken.nbrf");
            nbrf_multi_broken = readFromResource("/nbrf_multi_broken.nbrf");
            fasta_aa_alignment = readFromResource("/fasta_aa_alignment.fas");
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
    public void testAminoAcid_Alignment() {
        validator = new NBRF_Validator();
        validator.setContent(CONTENT.AA);
        validator.setAlignment(true);
        //---
        ValidationResult vr = validator.validateThis(nbrf_aa_alignment);
        // AA 
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(nbrf_aa_amb_alignment);
        // AA ambigious
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(nbrf_dna_alignment);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(nbrf_rna_alignment);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(nbrf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(nbrf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken nbrf data
        vr = validator.validateThis(nbrf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testAminoAcidambigous_Alignment() {
        validator = new NBRF_Validator();
        validator.setContent(CONTENT.AA);
        validator.setStrictness(STRICTNESS.ambiguous);
        validator.setAlignment(true);
        //---
        ValidationResult vr = validator.validateThis(nbrf_aa_alignment);
        // AA 
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(nbrf_aa_amb_alignment);
        // AA ambigious
        assertTrue(vr.getMessage(), vr.isValid());
    }

    @Test
    public void testDNA_Alignment() {
        validator = new NBRF_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setAlignment(true);
        // --
        ValidationResult vr = validator.validateThis(nbrf_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(nbrf_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(nbrf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(nbrf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken nbrf data
        vr = validator.validateThis(nbrf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testRNA_Alignment() {
        validator = new NBRF_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setAlignment(true);
        // --
        ValidationResult vr = validator.validateThis(nbrf_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(nbrf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(nbrf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken nbrf data
        vr = validator.validateThis(nbrf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testAminoAcid_Multi() {
        validator = new NBRF_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.multi);
        // --
        ValidationResult vr = validator.validateThis(nbrf_aa_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
//        vr = validator.validateThis(nbrf_dna_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
//        vr = validator.validateThis(nbrf_rna_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(nbrf_aa_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(nbrf_dna_multi);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(nbrf_rna_multi);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(nbrf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken nbrf data
        vr = validator.validateThis(nbrf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testAminoAcid_Single() {

        validator = new NBRF_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.single);
        // --
        ValidationResult vr = validator.validateThis(nbrf_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(nbrf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(nbrf_aa_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(nbrf_dna_single);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(nbrf_rna_single);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken nbrf data
        vr = validator.validateThis(nbrf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testDNA_Multi() {

        validator = new NBRF_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.multi);
        // --
        ValidationResult vr = validator.validateThis(nbrf_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
//        vr = validator.validateThis(nbrf_dna_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
        vr = validator.validateThis(nbrf_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(nbrf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(nbrf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(nbrf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken nbrf data
        vr = validator.validateThis(nbrf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testDNA_Single() {
        validator = new NBRF_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.single);
        // --
        ValidationResult vr = validator.validateThis(nbrf_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(nbrf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(nbrf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(nbrf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken nbrf data
        vr = validator.validateThis(nbrf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testRNA_Multi() {
        validator = new NBRF_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.multi);
        // --
        ValidationResult vr = validator.validateThis(nbrf_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
//        vr = validator.validateThis(nbrf_rna_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(nbrf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(nbrf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken nbrf data
        vr = validator.validateThis(nbrf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testRNA_Single() {
        validator = new NBRF_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.single);
        // --
        ValidationResult vr = validator.validateThis(nbrf_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(nbrf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(nbrf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_rna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken nbrf data
        vr = validator.validateThis(nbrf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(nbrf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }
    
    @Test
    public void testAAAlignmentWrongFormat() {
        validator = new NBRF_Validator();
        validator.setContent(CONTENT.AA);
        validator.setStrictness(STRICTNESS.ambiguous);
        validator.setAlignment(true);
        // -- alignment data
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertFalse(vr.getMessage(), vr.isValid());
    }


}
