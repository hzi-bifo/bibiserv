/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2016 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2016 BiBiServ"
 *
 * Contributor(s): Jan Krüger
 *
 */
package de.unibi.techfak.bibiserv.deploy.tools;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class VerifyReferencesTest {

    VerifyReferences vr;
    final static File runnableitemOk = new File("src/test/resources/rnamovies.bs2");
    final static File runnableitemFail = new File("src/test/resources/ceged.bs2");

    public VerifyReferencesTest() {

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

    /**
     * Test of execute method, of class VerifyReferences.
     */
    @Test
    public void testExecute() {

        // should be ok
        try {
            vr = new VerifyReferences(runnableitemOk);

        } catch (BuildException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        try {
            vr.execute();
        } catch (BuildException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // should be failed
        try {
            vr = new VerifyReferences(runnableitemFail);

        } catch (BuildException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        try {
            vr.execute();
            fail("Broken reference are ok ???");
        } catch (BuildException e) {
            e.printStackTrace();

        }

    }
}
