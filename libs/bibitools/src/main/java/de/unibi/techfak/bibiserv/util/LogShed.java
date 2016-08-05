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

package de.unibi.techfak.bibiserv.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * The class LogShed is micro log system. in the case of the bibitools the LogShed
 * class is used to control a process without throwing an exception (e.g. parameter
 * parsing.
 * 
 * @author Jan Krueger jkrueger(at)techfak.uni-bielefeld.de
 */
public class LogShed {

    public enum LogLevel {

        info, warn, error, fatal
    };

    private class Log {

        private Calendar calendar;
        private LogLevel loglevel;
        private String message;

        public Log(LogLevel loglevel, String message) {
            this.loglevel = loglevel;
            this.message = message;
            calendar = Calendar.getInstance();
        }

        public LogLevel getLogLevel() {
            return loglevel;
        }

        public String getMessage() {
            return message;
        }

        public Calendar getCalendar() {
            return calendar;
        }

        /* @Override
        public String toString() {
        return "[" + loglevel.toString() + "] " + message;
        }*/
    }
    private List<Log> logs = new ArrayList<Log>();
    private LogLevel loglevel = LogLevel.info;
    private boolean enabletimestamp = false;

    /**
     * Logs a message at level info.
     * 
     * @param message to be logged.
     */
    public void info(String message) {
        logs.add(new Log(LogLevel.info, message));
    }

    /**
     * Logs a message at level warn.
     * 
     * @param message to be logged.
     */
    public void warn(String message) {
        logs.add(new Log(LogLevel.warn, message));
    }

    /** 
     * Logs a message at level error.
     * 
     * @param message to be logged.
     */
    public void error(String message) {
        logs.add(new Log(LogLevel.error, message));
    }

    /** 
     * Logs a message at level fatal.
     * 
     * @param message to be logged.
     */
    public void fatal(String message) {
        logs.add(new Log(LogLevel.fatal, message));
    }

    /**
     * Return a List (of Strings) containing all Log Messages  up to set
     * log level.
     * @see setLogLevel;
     * @see enableTimeStamp();
     * 
     * @return Return a list of messages.
     */
    public List<String> getLogList() {
        List<String> list = new ArrayList<String>();
        for (Log log : logs) {


            String datestring = "";
            if (enabletimestamp) {
                datestring = " (" + log.getCalendar().getTime().toString() + ")";
            }

            if (loglevel.compareTo(log.getLogLevel()) <= 0) {
                list.add("[" + log.getLogLevel().toString() + "]" + datestring + " - " + log.getMessage());
            }
        }
        return list;

    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (String s : getLogList()) {
            sb.append(s + System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    /**
     * Set the loglevel. A log greater equals the set log level
     * will be returned by getLogList/toString().
     * @param loglevel
     */
    public void setLogLevel(LogLevel loglevel) {
        this.loglevel = loglevel;
    }

    /**
     * Enable/Disable the log timestamp 
     * 
     * @param enabletimestamp
     */
    public void enableTimeStamp(boolean enabletimestamp) {
        this.enabletimestamp = enabletimestamp;
    }
}
