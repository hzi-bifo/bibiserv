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
package de.unibi.cebitec.bibiserv.search.suffixtree;

import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the implementation of the SuffixTreeCharacter.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeCharacterTest {

    public SuffixTreeCharacterTest() {
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
    public void testTransformation() throws SuffixTreeException {
        String testString = "test";
        char[] testChars = testString.toUpperCase().toCharArray();
        SuffixTreeCharacter[] chars = SuffixTreeCharacter.transformString(testString);
        int charIndex = 0;
        while (charIndex < chars.length - 1) {
            assertFalse(chars[charIndex].isEnd());
            assertTrue(chars[charIndex].getActualChar() == testChars[charIndex]);
            charIndex++;
        }
        assertTrue(chars[charIndex].isEnd());
    }

    @Test
    public void testHashing() {
        char a = 'a';
        assertTrue(Character.valueOf(a).hashCode() == 97);
        assertFalse(SuffixTreeCharacter.transformChar(a).hashCode() == 97);
        assertTrue(SuffixTreeCharacter.getEndChar().hashCode() == 97);
    }
}
