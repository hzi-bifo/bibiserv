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

import de.unibi.cebitec.bibiserv.util.visualizer.AbstractTest;
import de.unibi.techfak.bibiserv.BiBiTools;
import java.io.FileInputStream;
import java.util.Scanner;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Benjamin Paassen 
 * 
 * This class tests the RAEDA-Viewer implementation.
 * For that purpose, a rudimentary database and logging-system is set up
 * (BiBiToolTest-methods are used here).
 * Before testing, please make sure that a RAEDA.jar isavaliable at the same
 * folder as the TestRAEDA_result.html-page.
 * Also make sure that bibiserv_properties.xml, log4j.properties and status.sql
 * are available in the test-folder.
 */
public class TestRAEDA extends AbstractTest{

    private DocumentBuilder db;
    private Class currentclass;
    private static final Logger log = Logger.getLogger(TestRAEDA.class);
    //

    public TestRAEDA() throws Exception {
        // get class
        currentclass = this.getClass();

        // read parameter set from xml file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        db = dbf.newDocumentBuilder();

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        log.info("ClassPath   : " + System.getProperty("java.class.path"));
        log.info("LibraryPath : " + System.getProperty("java.library.path"));
        log.info("BiBiServ2Config : " + System.getProperty("de.unibi.techfak.bibiserv.config"));
        // set DataSource
        BiBiTools.setDataSource(AbstractTest.getDataSource());
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
     * Test of showThis method of class RAEDA.
     * @throws java.lang.Exception
     */
    @Test
    public void testShowThis() throws Exception {
        RAEDA raedaVisualizer = new RAEDA();

        StringBuilder inputData = new StringBuilder();
        String NL = System.getProperty("line.separator");

        /*
         * The input-alignment-file is specified here.
         * Please correct the path if necessary!
         * If you do not know where the relative path leads, please use the
         * following line of code
         */

       


        System.out.println("Now start scanning of test/TestRAEDA_input.xml");


        Scanner scanner = new Scanner(new FileInputStream("src/test/resources/TestRAEDA_input.xml"), "UTF-8");

        while (scanner.hasNextLine()) {
            inputData.append(scanner.nextLine());
            inputData.append(NL);
        }
        scanner.close();


        System.out.println("Content of input-file:\n" + inputData.toString() + "\n\n\n");



        String container = raedaVisualizer.showThis(inputData.toString());

        System.out.println("Div-Container calling the RAEDA-Visualizer");

        String output = "<html>\n";
        output += "<head>\n";
        output += "<title>RAEDA Testpage</title>\n";
        output += "</head>\n";
        output += "<body>\n";
        output += container;
        output += "</body>\n";
        output += "</html>\n";

        //TestRAEDA_result.html-page is created.
        //Please correct path if necessary!


        System.out.println("Creating result-page named test/TestRAEDA_result.html");


        File f = new File("test/TestRAEDA_result.html");

        Writer w = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
        w.write(output);
        w.flush();
        w.close();

        System.out.println("Successfully created result-page.");


    }



    
}
