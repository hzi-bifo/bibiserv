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

package de.unibi.techfak.bibiserv.impl;

import de.unibi.techfak.bibiserv.Call;
import de.unibi.techfak.bibiserv.Status;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.exception.IdNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.apache.log4j.Logger;

/**
 * Incomplete implementation (abstract class) of the Call interface as basis 
 * for LocalCall, SGECall and DRMAACall classes.
 * 
 * 
 * @author Jan Krueger (jkrueger@techfak.uni-bielefeld.de)
 */
public abstract class CallImpl implements Call {

    /**
     * private static reference to a Logger object
     */
    private static Logger log = Logger.getLogger(CallImpl.class);
    
    /**
     * Reference of a Status object 
     */
    protected Status status;
    /**
     * Reference of a BiBiTools object
     */
    protected BiBiTools wsstools;
    
    /**
     * STDOUT Stream of called program 
     */
    protected InputStream stdoutStream;
    /**
     * STDERR Stream of called program 
     */
    protected InputStream stderrStream;

    /**
     * Generates a batchfile within the current spool directory.
     * The following properties (stored within the environment) are
     * used from this  method.
     * <table>
     * <tr><th>property</th><th>description</th></tr>
     * <tr><td>batchfile.concat</td> <td>bool concation within the shell</td></tr>
     * <tr><td>batchfile.shell</td><td>(path to) shell used for batchshell execution</td></tr>
     * <tr><td>batchfile.prefix</td><td>prefix of generated batchfile name <pre><b>PREFIX</b>genidSUFFIX</pre></td></tr>
     * <tr><td>batchfile.suffix</td><td>suffix of generated batchfile name <pre>PREFIXgenid<b>SUFFIX</b></pre></td></tr>
     * <tr><td>chmod.bin</td><td>path to chmod executable</td></tr>
     * <tr><td>chmod.param</td><td>chmod mode parameters</td></tr> 
     *   
     * </table>
     * 
     * @param exec
     * 
     * @return Returns a File object linked to created BatchFile
     */
    protected File generateBatchFile(String[] exec) throws DBConnectionException{


        try {
            //check for bad chars within exec
            for (int i = 0; i < exec.length; i++) {
                if (exec[i] == null) {
                    continue;
                }
                if (exec[i].matches(".*(&&|;).*")) {
                    status.setStatuscode(721,"Internal Resource Error","Illegal characters in exec of Call");
                    log.error(status);
                    return null;
                }
                exec[i] = exec[i] + " || exit 4";
            }
          

            log.info ("create");
            //creating batch file to call by SGE

            File batchFile = File.createTempFile(wsstools.getProperty("batchfile.prefix"), wsstools.getProperty("batchfile.suffix"), wsstools.getSpoolDir());
            PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(batchFile)));
            w.println("#!"+wsstools.getProperty("batchfile.shell"));
            w.println("timestart=`date`");
            w.println("");
            
            // the exit code needs to be written to a file in order to find out if the grid caused
            // the abort or if it was the script
            w.println("#make alias usable");
            w.println("shopt -s expand_aliases");
            w.println("#create a function that writes retun code to file and then exits");
            w.println("exitWithFile() {");
            w.println("\tunalias exit");
            w.println("\techo $1 > "+wsstools.getSpoolDir() + "/$JOB_ID.RETURN_VALUE");
            w.println("\texit $1");
            w.println("}");
            w.println("#alias exit, every retun value is written to file");
            w.println("alias exit=exitWithFile");
            w.println("");
            
            // change to spool dir 
            w.println("cd "+wsstools.getSpoolDir());
            w.println("");
            
            for (int i = 0; i < exec.length; i++) {
                if (exec[i] == null) {
                    log.info("Ignoring null-String on Batch Call (item no." + i + ")");
                    continue;
                }
                w.println(exec[i]);

            }
            log.info("touch");
            // create touchfile "$JOB_ID.SUCCESS_EXEC"
            w.println("# write status files for finished job");
            w.println("echo concat of execs succ terminated > " + wsstools.getSpoolDir() + "/$JOB_ID.SUCCESS_EXEC");
            w.println("echo starttime:$timestart stoptime:`date` running on: $HOSTNAME > " + wsstools.getSpoolDir() + "/$JOB_ID.SUCCESS_SGE_CALL");
            // needed, so that the return value file is written
            w.println("exit 0");
            
            
            // 
            w.close();
            log.debug("batchfile created: " + batchFile.toString());
            batchFile.setExecutable(true,false);
            batchFile.setReadable(true,false);
            log.debug("chmod on batchfile done");
            return batchFile;
        } catch (Exception e) {
            log.fatal("error on creating batchfile: " + e.getMessage(),e);
            status.setStatuscode(723);
            return null;
        }




    }
    
    public void setOutpuMessagesCorrect(File stdErrfile, File stdInFile) throws FileNotFoundException{
        setOutpuMessagesCorrect(stdErrfile, stdInFile, new ByteArrayInputStream("".getBytes()), new ByteArrayInputStream("".getBytes()));
    }
    
    
    public void setOutpuMessagesCorrect(File stdErrfile, File stdInFile, InputStream err, InputStream out) throws FileNotFoundException {
        if (stdInFile.exists() && stdInFile.canRead()) {
            stdoutStream = new FileInputStream(stdInFile);
        } else {
            stdoutStream = out;
        }
        
        if (stdErrfile.exists() && stdErrfile.canRead()) {
            stderrStream = new FileInputStream(stdErrfile);
        } else {
            stderrStream = err;
        }
    }
    
    
    public void setOutputMessagesError(int returnValue, File stdErrfile, File stdInFile, String path) {
        
        try {
            
            // set error file according to returnValue
            // 3 for s3/wget errors
            // 4 for toolerror
            // 5 for upload error
            // 6+ for validation and conversion
           switch(returnValue) {
               case -1:
                   stderrStream = getStreamOfFileWithMessage(stdErrfile, "Execution of the tool failed as well as the error detection functionality. Message was: ");
               case 0:
                   setStErrStreamToFile(stdErrfile);
                   break;
               case 1:
               case 2:
                   stderrStream = getStreamOfFileWithMessage(stdErrfile, "Execution of the script managing inputs and uploads failed. Error code was "+returnValue+". Message was: ");
                   break;
               case 3:
                   stderrStream = getStreamOfFileWithMessage(stdErrfile, "Download of one or more Inputs failed: ");
                   break;
               case 4:
                   stderrStream = getStreamOfFileWithMessage(stdErrfile, "Execution of the tool failed. Message was: ");
                   break;
               case 5:
                   stderrStream = getStreamOfFileWithMessage(stdErrfile, "Uploading the results failed. Message was: ");
                   break;
               default:   // >= 6
                   setStErrStreamToFile(new File(path, "error"+returnValue+".err"));
                   break;
           }
                           
           // set normal output 
           if (stdInFile.exists() && stdInFile.canRead()) {
               stdoutStream = new FileInputStream(stdInFile);
           } else {
               stdoutStream = new ByteArrayInputStream("".getBytes());
           }
            
        } catch (IOException ex) {
            stderrStream = new ByteArrayInputStream("Error file could not be read.".getBytes());
            stdoutStream = new ByteArrayInputStream("".getBytes());
        }
  
    }
    
    private void setStErrStreamToFile(File file) throws FileNotFoundException {
        if(file.exists() && file.canRead()){
            stderrStream = new FileInputStream(file);
        } else {
            stderrStream = new ByteArrayInputStream("Error file could not be read.".getBytes());
        }
    }
    
    private InputStream getStreamOfFileWithMessage(File file, String message) throws FileNotFoundException, IOException {
        if(file.exists() && file.canRead()){
             FileInputStream fileInputStream = new FileInputStream(file);
             message += is2string(fileInputStream);
        } else {
            message+="Error file could not be read.";
        }
        return new ByteArrayInputStream(message.getBytes());
    }
     
    
    protected File getReturnValueFile() throws FileNotFoundException{
        try {
            return new File(wsstools.getSpoolDir().toString() + "/" + status.getDrmaaId() + ".RETURN_VALUE");
        } catch (DBConnectionException | IdNotFoundException ex) {
            throw new FileNotFoundException();
        } 
    }
    
    protected int readReturnValueFile() throws FileNotFoundException, IOException{
        
        File file = getReturnValueFile();
        if(file.exists()) {
            if(file.canRead()) {
                String content = is2string(new FileInputStream(file));
                try{
                    return Integer.parseInt(content);
                } catch(NumberFormatException ex) {
                }
            }
        } else {
            return 2;
        }
        return -1;
    }
     
       /**
     * converts a stream to a string
     * 
     * @param in
     *            InputStream to convert
     * @return String contains data of InputStream
     * @exception IOException
     *                if occours
     */
    protected String is2string(InputStream in) throws IOException {
        StringBuilder buf_s = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = r.readLine()) != null) {
            buf_s.append(line);
        }
        return buf_s.toString();
    }  
    
    
        /////////////////////////
    // getter / setter methods
    /////////////////////////
    /** 
     * returns the stream to STDOUT of system call.
     */    
    @Override
    public InputStream getStdOutStream() throws IOException {
        return stdoutStream;

    }

    /** 
     * returns the stream to STDERR of system call.
     */  
    @Override
    public InputStream getStdErrStream() throws IOException {
        return stderrStream;
    }

    /**
     * Set a status object to current Call object.
     * 
     * @param status
     */
    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Set a bibitools objetc to current Call object
     *
     * @param bibitools
     */
    @Override
    public void setBiBiTools(BiBiTools bibitools) {
        this.wsstools = bibitools;
    }




}
