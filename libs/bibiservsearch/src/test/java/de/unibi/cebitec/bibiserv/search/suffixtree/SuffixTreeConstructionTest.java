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
import de.unibi.cebitec.bibiserv.search.index.WordID;
import de.unibi.cebitec.bibiserv.search.index.WordIndex;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test tries to construct a Suffix Tree with Ukkonens algorithm.
 *
 * Attention: Remove test .. previous  test compares a generated suffixtree with an
 * manual generated string using the suffixtree toString Method. The result of the toString
 * is not unique because the data structure behind the suffix tree bases on Collections.
 * 
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeConstructionTest {

    //id of the word that is transformed to a suffix tree
    WordID id;
    //expected result tree
    String expectedTree;
    //expected complex result tree
    String expectedComplexTree;

    public SuffixTreeConstructionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws SuffixTreeException {
        //set up fairly simple test
        WordIndex.reset();
        String testword = "abcabxabcd";
        id = WordIndex.indexWord(testword);
        StringBuilder expectedTreeBuilder = new StringBuilder();
        expectedTreeBuilder.append("(|D->(end ->{\"ABCABXABCD\"})");
        expectedTreeBuilder.append("|->(end ->{})");
        expectedTreeBuilder.append("|XABCD->(end ->{\"ABCABXABCD\"})");
        expectedTreeBuilder.append("|AB->(");
        expectedTreeBuilder.append("|XABCD->(end ->{\"ABCABXABCD\"})");
        expectedTreeBuilder.append("|C->(");
        expectedTreeBuilder.append("|D->(end ->{\"ABCABXABCD\"})");
        expectedTreeBuilder.append("|ABXABCD->(end ->{\"ABCABXABCD\"})))");
        expectedTreeBuilder.append("|B->(");
        expectedTreeBuilder.append("|XABCD->(end ->{\"ABCABXABCD\"})");
        expectedTreeBuilder.append("|C->(");
        expectedTreeBuilder.append("|D->(end ->{\"ABCABXABCD\"})");
        expectedTreeBuilder.append("|ABXABCD->(end ->{\"ABCABXABCD\"})))");
        expectedTreeBuilder.append("|C->(");
        expectedTreeBuilder.append("|D->(end ->{\"ABCABXABCD\"})");
        expectedTreeBuilder.append("|ABXABCD->(end ->{\"ABCABXABCD\"})))");
        expectedTree = expectedTreeBuilder.toString();
        // set up result for complex test.
        expectedTreeBuilder = new StringBuilder();
        expectedTreeBuilder.append("(");
        expectedTreeBuilder.append("|E->(");
        expectedTreeBuilder.append("|->(end ->{\"TESTE\"})");
        expectedTreeBuilder.append("|S->(");
        expectedTreeBuilder.append("|T->(");
        expectedTreeBuilder.append("|E->(end ->{\"TESTE\"})");
        expectedTreeBuilder.append("|->(end ->{\"TEST\",\"EST\"})");
        expectedTreeBuilder.append(")");
        expectedTreeBuilder.append("|->(end ->{\"TES\",\"ES\"})");
        expectedTreeBuilder.append(")");
        expectedTreeBuilder.append(")");
        expectedTreeBuilder.append("|T->(");
        expectedTreeBuilder.append("|E->(");
        expectedTreeBuilder.append("|->(end ->{\"TESTE\"})");
        expectedTreeBuilder.append("|S->(");
        expectedTreeBuilder.append("|T->(");
        expectedTreeBuilder.append("|E->(end ->{\"TESTE\"})");
        expectedTreeBuilder.append("|->(end ->{\"TEST\"})");
        expectedTreeBuilder.append(")");
        expectedTreeBuilder.append("|->(end ->{\"TES\"})");
        expectedTreeBuilder.append(")");
        expectedTreeBuilder.append(")");
        expectedTreeBuilder.append("|->(end ->{\"TEST\",\"EST\"})");
        expectedTreeBuilder.append(")");
        expectedTreeBuilder.append("|->(end ->{})");
        expectedTreeBuilder.append("|S->(");
        expectedTreeBuilder.append("|T->(");
        expectedTreeBuilder.append("|E->(end ->{\"TESTE\"})");
        expectedTreeBuilder.append("|->(end ->{\"TEST\",\"EST\"})");
        expectedTreeBuilder.append(")");
        expectedTreeBuilder.append("|->(end ->{\"TES\",\"ES\"})");
        expectedTreeBuilder.append(")");
        expectedTreeBuilder.append(")");
        expectedComplexTree = expectedTreeBuilder.toString();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSuffixTreeConstruction() throws SuffixTreeException {
        

//        SuffixTree testTree = new SuffixTree();
//        testTree.appendToTree(id);
//        System.out.println(testTree.prettyPrint());
//        assertEquals(expectedTree, testTree.toString());
    }

    @Test
    public void complexSuffixTreeConstructionTest() throws SuffixTreeException {
//        SuffixTree testTree = new SuffixTree();
//        //prepare test words.
//        WordIndex.reset();
//        String[] testwords = {"test", "tes", "es", "est", "teste"};//
//        for (String testword : testwords) {
//            //store the word in the index.
//            WordID testId = WordIndex.indexWord(testword);
//            //add the word to the tree.
//            testTree.appendToTree(testId);
//        }
//        //check the result.
//        assertEquals(expectedComplexTree, testTree.toString());
    }
}
