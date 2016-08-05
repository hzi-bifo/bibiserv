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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

/**
 * General index class that ensures thread safe access to index class.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class Index<X, Y> {

    /**
     * Reentrant Lock that ensures thread safe access to the index.
     */
    private ReentrantLock lock = new ReentrantLock();
    /**
     * The actual index that contains content.
     */
    private HashMap<X, Y> actualIndex = new HashMap<>();

    /**
     * Simple constructor that initializes the classes internal datastructures.
     */
    public Index() {
    }

    /**
     * Retrieves an item from the index
     *
     * @param key the key value
     * @return the actual value that is mapped to the given key.
     */
    public Y get(X key) {
        lock.lock();
        try {
            return actualIndex.get(key);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds an item to the index.
     *
     * @param key the key value
     * @param value the actual value that shall be mapped to the given key.
     * @return the previous value associated with the given key (or null if
     * there
     * wasn't a value before)
     */
    public Y put(X key, Y value) {
        lock.lock();
        try {
            return actualIndex.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes the entry for the given key from the index.
     *
     * @param key key of the entry that shall be removed.
     * @return the value previously associated with the given key (or null if
     * there
     * wasn't a value for the given key in the index).
     */
    public Y remove(X key) {
        lock.lock();
        try {
            return actualIndex.remove(key);
        } finally {
            lock.unlock();
        }
    }

    /**
     *
     * @return the current size of the index.
     */
    public int size() {
        lock.lock();
        try {
            return actualIndex.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * This method allows you to genericly transform the Set of Keys of this
     * index to a set of target objects.
     *
     * Please note that the use of this method blocks the index and may take a
     * while
     * depending of the time your transformation needs!
     *
     * @param <Z> target class of the transformation.
     * @param transformation the transformation action that transforms key
     * elements
     * of the index into your desired target class.
     * @return A copy of the transformed key set.
     */
    public <Z> ArrayList<Z> transformKeySet(TransformationAction<X, Z> transformation) {
        ArrayList<Z> result = new ArrayList<>();
        lock.lock();
        try {
            result = Collections.transform(actualIndex.keySet(), transformation);
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     *
     * @return a copy of the current keys in this index.
     */
    public HashSet<X> getKeys() {
        HashSet<X> returnSet = new HashSet<>();
        addAllKeys(returnSet);
        return returnSet;
    }

    /**
     * @return a copy of the current values in this index.
     */
    public HashSet<Y> getValues() {
        HashSet<Y> returnSet = new HashSet<>();
        addAllValues(returnSet);
        return returnSet;
    }

    /**
     *
     * @return a copy of all entries in this index.
     */
    public HashSet<Entry<X, Y>> getEntries() {
        HashSet<Entry<X, Y>> returnSet = new HashSet<>();
        addAllEntries(returnSet);
        return returnSet;
    }

    /**
     * Adds all keys of this index to the given collection.
     *
     * @param collection a collection all keys shall be added to (please
     * ensure thread safe access to this collection!)
     */
    public void addAllKeys(Collection<X> collection) {
        lock.lock();
        try {
            collection.addAll(actualIndex.keySet());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds all values of this index to the given collection.
     *
     * @param collection a collection all values shall be added to (please
     * ensure thread safe access to this collection!)
     */
    public void addAllValues(Collection<Y> collection) {
        lock.lock();
        try {
            collection.addAll(actualIndex.values());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds all entries of this index to the given collection.
     *
     * @param collection a collection all entries shall be added to (please
     * ensure thread safe access to this collection!)
     */
    public void addAllEntries(Collection<Entry<X, Y>> collection) {
        lock.lock();
        try {
            collection.addAll(actualIndex.entrySet());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clears the index.
     */
    public void reset() {
        lock.lock();
        try {
            actualIndex.clear();
        } finally {
            lock.unlock();
        }
    }
}
