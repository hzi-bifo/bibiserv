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
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import org.junit.Test;
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
 * Test for all GCG9/RSF validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author mrumming
 */
public class RSF_ValidatorTest extends Abstract_ValidatorTest {

    private RSF_Validator validator;
    private String rsf_aa_single;
    private String rsf_dna_single;
    private String rsf_rna_single;
    private String rsf_aa_multi;
    private String rsf_dna_multi;
    private String rsf_rna_multi;
    private String rsf_single_broken;
    private String rsf_multi_broken;
    private String fasta_aa_single;

    public RSF_ValidatorTest() {
        try {
            rsf_aa_single = readFromResource("/rsf_aa_single.rsf");
            rsf_dna_single = readFromResource("/rsf_dna_single.rsf");
            rsf_rna_single = readFromResource("/rsf_rna_single.rsf");
            rsf_aa_multi = readFromResource("/rsf_aa_multi.rsf");
            rsf_dna_multi = readFromResource("/rsf_dna_multi.rsf");
            rsf_rna_multi = readFromResource("/rsf_rna_multi.rsf");
            rsf_single_broken = readFromResource("/rsf_single_broken.rsf");
            rsf_multi_broken = readFromResource("/rsf_multi_broken.rsf");
            
            fasta_aa_single = readFromResource("/fasta_aa_single.fas");
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
    public void testAminoAcid_Multi() {
        validator = new RSF_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.multi);
        //--
        ValidationResult vr = validator.validateThis(rsf_aa_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(rsf_dna_multi);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(rsf_rna_multi);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(rsf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(rsf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testAminoAcid_Single() {
        validator = new RSF_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.single);

        ValidationResult vr = validator.validateThis(rsf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(rsf_aa_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(rsf_dna_single);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(rsf_rna_single);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(rsf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testDNA_Multi() {
        validator = new RSF_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.multi);

        ValidationResult vr = validator.validateThis(rsf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_dna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(rsf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(rsf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(rsf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testDNA_Single() {
        validator = new RSF_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.single);

        ValidationResult vr = validator.validateThis(rsf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(rsf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_dna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(rsf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(rsf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testRNA_Multi() {
        validator = new RSF_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.multi);

        ValidationResult vr = validator.validateThis(rsf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_rna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(rsf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(rsf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testRNA_Single() {
        validator = new RSF_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.single);

        ValidationResult vr = validator.validateThis(rsf_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(rsf_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_rna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(rsf_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(rsf_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }
    
    @Test
    public void testAASingleWrongFormat() {
        validator = new RSF_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.single);

        // -- alignment data
        ValidationResult vr = validator.validateThis(fasta_aa_single);
        assertFalse(vr.getMessage(), vr.isValid());
    }


}
