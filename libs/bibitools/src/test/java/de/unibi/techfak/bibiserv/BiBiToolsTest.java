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

import org.apache.log4j.Logger;
import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import de.unibi.techfak.bibiserv.util.LogShed;
import de.unibi.techfak.bibiserv.util.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit Test(s) for BiBiTools
 *
 *
 * @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de
 */
public class BiBiToolsTest {

    private DocumentBuilder db;
    private Class currentclass;
    
    private static final File GUUGLE = new File("src/test/xml/guugle.xml");
    private static final File PARAM_GUUGLE = new File("src/test/xml/param_guugle.xml");
    
    private static final File TEST = new File("src/test/xml/paramtesttool.xml");
    private static final File PARAM_TEST = new File("src/test/xml/param_paramtesttool.xml");

    private static final Logger LOG = Logger.getLogger(BiBiToolsTest.class);

    public BiBiToolsTest() throws Exception {
        // get class
        currentclass = this.getClass();

        // read parameter set from xml file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        db = dbf.newDocumentBuilder();

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
        System.setProperty("de.unibi.techfak.bibiserv.config", "src/test/config/bibiserv_properties.xml");
        LOG.info("ClassPath   : " + System.getProperty("java.class.path"));
        LOG.info("LibraryPath : " + System.getProperty("java.library.path"));
        LOG.info("BiBiServ2Config : " + System.getProperty("de.unibi.techfak.bibiserv.config"));
        // set DataSource
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
    public void testCheckAndParseParam() throws Exception {
        LOG.info("+++++++++++++++++++\ntest checkAndParseParam\n+++++++++++++++++++\n");

        // initalize BiBiTools object
        InputStream is = new FileInputStream(TEST);
        BiBiTools bibitools = new BiBiTools(is);

        //  create new LogShed
        LogShed logshed = new LogShed();

        // load sample parameter 
        InputStream checkAndParseParamInput = new FileInputStream(PARAM_TEST);
        

        List<Pair<String, String>> list_param = BiBiTools.paramdoc2List(db.parse(checkAndParseParamInput));

        // create parmater hash using checkAndParseParam ...
        Map<String, String> paramhash = bibitools.checkAndParseParam(list_param, "parametertesttool_function_0", logshed);
        // print out logshed messages 
        LOG.info("logshed messages : \n" + logshed.toString());

        /*<param>
        <parametertesttool_param_boolean>true</parametertesttool_param_boolean>
        <parametertesttool_param_boolean2>false</parametertesttool_param_boolean2>
        <parametertesttool_param_int>42</parametertesttool_param_int>
        <!-- <parametertesttool_param_int_min_max_default>50</parametertesttool_param_int_min_max_default> -->
        <parametertesttool_param_float>0.5</parametertesttool_param_float>
        <!-- <parametertesttool_param_float_max_min_default>99.9</parametertesttool_param_float_max_min_default> -->
        <parametertesttool_param_string>test</parametertesttool_param_string>
        <parametertesttool_param_string_regexp>32278 Kirchlengern</parametertesttool_param_string_regexp>
        <parametertesttool_param_string_min_max>abcdefghijklmn</parametertesttool_param_string_min_max>
        <!-- <parametertesttool_param_string_regexp_default>ACGU</parametertesttool_param_string_regexp_default> -->
        ... */
 /*parametertesttool_param_boolean - true */
        if (paramhash.containsKey("parametertesttool_param_boolean") && paramhash.get("parametertesttool_param_boolean").trim().equals("-boolean")) {
            LOG.info("Found : \"parametertesttool_param_boolean\"-\"-boolean\"");
        } else {
            fail("Expected : \"parametertesttool_param_boolean\"-\"-boolean\"");
        }

        /*parametertesttool_param_boolean2 - false*/
        if (paramhash.containsKey("parametertesttool_param_boolean2") && paramhash.get("parametertesttool_param_boolean2").trim().equals("")) {
            LOG.info("Found : \"parametertesttool_param_boolean2\"-\"\"");
        } else {
            fail("Expected : \"parametertesttool_param_boolean2\"-\"\"");
        }

        /*parametertesttool_param_int - 42 */
        if (paramhash.containsKey("parametertesttool_param_int") && paramhash.get("parametertesttool_param_int").trim().equals("-int42")) {
            LOG.info("Found :\"parametertesttool_param_int\"-\"-int42\"");
        } else {
            fail("Expected :\"parametertesttool_param_int\"-\"-int42\"");
        }

        /* parametertesttool_param_int_min_max_default - default : 50 // comment out in source, must be inserted by funtcion because of set default value*/
        if (paramhash.containsKey("parametertesttool_param_int_min_max_default") && paramhash.get("parametertesttool_param_int_min_max_default").trim().equals("-int_max_min_default50")) {
            LOG.info("Found :\"parametertesttool_param_int_min_max_default\"-\"-int_max_min_default50\"");
        } else {
            fail("Expected :\"parametertesttool_param_int_min_max_default\"-\"-int_max_min_default50\"");
        }

        /* parametertesttool_param_float - 0.5 */
        if (paramhash.containsKey("parametertesttool_param_float") && paramhash.get("parametertesttool_param_float").trim().equals("-float0.5")) {
            LOG.info("Found :\"parametertesttool_param_float\"-\"-float0.5\"");
        } else {
            fail("Expected :\"parametertesttool_param_float\"-\"-float0.5\"");
        }

        /* parametertesttool_param_float_max_min_default - default : 99.9 // comment out in source, must be inserted by function because of set default value*/
        if (paramhash.containsKey("parametertesttool_param_float_max_min_default") && paramhash.get("parametertesttool_param_float_max_min_default").trim().equals("-float_min_max_default99.9")) {
            LOG.info("Found :\"parametertesttool_param_float_max_min_default\"-\"-float_min_max_default99.9\"");
        } else {
            fail("Expected :\"parametertesttool_param_float\"-\"-float_min_max_default99.9\"");
        }

        /* parametertesttool_param_string - test */
        if (paramhash.containsKey("parametertesttool_param_string") && paramhash.get("parametertesttool_param_string").trim().equals("-stringtest")) {
            LOG.info("Found :\"parametertesttool_param_string\"-\"-stringtest\"");
        } else {
            fail("Expected :\"parametertesttool_param_string\"-\"-stringtest\"");
        }

        /* parametertesttool_param_string_regexp - 32278 Kirchlengern */
        if (paramhash.containsKey("parametertesttool_param_string_regexp") && paramhash.get("parametertesttool_param_string_regexp").trim().equals("-string_regexp32278 Kirchlengern")) {
            LOG.info("Found :\"parametertesttool_param_string_regexp\"-\"-string_regexp32278 Kirchlengern\"");
        } else {
            fail("Expected :\"parametertesttool_param_string_regexp\"-\"-string_regexp32278 Kirchlengern\"");
        }

        /* parametertesttool_param_string_min_max - abcdefghijklmn // no option*/
        if (paramhash.containsKey("parametertesttool_param_string_min_max") && paramhash.get("parametertesttool_param_string_min_max").trim().equals("abcdefghijklmn")) {
            LOG.info("Found :\"parametertesttool_param_string_min_max\"-\"abcdefghijklmn\"");
        } else {
            fail("Expected :\"parametertesttool_param_string_min_max\"-\"abcdefghijklmn\"");
        }

        /* parametertesttool_param_string_regexp_default - default : ACGU // comment out in source, must be inserted by function because of set default value */
        if (paramhash.containsKey("parametertesttool_param_string_regexp_default") && paramhash.get("parametertesttool_param_string_regexp_default").trim().equals("-string_regexp_defaultACGU")) {
            LOG.info("Found :\"parametertesttool_param_string_regexp_default\"-\"-string_regexp_defaultACGU\"");
        } else {
            fail("Expected :\"parametertesttool_param_string_regexp_default\"-\"-string_regexp_defaultACGU\"");
        }

        if (paramhash.keySet().size() != 10) {
            fail("Expected 10 'normal' parameter  (key/value pairs), found " + paramhash.keySet().size());
        }

        /* test function_1 with enum parameter */

 /* ...
        <parametertesttool_enum_selectoneradio>DE</parametertesttool_enum_selectoneradio>
        <parametertesttool_enum_selectonelistbox>NL</parametertesttool_enum_selectonelistbox>
        <parametertesttool_enum_selectonemenu>US</parametertesttool_enum_selectonemenu>
        <parametertesttool_enum_selectmanycheckbox>DE NL US</parametertesttool_enum_selectmanycheckbox>
        <!-- <parametertesttool_enum_selectmanylistbox>DE US SE</parametertesttool_enum_selectmanylistbox> -->
        <parametertesttool_enum_selectmanymenu>DE SE</parametertesttool_enum_selectmanymenu> 
        </param>*/
        logshed = new LogShed();
        // create parmater hash using checkAndParseParam ...
        try {
            
            paramhash = bibitools.checkAndParseParam(list_param, "parametertesttool_function_1", logshed);
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        // print out logshed messages
        LOG.info("logshed messages : \n" + logshed.toString());


        /* parametertesttool_enum_selectoneradio - DE*/
        if (paramhash.containsKey("parametertesttool_enum_selectoneradio") && paramhash.get("parametertesttool_enum_selectoneradio").trim().equals("-lang_sor DE")) {
            LOG.info("Found :\"parametertesttool_enum_selectoneradio\"-\"-lang_sor DE\"");
        } else {
            fail("Expected :\"parametertesttool_enum_selectoneradio\"-\"-lang_sor DE\"");
        }

        /* parametertesttool_enum_selectonelistbox - NL // default US*/
        if (paramhash.containsKey("parametertesttool_enum_selectonelistbox") && paramhash.get("parametertesttool_enum_selectonelistbox").trim().equals("-lang_sol NL")) {
            LOG.info("Found :\"parametertesttool_enum_selectonelistbox\" - \"-lang_sol NL\"");
        } else {
            fail("Expected :\"parametertesttool_enum_selectonelistbox\" - \"-lang_sol NL\"");
        }

        /* parametertesttool_enum_selectonemenu - US */
        if (paramhash.containsKey("parametertesttool_enum_selectonemenu") && paramhash.get("parametertesttool_enum_selectonemenu").trim().equals("-lang_som US")) {
            LOG.info("Found :\"parametertesttool_enum_selectonemenu\" - \"-lang_som US\"");
        } else {
            fail("Expected :\"parametertesttool_enum_selectonemenu\" - \"-lang_som US\"");
        }

        /* parametertesttool_enum_selectmanycheckbox -DE NL US // maxoccurrs 3 , separator ','*/
        if (paramhash.containsKey("parametertesttool_enum_selectmanycheckbox") && paramhash.get("parametertesttool_enum_selectmanycheckbox").trim().equals("-lang_smc DE,NL,US")) {
            LOG.info("Found :\"parametertesttool_enum_selectmanycheckbox\" - \"-lang_smc DE,NL,US\"");
        } else {
            fail("Expected :\"parametertesttool_enum_selectmanycheckbox\" - \"-lang_smc DE,NL,US\"");
        }
        /* parametertesttool_enum_selectmanylistbox - default DE US SE, maxOccurs 3, separator ',' */
        if (paramhash.containsKey("parametertesttool_enum_selectmanylistbox") && paramhash.get("parametertesttool_enum_selectmanylistbox").trim().equals("-lang_sml DE,US,SE")) {
            LOG.info("Found :\"parametertesttool_enum_selectmanylistbox\" - \"-lang_sml DE,US,SE\"");
        } else {
            fail("Expected :\"parametertesttool_enum_selectmanylistbox\" - \"-lang_sml DE,US,SE\"");
        }

        /* parametertesttool_enum_selectmanymenu - DE SE // maxoccurs 3, separator '+', prefix '[', suffix ']' */
        if (paramhash.containsKey("parametertesttool_enum_selectmanymenu") && paramhash.get("parametertesttool_enum_selectmanymenu").trim().equals("-lang_smm [DE+SE]")) {
            LOG.info("Found :\"parametertesttool_enum_selectmanymenu\" - \"-lang_smm [DE+SE]\"");
        } else {
            fail("Expected :\"parametertesttool_enum_selectmanymenu\" - \"-lang_smm [DE+SE]\"");
        }

        if (paramhash.keySet().size() != 6) {
            fail("Expected 6 'normal' parameter  (key/value pairs), found " + paramhash.keySet().size());
        }
    }

    @Test
    public void testParseInput() throws Exception {
        LOG.info("+++++++++++++++++++\ntest parseInput\n+++++++++++++++++++\n");

        // initalize BiBiTools object
        InputStream is = new FileInputStream(GUUGLE);
        BiBiTools bibitools = new BiBiTools(is);

        Map<String, String> hash = new HashMap<String, String>();
        String postfix;

        try {
            postfix = bibitools.parseInput("guugle_input_querysequence", hash, ">id1\nACGUACGAUCGUCGUACAGCUAGCUA", "PRIMITIVE", "java.lang.String");
            postfix += bibitools.parseInput("guugle_input_targetsequence", hash, ">id2\nACGUACUGUGCGAUCUAUCGUUGU", "PRIMITIVE", "java.lang.String");
        } catch (BiBiToolsException e) {
            fail(e.getMessage());
        }

        if (hash.size() != 2) {
            fail("Expected 2 inputs, found " + hash.size());
        }

        if (!hash.get("guugle_input_querysequence").equals(bibitools.getSpoolDir() + File.separator + "guugle_input_querysequence.FILE")) {
            fail("Expected \n'" + bibitools.getSpoolDir() + File.separator + "guugle_input_querysequence.FILE' for input 'guugle_input_querysequence' found \n'" + hash.get("guugle_input_querysequence") + "'");
        }

        if (!hash.get("guugle_input_targetsequence").equals(bibitools.getSpoolDir() + File.separator + "guugle_input_targetsequence.FILE")) {
            fail("Expected \n'" + bibitools.getSpoolDir() + File.separator + "guugle_input_targetsequence.FILE' for input 'guugle_input_targetsequence' found \n'" + hash.get("guugle_input_targetsequence") + "'");
        }

    }

    @Test
    public void testGenerateCmdLineString() throws Exception {
        LOG.info("+++++++++++++++++++\ntestgenerateParameterString\n+++++++++++++++++++\n");

        String prefix = "";
        String postfix = "";
        LogShed logshed = new LogShed();

        // initalize BiBiTools object
        InputStream is = new FileInputStream(GUUGLE);
        BiBiTools bibitools = new BiBiTools(is);

        // load sample parameter
        List<Pair<String, String>> list_param;
        list_param = BiBiTools.paramdoc2List(db.parse(new FileInputStream(PARAM_GUUGLE)));

        // create parameter hash using checkAndParseParam ...
        Map<String, String> hash = bibitools.checkAndParseParam(list_param, "guugle_function_0", logshed);
        // print out logshed messages
        LOG.info("logshed messages : \n" + logshed.toString());

        // parse Inputs (without validation) and add  them to hash
        try {
            postfix = bibitools.parseInput("guugle_input_querysequence", hash, ">id1\nACGUACGAUCGUCGUACAGCUAGCUA", "PRIMITIVE", "java.lang.String");
            postfix += bibitools.parseInput("guugle_input_targetsequence", hash, ">id2\nACGUACUGUGCGAUCUAUCGUUGU", "PRIMITIVE", "java.lang.String");
        } catch (BiBiToolsException e) {
            fail(e.getMessage());
        }

        // generate cmdline
        String result = bibitools.generateCmdLineString("guugle_function_0", hash, prefix, postfix, false);

        // and print it
        LOG.info(result);

    }

    @Test
    public void testSpoolDirCreation() throws Exception {
        LOG.info("+++++++++++++++++++\ntest creation of spooldir and temp spooldir\n+++++++++++++++++++\n");
        // initalize BiBiTools object
        InputStream is = new FileInputStream(GUUGLE);
        BiBiTools bibitools = new BiBiTools(is);
        // create new spooldir
        File spooldir = bibitools.getSpoolDir();
        if (!(spooldir.exists() && spooldir.isDirectory())) {
            fail("spooldir does not exist or is not a directory ...");
        }
        // create some subdirectories within spooldir
        File subdir1 = new File(spooldir, "subdir_1");
        subdir1.mkdir();
        File subdir2 = new File(spooldir, "subdir_2");
        subdir2.mkdir();
        File subsubdir1 = new File(subdir1, "subsubdir_1");
        subsubdir1.mkdir();

        LOG.info("Please check manually if '" + spooldir.toString() + "' filemodes has mode '" + bibitools.getProperty("chmod.param") + "'!");
        File tmpdir = bibitools.getTmpDir();
        if (!(tmpdir.exists() && tmpdir.isDirectory())) {
            fail("tmpspooldir does not exist or is not a directory ...");
        }
        // create some subdirectory within tmpdir and create an file
        File subtmpdir = new File(tmpdir, "subdir_1");
        subtmpdir.mkdir();
        bibitools.writeSpoolFile(new File("file"), "some content");
        LOG.info("Please check manually if '" + tmpdir.toString() + "' filemodes has mode '" + bibitools.getProperty("chmod.param") + "'!");

    }

    @Test
    public void testGetFileSize() throws Exception {
        LOG.info("+++++++++++++++++++\ntest creation of spooldir and temp spooldir\n+++++++++++++++++++\n");
        // initalize BiBiTools object
        InputStream is = new FileInputStream(GUUGLE);
        BiBiTools bibitools = new BiBiTools(is);
        // run du from system

        long expected_size = 0;

        Process p = Runtime.getRuntime().exec("du -s --bytes .");
        if (p.waitFor() == 0) {
            String tmp = BiBiTools.i2s(new InputStreamReader(p.getInputStream()));
            expected_size = Long.parseLong(tmp.split("\\s")[0]);
        } else {
            fail(BiBiTools.i2s(new InputStreamReader(p.getErrorStream())));
        }

        long size = bibitools.getFileSize(new File("."));

        assertEquals(expected_size, size);

    }
    private static DataSource datasource = null;

    public static DataSource getDataSource() throws Exception {
        if (datasource == null) {
            datasource = derbydb();
        }
        return datasource;
    }

    private static DataSource derbydb() throws Exception {

        EmbeddedDataSource ds = new EmbeddedDataSource();

        String db = "test/testdb_"+System.currentTimeMillis();
        

        // check if database exists
        File db_dir = new File(db);
        if (db_dir.exists()) {
            try {
                FileUtils.deleteDirectory(db_dir);
            } catch (IOException e) {
                assertTrue(e.getMessage(),false);
            }
        }
        ds.setDatabaseName(db);
        ds.setCreateDatabase("create");

        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();

        // read SQL Statement from file
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("src/test/config/status.sql")));
        String line;
        StringBuilder sql = new StringBuilder();
        while ((line = r.readLine()) != null) {
            // skip commend lines
            if (!line.startsWith("--")) {
                sql.append(line);
                sql.append('\n');
            }
        }

        r.close();

        // execute sqlcmd's
        for (String sqlcmd
                : sql.toString()
                .split(";")) {
            sqlcmd = sqlcmd.trim(); // ignore trailing/ending whitespaces
            sqlcmd = sqlcmd.replaceAll("\n\n", "\n"); // remove double newline
            if (sqlcmd.length() > 1) { // if string contains more than one char, execute sql cmd
                LOG.debug(sqlcmd + "\n");
                stmt.execute(sqlcmd);
            }
        }

        // close stmt
        stmt.close();

        return ds;
    }
}
