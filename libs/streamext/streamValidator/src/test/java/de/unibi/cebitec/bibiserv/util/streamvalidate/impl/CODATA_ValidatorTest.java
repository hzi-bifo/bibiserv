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
 * Test for all PIR validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author mrumming
 */
public class CODATA_ValidatorTest {

    private CODATA_Validator validator;
    private String codata_aa_alignment;
    private String codata_aa_amb_alignment;
    private String codata_dna_alignment;
    private String codata_rna_alignment;
    private String codata_aa_single;
    private String codata_dna_single;
    private String codata_rna_single;
    private String codata_aa_multi;
    private String codata_dna_multi;
    private String codata_rna_multi;
    private String codata_single_broken;
    private String codata_multi_broken;
    private String fasta_aa_alignment;

    public CODATA_ValidatorTest() {
        try {
            codata_aa_alignment = readFromResource("/codata_aa_alignment.pir");
            codata_aa_amb_alignment = readFromResource("/codata_aa_amb_alignment.pir");
            codata_dna_alignment = readFromResource("/codata_dna_alignment.pir");
            codata_rna_alignment = readFromResource("/codata_rna_alignment.pir");
            codata_aa_single = readFromResource("/codata_aa_single.pir");
            codata_dna_single = readFromResource("/codata_dna_single.pir");
            codata_rna_single = readFromResource("/codata_rna_single.pir");
            codata_aa_multi = readFromResource("/codata_aa_multi.pir");
            codata_dna_multi = readFromResource("/codata_dna_multi.pir");
            codata_rna_multi = readFromResource("/codata_rna_multi.pir");
            codata_single_broken = readFromResource("/codata_single_broken.pir");
            codata_multi_broken = readFromResource("/codata_multi_broken.pir");
            
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
    public void testAminoAcidAlignment() {
        validator = new CODATA_Validator();
        validator.setAlignment(true);
        validator.setContent(CONTENT.AA);
        validator.setStrictness(STRICTNESS.strict);
        // -- alignment data
        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(codata_aa_amb_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_alignment);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(codata_rna_alignment);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(codata_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(codata_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(codata_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testAmbigousAminoAcidAlignment() {
        validator = new CODATA_Validator();
        validator.setAlignment(true);
        validator.setContent(CONTENT.AA);
        validator.setStrictness(STRICTNESS.ambiguous);
        // -- alignment data
        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(codata_aa_amb_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
    }

    @Test
    public void testDNAAlignemnt() {
        validator = new CODATA_Validator();
        validator.setAlignment(true);
        validator.setContent(CONTENT.DNA);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(codata_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(codata_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(codata_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(codata_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testRNAAlignment() {
        validator = new CODATA_Validator();
        validator.setAlignment(true);
        validator.setContent(CONTENT.RNA);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(codata_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(codata_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(codata_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testAminoAcidMulti() {
        validator = new CODATA_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.multi);
        validator.setStrictness(STRICTNESS.strict);

        ValidationResult vr = validator.validateThis(codata_aa_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
//        vr = validator.validateThis(codata_dna_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
//        vr = validator.validateThis(codata_rna_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(codata_aa_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(codata_dna_multi);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(codata_rna_multi);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(codata_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(codata_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testAminoAcidSingle() {
        validator = new CODATA_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.single);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(codata_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(codata_aa_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(codata_dna_single);
        // DNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(codata_rna_single);
        // RNA alphabet is a subset of AA alphabet -> expect valid
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(codata_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testDNAMulti() {
        validator = new CODATA_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.multi);
        validator.setStrictness(STRICTNESS.strict);

        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
//        vr = validator.validateThis(codata_dna_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
        vr = validator.validateThis(codata_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(codata_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(codata_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(codata_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(codata_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testDNASingle() {
        validator = new CODATA_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.single);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(codata_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(codata_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(codata_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(codata_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testRNAMulti() {
        validator = new CODATA_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.multi);
        validator.setStrictness(STRICTNESS.strict);

        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
//        vr = validator.validateThis(codata_rna_alignment);
//        assertTrue(vr.getMessage(),!vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(codata_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(codata_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(codata_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testRNASingle() {
        validator = new CODATA_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.single);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- multiple sequence data
        vr = validator.validateThis(codata_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(codata_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_dna_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_rna_single);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken pir data
        vr = validator.validateThis(codata_single_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(codata_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }
    
    @Test
    public void testAAAlignmentWrongFormat() {
        validator = new CODATA_Validator();
        validator.setAlignment(true);
        validator.setContent(CONTENT.AA);
        validator.setStrictness(STRICTNESS.strict);
        // -- alignment data
        ValidationResult vr = validator.validateThis(fasta_aa_alignment);
        assertFalse(vr.getMessage(), vr.isValid());
    }

    /**
     * Priavate helper method. Return content from an 'named' resource' as string.
     *
     *
     * @param name - name of rsource
     * @return string
     * @throws IOException
     */
    private String readFromResource(String name) throws IOException {
        InputStream rin = getClass().getResourceAsStream(name);
        if (rin == null) {
            throw new IOException("Resource '" + name + "' not found in classpath!");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(rin));
        String l = null;
        StringBuffer sb = new StringBuffer();
        while ((l = br.readLine()) != null) {
            sb.append(l);
            sb.append(System.getProperty("line.separator"));
        }
        br.close();
        return sb.toString();
    }

    private String readFile(FileReader fr) throws IOException {
        BufferedReader r = new BufferedReader(fr);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();

    }
}
