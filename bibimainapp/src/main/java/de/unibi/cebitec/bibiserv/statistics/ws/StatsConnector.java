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
package de.unibi.cebitec.bibiserv.statistics.ws;

import de.unibi.cebitec.bibiserv.statistics.ws.response.StatsMethodEnum;
import de.unibi.cebitec.bibiserv.statistics.ws.response.SingleCategoryResult;
import de.unibi.cebitec.bibiserv.statistics.ws.response.SingleTimerangeResult;
import de.unibi.cebitec.bibiserv.statistics.ws.response.StatsWsReponse;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/**
 * Contains all methods to handle statistical web service requests.
 *
 * @author Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 */
public class StatsConnector {

    public static final long MIN = 1000L * 60;
    public static final long HOUR = 60 * MIN;
    public static final long DAY = HOUR * 24;
    private Connection con;
    private static final Logger LOG = Logger.getLogger("de.unibi.cebitec.bibiserv.statistics.ws.StatsConnector");
    private Map<StatsMethodEnum, Map<String, Long>> toolToTimerangeMap = new HashMap<>();
    private Map<StatsMethodEnum, Map<String, SingleTimerangeResult>> toolToResultMap = new HashMap<>();
    private static final long TIMERANGE = 1;

    public List<String> toolList() {
        List<String> tools = new LinkedList<>();
        Connection con = null;
        
        try {         
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement pstmnt = con.prepareStatement("select distinct toolname from stats_toolclicks");
            ResultSet rset = pstmnt.executeQuery();
            while (rset.next()) {
                tools.add(rset.getString(1));
            }
            rset.close();
            con.close();
        } catch (SQLException | DBConnectionException ex) {
            LOG.fatal("The following exception occured :" + ex.getMessage(),ex);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LOG.fatal("Could not close DB connection: " + ex);
            } 
        }
        return tools;
    }

    /**
     * Creates a StatsWsResponse for given input data.
     *
     * @param methodName Statistical type of type StatsMethodEnum
     * @param timeranges String array with the requested time ranges.
     * @return Web service response ADT of type StatsWsResponse.
     */
    public StatsWsReponse getStats(StatsMethodEnum methodName, String[] timeranges, String toolname) {
        StatsWsReponse swsr = new StatsWsReponse();
        swsr.setMethodName(methodName);
        if (timeranges != null) {
            for (String timerange : timeranges) {
                swsr.addTimerangeResult(getSingleTimerange(methodName, timerange, toolname));
            }
        }

        return swsr;
    }

    /**
     * Calculates different timestamps for given time range.
     *
     * @param timerange Time range of "This hour", "Last hour", "Today",
     * "Yesterday", "This week", "Last week", "This month", "Last month",
     * "This year" or "Complete" without quotes.
     * @return List of timestamps. First item is begin and second one is end.
     */
    private List<String> calcTimeRange(String timerange) {
        String begin;
        String end;
        long now = System.currentTimeMillis();
        if (timerange.startsWith("60M")) {
            int time = Integer.parseInt(timerange.substring(3));
            begin = new Timestamp(System.currentTimeMillis() - (time + 5) * MIN).toString();
            end = new Timestamp(System.currentTimeMillis() - time * MIN).toString();
        } else if (timerange.startsWith("24H")) {
            int time = Integer.parseInt(timerange.substring(3));
            begin = new Timestamp(System.currentTimeMillis() - (time + 2) * HOUR).toString();
            end = new Timestamp(System.currentTimeMillis() - time * HOUR).toString();
        } else if (timerange.equalsIgnoreCase("Today")) {
            begin = new Timestamp(System.currentTimeMillis()).toString().substring(0, 10) + " 00:00:00.000";
            end = new Timestamp(now).toString();
        } else if (timerange.equalsIgnoreCase("Yesterday")) {
            begin = new Timestamp(System.currentTimeMillis() - DAY).toString().substring(0, 10) + " 00:00:00.000";
            end = new Timestamp(System.currentTimeMillis()).toString().substring(0, 10) + " 00:00:00.000";
        } else if (timerange.equalsIgnoreCase("This hour")) {
            begin = new Timestamp(System.currentTimeMillis()).toString().substring(0, 13) + ":00:00.000";
            end = new Timestamp(now).toString();
        } else if (timerange.equalsIgnoreCase("Last hour")) {
            begin = new Timestamp(System.currentTimeMillis() - HOUR).toString().substring(0, 13) + ":00:00.000";
            end = new Timestamp(System.currentTimeMillis()).toString().substring(0, 13) + ":00:00.000";
        } else if (timerange.equalsIgnoreCase("This week")) {
            begin = new Timestamp(System.currentTimeMillis() - DAY * (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1)).toString().substring(0, 10) + " 00:00:00.000";
            end = new Timestamp(System.currentTimeMillis()).toString();
        } else if (timerange.equalsIgnoreCase("Last week")) {
            begin = new Timestamp(System.currentTimeMillis() - DAY * (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 6)).toString().substring(0, 10) + " 00:00:00.000";
            end = new Timestamp(System.currentTimeMillis() - DAY * (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1)).toString().substring(0, 10) + " 00:00:00.000";
        } else if (timerange.equalsIgnoreCase("This month")) {
            String tmp = new Timestamp(System.currentTimeMillis()).toString();
            int year = Integer.parseInt(tmp.substring(0, 4));
            String month = tmp.substring(5, 7);
            String day = "01";
            begin = year + "-" + month + "-" + day + " 00:00:00.000";
            end = new Timestamp(System.currentTimeMillis()).toString();
        } else if (timerange.equalsIgnoreCase("Last month")) {
            String tmp = new Timestamp(System.currentTimeMillis()).toString();
            int year = Integer.parseInt(tmp.substring(0, 4));
            String month = tmp.substring(5, 7);
            String day = "01";
            end = year + "-" + month + "-" + day + " 00:00:00.000";
            if ("01".equals(month)) {
                month = "12";
                year -= 1;
            } else {
                month = String.valueOf(Integer.parseInt(month) - 1);
                if (month.length() == 1) {
                    month = "0" + month;
                }
            }
            begin = year + "-" + month + "-" + day + " 00:00:00.000";
        } else if (timerange.equalsIgnoreCase("This year")) {
            String tmp = new Timestamp(System.currentTimeMillis()).toString();
            int year = Integer.parseInt(tmp.substring(0, 4));
            begin = year + "-01-01 00:00:00.000";
            end = new Timestamp(System.currentTimeMillis()).toString();
        } else {
            begin = new Timestamp(0).toString();
            end = new Timestamp(now).toString();
        }
        List<String> tslist = new LinkedList<String>();
        tslist.add(begin);
        tslist.add(end);
        return tslist;
    }

    /**
     * Provides a SingleTimerangeResult with the requested data.
     * This method caches data and updates them every 5 minutes.
     *
     * @param methodName Statistical type of type StatsMethodEnum
     * @param timeranges Requested time range of type String
     * @return SingleTimerangeResult with the requested data
     */
    private SingleTimerangeResult getSingleTimerange(StatsMethodEnum methodName, String timerange, String toolname) {

        SingleTimerangeResult str = new SingleTimerangeResult(timerange);
        Map<String, Long> timerangeToTime = null;
        Map<String, SingleTimerangeResult> timerangeToResult = null;
        boolean isToolRequest = (methodName == StatsMethodEnum.SINGLETOOL);
        if (toolToTimerangeMap.get(methodName) == null) {
            timerangeToTime = new HashMap<String, Long>();
            timerangeToResult = new HashMap<String, SingleTimerangeResult>();
            toolToTimerangeMap.put(methodName, timerangeToTime);
            toolToResultMap.put(methodName, timerangeToResult);
        } else {
            timerangeToTime = toolToTimerangeMap.get(methodName);
            timerangeToResult = toolToResultMap.get(methodName);
        }
        //Statement for retrieving xml from DB...
        List<String> startStop = calcTimeRange(timerange);
        String statement = "";
        boolean mergeColumns12 = false;
        switch (methodName) {
            case CATEGORYCLICKS:
                statement = "select CATEGORY, COUNT(CATEGORY) from stats_categoryclicks where (date between '" + startStop.get(0) + "' AND '" + startStop.get(1) + "') group by CATEGORY";
                break;
            case TOOLCLICKS:
                statement = "select toolname, COUNT(toolname) from stats_toolclicks where (date between '" + startStop.get(0) + "' AND '" + startStop.get(1) + "') group by toolname";
                break;
            case BROWSER:
                statement = "select browsername, COUNT(browsername) from stats_clientinfo where (date between '" + startStop.get(0) + "' AND '" + startStop.get(1) + "') group by browsername";
                break;
            case OS:
                statement = "select os, COUNT(os) from stats_clientinfo where (date between '" + startStop.get(0) + "' AND '" + startStop.get(1) + "') group by os";
                break;
            case RESOLUTION:
                mergeColumns12 = true;
                statement = "select resolutionx,resolutiony, (count(resolutiony)) from stats_clientinfo where (date between '" + startStop.get(0) + "' AND '" + startStop.get(1) + "') group by resolutionx, resolutiony";
                break;
            case SINGLETOOL:
                statement = "select location, COUNT(location) from stats_toolclicks where toolname = '" + toolname + "' and (date between '" + startStop.get(0) + "' AND '" + startStop.get(1) + "') group by location";
                break;
            case SUBMISSIONS:
                statement = "select toolname, COUNT(toolname) from stats_toolsubmission where (date between '" + startStop.get(0) + "' AND '" + startStop.get(1) + "') group by toolname";
                break;
            case SUBMISSIONS_EXA:
                statement = "select toolname, COUNT(toolname) from stats_toolsubmission where toolname = '" + toolname + "' and example='1' and (date between '" + startStop.get(0) + "' AND '" + startStop.get(1) + "') group by toolname";
                break;
            case SUBMISSIONS_STD:
                statement = "select toolname, COUNT(toolname) from stats_toolsubmission where toolname = '" + toolname + "' and stdparam='1' and (date between '" + startStop.get(0) + "' AND '" + startStop.get(1) + "') group by toolname";
                break;
            case SUBMISSIONS_EXA_STD:
                statement = "select toolname, COUNT(toolname) from stats_toolsubmission where toolname = '" + toolname + "' and example='1' and stdparam='1' and (date between '" + startStop.get(0) + "' AND '" + startStop.get(1) + "') group by toolname";
                break;
            default:
                break;
        }
     
        ResultSet rset = null;
        if (timerangeToTime.get(timerange) == null || isToolRequest || System.currentTimeMillis() - timerangeToTime.get(timerange) > TIMERANGE) {
            Connection con = null;
            try {
                con = BiBiTools.getDataSource().getConnection();
                Statement stmt = con.createStatement();
                rset = stmt.executeQuery(statement);
                timerangeToTime.put(timerange, System.currentTimeMillis());

                while (rset.next()) {
                    SingleCategoryResult scr;
                    if (mergeColumns12) {
                        scr = new SingleCategoryResult(rset.getString(1) + "/" + rset.getString(2), rset.getLong(3));
                    } else {
                        scr = new SingleCategoryResult(rset.getString(1), rset.getLong(2));
                    }
                    str.addSingleCategoryResults(scr);
           
                }
                rset.close();
                stmt.close();
                timerangeToResult.put(timerange, str);
            } catch (SQLException | DBConnectionException ex) {
                LOG.fatal("The following exception occured :" + ex.getMessage(), ex);
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    LOG.fatal("The following SQL - exception occured when trying to closethe connection to database in BiBiServXmlReader...:" + ex);
                }
            }
        } else {
            str = timerangeToResult.get(timerange);
        }
        return str;
    }
}
