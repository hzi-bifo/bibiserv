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
import de.unibi.cebitec.bibiserv.util.convert.impl.FASTQALL2FASTA;
import de.unibi.cebitec.bibiserv.util.streamconvert.AbstractTest;
import java.io.FileReader;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests to conversion of all FASTQ variants to fasta
 * (quality score gets lost)
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class FASTQALL2FASTATest extends AbstractTest {

    private String sanger;
    private String fasta;

    public FASTQALL2FASTATest() {
        try {
            sanger = readFromResource("/sanger_full_range_original_sanger.fastq");
            fasta = readFromResource("/sanger_full_range_as_fasta.fastq");
        } catch (IOException ex) {
            fail("could not read testfiles : " + ex.getMessage());
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
    public void testFastq2FastaConversion() throws ConversionException {
         FASTQALL2FASTA converter = new FASTQALL2FASTA();
         String result = (String) converter.convert(sanger);
         assertEquals(result,fasta);
    }
    
    
}
