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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */ 

package de.unibi.techfak.bibiserv.util;

import java.util.List;

/**
 * Internal class that can store a pair of Strings. This class is mainly used to store
 * a parameter document of the form.
 * <pre>
 *  &lt;param>&gt;s
 *    &lt;param_key&gt;param_value&lt;/param_key&gt;>
 *    &lt;param_key>&gt;param_value2&lt;/param_key&gt;>
 *    ...
 *    &lt;param_key2&gt;>param_value3&lt;/param_key&gt;>
 *    ...
 * </pre>
 *
 * in a List of Pair object.
 *
 *              */
public class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
    
    @Override
    public String toString(){
        return "("+key.toString()+","+value.toString()+")";
    }
    
    /**
     * Returns a String representation of a list of pairs, separated by a ','.
     * 
     * @param p
     * @return 
     */
    public static String listOfPairstoString (List<Pair<String,String>> pl){
        return listOfPairstoString(pl,',');
    }
    
    /**
     * Returns a String representation of a list of pairs, separated by the given char.
     * 
     * @param pl
     * @param separator
     * @return 
     */
    public static String listOfPairstoString(List<Pair<String,String>> pl,char separator){
        StringBuilder sb = new StringBuilder();
        
        for (Pair p : pl){    
            sb.append(p.toString()).append(separator);
        }
        
        // remove last separator char
        sb.setLength(sb.length()-1);
        
        return sb.toString();
    }
}

