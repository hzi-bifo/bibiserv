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

import de.unibi.cebitec.bibiserv.statistics.charts.DatabaseEntry;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class gets information from the database and puts it in the requested
 * container class.
 *
 * DataSource/Connection handling adjusted by Jan Krueger (June 2015)
 * 
 * @author Jan Schmolke <jschmolke@techfak.uni-bielefeld.de>
 */
public class DBGetter {


    /**
     * Gets information from the STATUS-Table and puts it in a list for further
     * use in the statistics.
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @return List of entries.
     * @throws
     * de.unibi.cebitec.bibiserv.statistics.logging.database.MySQLException
     */
    public ArrayList<DatabaseEntry> getToolusageList(Date minDate, Date maxDate) throws MySQLException {
        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT * FROM status,stats_clientinfo,stats_usage WHERE status.id = stats_usage.bibiservid AND stats_usage.sessionid = stats_clientinfo.sessionid and created>=? and created<=?");
            pstmnt.setTimestamp(1, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(2, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("TOOLNAME"), res.getDate("CREATED"), res.getInt("STATUSCODE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"), res.getInt("CPUTIME"), res.getString("INTERFACETYPE"), res.getString("CALLCMDLINE"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage(),ex);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage(),ex);
            }
        }
        return entries;
    }

    /**
     * Gets information from the STATUS-Table and puts it in a list for further
     * use in the statistics. Selects given tool. If toolname is ALL it calls
     * the standard getToolusageList().
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @param toolname Name of tool to select.
     * @return List of entries.
     * @throws
     * de.unibi.cebitec.bibiserv.statistics.logging.database.MySQLException
     */
    public ArrayList<DatabaseEntry> getToolusageList(Date minDate, Date maxDate, String toolname) throws MySQLException {
        if (toolname.equals("ALL")) {
            return getToolusageList(minDate, maxDate);
        }

        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT * FROM status,stats_clientinfo,stats_usage WHERE status.id = stats_usage.bibiservid AND stats_usage.sessionid = stats_clientinfo.sessionid AND toolname=? and created>=? and created<=?");
            pstmnt.setString(1, toolname);
            pstmnt.setTimestamp(2, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(3, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("TOOLNAME"), res.getDate("CREATED"), res.getInt("STATUSCODE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"), res.getInt("CPUTIME"), res.getString("INTERFACETYPE"), res.getString("CALLCMDLINE"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException | DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets information from the STATUS-Table and puts it in a list for further
     * use in the statistics orders by country not date.
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @return List of entries.
     * @throws
     * de.unibi.cebitec.bibiserv.statistics.logging.database.MySQLException
     */
    public ArrayList<DatabaseEntry> getToolusageListOrderCountry(Date minDate, Date maxDate) throws MySQLException {
        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement  pstmnt = con.prepareStatement("SELECT * FROM status,stats_clientinfo,stats_usage WHERE status.id = stats_usage.bibiservid AND stats_usage.sessionid = stats_clientinfo.sessionid and created>=? and created<=? order by country");
            pstmnt.setTimestamp(1, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(2, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("TOOLNAME"), res.getDate("CREATED"), res.getInt("STATUSCODE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"), res.getInt("CPUTIME"), res.getString("INTERFACETYPE"), res.getString("CALLCMDLINE"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets information from the STATUS-Table and puts it in a list for further
     * use in the statistics orders by country not date. Selects given tool. If
     * toolname is ALL it calls the standard getToolusageListOrderCountry().
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @param toolname Tool to select
     * @return List of entries.
     * @throws
     * de.unibi.cebitec.bibiserv.statistics.logging.database.MySQLException
     */
    public ArrayList<DatabaseEntry> getToolusageListOrderCountry(Date minDate, Date maxDate, String toolname) throws MySQLException {
        if (toolname.equals("ALL")) {
            return getToolusageListOrderCountry(minDate, maxDate);
        }

        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT * FROM status,stats_clientinfo,stats_usage WHERE status.id = stats_usage.bibiservid AND stats_usage.sessionid = stats_clientinfo.sessionid and toolname=? and created>=? and created<=? order by country");
            pstmnt.setString(1, toolname);
            pstmnt.setTimestamp(2, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(3, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("TOOLNAME"), res.getDate("CREATED"), res.getInt("STATUSCODE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"), res.getInt("CPUTIME"), res.getString("INTERFACETYPE"), res.getString("CALLCMDLINE"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets information about toolclicks for further use in the statistics. You
     * cannot use select * because result set does not know the table names
     * therefore name every column in the select.
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @return List of entries
     * @throws
     * de.unibi.cebitec.bibiserv.statistics.logging.database.MySQLException
     */
    public ArrayList<DatabaseEntry> getToolclickList(Date minDate, Date maxDate) throws MySQLException {
        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement  pstmnt = con.prepareStatement("SELECT id, stats_clicks.timestamp as date, os, country, browsername, browserversion FROM stats_clientinfo,stats_clicks WHERE stats_clientinfo.sessionid=stats_clicks.sessionid and stats_clicks.timestamp>=? and stats_clicks.timestamp<=?");
            pstmnt.setTimestamp(1, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(2, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("ID"), res.getDate("DATE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets information about toolclicks for further use in the statistics. You
     * cannot use select * because result set does not know the table names
     * therefore name every column in the select. Selects given tool. If
     * toolname is ALL it calls the standard getToolclickList().
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @param toolname Tool to select.
     * @return List of entries
     * @throws
     * de.unibi.cebitec.bibiserv.statistics.logging.database.MySQLException
     */
    public ArrayList<DatabaseEntry> getToolclickList(Date minDate, Date maxDate, String toolname) throws MySQLException {
        if (toolname.equals("ALL")) {
            return getToolclickList(minDate, maxDate);
        }

        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement  pstmnt = con.prepareStatement("SELECT id, stats_clicks.timestamp as date, os, country, browsername, browserversion FROM stats_clientinfo,stats_clicks WHERE stats_clientinfo.sessionid=stats_clicks.sessionid AND id=? and stats_clicks.timestamp>=? and stats_clicks.timestamp<=?");
            pstmnt.setString(1, toolname);
            pstmnt.setTimestamp(2, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(3, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("ID"), res.getDate("DATE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets information about toolclicks for further use in the statistics. You
     * cannot use select * because result set does not know the table names
     * therefore name every column in the select. Orders by country not date.
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @return List of entries
     * @throws
     * de.unibi.cebitec.bibiserv.statistics.logging.database.MySQLException
     */
    public ArrayList<DatabaseEntry> getToolclickListOrderCountry(Date minDate, Date maxDate) throws MySQLException {
        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT id, stats_clicks.timestamp as date, os, country, browsername, browserversion FROM stats_clientinfo,stats_clicks WHERE stats_clientinfo.sessionid=stats_clicks.sessionid and stats_clicks.timestamp>=? and stats_clicks.timestamp<=? order by country");
            pstmnt.setTimestamp(1, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(2, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("ID"), res.getDate("DATE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets information about toolclicks for further use in the statistics. You
     * cannot use select * because result set does not know the table names
     * therefore name every column in the select. Orders by country not date.
     * Selects given tool. If toolname is ALL it calls the standard
     * getToolclickListOrderCountry().
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @param toolname Tool to select.
     * @return List of entries
     * @throws
     * de.unibi.cebitec.bibiserv.statistics.logging.database.MySQLException
     */
    public ArrayList<DatabaseEntry> getToolclickListOrderCountry(Date minDate, Date maxDate, String toolname) throws MySQLException {
        if (toolname.equals("ALL")) {
            return getToolclickListOrderCountry(minDate, maxDate);
        }

        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT id, stats_clicks.timestamp as date, os, country, browsername, browserversion FROM stats_clientinfo,stats_clicks WHERE stats_clientinfo.sessionid=stats_clicks.sessionid AND id=? and stats_clicks.timestamp>=? and stats_clicks.timestamp<=? order by country");
            pstmnt.setString(1, toolname);
            pstmnt.setTimestamp(2, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(3, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("ID"), res.getDate("DATE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets entries from downloads You cannot use select * because result set
     * does not know the table names therefore name every column in the select.
     * for further use in the statistics.
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @return List of entries.
     * @throws MySQLException Thrown exception.
     */
    public ArrayList<DatabaseEntry> getDownloadList(Date minDate, Date maxDate) throws MySQLException {
        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT id, stats_download.timestamp as date, os, country, browsername, browserversion  FROM stats_clientinfo,stats_download WHERE stats_clientinfo.sessionid=stats_download.sessionid and stats_download.timestamp>=? and stats_download.timestamp<=?");
            pstmnt.setTimestamp(1, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(2, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("ID"), res.getDate("DATE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets entries from downloads You cannot use select * because result set
     * does not know the table names therefore name every column in the select.
     * for further use in the statistics. Selects given tool. If toolname is ALL
     * it calls the standard getDownloadList().
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @param toolname Tool to select.
     * @return List of entries.
     * @throws MySQLException Thrown exception.
     */
    public ArrayList<DatabaseEntry> getDownloadList(Date minDate, Date maxDate, String toolname) throws MySQLException {
        if (toolname.equals("ALL")) {
            return getDownloadList(minDate, maxDate);
        }

        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT id, stats_download.timestamp as date, os, country, browsername, browserversion  FROM stats_clientinfo,stats_download WHERE stats_clientinfo.sessionid=stats_download.sessionid AND id=? and stats_download.timestamp>=? and stats_download.timestamp<=?");
            pstmnt.setString(1, toolname);
            pstmnt.setTimestamp(2, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(3, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("ID"), res.getDate("DATE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try { 
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets entries from downloads You cannot use select * because result set
     * does not know the table names therefore name every column in the select.
     * for further use in the statistics. Orders by country not date.
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @return List of entries.
     * @throws MySQLException Thrown exception.
     */
    public ArrayList<DatabaseEntry> getDownloadListOrderCountry(Date minDate, Date maxDate) throws MySQLException {
        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT id, stats_download.timestamp as date, os, country, browsername, browserversion  FROM stats_clientinfo,stats_download WHERE stats_clientinfo.sessionid=stats_download.sessionid and stats_download.timestamp>=? and stats_download.timestamp<=? order by country");
            pstmnt.setTimestamp(1, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(2, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("ID"), res.getDate("DATE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets entries from downloads You cannot use select * because result set
     * does not know the table names therefore name every column in the select.
     * for further use in the statistics. Orders by country not date. Selects
     * given tool. If toolname is ALL it calls the standard
     * getDownloadListOrderCountry().
     *
     * @param minDate The date to start with.
     * @param maxDate The date to end with.
     * @param toolname Tool to select.
     * @return List of entries.
     * @throws MySQLException Thrown exception.
     */
    public ArrayList<DatabaseEntry> getDownloadListOrderCountry(Date minDate, Date maxDate, String toolname) throws MySQLException {
        if (toolname.equals("ALL")) {
            return getDownloadListOrderCountry(minDate, maxDate);
        }

        ArrayList<DatabaseEntry> entries = new ArrayList<>();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT id, stats_download.timestamp as date, os, country, browsername, browserversion  FROM stats_clientinfo,stats_download WHERE stats_clientinfo.sessionid=stats_download.sessionid AND id=? and stats_download.timestamp>=? and stats_download.timestamp<=? order by country");
            pstmnt.setString(1, toolname);
            pstmnt.setTimestamp(2, new Timestamp(minDate.getTime()));
            pstmnt.setTimestamp(3, new Timestamp(maxDate.getTime()));
            ResultSet res = pstmnt.executeQuery();
            while (res.next()) {
                DatabaseEntry entry = new DatabaseEntry(res.getString("ID"), res.getDate("DATE"), res.getString("OS"), res.getString("COUNTRY"), res.getString("BROWSERNAME"), res.getString("BROWSERVERSION"));
                entries.add(entry);
            }
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return entries;
    }

    /**
     * Gets the absolute minimum date from table status.
     *
     * @return The minimum date.
     * @throws MySQLException
     */
    public Date getAbsMinDateUsage() throws MySQLException {
        Date date = new Date();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("select min(created) as date from status");

            ResultSet res = pstmnt.executeQuery();
            res.next();
            date = res.getDate("date");
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return date;

    }

    /**
     * Gets the absolute minimum date from table status. Selects given tool. If
     * toolname is ALL it calls the standard getAbsMinDateUsage().
     *
     * @param toolname Tool to select.
     * @return The minimum date.
     * @throws MySQLException
     */
    public Date getAbsMinDateUsage(String toolname) throws MySQLException {
        if (toolname.equals("ALL")) {
            return getAbsMinDateUsage();
        }

        Date date = new Date();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("select min(created) as date from status WHERE toolname=?");
            pstmnt.setString(1, toolname);

            ResultSet res = pstmnt.executeQuery();
            res.next();
            date = res.getDate("date");
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return date;

    }

    /**
     * Gets the absolute minimum date from table stats_clicks.
     *
     * @return The minimum date.
     * @throws MySQLException
     */
    public Date getAbsMinDateClicks() throws MySQLException {
        Date date = new Date();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT MIN(timestamp) as date FROM stats_clicks");

            ResultSet res = pstmnt.executeQuery();
            res.next();
            date = res.getDate("date");
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return date;

    }

    /**
     * Gets the absolute minimum date from table stats_clicks. Selects given
     * tool. If toolname is ALL it calls the standard getAbsMinDateClicks().
     *
     * @param toolname Tool to select.
     * @return The minimum date.
     * @throws MySQLException
     */
    public Date getAbsMinDateClicks(String toolname) throws MySQLException {
        if (toolname.equals("ALL")) {
            return getAbsMinDateClicks();
        }

        Date date = new Date();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT MIN(timestamp) as date FROM stats_clicks WHERE id=?");
            pstmnt.setString(1, toolname);

            ResultSet res = pstmnt.executeQuery();
            res.next();
            date = res.getDate("date");
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return date;

    }

    /**
     * Gets the absolute minimum date from table stats_download.
     *
     * @return The minimum date.
     * @throws MySQLException
     */
    public Date getAbsMinDateDownload() throws MySQLException {
        Date date = new Date();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT MIN(timestamp) as date FROM stats_download");

            ResultSet res = pstmnt.executeQuery();
            res.next();
            date = res.getDate("date");
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return date;

    }

    /**
     * Gets the absolute minimum date from table stats_download. Selects given
     * tool. If toolname is ALL it calls the standard getAbsMinDateDownloads().
     *
     * @param toolname Tool to select.
     * @return The minimum date.
     * @throws MySQLException
     */
    public Date getAbsMinDateDownload(String toolname) throws MySQLException {
        if (toolname.equals("ALL")) {
            return getAbsMinDateDownload();
        }

        Date date = new Date();
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("SELECT MIN(timestamp) as date FROM stats_download WHERE id=?");
            pstmnt.setString(1, toolname);

            ResultSet res = pstmnt.executeQuery();
            res.next();
            date = res.getDate("date");
            res.close();
            pstmnt.close();
        } catch (SQLException | DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {           
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }
        return date;
    }

    /**
     * Gets the name of all tools for the admin.
     *
     * @return List of toolnames.
     */
    public ArrayList<String> getToollistForAdmin() throws MySQLException {
        ArrayList<String> tools = new ArrayList<>();

        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("select toolname from status union select id from stats_clicks union select id from stats_download");
            
            ResultSet res = pstmnt.executeQuery();
            while(res.next()){
                tools.add(res.getString(1));
            }
            
            res.close();
            pstmnt.close();
        } catch (SQLException |DBConnectionException ex) {
            throw new MySQLException(ex.getMessage());
        } finally {
            try {
                if (con != null){
                    con.close();
                }
            } catch (SQLException ex) {
                throw new MySQLException(ex.getMessage());
            }
        }

        return tools;
    }
}
