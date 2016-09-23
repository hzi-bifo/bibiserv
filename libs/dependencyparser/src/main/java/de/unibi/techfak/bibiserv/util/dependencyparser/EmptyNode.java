/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
All rights reserved.

The contents of this file are subject to the terms of the Common
Development and Distribution License("CDDL") (the "License"). You
may not use this file except in compliance with the License. You can
obtain a copy of the License at http://www.sun.com/cddl/cddl.html

See the License for the specific language governing permissions and
limitations under the License.  When distributing the software, include
this License Header Notice in each file.  If applicable, add the following
below the License Header, with the fields enclosed by brackets [] replaced
by your own identifying information:

"Portions Copyrighted 2011 BiBiServ"

Contributor(s):
 */
package de.unibi.techfak.bibiserv.util.dependencyparser;

import java.util.ArrayList;
import java.util.List;

/**
 * EmptyNode - represents an empty tree. Only used in the case no dependency string is set.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class EmptyNode extends Node{

    @Override
    public boolean evaluate() throws DependencyException {
       return true;
    }

    @Override
    public List<Node> getChildNodes() {
       return new ArrayList<Node>();
    }

    @Override
    public boolean hasChildNodes() {
        return false;
    }

    @Override
    public String toString() {
        return "empty";
    }
    
    
    
    
    
    

}
