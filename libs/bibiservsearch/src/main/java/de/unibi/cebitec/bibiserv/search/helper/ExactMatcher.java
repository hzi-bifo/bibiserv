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
package de.unibi.cebitec.bibiserv.search.helper;

import de.unibi.cebitec.bibiserv.search.index.MainIndex;
import de.unibi.cebitec.bibiserv.search.index.Occurence;
import de.unibi.cebitec.bibiserv.search.index.WordID;
import de.unibi.cebitec.bibiserv.search.index.WordIndex;
import de.unibi.cebitec.bibiserv.search.results.ExactSearchResult;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class implements matching for exact search queries.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class ExactMatcher {

    /**
     * The words that shall be found in that exact order.
     */
    String[] words;

    /**
     *
     * @param words The words that shall be found in that exact order.
     */
    public ExactMatcher(String[] words) {
        this.words = words;
    }

    /**
     * Starts the matching process.
     *
     * @return an exact search result for this query.
     */
    public ExactSearchResult getMatches() {
        //get all word ids.
        WordID[] queryIDs = new WordID[words.length];
        /* 
         * retrieve index of the longest word. This is done because
         * longer words have far less occurences and therefore the
         * following algorithm works much faster.
         */
        int startIndex = 0;
        int maxLength = 0;
        for (int wordIndex = 0; wordIndex < queryIDs.length; wordIndex++) {
            String currentWord = words[wordIndex];
            queryIDs[wordIndex] = WordIndex.getID(currentWord);
            if (queryIDs[wordIndex] == null) {
                /* 
                 * if a word is not indexed at all there is no result 
                 * and we can return an empty set immediatly.
                 */
                return new ExactSearchResult(queryIDs.length, new ArrayList<Occurence>());
            } else {
                //if it is indexed, look for the longest word.
                if (currentWord.length() > maxLength) {
                    startIndex = wordIndex;
                    maxLength = currentWord.length();
                }
            }
        }
        //start with the set of occurences for the longest word.
        Collection<Occurence> occurences =
                MainIndex.getOccurencesForWordID(queryIDs[startIndex]);
        ArrayList<Occurence> resultOccs = new ArrayList<>();
        Occurence currentOcc;
        Occurence startOcc = null;
        /* 
         * then check for all occurences if the previous and the following ones
         * match the query.
         */
        for (Occurence occ : occurences) {
            boolean matches = true;
            //first check backwards.
            if (startIndex > 0) {
                currentOcc = occ;
                for (int wordIndex = startIndex; wordIndex > 0; wordIndex--) {
                    currentOcc = currentOcc.getPrevious();
                    if (currentOcc != null
                            && currentOcc.getWordID().equals(queryIDs[wordIndex - 1])) {
                        if (wordIndex == 1) {
                            //get the start occurence.
                            startOcc = currentOcc;
                        }
                    } else {
                        //if it does not match (or is null) stop matching.
                        matches = false;
                        break;
                    }
                }
            } else {
                startOcc = occ;
            }
            /* 
             * if it is already clear that it does not match, go to the next
             * occurence.
             */
            if (!matches) {
                continue;
            }
            //if backwards matching worked, start forward matching.
            if (startIndex < queryIDs.length - 1) {
                currentOcc = occ;
                for (int wordIndex = startIndex; wordIndex < queryIDs.length - 1; wordIndex++) {
                    currentOcc = currentOcc.getNext();
                    if (currentOcc == null
                            || !currentOcc.getWordID().equals(queryIDs[wordIndex + 1])) {
                        //if it does not match (or is null) stop matching.
                        matches = false;
                        break;
                    }
                }
            }
            if (matches) {
                /* 
                 * if forward matching worked too we get here and append the start
                 * occurence to the result list.
                 */
                resultOccs.add(startOcc);
            }
        }
        return new ExactSearchResult(queryIDs.length, resultOccs);
    }
}