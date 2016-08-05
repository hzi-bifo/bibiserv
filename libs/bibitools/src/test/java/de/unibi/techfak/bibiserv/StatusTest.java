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

package de.unibi.techfak.bibiserv;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

/**
 *
 * @author jkrueger
 */
public class StatusTest {

    private BiBiTools bibitools;
    private Document param_doc;
    private String function_id;
    private String input_id;
    private Class currentclass;
    private HashMap<String, String> paramhash;
    private HashMap<String, String> inputhash;
    private final String sample_input = ">Alanine tRNA of Natronobacterium pharaonis (gb: AB003409.1/96-167)\n"
            + "GGGCCCAUAGCUCAGUGGUAGAGUGCCUCCUUUGCAAGGAGGAUGCCCUGGGUUCGAAUCCCAGUGGGUCCA";
    
    private static final File GUUGLE = new File("src/test/xml/guugle.xml");
    private static final File PARAM_GUUGLE = new File("src/test/xml/param_guugle.xml");

    public StatusTest() throws Exception {
        // get class
        currentclass = this.getClass();

        // set function id
        function_id = "rnashapes_function_1";

        // set input id
        input_id = "rnashapes_input_FASTA";

        // read parameter set from xml file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        param_doc = db.parse(new FileInputStream(PARAM_GUUGLE));

        // create Inputstream
        InputStream is = new FileInputStream(GUUGLE);

        // initalize BiBiTools
        bibitools = new BiBiTools(is);


    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty("de.unibi.techfak.bibiserv.config", "src/test/config/bibiserv_properties.xml");
        System.err.println("ClassPath   : " + System.getProperty("java.class.path"));
        System.err.println("LibraryPath : " + System.getProperty("java.library.path"));
        System.err.println("BiBiServ2Config : " + System.getProperty("de.unibi.techfak.bibiserv.config"));

        BiBiTools.setDataSource(BiBiToolsTest.getDataSource());
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
    public void testSetGetStatusCode() throws Exception {
        Status status = bibitools.getStatus();
        //
        status.setStatuscode(1999);

        if (status.getStatuscode() != 1999) {
            fail("[set|get]Status(int) failed");
        }


    }
}
