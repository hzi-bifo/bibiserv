/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.cebitec.bibiserv.client.manager;

/**
 *
 * @author jkrueger
 */
public class AntTaskDefClassLoader extends ClassLoader{

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.err.println(System.getProperty("java.class.path"));

        return super.findClass(name);
    }



}
