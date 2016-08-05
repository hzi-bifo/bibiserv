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
 * "Portions Copyrighted 2010-2016 BiBiServ"
 *
 * Contributor(s): Jan Krueger
 *
 */ 

package de.unibi.techfak.bibiserv.impl;

import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.Call;
import org.apache.log4j.Logger;

/**
 * Test for DRMAACall (extends CallTemplate)
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class DRMAACallTest  extends CallTemplate{

    private static final Logger LOG = Logger.getLogger(DRMAACallTest.class);
    
     @Override
    public Call getCall(BiBiTools bibitools) {
        
            return new DRMAACall(bibitools);
    }

    @Override
    public boolean ignore() {
        try {
            System.loadLibrary("drmaa.so");
        } catch (UnsatisfiedLinkError e) {
        
            LOG.warn("Ignore 'DRMAACallTest' test, because drmaa.so is not found in java.library.path!");
            return true;    
        }
        
        return false;
        
    }
    
    
}
