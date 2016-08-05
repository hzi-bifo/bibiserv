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
 * Test for all Dialign validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author mrumming
 */
public class Dialign_ValidatorTest extends AbstractTest{

    private Dialign_Validator validator;
    private String dialign_aa_alignment;
    private String dialign_dna_alignment;
    private String dialign_rna_alignment;
    private String dialign_broken_1;
    private String dialign_broken_2;
    private String fasta_aa_alignment;

    public Dialign_ValidatorTest() {
        try {
            dialign_aa_alignment = readFromResource("/dialign_aa_alignment.dia");
            dialign_dna_alignment = readFromResource("/dialign_dna_alignment.dia");
            dialign_rna_alignment = readFromResource("/dialign_rna_alignment.dia");
            dialign_broken_1 = readFromResource("/dialign_broken_1.dia");
            dialign_broken_2 = readFromResource("/dialign_broken_2.dia");
            
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
    public void testDialign_AminoAcid_Alignment() {
        validator = new Dialign_Validator();
        validator.setContent(CONTENT.AA);

        // -- alignment data
        ValidationResult vr = validator.validateThis(dialign_aa_alignment);

        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(dialign_dna_alignment);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(dialign_rna_alignment);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken dia data
        vr = validator.validateThis(dialign_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dialign_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testDialign_DNA_Alignment() {
        validator = new Dialign_Validator();
        validator.setContent(CONTENT.DNA);
        // -- alignment data
        ValidationResult vr = validator.validateThis(dialign_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dialign_dna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(dialign_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken dia data
        vr = validator.validateThis(dialign_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dialign_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testDialign_RNA_Alignment() {
        validator = new Dialign_Validator();
        validator.setContent(CONTENT.RNA);
        // -- alignment data
        ValidationResult vr = validator.validateThis(dialign_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dialign_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dialign_rna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken dia data
        vr = validator.validateThis(dialign_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dialign_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }
    
    @Test
    public void testAAAlignmentWrongFormat() {
        validator = new Dialign_Validator();
        validator.setContent(CONTENT.AA);
        // -- alignment data
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertFalse(vr.getMessage(), vr.isValid());
    }

    
}
