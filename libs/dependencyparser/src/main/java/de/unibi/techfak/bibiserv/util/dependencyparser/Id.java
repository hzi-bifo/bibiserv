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
 * IdNode - represents an Id (aka parameter id)
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class Id extends LeafNode implements Comparable<Id> {

    private String id;
    private ParameterWrapper pw;


    public Id(String id, TYPE type) {
        this.id = id;
        this.type = type;
    }

    public Id(String id, TYPE type, ParameterWrapper pw) {
        this.id = id;
        this.type = type;
        this.pw = pw;
    }



    public String getId(){
        return id;
    }

    public TYPE getType(){
        return type;
    }



    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Id other = (Id) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean evaluate() throws DependencyException {
        if (pw == null) {
            throw new DependencyException("Call evaluate with non set parameterwrapper ...", DependencyExceptionEnum.noParameterWrapper, "");

        }
        missingconstraints.clear();
        fullfilledconstraints.clear();
        valuelist.clear();

        if (pw.idExists(id)) {
            /* Remark :
             *
             * A parameter can be occurred more than once (e.g. for multiple select lists).
             *
             *
             */
            List<String> tmplist = pw.getValue(id);

            for (String tmp : tmplist) {
            switch (type) {
                case STRING:
                    valuelist.add(tmp);
                    break;
                case INT:
                    try {
                        valuelist.add(Integer.parseInt(tmp));
                    } catch (NumberFormatException e) {
                        throw new DependencyException(e, DependencyExceptionEnum.stringToTypeCastFailed, "Integer: "+tmp);
                    }
                    break;
                case FLOAT:
                    try {
                        valuelist.add(Float.parseFloat(tmp));
                    } catch (NumberFormatException e) {
                        throw new DependencyException(e, DependencyExceptionEnum.stringToTypeCastFailed, "Float: "+tmp);
                    }
                    break;
                case BOOLEAN:
                    valuelist.add(Boolean.parseBoolean(tmp));
                    break;
                default:
                    throw new DependencyException("Type " + type + " not supported or yet implemented ...", DependencyExceptionEnum.unsupportedType, type.toString());
            }
            }
            fullfilledconstraints.put(this,new ArrayList<Constraints> ());
            return true;
        }
        missingconstraints.put(this,new ArrayList<Constraints> ());
        return false;
    }



    @Override
    public String toString(){
        return id;
    }



    @Override
    public int compareTo(Id o) {
        return id.compareTo(o.getId());
    }


}
