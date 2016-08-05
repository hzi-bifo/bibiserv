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
 * Contributor(s): Jan krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *
 */
package de.unibi.cebitec.bibiserv.web.xmlreader;

/**
 * Concrete Implementation of an ItemContent for a linked item nodes.
 * <p>
 * It supports 3 different kind of behavior depending on the given UR(L\I).
 * </p>
 * 
 * <ol>
 *  <li>Give a server/host only URL. Example : A linked item with id 'cgi-bin' defines 
 *  the URL http://myserver.com. Any request to the local server with URL-path prefix /cgi-bin/...
 *  is then proxied to http://myserver.com/cgi-bin/... </li>
 *  <li>Give a complete absolute URL. Example: A linkeditem with id 'wiki' defines the URL 
 * http://wiki.techfak.uni-bielefeld.de/bibiserv. Any request to the local server
 * with URL-path prefix /wiki/... is redirected to http://wiki.techfak.uni-bielefeld.de/bibiserv. </li>
 * <li>Give a relative URL. A linkeditem with id 'prevres' defines the URL '/prevres.jsf'.
 *  Any request to the local server with URL-path prefix '/prevres' redirects directly to
 * local adress .../prevres.jsf. </li>
 * 
 * </ol>
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class LinkedItemContent extends ItemContent{
    
    
    
    /*  protocol used for proxy redirect - supported by linkeditem */
    private String protocol = null;
    
    /* server for proxy redirect - supported by linkeditem */
    private String server = null;

    /* port for proxy redirect - supported by linkeditem */
    private int port;
    
    /* URL as String contains the (maybe local) redirect URL in the of an non 
       proxy request.*/
    private String URL = null;
    
    /* Declare if redirect is local */
    private boolean local = false;
    

    public int getPort() {
        return port;
    }


    public void setPort(int port) {
        this.port = port;
    }


    public String getServer() {
        return server;
    }
    
    public void setServer(String server) {
        this.server = server;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
    
    
    
}
