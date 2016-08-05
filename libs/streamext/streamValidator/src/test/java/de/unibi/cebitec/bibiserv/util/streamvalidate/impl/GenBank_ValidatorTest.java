/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Jan Krueger
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
 * Test for  GenBank validator;
 *
 * Make sure that directory 'testdata' is within classpath!
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class GenBank_ValidatorTest extends Abstract_ValidatorTest{

    private GenBank_Validator validator;
    private String genbank_dna;
    private String genbank_rna;
    private String genbank_brocken;
    
  

    public GenBank_ValidatorTest() {
        try {
            
            genbank_dna = readFromResource("/genbank_dna.db");
            genbank_rna = readFromResource("/genbank_rna.db");
          
            
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
    public void testDNA() {
        validator = new GenBank_Validator();
        validator.setContent(CONTENT.DNA);
        // DNA -should be ok
        ValidationResult vr = validator.validateThis(genbank_dna);
        assertTrue(vr.getMessage(), vr.isValid());
        // RNA - should be failed
        vr = validator.validateThis(genbank_rna);
        assertFalse(vr.getMessage(), vr.isValid());  
    }

    
    @Test
    public void testRNA() {
        validator = new GenBank_Validator();
        validator.setContent(CONTENT.RNA);
        // DNA - should be failed
        ValidationResult vr = validator.validateThis(genbank_dna);
        assertTrue(vr.getMessage(), !vr.isValid());
        // RNA - should be ok
        vr = validator.validateThis(genbank_rna);
        assertTrue(vr.getMessage(), vr.isValid());  
    }
    
   
  
}
