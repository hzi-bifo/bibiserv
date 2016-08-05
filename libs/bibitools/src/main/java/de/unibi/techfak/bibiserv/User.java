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
 * "Portions Copyrighted 2010 BiBiServ Curator Team"
 *
 * Contributor(s):
 *
 */
package de.unibi.techfak.bibiserv;

import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.exception.IdExistsException;
import de.unibi.techfak.bibiserv.exception.IdInvalidException;
import de.unibi.techfak.bibiserv.exception.IdNotFoundException;
import de.unibi.techfak.bibiserv.exception.IdNotUniqueException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * PoJo representing an user, its resource limits and currently used resources.
 * Offers a methods to check if user exceeds it
 *
 *
 * @author jkrueger
 */
public class User {

    public static final Logger log = Logger.getLogger(User.class);
    // some constants
    public final static String ANONYMOUS = "anonymous";
    private static int hours = 24 * 7; // == 7 days
    // user id
    private String id = ANONYMOUS;
    // password
    private String password = "change me";
    // authorized
    //private boolean authorized = false;
    // name
    private String name = "";
    //surname
    private String surname = "";
    // title
    private String title = "";
    // organisation
    private String organisation = "";
    //email
    private String email = "";
    // phone
    private String phone = "";
    // concessions
    private SortedSet<String> authorities = new TreeSet<>();
    // responsibilties
    private Set<String> responsibilities = new HashSet<>();
    
    // limitclass
    private String limitclass = "unregistered";
    // resource limits
    private Resource resource_limits;
    // timestamp , used for test environment
    private Timestamp current_timespamp;
    
    private boolean passwordreset = false;

    /**
     * Create a new ANONYMOUS user.
     *
     * Also try to retrieve limits for ANONYMOUS user from database.
     *
     */
    public User() throws DBConnectionException {
        resource_limits = retrieve_limits();
    }

    /**
     * Constructor setting user id with given id. Update all user data from
     * database, except the id equals 'ANONYMOUS'.
     *
     * Throws a IdNotFoundException in the case that the doesn't exists in
     * database. Throws a DBConnectionException in the case something went wrong
     * during quering the database.
     *
     * @param id
     */
    public User(String id) throws IdNotFoundException, DBConnectionException {
        this.id = id;
        if (id.equals((ANONYMOUS))) {
            resource_limits = retrieve_limits();
        } else {
            dbselect();
        }
    }

    /**
     * Return the id of current user.
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Check if given id is unique and matches the regular expression
     * [a-z,A-Z][a-z,A-Z,0-9]{7,99}.
     *
     * @param id to be checked
     * @return true if id is uniqe and matches regexp
     * @throws a IdInvalidException if id doesn't matches regexp
     */
    public static boolean checkId(String id) throws IdInvalidException {
        if (!id.matches("[a-zA-Z][a-zA-Z0-9]{1,99}")) {
            throw new IdInvalidException();
        }

        boolean rv = false;
        try {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            Resource resource = null;

            try {
                conn = BiBiTools.getDataSource().getConnection();
                if (conn != null) {
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("SELECT count(*) from users where id='" + id + "'");
                    rs.next();
                    rv = rs.getInt(1) == 0;

                }
            } catch (SQLException e) {
                log.fatal("SQLException occurred : " + e.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    log.error("while selecting id (closing) " + id + " :" + e.toString());

                }
            }
        } catch (DBConnectionException e) {
            log.fatal("DbConnectionException occurred : " + e.getMessage());
        }
        return rv;
    }

    @Override
    public boolean equals(Object obj) {
        User user = (User) obj;
        if (authorities.size() != user.authorities.size()) {
            return false;
        }

        String[] c1 = new String[authorities.size()];
        c1 = authorities.toArray(c1);
        String[] c2 = new String[authorities.size()];
        c2 = authorities.toArray(c2);
        for (int i = 0; i < c1.length; ++i) {
            if (!c1[i].equals(c2[i])) {
                return false;
            }
        }

        return id.equals(user.id)
                && password.equals(user.password)
                && name.equals(user.name)
                && surname.equals(user.surname)
                && title.equals(user.title)
                && organisation.equals(user.organisation)
                && email.equals(user.email)
                && phone.equals(user.phone)
                && resource_limits.equals(user.resource_limits);
    }

    /**
     * Returned a cloned Resource object describing the resource limits
     *
     * @return
     */
    public final Resource getLimits() {
        return (Resource) resource_limits.clone();


    }

    /**
     * Return currently used resources.
     *
     * @return
     */
    public Resource getResources() {
        try {
            return retrieve_resources();


        } catch (Exception e) {
            log.fatal("Exception " + e.getClass().getSimpleName() + " occurred :" + e.getMessage());


            throw new RuntimeException(e);


        }
    }

    /**
     * Set timestamp used for sql select statememtns with time constraints. This
     * function should only be used in a test environment. Set getTimestamp()
     *
     * @param timestamp
     */
    public void setTimestamp(Timestamp timestamp) {
        current_timespamp = timestamp;


    }

    /**
     * Return a timestamp currently used for sql select statments with time
     * constraints. If timestamp wasn't changed manually before (see
     * setTimestamp()), returns the current system timestamp.
     *
     * @return Return a timestamp currently used for sql select statementss
     */
    public Timestamp getTimestamp() {
        if (current_timespamp != null) {
            return current_timespamp;


        }
        return new Timestamp(System.currentTimeMillis());


    }

    /**
     * Returns true if resources reserved by current user exceeds its limit,
     * false other
     *
     * @return
     */
    public boolean isExceedsLimit() {
        try {
            Resource res = retrieve_resources();


            if (res.getRuns() >= resource_limits.getRuns()
                    && res.getCputime() >= resource_limits.getCputime()
                    && res.getDiskspace() >= resource_limits.getDiskspace()
                    && res.getMemory() >= resource_limits.getMemory()) {
                return true;


            }
            return false;


        } catch (Exception e) {
            log.fatal("Exception " + e.getClass().getSimpleName() + " occurred :" + e.getMessage());


            throw new RuntimeException(e);


        }
    }

    /**
     * Static method set the considered period to the past from used timestamp
     * for consumed cputime and memory usage. Default value are 7 days (168
     * hours).
     *
     * @param hours
     */
    public static void setConsideredPeriod(int hours) {
        User.hours = hours;


    }

    /**
     * Returns the considered period to the pasth from used timestamp for
     * consumed cputime and memory usage.
     *
     * @return
     */
    public static int getConsideredPeriod() {
        return User.hours;


    }

    public String getEmail() {
        return email;


    }

    public void setEmail(String email) {
        this.email = email;


    }

    public static int getHours() {
        return hours;


    }

    public static void setHours(int hours) {
        User.hours = hours;


    }

    public String getName() {
        return name;


    }

    public void setName(String name) {
        this.name = name;


    }

    public String getOrganisation() {
        return organisation;


    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addAuthorities(String authority) {
        authorities.add(authority);
    }

    public void removeAuthorities(String authority) {
        authorities.remove(authority);
    }

    public SortedSet<String> getAuthorities() {
        return authorities;
    }
    
    public Set<String> getResponsibilities() {
        return responsibilities;
    }

    public String getLimitclass() {
        return limitclass;
    }

    public boolean isPasswordreset() {
        return passwordreset;
    }

    public void setPasswordreset(boolean passwordreset) {
        this.passwordreset = passwordreset;
    }
    
    public void setLimitclass(String limitclass) {
        if (limitclass.equals(this.limitclass)) {
            this.limitclass = limitclass;
            try {
                retrieve_limits();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
     * ----------------- private methods -----------------
     */
    private Resource retrieve_limits() throws DBConnectionException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Resource resource = null;

        try {
            conn = BiBiTools.getDataSource().getConnection();
            if (conn != null) {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT r.id, r.runs,r.cputime,r.diskspace,r.memory from resources r where r.id='" + limitclass + "'");
                // if query returns no entry, log an error message and continue with default resource limits!
                if (!rs.next()) {
                    log.error("User with id '" + id + "' hasn't a limit class entry  in database using default resource limitss !");
                    resource_limits = new Resource(limitclass);
                } else {
                    resource = new Resource(rs.getString("ID"), rs.getInt("RUNS"), rs.getInt("CPUTIME"), rs.getInt("DISKSPACE"), rs.getInt("MEMORY"));
                }
            } else {
                log.error("Connection is null");
                throw new DBConnectionException("Connection is null!");
            }
        } catch (SQLException e) {
            log.fatal("A SQLException occurred : " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.fatal("A SQLException occurred : " + e.getMessage(), e);
                throw new DBConnectionException("A SQLException occurred : " + e.getMessage());
            }
        }
        return resource;
    }

    private Resource retrieve_resources() throws DBConnectionException, SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        Resource resource = null;

        try {
            conn = BiBiTools.getDataSource().getConnection();
            if (conn != null) {
                stmt = conn.createStatement();
                // runs  ::  select count(*) from status_ID where statuscode >= 600 and statuscode <700;
                rs = stmt.executeQuery("SELECT count(*) from status_" + id + " where statuscode >= 600 and statuscode < 700");
                int runs = rs.getInt(1);
                rs.close();
                rs = stmt.executeQuery("SELECT sum(cputime) from status_" + id + " where lastmod > '" + new Timestamp(getTimestamp().getTime() - (1000 * 60 * 60) * hours).toString() + "'");
                // cputime ::
                int cputime = rs.getInt(1);
                rs.close();
                // diskspace :: select sum(diskspace) from status_ID;
                rs = stmt.executeQuery("SELECT sum(diskspace) from status_" + id);
                int diskspace = rs.getInt(1);
                rs.close();
                // memory ::
                rs = stmt.executeQuery("SELECT sum(memory) from status_" + id + " where lastmod > '" + new Timestamp(getTimestamp().getTime() - (1000 * 60 * 60) * hours).toString() + "'");
                int memory = rs.getInt(1);
                resource = new Resource(id, runs, cputime, diskspace, memory);
            } else {
                log.error("Connection is null");
                throw new DBConnectionException();
            }
        } catch (SQLException e) {
            log.fatal("SQLException occurred : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("while selecting id (closing) " + id + " :" + e.toString());
                throw new DBConnectionException();
            }
        }
        return resource;
    }

    /**
     * Update current user object from database using user id. Be carefull - any
     * data beside the user object will be overwritten
     *
     */
    private void dbselect() throws IdNotFoundException, DBConnectionException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = BiBiTools.getDataSource().getConnection();
            if (conn != null) {
                stmt = conn.createStatement();
                // +++++++++++++ User data +++++++++++++
                rs = stmt.executeQuery("SELECT password, name, surname, title, mail, organisation, phone, limitclass, passwordreset from users where id='" + id + "'");
                // if resultset has no entries, the given id doesn;t exists in database
                if (!rs.next()) {
                    rs.close();
                    stmt.close();
                    conn.close();
                    throw new IdNotFoundException("User with id '" + id + "' not found in database!");
                }
                password = rs.getString("password");
                name = rs.getString("name");
                surname = rs.getString("surname");
                title = rs.getString("title");
                email = rs.getString("mail");
                organisation = rs.getString("organisation");
                phone = rs.getString("phone");
                limitclass = rs.getString("limitclass");
                passwordreset = (rs.getInt("passwordreset") > 0);
                rs.close();
                // +++++++++++++ Resource Limits +++++++++++++++
                rs = stmt.executeQuery("SELECT r.id, r.runs,r.cputime,r.diskspace,r.memory from resources r, users u where u.id='" + id + "' and r.id=u.limitclass");
                // if query return no entry continue with default resource limits
                if (!rs.next()) {
                    log.error("User with id '" + id + "' hasn't a limit class entry  in database using default resource limitss !");
                    resource_limits = new Resource(limitclass);
                } else {
                    resource_limits = new Resource(rs.getString("ID"), rs.getInt("RUNS"), rs.getInt("CPUTIME"), rs.getInt("DISKSPACE"), rs.getInt("MEMORY"));
                }
                rs.close();
                // +++++++++++++ Authorities +++++++++++++
                rs = stmt.executeQuery("SELECT AUTHORITY from AUTHORITIES where id='" + id + "'");
                authorities = new TreeSet();
                while (rs.next()) {
                    authorities.add(rs.getString("AUTHORITY"));
                }
                rs.close();
                // +++++++++++++ Responsibilities +++++++++++
                rs = stmt.executeQuery("SELECT itemid FROM responsibilities where id='"+id+"'");
                responsibilities = new HashSet();
                while(rs.next()) {
                    responsibilities.add(rs.getString("itemid"));
                }
                rs.close();
            }


        } catch (SQLException e) {
            log.fatal("A SQLException occurred : " + e.getMessage(), e);
        } finally {
            try {

                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("A SQLException occurred :" + e.getMessage(), e);

            }
        }


    }
}
