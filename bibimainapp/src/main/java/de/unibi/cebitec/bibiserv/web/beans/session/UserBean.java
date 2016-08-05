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
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.techfak.bibiserv.User;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.exception.IdInvalidException;
import de.unibi.techfak.bibiserv.exception.IdNotFoundException;
import de.unibi.techfak.bibiserv.web.beans.session.UserInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * User Bean as Proxy between PrimeFaces and SpringSecurity.
 *
 *
 * @author Jan Krueger jkrueger(at)cebitec.uni-bielefeld.de
 */
public class UserBean implements InitializingBean, UserInterface {

    private static final Logger log = Logger.getLogger(UserBean.class);

    // should be initialized after bean creation with usefull defaults
    private User user;
    private String id;
    private Collection<GrantedAuthority> roles;

    /**
     * Return the user id as string.
     *
     * @return Return user id as string
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Return all roles current user has.
     *
     * @return Return a collection of roles current user has.
     */
    @Override
    public Collection<GrantedAuthority> getRoles() {
        return roles;
    }

    /**
     * Check if a current user has given role.
     *
     * @param role - Role to be checked.
     * @return Return true if current user has given role.
     */
    @Override
    public boolean isUserInRole(String role) {
        for (GrantedAuthority auth : getRoles()) {
            if (auth.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current user has role 'ROLE_ANONYMOUS'.
     *
     * @return Return true if current user has role 'ROLE_ANONYMOUS'.
     */
    @Override
    public boolean isAnonymous() {
        return id.equals(User.ANONYMOUS);
    }

    /**
     * Implementation of Interface InitializeBean
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        reset();
    }

    /**
     * Return a BiBiServ User object.
     *
     * @return Return a BiBiServ User object.
     */
    @Override
    public User getUser() {
       return user;
    }

    /**
     * Reset UserBean, should be called after successful logout 
     */
    public void reset() {
        id = User.ANONYMOUS;
        try {
            user = new User();
        } catch (DBConnectionException e) {
            log.fatal(e.getMessage(), e);
        }
        roles = new ArrayList<>();
    }

    /**
     * Refresh UserBean, should be called after successful login 
     * @param auth - SpringSecurity Authentication object
     * 
     */
    public void refresh(Authentication auth) {
        // update id
        try {
            Object o = auth.getPrincipal();
            if (o.getClass().getName().equals("org.springframework.security.core.userdetails.User")) {
                org.springframework.security.core.userdetails.User u = (org.springframework.security.core.userdetails.User) o;
                id = u.getUsername();
            } else {
                id = o.toString();
            }
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
        // update User object
        try {
            try {
                user = new User(id);
            } catch (IdNotFoundException e) { // both exception shouldn't occur since we retrieve the id's from database
                log.error("ID '" + id + "' is an unknown/invalid in database! Return an anonymous user object!", e);
                user = new User();
            }
        } catch (DBConnectionException e) {
            log.fatal("A DBConnection occurred :" + e.getMessage(), e);
        }
        // update roles     
        if (auth.getAuthorities() != null) {
            roles = auth.getAuthorities();
        }
    }
}
