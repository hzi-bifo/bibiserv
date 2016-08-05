/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2013 BiBiServ Team]"
 *
 * Contributor(s): Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.server.manager.Core;
import de.unibi.cebitec.bibiserv.utils.Cookie;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiTool;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * PrevRes is the backing bean for the PreviousResult (=prevres.jsf) page. Make
 * sure that this bean is in request scope - parameter and beans updated only
 * after initialize ...
 *
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class PrevRes {

    private static final Logger log = Logger.getLogger(PrevRes.class.getName());
    
    

    private String id;
    private String selid;
 
    private long lastupdated;
    private Map<String, String> prevJobs = new HashMap();
    
    
    /**
     * ----------- DI Userbean -----------
     */
    
    private UserBean user;

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getSelid() {
        return selid;
    }

    public void setSelid(String selid) {
        this.selid = selid;
    }
    
    
    
    


    public void setId(String id) {
        
            this.id = id;
       
    }

    /**
     * Getter method used by both inputs (SelectOneRadio and InputText). Check if an 
     * parameter named 'id' is given to request and submit it directly ...
     * 
     * @return 
     */
    public String getId() {
        // check if an id is given as parameter and then redirect immediately to corresponding result page ...
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (params.containsKey("id")) {
            id = params.get("id");
            submit();
        }
        return id;
    }
    
    

    public boolean isPreviousJobsAvailable() {
        update();
        return !prevJobs.isEmpty();
    }

    public Collection<String> getPreviousJobs() {
        update();
        return prevJobs.keySet();
    }

    public String getJobDescription(String id) {
        update();
        return prevJobs.get(id);
    }

    public void submit() {
        Connection con = null;
        try {
            // check if submitted id is valid
            if ((id != null && !id.isEmpty()) || (selid != null && !selid.isEmpty())) {

                String local_id;
                
               if (id != null && !id.isEmpty()) {
                   local_id = id;
               } else {
                   local_id = selid;
               }
                
                con = BiBiTools.getDataSource().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * from status WHERE id='" + local_id + "' and userid='"+user.getUser().getId()+"'");

                // id is unknown
                if (!rs.next()) {
                    facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Unknown id" , "Id '" + local_id + "' is unknown or not available for '"+
                            user.getUser().getName()+" "+user.getUser().getSurname()+
                            "'["+user.getUser().getId()+"]"));
                } else {
                    String toolname = rs.getString("toolname");
                    String functionname = rs.getString("functionname");
                    Integer status = rs.getInt("statuscode");
                    String description = rs.getString("description");
                    if (status >= 700) {
                        facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR, "No result available", "No result for id '" + local_id + "' found (" + status + " - " + description + ")"));

                    } else {
                        // set bibiserv id to function                
                        Object result_bean = Core.getInstance().getApplicationContext().getBean(functionname + "_result");
                        Method m = result_bean.getClass().getDeclaredMethod("setBibiservid", java.lang.String.class);
                        m.invoke(result_bean, local_id);
                        //redirect
                        FacesContext.getCurrentInstance().getExternalContext().redirect("/" + toolname + "/?viewType=submission&subType=" + functionname + "_result");
                       
                    }
                }
                rs.close();
                stmt.close();
                
            }

        } catch (SQLException| DBConnectionException e) {
            log.fatal(e);
            facesMsg(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Database", e.getMessage()));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            log.fatal(e);
            facesMsg(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Set id to function", e.getMessage()));
        } catch (IOException e) {
            log.fatal(e);
            facesMsg(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Redirect", e.getMessage()));
        } finally {
            try {
                if (con != null) {
                    con.close();
                } 
            } catch (SQLException ex) {
                log.fatal ("Can't close db connection!",ex);
             
            }
        }

    }

    private void facesMsg(FacesMessage msg) {
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    

    private void update() {

        long ctm = System.currentTimeMillis();
        
        if (ctm - lastupdated > 1000) {
            
            Connection con = null;
            
            try {
                // cleanup map
                prevJobs.clear();
                
               

                // read cookie and get all informations from database
                javax.servlet.http.Cookie cookie = Cookie.getCookie(FacesContext.getCurrentInstance().getExternalContext(), "bibiservids");
                if (cookie != null) {
                    Map<String, Long> map = Cookie.parseBiBiServIdsCookie(cookie);
                    // limit the output 


                    // iterate over all id's

                        
                    con = BiBiTools.getDataSource().getConnection();
                    Statement stmt = con.createStatement();

                    for (String local_id : map.keySet()) {

                        ResultSet rs = stmt.executeQuery("SELECT * from status WHERE id='" + local_id + "' and userid='"+user.getUser().getId()+"'");
                        if (rs.next()) {
                            String toolname = rs.getString("toolname");
                            String functionname = rs.getString("functionname");
                            Integer status = rs.getInt("statuscode");
                            Date created = rs.getDate("created");
                            if (status < 700) {
                                prevJobs.put(local_id, toolname + "[" + functionname + "] finished at " + created.toString());
                                selid = local_id;
                            }
                        }
                        rs.close();
                    }
                    stmt.close();
                    


                }
            } catch (DBConnectionException | SQLException e) {
                log.fatal(e);
                facesMsg(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Database", e.getMessage()));
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e){
                    log.fatal(e.getMessage(), e);
                }
            }
            
            
            lastupdated = ctm;
        }
    }
}
