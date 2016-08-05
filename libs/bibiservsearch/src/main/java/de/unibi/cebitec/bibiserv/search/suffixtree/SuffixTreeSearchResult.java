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
package de.unibi.cebitec.bibiserv.search.suffixtree;

import de.unibi.cebitec.bibiserv.search.index.WordID;
import java.util.Objects;

/**
 * This represents a search result of a suffix tree matching process.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeSearchResult {

    /**
     * The length of the match with the query sequence in the matched word.
     */
    private final int matchLength;
    /**
     * True if spellcorrection had to be used for the match.
     */
    private final boolean spellcorrectionUsed;
    /**
     * The id of the word that has been matched.
     */
    private final WordID matchedWordID;

    /**
     *
     * @param matchLength The length of the match with the query sequence in the
     * matched word.
     * @param spellcorrectionUsed True if spellcorrection had to be used for the
     * match.
     * @param matchedWordID The id of the word that has been matched.
     */
    public SuffixTreeSearchResult(int matchLength, boolean spellcorrectionUsed, WordID matchedWordID) {
        this.matchLength = matchLength;
        this.spellcorrectionUsed = spellcorrectionUsed;
        this.matchedWordID = matchedWordID;
    }

    public int getMatchLength() {
        return matchLength;
    }

    public boolean isSpellcorrectionUsed() {
        return spellcorrectionUsed;
    }

    public WordID getMatchedWordID() {
        return matchedWordID;
    }

    @Override
    public int hashCode() {
        return matchedWordID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SuffixTreeSearchResult other = (SuffixTreeSearchResult) obj;
        if (!Objects.equals(this.matchedWordID, other.matchedWordID)) {
            return false;
        }
        return true;
    }
}
