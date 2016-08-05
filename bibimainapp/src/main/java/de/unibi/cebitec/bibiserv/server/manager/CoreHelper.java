/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2013 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.server.manager;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Helper class that initialize the Manager.Core and set the ApplicationContext.
 *    
 * 
 * @author Jan Krueger - jkrueger@cebitec.uni-bielefeld.de
 */
public class CoreHelper implements ApplicationContextAware{

     private static Logger log = Logger.getLogger("de.unibi.cebitec.bibiserv.server.manager");
    
    @Override
    public void setApplicationContext(ApplicationContext applicationcontext) throws BeansException {
         try {
             log.debug("set ApplicationContext");
             Core core = Core.getInstance();
             core.setApplicationContext(applicationcontext);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FatalBeanException(e.getMessage());
        }
    }
    
}
