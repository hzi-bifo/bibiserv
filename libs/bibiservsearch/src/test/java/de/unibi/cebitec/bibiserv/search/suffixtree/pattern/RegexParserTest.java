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
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class RegexParserTest {

    public RegexParserTest() {
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
    public void testRegexParsing() throws MalformedRegularExpressionException {

        String testRegex = "a*abc\\[[abc]*[^abc]*";
        TestParser testParser = new TestParser(testRegex);
        testParser.parse();
        String expectedResult = "*(A)*ABC[*([ABC])**([^ABC])*";
        assertEquals(expectedResult, testParser.getString());
    }
}

class TestParser extends RegexParser {

    StringBuilder builder = new StringBuilder();

    public TestParser(String regex) {
        super(regex);
    }

    @Override
    public void handleNewChar(PatternChar newCharacter) {
        builder.append(newCharacter.toString());
    }

    @Override
    public void handleAsManyAsYouLike(PatternChar newCharacter) {
        builder.append("*(");
        builder.append(newCharacter.toString());
        builder.append(")*");
    }

    public String getString() {
        return builder.toString();
    }
}