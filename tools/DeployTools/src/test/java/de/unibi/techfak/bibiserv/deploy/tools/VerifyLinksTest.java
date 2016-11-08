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

import de.unibi.techfak.bibiserv.deploy.tools.VerifyLinks.Ignore;
import org.w3c.dom.Document;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import static org.junit.Assert.*;

/**
 * Class providing tests for validating links.
 * 
 * 
 * @author @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class VerifyLinksTest {

    VerifyLinks vi;
    final static File runnableitem = new File("src/test/resources/paramtesttool.bs2");
    final static File resourcedir = new File("src/test/resources/resources");
    final static List<Ignore> ignorelist = Arrays.asList(new Ignore("http.*"),new Ignore("/.*"), new Ignore("#.*$") );

    public VerifyLinksTest() {
        try {
            vi = new VerifyLinks(runnableitem, resourcedir);
        } catch (Exception e) {
            e.printStackTrace();
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
    public void testGetImagesEntries_without_ignorelist() {
        try {
            System.out.println("test getImagesEntries(doc)");

            List<String> result_1 = VerifyLinks.list2list(VerifyLinks.getImagesEntries(vi.getRunnableitemAsDocument()));
            List<String> expected_result_1 = Arrays.asList(
                    "images/parameter_block.png",
                    "images/parameter_block.png",
                    "images/The_Earth_seen_from_Apollo_17.jpg",
                    "http://en.wikipedia.org/wiki/ICEfaces",
                    "http://de.wikipedia.org/wiki/ICEfaces",
                    "/cg-cat?viewType=manual",
                    "/cg-cat?viewType=references",
                    "http://java.sun.com/products/javawebstart/download.jsp",
                    "http://java.sun.com/javase/6/webnotes/6u11.html#rootcertificates-1.6.0_11",
                    "http://mailhide.recaptcha.net/d?k=01SmXl276guo9S44_bhjUwPA==&c=K12sZO1-T7B5Py34tWDH9eiSTcMzz6gz6z5BdAF9MCMK6wRoGjVn801PpJHHfeT6",
                    "http://www.gnu.org/licenses/gpl-3.0-standalone.html",
                    "http://mailhide.recaptcha.net/d?k=01SmXl276guo9S44_bhjUwPA==&c=K12sZO1-T7B5Py34tWDH9eiSTcMzz6gz6z5BdAF9MCMK6wRoGjVn801PpJHHfeT6",
                    "/cgi-bin/treecat_start",
                    "webstart/treecat_splash64px.gif",
                    "webstart/treecat_splash64px.gif",
                    "http://java.sun.com/products/autodl/j2se",
                    "webstart/cg-cat.jar",
                    "http://bibiserv.cebitec.uni-bielefeld.de",
                    "#test",
                    "/rnamovies?viewType=references",
                    "example/test.fas");

            if (!expected_result_1.containsAll(result_1)) {
                fail("resultlist \n(" + VerifyDownload.printList(result_1) + ")\n isn't equal to expected result list \n(" + VerifyDownload.printList(expected_result_1) + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetImagesEntries_with_ignorelist() throws Exception {
        System.out.println("test getImagesEntries(dox,ignorelist)");




        List<String> result_2 = VerifyLinks.list2list(VerifyLinks.getImagesEntries(vi.getRunnableitemAsDocument(), ignorelist));
        List<String> expected_result_2 = Arrays.asList(
                "images/parameter_block.png",
                "images/parameter_block.png",
                "images/The_Earth_seen_from_Apollo_17.jpg",
                "webstart/treecat_splash64px.gif",
                "webstart/treecat_splash64px.gif",
                "webstart/cg-cat.jar",
                "example/test.fas");



        if (!expected_result_2.containsAll(result_2)) {
            fail("resultlist (" + VerifyDownload.printList(result_2) + ") isn't equal to expected result list (" + VerifyDownload.printList(expected_result_2) + ")");
        }


    }

    @Test
    public void testCompareEntries() throws Exception {
        System.out.println("test compareEntries() [with ignorelist]");
        List<String> result_3a = VerifyLinks.list2list(VerifyLinks.getImagesEntries(vi.getRunnableitemAsDocument(), ignorelist));
        List<String> result_3b = new ArrayList<>();
        VerifyDownload.getDirEntries(resourcedir, result_3b);
        List<String> result_3c = new ArrayList<>();

        if (!VerifyLinks.compareEntries(result_3a, result_3b, result_3c)) {

           

            System.out.println("a:" + VerifyDownload.printList(result_3a));
            System.out.println("b:" + VerifyDownload.printList(result_3b));
            System.out.println("c:" + VerifyDownload.printList(result_3c));

            result_3a.removeAll(result_3c);
            fail("Entr(y|ies) " + VerifyDownload.printList(result_3a) + " missed in download dir!");
        }






    }

    @Test
    public void testReplace() throws Exception {
        System.out.println("testReplace()");

        Document doc = vi.getRunnableitemAsDocument();

        NamedNodeMap nnm = doc.getDocumentElement().getAttributes();
        Node n = nnm.getNamedItem("id");
        String id = ((Attr) n).getValue();


        List<Node> result_4 = VerifyLinks.getImagesEntries(doc,ignorelist);
        VerifyLinks.addPrefix(result_4, "applications/" + id + "/");

        System.out.println(VerifyLinks.NodeToString(doc));

    }
    
    
    @Test
    public void testRegExp(){
        String pattern = "http.*";
        
        String text1 = "http://bibiserv";
        String text2 = "/http/bibiserv";
        
        assertTrue(text1.matches(pattern));
        
        assertFalse(text2.matches(pattern));
        
    }
}
