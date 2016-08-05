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

import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class represents a session for adding words to the suffix tree. At the
 * start of the session (open()) the current Tree is copied and can then be used
 * to append new words. During the sessions runtime no other user can append
 * words to the tree until close() is called for this session.
 * If you call close, the currently used suffixTree is replaced by your copy.
 *
 * This session system has the main advantage that the server can still work on
 * search queries while you are currently appending words to the tree.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public final class SuffixTreeAppendSession {

    /**
     * This locks the opened variable.
     */
    private ReentrantLock openedLock = new ReentrantLock();
    /**
     * This variable stores if the session is opened or not.
     */
    private boolean opened = false;

    /**
     * This class represents a session for adding words to the suffix tree. At
     * the start of the session (open()) the current Tree is copied and can then
     * be used to append new words. During the sessions runtime no other user
     * can append words to the tree until close() is called for this session.
     * If you call close, the currently used suffixTree is replaced by your
     * copy.
     *
     * This session system has the main advantage that the server can still work
     * on search queries while you are currently appending words to the tree.
     */
    public SuffixTreeAppendSession() {
    }

    /**
     * Oppens an append session (if the session is already open, nothing
     * happens).
     */
    public void open() throws SuffixTreeException {
        openedLock.lock();
        try {
            if (!opened) {
                WordIndex.startAppendSession();
                opened = true;
            }
        } finally {
            openedLock.unlock();
        }
    }

    /**
     * Appends a word with the given id to the tree copy.
     */
    public void appendWordToTree(WordID id) throws SuffixTreeException {
        openedLock.lock();
        try {
            if (!opened) {
                throw new SuffixTreeException("This session is not opened yet!");
            } else {
                WordIndex.appendWordToTree(id);
            }
        } finally {
            openedLock.unlock();
        }
    }

    /**
     * closes the append session.
     */
    public void close() throws SuffixTreeException {
        openedLock.lock();
        try {
            if (!opened) {
                throw new SuffixTreeException("This session is not opened yet!");
            } else {
                WordIndex.endAppendSession();
                opened = false;
            }
        } finally {
            openedLock.unlock();
        }
    }
}
