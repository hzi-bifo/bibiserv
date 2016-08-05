/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2016 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2016 BiBiServ"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.impl;

import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.Status;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.exception.IdNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.log4j.Logger;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.ExitTimeoutException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;

/**
 * <p>
 * <b>DRMAACall</b> - Class to submit a command  to a GridComputing System (gridengine)
 * using the DRMAA (Distributed Resource Management Application API ) interface.
 * The central function call (see below) blocks until the grid call is (un-)successful
 * finished. Updates the status object.
 * </p>
 * <p>
 * <b>Attention</b> To use/run the DRMAA Java binding, the environment must be set for the
 * JVM running this class. At last <i>$SGE_ROOT</i>, <i>$SGE_EXECD_PORT</i>,
 * <i>SGE_QMASTER_PORT</i> and <i>SGE_CELL</i> must be set. Additional the
 * Shared Object <i>$SGE_ROOT/lib/$ARCH</i> must be in the library path
 * (e.g. setting system property 'java.library.path').
 * </p>
 * <p>
 * <b>Remark</b> Since DRMAA is an general purpose API how to use distributed resources
 * not everything of the GridEngine features is supported by an API function. Special
 * GridEngine features are supported using the native option functions.
 * </p>
 * <p>
 *  Supported/Expected Properties :
 *  <table>
 *      <tr><th>Key</th><th>Comment</th><th>mandatory</th></tr>
 *      <tr><td>drmaa.native.param.admin</td><td>sge qsub (hard) parameter</td><td>no</td></tr>
 *      <tr><td>drmaa.native.param.user</td><td></td><td>no</td></tr>
 *      <tr><td>drmaa.waittime</td><td>Waittime in ms for repoll of DRMAA state</td>no, default is 5000 ms</tr>
 *      <tr><td>drmaa.grid_only</td><td>boolean, if false, each call is executed as short run <b>#</b></td></tr>
 *      <tr><td>lsub.bin</td><td>path to lsub shell script</td><td>yes, if drmaa.grid_only is set to false</td></tr>
 *      <tr><td>lsub.maxcpu</td><td>CPU runtime limitation in seconds</td><td>yes, if drmaa.grid_only is set to false</td></tr>
 *      <tr><td>lsub.maxmem</td><td>Memory usage limitation in XXX </td><td>yes, if drmaa.grid_only is set to false</td></tr>
 *
 * </table>
 *
 * <b>#</b> Submitting a job to a Grid Computing System increases the running time (not CPU time)
 * at minimum of about 5-10 seconds (independent from the grid system load). For the a small job that only
 * needs a few seconds to run on CPU this a big overhead. To increase the "felt" running time, the DRMAA
 * call execute each job as short running job first (limited cpu time and limited memory usage) first.
 * Depending on given runtime (lsub.cputime of 5-10 seconds seems to be a good value) such short running
 * jobs normally represents up to 90 % of all jobs running on BiBiServ. If such a short running job
 * fails it will be started as DRMAA job on compute cluster.
 *
 * </p>
 *
 * <
 * <p><b>Notes:</b> 
 *  <ul>
 *  <li>In principle DRMAA should be independent from the Grid Batch System. In fact we (BiBiServ team) test
 * this implementation only against Sun/Oracle GridEngine Version 6.2.x on Solaris and Linux</li>
 *  <li>It seems to be a good idea using drmaa.jar delivered with the installed DRMAA package. </li>
 *  <li>Be sure that the system property java.library.path point to drmaa c shared library  </li>
 *  </ul>
 * </p>
 *
 *
 * @author Jan Kölling - jkoellin(at)cebitec.uni-bielefeld.de (first release)
 *         Jan Krüger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class DRMAACall extends CallImpl {

    /**
     * private instance of an logger
     */
    private final static Logger log = Logger.getLogger(DRMAACall.class);
    
    /*
     * private static instance of session
     */
    private final static Session session;

    static {
        SessionFactory factory = SessionFactory.getFactory();
        session = factory.getSession();
        try {
            session.init(""); // use the default DRM system
            log.info("DRMAA session initialized!");
            log.debug("Contact   : "+session.getContact());
            log.debug("DRMSystem : "+session.getDrmSystem());
        } catch (Exception | Error e) {
            log.error("Error occured while DRMAA session init.", e);
        }

        /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
         add shutdown hook to exit the session properly
         * -------------------------------------------------------------
         */
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    session.exit();
                    log.info("DRMAA session removed.");
                } catch (Exception e) {
                    log.error("Error occured while DRMAA session remove.",e);
                }
            }
        }));
    }
    
    /**
     * params to show if all conditions/prerequisite to use DRMAA are fullfilled.
     */
    private boolean prerequisite = true;
    /**
     * params for drmaa-call (will load defaults from bibiprops)
     */
    private String userDRMAAParams;

    

    //////////////////////////
    //     Contructors      //
    //////////////////////////
    /**
     * Constructor used by Factory Class 
     */
    public DRMAACall() {
        log.debug("check if mandantory environments variables are set ...");


        /* Test for DRMAA usage prerequisites - from DRMAA documenation ...
         * This class is used to create a SessionImpl instance. In order to use the Grid Engine binding,
         * the $SGE_ROOT environment variable must be set, the $SGE_ROOT/lib/drmaa.jar file must in included
         * in the CLASSPATH environment variable, and the $SGE_ROOT/lib/$ARCH directory must be included in
         * the library path, e.g. LD_LIBRARY_PATH.
         */


        /* SGE_ROOT */
        if (System.getenv("SGE_ROOT") == null) {
            log.error("The enviroment variable $SGE_ROOT must be set and point to SGE dir!");
            prerequisite = false;
        } else {
            log.debug("$SGE_ROOT points to " + System.getenv("SGE_ROOT"));
        }

        /* LD_LIBRARY_PATH or java.library.path*/
        if ((System.getenv("LD_LIBRARY_PATH") == null)  && System.getProperty("java.library.path") == null){
            log.warn("The environment variable $LD_LIBRARY_PATH or Java System Variable 'java.library.path' "
                    + "should be set and contain a path to the $SGE_ROOT/lib/$ARCH folder!");
        } else {
            log.debug("$LD_LIBRARY_PATH contains " + System.getenv("LD_LIBRARY_PATH"));
        }
        log.debug("successfully created DRMAACall object");
    }

    /**
     * Generates a new SGECall object
     * @param bibitools
     */
    public DRMAACall(BiBiTools bibitools) {
        this(bibitools, bibitools.getStatus());
    }

    /**
     * will generate a new DRMAACall-object
     *
     * @param submitted_bibitools
     *            BiBiTools reference to bibitools for getting properties and
     *            logging
     * @param submitted_status
     *            Status reference to status for updating on errors
     */
    public DRMAACall(BiBiTools submitted_bibitools, Status submitted_status) {
        this();
        wsstools = submitted_bibitools;
        status = submitted_status;        
    }

    /////////////////////////////
    // getter / setter methods //
    ////////////////////////////
    /**
     * sets native user parameters for the DRMAA Call for overriding defaults !
     *
     * <br/>All params may have a prefix <i>-soft </i> or <i>-hard </i>depending
     * if parameter recommend or required <br/>
     *
     * Example: <br/><b>-hard arch=solaris64 </b> - SGE/DRMAA will only use
     * x86-64 Solaris machines <br/>
     *
     * @param params - String sgeparams to set
     */
    public void setUserDRMAAParams(String params) {
        if (params != null) {
            userDRMAAParams = params;
        } else {
            log.debug("ignoring UserDRMAAParams setting to null");
        }
    }

    /**
     * @return String of parameters of SGE Call
     */
    public String getUserDRMAAParams() {
        /*
         * get sge qsub (soft) parameter from bibi properties
         */
        if (userDRMAAParams == null) {
            return (wsstools.getProperty("drmaa.native.param.user")!=null)?wsstools.getProperty("drmaa.native.param.user"):"";
        }
        
        return userDRMAAParams;
    }

    /**
     * Returns
     *
     * @return
     */
    public String getBibiDRMAAParams() {
        
        /* get (and set) sge qsub (hard) parameter from bibi properties */
        return (wsstools.getProperty("drmaa.native.param.admin")!= null)?wsstools.getProperty("drmaa.native.param.admin"):"";
       
    }

    ////////////////////////
    //   public methods   //
    ////////////////////////
    /**
     * @param submitted_exec String executeable and params to calls
     * @return boolean true for successful executing or false for an error
     */
    public boolean call(String submitted_exec) {
        return call(new String[]{submitted_exec});
    }

    public boolean call(String[] submitted_exec) {
        try {
            try {
                status.setCallCMDLine(submitted_exec);
                status.setStatuscode(603);

                if (!prerequisite) {
                    log.fatal("Prerequisites for DRMAA usage aren't fullfilled. "
                            + "See previous log messages for detailed description! ");
                    status.setStatuscode(725, "Internal Server Error - BiBiServ Team "
                            + "is informed. Please try again later.", "Prerequisites for DRMAA usage aren't fullfilled. See previous log messages for detailed description!");
                    return false;
                }

                log.info("generate batch file ");

                // generate batch file
                File bf = generateBatchFile(submitted_exec);

                status.setStatuscode(604);
                
                


                /* if sge.grid_only isn't set (or != 'true) try the  localcall strategy */
                if ((wsstools.getProperty("drmaa.grid_only") == null) || !(wsstools.getProperty("drmaa.grid_only").equalsIgnoreCase("true"))) {

                    try {
                        /* check if mandantory properties 'lsub.bin', 'lsub.maxcpu' and 'lsub.maxmem' are set */
                        if (wsstools.getProperty("lsub.bin") == null) {
                            status.setStatuscode(725, "Internal Resource Error", "Property 'lsub.bin' must be set and must be point to a (executable) lsub script!");
                            return false;
                        }
                        if (wsstools.getProperty("lsub.maxcpu") == null) {
                            status.setStatuscode(725, "Internal Resource Error", "Property 'lsub.maxcpu' must be set!");
                            return false;
                        }
                        if (wsstools.getProperty("lsub.maxmem") == null) {
                            status.setStatuscode(725, "Internal Resource Error", "Property 'lsub.maxmem' must be set!");
                            return false;
                        }

                        /* generate lsub call */
                        String lsub[] = {wsstools.getProperty("lsub.bin"), wsstools.getSpoolDir().toString(), bf.getName(), wsstools.getProperty("lsub.maxcpu"), wsstools.getProperty("lsub.maxmem")};

                        /* set STDOUT and STDERR redirect file to status */
                        status.setStdout(bf.getName() + ".stdout");
                        status.setStderr(bf.getName() + ".stderr");
                        
                        File stdOutFile= new File(bf.getPath() + ".stdout");
                        File stdErrFile = new File(bf.getPath() + ".stderr");

                        /*start lsub call */
                        Process localProcess = Runtime.getRuntime().exec(lsub);
                        status.setDrmaaId("0");
                        localProcess.waitFor();
                        log.debug("lsub finished ...");
                        
                        
                        //checking for succes on touchfile SUCCESS_SGE_CALL
                        int returnCode = readReturnValueFile();
                        
                        switch (returnCode) {        
                                    // successful
                            case 0 : setOutpuMessagesCorrect(stdErrFile, stdOutFile, localProcess.getErrorStream(), localProcess.getInputStream());
                                     log.debug("found finished local job");
                                     status.setStatuscode(605);
                                     return true;
                                   // ulimit killed it or or uncompileable! Submit to Grid!
                            case 4 : log.debug("resubmit as grid job");
                                     status.setStatuscode(603);
                                     break;
                                    // not killed by ulimit and no general error, but rather a download, upload, validation, tool or conversion error
                            default : setOutputMessagesError(returnCode, stdErrFile, stdOutFile, bf.getParent());
                                      status.setStatuscode(703, is2string(stderrStream));
                                      return false;
                        }
                       
                        
                    } catch (FileNotFoundException e) {
                        status.setStatuscode(721, "internal Server error", e.getMessage());
                        log.error(status);
                        return false;
                    } catch (IOException e) {
                        status.setStatuscode(721, "internal Server error", e.getMessage());
                        log.error(status);
                        return false;
                    } catch (InterruptedException e) {
                        status.setStatuscode(721, "Process can not sleep.");
                        log.warn(status);
                    }
                }

               
                JobTemplate jobTemplate = session.createJobTemplate();
                jobTemplate.setWorkingDirectory(wsstools.getSpoolDir().toString());
                jobTemplate.setRemoteCommand(bf.toString());
                
                //set drmaa constraints
       
                String nativeSpec = getBibiDRMAAParams().isEmpty()?getUserDRMAAParams():getUserDRMAAParams()+" "+ getBibiDRMAAParams();
                
                if (!nativeSpec.isEmpty()){
                    log.debug("Set native specification : '"+nativeSpec.trim()+"'");
                    jobTemplate.setNativeSpecification(nativeSpec.trim());
                } 
                
                String jobId;
                try {
                    jobId = session.runJob(jobTemplate);
                } catch (DrmaaException e) {
                    log.fatal(e.getMessage(),e);
                    status.setStatuscode(721, "Internal Server Error - The BiBiServ Team is informed. Please try again later", e.getClass().getSimpleName() + " : " + e.getMessage());
                    return false;
                }
                status.setDrmaaId(jobId);
                //set file names for stderr and stdout
                File stdErrFile = new File(bf.getPath() + ".e" + jobId);
                File stdOutFile = new File(bf.getPath() + ".o" + jobId);

                JobInfo jobInfo = null;
                //all valid values for job status are positive
                int jobStatus = -1;
                int tmpStatus;
                // jobInfo is only available if job has finished
                while (jobInfo == null) {
                    //update log and status only if they change
                    tmpStatus = jobStatus;
                    jobStatus = session.getJobProgramStatus(jobId);
                    if (jobStatus != tmpStatus) {
                        switch (jobStatus) {
                            case Session.UNDETERMINED: //Job status is unknown
                                break;
                            case Session.QUEUED_ACTIVE: //Job is pending
                                log.debug("setting to pending " + status.getStatuscode());
                                status.setStatuscode(603);
                                break;
                            case Session.SYSTEM_ON_HOLD:
                            case Session.USER_ON_HOLD:
                            case Session.USER_SYSTEM_ON_HOLD: //Job is on hold
                                //TODO log or remove hold cases
                                break;
                            case Session.RUNNING: //Job is running
                                log.debug("setting to running...");
                                status.setStatuscode(604);
                                break;
                            case Session.SYSTEM_SUSPENDED:
                            case Session.USER_SUSPENDED:
                            case Session.USER_SYSTEM_SUSPENDED: //Job is suspended
                                //TODO log or remove suspend cases
                                break;
                            //the job has finished if one of the two following cases occures
                            case Session.DONE: //Job has completed
                                log.debug("DRMAA job (" + jobId + ") is finished.");
                                break;
                            case Session.FAILED: //Job has failed
                                log.fatal("DRMAA job (" + jobId + ") failed");
                                break;
                        }
                    }

                    /* At last try to get a valid jobInfo object or wait 'drmaa.waittime'
                    miliseconds until try again. Since DRMAA doesn't support to get
                    information about the position in the queue yet, we wait currently a
                    constant ('drmaa.waittime') seconds.*/
                    try {
                        jobInfo = session.wait(jobId, Session.TIMEOUT_NO_WAIT);
                    } catch (ExitTimeoutException ie) {
                        jobInfo = null;
                        try {
                            int waittime = 5000;
                            
                            try { 
                                waittime = Integer.parseInt(wsstools.getProperty("drmaa.waittime"));
                            } catch (NumberFormatException e) {
                                log.warn("BiBitools Property 'drmaa.waittime'  isn't set or can't be parsed as Integer, using default!");
                            }
                            //wait a bit and have another look                            
                            Thread.sleep(waittime);
                        } catch (InterruptedException i) {
                        }
                    }
                }
                /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 * Job has finished, evaluate
                 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 */

                setOutpuMessagesCorrect(stdErrFile, stdOutFile);
                
                /* Get Map of used Resources - 
                   Attention: The returned values depends on used Grid and is *not* 
                   specified in the DRMAA interface specification */
                Map usedResources  = jobInfo.getResourceUsage();
                
                
                if (jobInfo.hasExited()) {
                    
                    File sucReturnFile = getReturnValueFile();
                    if(sucReturnFile.exists() && sucReturnFile.canRead()) {
                        int exitCode = readReturnValueFile();

                        switch (exitCode) {
                            case 0:
                                log.debug("job finished successful (exit status: " + jobInfo.
                                        getExitStatus() + ")");
                                
                                status.setCputime(new Double((Double.parseDouble(usedResources.get("acct_cpu").toString()) * 1000)).longValue());
                                status.setMemory(new Double(Double.parseDouble(usedResources.get("ru_maxrss").toString())).longValue());
                                status.setStatuscode(605);
                                return true;
                            default:
                                setOutputMessagesError(exitCode, stdErrFile, stdOutFile, bf.
                                        getParent());
                                status.setStatuscode(703, is2string(stderrStream));
                               
                        }
                    } else {
                        // failed on grid
                        log.debug("Job finished with errors (exit status " + jobInfo.getExitStatus() + ")");
                        status.setStatuscode(703,   "Your job (" + status.getId() + ") was ended in an unknown error state. It finished with exit code : " + jobInfo.getExitStatus() + ".");
                        return false;
                    }
                } else if (jobInfo.hasSignaled()) {
                    String termSignal = jobInfo.getTerminatingSignal();
                    if (termSignal.equals("SIGKILL")) {
                        status.setStatuscode(705, "Your job (" + status.getId() + ") reached a ressource limit and was killed!");
                        log.error("job (" + status.getId() + " with JID" + status.getDrmaaId() + " was removed from queue because a ressource limit was reached");
                    } else if (termSignal.equals("SIGXCPU")) {
                        //@ToDo distinguish between cpu and mem limit
                        status.setStatuscode(705, "Your job (" + status.getId() + ") reached CPU limit or memory size and was killed!");
                        log.error("job (" + status.getId() + " with JID" + status.getDrmaaId() + " was removed from queue because cpu or memory limit was reached");
                    } else if (termSignal.equals("SIGUSR1")) {
                        log.info("SIGNAL 'SUGURS1' occurs ...");
                        status.setStatuscode(705, "Your job (" + status.getId() + ") was interrupted by a signal");
                    } else if (termSignal.equals("SIGUSR2")) {
                        log.info("SIGNAL 'SIGUSR2 occurs ...");
                        status.setStatuscode(705, "Your job (" + status.getId() + ") was interrupted by a signal");
                    } else {
                        status.setStatuscode(705, "Your job (" + status.getId() + ") was interrupted by a signal");
                    }
                } else if (jobInfo.hasCoreDump()) {
                    log.fatal("Job has core dumped.");
                    status.setStatuscode(725, "Your job (" + status.getId() + ") has been core dumped.");

                } else if (jobInfo.wasAborted()) {
                    log.fatal("Job (ID " + jobId + ") was abborted.");
                    status.setStatuscode(725, "Internal Server Error - BiBiServ Team is informed! Please try again later!", "");
                } else {
                    log.debug("the exit status of Job (ID " + jobId + ") is unknown.");
                    status.setStatuscode(703, "The exit status of Job (ID " + jobId + ") is unknown.");
                    
                }

                return false;
                
            } catch (DrmaaException |IOException e) {
                log.fatal("A " + e.getClass().getSimpleName() + " exeception ocurrs with message '" + e.getMessage() + "'",e);
                status.setStatuscode(725, "Internal Server Error - BiBiServ Team is informed. Please try again later.", e.getClass().getName() + " :: " + e.getMessage());
                return false;
            } 

        } catch (DBConnectionException | IdNotFoundException e) {
            log.fatal(e.getMessage(),e);
            return false;
        }
    }
}
