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
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class ConstraintHashMap extends HashMap<Id, List<Constraints>> {

    public List<Constraints> put(Id key, Constraints value) {
        // if value is null (which means, that Constraint is missing/fullfilled without special value
        if (value == null) {
            // and key already known , we shouldn't do nothing
            if (containsKey(key)){
                return super.get(key);
            } else {
                return super.put(key,null);
            }
        }
        // otherwise we should
        List<Constraints> _value = get(key);
        if (_value == null) {
            _value = new ArrayList<Constraints>();
            _value.add(value);
            super.put(key, _value);
        } else {
            _value.add(value);
        }
        return _value;
    }

    @Override
    public List<Constraints> put(Id key, List<Constraints> valuelist) {
        if (valuelist == null || valuelist.isEmpty()){
            return put(key,(Constraints)null);
        }
        for (Constraints tmp : valuelist){
            put (key,tmp);
        }
        return get(key);
    }

    public void putAll(ConstraintHashMap chm) {
        for (Id id : chm.keySet()) {
//            if (containsKey(id)) {
//                List<Constraints> _value = get(id);
//                if (_value == null) {
//                    _value = new ArrayList<Constraints>();
//                    _value.addAll(chm.get(id));
//                    put(id, _value);
//                } else {
//                    _value.addAll(chm.get(id));
//                }
//                put
//            } else {
//                put(id, get(id));
//            }
            put(id,chm.get(id));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Id key : keySet()) {
            sb.append(key).append("->(");
            boolean first = true;
            if (get(key) != null) {
                for (Constraints c : get(key)) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(",");
                    }
                    if (c.getOperation() != null) {
                        sb.append(c.getOperation());
                        sb.append(c.getValue());

                    }
                }
            } 
            sb.append(")");
        }
        return sb.toString();
    }
}
