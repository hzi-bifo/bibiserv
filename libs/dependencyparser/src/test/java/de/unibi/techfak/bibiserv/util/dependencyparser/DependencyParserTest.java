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

import de.unibi.techfak.bibiserv.util.dependencyparser.LeafNode.TYPE;
import java.util.Set;
import de.unibi.techfak.bibiserv.util.Pair;
import de.unibi.techfak.bibiserv.util.dependencyparser.javacc.ParseException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Collection;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit Test class. Test generated Parser and build Tree using the sample tool
 * description containing
 *
 * @author jkrueger
 */
public class DependencyParserTest {

    private DependencyParser dp;
    private ParameterWrapper pw;
    private List<Pair<String, String>> pl;

    public DependencyParserTest() throws DependencyException, FileNotFoundException {
        dp = new DependencyParser();
        dp.setTooldescription(new FileInputStream("src/test/xml/sample.xml"));
        pw = new ParameterWrapper();
        dp.setParameterWrapper(pw);

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void test_ConstraintHashMap() {

        ConstraintHashMap chm = new ConstraintHashMap();

        chm.put(new Id("A", TYPE.INT), (Constraints) null);

        chm.put(new Id("B", TYPE.FLOAT), Arrays.asList(new Constraints[]{new Constraints(OpNode.Operations.GT, new Const(20f))}));

        ConstraintHashMap chm2 = new ConstraintHashMap();

        chm2.put(new Id("C", TYPE.INT), new Constraints(OpNode.Operations.EQ, new Const(1)));

        ConstraintHashMap chm3 = new ConstraintHashMap();
        chm3.putAll(chm);
        chm3.putAll(chm2);

        assertTrue(true);

    }

    @Test
    public void test_NodeUtility() throws ParseException, DependencyException {
        dp.setFunctionId("fct_1");
        String expectedString = dp.getDependencyStrings().get(0);
        Node node = dp.generate();
        String string = NodeUtility.Node2String(node);
        System.err.println("DPStr : " + string);
        assertEquals(expectedString, string);

        dp.setFunctionId("fct_10");
        expectedString = dp.getDependencyStrings().get(0);
        node = dp.generate();
        string = NodeUtility.Node2String(node);
        System.err.println("DPStr : " + string);
        assertEquals(expectedString, string);

        dp.setFunctionId("fct_17");

        List<String> l = dp.getDependencyStrings();
        expectedString = "and(" + l.get(0) + "," + l.get(1) + ")";
        node = dp.generate();
        string = NodeUtility.Node2String(node);
        System.err.println("DPStr : " + string);
        assertEquals(expectedString, string);

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

        assertHashEquals("Expected [A,B,C] but got " + node.getMissingConstraints().keySet().toString() + "!",
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), null,
            new Id("B", LeafNode.TYPE.FLOAT), null,
            new Id("C", LeafNode.TYPE.INT), null
        }));
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("C", "5"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());

        assertHashEquals("Expected [B] but got " + node.getMissingConstraints().keySet().toString() + "!",
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("B", LeafNode.TYPE.FLOAT), null
        }));

        pl.add(new Pair("B", "20"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());
    }

    // fct2 has three parameters (A,B,C) and dependency (A | B | C)
    @Test
    public void test_fct_2() throws ParseException, DependencyException {
        dp.setFunctionId("fct_2");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        assertFalse(node.evaluate());
        assertHashEquals("Expected [A,B,C] but got " + node.getMissingConstraints().keySet().toString() + "!",
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), null,
            new Id("B", LeafNode.TYPE.FLOAT), null,
            new Id("C", LeafNode.TYPE.INT), null
        }));

        pl.add(new Pair("B", "20"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());

    }

    // fct3 has three parameters (A,B,C) and dependency ((A & B) | C)
    @Test
    public void test_fct_3() throws ParseException, DependencyException {
        dp.setFunctionId("fct_3");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        assertFalse(node.evaluate());
        assertHashEquals("Expected [A,B,C] but got " + node.getMissingConstraints().keySet().toString() + "!",
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), null,
            new Id("B", LeafNode.TYPE.FLOAT), null,
            new Id("C", LeafNode.TYPE.INT), null
        }));
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "20"));
        pw.setParameter(pl);
        assertTrue(node.evaluate());

        pl.clear();
        pl.add(new Pair("C", "5"));
        pw.setParameter(pl);
        assertTrue(node.evaluate());
    }

    // fct4 has five parameters (A,B,C,D,E) and dependency ((A & B) | (C & D) | E)
    @Test
    public void test_fct_4() throws ParseException, DependencyException {
        dp.setFunctionId("fct_4");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        assertFalse(node.evaluate());

        assertHashEquals("Expected [A,B,C,D,E] but got " + node.getMissingConstraints().keySet().toString() + "!",
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), null,
            new Id("B", LeafNode.TYPE.FLOAT), null,
            new Id("C", LeafNode.TYPE.INT), null,
            new Id("D", LeafNode.TYPE.FLOAT), null,
            new Id("E", LeafNode.TYPE.INT), null
        }));

        pl.add(new Pair("E", "5"));
        pw.setParameter(pl);
        assertTrue(node.evaluate());
    }

    // fct5 has two parameters (A,B) and dependency (A & NOT(B))
    @Test
    public void test_fct_5() throws ParseException, DependencyException {
        dp.setFunctionId("fct_5");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        assertFalse(node.evaluate());
        assertHashEquals("Expected [A] but got " + node.getMissingConstraints().keySet().toString() + "!",
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), null
        }));
        pl.add(new Pair("A", "10"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());

        pl.add(new Pair("B", "20"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
        assertHashEquals("Expected [B] but got " + node.getMissingConstraints().keySet().toString() + "!",
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("B", LeafNode.TYPE.FLOAT), null
        }));
    }

    // fct6 has two parameters (A,B) and dependency (A -> B)
    @Test
    public void test_fct_6() throws ParseException, DependencyException {
        dp.setFunctionId("fct_6");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        assertTrue(node.evaluate());

        pl.add(new Pair("A", "10"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
        assertHashEquals("Expected [A,B] but got " + node.getMissingConstraints().keySet().toString() + "!",
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), null,
            new Id("B", LeafNode.TYPE.FLOAT), null
        }));
        pl.add(new Pair("B", "20"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());
    }

    // fct7 has two parameters (A,B) and dependency ((A == 10) -> B)
    @Test
    public void test_fct_7() throws ParseException, DependencyException {
        dp.setFunctionId("fct_7");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        assertTrue(node.evaluate());

        pl.add(new Pair("A", "10"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
        assertHashEquals(
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), null,
            new Id("B", LeafNode.TYPE.FLOAT), null
        }));

        pl.add(new Pair("B", "20"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());

        // it is not forbidden to assign more than value to a parameter
        pl.add(new Pair("A", "11")); // should be ok also
        pw.setParameter(pl);

        assertTrue(node.evaluate());

    }

    // fct8 has two parameters (A,B) and dependency (A <-> B)
    @Test
    public void test_fct_8() throws ParseException, DependencyException {
        dp.setFunctionId("fct_8");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        assertTrue(node.evaluate());

        pl.add(new Pair("A", "10"));
        pw.setParameter(pl);
        assertFalse(node.evaluate());

        assertHashEquals("Expected [A,B] but got " + node.getMissingConstraints().keySet().toString() + "!",
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), null,
            new Id("B", LeafNode.TYPE.FLOAT), null
        }));
        pl.clear();
        pl.add(new Pair("B", "10"));
        pw.setParameter(pl);
        assertFalse(node.evaluate());
        assertHashEquals("Expected [A,B] but got " + node.getMissingConstraints().keySet().toString() + "!",
                node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), null,
            new Id("B", LeafNode.TYPE.FLOAT), null
        }));
        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "20"));
        pw.setParameter(pl);
        assertTrue(node.evaluate());
    }

    // fct9 has two parameters (A,B) and dependency ((A > 10) And  (B > 10.0))
    @Test
    public void test_fct_9() throws ParseException, DependencyException {
        dp.setFunctionId("fct_9");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "10"));
        pw.setParameter(pl);
        assertFalse(node.evaluate());
        assertHashEquals(node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), OpNode.Operations.GT, new Const(new Integer(10)),
            new Id("B", LeafNode.TYPE.FLOAT), OpNode.Operations.GT, new Const(new Float(10))
        }));
        pl.clear();
        pl.add(new Pair("A", "11"));
        pl.add(new Pair("B", "11"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());
    }

    // fct10 has two parameters (A,B) and dependency ((A = 10) And  (B = 10.0))
    @Test
    public void test_fct_10() throws ParseException, DependencyException {
        dp.setFunctionId("fct_10");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        pl.add(new Pair("A", "11"));
        pl.add(new Pair("B", "11"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
        assertHashEquals(node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), OpNode.Operations.EQ, new Const(new Integer(10)),
            new Id("B", LeafNode.TYPE.FLOAT), OpNode.Operations.EQ, new Const(new Float(10))
        }));

        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "10"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());
    }

    // fct11 has two parameters (A,B) and dependency ((A < 10) And  (B < 10.0))
    @Test
    public void test_fct_11() throws ParseException, DependencyException {
        dp.setFunctionId("fct_11");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        pw.setParameter(pl);
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "10"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
        assertHashEquals("Expected values [A<10, B<10], but got [" + node.getMissingConstraints().toString() + "]", node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), OpNode.Operations.LT, new Const(new Integer(10)),
            new Id("B", LeafNode.TYPE.FLOAT), OpNode.Operations.LT, new Const(new Float(10))
        }));

        pl.clear();
        pl.add(new Pair("A", "9"));
        pl.add(new Pair("B", "9"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());
    }

    // fct9 has two parameters (A,B) and dependency ((A >= 10) And  (B >= 10.0))
    @Test
    public void test_fct_12() throws ParseException, DependencyException {
        dp.setFunctionId("fct_12");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        pl.clear();
        pl.add(new Pair("A", "9"));
        pl.add(new Pair("B", "9"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
        assertHashEquals(node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), OpNode.Operations.GE, new Const(new Integer(10)),
            new Id("B", LeafNode.TYPE.FLOAT), OpNode.Operations.GE, new Const(new Float(10))
        }));

        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "10"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());
    }

    // fct10 has two parameters (A,B) and dependency ((A != 10) And  (B != 10.0))
    @Test
    public void test_fct_13() throws ParseException, DependencyException {
        dp.setFunctionId("fct_13");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "10"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
        assertHashEquals(node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), OpNode.Operations.NE, new Const(new Integer(10)),
            new Id("B", LeafNode.TYPE.FLOAT), OpNode.Operations.NE, new Const(new Float(10))
        }));

        pl.clear();
        pl.add(new Pair("A", "11"));
        pl.add(new Pair("B", "11"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());
    }

    // fct11 has two parameters (A,B) and dependency ((A <= 10) And  (B <= 10.0))
    @Test
    public void test_fct_14() throws ParseException, DependencyException {
        dp.setFunctionId("fct_14");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        pl.clear();
        pl.add(new Pair("A", "11"));
        pl.add(new Pair("B", "11"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
        assertHashEquals(node.getMissingConstraints(), createHashfromArray(new Object[]{
            new Id("A", LeafNode.TYPE.INT), OpNode.Operations.LE, new Const(new Integer(10)),
            new Id("B", LeafNode.TYPE.FLOAT), OpNode.Operations.LE, new Const(new Float(10))
        }));

        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "10"));
        pw.setParameter(pl);
        assertTrue(node.evaluate());
    }

    // fct11 has two parameters (A,B) and dependency ((A > B))
    @Test
    public void test_fct_15() throws ParseException, DependencyException {
        dp.setFunctionId("fct_15");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        assertFalse(node.evaluate());
        assertHashEquals(node.getMissingConstraints(), createHashfromArray(new Object[]{new Id("A", LeafNode.TYPE.INT), null, new Id("B", LeafNode.TYPE.FLOAT), null}));

        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "10"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
        assertHashEquals(node.getMissingConstraints(), createHashfromArray(new Object[]{new Id("A", LeafNode.TYPE.INT), OpNode.Operations.GT, new Id("B", LeafNode.TYPE.FLOAT)}));

        pl.clear();
        pl.add(new Pair("A", "11"));
        pl.add(new Pair("B", "10"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());
    }

    // fct16 has two parameters (A,B) and dependency (A and (not (B > 10))
    @Test
    public void test_fct_16() throws ParseException, DependencyException {
        dp.setFunctionId("fct_16");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        assertFalse(node.evaluate());
        assertHashEquals(node.getMissingConstraints(), createHashfromArray(new Object[]{new Id("A", LeafNode.TYPE.INT), null}));

        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "10"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());

        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "11"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
        assertHashEquals(node.getMissingConstraints(), createHashfromArray(new Object[]{new Id("B", LeafNode.TYPE.INT), OpNode.Operations.LE, new Const(new Integer(10))}));
    }

    @Test
    public void test_fct_17() throws ParseException, DependencyException {
        dp.setFunctionId("fct_17");

        pl = new ArrayList<Pair<String, String>>();
        pw.setParameter(pl);
        // generate tree
        Node node = dp.generate();

        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "10"));
        pl.add(new Pair("C", "TRUE"));
        pl.add(new Pair("D", "Hello World"));
        pw.setParameter(pl);

        assertTrue(node.evaluate());

        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "10"));
        pl.add(new Pair("C", "FALSE"));
        pw.setParameter(pl);

        assertFalse(node.evaluate());
    }

    @Test
    public void test_fct_18() throws ParseException, DependencyException {
        /*
         * Test Funktion entspricht der Abhaengigkeitbeschreibung von dem Tool 
         * RNAhybrid.
         */
        dp.setFunctionId("fct_18");

        pl = new ArrayList();

        pw.setParameter(pl);

        Node node = dp.generate();

        System.err.println("cum. dep : " + NodeUtility.Node2String(node));

        // no parameter set should return true
        assertTrue(node.evaluate());

        pl.clear();
        pl.add(new Pair("A", "10"));
        pl.add(new Pair("B", "11"));
        pw.setParameter(pl);

        // Parameter A,B : A < B should return true
        assertTrue(node.evaluate());

    }

    @Test
    public void test_fct_19() throws ParseException, DependencyException {
        /*
         * Test Funktion entspricht der Abhaengigkeitbeschreibung von dem Tool 
         * RNAhybrid.
         */
        dp.setFunctionId("fct_19");

        pl = new ArrayList();
        pl.add(new Pair("C", "false"));
        pw.setParameter(pl);

        Node node = dp.generate();

        // no parameter set should return true
        boolean result = node.evaluate();
        String missedConstraints = "none";
        if (!result) {
            missedConstraints = ConstraintHashMaptoString(node.getMissingConstraints());
        }
        assertTrue("Missed constraints:" + missedConstraints, result);

        pl.clear();
        pl.add(new Pair("C", "true"));
        pw.setParameter(pl);

        result = node.evaluate();
        missedConstraints = "none";
        if (!result) {
            missedConstraints = ConstraintHashMaptoString(node.getMissingConstraints());
        }
        assertFalse("Missed constraints:" + missedConstraints, result);

        pl.clear();
        pl.add(new Pair("C", "true"));
        pl.add(new Pair("A", "10"));

        pw.setParameter(pl);

        // should return true
        assertTrue(node.evaluate());

    }

    public static void assertListEquals(Collection result, Collection expected) {
        assertListEquals("", result, expected);

    }

    public static void assertListEquals(String message, Collection result, Collection expected) {

        if (result == null && expected == null) {
            return;
        }

        if (result == null) {
            fail("Result Set is null ! Expected " + expected.toString());
        }

        // if expected value is null it doesn't matter what the result contains ...
        if (expected == null) {
            return;
        }

        if (result.size() != expected.size()) {
            fail("Size of expected Set (" + expected.size() + ") differs from result Set (" + result.size() + ").");
        }
        List a = new ArrayList(result);
        Collections.sort(a);

        List b = new ArrayList(expected);
        Collections.sort(b);

        for (int c = 0; c < a.size(); ++c) {
            assertEquals("[" + a.get(c) + "] differs from [" + b.get(c) + "] " + message, a.get(c), b.get(c));

        }
    }

    public static void assertHashEquals(String message, ConstraintHashMap result, Collection expected) {
        assertListEquals(message, result.keySet(), expected);
    }

    public static void assertHashEquals(ConstraintHashMap result, ConstraintHashMap expected) {
        assertHashEquals("", result, expected);
    }

    public static void assertHashEquals(String message, ConstraintHashMap result, ConstraintHashMap expected) {
        Set<Id> s_a = result.keySet();

        Set<Id> s_b = expected.keySet();

        if (s_a.size() != s_b.size()) {
            fail("Number of expected keys (" + s_a.size() + ") differs from keys in result map (" + s_b.size() + ").");
        }

        for (Id id_a : s_a) {
            if (!s_b.contains(id_a)) {
                fail("Expected id (" + id_a + ") not found in result map!");
            } else {
                assertListEquals(result.get(id_a), expected.get(id_a));
            }
        }

    }

    public static ConstraintHashMap createHashfromArray(Object[] ts) {
        ConstraintHashMap r = new ConstraintHashMap();

        int s = 0;
        Id id = null;
        OpNode.Operations op = OpNode.Operations.NULL;
        LeafNode ln = null;
        for (Object t : ts) {
            switch (s) {
                case 0: {
                    id = (Id) t;
                    s = 1;
                    break;
                }
                case 1: {
                    if (t == null || t.equals(OpNode.Operations.NULL)) {
                        r.put(id, (List) null);
                        s = 0;
                    } else {
                        op = (OpNode.Operations) t;
                        s = 2;
                    }
                    break;
                }
                case 2: {
                    ln = (LeafNode) t;
                    r.put(id, new Constraints(op, ln));
                    s = 0;
                }
            }
        }

        return r;
    }

    public static String ConstraintHashMaptoString(ConstraintHashMap map) {
        StringBuilder sb = new StringBuilder();
        for (Id id : map.keySet()) {
            System.err.println(".");
            sb.append(id.getId()).append("[").append(id.getType()).append("]:");
            // cronstraints could be null if not defined
            if (map.get(id) == null) {
                sb.append("defined");
            } else {
                for (Constraints c : map.get(id)) {
                    sb.append(c.toString());
                    sb.append(",");
                }
                sb.setLength(sb.length() - 1);
            }
        }
        return sb.toString();
    }
}
