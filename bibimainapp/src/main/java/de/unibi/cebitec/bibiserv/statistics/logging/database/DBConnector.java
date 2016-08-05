/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2015 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2015 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.statistics.logging.database;

import de.unibi.techfak.bibiserv.BiBiTools;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * DBConnector provides methods for storing statistical data. 
 * 
 * Comment:
 * I tried to share *One* connection between multiple request to use less 
 * resources but this ends in instable behavior. See 
 * http://docs.oracle.com/javadb/10.5.1.1/devguide/cdevconcepts23499.html
 * for an explanation.
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */


public class DBConnector  {

    private final static Logger LOG = Logger.getLogger(DBConnector.class.getName());
    private final static String COULD_NOT_CLOSE_STMT = "Could not close statement.";
    private final static String COULD_NOT_CLOSE_CON = "Could not close connection.";

    /**
     * Log client info to database (stats_clientinfo table)
     * 
     * @param sessionid - current session id
     * @param ip - ip address (should be hashed for privacy reason)
     * @param country - country two letter code (determined from ip address)
     * @param browser - browser extract from user agent string
     * @param browserversion - browser version extract from user agent string
     * @param os - os extract from user agent string
     * @param device - device
     * @param ua  - user agent string
     */
    public void logClientInfo(String sessionid, String ip, String country, String browser, String browserversion, String os, String device, String ua){
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con. prepareStatement("INSERT INTO stats_clientinfo (sessionid, ip, country, browsername, browserversion, os, device, ua) values (?,?,?,?,?,?,?,?)");
            pstmnt.setString(1,sessionid);
            pstmnt.setString(2,ip);
            pstmnt.setString(3,country);
            pstmnt.setString(4,browser);
            pstmnt.setString(5,browserversion);
            pstmnt.setString(6,os);
            pstmnt.setString(7,device);
            if (ua.length() > 1000 ) { // UA grows over time (like we saw in the past) so limit it to 1000 chars.
                pstmnt.setString(8,ua.substring(0, 999));
            } else {
                pstmnt.setString(8,ua);
            }
            
            pstmnt.executeUpdate();
            pstmnt.close();
         } catch (Exception ex) {
            LOG.fatal("An  Exception occured while updating ClientInfo...", ex);        
        } finally {
           
            // close connection
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LOG.fatal(COULD_NOT_CLOSE_CON,ex);
            }
        }
    }
    
    /**
     * Log tool click info to database (stats_toolclick table)
     * 
     * @param sessionid
     * @param id
     * @param user
     */
    public void logToolClick(String sessionid, String id, String user){
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con. prepareStatement("INSERT INTO stats_clicks (sessionid, id,userid) values (?,?,?)");
            pstmnt.setString(1,sessionid);
            pstmnt.setString(2,id);
            pstmnt.setString(3,user);
            pstmnt.executeUpdate();
            pstmnt.close();
         } catch (Exception ex) {
            LOG.fatal("An Exception occured while updating ToolClick ...", ex);
        } finally {
            // close connection
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LOG.fatal(COULD_NOT_CLOSE_CON,ex);
            }
        }
    }
    
    
    /**
     * Log download info to database (stats_download table)
     * 
     * @param sessionid 
     * @param id - tool/app id
     * @param name - filename
     * @param url - url 
     * 
     */
    public void logDownload(String sessionid, String id, String name, String url) {
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("INSERT INTO stats_download (sessionid,id,name,url) values (?,?,?,?)");
            pstmnt.setString(1,sessionid);
            pstmnt.setString(2,id);
            pstmnt.setString(3,name);
            pstmnt.setString(4,url);
            pstmnt.executeUpdate();
            pstmnt.close();
        } catch (Exception ex){
            LOG.fatal("An Exception occured while updating Download ...",ex);
        } finally {
            // close connection
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LOG.fatal(COULD_NOT_CLOSE_CON,ex);
            }
        }
    }
    
    /**
     * Log Tool Usage  into database, link sessionid to bibiservid 
     * 
     * @param sessionid
     * @param bibiservid 
     */
    
    public void logToolUsage(String sessionid, String bibiservid){
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("INSERT INTO stats_usage (sessionid, bibiservid) values (?,?)");
            pstmnt.setString(1,sessionid);
            pstmnt.setString(2,bibiservid);          
            pstmnt.executeUpdate();
            pstmnt.close();
        } catch (Exception ex){
            LOG.fatal("An Exception occured while updating ToolUsage table ...",ex);
        } finally {
           
            // close connection
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LOG.fatal(COULD_NOT_CLOSE_CON,ex);
            }
        }
               
    }
}
