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

import de.unibi.cebitec.bibiserv.search.exceptions.InvalidSearchQueryException;
import de.unibi.cebitec.bibiserv.search.exceptions.InvalidWordException;
import de.unibi.cebitec.bibiserv.search.util.WordPreProcessor;
import java.util.ArrayList;

/**
 * This class represents a parsed search query.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SearchQuery {

    /**
     * These are the characters that are considered valid word content in a part
     * word or inexact search.
     */
    private static final String DEFAULTVALIDCONTENT =
            WordPreProcessor.VALIDCHARS + "\"\\.*\\[\\]\\^\\\\";
    /**
     * The words that shall be searched for.
     */
    private final String[] words;
    /**
     * This queries type.
     */
    private final SearchQueryType type;

    private SearchQuery(String[] words, SearchQueryType type) {
        this.words = words;
        this.type = type;
    }

    /**
     *
     * @return The words that shall be searched for.
     */
    public String[] getWords() {
        return words;
    }

    /**
     *
     * @return This queries type.
     */
    public SearchQueryType getType() {
        return type;
    }

    /**
     * This method takes a query string as given by the user and transforms it
     * into a SearchQuery instance.
     *
     * @param queryString the string the user has given as input.
     * @return a SearchQuery instance for this particular query.
     */
    public static SearchQuery parseQuery(String queryString)
            throws InvalidWordException {
        String parsedQueryString;
        for (SearchQueryType possibleType : SearchQueryType.values()) {
            //try to identify the queries type and return the respective query object.
            if ((parsedQueryString = possibleType.matchQuery(queryString)) != null) {
                switch (possibleType) {
                    case EXACT:
                        /* 
                         * if we search exactly, split at word borders and use
                         * regular word pre processing.
                         */
                        return new SearchQuery(
                                WordPreProcessor.preProcessString(parsedQueryString),
                                possibleType);
                    default:
                        /*
                         * otherwise split at characters that are neither a word
                         * char nor special characters for regexes.
                         */
                        String[] split = parsedQueryString.split("[^"
                                + DEFAULTVALIDCONTENT + "]");
                        ArrayList<String> finalWords = new ArrayList<>();
                        for (int i = 0; i < split.length; i++) {
                            if (split[i].matches("[" + DEFAULTVALIDCONTENT + "]+")) {
                                //only use words that consist of valid content.
                                finalWords.add(split[i].toUpperCase());
                            }
                        }
                        return new SearchQuery(finalWords.toArray(
                                new String[finalWords.size()]), possibleType);
                }
            }
        }
        /* 
         * if we got here the query could not be identified and an exception
         * has to be thrown.
         */
        throw new InvalidSearchQueryException(queryString,
                "Type could not be identified.");
    }
}
