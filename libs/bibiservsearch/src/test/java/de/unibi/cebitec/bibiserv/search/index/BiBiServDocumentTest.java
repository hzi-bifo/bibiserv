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

import java.util.ArrayList;
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
public class BiBiServDocumentTest {
    
    public BiBiServDocumentTest() {
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
    public void documentIndexingTest() {
        BiBiServDocument.reset();
        assertTrue(BiBiServDocument.getIndexSize() == 0);
        String[] urls = {"testurl1", "testurl2"};
        BiBiServDocument document = BiBiServDocument.createIndexedDocument(urls[0]);
        assertTrue(BiBiServDocument.getIndexSize() == 1);
        int firstHashCode = document.getId().hashCode();
        BiBiServDocument document2 = BiBiServDocument.createIndexedDocument(urls[1]);
        assertTrue(BiBiServDocument.getIndexSize() == 2);
        //check if hashcodes are unique
        assertFalse(firstHashCode == document2.getId().hashCode());
        //also check if id objects are unique.
        assertFalse(document.getId().equals(document2.getId()));
        //check document retrieval.
        assertSame(document, BiBiServDocument.getDocumentByID(document.getId()));
        assertSame(document2, BiBiServDocument.getDocumentByID(document2.getId()));
        //check stored identifiers.
        ArrayList<String> identifiers = BiBiServDocument.getIdentifiers();
        String resultURLs = identifiers.get(0);
        resultURLs += identifiers.get(1);
        for (String url : urls) {
            assertTrue(resultURLs.contains(url));
        }
        //test removal.
        BiBiServDocument.removeDocumentByIdentifier(urls[0]);
        assertTrue(BiBiServDocument.getIndexSize() == 1);
        identifiers = BiBiServDocument.getIdentifiers();
        assertEquals(urls[1], identifiers.get(0));
        //test reset
        BiBiServDocument.reset();
        assertTrue(BiBiServDocument.getIndexSize() == 0);
    }
}
