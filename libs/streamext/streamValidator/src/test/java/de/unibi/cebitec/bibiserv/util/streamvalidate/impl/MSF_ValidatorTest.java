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
 * Test for all MSF validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author mrumming
 */
public class MSF_ValidatorTest extends Abstract_ValidatorTest{

    private MSF_Validator validator;
    private String msf_aa_msf_alignment;
    private String msf_dna_msf_alignment;
    private String msf_rna_msf_alignment;
    private String msf_aa_pileup_alignment;
    private String msf_dna_pileup_alignment;
    private String msf_rna_pileup_alignment;
    private String msf_aa_rich_alignment;
    private String msf_dna_rich_alignment;
    private String msf_rna_rich_alignment;
    private String fasta_aa_alignment;

    public MSF_ValidatorTest() {
        try {
            msf_aa_msf_alignment = readFromResource("/msf_aa_alignment_msf.msf");
            msf_dna_msf_alignment = readFromResource("/msf_dna_alignment_msf.msf");
            msf_rna_msf_alignment = readFromResource("/msf_rna_alignment_msf.msf");
            msf_aa_pileup_alignment = readFromResource("/msf_aa_alignment_pileup.msf");
            msf_dna_pileup_alignment = readFromResource("/msf_dna_alignment_pileup.msf");
            msf_rna_pileup_alignment = readFromResource("/msf_rna_alignment_pileup.msf");
            msf_aa_rich_alignment = readFromResource("/msf_aa_alignment_rich.msf");
            msf_dna_rich_alignment = readFromResource("/msf_dna_alignment_rich.msf");
            msf_rna_rich_alignment = readFromResource("/msf_rna_alignment_rich.msf");
            
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
    public void testMSF_AminoAcid() {
        validator = new MSF_Validator();
        validator.setContent(CONTENT.AA);

        // -- msf
        ValidationResult vr = validator.validateThis(msf_aa_msf_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(msf_dna_msf_alignment);
        // DNA alphabet is a subset of AA alphabet -> expect valid

        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(msf_rna_msf_alignment);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- rich, 
        vr = validator.validateThis(msf_aa_rich_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(msf_dna_rich_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(msf_rna_rich_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- pileup, 
        vr = validator.validateThis(msf_aa_pileup_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(msf_dna_pileup_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(msf_rna_pileup_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
    }

    @Test
    public void testMSF_DNA() {
        validator = new MSF_Validator();
        validator.setContent(CONTENT.DNA);
        // -- msf
        ValidationResult vr = validator.validateThis(msf_aa_msf_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(msf_dna_msf_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(msf_rna_msf_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- rich, 
        vr = validator.validateThis(msf_aa_rich_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(msf_dna_rich_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(msf_rna_rich_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- pileup,
        vr = validator.validateThis(msf_aa_pileup_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(msf_dna_pileup_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(msf_rna_pileup_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testMSF_RNA() {
        validator = new MSF_Validator();
        validator.setContent(CONTENT.RNA);
        // -- msf
        ValidationResult vr = validator.validateThis(msf_aa_msf_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(msf_dna_msf_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(msf_rna_msf_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- rich
        vr = validator.validateThis(msf_aa_rich_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(msf_dna_rich_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(msf_rna_rich_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- pileup
        vr = validator.validateThis(msf_aa_pileup_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(msf_dna_pileup_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(msf_rna_pileup_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
    }

      @Test
    public void testAAAlignmentWrongFormat() {
        validator = new MSF_Validator();
        validator.setContent(CONTENT.AA);
        // -- alignment data
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertFalse(vr.getMessage(), vr.isValid());
    }


}
