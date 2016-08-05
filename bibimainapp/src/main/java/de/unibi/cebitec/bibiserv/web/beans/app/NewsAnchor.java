/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2012 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.beans.app;

import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * This class will deliver news items for the main system.
 *
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de (1st release)
 * Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class NewsAnchor implements InitializingBean {

    private List<String> content = new ArrayList<>();
    private Connection con;
    private final static Logger log = Logger.getLogger(NewsAnchor.class);

    /**
     * Returns true in the case any new available
     *
     * @return Returns true in the case any new available
     */
    public boolean isAvailable() {
        return !content.isEmpty();
    }

    /**
     * Returns random content from list (Content could be contain simple html
     * tags like <em/> <strong/>
     * <p/>
     * and <br/>)
     *
     *
     * @return Returns random content from list (could be contain simple html
     * tags like <em/> <strong/>
     * <p/>
     * and <br/>
     */
    public String getContent() {
        if (content.isEmpty()) {
            return "";
        }

        int nr = new Double(Math.random() * content.size()).intValue();

        return content.get(nr);
    }

    /*
     Returns current year as string
     */
    public int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        update();
    }

    /**
     * Update news from database
     *
     */
    public void update() {

        try {
            List<String> temporary_content = new ArrayList<>();

            con = BiBiTools.getDataSource().getConnection();

            //Statement for reading News items from the database
            PreparedStatement pstmnt = con.prepareStatement("SELECT id,content from NEWS WHERE expired > CURRENT_TIMESTAMP");
            ResultSet dbresult = pstmnt.executeQuery();

            //parsing strings to xml documents in private method
            while (dbresult.next()) {
                Clob c = dbresult.getClob("content");
                temporary_content.add(c.getSubString(1, (int) c.length())); // ugly conversion, fails if clob contains more than maxint chars
            }
            synchronized (this) {
                content = temporary_content;
            }
        } catch (SQLException | DBConnectionException ex) {
            log.fatal(ex.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    log.fatal(ex.getMessage());
                }
            }
        }

    }
}
