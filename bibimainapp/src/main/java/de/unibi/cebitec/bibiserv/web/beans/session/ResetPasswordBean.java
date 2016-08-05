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
 * "Portions Copyrighted  2010-2015 BiBiServ Curator Team"
 * 
 * Contributor(s): Jan Krueger
 * 
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.utils.SHA1;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.Mail;
import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * Bean associated with the forgotpassword and resetpassword pages
 *
 *
 * @author Jan Krueger <jkrueger(at)cebitec.uni-bielefeld.de>
 */
public class ResetPasswordBean implements InitializingBean {

    private static final Logger log = Logger.getLogger(ResetPasswordBean.class);

    private boolean recaptcha = false;
    private boolean mailsend = false;
    private static final long validity = 1000 * 60 * 60 * 24; // one day
    private String accesskey;
    private String id;
    private String password;
    private String confirmpassword;
    private boolean passwordchanged = false;

    private static final String mailtemplate
            = "We received a request to reset the password associated with this e-mail address. \n"
            + "If you made this request, please follow the instructions below.\n"
            + "\n"
            + "Click the link below to reset your password using our secure server:\n"
            + "\n"
            + "LINK \n"
            + "\n"
            + "\n"
            + "If you did not request to have your password reset you can safely ignore this email.\n"
            + "\n"
            + "Support team";

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRecaptcha() {
        return recaptcha;
    }

    public boolean isMailsend() {
        return mailsend;
    }

    public String getAccesskey() {
        // check for parameter accesskey;
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String tmp = req.getParameter("accesskey");
        if ((tmp != null && accesskey == null) || (tmp != null && accesskey != null && !tmp.equals(accesskey))) {
            accesskey = tmp;
            id = get_Id();
            // new accesskey -> reset values
            mailsend = false;
            name="";
            passwordchanged = false;
            password = "";
            confirmpassword = "";

        }
        return accesskey;
    }

    public String getId() {
        return id;
    }

    public void setPassword(String v) {
        password = v;
    }

    public String getPassword() {
        return password;
    }

    public void setConfirmPassword(String v) {
        confirmpassword = v;
    }

    public String getConfirmPassword() {
        return confirmpassword;
    }

    public boolean isPasswordchanged() {
        return passwordchanged;
    }

    /* ---- Actions ---- */
    public void continueAction(ActionEvent ae) {

        String mail = getMailforId(name);
        // check database for given id
        if (mail == null) {
            message(FacesMessage.SEVERITY_ERROR, "There was a problem with your request!", "We were not able to identify you with the given information (unkown id).");
            return;
        }
        // create key (and a database entry)
        String localkey = generateAccesskeyforRequest(name, mail);
        // create link
        ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();
        String LINK = "https://" + ex.getRequestServerName() + ":" + ex.getRequestServerPort() + "/resetpassword.jsf?accesskey=" + localkey;
        // check 
        
        String mail_host = BiBiTools.getProperties().getProperty("mail.host");
        if (mail_host == null) {
            message(FacesMessage.SEVERITY_FATAL,"Property mail.host not set!","Please configure property 'mail.host' (and also 'mail.sendmail' if neccessary) in BiBiServ properties file!");
            return;
        }
        String mail_sendmail = BiBiTools.getProperties().getProperty("mail.sendmail","/usr/sbin/sendmail -t");
        
        
        
        // and send mail
        Properties mailprop = new Properties();
        mailprop.setProperty("mail.host", mail_host); // "smtp-relay.CeBiTec.Uni-Bielefeld.DE");
        mailprop.setProperty("mail.sendmail", mail_sendmail);
        mailprop.setProperty("mail.from", "do.not.reply@" + ex.getRequestServerName());
        try {
            Mail.sendmail(mail, ex.getRequestServerName() + ":" + ex.getRequestServerPort() + " - Password Assistance", mailtemplate.replaceFirst("LINK", LINK), mailprop);
        } catch (BiBiToolsException e) {
            message(FacesMessage.SEVERITY_ERROR, "SendMail exception while sending registration confirmation !", e.getMessage());
            return;
        }
        mailsend = true;
    }

    public void changePWDAction() {
        if (confirmpassword.equals(password)) {
            update_Db();
            if (passwordchanged) {              
                delete_Accesskey();
            }
        } else {
            message(FacesMessage.SEVERITY_ERROR, "Password and confirmed password are not equal!", null);
        }

    }

    /* ---- Init ---- */
    @Override
    public void afterPropertiesSet() throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            ExternalContext extcontext = context.getExternalContext();
            // Check if ReCaptcha can be used
            String prck = extcontext.getInitParameter("primefaces.PRIVATE_CAPTCHA_KEY");
            String puck = extcontext.getInitParameter("primefaces.PUBLIC_CAPTCHA_KEY");
            recaptcha = prck != null && puck != null;
        }
    }

    private String getMailforId(String id) {
        Connection con = null;
        String mail = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT mail FROM users WHERE id='" + id + "'");
            if (rs.next()) {
                mail = rs.getString("mail");
            }
            stmt.close();
        } catch (DBConnectionException | SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.error("Can't close Connection!");
            }
        }
        return mail;
    }

    private String generateAccesskeyforRequest(String id, String mail) {
        try {
            
            String localkey = SHA1.SHAsum(id + (System.currentTimeMillis()+validity) + mail);

            Connection con = null;
            try {
                con = BiBiTools.getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement("INSERT INTO resetpassword VALUES (?,?,?)");
                stmt.setString(1, localkey);
                stmt.setString(2, id);
                stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()+validity));
                stmt.execute();
                stmt.close();
            } catch (DBConnectionException | SQLException e) {
                log.error(e.getMessage(), e);
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception e) {
                    log.error("Can't close Connection!", e);
                }
            }
            return localkey;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            log.fatal(ex.getMessage(), ex);
        }
        return "___don't_work___";
    }

    private void update_Db() {
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE Users SET password = ? where id= ?");
            stmt.setString(1, SHA1.SHAsum(password));
            stmt.setString(2, id);
            stmt.execute();
            stmt.close();
            passwordchanged = true;
        } catch (DBConnectionException | SQLException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.error("Can't close Connection!", e);
            }
        }
    }

    private String get_Id() {
        Connection con = null;
        String localid = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            // remove all expired entries from resetpassword
            PreparedStatement stmt = con.prepareStatement("DELETE from resetpassword where expired < ?");
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.execute();
            stmt.close();
            // get id from resetpassword       
            stmt = con.prepareStatement("SELECT id  FROM resetpassword WHERE accesskey = ?");
            stmt.setString(1, accesskey);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                localid = rs.getString("id");
            }
            stmt.close();
            // and remove 
        } catch (DBConnectionException | SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.error("Can't close Connection!", e);
            }
        }
        return localid;
    }

    private void delete_Accesskey() {
        Connection con = null;
        try {
            con = BiBiTools.getDataSource().getConnection();
            // remove all expired entries from resetpassword
            PreparedStatement stmt = con.prepareStatement("DELETE from resetpassword where accesskey =  ?");
            stmt.setString(1,accesskey);
            stmt.execute();
            stmt.close();

        } catch (DBConnectionException | SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.error("Can't close Connection!", e);
            }
        }
    }

    private void message(FacesMessage.Severity severity, String content, String detail) {
        FacesMessage msg = new FacesMessage(severity, content, detail);
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext.getCurrentInstance().addMessage("global", msg);
    }
}
