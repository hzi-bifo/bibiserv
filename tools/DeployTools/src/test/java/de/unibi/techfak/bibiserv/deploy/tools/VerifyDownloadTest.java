/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2016 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2016 BiBiServ"
 *
 * Contributor(s): Jan Krüger
 *
 */
package de.unibi.techfak.bibiserv.deploy.tools;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.apache.tools.ant.BuildException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Class providing test methods for VerifyDownload Class.
 *
 *
 * @author Jan Krueger -  jkrueger(at)cebitec.uni-bielefeld.de
 */
public class VerifyDownloadTest {

    VerifyDownload vd;
    File runnableitem = new File("src/test/resources/paramtesttool.bs2");
    File downloaddir = new File("src/test/resources/resources/downloads");

//    File runnableitem = new File("/vol/bibi/share/bibiserv2_central/jkrueger/apps/guugle_20120207135611/config/runnableitem.xml");
//    File downloaddir = new File("/vol/bibi/share/bibiserv2_central/jkrueger/apps/guugle_20120207135611/resources/downloads");
    
    public VerifyDownloadTest() {
        try {
            vd = new VerifyDownload(runnableitem, downloaddir);
            vd.initialize();
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (JAXBException e) {
            fail(e.getMessage());
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testDirEntries() {
        List<String> resultlist = new ArrayList<String>();
        VerifyDownload.getDirEntries(downloaddir, resultlist);
        // list should contain three entries

        List<String> expected_resultlist = new ArrayList<String>();
        expected_resultlist.add("file1.txt");
        expected_resultlist.add("file2.txt");
        expected_resultlist.add("subdir/file3.txt");


        if (!resultlist.containsAll(expected_resultlist)) {
            fail("resultlist (" + VerifyDownload.printList(resultlist) + ") isn't equal to expected result list (" + VerifyDownload.printList(expected_resultlist));
        }

    }

    @Test
    public void testDownloadEntries() {
        List<String> resultlist = new ArrayList<String>();
        vd.getDownloadableEntries(resultlist);

        List<String> expected_resultlist = new ArrayList<String>();
        expected_resultlist.add("file1.txt");

        if (!resultlist.containsAll(expected_resultlist)) {
            fail("resultlist (" + VerifyDownload.printList(resultlist) + ") isn't equal to expected result list (" + VerifyDownload.printList(expected_resultlist));
        }

    }

    @Test
    public void testCompareEntries() {
        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        List<String> resultlist = new ArrayList<String>();

        VerifyDownload.getDirEntries(downloaddir, list1);
        vd.getDownloadableEntries(list2);

        System.out.println("list 1 ::"+VerifyDownload.printList(list1));
        System.out.println("list 2 ::"+VerifyDownload.printList(list2));

        // compareEntries should return false
        if (VerifyDownload.compareEntries(list1, list2, resultlist)) {
            fail("Since list1 and list2 aren't equal, compareEntries should return false!");
        }

        // list1 should contain two elements
        if (list1.size() != 2) {
            fail("List1 should contain two list elements, but contains (" + VerifyDownload.printList(list1) + ")");
        }

        // list2 should be empty
        if (!list2.isEmpty()) {
            fail("List2 should be empty, but contains (" + VerifyDownload.printList(list2) + ")");
        }

        // resultlist shoudl could contain one elment ("file1.txt")
        if (resultlist.size() != 1 && !resultlist.get(0).equals("file1.txt")) {
            fail("Expected one element named \"file1.txt\" in resultlist, got " + resultlist.size() + " element(s) containing " + VerifyDownload.printList(resultlist));
        }




    }

    @Test
    public void testExecute() {
        vd = new VerifyDownload();
        try {
            vd.setDownloaddir(downloaddir);
            vd.setRunnableitemfile(runnableitem);
            vd.execute();
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (JAXBException e) {
            fail(e.getMessage());
        } catch (BuildException e) {
            System.out.println(e.getMessage());
        }
    }
}
