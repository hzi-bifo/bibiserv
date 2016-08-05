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
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.server.manager;

import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;

/**
 * REST Interface for manager application.
 *
 * Supports deploy/undeploy of BiBiServ Archives via REST.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
@Path("/manager")
public class Manager {

    private static final Logger log = Logger.getLogger("de.unibi.cebitec.bibiserv.server.manager");
    private final Core core = Core.getInstance();
    private DataSource datasource;

    public Manager() throws DBConnectionException {
        initializeDataSource();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String sayHello(String s) {
        return "Hello " + s + "\n";

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "alive";
    }

    @PUT
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public void deploy(byte[] input, @Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        StringBuilder message = new StringBuilder();
        if (checkCredentials(request, message)) {
            try {
                core.deploy(input);
            } catch (ManagerException e) {
                log.error("ManagerException occurred! " + e.getMessage());
                response.sendError(400, e.getMessage());
            }
        } else {
            log.error("Access Denied!" + message.toString());
            response.sendError(401, message.toString());
        }
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)

    public void undeploy(String name, @Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        StringBuilder message = new StringBuilder();
        if (checkCredentials(request, message)) {
            try {
                core.undeploy(name, true);
            } catch (ManagerException e) {
                log.error("ManagerException occurred! " + e.getMessage());
                response.sendError(400, e.getMessage());
            }
        } else {
            log.error("Access Denied!" + message.toString());
            response.sendError(401, message.toString());
        }
    }

    /**
     * private helper function
     *
     * @param request
     * @param message
     * @return
     */
    private boolean checkCredentials(HttpServletRequest request, StringBuilder message) {
        String authorization = request.getHeader("Authorization");

        // test for authenticate header
        if (authorization == null) {
            message.append("HTTP basic authentication required!");
            return false;
        }
        // test for authentication type
        String[] b = authorization.split(" ", 2);
        if (!b[0].equalsIgnoreCase("basic")) {
            message.append("Unsupported authentication type '").append(b[0]).append("! HTTP basic authentication required!");
            return false;
        }

        // test for existing credentials
        int t = b[1].indexOf(":");
        if (b.length != 2) {
            message.append("Credentials missing! HTTP basic authentication required!");
            return false;
        }

        // test for valid credentials
        String[] cred = b[1].split(":", 2);

        try {
            if (cred.length == 2 && cred[1].equals(select_auth(cred[0]))) {
                message.append("authorized!");
                return true;
            }

        } catch (SQLException e) {
            log.fatal("SQL Exception occurred while authorization!", e);
            message.append("failed!");

        }
        return false;
    }

    /**
     * private helper function - used within the constructor to initialize the
     * datasource.
     *
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     */
    private void initializeDataSource() throws DBConnectionException {
        try {
            javax.naming.Context dbctx = new InitialContext();
            if (dbctx == null) {
                throw new DBConnectionException();
            } // @todo: Hardcoded DataSource !!!
            datasource = (DataSource) dbctx.lookup("jdbc/bibiserv2");
        } catch (NamingException ex) {
            log.fatal("An NamingException occurred : " + ex.getMessage());
            throw new DBConnectionException();
        }
        log.info("DataSource initalized");
    }

    /**
     * private helper function - used within the public handle function
     *
     * @param role
     * @return password of role
     * @throws SQLException
     */
    private String select_auth(String role) throws SQLException {
        String pwd = null;
        Connection conn = null;
        try {
            conn = datasource.getConnection();

            PreparedStatement stmt = conn.prepareStatement("select password from manager_auth where id= ?");
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pwd = rs.getString("password");
            }
            rs.close();
            stmt.close();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        return pwd;
    }
}
