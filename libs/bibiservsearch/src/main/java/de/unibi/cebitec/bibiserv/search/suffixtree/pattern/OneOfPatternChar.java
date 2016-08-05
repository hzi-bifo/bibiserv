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
package de.unibi.cebitec.bibiserv.search.suffixtree.pattern;

import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeCharacter;
import de.unibi.cebitec.bibiserv.search.util.Collections;
import de.unibi.cebitec.bibiserv.search.util.Tuple;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * This represents a character in a pattern that matches every character that is
 * member of a given set.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class OneOfPatternChar extends PatternChar {

    /**
     * Set of characters that are allowed at the place of this character.
     */
    private final HashSet<SuffixTreeCharacter> allowedCharacters;

    /**
     * Create a new one of char.
     *
     * @param allowedCharacters the characters that are matched by this
     * character.
     */
    public OneOfPatternChar(HashSet<SuffixTreeCharacter> allowedCharacters) {
        super(PatternCharType.ONEOF);
        this.allowedCharacters = allowedCharacters;
    }

    /**
     *
     * @return a copy of the set of allowed characters in this character.
     */
    public HashSet<SuffixTreeCharacter> getAllowedCharacters() {
        HashSet<SuffixTreeCharacter> copy = new HashSet<>();
        copy.addAll(allowedCharacters);
        return copy;
    }

    @Override
    public Tuple<Collection<SuffixTreeCharacter>, Collection<SuffixTreeCharacter>> getMatchingCharacters(Collection<? extends SuffixTreeCharacter> characters) {
        /**
         * for this character, only allowed characters match, therefore we
         * check which of those characters are contained in the input set and
         * return those as matches.
         */
        Collection<SuffixTreeCharacter> first = new ArrayList<>();
        Collection<SuffixTreeCharacter> second = (Collection<SuffixTreeCharacter>) characters;
        for (SuffixTreeCharacter allowedCharacter : allowedCharacters) {
            if (second.remove(allowedCharacter)) {
                first.add(allowedCharacter);
            }
        }
        return new Tuple<>(first, second);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (SuffixTreeCharacter character : allowedCharacters) {
            builder.append(character.toString());
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OneOfPatternChar other = (OneOfPatternChar) obj;
        if (this.allowedCharacters != other.allowedCharacters && (this.allowedCharacters == null || !this.allowedCharacters.equals(other.allowedCharacters))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.allowedCharacters != null ? this.allowedCharacters.hashCode() : 0);
        return PatternCharType.makeHashCodeUnique(PatternCharType.ONEOF, hash);
    }

    @Override
    public boolean matchSingleCharacter(SuffixTreeCharacter character) {
        return allowedCharacters.contains(character);
    }

    @Override
    public boolean isSubset(PatternChar other) {
        switch (other.getType()) {
            case EXACT:
                /*
                 * if the other character is an exact char, it is a subset if
                 * its content is included in the list of allowed characters.
                 */
                SuffixTreeCharacter otherChar = ((ExactPatternChar) other).getContent();
                return allowedCharacters.contains(otherChar);
            case ANY:
                //if the other character is an any char, this character can't be a subset.
                return false;
            case NOTONEOF:
                /*
                 * Theoretically a "not one of" sequence could be a subset of
                 * a "one of" sequence (if all characters of the alphabet would
                 * not be allowed execept some of those that are allowed by
                 * this "one of" sequence) but there are no application scenarios
                 * where a user is likely to do so. Therefore we will ignore
                 * that possibility and return false.
                 */
                return false;
            case ONEOF:
                /*
                 * if the other character is a "one of" character too it is a subset
                 * if the set of allowed characters of the other character is
                 * a subset of this characters set of allowed characters.
                 */
                HashSet<SuffixTreeCharacter> otherAllowedChars = ((OneOfPatternChar) other).getAllowedCharacters();
                return Collections.containsAll(allowedCharacters, otherAllowedChars);
            default:
                throw new UnsupportedOperationException("Invalid input argument for subset:" + other);
        }
    }
}
