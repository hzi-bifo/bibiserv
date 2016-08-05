/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.streamvalidate.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author jkrueger
 */
public class Abstract_ValidatorTest {
    
     /**
     * private helper method. Return content from an 'named' resource' as string.
     *
     *
     * @param name - name of resource
     * @return string
     * @throws IOException
     */
    protected String readFromResource(String name) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(name)));
        String l = null;
        StringBuilder sb = new StringBuilder();
        while ((l = br.readLine()) != null) {
            sb.append(l).append("\n");
        }
        br.close();
        return sb.toString();
    }

    protected String readFile(FileReader fr) throws IOException {
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
