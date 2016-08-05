/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Benjamin Paassen, CeBiTec,
 * http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 Benjamin Paassen"
 *
 * Contributor(s): Benjamin Paassen, Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.search;

import de.unibi.cebitec.bibiserv.search.exceptions.InvalidWordException;
import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.index.MainIndex;
import de.unibi.cebitec.bibiserv.search.index.SuffixTreeAppendSession;
import de.unibi.cebitec.bibiserv.search.util.Collections;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;

/**
 * JUnit Test class for class Search
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SearchTest {

    protected static final String TESTDIR = "test/data";
    private Search search;
    List<Integer> indexsize = new ArrayList<>();
    private static List<URL> testurls = new ArrayList<>();

    public SearchTest() throws MalformedURLException {
        // get instance of search class
        search = Search.getInstance();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        File testdir = new File(TESTDIR);
        for (String fn : testdir.list(new FileFilter())) {
            testurls.add(new URL("file:" + testdir.toString() + "/" + fn));
            System.out.println("Add testfile : " + testdir.toString() + "/" + fn);
        }

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        System.out.println("Set Up index");
        // clear up 
        indexsize.clear();
        MainIndex.reset();
        // at beginning the index size is 0;
        indexsize.add(0);
        // now add all url from testurls list
        for (URL url : testurls) {
            URL[] wrapperArray = {url};
            search.addDocuments(wrapperArray);
            indexsize.add(search.getNumberOfWords());
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testremoveURL() {
        System.out.println("remove documents from index ... ");
        for (int i = testurls.size(); i > 0; --i) {
            URL url = testurls.get(i - 1);
            search.removeDocument(url);
            // check index against previous determined index sizes
            System.out.println("Index contains " + search.getNumberOfWords() + " words !");
            Assert.assertEquals((int) indexsize.get(i - 1), search.getNumberOfWords());
        }
    }

    @Test
    public void testremoveAllURL() {
        System.out.println("remove a list of documents from index ...");
        try {
            search.removeAllDocument(new URL("file:test/data").toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(SearchTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // should be empty : all URLs starts with file:test/data
        System.out.println("Index contains " + search.getNumberOfWords() + " words !");
        Assert.assertEquals(0, search.getNumberOfWords());
    }

    @Test
    public void testSearch() throws InvalidWordException {
        System.out.println("Call testSearch ...");
        List<String> resultset = getIdentResults("\"necessary\"");
        String[] expected = {"file:test/data/linkeditem.xml",
            "file:test/data/runnableitem.xml",
            "file:test/data/handbuch.pdf"};
        assertSearchResults(Arrays.asList(expected), resultset);
    }

    @Test
    public void testExactSearch() throws InvalidWordException {
        System.out.println("Call testExactSearch ...");
        List<String> resultset = getIdentResults("\"funktionieren daher\"");
        String[] expected = {"file:test/data/structure.xml"};
        assertSearchResults(Arrays.asList(expected), resultset);
    }

    @Test
    public void testPartWordSearch() throws InvalidWordException {
        System.out.println("Call testPartWordSearch ...");
        List<String> resultset = getIdentResults("!eces!");
        String[] expected = {"file:test/data/runnableitem.xml",
            "file:test/data/linkeditem.xml",
            "file:test/data/handbuch.pdf"};
        assertSearchResults(Arrays.asList(expected), resultset);
    }

    @Test
    public void testInexactSearch() throws InvalidWordException {
        System.out.println("Call testInexactSearch ...");
        List<String> resultset = getIdentResults("ertsellung");
        String[] expected = {"file:test/data/structure.xml",
            "file:test/data/handbuch.pdf"};
        assertSearchResults(Arrays.asList(expected), resultset);
    }

    /**
     * This is a special search case because chars like ä do not match the
     * used word regex \\w+. Therefore it is not clear that all search
     * algorithms find results you would expect them to find.
     */
    @Test
    public void testUmlautSearch() throws InvalidWordException, SuffixTreeException {
        //append a test word.
        SuffixTreeAppendSession session = new SuffixTreeAppendSession();
        session.open();
        String testIdent = "umlautIdent";
        String testUmlautString = "geäußert";
        BufferedReader testReader = new BufferedReader(new StringReader(testUmlautString));
        search.addDocument(testIdent, testReader, session);
        session.close();
        //search for the String in all search modes.
        ArrayList<String> expectedIdent = new ArrayList<>();
        expectedIdent.add(testIdent);
        List<String> resultset = getIdentResults("\"" + testUmlautString + "\"");
        assertSearchResults(expectedIdent, resultset);
        resultset = getIdentResults("!" + testUmlautString + "!");
        assertSearchResults(expectedIdent, resultset);
        resultset = getIdentResults(testUmlautString);
        assertSearchResults(expectedIdent, resultset);
    }

    private List<String> getIdentResults(String pattern) throws InvalidWordException {
        return Collections.transform(
                search.search(pattern),
                new ExtractStringFromSearchResultTransformation());
    }

    private void assertSearchResults(List<String> expectedIdents, List<String> actualIdents) {
        HashSet<String> expected = new HashSet<>(expectedIdents);
        HashSet<String> actual = new HashSet<>(actualIdents);
        Assert.assertTrue(expected.size() == actual.size());
        for (String exp : expected) {
            Assert.assertTrue("Result set does not contain expected String "
                    + exp, actual.contains(exp));
        }
    }
}

class FileFilter implements FilenameFilter {

    public FileFilter() {
        super();
    }

    @Override
    public boolean accept(File dir, String name) {
        return (new File(SearchTest.TESTDIR + "/" + name)).isFile();
    }
}