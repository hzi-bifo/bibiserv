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
package de.unibi.cebitec.bibiserv.util.streamconvert.impl;

import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.impl.FASTA2NBRF;
import java.io.FileReader;
import de.unibi.cebitec.bibiserv.util.streamconvert.AbstractTest;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class NBRF2FASTATest extends AbstractTest {

    private String nbrf_aa;
    private String nbrf_na;
    private String nbrf_rna;
    private String nbrf_dna;
    private String nbrf_aa_multi;
    private String nbrf_na_multi;
    private String nbrf_rna_multi;
    private String nbrf_dna_multi;
    private String nbrf_aa_alignment;
    private String nbrf_na_alignment;
    private String nbrf_rna_alignment;
    private String nbrf_dna_alignment;
    private String fasta_aa;
    private String fasta_na;
    private String fasta_rna;
    private String fasta_dna;
    private String fasta_aa_multi;
    private String fasta_na_multi;
    private String fasta_rna_multi;
    private String fasta_dna_multi;
    private String fasta_aa_alignment;
    private String fasta_na_alignment;
    private String fasta_rna_alignment;
    private String fasta_dna_alignment;
   // private NBRF2FASTA nbrf2fasta;
    private FASTA2NBRF fasta2nbrf;

    public NBRF2FASTATest() {
        try {
            nbrf_aa = readFromResource("/nbrf_aa_single.nbrf");
            nbrf_dna = readFromResource("/nbrf_dna_single.nbrf");
            nbrf_rna = readFromResource("/nbrf_rna_single.nbrf");

            nbrf_aa_multi = readFromResource("/nbrf_aa_multi.nbrf");
            nbrf_dna_multi = readFromResource("/nbrf_dna_multi.nbrf");
            nbrf_rna_multi = readFromResource("/nbrf_rna_multi.nbrf");

            nbrf_aa_alignment = readFromResource("/nbrf_aa_alignment.nbrf");
            nbrf_dna_alignment = readFromResource("/nbrf_dna_alignment.nbrf");
            nbrf_rna_alignment = readFromResource("/nbrf_rna_alignment.nbrf");


            fasta_aa = readFromResource("/fasta_aa_single.fas");
            fasta_rna = readFromResource("/fasta_rna_single.fas");
            fasta_dna = readFromResource("/fasta_dna_single.fas");

            fasta_aa_multi = readFromResource("/fasta_aa_multi.fas");
            fasta_rna_multi = readFromResource("/fasta_rna_multi.fas");
            fasta_dna_multi = readFromResource("/fasta_dna_multi.fas");

            fasta_aa_alignment = readFromResource("/fasta_aa_alignment.fas");
            fasta_rna_alignment = readFromResource("/fasta_rna_alignment.fas");
            fasta_dna_alignment = readFromResource("/fasta_dna_alignment.fas");

        } catch (IOException ex) {
            fail("could not read testfiles : " + ex.getMessage());
        }

        //nbrf2fasta = new NBRF2FASTA();
        fasta2nbrf = new FASTA2NBRF();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of convert method, of class GENBANK2FASTA and FASTA2GENBANK.
     */
//    @Test
//    public void testConvert_from_AA() {
//
//        //test aml -> fasta [aminoacid]
//        nbrf2fasta.setContent(CONTENT.AA);
//        try {
//            String result = (String) nbrf2fasta.convert(nbrf_aa);      
//            assertEquals(trim(result), trim(fasta_aa));
//        } catch (ConversionException ex) {
//            fail("could not convert from nbrf_aa !");
//        }
//    }

    public void testConvert_to_AA() {
        // test fasta -> aml [aminoacid]
//        fasta2nbrf.setContent(CONTENT.AA);
        try {
            String result = (String) fasta2nbrf.convert(fasta_aa);
            System.err.println("result:\n"+trimnbrf(result));
            System.err.println("orig  :\n"+trimnbrf(nbrf_aa));
            assertEquals(trimnbrf(result), trimnbrf(nbrf_aa));
        } catch (ConversionException ex) {
            fail("could not convert to nbrf_aa !");
        }
    }

//    @Test
//    public void testConvert_from_AA_Alignment() {
//
//        nbrf2fasta.setContent(CONTENT.AA);
//        try {
//            String result = (String) nbrf2fasta.convert(nbrf_aa_alignment);
//            assertEquals(trim(result), trim(fasta_aa_alignment));
//        } catch (ConversionException ex) {
//
//            fail("could not convert from nbrf_aa_alignment !");
//        }
//    }

    @Test
    public void testConvert_to_AA_Alignment() {

//        fasta2nbrf.setContent(CONTENT.AA);
        try {
            String result = (String) fasta2nbrf.convert(fasta_aa_alignment);
            assertEquals(trimnbrf(result), trimnbrf(nbrf_aa_alignment));
        } catch (ConversionException ex) {
            fail("could not convert to nbrf_aa_alignment !");
        }
    }

//    @Test
//    public void testConvert_from_AA_Multi() {
//        nbrf2fasta.setContent(CONTENT.AA);
//        try {
//            String result = (String) nbrf2fasta.convert(nbrf_aa_multi);
//            assertEquals(trim(result), trim(fasta_aa_multi));
//        } catch (ConversionException ex) {
//
//            fail("could not convert from nbrf_aa_multi !");
//        }
//    }

    @Test
    public void testConvert_to_AA_Multi() {
//        fasta2nbrf.setContent(CONTENT.AA);
        try {
            String result = (String) fasta2nbrf.convert(fasta_aa_multi);
            assertEquals(trimnbrf(result), trimnbrf(nbrf_aa_multi));
        } catch (ConversionException ex) {
            fail("could not convert to nbrf_aa_multi !");
        }
    }

//    @Test
//    public void testConvert_from_DNA() {
//        nbrf2fasta.setContent(CONTENT.DNA);
//        try {
//            String result = (String) nbrf2fasta.convert(nbrf_dna);
//            assertEquals(trim(result), trim(fasta_dna));
//        } catch (ConversionException ex) {
//            fail("could not convert from nbrf_dna !");
//        }
//    }

    @Test
    public void testConvert_to_DNA() {
//        fasta2nbrf.setContent(CONTENT.DNA);
        try {
            String result = (String) fasta2nbrf.convert(fasta_dna);
            assertEquals(trimnbrf(result), trimnbrf(nbrf_dna));
        } catch (ConversionException ex) {
            fail("could not convert to nbrf_dna !");
        }
    }

//    @Test
//    public void testConvert_from_DNA_Alignment() {
//        nbrf2fasta.setContent(CONTENT.DNA);
//        try {
//            String result = (String) nbrf2fasta.convert(nbrf_dna_alignment);
//            assertEquals(trim(result), trim(fasta_dna_alignment));
//        } catch (ConversionException ex) {
//            fail("could not convert from nbrf_dna_alignment !");
//        }
//    }

    @Test
    public void testConvert_to_DNA_Alignment() {
//        fasta2nbrf.setContent(CONTENT.DNA);
        try {
            String result = (String) fasta2nbrf.convert(fasta_dna_alignment);
            assertEquals(trimnbrf(result), trimnbrf(nbrf_dna_alignment));
        } catch (ConversionException ex) {
            fail("could not convert to nbrf_dna_alignment !");
        }
    }

//    @Test
//    public void testConvert_from_DNA_Multi() {
//        nbrf2fasta.setContent(CONTENT.DNA);
//        try {
//            String result = (String) nbrf2fasta.convert(nbrf_dna_multi);
//            assertEquals(trim(result), trim(fasta_dna_multi));
//        } catch (ConversionException ex) {
//            fail("could not convert from nbrf_dna_multi !");
//        }
//    }

    @Test
    public void testConvert_to_DNA_Multi() {
//        fasta2nbrf.setContent(CONTENT.DNA);
        try {
            String result = (String) fasta2nbrf.convert(fasta_dna_multi);
            assertEquals(trimnbrf(result), trimnbrf(nbrf_dna_multi));
        } catch (ConversionException ex) {
            fail("could not convert to nbrf_dna_multi !");
        }
    }
//
//    @Test
//    public void testConvert_from_RNA() {
//        nbrf2fasta.setContent(CONTENT.RNA);
//        try {
//            String result = (String) nbrf2fasta.convert(nbrf_rna);
//            assertEquals(trim(result), trim(fasta_rna));
//        } catch (ConversionException ex) {
//
//            fail("could not convert from nbrf_rna !");
//        }
//    }

    @Test
    public void testConvert_to_RNA() {
//        fasta2nbrf.setContent(CONTENT.RNA);
        try {
            String result = (String) fasta2nbrf.convert(fasta_rna);
            assertEquals(trimnbrf(result), trimnbrf(nbrf_rna));
        } catch (ConversionException ex) {
            fail("could not convert to nbrf_rna !");
        }
    }

//    @Test
//    public void testConvert_from_RNA_Alignment() {
//        nbrf2fasta.setContent(CONTENT.RNA);
//        try {
//            String result = (String) nbrf2fasta.convert(nbrf_rna_alignment);
//            assertEquals(trim(result), trim(fasta_rna_alignment));
//        } catch (ConversionException ex) {
//
//            fail("could not convert from nbrf_rna_alignment !");
//        }
//    }
    
    @Test
    public void testConvert_to_RNA_Alignment() {
//        fasta2nbrf.setContent(CONTENT.RNA);
        try {
            String result = (String) fasta2nbrf.convert(fasta_rna_alignment);
            assertEquals(trimnbrf(result), trimnbrf(nbrf_rna_alignment));
        } catch (ConversionException ex) {
            fail("could not convert to nbrf_rna_alignment !");
        }
    }

//    @Test
//    public void testConvert_from_RNA_Multi() {
//        nbrf2fasta.setContent(CONTENT.RNA);
//        try {
//            String result = (String) nbrf2fasta.convert(nbrf_rna_multi);
//            assertEquals(trim(result), trim(fasta_rna_multi));
//        } catch (ConversionException ex) {
//
//            fail("could not convert from nbrf_rna_multi !");
//        }
//    }
    @Test
    public void testConvert_to_RNA_Multi() {
//        fasta2nbrf.setContent(CONTENT.RNA);
        try {
            String result = (String) fasta2nbrf.convert(fasta_rna_multi);
            assertEquals(trimnbrf(result), trimnbrf(nbrf_rna_multi));
        } catch (ConversionException ex) {
            fail("could not convert to nbrf_rna_multi !");
        }
    }
}
