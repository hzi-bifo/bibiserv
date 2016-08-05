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
package de.unibi.techfak.bibiserv;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.exception.IdNotFoundException;
import de.unibi.techfak.bibiserv.util.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *  Status - represents a status of a WS-Call / job<br/>
 *
 *  @author Henning Mersch <hmersch@techfak.uni-bielefeld.de> (inital release, 2004)
 *  @author Jan Krueger <jkrueger@techfak.uni-bielefeld.de> (second release, 2008)
 *  @version $Id: Status.java,v 1.33 2005/12/02 14:26:24 jkrueger Exp jkrueger $
 *
 */
public class Status {

    /**
     * Time in milliseconds that has to be waited beetween data refresh calls.
     */
    private static final int REFRESH_WAIT = 1000;
    
    /**
     * String of chars for generating unique random part of bibiid
     */
    private static final String ID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    /**
     * reference to bibitools object for logging
     */
    private BiBiTools bibitools;
    /**
     * identification string of this run 
     */
    private String id = new String();
    /** 
     * current statuscode of this webservice
     */
    private int statuscode;
    /** 
     * description of current statuscode
     */
    private String description = new String();
    /** 
     * internal description of current statuscode
     */
    private String internalDescription = new String();
    /** 
     * JobID of current DRMAACall
     */
    private String drmaaId = "0";
    /** 
     * last modification time of status
     */
    private Date lastMod;
    /** 
     * creation time of status
     */
    private Date createdDate;
    /** 
     * name (as string) of file storing jobs stdout 
     */
    private String stdout;
    /**
     * name (as string) of file storing jobs stderr
     */
    private String stderr;

    /**
     * name (as string) of user started this job
     */
    private String userid;
    
    
    /**
     * data from downloaduploadstatus table for up- and download progress
     */
    private List<Pair<String,String>> uploadDownloadData = new ArrayList<>();

    /**
     * cputime (in milliseconds) consumed by this job, must be updated by Call.call or manually
     */
    private long cputime = -1;

    /**
     * diskspace (in bytes) consumed by this job, must be updated by Call.call or manually
     */
    private long diskspace = -1;

    /**
     * memory (in Kbytes) consumed by this job, must be updated by Call.call or manually
     */
    private long memory = -1;
    
    
    /**
     * type of interface used, in case of BiBiServ that could be one of REST or WEB
     */
    private String interfaceType = "unknown";
    
    
    /**
     * contains callData
     */
    private String callCMDLine =  null;
    
    
    /**
     * for use while storing to DB (createdDate and lastMod)
     */
    private final SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * for generating ID (no spaces)
     */
    private final SimpleDateFormat idSdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");

    /**
     * private static instance of an logger
     */
    private final static Logger log = Logger.getLogger("de.unibi.techfak.bibiserv.Status");
    /**
     * Last time the data has been refreshed in system milliseconds.
     */
    private long refreshTime = 0; 
    
    /////////////////////////
    //Contructors
    ////////////////////////
    /** 
     * will generate a new status object, which represents a status of a webservice call<br/>
     * @param bibitools
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     */
    public Status(BiBiTools bibitools) throws DBConnectionException {
        this.bibitools = bibitools;

        //generate a DB entry and set defaults.
        insertDBStatus();
        log.debug("generated new status");

    }

    /** 
     * will retrieve a existing status object from DB, which represents a status of a webservice call
     * returns null if id is not found
     * @param bibitools reference to bibitools for proper logging
     * @param id id of the status to retrieve from DB
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     * @exception de.unibi.techfak.bibiserv.exception.IdNotFoundException thrown if the requested id is not in the DB
     */
    public Status(BiBiTools bibitools, String id) throws DBConnectionException, IdNotFoundException {
        this(bibitools, id, (DataSource) null);
    }

    public Status(BiBiTools bibitools, String id, DataSource ds) throws DBConnectionException, IdNotFoundException {
        this.bibitools = bibitools;
        this.id = id;
        //get a status from DB
        getDBStatus();
    }

    /////////////////////////
    //Getter / Setter Methods
    ////////////////////////      
    /** 
     * returns current id
     * @return String current id
     */
    public String getId() {
        return id;
    }

    /** 
     * sets the statuscode and tries to get matching description from bibi.properties and tool.properties
     * @param submitted_statuscode int the statuscode to set
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    public void setStatuscode(int submitted_statuscode) throws DBConnectionException {
        if (statuscode == submitted_statuscode) {
            return;
        } // ignore if no update
        statuscode = submitted_statuscode;
        // set status description
        if (bibitools.getProperty("statuscode." + statuscode) != null) {
            description = bibitools.getProperty("statuscode." + statuscode);
        } else {
            description = bibitools.getProperty("statuscode.undef");
        }
        // set status internal description     
        if (bibitools.getProperty("statuscode." + statuscode + ".internal") != null) {
            internalDescription = bibitools.getProperty("statuscode." + statuscode + ".internal");
        } else {
            internalDescription = bibitools.getProperty("statuscode.undef.internal");
        }
        lastMod = new Date();
        updateDBStatus();
    }

    /** 
     * sets the description from bibi.properties and tool.properties
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    @Deprecated
    public void setDescription() throws DBConnectionException {
        log.error ("FCT is deprecated and should not longer used !");
    }


    
    /** 
     * sets the internal description from bibi.properties and tool.properties
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    @Deprecated
    public void setInternalDescription() throws DBConnectionException {
        log.error("FCT is deprecated and should not longer used !");
    }

    /** 
     * sets the statuscode, description and interal-description like description
     * @param submitted_statuscode int the statuscode to set
     * @param submitted_description String description to override default from bibi.properties
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    public void setStatuscode(int submitted_statuscode, String submitted_description) throws DBConnectionException {
        setStatuscode( submitted_statuscode, submitted_description, submitted_description);
    }

    /** 
     * sets the statuscode, description and interal-description (defined by user)
     * @param submitted_statuscode int the statuscode to set
     * @param submitted_description String description to override default from bibi.properties
     * @param submitted_internal_description String description to override default from bibi.properties
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    public void setStatuscode(int submitted_statuscode, String submitted_description, String submitted_internal_description) throws DBConnectionException {
        if (statuscode == submitted_statuscode && description.equals(submitted_description) && internalDescription.equals(submitted_internal_description)) {
            return;
        } // ignore if no update
        statuscode = submitted_statuscode;
        description = submitted_description;
        internalDescription = submitted_internal_description;
        lastMod = new Date();
        updateDBStatus();
    }

    /** 
     * returns current statuscode
     * @return int current statuscode 
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException 
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException 
     */
    public int getStatuscode() throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return statuscode;
    }

    /** 
     * sets description and internal-description to description (without modifying statuscode)
     * @param submitted_description String description to override default from bibi.properties
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    @Deprecated
    public void setDescription(String submitted_description) throws DBConnectionException {
        if (description.equals(submitted_description)) {
            return;
        } // ignore if no update
        description = submitted_description;
        internalDescription = submitted_description;
        lastMod = new Date();
        updateDBStatus();
    }

    /** 
     * sets internal-description without modifying description (without modifying statuscode)
     * @param submitted_internal_description String description to override default from bibi.properties
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    @Deprecated
    public void setInternalDescription(String submitted_internal_description) throws DBConnectionException {
        if (internalDescription.equals(submitted_internal_description)) {
            return;
        } // ignore if no update
        internalDescription = submitted_internal_description;
        lastMod = new Date();
        updateDBStatus();
    }

    /** 
     * sets description and internal-description (without modifying statuscode)
     * @param submitted_description String description to override default from bibi.properties
     * @param submitted_internal_description String description to override default from bibi.properties
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    @Deprecated
    public void setDescription(String submitted_description, String submitted_internal_description) throws DBConnectionException {
        if (description.equals(submitted_description) && internalDescription.equals(submitted_internal_description)) {
            return;
        } // ignore if no update
        description = submitted_description;
        internalDescription = submitted_internal_description;
        lastMod = new Date();
        updateDBStatus();
    }

    /** 
     * returns current description of statuscode
     * @return String current description of status
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    public String getDescription() throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return description;
    }

    /** 
     * returns current internal-description of statuscode
     * @return String current internal description of status
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    public String getInternalDescription() throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return internalDescription;
    }

    /** 
     * setDRMAAId sets DRMAAId 
     * @param submitted_drmaaid String drmaaid to set
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    public void setDrmaaId(String submitted_drmaaid) throws DBConnectionException {
        if (drmaaId.equals(submitted_drmaaid)) {
            return;
        } // ignore if no update
        drmaaId = submitted_drmaaid;
        updateDBStatus();
    }

    /** 
     * returns current JID in SunGridEngine
     * @return String SunGridEngine-JID
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    public String getDrmaaId() throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return drmaaId;
    }

    /** 
     * returns creation date of WS call
     * @return Date creation date of status
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    //no setCreatedDate - makes no sense, automatically generated !
    public Date getCreatedDate() throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return createdDate;
    }

    /** 
     * returns last modification time of status
     * @return Date date of last modification
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    //no setLastMod Date - makes no sense, automatically handeled !
    public Date getLastModDate() throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return lastMod;
    }

    /**
     * Set FileName for STDOUT redirect.
     * 
     * @param name as string 
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException 
     */
    public void setStdout(String name) throws DBConnectionException {
        if (name.equals(stdout)) {
            return;
        }
        stdout = name;
        updateDBStatus();
    }

    /**
     * Set FileName for STDOUT redirect.
     * 
     * @param file as file 
     * @throws DBConnectionException
     */
    public void setStdout(File file) throws DBConnectionException {
        setStdout(file.getName());
    }

    /**
     * Return a file object containing the full path to STDOUT redirect.
     * 
     * @return Return a file object containing the full path to STDOUT redirect.
     * @throws java.io.FileNotFoundException
     */
    public File getStdout() throws FileNotFoundException {
        return new File(bibitools.getSpoolDir(), System.getProperty("file.separator") + stdout);
    }

    /**
     * Set FileName for STDERR redirect.
     * 
     * @param name as string
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     */
    public void setStderr(String name) throws DBConnectionException {
        if (name.equals(stderr)) {
            return;
        }
        stderr = name;
        updateDBStatus();
    }

    /**
     * Set FileName for STDERR redirect.
     * 
     * @param file as file
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     */
    public void setStderr(File file) throws DBConnectionException {
        setStderr(file.getName());
    }

    /**
     * Return a file object containing the full path to STDOUT redirect.
     * 
     * @return Return a file object containing the full path to STDOUT redirect.
     * @throws java.io.FileNotFoundException
     */
    public File getStderr() throws FileNotFoundException {
        return new File(bibitools.getSpoolDir(), System.getProperty("file.separator") + stderr);
    }

    /**
     * Return the cputime in milliseconds consumed by this job.
     *
     * @return
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    public long getCputime() throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return cputime;
    }

    /**
     * Set the cputime in milliseconds consumed by this job.
     *
     * @param cputime
     */
    public void setCputime(long cputime) {
        this.cputime = cputime;
    }

    /**
     * Returns the diskspace (in bytes) consumed by this job.
     *
     * @return
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    public long getDiskspace() throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return diskspace;
    }


    /**
     * Set the diskspace (in kbytes) consumed by this job.
     *
     * @param diskspace
     */
    public void setDiskspace(long diskspace) {
        this.diskspace = diskspace;
    }

    /**
     * Get the memory (in kbytes) consumed by this job.
     *
     * @return
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    public long getMemory() throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return memory;
    }

    /**
     * Set the memory (in kbytes) consumed by this job.
     * @param memory 
     */
    public void setMemory(long memory) {
        this.memory = memory;
    }
    
    public List<Pair<String, String>> getUploadDownloadData() throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return uploadDownloadData;
    }

    /**
     * Set the interaceType which initiated this BiBiTools Call
     * 
     * @param t 
     */
    public void setInterfaceType(String t){
       interfaceType = t;
    }
    
    /**
     * Returns  the interfaceType
     * 
     * @param t
     * @return
     * @throws DBConnectionException
     * @throws IdNotFoundException 
     */
    public String getInterfaceType(String t) throws DBConnectionException, IdNotFoundException {
        getDBStatus();
        return interfaceType;
    }
 
    public void setCallCMDLine(String t){
        setCallCMDLine(new String [] {t});        
    }
    
    public void setCallCMDLine(String [] t){
        StringBuilder sb = new StringBuilder();
        
        for (String ts : t) {
            sb.append(ts).append("\n");
        }
        callCMDLine = sb.toString();
    }
     
    public String  getCallCMDLine() throws DBConnectionException, IdNotFoundException{
        getDBStatus();
        return callCMDLine;
    }
    
    /*
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }



    /////////////////////////
    // public methods
    ////////////////////////

    /**
     * getting information about this status (e.g. for logging)
     * @return String of information about current status
     * 
     */
    @Override
    public String toString() {
        return "Id:" + id + "\n Toolname:" + bibitools.getProperty("toolname") + "\n Status:" + statuscode + "\n Description:" + description + "\n internal Description:" + internalDescription + "\n DRMAAId:" + drmaaId + "\n Created:" + dbSdf.format(createdDate) + "\n LastMod:" + dbSdf.format(lastMod);
    }

    /////////////////////////
    // private methods
    ////////////////////////
    
    /**
     * Returns true if the last time this function was called was more than REFRESH_WAIT
     * milliseconds ago in oder to determine if data can be updated from DB again.
     * This is used to minimize the Database load.
     * @return true if the last time this function was called was more than REFRESH_WAIT
     * milliseconds ago
     */
    private boolean refresh() {
        long currentTime = System.currentTimeMillis() ;
        boolean refresh = (currentTime - refreshTime > REFRESH_WAIT);
        if(refresh) {
            refreshTime = currentTime;
        }
        return refresh;
    } 
    
    
    /** 
     * getDBStatus() get Status from DB
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     * @exception de.unibi.techfak.bibiserv.exception.IdNotFoundException thrown if the requested id is not in the DB
     */
    private void getDBStatus() throws DBConnectionException, IdNotFoundException {
        
        // only update data from DB when the last update was more tha REFRESH_WAIT milliseconds ago
        // This is done to minimize the Database load.
        if(!refresh())
        {
            return;
        }
        
        Statement stmt = null;
        Connection conn = null;
        
        try {

            conn = BiBiTools.getDataSource().getConnection();
            
            // normal data
            if (conn != null) {
              
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * from status WHERE id='" + id + "'");
                if (!rs.next()) {
                    log.error("id '" + id + "' not found");
                    throw new IdNotFoundException();
                }
                //id -> 1
                //statuscode -> 2
                bibitools.setFunctionname(rs.getString("functionname"));
                statuscode = rs.getInt("statuscode");
                description = rs.getString("description"); //4
                internalDescription = rs.getString("internaldescription"); 
                drmaaId = rs.getString("drmaaid"); 
                createdDate = rs.getTimestamp("created"); 
                lastMod = rs.getTimestamp("lastmod"); 
                stdout = rs.getString("stdout"); 
                stderr = rs.getString("stderr");
                
                memory = rs.getLong("memory");
                cputime = rs.getLong("memory");
                diskspace = rs.getLong("diskspace");
                
                interfaceType = rs.getString("interfacetype");
                callCMDLine = rs.getString("callcmdline");

                

            } else {
                log.error("Connection is null");
                throw new DBConnectionException("Connection is null!");
            }
            
            stmt.close();
     
            uploadDownloadData = new ArrayList();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT history, state from downloaduploadstatus WHERE statusid='" + id + "'  ORDER BY index" );
            while(rs.next()) {
                uploadDownloadData.add(new Pair(rs.getString("history"), rs.getString("state")));
            }
            
            
        } catch (SQLException e) {
            log.error(e.toString(),e);
            throw new DBConnectionException();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("while selecting id (closing) '" + id + "' :" + e.getMessage(),e);
                throw new DBConnectionException(e);
            }
        }

    }

    /** 
     * updateDBStatus() update Status in DB
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    private void updateDBStatus() throws DBConnectionException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            Date curTime = new Date();

            conn = BiBiTools.getDataSource().getConnection();
            if (conn != null) {
                log.debug("got connection to db");
                description = description.replaceAll("'", "\"");  // reformating for SB-Update String
                internalDescription = internalDescription.replaceAll("'", "\"");
                
                stmt = conn.prepareStatement("UPDATE status set statuscode = ?, description = ?, internaldescription = ?, drmaaId = ?, lastmod = ?,stdout = ?, stderr = ?, cputime = ?, memory = ?, interfacetype = ?, callcmdline = ?, diskspace = ?  WHERE id = ?");
                stmt.setInt(1,statuscode);
                stmt.setString(2,description);
                stmt.setString(3,internalDescription);
                stmt.setString(4,drmaaId);
                stmt.setTimestamp(5,new java.sql.Timestamp(curTime.getTime()));
                stmt.setString(6, stdout);
                stmt.setString(7,stderr);
                stmt.setLong(8,cputime);
                stmt.setLong(9,memory);
                
                stmt.setString(10,interfaceType);
                
                
                Clob clob = conn.createClob();
                if (callCMDLine != null) {
                    clob.setString(1, callCMDLine);
                }
                stmt.setClob(11, clob);
                
                stmt.setLong(12,diskspace);
                
                stmt.setString(13,id);
                stmt.execute();
            } else {
                log.error("connection is null");
                throw new DBConnectionException("Connection is null!");
            }

        } catch (SQLException e) {
            log.error("while updateing id '" + id + "' :" + e.getMessage(),e);
            throw new DBConnectionException(e);

        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("while updating id (closing) '" + id + "' :" + e.getMessage(),e);
                throw new DBConnectionException(e);
            }
        }
    }

    /** 
     * insertDBStatus() insert new Status in DB
     * @exception de.unibi.techfak.bibiserv.exception.DBConnectionException thrown if DB for storing status isn't accessible
     */
    private void insertDBStatus() throws DBConnectionException {
        Date curTime = new Date();
        //prepare date
        String myDate = idSdf.format(curTime);
        //prepare random String for unique IDs
        Random rand = new Random();
        StringBuffer myRnd = new StringBuffer();
        for (int n = 0; n < 5; n++) {//length is 5
            myRnd = myRnd.append(ID_CHARS.charAt(rand.nextInt(62)));
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        //create ID from hostname with date and rnd 
        id = bibitools.getProperty("hostname") + "_" + myDate + "_" + myRnd.toString();
        createdDate = new Date();
        lastMod = createdDate;
        //setStatuscode(601); //default after creating
        statuscode = 601;
        description = "Submitted";
        try {

            conn = BiBiTools.getDataSource().getConnection();
            if (conn != null) {
                log.debug("(insert)got connection to db");
                description = description.replaceAll("\\'", "\\\\'");  // reformating for SB-Update String
                internalDescription = internalDescription.replaceAll("\\'", "\\\\'");
                
                stmt = conn.prepareStatement("INSERT INTO status (id,toolname,functionname,statuscode,description, internaldescription, drmaaid, userid, created, lastmod,stdout, stderr, interfaceType, callcmdline) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                
                stmt.setString(1, id);
                stmt.setString(2, bibitools.getToolname());
                stmt.setString(3, bibitools.getFunctionname());
                
                stmt.setInt(4, statuscode);
                stmt.setString(5, description);
                stmt.setString(6,internalDescription);
                
                stmt.setString(7,drmaaId);
                stmt.setString(8,bibitools.getUser().getId());
                
                
                stmt.setTimestamp(9,new java.sql.Timestamp(createdDate.getTime()));
                stmt.setTimestamp(10,new java.sql.Timestamp(lastMod.getTime()));
                
                stmt.setString(11,stdout);
                stmt.setString(12,stderr);
                
                stmt.setString(13,interfaceType);
                
                Clob clob = conn.createClob();
                if (callCMDLine != null) {
                    clob.setString(1,callCMDLine);
                }
                stmt.setClob(14, clob);
                
                stmt.execute();
            } else {
                log.error("connection is null");
                throw new DBConnectionException();
            }

        } catch (SQLException e) {
            log.error("while createing id '" + id + "' :" + e.getMessage(),e);
            throw new DBConnectionException(e);

        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("while creating id (closing) '" + id + "' :" + e.getMessage(),e);
                throw new DBConnectionException(e);
            }
        }
    }
}
