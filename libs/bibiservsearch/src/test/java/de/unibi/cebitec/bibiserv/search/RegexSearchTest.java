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
package de.unibi.cebitec.bibiserv.search;

import de.unibi.cebitec.bibiserv.search.exceptions.InvalidWordException;
import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.index.SuffixTreeAppendSession;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This test does some regular expression searching.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class RegexSearchTest {
    
    private static final Search search = Search.getInstance();
    private static final String[] testidents = {"ident1", "ident2"};
    private static final String[] testcontent = {"aaaaab test", "baaaaaaac"};
    
    public RegexSearchTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        search.reset();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws SuffixTreeException {
        //index some words.
        SuffixTreeAppendSession session = new SuffixTreeAppendSession();
        session.open();
        for (int i = 0; i < testidents.length; i++) {
            search.addDocument(testidents[i],
                    new BufferedReader(new StringReader(testcontent[i])), session);
        }
        session.close();
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testRegexQueries() throws InvalidWordException {
        //search for a+
        String query = "!a*a!";
        ArrayList<String> expectedMatches = new ArrayList<>();
        expectedMatches.add("aaaaab");
        expectedMatches.add("baaaaaaac");
        assertMatches(query, expectedMatches);
        //search for a *-chain.
        query = "!a*e*b*[bsc]!";
        expectedMatches.add("test");
        assertMatches(query, expectedMatches);
    }
    
    public void assertMatches(String query, ArrayList<String> expectedWords)
            throws InvalidWordException {
        List<OutputSearchResult> results = search.search(query);
        HashSet<String> resultWords = new HashSet<>();
        for (OutputSearchResult result : results) {
            for (MatchRepresentation match : result.getMatches()) {
                resultWords.add(match.getMatchedContent());
            }
        }
        for (String word : expectedWords) {
            assertTrue(word + " was not found in results for query " + query,
                    resultWords.contains(word.toUpperCase()));
        }
    }
}
