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

import de.unibi.cebitec.bibiserv.util.validate.AbstractSequenceValidator;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CARDINALITY;
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
 * Test for all UniProt and SwissProt validators;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author mrumming
 */
public class UniProt_SwissProt_ValidatorTest extends AbstractTest{

    private AbstractSequenceValidator validator;
    private String uniprot_aa_single;
    private String uniprot_aa_multi;
    private String uniprot_broken_single_1;
    private String uniprot_broken_single_2;
    private String uniprot_broken_single_3;
    private String uniprot_broken_single_4;
    private String uniprot_broken_multi_1;
    private String uniprot_broken_multi_2;
    private String fasta_aa_single;
    
    public UniProt_SwissProt_ValidatorTest() {
        try {
            uniprot_aa_single = readFromResource("/uniprot_aa_single.uniprot");
            uniprot_aa_multi = readFromResource("/uniprot_aa_multi.uniprot");
            uniprot_broken_single_1 = readFromResource("/uniprot_broken_single_1.uniprot");
            uniprot_broken_single_2 = readFromResource("/uniprot_broken_single_2.uniprot");
            uniprot_broken_single_3 = readFromResource("/uniprot_broken_single_3.uniprot");
            uniprot_broken_single_4 = readFromResource("/uniprot_broken_single_4.uniprot");
            uniprot_broken_multi_1 = readFromResource("/uniprot_broken_multi_1.uniprot");
            uniprot_broken_multi_2 = readFromResource("/uniprot_broken_multi_2.uniprot");
            
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
    public void testUAM() {
        validator = new UniProt_Validator();
        validator.setCardinality(CARDINALITY.multi);
        // -- multiple sequence data
        ValidationResult vr = validator.validateThis(uniprot_aa_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(uniprot_aa_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- broken uniprot data
        vr = validator.validateThis(uniprot_broken_single_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(uniprot_broken_single_2);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(uniprot_broken_single_3);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(uniprot_broken_single_4);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(uniprot_broken_multi_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        // TEST: The following test should be false, but readseq ignores
        // the missing '//', which stands for sequence end.
        vr = validator.validateThis(uniprot_broken_multi_2);
        assertTrue(vr.getMessage(), vr.isValid());
    }

    @Test
    public void testUAS() {
        validator = new UniProt_Validator();
        validator.setCardinality(CARDINALITY.single);
        // -- multiple sequence data
        ValidationResult vr = validator.validateThis(uniprot_aa_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        // -- single sequence data
        vr = validator.validateThis(uniprot_aa_single);
        assertTrue(vr.getMessage(), vr.isValid());
        // -- broken uniprot data
        // TEST: The following test should be false, but readseq ignores
        // the missing '//', which stands for sequence end.
        vr = validator.validateThis(uniprot_broken_single_1);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(uniprot_broken_single_2);
        assertTrue(vr.getMessage(), !vr.isValid());
        // TEST: The following test should be false, but readseq ignores
        // the missing 'ID' and 'AC' lines, which stand for identifier
        // and accession.
        vr = validator.validateThis(uniprot_broken_single_3);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(uniprot_broken_single_4);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(uniprot_broken_multi_1);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(uniprot_broken_multi_2);
        assertTrue(vr.getMessage(), !vr.isValid());
    }
    
        @Test
    public void testAASingleWrongFormat() {
        validator = new UniProt_Validator();
        validator.setCardinality(CARDINALITY.single);

        // -- alignment data
        ValidationResult vr = validator.validateThis(fasta_aa_single);
        assertFalse(vr.getMessage(), vr.isValid());
    }

  
}
