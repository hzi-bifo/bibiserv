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
 * Contributor(s): Benjamin Paassen
 *
 */
package de.unibi.cebitec.bibiserv.search.helper;

import de.unibi.cebitec.bibiserv.search.MatchRepresentation;
import de.unibi.cebitec.bibiserv.search.Search;
import de.unibi.cebitec.bibiserv.search.index.MainIndex;
import de.unibi.cebitec.bibiserv.search.index.Occurence;
import de.unibi.cebitec.bibiserv.search.results.ExactSearchResult;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SearchScoreTest {

    private static Occurence testOcc;

    public SearchScoreTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        //index a simple test case.
        String testCase = "es glitzert des holden Knabens lockiges Haar";
        BufferedReader reader = new BufferedReader(new StringReader(testCase));
        String testIdent = "test.xhtml";
        BufferedReader[] readerArray = {reader};
        String[] identArray = {testIdent};
        Search search = Search.getInstance();
        search.removeDocument(testIdent);
        search.addDocuments(identArray, readerArray);
        ExactSearchResult result = MainIndex.searchExactly("holden");
        Collection<Occurence> occurences = result.getOccurences();
        assertTrue(occurences.size() == 1);
        testOcc = occurences.iterator().next();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAddMatch() {
        SearchScore testScore = new SearchScore(null, testOcc, 1, 0);
        ArrayList<MatchRepresentation> matches = testScore.getMatches();
        assertTrue(matches.size() == 1);
        MatchRepresentation match = matches.get(0);
        assertEquals("es glitzert des".toUpperCase(), match.getContextPrefix());
        assertEquals("holden".toUpperCase(), match.getMatchedContent());
        assertEquals("Knabens lockiges Haar".toUpperCase(), match.getContextSuffix());
    }

    /**
     * Test of compareTo method, of class SearchScore.
     */
    @Test
    public void testCompareTo() {
        SearchScore score1 = new SearchScore(null, testOcc, 1, 10);
        SearchScore score2 = new SearchScore(null, testOcc, 1, 20);
        assertTrue(score1.compareTo(score2) < 0);
        assertTrue(score2.compareTo(score1) > 0);
        score2.addMatch(testOcc, 1);
        assertTrue(score1.compareTo(score2) > 0);
        assertTrue(score2.compareTo(score1) < 0);
        SearchScore score3 = new SearchScore(null, testOcc, 1, 10);
        assertTrue(score1.compareTo(score3) == 0);
        assertTrue(score3.compareTo(score1) == 0);
        score3.addMatch(testOcc, 1);
        score3.addMatch(testOcc, 1);
        ArrayList<SearchScore> testList = new ArrayList<>();
        testList.add(score1);
        testList.add(score2);
        testList.add(score3);
        Collections.sort(testList);
        SearchScore[] expected = {score3, score2, score1};
        assertArrayEquals(expected, testList.toArray(new SearchScore[testList.size()]));
    }
}
