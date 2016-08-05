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

import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeCharacter;
import java.util.HashSet;
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
public class PatternCharTest {

    public PatternCharTest() {
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

    /**
     * Test of isSubset method, of class PatternChar.
     */
    @Test
    public void testIsSubset() {
        AnyPatternChar any = new AnyPatternChar();
        NotOneOfPatternChar notOneOf1 = new NotOneOfPatternChar(createSet("ab"));
        NotOneOfPatternChar notOneOf2 = new NotOneOfPatternChar(createSet("a"));
        OneOfPatternChar oneOf1 = new OneOfPatternChar(createSet("cde"));
        OneOfPatternChar oneOf2 = new OneOfPatternChar(createSet("de"));
        ExactPatternChar exact1 = new ExactPatternChar(SuffixTreeCharacter.transformChar('e'));
        ExactPatternChar exact2 = new ExactPatternChar(SuffixTreeCharacter.transformChar('a'));
        assertTrue(any.isSubset(any));
        assertTrue(any.isSubset(notOneOf1));
        assertTrue(any.isSubset(notOneOf2));
        assertTrue(any.isSubset(oneOf1));
        assertTrue(any.isSubset(oneOf2));
        assertTrue(any.isSubset(exact1));
        assertTrue(any.isSubset(exact2));
        assertFalse(notOneOf1.isSubset(any));
        assertTrue(notOneOf1.isSubset(notOneOf1));
        assertFalse(notOneOf1.isSubset(notOneOf2));
        assertTrue(notOneOf1.isSubset(oneOf1));
        assertTrue(notOneOf1.isSubset(oneOf2));
        assertTrue(notOneOf1.isSubset(exact1));
        assertFalse(notOneOf1.isSubset(exact2));
        assertFalse(notOneOf2.isSubset(any));
        assertTrue(notOneOf2.isSubset(notOneOf1));
        assertTrue(notOneOf2.isSubset(notOneOf2));
        assertTrue(notOneOf2.isSubset(oneOf1));
        assertTrue(notOneOf2.isSubset(oneOf2));
        assertTrue(notOneOf2.isSubset(exact1));
        assertFalse(notOneOf2.isSubset(exact2));
        assertFalse(oneOf1.isSubset(any));
        assertFalse(oneOf1.isSubset(notOneOf1));
        assertFalse(oneOf1.isSubset(notOneOf2));
        assertTrue(oneOf1.isSubset(oneOf1));
        assertTrue(oneOf1.isSubset(oneOf2));
        assertTrue(oneOf1.isSubset(exact1));
        assertFalse(oneOf1.isSubset(exact2));
        assertFalse(oneOf2.isSubset(any));
        assertFalse(oneOf2.isSubset(notOneOf1));
        assertFalse(oneOf2.isSubset(notOneOf2));
        assertFalse(oneOf2.isSubset(oneOf1));
        assertTrue(oneOf2.isSubset(oneOf2));
        assertTrue(oneOf2.isSubset(exact1));
        assertFalse(oneOf2.isSubset(exact2));
        assertFalse(exact1.isSubset(any));
        assertFalse(exact1.isSubset(notOneOf1));
        assertFalse(exact1.isSubset(notOneOf2));
        assertFalse(exact1.isSubset(oneOf1));
        assertFalse(exact1.isSubset(oneOf2));
        assertTrue(exact1.isSubset(exact1));
        assertFalse(exact1.isSubset(exact2));
        assertFalse(exact2.isSubset(any));
        assertFalse(exact2.isSubset(notOneOf1));
        assertFalse(exact2.isSubset(notOneOf2));
        assertFalse(exact2.isSubset(oneOf1));
        assertFalse(exact2.isSubset(oneOf2));
        assertFalse(exact2.isSubset(exact1));
        assertTrue(exact2.isSubset(exact2));
    }

    private static HashSet<SuffixTreeCharacter> createSet(String characters) {
        HashSet<SuffixTreeCharacter> charSet = new HashSet<>();
        SuffixTreeCharacter[] chars = SuffixTreeCharacter.transformString(characters);
        for (int i = 0; i < chars.length - 1; i++) {
            charSet.add(chars[i]);
        }
        return charSet;
    }
}
