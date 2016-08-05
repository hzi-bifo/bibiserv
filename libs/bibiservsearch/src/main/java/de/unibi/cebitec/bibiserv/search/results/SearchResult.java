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
import de.unibi.cebitec.bibiserv.search.index.MainIndex;
import de.unibi.cebitec.bibiserv.search.index.Occurence;
import de.unibi.cebitec.bibiserv.search.index.WordID;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This represents the result of a search. There are different types of
 * search queries (see SearchQueryType.java) which also leads to different
 * types of search results.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public abstract class SearchResult {

    /**
     * all occurences of this search result.
     */
    private final Collection<Occurence> occurences;
    /**
     * This search results type.
     */
    private final SearchQueryType type;

    /**
     * This method calculates how close this search result is to the actual
     * query of the user. A higher score means greater distance. A score of 0
     * would mean a perfect match. Negative scores indicate multiple occurences.
     *
     * @return this search results score.
     */
    public abstract int calculateScore();

    /**
     * Constructor for a search result.
     *
     * @param matchedWord id of the word that has actually been matched.
     * @param type This search results type.
     */
    public SearchResult(WordID matchedWord, SearchQueryType type) {
        this.occurences = MainIndex.getOccurencesForWordID(matchedWord);
        this.type = type;
    }

    protected SearchResult(Collection<Occurence> occurences, SearchQueryType type) {
        this.occurences = occurences;
        this.type = type;
    }

    /**
     * Returns all occurences of this search result.
     *
     * @return all occurences of this search result.
     */
    public final Collection<Occurence> getOccurences() {
        if (occurences == null) {
            return new ArrayList<>();
        }
        return occurences;
    }

    public final SearchQueryType getType() {
        return type;
    }
    
    /**
     * 
     * @return the match length is the length of the matching string in words.
     */
    public abstract int getMatchLength();
}
