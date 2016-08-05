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
 * Test for all IG validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author mrumming
 */
public class IG_ValidatorTest extends AbstractTest{

    private IG_Validator validator;
    private String ig_aa_alignment;
    private String ig_dna_alignment;
    private String ig_rna_alignment;
    private String ig_aa_single;
    private String ig_dna_single;
    private String ig_rna_single;
    private String ig_aa_multi;
    private String ig_dna_multi;
    private String ig_rna_multi;
    private String ig_single_broken;
    private String ig_multi_broken;
    private String fasta_aa_alignment;

    public IG_ValidatorTest() {
        try {
            ig_aa_alignment = readFromResource("/ig_aa_alignment.ig");
            ig_dna_alignment = readFromResource("/ig_dna_alignment.ig");
            ig_rna_alignment = readFromResource("/ig_rna_alignment.ig");
            ig_aa_single = readFromResource("/ig_aa_single.ig");
            ig_dna_single = readFromResource("/ig_dna_single.ig");
            ig_rna_single = readFromResource("/ig_rna_single.ig");
            ig_aa_multi = readFromResource("/ig_aa_multi.ig");
            ig_dna_multi = readFromResource("/ig_dna_multi.ig");
            ig_rna_multi = readFromResource("/ig_rna_multi.ig");
            ig_single_broken = readFromResource("/ig_single_broken.ig");
            ig_multi_broken = readFromResource("/ig_multi_broken.ig");
            
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
    public void testIG_AminoAcid_Alignment() {
        validator = new IG_Validator();
        validator.setContent(CONTENT.AA);
        validator.setAlignment(true);
        // -- alignment data
        ValidationResult vr = validator.validateThis(ig_aa_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(ig_dna_alignment);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(ig_rna_alignment);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(ig_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(ig_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken IG data
        vr = validator.validateThis(ig_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testIG_DNA_Alignment() {

        validator = new IG_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setAlignment(true);
        ValidationResult vr = validator.validateThis(ig_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(ig_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(ig_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(ig_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken ig data
        vr = validator.validateThis(ig_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testIG_RNA_Alignment() {
        validator = new IG_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setAlignment(true);
        // ----
        ValidationResult vr = validator.validateThis(ig_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(ig_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(ig_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken ig data
        vr = validator.validateThis(ig_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testIG_AminoAcid_Multi() {
        validator = new IG_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.multi);
        // ----

        ValidationResult vr = validator.validateThis(ig_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(ig_aa_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(ig_dna_multi);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(ig_rna_multi);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(ig_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken ig data
        vr = validator.validateThis(ig_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testIG_AminoAcid_Single() {
        validator = new IG_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.single);
        // ----
        ValidationResult vr = validator.validateThis(ig_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(ig_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(ig_aa_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(ig_dna_single);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(ig_rna_single);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken ig data
        vr = validator.validateThis(ig_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testIG_DNA_Multi() {
        validator = new IG_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.multi);
        // ----

        ValidationResult vr = validator.validateThis(ig_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(ig_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(ig_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(ig_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken ig data
        vr = validator.validateThis(ig_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testIG_DNA_Single() {
        validator = new IG_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.single);
        // ----
        ValidationResult vr = validator.validateThis(ig_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(ig_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(ig_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(ig_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken ig data
        vr = validator.validateThis(ig_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testIG_RNA_Multi() {
        validator = new IG_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.multi);
        // ----

        ValidationResult vr = validator.validateThis(ig_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(ig_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(ig_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken ig data
        vr = validator.validateThis(ig_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testIG_RNA_Single() {
        validator = new IG_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.single);
        // ----
        ValidationResult vr = validator.validateThis(ig_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(ig_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(ig_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_rna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken ig data
        vr = validator.validateThis(ig_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(ig_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }
    
    @Test
    public void testAAAlignmentWrongFormat() {
        validator = new IG_Validator();
        validator.setContent(CONTENT.AA);
        validator.setAlignment(true);
        // -- alignment data
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertFalse(vr.getMessage(), vr.isValid());
    }


}
