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

/**
 * An occurence identifies the occurence of a word. E.g. there might be multiple
 * occurences of words even in the same document.
 *
 * For future extensions of the BiBiServSearch it might be interesting to
 * characterize occurences not only by their document and their following word
 * but also by page and line or something similar. This would make better
 * visualization of search results possible.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public final class Occurence {

    /**
     * The id of the document this occurence happened in.
     */
    private final DocumentID docID;
    /**
     * The id of the word that occured.
     */
    private final WordID wordID;
    /**
     * The occurence that follows this occurence.
     */
    private Occurence next;
    /**
     * The occurence that preceeds this occurence.
     */
    private Occurence previous;

    /**
     *
     * @param id The id of the document this occurence happened in.
     * @param wordID The id of the word that occured.
     */
    public Occurence(DocumentID id, WordID wordID) {
        this.docID = id;
        this.wordID = wordID;
    }

    public DocumentID getId() {
        return docID;
    }

    public WordID getWordID() {
        return wordID;
    }

    public void setNext(Occurence next) {
        this.next = next;
    }

    public Occurence getNext() {
        return next;
    }

    public void setPrevious(Occurence previous) {
        this.previous = previous;
    }

    public Occurence getPrevious() {
        return previous;
    }
}