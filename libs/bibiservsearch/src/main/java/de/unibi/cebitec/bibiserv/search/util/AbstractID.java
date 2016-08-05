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

import java.util.concurrent.locks.ReentrantLock;

/**
 * This class provides methods for general ID creation that may be extended by
 * specific ID classes.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public abstract class AbstractID {
    
    private final int actualID = generateID();
    /**
     * Lock to make access to ID generation thread safe.
     */
    private static ReentrantLock generationLock = new ReentrantLock();
    /**
     * Simple counter to construct unique IDs.
     */
    private static int idCounter = 0;

    @Override
    public int hashCode() {
        return actualID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractID other = (AbstractID) obj;
        if (this.actualID != other.actualID) {
            return false;
        }
        return true;
    }

    private static int generateID() {
        generationLock.lock();
        try {
            return idCounter;
        } finally {
            idCounter++;
            generationLock.unlock();
        }
    }
    
    /**
     * Resets document id generation mechanism.
     */
    public static void resetIDGeneration(){
        generationLock.lock();
        try{
            idCounter = 0;
        } finally{
            generationLock.unlock();
        }
    }
}
