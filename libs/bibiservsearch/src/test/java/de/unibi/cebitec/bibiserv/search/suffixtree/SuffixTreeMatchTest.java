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

import de.unibi.cebitec.bibiserv.search.exceptions.MalformedRegularExpressionException;
import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.index.WordID;
import de.unibi.cebitec.bibiserv.search.index.WordIndex;
import java.util.Collection;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test tests the SuffixTreeMatcher.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeMatchTest {

    private SuffixTree testTree;

    public SuffixTreeMatchTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        WordIndex.reset();
        testTree = new SuffixTree();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSimpleMatching()
            throws MalformedRegularExpressionException, SuffixTreeException {
        String[] testString = {"abc"};
        indexAndAppend(testString);
        assertMatches("abc", testString);
        assertMatches("bc", testString);
        assertMatches("c", testString);
    }

    @Test
    public void testSimpleMultipleMatching()
            throws MalformedRegularExpressionException, SuffixTreeException {
        String[] testStrings = {"test", "est", "st"};
        indexAndAppend(testStrings);
        assertMatches("st", testStrings);
        String[] expected = {"test", "est"};
        assertMatches("est", expected);
        String[] expected2 = {"test"};
        assertMatches("test", expected2);
    }

    @Test
    public void testSimpleRegexMatching() throws MalformedRegularExpressionException,
            SuffixTreeException {
        String[] testString = {"teststring"};
        indexAndAppend(testString);
        assertMatches("tst.*", testString);
        assertMatches("te[st]*ring", testString);
        assertMatches("[^a]*g", testString);
        assertMatches("[^e]*e[^e]*g", testString);
    }

    /**
     * This tests a typical search query: the given string shall be searched as
     * well as all words that have the string as substring.
     */
    @Test
    public void testRegularQuery() throws MalformedRegularExpressionException,
            SuffixTreeException {
        String[] testStrings = {"teststring", "teststrings", "strings"};
        String[] expected = {"teststring", "teststrings"};
        indexAndAppend(testStrings);
        assertMatches("tst.*", expected);
    }

    /**
     * This method tests the spell correction capacity of the suffix tree
     * matcher.
     */
    @Test
    public void testInexactMatching() throws MalformedRegularExpressionException,
            SuffixTreeException {
        String[] testString = {"blablabla", "lalala"};
        String[] expected = {"blablabla"};
        indexAndAppend(testString);
        //test gap
        assertMatches("blb", expected, true);
        //test insert
        assertMatches("blacb", expected, true);
        //test replacement
        assertMatches("blablcbla", expected, true);
        //test swap
        assertMatches("balblabla", expected, true);
    }

    /**
     * This test appends all possible permutations of the ascii alphabet from a
     * to g (ascii codes 97 to 103) of length 6 to the tree
     * (all in all 7^6 = 117649 permutations) and does some test queries.
     */
    @Test
    public void matchingStressTest() throws MalformedRegularExpressionException,
            SuffixTreeException {
        final int length = 6;
        final int lowerBorder = 97;
        //append all permutations.
        String[] permutations = permutations(length, lowerBorder, lowerBorder + length);
        indexAndAppend(permutations);
        //assert number of matches for query of every single letter.
        int expectedFreq = calculateExpectedFrequentness(length + 1, length);
        for (int asciiCode = lowerBorder; asciiCode <= lowerBorder + length; asciiCode++) {
            //construct query
            StringBuilder patternBuilder = new StringBuilder();
            patternBuilder.append((char) asciiCode);
            patternBuilder.append(".*");
            //get all matches.
            Collection<SuffixTreeSearchResult> allMatches = testTree.getAllMatches(
                    patternBuilder.toString(), false);
            //check size of return set.
            assertTrue(allMatches.size() == expectedFreq);
        }
        //construct complementary queries (every char is allowed but the given one)
        expectedFreq = (int) Math.pow(length + 1, length) - expectedFreq;
        for (int asciiCode = lowerBorder; asciiCode <= lowerBorder + length; asciiCode++) {
            //construct query
            StringBuilder subPatternBuilder = new StringBuilder();
            subPatternBuilder.append("[^");
            subPatternBuilder.append((char) asciiCode);
            subPatternBuilder.append("]");
            StringBuilder patternBuilder = new StringBuilder();
            for (int k = 0; k < length; k++) {
                patternBuilder.append(subPatternBuilder.toString());
            }
            //get all matches.
            Collection<SuffixTreeSearchResult> allMatches = testTree.getAllMatches(
                    patternBuilder.toString(), false);
            //check size of return set.
            assertTrue(allMatches.size() == expectedFreq);
        }
    }

    /**
     * This test creates all permutations of the word
     * "donaudampfschiffkapitaensmuetzenhersteller" with edit
     * distance 1 for the ascii alphabet from code 33 (!) to 126 (~).
     * This are 5776 words.
     * These are appended to the suffix tree and then searched by inexact
     * search for the word itself and edit distance 1.
     */
    @Test
    public void inexactMatchingStressTest()
            throws MalformedRegularExpressionException, SuffixTreeException {
        int lowerBorder = 33;
        int upperBorder = 126;
        String word = "donaudampfschiffkapitaensmuetzenhersteller";
        String[] strings = edit1permutations(word, lowerBorder, upperBorder);
        indexAndAppend(strings);
        assertMatches(word, strings, true);
    }

    private void indexAndAppend(String[] strings) throws SuffixTreeException {
        for (String string : strings) {
            WordID currentID = WordIndex.indexWord(string);
            testTree.appendToTree(currentID);
        }
    }

    private void assertMatches(String pattern, String[] expected)
            throws MalformedRegularExpressionException, SuffixTreeException {
        assertMatches(pattern, expected, false);
    }

    private void assertMatches(String pattern, String[] expected,
            boolean spellCorrection) throws MalformedRegularExpressionException,
            SuffixTreeException {
        Collection<SuffixTreeSearchResult> results =
                testTree.getAllMatches(pattern, spellCorrection);
        HashSet<String> actualStrings = new HashSet<>();
        for (SuffixTreeSearchResult result : results) {
            actualStrings.add(result.getMatchedWordID().
                    getWordReference().getContent());
        }
        assertTrue("For pattern " + pattern
                + " the actual result set size was not "
                + "equal to the actual result set size.",
                actualStrings.size() == expected.length);
        for (String expectedString : expected) {
            assertTrue("While matching pattern " + pattern
                    + " the expected string " + expectedString
                    + " was not part of the result set.",
                    actualStrings.contains(expectedString.toUpperCase()));
        }
    }

    /**
     * Creates all permutations with the given length of a defined section of
     * the ASCII-alphabet.
     *
     * e.g. for the alphabet-section a-c (97-98)and length 2 the return array
     * would consist of
     *
     * aa
     * ab
     * ac
     * ba
     * bb
     * bc
     * ca
     * cb
     * cc
     *
     * @param length desired length of result strings
     * @return all possible alphabet permutations for the given length. This
     * array
     * has size: |Alphabet|^length
     */
    private static String[] permutations(int length, int lowerBorder,
            int upperBorder) {
        int size = upperBorder - lowerBorder + 1;
        int numberOfPermutations = (int) Math.pow(size,
                length);
        char[][] permutations = new char[numberOfPermutations][length];
        int repeats = 1;
        for (int i = 1; i <= length; i++) {
            for (int j = 0; j < numberOfPermutations; j = j + repeats
                            * size) {
                for (int c = lowerBorder; c <= upperBorder; c++) {
                    for (int k = 0; k < repeats; k++) {
                        permutations[j + k + (c - lowerBorder) * repeats][length
                                - i] =
                                (char) c;
                    }
                }
            }
            repeats *= size;
        }

        String[] returnValue = new String[numberOfPermutations];
        for (int i = 0; i < returnValue.length; i++) {
            returnValue[i] = String.copyValueOf(permutations[i]);
        }
        return returnValue;
    }

    /**
     * Calculates the expected frequentness of strings that contain a certain
     * letter in permutations of a given alphabet.
     *
     * e.g.: we have all permutations of length 4 for the alphabet a,b,c,d,e,f.
     *
     * (all in all 6^4 = 1296 permutations)
     *
     * How many permutations contain the letter a?
     *
     * Answer: 1*6^3 + 5*1*6^2 + 5^2*1*6 + 5^3*1 = 671
     *
     * @param alphabetLength length of the alphabet used.
     * @param places permutation length.
     * @return expected frequentness.
     */
    private static int calculateExpectedFrequentness(int alphabetLength, int places) {
        int sum = 0;
        for (int k = 0; k < places; k++) {
            sum += ((int) Math.pow(alphabetLength - 1, k))
                    * ((int) Math.pow(alphabetLength, places - 1 - k));
        }
        return sum;
    }

    /**
     * This method creates all permutations of the given string with edit
     * distance 1
     */
    private static String[] edit1permutations(String string, int lowerBorder, int upperBorder) {
        HashSet<String> retSet = new HashSet<>();
        char[] characters = string.toUpperCase().toCharArray();
        //gaps
        for (int i = 0; i < characters.length; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < characters.length; j++) {
                if (i == j) {
                    continue;
                }
                builder.append(characters[j]);
            }
            retSet.add(builder.toString());
        }
        //inserts
        for (int i = 0; i < characters.length; i++) {
            for (int asciiCode = lowerBorder; asciiCode <= upperBorder; asciiCode++) {
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < characters.length; j++) {
                    if (i == j) {
                        builder.append(Character.toUpperCase((char) asciiCode));
                    }
                    builder.append(characters[j]);

                }
                retSet.add(builder.toString());
            }
        }
        for (int asciiCode = lowerBorder; asciiCode <= upperBorder; asciiCode++) {
            StringBuilder builder = new StringBuilder();
            builder.append(string.toUpperCase());
            builder.append(Character.toUpperCase((char) asciiCode));
            retSet.add(builder.toString());
        }
        //swaps
        for (int i = 0; i < characters.length - 1; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < i; j++) {
                builder.append(characters[j]);
            }
            builder.append(characters[i + 1]);
            builder.append(characters[i]);
            for (int j = i + 2; j < characters.length; j++) {
                builder.append(characters[j]);
            }
            retSet.add(builder.toString());
        }
        //replaces
        for (int i = 0; i < characters.length; i++) {
            for (int asciiCode = lowerBorder; asciiCode <= upperBorder; asciiCode++) {
                if (Character.toUpperCase((char) asciiCode)
                        != characters[i]) {
                    //only add replaces that change something.
                    StringBuilder builder = new StringBuilder();
                    for (int j = 0; j < characters.length; j++) {
                        if (i == j) {
                            builder.append(Character.toUpperCase((char) asciiCode));
                        } else {
                            builder.append(characters[j]);
                        }
                    }
                    retSet.add(builder.toString());
                }
            }
        }
        return retSet.toArray(new String[retSet.size()]);
    }
}
