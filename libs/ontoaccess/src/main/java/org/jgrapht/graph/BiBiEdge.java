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
 * "Portions Copyrighted 2010 BiBiServ"
 *
 * Contributor(s): Jan Krueger
 *
 */
package org.jgrapht.graph;



/**
 *
 * Extension of default edge, allowing easy access to start/end object of an edge.
 * 
 * JK: Ugly hack : Must move BiBiEdge to 'org.jgrapht.graph' to get access to package variables.
 * 
 * @author Sven Hartmeier - shartmei(at) techfak.uni-bielefeld.de, Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * 
 */
public class BiBiEdge extends DefaultEdge {

    public String getStart() {
        
        
        return source.toString();
    }

    public String getEnd() {
        return target.toString();
    }
}
