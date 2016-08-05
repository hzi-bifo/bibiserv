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
package de.unibi.cebitec.bibiserv.util.validate.impl;

import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.STRICTNESS;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sven Hartmeier
 */
public class RNAStructML_ValidatorTest extends AbstractTest {

    String rml_strict;
    String rml_ambiguous;
    String rml_strict_broken;

    public RNAStructML_ValidatorTest() {
        try {
            rml_strict = readFromResource("/rnastructml_strict.xml");
            rml_ambiguous = readFromResource("/rnastructml_ambigous.xml");
            rml_strict_broken = readFromResource("/rnastructml_strict_wrong.xml");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
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
    public void testRNAStructML_strict() {
        RNAStructML_Validator validator = new RNAStructML_Validator();
        validator.setStrictness(STRICTNESS.strict);

        //strict
        ValidationResult vr = validator.validateThis(rml_strict);
        assertTrue(vr.getMessage(), vr.isValid());
        //ambiguous
        vr = validator.validateThis(rml_ambiguous);
        assertTrue(vr.getMessage(), !vr.isValid());
        // strict broken
        vr = validator.validateThis(rml_strict_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

    @Test
    public void testRNAStructML_ambiguous() {
        RNAStructML_Validator validator = new RNAStructML_Validator();
        validator.setStrictness(STRICTNESS.ambiguous);

        //strict
        ValidationResult vr = validator.validateThis(rml_strict);
        assertTrue(vr.getMessage(), vr.isValid());
        //ambiguous
        vr = validator.validateThis(rml_ambiguous);
        assertTrue(vr.getMessage(), vr.isValid());
        // strict broken
        vr = validator.validateThis(rml_strict_broken);
        assertTrue(vr.getMessage(), !vr.isValid());

    }

   
}
