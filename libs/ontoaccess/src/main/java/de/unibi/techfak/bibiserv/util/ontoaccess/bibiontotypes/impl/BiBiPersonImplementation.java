/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s):
 *
 */
package de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl;

import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPerson;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shartmei
 */
public class BiBiPersonImplementation implements BiBiPerson {
    //The naming member fields could be a problem, currently they are
    //modeling standard western naming conventions
    
    private String givenname;       // Given Name(s)
    private String family_name;     // Family Name
    private String name=null;       // Full Name
    private String email=null;
    private URL homepage=null;

    @Override
    public String getFamily_name() {
        return family_name;
    }

    @Override
    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    @Override
    public String getGivenname() {
        return givenname;
    }

    @Override
    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    @Override
    public String getName() {
        return (name == null) ? givenname.concat(" ").concat(family_name) : name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return (email == null) ? "" : email;
    }

    public void setEmail(String _email) {
        this.email = _email;
    }

    public URL getWebpage() {
        URL retval=null;
        try {
            if (homepage == null) { 
                retval = new URL("");
            } else {
                retval = homepage;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(BiBiPersonImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    public void setWebpage(String url) {
        try {
            this.homepage = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(BiBiPersonImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
