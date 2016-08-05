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
 * "Portions Copyrighted 2015 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.debug;

import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.util.Pair;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import org.apache.log4j.Logger;

/**
 * Implementation of java.sql.DataSource for debug purpose. Uses an existing
 * DataSource for functionality, but logs all getConnection calls.
 *
 * 
 *
 * @author Jan Krueger <jkrueger@cebitec.uni-bielefeld.de>
 */
public class DDataSource implements DataSource {

    private final static Logger log = Logger.getLogger(DDataSource.class.getName());

    private final Map<Object, Pair<Long, String>> activeConnections;

    private final DataSource ds;
    
    private final long time = Long.parseLong(BiBiTools.getProperties().getProperty("DDataSource.Time", "5000"));

    public DDataSource(DataSource ds) {
        this.ds = ds;
        activeConnections = Collections.synchronizedMap(new HashMap<Object, Pair<Long, String>>());   
        log.info("Use DDataSource logging all connections older than "+time+" ms.");
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection con = ds.getConnection();
        activeConnections.put(con, new Pair(System.currentTimeMillis(), sun.reflect.Reflection.getCallerClass(2).getName()));
        checkConnection();
        logActiveConnection(time);
        return con;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection con = ds.getConnection(username, password);    
        activeConnections.put(con, new Pair(System.currentTimeMillis(), sun.reflect.Reflection.getCallerClass(2).getName()));
        checkConnection();
        logActiveConnection(time);
        return con;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return ds.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        ds.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        ds.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return ds.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return ds.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return ds.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return ds.isWrapperFor(iface);
    }

    /**
     * Remove closed connections from activeconnections.
     */
    private void checkConnection() {

        Set<Object> removableSet = new HashSet<>();
        for (Object o : activeConnections.keySet()) {
            Connection c = (Connection) o;
            try {
                if (c == null || c.isClosed()) {
                    removableSet.add(o);
                }
            } catch (SQLException e) {
                log.fatal("Check for connection closed failed!");
            }
        }
        // remove closed connections from active connection set
        try {
            for (Object o : removableSet) {
                activeConnections.remove(o);
            }
        } catch (ConcurrentModificationException e) {
            log.fatal("Exception occurred while refreshing activeConnection Map");
        }
    }

    private void logActiveConnection(long time) {

        long currenttime = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        int c = 0;
        // print all connection to a logger which are older than time msec
        for (Object o : activeConnections.keySet()) {
            Pair<Long, String> p = activeConnections.get(o);
            if (p.getKey() <= (currenttime - time)) {          
                Date d  = new Date(p.getKey());
                sb.append(d.toString()).append(" :: ").append(p.getValue()).append("\n");
                c++;
            }
        }
        if (c > 0) {   
            log.debug(c +" active connection (older than "+time+" ms)\n"+sb.toString());
        }

    }

}
