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
import de.unibi.cebitec.bibiserv.search.index.WordID;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeSearchResult;

/**
 * This represents a search result for a search without spell correction
 * (see also: SearchQueryType.java).
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class WithoutCorrectionSearchResult extends SearchResult {

    /**
     * The actual length of query and found word that match.
     */
    private final int matchLength;

    public WithoutCorrectionSearchResult(SuffixTreeSearchResult result) {
        super(result.getMatchedWordID(), SearchQueryType.WITHOUTCORRECTION);
        this.matchLength = result.getMatchLength();
    }

    @Override
    public int calculateScore() {
        /*
         * in this case the score is the difference in length between the
         * occured word and the matched string.
         */
        /* 
         * first get the id of the word that occured. We can simply use the
         * first occurence in the set of occurences here because the wordID will
         * be the same for all of them.
         */
        WordID wordID = super.getOccurences().iterator().next().getWordID();
        //retrieve the word length.
        int wordLength = wordID.getWordReference().getContent().length();
        return wordLength - matchLength;
    }

    @Override
    public int getMatchLength() {
        return 1;
    }
}
