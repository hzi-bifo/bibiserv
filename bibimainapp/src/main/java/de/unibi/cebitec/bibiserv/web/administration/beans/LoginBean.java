/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011-2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2011-2012 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.administration.beans;


import de.unibi.cebitec.bibiserv.utils.SHA1;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;

/**
 * Bean Backend for JSF2 Login page 
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class LoginBean {
    
    private final static Logger log = Logger.getLogger(LoginBean.class);
    
    private String message = null;
    
    Map<String, List<String>> m = new HashMap<>();

    public String getJ_password() {
        try {
            return m.get("j_password").get(0);
        }catch (NullPointerException e){
            return null;
        }
    }

    public void setJ_password(String j_password) {
        try {
            //m.put("j_password",Arrays.asList(new String[] {j_password}));   
           m.put("j_password",Arrays.asList(new String[] {SHA1.SHAsum(j_password)}));
        } catch (Exception e){
            log.fatal("Exception while encryption password!",e);
        }
        
    }

    public String getJ_username() {
        try {
            return m.get("j_username").get(0);
        } catch (NullPointerException e){
            return null;
        }
    }

    public void setJ_username(String j_username) {
        m.put("j_username",Arrays.asList(new String[] {j_username}));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMessageAvailable() {
        return (message == null || message.isEmpty())?false:true;
    }
    
    public void submit(ActionEvent ae) throws IOException{
        // build dispatch request url
        String dispatchurl = FacesContext.getCurrentInstance().getExternalContext().encodeRedirectURL("/j_spring_security_check",m);
        //dispatch request 
        FacesContext.getCurrentInstance().getExternalContext().dispatch(dispatchurl);
        // complete response
        FacesContext.getCurrentInstance().responseComplete();
        
    }
    
    
}
