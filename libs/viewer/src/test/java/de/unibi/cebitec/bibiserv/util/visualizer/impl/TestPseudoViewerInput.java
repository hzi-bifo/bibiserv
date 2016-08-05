/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010,2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010,2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.dev"
 *
 * Contributor(s): Jan Krueger, Benjamin Paassen
 *
 */
package de.unibi.cebitec.bibiserv.util.visualizer.impl;

import java.io.FileInputStream;
import java.util.Scanner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Benjamin Paassen
 * 
 * Tests the building of valid SOAP-messages for the PseudoViewerWebService
 */
public class TestPseudoViewerInput {

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
     * Test of createSOAP-method in PseudoViewer.java
     */
    @Test
    public void testCreateSOAP() throws Exception {

        PseudoViewer pseudoViewer = new PseudoViewer();

        StringBuilder inputData = new StringBuilder();

        /*
         * The input-file is specified here.
         * Please correct the path if necessary!
         * If you do not know where the relative path leads, please use the
         * following line of code
         */

        // System.out.println((new File("test/TestPseudoViewer_output.xml")).getAbsolutePath());

        System.out.println("Now start scanning of test/TestPseudoViewer_input.xml");

        Scanner scanner = new Scanner(new FileInputStream("src/test/resources/TestPseudoViewer_input.xml"), "UTF-8");

        // Now the input-data is scanned.

        String NL = System.getProperty("line.separator");

        while (scanner.hasNextLine()) {
            inputData.append(scanner.nextLine());
            inputData.append(NL);
        }
        scanner.close();

        System.out.println("Content of input-file:\n" + inputData.toString() + "\n\n\n");

        System.out.println("Now start parsing of input-data and constructing the SOAP-message.\n\n");
        
        // Input-data is given to the createSOAP-method of PseudoViewer.java.

        String soapInputMessage = pseudoViewer.createSOAP(inputData.toString());

        System.out.println("SOAP-message: \n\n" + soapInputMessage);

    }
}
