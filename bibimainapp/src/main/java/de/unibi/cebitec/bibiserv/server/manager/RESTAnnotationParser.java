/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.server.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Path;


/**
 * Class that provides some function to parse a path for classes having Path annotations.
 * 
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class RESTAnnotationParser {
    
    
    /**
     * Parse a given file path for all class files having Path annotation using the default
     * classloader.
     * 
     * 
     * @param dir : directory to be searched
     * @return Returns a set of classes having an Path annotation
     * @throws Exception 
     */
     public static Set<Class> parse(File dir) throws Exception {
         return parse(RESTAnnotationParser.class.getClassLoader(),dir);
     }
    
     /**
      *  * Parse a given file path for all class files having Path annotation using the given
     * classloader.
      * 
      * 
      * @param cl - ClassLoader
      * @param dir - directory to be searched
      * @return Returns a set of classes having an Path annotation
      * 
      * @throws Exception 
      */
    public static Set<Class> parse(ClassLoader cl, File dir) throws Exception {
         Set<Class> restclasses = new HashSet();
         FilenameFilter ff = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                 if (name.endsWith(".class")){
                        return true;
                 }
                 return false;
            }
        };
         
        _parse(restclasses, cl, dir, dir, ff, Path.class);
        
        return restclasses;
        
    }
    
    
    
     public static Set<Class> parse(Set<Class> classes, Class ac) throws Exception {
        Set<Class> restclasses = new HashSet();
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(ac)) {
                restclasses.add(clazz);
            }
        }
        return restclasses;

    }
    
    
   
    
    
    private static void _parse(Set<Class> s, ClassLoader cl, File root, File dir, FilenameFilter ff, Class ac) {
        for (File t : dir.listFiles()) {
            if (t.isDirectory()) {
                _parse(s,cl, root,t,ff,ac);
            }
            if (ff.accept(dir,t.getName())){
                // get classname from path
                String classname = t.getAbsolutePath().substring(root.toString().length()+1,t.getAbsolutePath().length()-6).replace('/', '.');
                try {
                    // load class
                    Class c = cl.loadClass(classname);
                    // check for suitable annotation
                    if (c.isAnnotationPresent(ac)) {
                        s.add(c);
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(RESTAnnotationParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
