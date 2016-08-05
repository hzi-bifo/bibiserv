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
package de.unibi.cebitec.bibiserv.search.util;

import java.util.ArrayList;

/**
 * This class preprocesses strings and gives back only word content.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class WordPreProcessor {

    /**
     * This defines all characters that are allowed as valid word content.
     */
    public static final String VALIDCHARS = "\\wäöüÄÖÜß";
    public static final String VALIDCHARREGEX = "[" + VALIDCHARS + "]";
    public static final String INVALIDCHARREGEX = "[^" + VALIDCHARS + "]";
    
    /**
     * preprocesses strings and gives back only word content meaning objects
     * matching the regular expression \\w+.
     *
     * @param string a raw string.
     * @return the pre processed words that were found in this line.
     */
    public static String[] preProcessString(String string) {
        //split the string at word borders.
        String[] split = string.split(INVALIDCHARREGEX);
        //then remove all empty strings.
        ArrayList<String> remainingWords = new ArrayList<>();
        for (String rawWord : split) {
            if (rawWord.matches(VALIDCHARREGEX + "+")) {
                remainingWords.add(rawWord.toUpperCase());
            }
        }
        return remainingWords.toArray(new String[remainingWords.size()]);
    }
}
