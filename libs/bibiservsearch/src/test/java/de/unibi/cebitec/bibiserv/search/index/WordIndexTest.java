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
package de.unibi.cebitec.bibiserv.search.index;

import de.unibi.cebitec.bibiserv.search.util.Index;
import de.unibi.cebitec.bibiserv.search.util.StringPointer;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is a test class for the general idea behind the WordIndex.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class WordIndexTest {

    public WordIndexTest() {
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
    public void testWordIndex() {
        BiBiServDocument.reset();
        //create test index.
        Index<StringPointer, TestWordID> testIndex = new Index<>();
        //store test entry
        String testWord = "TEST";
        StringPointer testPtr = new StringPointer(testWord);
        TestWordID testID = new TestWordID(testPtr);
        testIndex.put(testPtr, testID);
        //check this entry.
        TestWordID get = testIndex.get(new StringPointer(testWord));
        assertNotNull(get);
        assertEquals(testID, get);
        //manipulate entry to check for call by reference.
        String testWord2 = "TEST2";
        testID.getPtr().setContent(testWord2);
        assertEquals(testPtr.getContent(), testWord2);
    }
}

class TestWordID {

    private StringPointer ptr;

    public TestWordID(StringPointer ptr) {
        this.ptr = ptr;
    }

    public StringPointer getPtr() {
        return ptr;
    }
}