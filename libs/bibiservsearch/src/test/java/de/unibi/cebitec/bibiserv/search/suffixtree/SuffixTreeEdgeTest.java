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
import de.unibi.cebitec.bibiserv.search.util.IntPointer;
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
public class SuffixTreeEdgeTest {

    private static WordID id;

    public SuffixTreeEdgeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws SuffixTreeException {
        WordIndex.reset();
        String testword = "abcabxabcd";
        id = WordIndex.indexWord(testword);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testWordRetrieval() {
        SuffixTreeLeaf testNode = new SuffixTreeLeaf();
        testNode.addWordID(id);
        IntPointer start = new IntPointer(0);
        IntPointer end = new IntPointer(2);
        SuffixTreeEdge newEdge = new SuffixTreeEdge(id, start, end, testNode);
        assertEquals("A", newEdge.getWordOnEdge());
    }
}
