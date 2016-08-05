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
package de.unibi.cebitec.bibiserv.web.rest;

import de.unibi.cebitec.bibiserv.server.manager.Core;
import de.unibi.cebitec.bibiserv.server.manager.Manager;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.apache.log4j.Logger;

/**
 * We define our own provider for  JSR311 (REST) annotated classes here.
 * 
 * 
 * @author Jan Krueger jkrueger(at)cebitec.uni-bielefeld.de
 */
public class BiBiServApplication extends Application{
    
    private static Logger log = Logger.getLogger(BiBiServApplication.class.getName());
    
  
    private Core core = Core.getInstance();
    
    @Override
    public Set<Class<?>> getClasses() {
       
       Set<Class<?>> s = new HashSet();
       // the Manager application can be hardcoded 
        s.add(Manager.class);
        // add all other JAXRS annatoted classes from Core
        s.addAll(core.getRESTClazzes());
        
        return s;
    }

    @Override
    public Set<Object> getSingletons() {
        return super.getSingletons();
    }
    

   
    
}
