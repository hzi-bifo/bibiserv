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

import de.unibi.cebitec.bibiserv.search.MatchRepresentation;
import de.unibi.cebitec.bibiserv.search.OutputSearchResult;
import de.unibi.cebitec.bibiserv.search.index.BiBiServDocument;
import de.unibi.cebitec.bibiserv.search.index.DocumentID;
import de.unibi.cebitec.bibiserv.search.index.Occurence;
import de.unibi.cebitec.bibiserv.search.results.SearchResult;
import de.unibi.cebitec.bibiserv.search.util.TransformationAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

/**
 * Instances of this class are able to calculate a plausible score for ranking
 * search results in the result list.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SearchScore implements Comparable<SearchScore> {

    /**
     * The document that is scored.
     */
    private final DocumentID id;
    /**
     * The list of matches for this document.
     */
    private ArrayList<MatchRepresentation> matches = new ArrayList<>();
    /**
     * The sum of secondary scores of the stored matches.
     */
    private int secondaryScore;

    protected SearchScore(DocumentID id, Occurence firstOccurence,
            int matchLength, int secondaryScore) {
        this.id = id;
        this.secondaryScore = secondaryScore;
        addMatch(firstOccurence, matchLength);
    }

    /**
     *
     * @param match the occurence of the match.
     * @param matchLength the number of words that matched.
     */
    public final void addMatch(Occurence match, int matchLength) {
        Occurence currentOcc = match;
        //build the match prefix.
        StringBuilder prefixBuilder = new StringBuilder();
        {
            Stack<String> prefixStack = new Stack<>();
            for (int i = 0; i < MatchRepresentation.PREFIXLENGTH && currentOcc != null; i++) {
                currentOcc = currentOcc.getPrevious();
                if (currentOcc != null) {
                    //put the word on a stack because we need them in right order.
                    prefixStack.push(currentOcc.getWordID().getWordReference().getContent());
                }
            }
            //then append the stacks content to the builder.
            while (!prefixStack.empty()) {
                prefixBuilder.append(prefixStack.pop());
                prefixBuilder.append(' ');
            }
        }
        //build the actual match.
        currentOcc = match;
        StringBuilder matchBuilder = new StringBuilder();
        for (int i = 0; i < matchLength; i++) {
            matchBuilder.append(currentOcc.getWordID().getWordReference().getContent());
            matchBuilder.append(' ');
            currentOcc = currentOcc.getNext();
        }
        //build the suffix.
        StringBuilder suffixBuilder = new StringBuilder();
        for (int i = 0; i < MatchRepresentation.SUFFIXLENGTH && currentOcc != null; i++) {
            suffixBuilder.append(currentOcc.getWordID().getWordReference().getContent());
            suffixBuilder.append(' ');
            currentOcc = currentOcc.getNext();
        }
        //build the match representation.
        MatchRepresentation matchRep = new MatchRepresentation(
                getBuilderContent(prefixBuilder),
                getBuilderContent(matchBuilder),
                getBuilderContent(suffixBuilder));
        //add it to the list.
        matches.add(matchRep);
    }

    public ArrayList<MatchRepresentation> getMatches() {
        return matches;
    }

    /**
     *
     * @return substring of the string builder from 0 to length-1 or empty
     * string if the builder is empty.
     */
    private String getBuilderContent(StringBuilder builder) {
        int length = builder.length();
        if (length == 0) {
            return "";
        }
        return builder.substring(0, length - 1);
    }

    public void addSecondaryScore(int score) {
        secondaryScore += score;
    }

    public DocumentID getId() {
        return id;
    }

    @Override
    public int compareTo(SearchScore o) {
        if (this.matches.size() != o.matches.size()) {
            /* 
             * this is switched around, because higher score means less valuable 
             * search result, but more multiplicity is better.
             */
            return o.matches.size() - this.matches.size();
        }
        return this.secondaryScore - o.secondaryScore;
    }

    public static ArrayList<OutputSearchResult> calculateScoredResults(
            Collection<SearchResult> results) {
        /* 
         * calculate the score.
         */
        HashMap<DocumentID, SearchScore> scoreMap = new HashMap<>();
        SearchScore currentScore;
        int currentScoreValue;
        for (SearchResult result : results) {
            currentScoreValue = result.calculateScore();
            for (Occurence occ : result.getOccurences()) {
                currentScore = scoreMap.get(occ.getId());
                if (currentScore == null) {
                    //if the score wasn't found before, create a new object.
                    scoreMap.put(occ.getId(),
                            new SearchScore(occ.getId(), occ,
                            result.getMatchLength(), currentScoreValue));
                } else {
                    //if the score is found, just change it.
                    currentScore.addSecondaryScore(currentScoreValue);
                    currentScore.addMatch(occ, result.getMatchLength());
                }
            }
        }
        //create the sorted list.
        ArrayList<SearchScore> scoreList = new ArrayList<>(scoreMap.values());
        Collections.sort(scoreList);
        // ... and return it.
        return de.unibi.cebitec.bibiserv.search.util.Collections.transform(
                scoreList, new SearchResultTransformation());
    }
}

class SearchResultTransformation implements
        TransformationAction<SearchScore, OutputSearchResult> {

    @Override
    public OutputSearchResult doTransformation(SearchScore input) {
        DocumentID id = input.getId();
        BiBiServDocument doc = BiBiServDocument.getDocumentByID(id);
        String ident = doc.getIdentifier();
        return new OutputSearchResult(ident, input.getMatches());
    }
}
