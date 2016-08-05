/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2011 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.administration.beans;

import de.unibi.cebitec.bibiserv.server.manager.Core;
import de.unibi.cebitec.bibiserv.web.beans.session.UserBean;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Implementation of Simple AuthenticationSuccessHandler
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class LoginSuccessHandler implements AuthenticationSuccessHandler  {

    private static Logger log = Logger.getLogger(LoginSuccessHandler.class.getName());
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest hsr, HttpServletResponse hsr1, Authentication a) throws IOException, ServletException {
        
       
        String redirect = "/admin";
        
        ApplicationContext ctx = Core.getInstance().getApplicationContext();
        LoginBean loginbean = (LoginBean)ctx.getBean("login");
        UserBean userbean = (UserBean)ctx.getBean("user");
      
        // refresh userbean
        userbean.refresh(a);
       
        // reset/clear message
        loginbean.setMessage("");
        
        // and redirect to MyBiBiServ area
        hsr1.sendRedirect(redirect);
    }

}
