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
package de.unibi.cebitec.bibiserv.search.index;

import de.unibi.cebitec.bibiserv.search.util.MultiListMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This object contains ids of documents a word occured in and maps those IDs
 * to specific occurences of a word.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public final class OccurenceSet {

    /**
     * Lock to make set operations thread safe.
     */
    private ReentrantLock setLock = new ReentrantLock();
    /**
     * The actual set.
     */
    private MultiListMap<DocumentID, Occurence> actualMap = new MultiListMap<>();

    /**
     * This constructor initializes an OccurenceSet.
     */
    public OccurenceSet() {
    }

    /**
     * Adds an occurence to the set.
     *
     * @param occurence a new occurence of a word.
     */
    public void addOccurence(Occurence occurence) {
        setLock.lock();
        try {
            actualMap.put(occurence.getId(), occurence);
        } finally {
            setLock.unlock();
        }
    }

    /**
     * Removes a document id from the set.
     *
     * @param id the id that shall be removed.
     */
    public void removeReferenceByID(DocumentID id) {
        setLock.lock();
        try {
            actualMap.remove(id);
        } finally {
            setLock.unlock();
        }
    }

    /**
     *
     * @return a set of all occurences for this word. This does not manipulate
     * the Occurence set itself. A copy is returned.
     */
    public Collection<Occurence> getAllOccurences() {
        ArrayList<Occurence> setCopy = new ArrayList<>();
        setLock.lock();
        try {
            setCopy.addAll(actualMap.valueSet());
        } finally {
            setLock.unlock();
        }
        return setCopy;
    }

    /**
     *
     * @return true if the set is empty.
     */
    public boolean isEmpty() {
        setLock.lock();
        try {
            return actualMap.isEmpty();
        } finally {
            setLock.unlock();
        }
    }
    
    /**
     * 
     * @return the current number of entries stored in this set.
     */
    public int size(){
         setLock.lock();
        try {
            return actualMap.size();
        } finally {
            setLock.unlock();
        }
    }
}
