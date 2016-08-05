/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.impl;

import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.Call;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 * Test for JoBProxyCall (extends CallTemplate)
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class JobProxyCallTest extends CallTemplate {

    private static final Logger LOG = Logger.getLogger(JobProxyCallTest.class);

    @Override
    public Call getCall(BiBiTools bibitools) {
        return new JobProxyCall(bibitools);
    }

    @Override
    public boolean ignore() {
        try {
            // check for local  running JobProxy
            URL url = new URL("http://localhost:9999/v1/jobproxy/ping");
            BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));

            String s = r.readLine();
            r.close();
            if (s.startsWith("alive")) {
                return false;
            }
        } catch (IOException ex) {
           // do nothing
        }
        LOG.warn("Ignore 'JoBProxyCallTest' test, because JobProxy doesn't run on localhost:9999!");
        return true;
    }

}
