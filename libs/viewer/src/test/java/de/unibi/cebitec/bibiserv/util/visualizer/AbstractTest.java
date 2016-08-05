/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.visualizer;

import de.unibi.cebitec.bibiserv.util.visualizer.impl.TestRAEDA;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.log;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.commons.io.FileUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractTest {
    
    private final static Logger LOG = Logger.getLogger(AbstractTest.class);
    private static DataSource datasource = null;
    
    /*
     * The following methods are copied from test-methods of BiBiTools.
     * Please do not change them. They simply create a test-database for
     * test-purposes while using the Spool-methods of BiBiTools.
     */
    public static DataSource getDataSource() throws Exception {
        if (datasource == null) {
            datasource = derbydb();
        }
        return datasource;
    }
    
    private static DataSource derbydb() throws Exception {

        EmbeddedDataSource ds = new EmbeddedDataSource();

        String db = "test/testdb_"+System.currentTimeMillis();
        
        // check if database exists
        File db_dir = new File(db);
        if (db_dir.exists()) {
            try {
                FileUtils.deleteDirectory(db_dir);
            } catch (IOException e) {
                LOG.error(e);
                assertTrue(e.getMessage(),false);
            }
        }
        ds.setDatabaseName(db);
        ds.setCreateDatabase("create");

        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();

        // read SQL Statement from file
        BufferedReader r = new BufferedReader(new InputStreamReader(TestRAEDA.class.getResourceAsStream("/status.sql")));
        String line;
        StringBuilder sql = new StringBuilder();
        while ((line = r.readLine()) != null) {
            // skip commend lines
            if (!line.startsWith("--")) {
                sql.append(line);
                sql.append('\n');
            }
        }
        r.close();

        // execute sqlcmd's
        for (String sqlcmd : sql.toString().split(";")) {
            sqlcmd = sqlcmd.trim(); // ignore trailing/ending whitespaces
            sqlcmd = sqlcmd.replaceAll("\n\n", "\n"); // remove double newline
            if (sqlcmd.length() > 1) { // if string contains more than one char, execute sql cmd
                LOG.debug(sqlcmd + "\n");
                stmt.execute(sqlcmd);
            }
        }

        // close stmt
        stmt.close();

        return ds;
    }
    
}
