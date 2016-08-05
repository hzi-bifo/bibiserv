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
package de.unibi.techfak.bibiserv.impl;

import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.BiBiToolsTest;
import de.unibi.techfak.bibiserv.Call;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Abstract Template for all classes that implements the CallImpl class /Call
 * interface.
 *
 * @author Jan Krueger - jkrueger(at)cebietc.uni-bielefeld.de
 */
public abstract class CallTemplate {
    
    private BiBiTools wsstools;
    private Class currentclass;
    
    public CallTemplate() {
        
        if (System.getProperty("de.unibi.techfak.bibiserv.config") == null) {            
            throw new RuntimeException("Set system property 'de.unibi.techfak.bibiserv.config' "
                    + "to a valid bibiserv properties file.");
        }
        
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty("de.unibi.techfak.bibiserv.config", "src/test/config/bibiserv_properties.xml");
        
        System.err.println("ClassPath   : " + System.getProperty("java.class.path"));
        System.err.println("LibraryPath : " + System.getProperty("java.library.path"));
        System.err.println("BiBiServ2Config : " + System.getProperty("de.unibi.techfak.bibiserv.config"));
        // set DataSource
        BiBiTools.setDataSource(BiBiToolsTest.getDataSource());
        
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        try {

            // get class
            currentclass = this.getClass();

            // create Inputstream
            InputStream is = new FileInputStream("src/test/xml/runnable_item.xml");

            /* initialize BiBiTools object */
            wsstools = new BiBiTools(is);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("An Exception occurred during test setup!\n" + e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testCall_String() {        
        if (!ignore()) {
            
            String exec = "echo Hello World";
            
            call = getCall(wsstools);
            if (call.call(exec)) {
                try {
                    
                    String result = BiBiTools.i2s(new InputStreamReader(call.getStdOutStream()));
                    assertEquals("Hello World", result.split("\n")[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                    fail(" IOException while exec '" + exec + "' ...");
                }
            } else {
                fail("exec of '" + exec + "' fails ...");
            }
        } else {
            assertTrue(true);
        }
    }
    
    private Call call;
    
    public abstract Call getCall(BiBiTools bibitools);

    /**
     * Should return true in the case that case can't be run on the target
     * system, false otherwise
     *
     * @return
     */
    public boolean ignore() {
        return false;
    }
}
