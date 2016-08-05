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
 * Contributor(s): Jan Krueger, Benjamin Paassen
 *
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import java.io.IOException;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

/**
 * This bean is only used to save the link for the session timeout 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class SessionTimeout {
    
    Logger log = Logger.getLogger(SessionTimeout.class);
    
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setCleanedUrl(String url) {
        this.url = url;
        
        if(url.startsWith("/applications")) {
            String[] split = url.split("/");
            if(split.length>2){
                this.url = "/"+split[2];
            }
        }
    }
    
    public void redirect(){
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(this.url);
        } catch (IOException ex) {
            log.warn("Could not redirect: "+ex);
        }
    } 
    
}
