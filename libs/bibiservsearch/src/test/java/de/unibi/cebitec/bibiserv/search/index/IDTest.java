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

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This tests the ID generation process.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class IDTest {

    public IDTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        WordID.resetIDGeneration();
        DocumentID.resetIDGeneration();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testWordID() {
        WordID id1 = new WordID(null);
        WordID id2 = new WordID(null);
        assertNotSame(id1, id2);
        assertFalse(id1.equals(id2));
        assertFalse(id1.hashCode() == id2.hashCode());
    }

    @Test
    public void testDocumentID() {
        DocumentID id1 = new DocumentID();
        DocumentID id2 = new DocumentID();
        assertNotSame(id1, id2);
        assertFalse(id1.equals(id2));
        assertFalse(id1.hashCode() == id2.hashCode());
    }
}
