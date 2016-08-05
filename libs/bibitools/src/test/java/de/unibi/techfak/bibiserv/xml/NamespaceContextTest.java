/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */ 

package de.unibi.techfak.bibiserv.xml;

import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jkrueger
 */
public class NamespaceContextTest {

    public NamespaceContextTest() {
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
    public void testAll() {
        System.out.println("test alle methods of namespacecontext together");
        String namespaceURI = "de:unibi:techfak:bibiserv:cms";
        String prefix[] = {"cms", "cms2"};

        NamespaceContext instance = new NamespaceContext();
        instance.addNamespace(namespaceURI, prefix[0]);
        instance.addNamespace(namespaceURI, prefix[1]);
        // test getPrefix
        assertEquals(instance.getPrefix(namespaceURI), prefix[0]);
        // test getNamespaceURI
        assertEquals(instance.getNamespaceURI(prefix[0]), namespaceURI);
        // test getPrefixes
        int e = 0;
        for (Iterator i = instance.getPrefixes(namespaceURI); i.hasNext();) {
            assertEquals(i.next(), prefix[e]);
            e++;
        }
        // should return null
        assertNull(instance.getNamespaceURI("cms3"));
        assertNull(instance.getPrefix("bla:blub:blabla"));


    }
}
