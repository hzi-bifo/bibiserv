/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de "
 *
 * Contributor(s): Thomas Gatter
 *
 */
package de.unibi.cebitec.bibiserv.util.streamvalidate.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for fastq variant illumina.
 * @author Thomas Gatter - tgatter@cebitec.uni-bielefeld.de
 */
public class Fastq_solexa_ValidatorTest extends Abstract_ValidatorTest{
    
    public Fastq_solexa_ValidatorTest() {
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
    public void testValidInput()throws IOException{
        
        String data = readFromResource("/fastq_solexa_valid.fastq");
        Fastq_solexa_Validator instance = new Fastq_solexa_Validator();
        
        ValidationResult result = instance.validateThis(data);
        
        assertTrue(result.isValid());

    }
    
    @Test
    public void testInvalidInput() throws IOException{
        
       
        Fastq_solexa_Validator instance = new Fastq_solexa_Validator();
        
        String data = readFromResource("/fastq_solexa_invalid.fastq");
        ValidationResult result = instance.validateThis(data);
        assertFalse(result.isValid());
    }
    
   
}
