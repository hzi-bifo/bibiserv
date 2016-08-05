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
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.utils.SHA1;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.Mail;
import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiTool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * Simple Bean used to collect data from registration form and support for CAPTCHA
 * (CAPTCHA is an acronym for Completely Automated Public Turing test to tell
 * Computers and Humans Apart).
 *
 *
 * @author Jan Krueger - jkrueger
 */
public class RegisterBean implements InitializingBean {

    private String title = "";
    private String name = "";
    private String surname = "";
    private String organisation = "";
    private String email = "";
    private String phone = "";
    private String password = "";
    private String confirmPassword = "";
    private String id = "";
    private static Logger log = Logger.getLogger(RegisterBean.class);
 
    private boolean recaptcha = false;
    /* #########################################
     * #  Implementation of InitializingBean   #
     * #########################################*/
    
    private String sessionId;

    @Override
    public void afterPropertiesSet() throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            ExternalContext extcontext = context.getExternalContext();
            Object obj = extcontext.getSession(false);

            // Check if ReCaptcha can be used
            String prck = extcontext.getInitParameter("primefaces.PRIVATE_CAPTCHA_KEY");
            String puck = extcontext.getInitParameter("primefaces.PUBLIC_CAPTCHA_KEY");
            recaptcha = prck != null && puck != null;

            log.debug("primefaces.PRIVATE_CAPTCHA_KEY has value :" + prck);
            log.debug("primefaces.PUBLIC_CAPTCHA_KEY has value :" + puck);

            if (obj != null) {
                sessionId = ((HttpSession) obj).getId();
                log.info("[afterPropertiesSet] HttpSessionId is " + sessionId);
            } else {
                log.fatal("[afterPropertiesSet] HttpSession is 'null'!");
            }
        } else {
            log.fatal("[afterPropertiesSet] FacesContext is null !");
        }
    }

    /* =======================================================================
     *                      Getter /Setter  
     * ======================================================================= */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;

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

    public boolean isRecaptcha() {
        return recaptcha;
    }

    /* =======================================================================
     *                      Validator functions 
     * ======================================================================= */
    public void checkId(FacesContext facescontext, UIComponent component, Object value) throws ValidatorException {
        String v = (String) value;
        if (v == null || v.isEmpty()) {
            ((UIInput) component).setValid(false);
            FacesMessage msg = new FacesMessage("Id is a mandantory field!");
            facescontext.addMessage(component.getClientId(facescontext), msg);
            return;
        }
        // given id should be unique, so have a look if it exist in database
        String t = get_and_check_Id(v);
        if (t == null) {
            ((UIInput) component).setValid(false);
            FacesMessage msg = new FacesMessage("Fatal error occurred while accessing database! Please contact the server admins.");
            facescontext.addMessage(component.getClientId(facescontext), msg);
            setId("");
            return;
        }
        if (!v.equals(t)) {
            setId(t);
            ((UIInput) component).setValid(false);
            FacesMessage msg = new FacesMessage("Id '" + v + "' already exists.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            facescontext.addMessage(component.getClientId(facescontext), msg);
        } else {
            ((UIInput) component).setValid(true);
        }
    }

    public void checkEmail(FacesContext facescontext, UIComponent component, Object value) throws ValidatorException {

        String tmp = value.toString();
        log.info("call checkEmail with value " + tmp);
        if (!tmp.matches(".+?\\@.+?\\.\\w+")) {
            log.info("Invalid email " + tmp);
            ((UIInput) component).setValid(false);
            FacesMessage msg = new FacesMessage("Invalid email address!");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            facescontext.addMessage(component.getClientId(facescontext), msg);
        } else {
            ((UIInput) component).setValid(true);
        }
    }

    String confirmPasswordId;
    

    /* ActionListener */
    public void registerAction(ActionEvent ae) {
        if (!getPassword().equals(getConfirmPassword())) {
            
            setConfirmPassword("");
            FacesMessage msg = new FacesMessage("Password and confirmpassword must be equal!");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("confirmpassword", msg);
           
        } else {
            // insert data into database
            insert_into_Db();
            
            // Maybe it is good idea to sent an email ...
            
            Properties mailprop = new Properties();
            mailprop.setProperty("mail.host", "smtp-relay.CeBiTec.Uni-Bielefeld.DE");
            mailprop.setProperty("mail.sendmail", "/usr/sbin/sendmail -t");      
            mailprop.setProperty("mail.bcc", "bibi-help@cebitec.uni-bielefeld.de");
            mailprop.setProperty("mail.from", "bibi-help@cebitec.uni-bielefeld.de");
            try {
                Mail.sendmail(email, "BiBiServ Registration", 
                    "Dear "+title+" "+name+" "+surname+"\n\n"+
                            "thanks for your registration at https://bibiserv.cebitec.uni-bielefeld.de.\n"+
                            "It typically takes up to one working day to check your registration data and \n"
                            + "activate your account! \n\n"
                            + " Your BiBiServ support team!",mailprop);
            } catch (BiBiToolsException e){
                FacesMessage msg = new FacesMessage("SendMail exception while sending registration confirmation !");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage("global", msg);
                return;
            }
            FacesMessage msg = new FacesMessage("Thank you for registration!",
                "It typically takes up to one working day to check your registration data and activate your account!");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage("global", msg);
            clearAction(ae);
        }
    }
    
    public void clearAction(ActionEvent ae) {
        title = "";
        name = "";
        surname = "";
        organisation = "";
        email = "";
        phone = "";
        password = "";
        confirmPassword = "";
        id = "";     
    }

    private String get_and_check_Id(String suggestion) {
        String retvalue = suggestion;
        Connection conn = null;      
        try {            
            conn = BiBiTools.getDataSource().getConnection();
            Statement stmt = conn.createStatement();
            int counter = 1;
            int found = 0;
            do {
                log.info("search if user with id '" + retvalue + "' exist ...");
                ResultSet rst = stmt.executeQuery("SELECT count(id) FROM USERS where id='" + retvalue + "'");
                if (rst.next()) {
                    found = rst.getInt(1);
                    if (found == 1) {
                        retvalue = retvalue + counter;
                        ++counter;
                    }
                }
                rst.close();
            } while (found == 1);
           
            stmt.close();
        } catch (Exception e) {
            log.fatal("Execption occurred while call get_and_check_id", e);
            retvalue = null;
        } finally {
        
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.fatal("SQLException while closing connection", e);
            }
        }
        return retvalue;
    }

    private void insert_into_Db() {
        Connection conn = null;
   

        try {
            conn = BiBiTools.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO USERS (id,password,limitclass,enabled,name,surname,mail,organisation,title) values "
                    + "(?,?,?,?,?,?,?,?,?)");
            stmt.setString(1, id);
            stmt.setString(2, SHA1.SHAsum(password));
            stmt.setString(3, "registered");
            stmt.setInt(4, 0);
            stmt.setString(5, name);
            stmt.setString(6, surname);
            stmt.setString(7, email);
            stmt.setString(8, organisation);
            stmt.setString(9, title);
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.fatal("Execption occurred while call insert_into_Db", e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.fatal("SQLException while closing connection", e);
            }
        }
    }
}
