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
package de.unibi.cebitec.bibiserv.util.validate.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class AbstractTest {
    
    
        /**
     * Priavate helper method. Return content from an 'named' resource' as string.
     *
     *
     * @param name - name of resource
     * @return string
     * @throws IOException
     */
    protected final String readFromResource(String name) throws IOException {
        InputStream rin = getClass().getResourceAsStream(name);
        if (rin == null) {
            throw new IOException("Resource '" + name + "' not found in classpath!");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(rin));
        String l = null;
        StringBuilder sb = new StringBuilder();
        while ((l = br.readLine()) != null) {
            sb.append(l);
            sb.append(System.getProperty("line.separator"));
        }
        br.close();
        return sb.toString();
    }

    protected final String readFile(FileReader fr) throws IOException {
        BufferedReader r = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();

    }
    
}
