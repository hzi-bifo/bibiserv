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
package de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes;

import java.net.URL;

/**
 * Interface for the access to a person object for the ontology, 
 * currently only giving minimal information for referencing
 * 
 * @author shartmei
 */
public interface BiBiPerson {

    String getFamily_name();
    void setFamily_name(String family_name);

    String getGivenname();
    void setGivenname(String givenname);   
    
    /**
     * returns the combination of given and family names,
     * or the specified name string, if set by 'setName(name)'
     * @return full name
     */
    String getName();
    
    /**
     * Set the full name String for this person. Set to 'null' to reset
     * specific name and get auto-generated name with 'getName()
     * @param name 
     */
    void setName(String name);
    
    String getEmail();
    void setEmail(String email);
    
    URL getWebpage();
    void setWebpage(String url);
    
}
