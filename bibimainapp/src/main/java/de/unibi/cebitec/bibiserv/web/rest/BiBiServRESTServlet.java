/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.rest;


import com.sun.jersey.spi.container.servlet.ServletContainer;
import de.unibi.cebitec.bibiserv.server.manager.Core;
import javax.ws.rs.core.Application;

/**
 *
 * @author jkrueger
 */
public class BiBiServRESTServlet extends ServletContainer{

    private Core core = Core.getInstance();
    
    
    
    public BiBiServRESTServlet() {
        super();
        //register current servlet instance at CoreI
        core.addRestServletContainer(this);
        
    }

    public BiBiServRESTServlet(Application app) {
        super(app);
        //register current servlet instance at Core
        core.addRestServletContainer(this);
    }

    public BiBiServRESTServlet(Class<? extends Application> appClass) {
        super(appClass);
        //register current servlet instance at Core
        core.addRestServletContainer(this);
    }
    
    
   
    
    
}
