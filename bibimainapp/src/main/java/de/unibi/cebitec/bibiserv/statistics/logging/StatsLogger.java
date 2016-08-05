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
package de.unibi.cebitec.bibiserv.statistics.logging;

import cz.mallat.uasparser.UserAgentInfo;
import de.unibi.cebitec.bibiserv.statistics.logging.database.DBConnector;
import de.unibi.cebitec.bibiserv.utils.SHA1;
import de.unibi.cebitec.bibiserv.web.beans.session.UserBean;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * {@inheritDoc}
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *         Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 */
public class StatsLogger implements StatsLoggerI {

    private static final Logger logger = Logger.getLogger(StatsLogger.class.getName());
    private DBConnector dbcon;
    private UserBean user;
    private UASparser uasparser;
    private GeoIP geoip;
    

    private String sessionid;
    private String ip;
    private String useragent;
   
    

    
    /**
     * Logs Client Info.
     * 
     * @param request 
     */
    @Override
    public void logClientInfo(HttpServletRequest request){
        if (sessionid == null) {
          
            sessionid =  request.getSession(true).getId();
            useragent = request.getHeader("User-Agent");
            if (useragent == null) {
                useragent = "not set";
            }
            ip = request.getRemoteAddr();
            
        
    
            // start a new thread in background and return immediatly
            Thread r = new Thread(new Runnable() {
                @Override
                public void run() {
                    
                    String browser = "unknown";
                    String browserversion = "unknown";
                    String device = "unknown";
                    String os = "unknown";
                    
                    try {
                        // parse useragentstring 
                        UserAgentInfo info = uasparser.parse(useragent);
                        // it seems that sometimes a null object is returned ...
                        if (info != null) {
                            browser = info.getUaFamily();
                            
                            browserversion = info.getBrowserVersionInfo();
                            if (browserversion == null) {
                                browserversion = "unknown";
                            } 
                            device = info.getDeviceType();
                            os = info.getOsName();
                        }
                    } catch (IOException e){
                        logger.fatal(e.getMessage(),e);
                    }
                            
                    // reverse country lookup country code
                    String country = "??";
                    
                    try {
                        country = geoip.getCountry(InetAddress.getByName(ip));
                    } catch (UnknownHostException e){
                        
                        logger.fatal("IP is :"+ip);
                        // do nothing 
                    }
                   
                    // hash ip address 
                    String haship = ip;
                    try { 
                        haship = SHA1.SHAsum(ip);
                    } catch (NoSuchAlgorithmException |UnsupportedEncodingException e) {
                        logger.fatal(e.getMessage(),e);
                    }
                    // log everything to database
                     dbcon.logClientInfo(sessionid, haship, country,browser, browserversion, os, device , useragent);
                }
            });
            r.start();
        }
    }
    
    /**
     * Log Tool Click Info
     * 
     * @param request - HttpServletRequest 
     * @param id - tool/app id
     */
    @Override
    public void logToolClick(HttpServletRequest request, String id) {
        dbcon.logToolClick(sessionid, id, user.getId());
    }
    
    
    /** Log Download Info 
     * 
     * @param id tool/app id
     * @param name - filename
     * @param url - download url
     */
    @Override
    public void logDownload(String id, String name, String url){
        dbcon.logDownload(sessionid, id,name,url);
    }
    
    /**
     * Log Tool usage 
     * 
     * @param sessionid
     * @param bibiservid 
     */
    @Override
    public void logToolUsage(String sessionid, String bibiservid){
        dbcon.logToolUsage(sessionid,bibiservid);
        
    }

    // +++++++++++++++++ getter/setter +++++++++++++++++++++
    
   


    public void setDbcon(DBConnector dbcon) {
        this.dbcon = dbcon;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public void setUasparser(UASparser uasparser) {
        this.uasparser = uasparser;
    }

    public void setGeoip(GeoIP geoip) {
        this.geoip = geoip;
    }
    
    

    
    
    
    
    // +++++++++++++++++ deprecated fct ++++++++++++++++++++
    
    

     /**
     * {@inheritDoc}
     */
    @Deprecated
    public void logRuntime(String toolname, String jobID, long seconds, int statusCode, String description) {
        if (logger.isDebugEnabled()) {
            logger.debug("logRunTime is deprecated and not supported anymore. ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public void logRuntime(String toolname, String sessionID, String jobID, long seconds, int statusCode, String description) {
     if (logger.isDebugEnabled()) {
            logger.debug("logRunTime is deprecated and not supported anymore. ");
     }
     logToolUsage(sessionid, jobID);
    }


     /**
     * {@inheritDoc}
     */
    @Deprecated
    public void logSubmit(String toolname, boolean isDefault, boolean isExample) {
        if (logger.isDebugEnabled()) {
            logger.debug("logSubmit is deprecated and not supported anymore. ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public void logSubmit(String toolname, String sessionID, boolean isDefault, boolean isExample) {
         if (logger.isDebugEnabled()) {
            logger.debug("logSubmit is deprecated and not supported anymore. ");
        }
       
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public void logTool(String appName, String appView, String sessionId, String ip) {
        if (logger.isDebugEnabled()) {
            logger.debug("logTool is deprecated and not supported anymore. ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public void logCategory(String category, String sessionId, String ip) {
        if (logger.isDebugEnabled()) {
            logger.debug("logCategory is deprecated and not supported anymore. ");
        }
    }

    /**
     * Called by logCategory and forwards a call to save the client stats info
     * in database.
     *
     * @param sessionId Session id.
     * @param ip IP of the user.
     */
    @Deprecated
    private void logClientStats(String sessionId, String ip) {
       if (logger.isDebugEnabled()) {
            logger.debug("logClientStats is deprecated and not supported anymore. ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public void logToolError(String appName, String appView, String ip) {
       if (logger.isDebugEnabled()) {
           logger.debug("logToolError is deprecated and not supported anymore. ");
       }
    }

    @Override
    @Deprecated
    public String getViewName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public String getViewType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public void setViewName(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public void setViewType(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
}
