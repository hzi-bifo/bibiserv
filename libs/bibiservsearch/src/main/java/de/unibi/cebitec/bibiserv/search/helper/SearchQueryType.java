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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This enum defines different types of search queries.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public enum SearchQueryType {

    /**
     * An exact matching query. There is no spell correction used and the search
     * engine will only accept results that contain the given words exactly as
     * they are given in the exact order as they are given.
     *
     * e.g. the query "holder Knabe" will only return those files that contain
     * the exact words "holder" and "Knabe" in that order.
     */
    EXACT(Pattern.compile("\"(.*)\"")),
    /**
     * A search query that will return any files that contain at least one word
     * that contains at least one of the words that are given in the query.
     *
     * e.g. the query !holder Knabe! will return all files that contain either
     * "holder" or "Knabe" (or both) as well as all files that contain words
     * like
     * "Wacholderbeere" (which contains "holder" as a substring).
     */
    WITHOUTCORRECTION(Pattern.compile("!(.*)!")),
    /**
     * A regular query will return all results for any of the given words (as
     * above)
     * but will also match words that are similar to the words of the query.
     * This similarity matching has the purpose to provide a basic spell
     * correction
     * mechanism for users that might have entered a word in a wrong way.
     *
     * For details, please look at the SuffixTreeMatcher class.
     */
    REGULAR(null);
    /**
     * The regex-pattern that matches queries of this type.
     */
    private final Pattern syntaxPattern;

    private SearchQueryType(Pattern syntaxPattern) {
        this.syntaxPattern = syntaxPattern;
    }

    /**
     * Tries to identify the given string as a query of this type and returns
     * the words that are the queries actual content.
     *
     * @param queryString the string the user has given as input.
     * @return the actual query string without meta-signs that mark the type.
     */
    protected String matchQuery(String queryString) {
        boolean matches;
        switch (this) {
            case REGULAR:
                matches = true;
                break;
            default:
                Matcher matcher = this.syntaxPattern.matcher(queryString);
                matches = matcher.matches();
                if (matches) {
                    queryString = matcher.group(1);
                }
        }
        if (matches) {
            return queryString;
        } else {
            return null;
        }
    }
}
