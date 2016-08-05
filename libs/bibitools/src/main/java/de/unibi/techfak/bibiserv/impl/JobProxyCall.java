/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2016 BiBiServ"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.impl;

import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.Status;
import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.exception.IdNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.*;

/**
 * <p>
 * <b>JobProxyCall</b> - Class to submit a command to a running JobProxy
 * (https://github.com/jkrue/jobproxy) instance using REST API.
 *
 * The central function call (see below) blocks until the jobproxy call is
 * (un-)successful finished. Updates the status object.
 * </p>
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class JobProxyCall extends CallImpl {

    // JobProxy Server URI
    private URI uri;

    private static Logger LOG = Logger.getLogger(JobProxyCall.class);

    /////////////////////////
    //Contructors
    //////////////////////////
    /**
     * Constructor used by Factory Class
     */
    public JobProxyCall() {
    }

    /**
     * Default Constructor! Will generate a new JobProxyCall object.
     *
     * @param wsstools BiBiTools reference to a BiBiTools object
     */
    public JobProxyCall(BiBiTools wsstools) {
        this(wsstools, wsstools.getStatus());
        // get servr JobProxyServer from 
        try {
            uri = new URI(wsstools.getProperty("JobProxyServer.URI", "http://localhost:9999/"));
        } catch (URISyntaxException e) {
            try {
                LOG.error(e.getMessage() + " Use default 'http://localhost:9999/' instead!");
                uri = new URI("http://localhost:9999/");
            } catch (URISyntaxException ex) {
                // should not occure
            }
        }

    }

    public JobProxyCall(BiBiTools submitted_wsstools, Status submitted_status) {
        wsstools = submitted_wsstools;
        status = submitted_status;
    }

    @Override
    public boolean call(String exec) {
        return call(new String[]{exec});
    }

    @Override
    public boolean call(String[] exec) {
        try {
            status.setCallCMDLine(exec);
            status.setStatuscode(603);
            File bf = generateBatchFile(exec);

            // build task string
            JSONObject task = new JSONObject();
            task.put("user", System.getProperty("user.name"));
            if (cores != null) {
                task.put("cores",cores);
            }
            if (mem != null ) {
                task.put("memory",mem);
            }
            if (cputime != null) {
                task.put("cputime",cputime);
            }
            task.put("cmd", bf.toString());
            task.put("stdout", bf.getParent());
            task.put("stderr", bf.getParent());

            /* wait a defined period of time to be sure that the generated
               spooldir is available on every host   */
            try {
                
                Thread.sleep(Long.parseLong(wsstools.getProperty("precall.wait", "2000")));
            } catch (InterruptedException e) {
                LOG.error("Can't sleep...");
            }
            
            // call submit
            String id = submit(task.toString());

            int statuscode = wsstools.getStatus().getStatuscode();
            while (statuscode > 600 && statuscode < 700) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOG.error("Can't sleep...");
                }
                state(id);
                statuscode = wsstools.getStatus().getStatuscode();
            }

            // set filenames for stdout and stderr
            File stdErrFile = new File(bf.getPath() + ".e" + id);
            File stdOutFile = new File(bf.getPath() + ".o" + id);

            // Wait up to 10 seconds to give shared fs time to synchronize between hosts
            int waitcounter = 0;
            while (!(stdErrFile.exists() && stdOutFile.exists())) {
                waitcounter++;
                if (waitcounter == 5) {
                    wsstools.getStatus().setStatuscode(723); // internal error (filesystem)
                    return true;
                }
                // wait 2 seconds ...
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    LOG.error("Can't sleep ...");
                }
            }

            // finished 
            if (statuscode == 600) {
                setOutpuMessagesCorrect(stdErrFile, stdOutFile);
            } else { // error
                setOutputMessagesError(statuscode, stdErrFile, stdOutFile, bf.getParent());
                wsstools.getStatus().setStatuscode(statuscode, BiBiTools.i2s(new InputStreamReader(new FileInputStream(stdErrFile))));
            }
            return true;

        } catch (DBConnectionException | BiBiToolsException | IdNotFoundException | IOException e) {
            LOG.error(e.getMessage(), e);

        }
        return false;
    }
    
    
    private Integer cores = null;
    private Integer mem = null;
    private Integer cputime = null;
    
    /**
     * Set number of cores reserved for this job. Only values > 1 are considered.hg push
     * 
     * @param cores 
     */
    public void setCores(int cores){
        this.cores = cores;
    }
    
    /**
     * Set amount of memory (in GB) reserved for this job. Only value > 1 are considered.
     * 
     * @param mem 
     */
    public void setMemory(int mem) {
        this.mem = mem;
    }
    
    
    /**
     * Set an upper limit for CPU time consumed by this task.
     * 
     * @param cputime 
     */
    public void setCPUTime(int cputime) {
        this.cputime = cputime;
    }

    private String submit(String task) throws DBConnectionException, BiBiToolsException {
        try {
            URL url = new URL(uri.toString() + "v1/jobproxy/submit");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(task.getBytes());
            os.flush();

            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                // set the status object
                wsstools.getStatus().setStatuscode(700, "HTTP error : " + conn.getResponseCode() + " -- " + conn.getResponseMessage());

            }

            // get id from response ...
            String id = BiBiTools.i2s(new InputStreamReader(conn.getInputStream()));
            // save it in status object ...
            wsstools.getStatus().setDrmaaId(id);

            conn.disconnect();

            // and return it
            return id;
        } catch (MalformedURLException ex) {
            wsstools.getStatus().setStatuscode(700, "MalformedURLException: " + ex.getMessage());
        } catch (ProtocolException ex) {
            wsstools.getStatus().setStatuscode(700, "ProtocolException: " + ex.getMessage());
        } catch (IOException ex) {
            wsstools.getStatus().setStatuscode(700, "IOException: " + ex.getMessage());
        }
        throw new BiBiToolsException(wsstools.getStatus());
    }

    private void state(String id) throws MalformedURLException, DBConnectionException, IOException {
        URL url = new URL(uri.toString() + "v1/jobproxy/state/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");

        conn.connect();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

            // set the status object
            wsstools.getStatus().setStatuscode(700, "HTTP error : " + conn.getResponseCode() + " -- " + conn.getResponseMessage());

        }

        try {
            JSONObject obj = new JSONObject(BiBiTools.i2s(new InputStreamReader(conn.getInputStream())));
            switch (obj.getInt("code")) {
                case 16:
                    wsstools.getStatus().setStatuscode(603, obj.getString("description"));
                    break;
                case 32:
                    wsstools.getStatus().setStatuscode(604, obj.getString("description"));
                    break;
                case 48:
                    wsstools.getStatus().setStatuscode(600, obj.getString("description"));
                    break;
                case 64:
                    wsstools.getStatus().setStatuscode(700, obj.getString("description"));
                    break;
                default:
                    wsstools.getStatus().setStatuscode(603, obj.getString("description"));
                    break;
            }
        } catch (JSONException e) {
            // unknown id
            wsstools.getStatus().setStatuscode(706, "Id unknown");
        }

    }

}
