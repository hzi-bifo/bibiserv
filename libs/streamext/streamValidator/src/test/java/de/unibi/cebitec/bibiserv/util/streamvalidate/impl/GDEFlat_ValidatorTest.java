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
 * Tests for all GDE validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author mrumming
 */
public class GDEFlat_ValidatorTest {

    private GDEFlat_Validator validator;
    private String gde_aa_single_flat;
    private String gde_dna_single_flat;
    private String gde_rna_single_flat;
    private String gde_aa_multi_flat;
    private String gde_dna_multi_flat;
    private String gde_rna_multi_flat;
    private String gde_aa_alignment_flat;
    private String gde_dna_alignment_flat;
    private String gde_rna_alignment_flat;
    private String gde_single_broken_1;
    private String gde_single_broken_2;
    private String gde_multi_broken;
    private String fasta_aa_single;

    public GDEFlat_ValidatorTest() {
        try {
            gde_aa_single_flat = readFromResource("/gde_aa_single_flat.gde");
            gde_dna_single_flat = readFromResource("/gde_dna_single_flat.gde");
            gde_rna_single_flat = readFromResource("/gde_rna_single_flat.gde");
            gde_aa_multi_flat = readFromResource("/gde_aa_multi_flat.gde");
            gde_dna_multi_flat = readFromResource("/gde_dna_multi_flat.gde");
            gde_rna_multi_flat = readFromResource("/gde_rna_multi_flat.gde");
            gde_aa_alignment_flat = readFromResource("/gde_aa_alignment_flat.gde");
            gde_dna_alignment_flat = readFromResource("/gde_dna_alignment_flat.gde");
            gde_rna_alignment_flat = readFromResource("/gde_rna_alignment_flat.gde");
            gde_single_broken_1 = readFromResource("/gde_single_broken_1.gde");
            gde_single_broken_2 = readFromResource("/gde_single_broken_2.gde");
            gde_multi_broken = readFromResource("/gde_multi_broken.gde");
            
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
    public void testGRA() {
        validator = new GDEFlat_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setAlignment(true);
        validator.setStrictness(STRICTNESS.strict);


        ValidationResult vr = validator.validateThis(this.gde_aa_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_dna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_rna_alignment_flat);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(this.gde_aa_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_aa_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_dna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_dna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_rna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_rna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testGDA() {
        validator = new GDEFlat_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setAlignment(true);
        validator.setStrictness(STRICTNESS.strict);


        ValidationResult vr = validator.validateThis(this.gde_aa_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_alignment_flat);
        assertTrue(vr.getMessage(), vr.isValid());

        vr = validator.validateThis(this.gde_rna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_rna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testGAA() {
        validator = new GDEFlat_Validator();
        validator.setContent(CONTENT.AA);
        validator.setAlignment(true);
        validator.setStrictness(STRICTNESS.strict);

        ValidationResult vr = validator.validateThis(this.gde_aa_alignment_flat);
        assertTrue(vr.getMessage(), vr.isValid());

        vr = validator.validateThis(this.gde_dna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_rna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testGAM() {
        validator = new GDEFlat_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.multi);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(this.gde_aa_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_multi_flat);
        assertTrue(vr.getMessage(), vr.isValid());

        vr = validator.validateThis(this.gde_aa_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_rna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testGAS() {
        validator = new GDEFlat_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.single);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(this.gde_aa_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_single_flat);
        assertTrue(vr.getMessage(), vr.isValid());

        vr = validator.validateThis(this.gde_dna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_rna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());


    }

    @Test
    public void testGDM() {
        validator = new GDEFlat_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.multi);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(this.gde_aa_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_multi_flat);
        assertTrue(vr.getMessage(), vr.isValid());

        vr = validator.validateThis(this.gde_dna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_rna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testGDS() {
        validator = new GDEFlat_Validator();
        validator.setContent(CONTENT.DNA);
        validator.setCardinality(CARDINALITY.single);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(this.gde_aa_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_single_flat);
        assertTrue(vr.getMessage(), vr.isValid());

        vr = validator.validateThis(this.gde_rna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_rna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }

    @Test
    public void testGRM() {
        validator = new GDEFlat_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.multi);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(this.gde_aa_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());


        vr = validator.validateThis(this.gde_aa_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_multi_flat);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(this.gde_rna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testGRS() {
        validator = new GDEFlat_Validator();
        validator.setContent(CONTENT.RNA);
        validator.setCardinality(CARDINALITY.single);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(this.gde_aa_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_alignment_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_aa_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_dna_single_flat);
        assertTrue(vr.getMessage(), !vr.isValid());

        vr = validator.validateThis(this.gde_rna_multi_flat);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_rna_single_flat);
        assertTrue(vr.getMessage(), vr.isValid());

        vr = validator.validateThis(this.gde_multi_broken);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(this.gde_single_broken_2);
        assertTrue(vr.getMessage(), !vr.isValid());

    }
    
    @Test
    public void testWrongFormat() {
     validator = new GDEFlat_Validator();
        validator.setContent(CONTENT.AA);
        validator.setCardinality(CARDINALITY.single);
        validator.setStrictness(STRICTNESS.strict);
        ValidationResult vr = validator.validateThis(fasta_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
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
