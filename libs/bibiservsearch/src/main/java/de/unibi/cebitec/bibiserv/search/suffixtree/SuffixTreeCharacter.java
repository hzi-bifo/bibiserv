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

import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;

/**
 * A character that can be appended to a suffix tree.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeCharacter {

    private CharacterTypes type;
    private char actualChar;

    /**
     * Constructor for end.
     */
    private SuffixTreeCharacter() {
        type = CharacterTypes.END;
    }

    /**
     * Constructor for actual char values.
     *
     * @param actualChar the actual char.
     */
    private SuffixTreeCharacter(char actualChar) {
        this.type = CharacterTypes.ACTUALCHAR;
        this.actualChar = actualChar;
    }

    /**
     * @return true if this character marks the end of a word.
     */
    public boolean isEnd() {
        switch (type) {
            case END:
                return true;
            default:
                return false;
        }
    }

    /**
     *
     * @return the actual char if there is one.
     */
    public char getActualChar() throws SuffixTreeException {
        switch (type) {
            case ACTUALCHAR:
                return actualChar;
            default:
                throw new SuffixTreeException("INTERNAL ERROR: suffix tree end char "
                        + "was asked for char content!");
        }
    }

    /**
     *
     * @param character the character this character shall be compared to.
     * @return true if the content of this character is equal to the given char.
     */
    public boolean equalsChar(char character) {
        switch (type) {
            case ACTUALCHAR:
                return actualChar == character;
            default:
                return false;
        }
    }

    @Override
    public int hashCode() {
        switch (type) {
            case ACTUALCHAR:
                //assure that each regular char has an even hashcode
                return (int) actualChar * 2;
            case END:
                //take an uneven hashcode for end.
                return 97;
            default:
                //this can't happen.
                return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        SuffixTreeCharacter other = (SuffixTreeCharacter) obj;
        if (this.type != other.type) {
            return false;
        }
        switch (type) {
            case ACTUALCHAR:
                return actualChar == other.actualChar;
            default:
                return true;
        }
    }

    @Override
    public String toString() {
        switch (type) {
            case ACTUALCHAR:
                return Character.toString(actualChar);
            case END:
                return "$";
            default:
                //this can't happen.
                return null;
        }
    }

    /**
     * This method allows to transform a java string to an array of internal
     * suffix tree characters too append it to a suffix tree.
     *
     * @param word regular word as java String
     * @return internal representation of this word as
     * SuffixTreeCharacter-array.
     */
    public static SuffixTreeCharacter[] transformString(String word) {
        char[] chars = word.toUpperCase().toCharArray();
        SuffixTreeCharacter[] returnChars = new SuffixTreeCharacter[chars.length + 1];
        for (int charIndex = 0; charIndex < chars.length; charIndex++) {
            returnChars[charIndex] = new SuffixTreeCharacter(chars[charIndex]);
        }
        returnChars[chars.length] = new SuffixTreeCharacter();
        return returnChars;
    }

    /**
     * Transforms a java character to a SuffixTreeCharacter.
     *
     * @param character java primitive char.
     * @return SuffixTreeCharacter.
     */
    public static SuffixTreeCharacter transformChar(char character) {
        return new SuffixTreeCharacter(Character.toUpperCase(character));
    }

    /**
     *
     * @return the word end character ($).
     */
    public static SuffixTreeCharacter getEndChar() {
        return new SuffixTreeCharacter();
    }
}

enum CharacterTypes {

    ACTUALCHAR, END;
}