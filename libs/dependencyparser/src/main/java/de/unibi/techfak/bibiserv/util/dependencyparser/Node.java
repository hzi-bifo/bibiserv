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

import java.util.List;


/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public abstract class Node {

    ConstraintHashMap missingconstraints = new ConstraintHashMap ();

    ConstraintHashMap fullfilledconstraints = new ConstraintHashMap ();

    /** Evaluate generated Tree. If the given parameter set fulfill dependency
     * tree return true, false otherwise
     * @return 
     * @throws de.unibi.techfak.bibiserv.util.dependencyparser.DependencyException */
    public abstract boolean evaluate() throws DependencyException;

    /**
     * Returns a list of id's, describing the missing parameter constraints, from the last evaluate run.
     *
     * @return
     */
    public ConstraintHashMap getMissingConstraints() {
        return missingconstraints;
    }

    /**
     * Returns a list of id's, describing the fulfilled parameter constraints, from the last evaluate run.
     *
     * @return
     */
    public ConstraintHashMap getFullfilledConstraints() {
        return fullfilledconstraints;
    }

    /** 
     * Return all childnodes of current Node. This function isn't neccessary to evaluate current Node,
     * but helpful to transform current to to another represenation.
     * 
     * @return Return all child nodes of current node.
     */
    public abstract List<Node> getChildNodes();
    
    
    /**
     * Return true in the case current Node has one ore more child nodes, false otherwise.
     * This function isn't neccessary to evaluate current Node, but helpful to 
     * transform current to to another represenation.
     * 
     * @return Return true in the case current Node has one ore more child nodes, false otherwise.
     */
    public abstract boolean hasChildNodes();

    
    /**
     *  Return a String representation of current node
     */
    public abstract String toString();
    
}
