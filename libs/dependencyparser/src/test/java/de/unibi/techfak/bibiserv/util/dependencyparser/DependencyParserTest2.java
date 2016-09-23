/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
All rights reserved.

The contents of this file are subject to the terms of the Common
Development and Distribution License("CDDL") (the "License"). You
may not use this file except in compliance with the License. You can
obtain a copy of the License at http://www.sun.com/cddl/cddl.html

See the License for the specific language governing permissions and
limitations under the License.  When distributing the software, include
this License Header Notice in each file.  If applicable, add the following
below the License Header, with the fields enclosed by brackets [] replaced
by your own identifying information:

"Portions Copyrighted 2011 BiBiServ"

Contributor(s):
 */
package de.unibi.techfak.bibiserv.util.dependencyparser;

import de.unibi.techfak.bibiserv.util.Pair;
import de.unibi.techfak.bibiserv.util.dependencyparser.javacc.ParseException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit Test class. Test generated Parser with broken dependency string
 * @author jkrueger
 */
public class DependencyParserTest2 {

    private DependencyParser dp;
    private ParameterWrapper pw;
    private List<Pair<String, String>> pl;

    public DependencyParserTest2() throws DependencyException, FileNotFoundException {
        dp = new DependencyParser();
        dp.setTooldescription(new FileInputStream("src/test/xml/broken.xml"));
        pw = new ParameterWrapper();
        dp.setParameterWrapper(pw);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    // fct1 has three parameters (A,B,C) and dependency (A & B & C)
    @Test
    public void test_fct_1() throws ParseException, DependencyException {
        dp.setFunctionId("fct_1");
        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();
        assertFalse(node.evaluate());
    }
}
