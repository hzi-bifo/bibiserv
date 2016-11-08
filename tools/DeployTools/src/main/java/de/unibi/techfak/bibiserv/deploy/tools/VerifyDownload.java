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
package de.unibi.techfak.bibiserv.deploy.tools;


import de.unibi.techfak.bibiserv.cms.Tfile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.tools.ant.BuildException;

/**
 * VerifyDownload is small tool class that compares file within a directory
 * and described by the download within the tool description.
 *
 * This class extends the ant Task class and overwrites its execute class.
 *
 *
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 */
public class VerifyDownload extends AbstractVerifyTask {

   
    List<String> download_xml_list = new ArrayList<String>();
    List<String> download_dir_list = new ArrayList<String>();
    private File downloaddir;



    /**
     * Default Constructor 
     */
    public VerifyDownload() {
    }

    /**
     * Constructor of class.
     *
     * @param runnableitemfile
     * @param downloaddir
     * @throws IOException in the case the runnableitem file or download dir doesn't exist or can't be read
     *
     */
    public VerifyDownload(File runnableitemfile, File downloaddir) throws IOException, JAXBException {
        setRunnableitemfile(runnableitemfile);
        setDownloaddir(downloaddir);
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (downloaddir == null) {
            throw new BuildException("Attribute downloaddir is mandatory!");
        }

        
        // if (downloaddir.exists() && !downloaddir.isDirectory() && !downloaddir.canRead()) {
            
        try {
            initialize();
        } catch (JAXBException e){
            throw new BuildException(e);
        }

        List<String> resultlist = new ArrayList<String>();
        if (compareEntries(download_xml_list, download_dir_list, resultlist)){
            System.out.println("Found "+printList(resultlist)+" download elements!");
        } else {
            if (download_xml_list.size() > 0) {
                throw new BuildException("The tooldescription contains downloadable "
                        + "items ("+printList(download_xml_list)+") which are NOT "
                        + "available in the resource directory '"+downloaddir+"' !");
            } else if (download_dir_list.size() > 0) {
                   throw new BuildException("The resource directory '"+downloaddir+"' "
                           + "contains items ("+printList(download_dir_list)+") which "
                           + "are NOT described in the tool description!");
            } else {
                throw new BuildException("Should never occure ....");
            }
        }
       

    }

    /**
     * Initize object using init() method. Method fill the both FileNameList with
     * content.
     *
     * @throws JAXBException  in the case RunnableItemFile can't be parsed into an JAXB class.
     */
    public void initialize() throws JAXBException {
        getDownloadableEntries(download_xml_list);
        
        if (downloaddir.exists() && downloaddir.isDirectory() && downloaddir.canRead()) {
            getDirEntries(downloaddir, download_dir_list);
        }
    }

    public File getDownloaddir() {
        return downloaddir;
    }

    public void setDownloaddir(File downloaddir) throws IOException{
         /* get all entries within download dir */
        this.downloaddir = downloaddir;
       
     
    }

  

    /**
     * Function compareEntries compares two lists (list1 and list2) and return 
     * true if both lists are equal, false otherwise. Attention! Elements that
     * occurrs in both list are removed from it and append to the result list.
     *
     * @param list1 - list containing strings
     * @param list2 - list containing strings
     * @param list3 - an empty, but initalized list
     *
     * @return
     */
    public static boolean compareEntries(List<String> list1, List<String> list2, List<String> list3) {
        int counter = 0;
        while (counter < list1.size()) {
            String elem = list1.get(counter);
            if (list2.contains(elem)) {
                // remove from both lists ...
                list1.remove(elem);
                list2.remove(elem);
                // ... and add to result list
                list3.add(elem);
            } else {
                counter++;
            }
        }
        if (list1.isEmpty() && list2.isEmpty()) {
            return true;
        }
        return false;
    }

   

    /**
     * Static Function getDownloadableEntries determins all downloadable (filename)
     * and pack them into a list.
     *

     * @param list - append all found Downloadable filenames to list
     */
    public void getDownloadableEntries(List<String> list) throws BuildException {
        List<Tfile> lod = getRunnableitem().getDownloadable();
        for (Tfile d : lod) {
            if (d.isSetFilename()) { //local resource
                list.add(d.getFilename());
            } else { // must be an external resource
                
                // check url
                try {
                    URL url = new URL(d.getUrl());
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    InputStream in = connection.getInputStream();
                } catch (MalformedURLException e) {
                    throw new BuildException("Malformed URL : '"+d.getUrl()+"'");
                    
                } catch (IOException e){
                    throw new BuildException("IOException while open connection to '"+d.getUrl()+ "': "+e.getMessage());
                }
                
                
            }
        }
    }

    /**
     * Function getDirEntries determins a list of files within a given
     * directory. Subdirectories are also considered.
     *
     *
     * @param file - Directory to be searched
     * @param list - List all found files
     */
    public static void getDirEntries(File file, List<String> list) {
        getDirEntries(file, file.toString().length() > 0 ? file.toString() + "/" : "", list);
    }

    /**
     * Private Helper function used by fct getDirEntries for recursion steps ...
     *
     * @param file
     * @param prefix
     * @param list
     */
    private static void getDirEntries(File file, String prefix, List<String> list) {
        if (file.isFile()) {
            String fn = file.toString().substring(prefix.length());
            list.add(fn);
        } else if (file.isDirectory()) {
            File[] file_list = file.listFiles();
            for (File f : file_list) {
                getDirEntries(f, prefix, list);
            }
        }
    }



    /**
     * Helper function, returns all elements of list as String
     *
     * @param l
     * @return
     */
     public static String printList(List<String> l) {
         return printList(l,false);
     }
    
    
    public static String printList(List<String> l, boolean br) {
        String s = null;
        StringBuilder sb = new StringBuilder();
        Iterator<String> i = l.iterator();
        while (i.hasNext()) {
            s = i.next();
            sb.append(s);
            if (i.hasNext()) {
                if (br) {
                    sb.append("\n");
                } else {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
    }
}
