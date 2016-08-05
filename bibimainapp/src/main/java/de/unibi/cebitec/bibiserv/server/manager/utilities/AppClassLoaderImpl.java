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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import org.apache.log4j.Logger;

/**
 * This classloader interpretation is based on the simple class loader 
 * from Chuck McManis from the Classloader tutorial hosted on
 * http://www.javaworld.com/javaworld/jw-10-1996/jw-10-indepth.html.
 * 
 * Thanks for this useful small tutorial.
 *
 * This is a implementation of a AppClassLoader. The AppClassLoader MUST
 * use together with an RootClassLoader. See RootClassLoader API doc for a 
 * detailed explaination.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class AppClassLoaderImpl extends AppClassLoader {

    private Set<File> jarpathset = new TreeSet<File>();
    private Set<File> classpathset = new TreeSet<File>();
    private List<File> jarlist = new ArrayList<File>();
    private Map<String, URLList> resourcecache = new Hashtable<String, URLList>();
    private Map<String, Class> cache = new Hashtable<String, Class>();
    private Map<String, File> jarcontenthash = new Hashtable<String, File>();
    private static Logger log = Logger.getLogger("de.unibi.cebitec.bibiserv.server.manager.utilities");

    /* blocksize if read from jarentry inputstream without having size of whole jarentry */
    private final static int blocksize = 2048;
    
    /**
     * Creates a new AppClassLoader searching for classes at the given rootpath.
     * This ClassLoader supports reading classes(and resources from a path
     * <pre>${rootpath}/classes</pre>, <pre>${rootpath}/config</pre> and
     * <pre>${rootpath}/resources</pre> and Java libraries (Jar's) from
     * <pre>${rootpath}/lib</pre>. Additional class- and libpathes can Attentention: this classloader looks first into
     * his own classpath and ask the parent classloader only if failure ...
     *
     * @param path - Root Path of this ApplicationClassLoader
     */
    public AppClassLoaderImpl(ClassLoader parent, File rootpath) throws IOException {
        super(parent);

        addClassPath(new File(rootpath, "classes"));
        addJarPath(new File(rootpath, "lib"));
        addClassPath(new File(rootpath, "config"));
        addClassPath(new File(rootpath, "resources"));
    }

    private void addClassPath(File path) throws IOException {
        if (path.exists() && path.isDirectory()) {
            classpathset.add(path);
        } else {
            throw new IOException("'" + path + "' doesn't exits or isn't a directory!");
        }
    }

    private void addJarPath(File path) throws IOException {
        if (path.exists() && path.isDirectory()) {
            jarpathset.add(path);
            jarlist.addAll(Arrays.asList(getJarList(path)));
            // to speed up search for class/resources in jar files build a hash over jar file content
            buildJarContenhash();
        } else {
            throw new IOException("'" + path + "' doesn't exits or isn't a directory!");
        }
    }

    @Override
    public URL getResource(String name) {
        return findResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return findResources(name);

    }

    @Override
    public InputStream getResourceAsStream(String name) {
        try {
            URL url = findResource(name);
            if (url != null) {
                // if resource is found inside an jar
                if (url.toString().endsWith("#" + name)) {
                    if (url.toString().startsWith("file:")) {
                        File jarfile = new File(url.toString().split("#")[0].substring(5));
                        return getResourceAsStreamFromJar(jarfile, name);
                    } else {
                        return null;
                    }
                } else if (url.toString().startsWith("file:")) {
                    return url.openStream();
                } else {
                    // unknown or unsupported protocol, retun null in that case
                    return null;
                }

            }
            return null;
        } catch (IOException e) {
            log.fatal(e);
            return null;
        }
    }

    public void removeClassPath(File path) {
        classpathset.remove(path);
        clear();
    }

    public void removeJarPath(File path) {
        jarpathset.remove(path);
        clear();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        byte[] classdata = null;
        /* check if cache contains a suitable class ...*/
        if (cache.containsKey(name)) {
            /* return class from cache */
            return cache.get(name);
        }
        /* ... otherwise load class from classpath */
        for (File classpath : classpathset) {
            classdata = getClassImplFromClasspath(classpath, name);
            if (classdata != null) {
                // first found first serve ...
                break;
            }
        }
        /* if class is not yet found, look into jarcontenthash  */
        if (classdata == null && jarcontenthash.containsKey(name)) {
            try {
                classdata = getClassImplFromJar(jarcontenthash.get(name), name);
            } catch (Exception e) {
                /* do nothing and ask parent class loader */
            }
        }
        if (classdata == null) {
            /* check if class is found by parent */
            return super.findClass(name);
        }
        /* create a class from classdate byte [] */
        Class result = defineClass(null, classdata, 0, classdata.length);
        if (result == null) {
            throw new ClassFormatError();
        }
        /* store class in cache */
        cache.put(name, result);
        /* return class */
        return result;
    }

    @Override
    protected URL findResource(String name) {
        URLList urllist = null;
        if (!resourcecache.containsKey(name)) {
            try {
                urllist = (URLList) findResources(name);
            } catch (IOException ex) {
                log.error("IOException while calling findResources(" + name + ") :: " + ex.getMessage());
                //ex.printStackTrace();
                return null;
            }
        } else {
            urllist = resourcecache.get(name);
        }
        return urllist.firstElement();
    }

    /**
     * Overrides ClassLoader.findResources.
     * Attention : Resources searched within classpath and jarpath in the
     * following order
     *
     * @param name
     * @return
     * @throws IOException
     */
    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        /* look if resource contains resource named 'name' */
        if (resourcecache.containsKey(name)) {
            return resourcecache.get(name);
        }
        URLList urllist = new URLList();
        /* otherwise we have to examine the jar's and classpath */

        /*search over all path*/
        for (File cp : classpathset) {
            File tmpres = new File(cp, name);
            if (tmpres.exists() && tmpres.isFile() && tmpres.canRead()) {
                urllist.add(new URL("file:" + cp.toString() + "/" + name));
            }
        }
        /* look into jar file */
        if (jarcontenthash.containsKey(name)) {          
            //urllist.add(getResourceURL(jarcontenthash.get(name), name));
            urllist.add(new URL(jarcontenthash.get(name).toURI()+"#"+name));
        }
        resourcecache.put(name, urllist);
        return urllist;
    }

    /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                        P R I V A T E
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
    private void clear() {
        // clear jar list
        jarlist.clear();
        // clear resource cache
        resourcecache.clear();
        // rebuild list of jars
        for (File jarpath : jarpathset) {
            jarlist.addAll(Arrays.asList(getJarList(jarpath)));
        }
        buildJarContenhash();
    }

    /**
     * This sample function for reading class implementations reads
     * them from the local file system
     */
    private byte[] getClassImplFromClasspath(File path, String className) {
        byte result[];
        try {
            // replace all '.' from className into
            String classname_as_path = className.replace('.', '/');
            log.debug("::" + path + "/" + classname_as_path + ".class");
            FileInputStream fi = new FileInputStream(path + "/" + classname_as_path + ".class");
            result = new byte[fi.available()];
            fi.read(result);
            log.debug("get result (!= null)");
            return result;
        } catch (Exception e) {
            log.debug(e.getMessage());
            /*
             * If we caught an exception, either the class wasn't found or it
             * was unreadable by our process.
             */
            return null;
        }
    }

    /**
     * Get a Class from a Jar file
     *
     * @param jar - Jar File to search in
     * @param className - class to be found
     * @return - the class as  byte [] or null if not found
     */
    private byte[] getClassImplFromJar(File jar, String className) {
        FileInputStream file_in = null;
        try {
            file_in = new FileInputStream(jar);
            JarInputStream jarin = new JarInputStream(file_in);
            JarEntry jarentry;

            // replace all '.' from className into '/'
            String classname_as_path = className.replace('.', '/');
            while ((jarentry = jarin.getNextJarEntry()) != null) {

                // if we found a suitable entry
                if (jarentry.getName().equals(classname_as_path + ".class")) {
                    // check if class size can be handled by an int ...
                    long lsize = jarentry.getSize();
                    if (lsize > Integer.MAX_VALUE) {
                        // throw an exception in that case (if this occurrs you have really a
                        // BIG problem!)
                        throw new IOException("found suitable class in jar, but it is too large to build class from it!");
                    }
                    
                    // sometime the size of an jarentry can't be determined, the getSize()
                    // methods return -1 in that case.
                    
                    
                    // if we got the jarentry size everything is fine ...
                    if (lsize != -1) {
                        
                    

                    // if everything is fine
                    byte[] buffer = new byte[Long.valueOf(lsize).intValue()];

                    // read class from Jar ...
                    int readbytes = 0;
                    while (buffer.length > readbytes) {
                        readbytes += jarin.read(buffer, readbytes, (buffer.length - readbytes));
                    }
                    jarin.closeEntry();
                    jarin.close();
                    // ... and return the byte buffer
                    return buffer;
                    
                    } else {
                        /* if we can't determine jarentries size it is a more complicated ... 
                         * - read jarentry in small buffers (each buffer has  blocksize)
                         * - concat all small buffer to a big one
                         * - return the big one
                         * 
                         * Comment : maybe this implementation isn't efficient as it could be ...
                         * Feel free to optimize it :-) JK 29.06.2012
                         
                         */
                        
                        List<byte[]> lb = new ArrayList<byte[]>();
                        lsize = 0;
                        int tmpread;
                        boolean eof = false;

                        while (!eof) {

                            // read a block
                            int buffread = 0;
                            byte[] buffer = new byte[blocksize];

                            while (!eof && buffread < buffer.length) {
                                tmpread = jarin.read(buffer, buffread, (buffer.length - buffread));
                                if (tmpread == -1) {
                                    eof = true;
                                } else {
                                    lsize = lsize + tmpread;
                                    buffread = buffread + tmpread;
                                }
                            }
                           

                            // store block in List
                            lb.add(buffer);



                        }
                        
                        // close stream
                        jarin.close();
                         
                        // create buffer over all

                        byte[] complete = new byte[Long.valueOf(lsize).intValue()];
                        int counter = 0;
                        for (byte[] ba : lb) {
                            for (byte b : ba) {
                                complete[counter] = b;

                                counter++;
                                if (counter == lsize) {
                                    break;
                                }
                            }
                        }

                        
                       // return complete buffer;
                        return complete;
                       
                    }
                }
            }
            return null;

        } catch (Exception e) {
            /*
             * If we caught an exception, either the class wasn't found or it
             * was unreadable by our process.
             */
            log.debug("could not open or read classes from jar file: " + e.getLocalizedMessage());
            return null;
        }
    }

    private File[] getJarList(File path) {
        File[] tempfile = path.listFiles(new FilenameFilter() {

            public boolean accept(File file, String string) {
                if (string.endsWith(".jar")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        return tempfile;
    }

    private InputStream getResourceAsStreamFromJar(File jar, String resource) throws IOException {
        log.debug("call getResourceAsStreamFromJar(" + jar.toString() + "," + resource + ") ...");
        JarFile jarfile = new JarFile(new File(jar.toString()));
        JarEntry jarentry = jarfile.getJarEntry(resource);
        if (jarentry != null) {
            log.debug("... got suitable jarentry ...");
            return jarfile.getInputStream(jarentry);
        }
        log.debug("... doesn't found suitable jarentry ...");
        return null;
    }

    private URL getResourceURL(File jar, String resource) throws IOException {
        JarInputStream jarin = new JarInputStream(new FileInputStream(jar));
        JarEntry jarentry;
        while ((jarentry = jarin.getNextJarEntry()) != null) {
            if (jarentry.getName().equals(resource)) {
                URL url = new URL(jar.toURI() + "#" + resource);
                jarin.close();
                return url;
            }
        }
        jarin.close();
        return null;
    }

    private void buildJarContenhash() {
        try {
            for (File jar : jarlist) {

                FileInputStream file_in = new FileInputStream(jar);
                JarInputStream jarin = new JarInputStream(file_in);
                JarEntry jarentry;
                while ((jarentry = jarin.getNextJarEntry()) != null) {
                    if (!jarentry.isDirectory()) {
                        String name = jarentry.getName();
                        // entry is a class
                        if (name.endsWith(".class")) {
                            jarcontenthash.put(name.substring(0, name.length() - 6).replace('/', '.'), jar);
                        } else {
                            jarcontenthash.put(name, jar);
                        }
                    }
                }
            }

        } catch (IOException e) {
        }
    }
}
