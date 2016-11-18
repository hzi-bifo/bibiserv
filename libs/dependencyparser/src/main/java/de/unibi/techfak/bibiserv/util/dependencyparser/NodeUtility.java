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

Contributor(s): Jan Krueger
 */
package de.unibi.techfak.bibiserv.util.dependencyparser;

/**
 * Provides some static utility functions.
 * 
 * 
 * @author jkrueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class NodeUtility {

    /** 
     * Converts a given Dependency node(tree) into its string representation 
     * 
     * @param n - Dependency Node
     * @return  Returns String representation of give node.
     */
    public static String Node2String(Node n) {
        return Node2StringHelper(n, null);

    }
    
    
    private static String Node2StringHelper(Node n, Node p) {
         String ret = "";
        if (n instanceof OpNode) {
            //work on childs
            if (n.hasChildNodes()) {
                for (int i = 0; i < n.getChildNodes().size(); ++i) {
                    if (i > 0) {
                        ret = ret + ",";
                    }
                    ret = ret + Node2StringHelper(n.getChildNodes().get(i),n);
                }
            }
            OpNode on = (OpNode) n;
            ret = on.getOperation().toString().toLowerCase() + "(" + ret + ")";
        } else if (n instanceof Id) {
            ret = "@"+((Id)n).getId();
            if (p != null && !(p instanceof CmpOpNode)) {
                ret = "def("+ret+")";
            }
            
        } else if (n instanceof Const) {
            ret =  ((Const)n).getValues().get(0).toString();
            
        } else if (n instanceof EmptyNode) {
            ret = "EMPTY";
        }
        return ret;
    }
}
