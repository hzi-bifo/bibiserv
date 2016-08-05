/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 BiBiServ Curator Team"
 *
 * Contributor(s): Benjamin Paassen
 *
 */
package de.unibi.cebitec.bibiserv.search.helper;

import de.unibi.cebitec.bibiserv.search.exceptions.InvalidWordException;
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
public class SearchQueryTest {

    public SearchQueryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRegexQuery() throws InvalidWordException {
        String query = "![abc].\\.[abc]*[^abc]!";
        String[] expected = {"[ABC].\\.[ABC]*[^ABC]"};
        assertQuery(query, SearchQueryType.WITHOUTCORRECTION, expected);
    }

    /**
     * Test of parseQuery method, of class SearchQuery.
     */
    @Test
    public void testWordCleaning() throws Exception {
        String[] queries = {"\"holder.^!\" Knabe\"", "!holder  %!!!   Knabe!", "holder& ()Knabe"};
        String[] expected = {"HOLDER", "KNABE"};
        for (int i = 0; i < queries.length; i++) {
            assertQuery(queries[i], SearchQueryType.values()[i], expected);
        }
    }

    private void assertQuery(String queryString, SearchQueryType expectedType,
            String[] expectedWords) throws InvalidWordException {
        SearchQuery resultQuery = SearchQuery.parseQuery(queryString);
        assertEquals(expectedType, resultQuery.getType());
        assertArrayEquals(expectedWords, resultQuery.getWords());
    }
}
