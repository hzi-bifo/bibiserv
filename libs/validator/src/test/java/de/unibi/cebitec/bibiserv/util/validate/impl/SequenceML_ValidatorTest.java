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
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.STRICTNESS;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for all SequenceML validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class SequenceML_ValidatorTest extends AbstractTest{

    public String sml_aa_single;
    public String sml_rna_single;
    public String sml_dna_single;
    public String sml_aa_multi;
    public String sml_rna_multi;
    public String sml_dna_multi;
    private SequenceML_Validator validator;

    public SequenceML_ValidatorTest() {
        try {
            sml_aa_single = readFromResource("/sml_aa_single.xml");
            sml_aa_multi = readFromResource("/sml_aa_multi.xml");
            sml_rna_single = readFromResource("/sml_rna_single.xml");
            sml_rna_multi = readFromResource("/sml_rna_multi.xml");
            sml_dna_single = readFromResource("/sml_dna_single.xml");
            sml_dna_multi = readFromResource("/sml_dna_multi.xml");




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
    public void test_AminoAcid_Single() {
        validator = new SequenceML_Validator();
        validator.setCardinality(CARDINALITY.single);
        validator.setContent(CONTENT.AA);
        validator.setStrictness(STRICTNESS.strict);

        // -- single
        ValidationResult vr = validator.validateThis(sml_aa_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(sml_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // --- multi
        vr = validator.validateThis(sml_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void test_AminoAcid_Multiple() {
        validator = new SequenceML_Validator();
        validator.setCardinality(CARDINALITY.multi);
        validator.setContent(CONTENT.AA);
        validator.setStrictness(STRICTNESS.strict);

        // -- single
        ValidationResult vr = validator.validateThis(sml_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // ---multi
        vr = validator.validateThis(sml_aa_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(sml_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void test_RNA_Single() {
        validator = new SequenceML_Validator();
        validator.setCardinality(CARDINALITY.single);
        validator.setContent(CONTENT.RNA);
        validator.setStrictness(STRICTNESS.strict);

        // -- single
        ValidationResult vr = validator.validateThis(sml_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_rna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(sml_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // --- multi
        vr = validator.validateThis(sml_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void test_RNA_Multiple() {
        validator = new SequenceML_Validator();
        validator.setCardinality(CARDINALITY.multi);
        validator.setContent(CONTENT.RNA);
        validator.setStrictness(STRICTNESS.strict);

        // --- single
        ValidationResult vr = validator.validateThis(sml_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // ---multi
        vr = validator.validateThis(sml_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_rna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(sml_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void test_DNA_Single() {
        validator = new SequenceML_Validator();
        validator.setCardinality(CARDINALITY.single);
        validator.setContent(CONTENT.DNA);
        validator.setStrictness(STRICTNESS.strict);
        // --- single
        ValidationResult vr = validator.validateThis(sml_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_dna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        // --- multi
        vr = validator.validateThis(sml_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void test_DNA_Multiple() {
        validator = new SequenceML_Validator();
        validator.setCardinality(CARDINALITY.multi);
        validator.setContent(CONTENT.DNA);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(sml_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // ---multi
        vr = validator.validateThis(sml_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(sml_dna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
    }

  
}
