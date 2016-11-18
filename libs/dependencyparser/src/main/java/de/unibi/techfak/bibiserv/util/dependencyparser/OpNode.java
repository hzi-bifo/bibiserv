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
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public abstract class OpNode extends Node {

    public enum Operations {AND,OR,XOR,NOT,IMPL,LOGEQ,EQ,NE,GT,GE,LT,LE,NULL}


    protected Operations operation = null;

    protected Node a = null;
    protected Node b = null;


    public OpNode(Operations operation){
        this.operation = operation;
    }

    public OpNode(Operations operation, Node a){
        this.operation = operation;
        this.a = a;
    }

    public OpNode(Operations operation, Node a,Node b){
        this.operation = operation;
        this.a = a;
        this.b = b;
    }

    public void add_A(Node node){
        a = node;
    }

    public void add_B(Node node){
        b = node;
    }

    public void setOperation(Operations operation){
        this.operation = operation;
    }
    
    public Operations getOperation(){
        return operation;
    }

    @Override
    public List<Node> getChildNodes() {
        List l = new ArrayList();
        if (a != null) { 
            l.add(a);      
        }
        if (b != null) {
            l.add(b);
        }
        return l;
    }

    @Override
    public boolean hasChildNodes() {
        return getChildNodes().size() > 0;
    }
    
    
    @Override
    public String toString() {
        return operation.name();
    }


}
