/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Benjamin Paassen, CeBiTec,
 * http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 Benjamin Paassen"
 *
 * Contributor(s): Benjamin Paassen
 *
 */ 
package de.unibi.cebitec.bibiserv.search.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * An extension of a HashMap for storage of multiple values for one key.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class MultiListMap<X, Y> {

    HashMap<X, ArrayList<Y>> actualMap;

    public MultiListMap() {
        this(8);
    }

    public MultiListMap(int initialCapacity) {
        this.actualMap = new HashMap<>(initialCapacity);
    }

    /**
     * Adds a new object to the set of values for this key.
     *
     * @return the new values for this key.
     */
    public ArrayList<Y> put(X key, Y value) {
        ArrayList<Y> values = actualMap.get(key);
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
        actualMap.put(key, values);
        return values;
    }

    /**
     * Returns all values for this key.
     */
    public ArrayList<Y> get(X key) {
        return actualMap.get(key);
    }

    /**
     *
     * @return true if this map is empty.
     */
    public boolean isEmpty() {
        return actualMap.isEmpty();
    }

    /**
     *
     * @return all keys stored in this MultiMap.
     */
    public HashSet<X> keySet() {
        HashSet<X> copy = new HashSet<>(actualMap.size());
        copy.addAll(actualMap.keySet());
        return copy;
    }

    /**
     *
     * @return All values for all keys in this MultiMap.
     */
    public HashSet<Y> valueSet() {
        HashSet<Y> copy = new HashSet<>(actualMap.size());
        for (ArrayList<Y> values : actualMap.values()) {
            copy.addAll(values);
        }
        return copy;
    }

    /**
     * Removes all values for a certain key from this MultiMap.
     *
     * @return true if the key existed before.
     */
    public boolean remove(X key) {
        return actualMap.remove(key) != null;
    }
    
    /**
     * 
     * @return the current size of this map.
     */
    public int size(){
        return actualMap.size();
    }
}
