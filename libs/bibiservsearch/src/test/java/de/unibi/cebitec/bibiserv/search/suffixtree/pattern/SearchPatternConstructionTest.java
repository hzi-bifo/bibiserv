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
package de.unibi.cebitec.bibiserv.search.suffixtree.pattern;

import de.unibi.cebitec.bibiserv.search.exceptions.MalformedRegularExpressionException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * Attention: Remove test .. previous  test compares a generated suffixtree with an
 * manual generated string using the suffixtree toString Method. The result of the toString
 * is not unique because the data structure behind the suffix tree bases on Collections.
 * 
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SearchPatternConstructionTest {

    public SearchPatternConstructionTest() {
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
    public void testInvalidPatternConstruction()
            throws MalformedRegularExpressionException {
        assertInvalid(".*");
        assertInvalid("[^]*");
        assertInvalid("a*b*c*d*");
    }

    private void assertInvalid(String regex) throws MalformedRegularExpressionException {

        assertFalse("regular expression " + regex + " was considered valid, but it is not.",
                SearchPattern.validate(regex));
        try {
            SearchPattern.compile(regex);
            fail("regular expression " + regex + " was considered valid, but it is not.");
        } catch (MalformedRegularExpressionException ex) {
            //do nothing.
        }
    }

    @Test
    public void testSimpleSearchPatternConstruction()
            throws MalformedRegularExpressionException {
        assertPattern("fa*[bcd][^y]",
                "0 : (|F -> 1),"
                + "1 : (|A -> 1|[DBC] -> 2),"
                + "2 : (|[^Y] -> 3),"
                + "3 : (|.* -> 4),"
                + "4 (accepting) : ()");
    }

    @Test
    public void testAsManyAsYouLikeChains()
            throws MalformedRegularExpressionException {
        assertPattern("ea*b*c*d*a",
                "0 : (|E -> 1),"
                + "1 : (|A -> 1,2|B -> 3|C -> 4|D -> 5),"
                + "2 : (|.* -> 6),"
                + "3 : (|A -> 2|B -> 3|C -> 4|D -> 5),"
                + "4 : (|A -> 2|C -> 4|D -> 5),"
                + "5 : (|A -> 2|D -> 5),"
                + "6 (accepting) : ()");
    }

    @Test
    public void testPatternOptimization() throws MalformedRegularExpressionException {
        assertPattern(".*a",
                "0 : (|A -> 1),"
                + "1 : (|.* -> 2),"
                + "2 (accepting) : ()");
        assertPattern(".*a.*",
                "0 : (|A -> 1),"
                + "1 : (|.* -> 2),"
                + "2 (accepting) : ()");
        assertPattern("a*b*c*a[ub]*[^xyz]*",
                "0 : (|A -> 1),"
                + "1 : (|.* -> 2),"
                + "2 (accepting) : ()");
        assertPattern("a[abc]*a*b*c*d",
                "0 : (|A -> 1),"
                + "1 : (|D -> 2|[ABC] -> 1),"
                + "2 : (|.* -> 3),"
                + "3 (accepting) : ()");
    }

    private void assertPattern(String regex, String expectedPattern)
            throws MalformedRegularExpressionException {
        assertTrue(SearchPattern.validate(regex));
//        SearchPattern testPattern = SearchPattern.compile(regex);
//        assertEquals(expectedPattern, testPattern.toString());
    }
}
