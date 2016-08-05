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
package de.unibi.cebitec.bibiserv.web.controller;

import de.unibi.cebitec.bibiserv.web.administration.beans.Authority;
import de.unibi.cebitec.bibiserv.web.beans.session.UserBean;
import java.io.IOException;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import de.unibi.cebitec.bibiserv.web.xmlreader.BiBiServXMLReader;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Filtering to only allow rest of password.
 * 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public final class ResetPasswordFilter implements Filter, ApplicationContextAware {


    private ApplicationContext context;
    private static final Logger log = Logger.getLogger("de.unibi.cebitec.bibiserv.web.controller");
    /**
     * doFilter method of RequestFilter implements the required method of Filer
     * interface Filters and organizes requests to different URLs
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
                
        
        if (request instanceof HttpServletRequest) {

            //set character encoding
            request.setCharacterEncoding("UTF-8");
            
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String url = httpServletRequest.getRequestURI();
            
            // ignore resources folder
            if (url.startsWith("/resources") || url.startsWith("/javax.faces.resource") || url.startsWith("/dwr")) {
                filterChain.doFilter(request, response);
                return;
            }

            
            // test user
            UserBean userb = (UserBean) context.getBean("user");
            if(userb.getUser().isPasswordreset() && !url.startsWith("/admin/changepassword/web/")) {
                ((HttpServletResponse) response).sendRedirect("/admin/changepassword/web/");
                return;
            }
            
            filterChain.doFilter(request, response);
            return;
        }
        throw new UnsupportedOperationException("Error in ResetPasswordFilter, the server is unable to forward your request!");
    }

    /**
     * Required method of Filter interface
     *
     * @param arg0 FilterConfig
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    /**
     * Required Method for implementation of FILTER Interface
     */
    @Override
    public void destroy() {
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

}
