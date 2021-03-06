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
package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.techfak.bibiserv.User;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

/**
 * userInterface describes an interface for all information needed to access user informations.
 *
 * @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de
 */
public interface UserInterface {


    /**
     * Return the user id as string.
     *
     * @return Return user id as string
     */
    public String getId();
    
    /**
     * Return all roles current user has.
     *
     * @return Return a collection of roles current user has.
     */
    public Collection<GrantedAuthority> getRoles();
    
    /**
     * Check if a current user has given role.
     *
     * @param role - Role to be checked.
     * @return Return true if current user has given role.
     */
    public boolean isUserInRole(String role);
    
    /**
     * Check if current user has role 'ROLE_ANONYMOUS'.
     *
     * @return Return true if current user has role 'ROLE_ANONYMOUS'.
     */
    public boolean isAnonymous();
    
    /**
     * Return a BiBiServ User object.
     * 
     * @return Return a BiBiServ User object.
     */
    
    public User getUser();
    

}
