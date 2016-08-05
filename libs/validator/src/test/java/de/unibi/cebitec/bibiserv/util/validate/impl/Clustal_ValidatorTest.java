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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 * Test for all clustal validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author Thomas Gatter - tgatter(aet)techfak.uni-bielefeld.de 
 */
public class Clustal_ValidatorTest extends AbstractTest{

    private Clustal_Validator validator;
    private String clustal_aa;
    private String clustal_aa_broken;
    private String clustal_rna;
    private String clustal_dna;
    private String codata_aa_alignment;

    public Clustal_ValidatorTest() {
        try {
            clustal_aa = readFromResource("/clustal_aa.cw");
            clustal_dna = readFromResource("/clustal_dna.cw");
            clustal_rna = readFromResource("/clustal_rna.cw");
            clustal_aa_broken = readFromResource("/clustal_aa_broken.cw");
           
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
    public void testAminoAcidAlignment() {
        validator = new Clustal_Validator();
        validator.setAlignment(true);
        validator.setStrictness(STRICTNESS.strict);
        validator.setContent(CONTENT.AA);
        // -- alignment data
        ValidationResult vr = validator.validateThis(clustal_aa);
        assertTrue(vr.getMessage(), vr.isValid());
        
        vr = validator.validateThis(clustal_aa_broken);
        assertFalse(vr.getMessage(), vr.isValid());
    }

    
    @Test
    public void testDNAAlignemnt() {
        validator = new Clustal_Validator();
        validator.setAlignment(true);
        validator.setStrictness(STRICTNESS.strict);
        validator.setContent(CONTENT.DNA);
        // -- alignment data
        ValidationResult vr = validator.validateThis(clustal_dna);
        assertTrue(vr.getMessage(), vr.isValid());
    }

    @Test
    public void testRNAAlignment() {
        validator = new Clustal_Validator();
        validator.setAlignment(true);
        validator.setStrictness(STRICTNESS.strict);
        validator.setContent(CONTENT.RNA);
        // -- alignment data
        ValidationResult vr = validator.validateThis(clustal_rna);
        assertTrue(vr.getMessage(), vr.isValid());
    }
    
    @Test
    public void testAAAlignmentWrongFormat() {
        validator = new Clustal_Validator();
        validator.setAlignment(true);
        validator.setStrictness(STRICTNESS.strict);
        validator.setContent(CONTENT.AA);
        // -- alignment data
        ValidationResult vr = validator.validateThis(codata_aa_alignment);
        assertFalse(vr.getMessage(), vr.isValid());
    }
    


}
