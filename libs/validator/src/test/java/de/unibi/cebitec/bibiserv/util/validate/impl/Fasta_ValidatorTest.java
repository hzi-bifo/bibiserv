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

import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator;
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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for all Fasta_Alignment validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class Fasta_ValidatorTest extends AbstractTest{

    private Fasta_Validator validator;
    private String fasta_aa_alignment;
    private String fasta_dna_alignment;
    private String fasta_rna_alignment;
    private String fasta_aa_single;
    private String fasta_dna_single;
    private String fasta_rna_single;
    private String fasta_aa_multi;
    private String fasta_dna_multi;
    private String fasta_rna_multi;
    private String fasta_single_broken;
    private String fasta_multi_broken;
    private String codata_aa_alignment;

    public Fasta_ValidatorTest() {
        try {




            fasta_aa_alignment = readFromResource("/fasta_aa_alignment.fas");
            fasta_dna_alignment = readFromResource("/fasta_dna_alignment.fas");
            fasta_rna_alignment = readFromResource("/fasta_rna_alignment.fas");
            fasta_aa_single = readFromResource("/fasta_aa_single.fas");
            fasta_dna_single = readFromResource("/fasta_dna_single.fas");
            fasta_rna_single = readFromResource("/fasta_rna_single.fas");
            fasta_aa_multi = readFromResource("/fasta_aa_multi.fas");  
            fasta_dna_multi = readFromResource("/fasta_dna_multi.fas");
            fasta_rna_multi = readFromResource("/fasta_rna_multi.fas");
            fasta_single_broken = readFromResource("/fasta_single_broken.fas");
            fasta_multi_broken = readFromResource("/fasta_multi_broken.fas");

            codata_aa_alignment = readFromResource("/codata_aa_alignment.pir");


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
    public void testFastaAminoAcidAlignemnt() {
        validator = new Fasta_Validator();
        validator.setAlignment(true);
        validator.setContent(SequenceValidator.CONTENT.AA);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);

        // -- alignment data
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(fasta_dna_alignment);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(fasta_rna_alignment);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(fasta_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken fasta data
        vr = validator.validateThis(fasta_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testFastaDNAAlignment() {
        validator = new Fasta_Validator();
        validator.setAlignment(true);
        validator.setContent(SequenceValidator.CONTENT.DNA);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(fasta_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(fasta_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken fasta data
        vr = validator.validateThis(fasta_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testFastaRNAAlignemt() {
        validator = new Fasta_Validator();
        validator.setAlignment(true);
        validator.setContent(SequenceValidator.CONTENT.RNA);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(fasta_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken fasta data
        vr = validator.validateThis(fasta_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testFastaAminoAcidMulti() {
        validator = new Fasta_Validator();
        validator.setContent(SequenceValidator.CONTENT.AA);
        validator.setCardinality(SequenceValidator.CARDINALITY.multi);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);

        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());

        // -- multiple sequence data
        vr = validator.validateThis(fasta_aa_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(fasta_dna_multi);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(fasta_rna_multi);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());

        // -- single sequence data
        vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken fasta data
        vr = validator.validateThis(fasta_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testFastaAminoAcidSingle() {
        validator = new Fasta_Validator();
        validator.setContent(SequenceValidator.CONTENT.AA);
        validator.setCardinality(SequenceValidator.CARDINALITY.single);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);
        // --
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(fasta_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(fasta_dna_single);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(fasta_rna_single);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken fasta data
        vr = validator.validateThis(fasta_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testFastaDNAMulti() {
        validator = new Fasta_Validator();
        validator.setContent(SequenceValidator.CONTENT.DNA);
        validator.setCardinality(SequenceValidator.CARDINALITY.multi);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);
        // -- 
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
//        vr = validator.validateThis(fasta_dna_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
        vr = validator.validateThis(fasta_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(fasta_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(fasta_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken fasta data
        vr = validator.validateThis(fasta_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testFastaDNASingle() {

        validator = new Fasta_Validator();
        validator.setContent(SequenceValidator.CONTENT.DNA);
        validator.setCardinality(SequenceValidator.CARDINALITY.single);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(fasta_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(fasta_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken fasta data
        vr = validator.validateThis(fasta_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testFastaRNAMulti() {
        validator = new Fasta_Validator();
        validator.setContent(SequenceValidator.CONTENT.RNA);
        validator.setCardinality(SequenceValidator.CARDINALITY.multi);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);

        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
//        vr = validator.validateThis(fasta_rna_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(fasta_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken fasta data
        vr = validator.validateThis(fasta_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testFastaRNASingle() {
        validator = new Fasta_Validator();
        validator.setContent(SequenceValidator.CONTENT.RNA);
        validator.setCardinality(SequenceValidator.CARDINALITY.single);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);
        // -- 
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(fasta_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_rna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken fasta data
        vr = validator.validateThis(fasta_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(fasta_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void test_Fasta_AminoAcid() {
        validator = new Fasta_Validator();
        validator.setContent(SequenceValidator.CONTENT.AA);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);
        // multi sequence data
        ValidationResult vr = validator.validateThis(fasta_aa_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        // single sequene data
        vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), vr.isValid());
    }

    @Test
    public void test_Fasta_DNA() {
        validator = new Fasta_Validator();
        validator.setContent(SequenceValidator.CONTENT.DNA);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);
        // multi sequence data
        ValidationResult vr = validator.validateThis(fasta_dna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        // single sequene data
        vr = validator.validateThis(fasta_dna_single);
        assertTrue(vr.getMessage(), vr.isValid());
    }

    @Test
    public void test_Fasta_RNA() {
        validator = new Fasta_Validator();
        validator.setContent(SequenceValidator.CONTENT.RNA);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);
        // multi sequence dataˆ
        ValidationResult vr = validator.validateThis(fasta_rna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        // single sequene data
        vr = validator.validateThis(fasta_rna_single);
        assertTrue(vr.getMessage(), vr.isValid());
    }

        @Test
    public void testAAAlignmentWrongFormat() {
        validator = new Fasta_Validator();
        validator.setAlignment(true);
        validator.setStrictness(SequenceValidator.STRICTNESS.strict);
        validator.setContent(SequenceValidator.CONTENT.AA);
        // -- alignment data
        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertFalse(vr.getMessage(), vr.isValid());
    }
    
    
}
