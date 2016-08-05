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
package de.unibi.cebitec.bibiserv.server.manager.utilities;

import java.util.HashMap;
import org.apache.log4j.Logger;


/**
 * This classloader interpretation is based on the simple class loader 
 * from Chuck McManis from the Classloader tutorial hosted on
 * http://www.javaworld.com/javaworld/jw-10-1996/jw-10-indepth.html.
 * 
 * Thanks for this useful small tutorial.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class RootClassLoader extends ClassLoader {

    private HashMap<String, AppClassLoader> classloader = new HashMap<String, AppClassLoader>();
    //private ClassLoader cl = null;
    private static Logger log = Logger.getLogger("de.unibi.cebitec.bibiserv.server.manager.utilities");

    /**
     * Constructor setting an (parent) classloader.
     *
     * @param cl
     */
    public RootClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * Add a new AppClassLoader
     *
     * @param name - Name of application, for which a new AppClassLoader should be added
     * @param cl - AppClassLoader to be added
     */
    public void addAppClassLoader(String name, AppClassLoader cl) {
        log.debug("call addAppClassLoader for application '" + name + "'");
        classloader.put(name, cl);
    }

    /**
     * Remove an existing AppClassloader
     *
     * @param name - Name of Application which AppClassloader should be removed
     */
    public void removeAppClassLoader(String name) {
        log.debug("call removeAppClassLoader for application '" + name + "'");
        classloader.remove(name);
    }
    
    /**
     * Retrieve an existing AppClassloader
     *
     * @param name - Name the AppClassloader that should be retrieved
     */
    public AppClassLoader getAppClassLoader(String name) {
        log.debug("call getAppClassLoader for application '" + name + "'");
        return classloader.get(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        /* check if class is found by parent */
        try {
             return super.findClass(name);
        } catch (ClassNotFoundException e) {
            // Do nothing ...
        }
        // iterate over all AppClassLoader
        log.debug("search for class '"+name+"'");
        for  (String key : classloader.keySet()){
            AppClassLoader tmp = classloader.get(key);
            try {
                return tmp.findClass(name);
            } catch (ClassNotFoundException e){
                // do nothing
            }
        }
        throw new ClassNotFoundException("Class '"+name+"' is not found!");
    }

}