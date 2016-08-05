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
package de.unibi.cebitec.bibiserv.search.results;

import de.unibi.cebitec.bibiserv.search.helper.SearchQueryType;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeSearchResult;

/**
 * This represents a search result for a search with spell correction
 * (see also: SearchQueryType.java).
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class RegularSearchResult extends SearchResult {

    /**
     * The actual length of query and found word that match.
     */
    private final int matchLength;
    /**
     * True if spellcorrection mechanisms had to be used for this match.
     */
    private final boolean spellcorrectionUsed;

    public RegularSearchResult(SuffixTreeSearchResult result) {
        super(result.getMatchedWordID(), SearchQueryType.REGULAR);
        this.matchLength = result.getMatchLength();
        this.spellcorrectionUsed = result.isSpellcorrectionUsed();
    }

    @Override
    public int calculateScore() {
        if (spellcorrectionUsed) {
            return matchLength + 3;
        }
        return matchLength;
    }

    @Override
    public int getMatchLength() {
        return 1;
    }
}
