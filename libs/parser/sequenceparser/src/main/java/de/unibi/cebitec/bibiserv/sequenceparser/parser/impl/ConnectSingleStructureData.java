/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.sequenceparser.parser.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is a wrapper structure for connect single structure content.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class ConnectSingleStructureData {

    /**
     * the structure header containing energy information.
     */
    private String energyHeader;
    /**
     * HashMap containing all bases in this structure.
     */
    private HashMap<Integer, ConnectBase> bases = new HashMap<Integer, ConnectBase>();
    /**
     * Stores the minimum index contained in the structure.
     */
    private int minIndex = -1;
    /**
     * Stores the maximum index contained in the structure.
     */
    private int maxIndex;
    /**
     * Lock to make hashMap operations thread safe.
     */
    private ReentrantLock baseLock = new ReentrantLock();

    /**
     * Creates a new .ct structure data object.
     *
     * @param energyHeader the structure header containing energy information.
     */
    public ConnectSingleStructureData(String energyHeader) {
        super();
        this.energyHeader = energyHeader;
    }

    /**
     *
     * @return .ct structure header containing information about the structures
     * energy.
     */
    public String getEnergyHeader() {
        return energyHeader;
    }

    /**
     *
     * @return all base indices contained in this structure.
     */
    public Set<Integer> getBaseIndices() {
        HashSet<Integer> indices = new HashSet<Integer>();
        baseLock.lock();
        try {
            indices.addAll(bases.keySet());
        } finally {
            baseLock.unlock();
        }
        return indices;
    }

    /**
     * stores a new base in this structure.
     *
     * @param index index of the base.
     * @param base base in one-letter notation.
     * @param partnerIndex index of pairing partner.
     */
    public void put(int index, char base, int partnerIndex) {
        ConnectBase baseWrapper = new ConnectBase(index, base, partnerIndex);
        baseLock.lock();
        try {
            if (index < minIndex || minIndex == -1) {
                minIndex = index;
            }
            if (index > maxIndex) {
                maxIndex = index;
            }
            bases.put(index, baseWrapper);
        } finally {
            baseLock.unlock();
        }
    }

    /**
     *
     * @param index base index.
     * @return true if a base with the given index is contained in this
     * structure.
     */
    public boolean contains(Integer index) {
        baseLock.lock();
        try {
            return bases.containsKey(index);
        } finally {
            baseLock.unlock();
        }
    }

    /**
     *
     * @param index index of this base.
     * @return base in one-letter format (or . if base index is not
     * contained).
     */
    public char getBase(Integer index) {
        ConnectBase base;
        baseLock.lock();
        try {
            base = bases.get(index);
        } finally {
            baseLock.unlock();
        }
        if (base == null) {
            return '.';
        } else {
            return base.getBase();
        }
    }

    /**
     *
     * @param index index of this base.
     * @return index of base partner (or null if base if index is not
     * contained).
     */
    public Integer getBasePartner(Integer index) {
        ConnectBase base;
        baseLock.lock();
        try {
            base = bases.get(index);
        } finally {
            baseLock.unlock();
        }
        if (base == null) {
            return null;
        } else {
            return base.getPartnerIndex();
        }
    }

    /**
     *
     * @return Stores the minimum index contained in the structure.
     */
    public int getMinIndex() {
        baseLock.lock();
        try {
            return minIndex;
        } finally {
            baseLock.unlock();
        }
    }

    /**
     *
     * @return Stores the maximum index contained in the structure.
     */
    public int getMaxIndex() {
        baseLock.lock();
        try {
            return maxIndex;
        } finally {
            baseLock.unlock();
        }
    }
}

/**
 * Wrapper structure for base information stored in connect structure data.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
class ConnectBase {

    private int index;
    private char base;
    private int partnerIndex;

    /**
     * Creates a new base object.
     *
     * @param index base index in structure.
     * @param base base letter.
     * @param partnerIndex base index of partner.
     */
    public ConnectBase(int index, char base, int partnerIndex) {
        this.index = index;
        this.base = base;
        this.partnerIndex = partnerIndex;
    }

    /**
     *
     * @return base index in structure.
     */
    public int getIndex() {
        return index;
    }

    /**
     *
     * @return base letter.
     */
    public char getBase() {
        return base;
    }

    /**
     *
     * @return base index of partner.
     */
    public int getPartnerIndex() {
        return partnerIndex;
    }
}
