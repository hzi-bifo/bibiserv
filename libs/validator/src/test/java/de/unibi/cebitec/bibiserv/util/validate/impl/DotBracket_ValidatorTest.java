/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.validate.impl;


import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CARDINALITY;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/** 
 * JUnit Test for DotBracket_Validator.
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class DotBracket_ValidatorTest extends AbstractTest{
    
    public String dotbracket_single;

    public String dotbracket_single_multistructure;
    public String dotbracket_multi;
    public String dotbracket_alignment;

    private DotBracket_Validator validator = new DotBracket_Validator();
    
    public DotBracket_ValidatorTest() {
         try {
            dotbracket_single = readFromResource("/dotbracket_single.db");
            dotbracket_single_multistructure = readFromResource("/dotbracket_single_multistructure.db");
            dotbracket_multi = readFromResource("/dotbracket_multi.db");
            dotbracket_alignment = readFromResource("/dotbracket_alignment.db");
          
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

    /**
     * Test of validateThis method, of class DotBracket_Validator.
     */
    @Test
    public void testValidateThis() {
        validator = new DotBracket_Validator();
        
        // default : CARDINALITY.single, STRICTNESS.strict, alignment = false
        ValidationResult vr = validator.validateThis(dotbracket_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(dotbracket_single_multistructure);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(dotbracket_multi);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dotbracket_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        
        
        // default : CARDINALITY.multi, STRICTNESS.strict, alignment = false
        validator.setCardinality(CARDINALITY.multi);
        vr = validator.validateThis(dotbracket_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dotbracket_single_multistructure);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dotbracket_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(dotbracket_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
        
        // default : CARDINALITY.honeybadger, STRICTNESS.strict, alignment = false
        validator.setCardinality(CARDINALITY.honeybadger);
        vr = validator.validateThis(dotbracket_single);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(dotbracket_single_multistructure);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(dotbracket_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(dotbracket_alignment);
        assertTrue(vr.getMessage(), !vr.isValid());
     
        validator.setCardinality(CARDINALITY.multi);
        validator.setAlignment(true);
        vr = validator.validateThis(dotbracket_single);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dotbracket_single_multistructure);
        assertTrue(vr.getMessage(), !vr.isValid());
        vr = validator.validateThis(dotbracket_multi);
        assertTrue(vr.getMessage(), vr.isValid());
        vr = validator.validateThis(dotbracket_alignment);
        assertTrue(vr.getMessage(), vr.isValid());
        
    }
    
    
  
}
