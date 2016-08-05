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
 * Contributor(s): Thomas Gatter
 *
 */ 
package de.unibi.cebitec.bibiserv.util.streamconvert.impl;

import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.impl.FASTQILLUMINA2FASTQSANGER;
import de.unibi.cebitec.bibiserv.util.convert.impl.FASTQILLUMINA2FASTQSOLEXA;
import de.unibi.cebitec.bibiserv.util.convert.impl.FASTQSANGER2FASTQILLUMINA;
import de.unibi.cebitec.bibiserv.util.convert.impl.FASTQSANGER2FASTQSOLEXA;
import de.unibi.cebitec.bibiserv.util.convert.impl.FASTQSOLEXA2FASTQILLUMINA;
import de.unibi.cebitec.bibiserv.util.convert.impl.FASTQSOLEXA2FASTQSANGER;
import de.unibi.cebitec.bibiserv.util.streamconvert.AbstractTest;
import java.io.FileReader;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for all interconversions of fastq variants.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class FASTQ2FASTQTest extends AbstractTest {
    
    private String sanger;
    private String sanger2solexa;
    private String sanger2illumina;
    
    private String illumina;
    private String illumina2solexa;
    private String illumina2sanger;
    
    private String solexa;
    private String solexa2sanger;
    private String solexa2illumina;   
        
    public FASTQ2FASTQTest() {
        try {
            sanger = readFromResource("/sanger_full_range_original_sanger.fastq");
            sanger2solexa = readFromResource("/sanger_full_range_as_solexa.fastq");
            sanger2illumina = readFromResource("/sanger_full_range_as_illumina.fastq");

            illumina = readFromResource("/illumina_full_range_original_illumina.fastq");
            illumina2solexa = readFromResource("/illumina_full_range_as_solexa.fastq");
            illumina2sanger = readFromResource("/illumina_full_range_as_sanger.fastq");

            solexa = readFromResource("/solexa_full_range_original_solexa.fastq");
            solexa2sanger = readFromResource("/solexa_full_range_as_sanger.fastq");
            solexa2illumina = readFromResource("/solexa_full_range_as_illumina.fastq");
            
        } catch (IOException ex) {
          fail("could not read testfiles : "+ex.getMessage());
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
     public void testSanger2Solexa() throws ConversionException {
         FASTQSANGER2FASTQSOLEXA converter = new FASTQSANGER2FASTQSOLEXA();
         String result = (String) converter.convert(sanger);
         assertEquals(result,sanger2solexa);
     }
     
     @Test
     public void testSanger2Illumina() throws ConversionException {
         FASTQSANGER2FASTQILLUMINA converter = new FASTQSANGER2FASTQILLUMINA();
         String result = (String) converter.convert(sanger);
         assertEquals(result,sanger2illumina);
     }
     
     @Test
     public void testIllumina2Solexa() throws ConversionException {
         FASTQILLUMINA2FASTQSOLEXA converter = new FASTQILLUMINA2FASTQSOLEXA();
         String result = (String) converter.convert(illumina);
         assertEquals(result,illumina2solexa);
     }
     
     @Test
     public void testIllumina2Sanger() throws ConversionException {
         FASTQILLUMINA2FASTQSANGER converter = new FASTQILLUMINA2FASTQSANGER();
         String result = (String) converter.convert(illumina);
         assertEquals(result,illumina2sanger);
     }
     
     @Test
     public void testSolexa2Sanger() throws ConversionException {
         FASTQSOLEXA2FASTQSANGER converter = new FASTQSOLEXA2FASTQSANGER();
         String result = (String) converter.convert(solexa);
         assertEquals(result,solexa2sanger);
     }
     
     @Test
     public void testSolexa2Illumina() throws ConversionException {
         FASTQSOLEXA2FASTQILLUMINA converter = new FASTQSOLEXA2FASTQILLUMINA();
         String result = (String) converter.convert(solexa);
         assertEquals(result,solexa2illumina);
     }
}
