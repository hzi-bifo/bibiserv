/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2013 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger, jkrueger(at)cebitec.uni-bielefeld.de
 *
 */
package de.unibi.techfak.bibiserv.impl;

import de.unibi.techfak.bibiserv.*;
import java.io.IOException;

import org.apache.log4j.Logger;

import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import java.io.File;
import java.io.FileInputStream;

/**
 * Implements the Call interface executing a program directly on the local
 * machine.<br/>
 *
 * <b>Don't use this if a Grid exists - use SGECall (for SGE 5.3), DRMAACAll
 * (for SGE6 and any DRMMA comptibles grid) or self implemented class instead.
 * The intention of this implementation is testing without having a grid
 * environment. Using this implementation in a <i>real world scenario</i> may
 * blow up the machine running the application server !</b><br/>
 *
 *
 * Example: <br/>
 * <pre>
 *  CallFactory cf = CallFactory.newInstance();
 *  cf.setFeature("local","true);
 *  Call call = cf.newCall(wsstools);
 *  boolean success = call.call("echo hallo");
 *  if(!success) {
 *    return;
 *  }
 * </pre>
 *
 * <b>or</b> <br/>
 *
 *
 * Set the property "DefaultCallClass" to
 * "de.unibi.techfak.bibiserv.impl.LocalCall". <br/>
 *
 *
 * First version was from Henning Mersch. Updated version now implements the
 * Call interface and is part of the default Call/CallFactory Implementation.
 *
 * @author Jan Krueger (jkrueger@cebitec.uni-bielefeld.de)
 *
 */
public class LocalCall extends CallImpl {

    /**
     * private static instance of an logger
     */
    private static Logger log = Logger.getLogger(LocalCall.class);
    private Integer maxruntime;
    private Integer maxmem;
    /////////////////////////
    //Contructors
    //////////////////////////

    /**
     * Constructor used by Factory Class
     */
    public LocalCall() {
    }

    /**
     * Default Constructor! Will generate a new LocalCall object.
     *
     * @param wsstools BiBiTools reference to a BiBiTools object
     */
    public LocalCall(BiBiTools wsstools) {
        this(wsstools, wsstools.getStatus(), null, null);
    }

    public LocalCall(BiBiTools wsstools, Integer runtime, Integer maxmem) {
        this(wsstools, wsstools.getStatus(), runtime, maxmem);
    }

    public LocalCall(BiBiTools submitted_wsstools, Status submitted_status) {
        this(submitted_wsstools, submitted_status, null, null);
    }

    /**
     * will generate a new LocalCall-object
     *
     * @param submitted_wsstools BiBiTools reference to wsstools for getting
     * properties and logging
     * @param submitted_status Status reference to status for updating on errors
     * @param submitted_maxruntime overides property defaults (measured in
     * seconds), -1 means infinity
     * @param submitted_maxmem overrides property defaults (measured in mb), -1
     * means infinity
     */
    public LocalCall(BiBiTools submitted_wsstools, Status submitted_status, Integer submitted_maxruntime, Integer submitted_maxmem) {
        log.debug("LocalCall object instantiate");
        wsstools = submitted_wsstools;
        status = submitted_status;
        maxruntime = submitted_maxruntime;
        maxmem = submitted_maxmem;

    }

    /////////////////////////
    // public methods
    /////////////////////////
    /**
     * calls a program.<br/> STDOUT and STDERR of the exec will be redirected to
     * stdoutFilename and stderrFilename<br/> sets status.Statuscode when
     * required<br/> returns AFTER finishing the Job<br/>
     *
     * @param exec String of executeable and params to call
     * @return boolean true for successful executing or false for an error
     */
    @Override
    public boolean call(String exec) {
        return call(new String[]{exec});
    }

    @Override
    public boolean call(String[] exec) {
        int runtime, mem;
        try {
            status.setCallCMDLine(exec);
            status.setStatuscode(603);
            File bf = generateBatchFile(exec);
            status.setStatuscode(604);
            try {
                /* local uses the lsub shell scripts which redirects
                 * stdout and stderr to batchfile.name.stdout/stderr and
                 * limits the process to lsub.maxmem memory and lsub.maxcpu
                 * cputime consumption */
                if (wsstools.getProperty("lsub.bin") == null) {
                    status.setStatuscode(725, "Internal Resource Error", "Property 'lsub.bin' must be set and must be point to a (executable) lsub script!");
                    return false;
                }
                if (maxruntime == null) {
                    if (wsstools.getProperty("lsub.maxcpu") == null) {
                        status.setStatuscode(725, "Internal Resource Error", "Property 'lsub.maxcpu' must be set!");
                        return false;
                    }
                    runtime = Integer.parseInt(wsstools.getProperty("lsub.maxcpu"));
                } else {
                    runtime = maxruntime;
                }
                if (maxmem == null) {
                    if (wsstools.getProperty("lsub.maxmem") == null) {
                        status.setStatuscode(725, "Internal Resource Error", "Property 'lsub.maxmem' must be set!");
                        return false;
                    }
                    mem = Integer.parseInt(wsstools.getProperty("lsub.maxmem"));
                } else {
                    mem = maxmem;
                }

                String lsub[] = {wsstools.getProperty("lsub.bin"), wsstools.getSpoolDir().toString(), bf.getName(), "" + runtime, "" + mem};
                /* set STDOUT and STDERR redirect file to status */
                status.setStdout(bf.getName() + ".stdout");
                status.setStderr(bf.getName() + ".stderr");

                File stdOutFile = new File(bf.getPath() + ".stdout");
                File stdErrFile = new File(bf.getPath() + ".stderr");

                /* start process */
                Process execProcess = Runtime.getRuntime().exec(lsub);
                int returnCode = execProcess.waitFor();

                File sucReturnFile = getReturnValueFile();

                if (returnCode == 0 && sucReturnFile.exists()) { //finished ok
                    log.info("Execution of " + bf.toString() + " successfull!");
                    // check if stout|stderr redirection exists ...
                    // otherwise use stdout/stderr from process
                    setOutpuMessagesCorrect(stdErrFile, stdOutFile, execProcess.getErrorStream(), execProcess.getInputStream());

                    return true;
                } else {
                    returnCode = readReturnValueFile();
                    setOutputMessagesError(returnCode, stdErrFile, stdOutFile, bf.getParent());
                    status.setStatuscode(703, is2string(stderrStream));
                    return false;
                }
            } catch (InterruptedException e) {
                status.setStatuscode(721, "Error on exec : " + e.getMessage());
                log.error(status);
                return false;

            } catch (IOException e) {
                status.setStatuscode(721, "IOError while exec " + bf.toString() + " " + e.getMessage());
                log.error(status);
                return false;
            }

        } catch (DBConnectionException e) {
            e.printStackTrace();
            return false;
        }


    }
    /////////////////////////
    // private methods
    /////////////////////////
}
