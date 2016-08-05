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
 * Test for all Phylip validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author mrumming
 */
public class Phylip_ValidatorTest extends AbstractTest{

    private Phylip_Validator validator;
    private String phylip_aa_alignment;
    private String phylip_dna_alignment;
    private String phylip_rna_alignment;
    private String phylip_broken_1;
    private String phylip_broken_2;
    private String fasta_aa_alignment;

    public Phylip_ValidatorTest() {
        try {
            phylip_aa_alignment = readFromResource("/phylip_aa_alignment.phy");
            phylip_dna_alignment = readFromResource("/phylip_dna_alignment.phy");
            phylip_rna_alignment = readFromResource("/phylip_rna_alignment.phy");
            phylip_broken_1 = readFromResource("/phylip_broken_1.phy");
            phylip_broken_2 = readFromResource("/phylip_broken_2.phy");
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
    public void testAminoAcid() {
        validator = new Phylip_Validator();
        validator.setContent(CONTENT.AA);
        validator.setStrictness(STRICTNESS.ambiguous);

        // -- alignment data
        ValidationResult vr = validator.validateThis(phylip_aa_alignment);

        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(phylip_dna_alignment);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(phylip_rna_alignment);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken phy data
        vr = validator.validateThis(phylip_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(phylip_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testDNA() {
        validator = new Phylip_Validator();
        validator.setContent(CONTENT.DNA);

        // -- alignment data
        ValidationResult vr = validator.validateThis(phylip_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(phylip_dna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(phylip_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken phy data
        vr = validator.validateThis(phylip_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(phylip_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testRNA() {
        validator = new Phylip_Validator();
        validator.setContent(CONTENT.RNA);
        // -- alignment data
        ValidationResult vr = validator.validateThis(phylip_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(phylip_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(phylip_rna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken phy data
        vr = validator.validateThis(phylip_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(phylip_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }
    /*
    @Test
    public void testBroken1(){
    this.validator = new Phylip_RNA_Alignment_Validator();
    
    }
     * 
     */
    
    @Test
    public void testAAAlignmentWrongFormat() {
        validator = new Phylip_Validator();
        validator.setContent(CONTENT.AA);
        validator.setStrictness(STRICTNESS.ambiguous);
        validator.setAlignment(true);
        // -- alignment data
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertFalse(vr.getMessage(), vr.isValid());
    }


}
