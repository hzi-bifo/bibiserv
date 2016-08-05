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
 * Christian Henke - chenke@cebitec.uni-bielefeld.de
 *
 */
package de.unibi.cebitec.bibiserv.server.manager.utilities;

import java.io.File;
import java.util.Hashtable;
import org.apache.log4j.Logger;

/**
 * This classloader interpretation is based on AppClassloaderImpl
 */
public class AdminRootClassLoaderImpl extends AppClassLoader implements AdminRootClassLoader {

   private Hashtable<String, AdminModClassLoader> classloader = new Hashtable<String, AdminModClassLoader>();
    //private ClassLoader cl = null;
    private static Logger log = Logger.getLogger(AdminRootClassLoaderImpl.class);

    /**
     * Constructor setting an (parent) classloader.
     *
     * @param cl
     */
    public AdminRootClassLoaderImpl(ClassLoader parent) {
        super(parent);
    }

    /**
     * Add a new AdminModClassLoader
     *
     * @param name - Name of application, for which a new AdminModClassLoader should be added
     * @param cl - AdminModClassLoader to be added
     */
    public void addAdminModClassLoader(String name, AdminModClassLoader cl) {
        log.debug("call addAdminModLoader for application '" + name + "'");
        classloader.put(name, cl);
    }

    /**
     * Remove an existing AdminModClassloader
     *
     * @param name - Name of Application which AdminModClassloader should be removed
     */
    public void removeAdminModClassLoader(String name) {
        log.debug("call removeAdminModLoader for application '" + name + "'");
        classloader.remove(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        /* check if class is found by parent */
        try {
             return super.findClass(name);
        } catch (ClassNotFoundException e) {
            // Do nothing ...
        }
        // iterate over all AdminModClassLoader
        log.debug("search for class '"+name+"'");
        for  (String key : classloader.keySet()){
            AdminModClassLoader tmp = classloader.get(key);
            try {
                return tmp.findClass(name);
            } catch (ClassNotFoundException e){
                // do nothing
            }
        }
        throw new ClassNotFoundException("Class '"+name+"' is not found!");
    }

    @Override
    public void removeClassPath(File path) {
    }

    @Override
    public void removeJarPath(File path) {
    }
    
    
}

