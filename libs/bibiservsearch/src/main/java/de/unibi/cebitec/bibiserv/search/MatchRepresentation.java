/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Benjamin Paassen, CeBiTec,
 * http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contexts of this file are subject to the terms of the Common
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
package de.unibi.cebitec.bibiserv.search;

/**
 * This is an API class representing a match of the search pattern with a
 * single occurence.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class MatchRepresentation {

    /**
     * How many words should be displayed before the match begins?
     */
    public static final int PREFIXLENGTH = 3;
    /**
     * How many words should be displayed before the match ends?
     */
    public static final int SUFFIXLENGTH = 3;
    /**
     * This should contain some words that came before the match.
     */
    private String contextPrefix;
    /**
     * This is the actual matched context.
     */
    private String matchedContent;
    /**
     * This should contain some words that came after the match.
     */
    private String contextSuffix;

    /**
     *
     * @param contextPrefix This should contain some words that came before the
     * match.
     * @param matchedContent This is the actual matched context.
     * @param contextSuffix This should contain some words that came after the
     * match.
     */
    public MatchRepresentation(String contextPrefix,
            String matchedContent, String contextSuffix) {
        this.contextPrefix = contextPrefix;
        this.matchedContent = matchedContent;
        this.contextSuffix = contextSuffix;
    }

    public String getContextSuffix() {
        return contextSuffix;
    }

    public String getContextPrefix() {
        return contextPrefix;
    }

    public String getMatchedContent() {
        return matchedContent;
    }
}
