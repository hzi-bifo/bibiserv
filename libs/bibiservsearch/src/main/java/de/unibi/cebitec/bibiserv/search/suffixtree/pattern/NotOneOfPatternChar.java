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
 * This represents a character in a pattern that matches every character but the
 * ones that are member of the given set.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class NotOneOfPatternChar extends PatternChar {

    private final HashSet<SuffixTreeCharacter> unallowedCharacters;

    /**
     * Create a new not one of char.
     *
     * @param unallowedCharacters the characters that are not matched by this
     * character.
     */
    public NotOneOfPatternChar(HashSet<SuffixTreeCharacter> unallowedCharacters) {
        super(PatternCharType.NOTONEOF);
        this.unallowedCharacters = unallowedCharacters;
    }

    /**
     *
     * @return a copy of the set of unallowed characters in this character.
     */
    public HashSet<SuffixTreeCharacter> getUnallowedCharacters() {
        HashSet<SuffixTreeCharacter> copy = new HashSet<>();
        copy.addAll(unallowedCharacters);
        return copy;
    }

    public NotOneOfPatternChar(HashSet<SuffixTreeCharacter> unallowedCharacters, PatternCharType type) {
        super(type);
        this.unallowedCharacters = unallowedCharacters;
    }

    @Override
    public Tuple<Collection<SuffixTreeCharacter>, Collection<SuffixTreeCharacter>> getMatchingCharacters(Collection<? extends SuffixTreeCharacter> characters) {
        /**
         * for this character, only unallowed characters do not match, therefore
         * we check which of those characters are contained in the input set and
         * return everything else as matches.
         */
        Collection<SuffixTreeCharacter> first = (Collection<SuffixTreeCharacter>) characters;
        Collection<SuffixTreeCharacter> second = new ArrayList<>();
        for (SuffixTreeCharacter allowedCharacter : unallowedCharacters) {
            if (first.remove(allowedCharacter)) {
                second.add(allowedCharacter);
            }
        }
        return new Tuple<>(first, second);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[^");
        for (SuffixTreeCharacter character : unallowedCharacters) {
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
        final NotOneOfPatternChar other = (NotOneOfPatternChar) obj;
        if (this.unallowedCharacters != other.unallowedCharacters && (this.unallowedCharacters == null || !this.unallowedCharacters.equals(other.unallowedCharacters))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.unallowedCharacters != null ? this.unallowedCharacters.hashCode() : 0);
        return PatternCharType.makeHashCodeUnique(PatternCharType.NOTONEOF, hash);
    }

    @Override
    public boolean matchSingleCharacter(SuffixTreeCharacter character) {
        return !unallowedCharacters.contains(character);
    }

    @Override
    public boolean isSubset(PatternChar other) {
        switch (other.getType()) {
            case EXACT:
                /*
                 * if the other character is an exact char, it is a subset if
                 * its content is not included in the list of unallowed characters.
                 */
                SuffixTreeCharacter otherChar = ((ExactPatternChar) other).getContent();
                return !unallowedCharacters.contains(otherChar);
            case ANY:
                //if the other character is an any char, this character can't be a subset.
                return false;
            case NOTONEOF:
                /*
                 * if the other character is a "not one of" character too it
                 * is a subset if this chars set of unallowed characters is a
                 * subset of the other ones set of unallowed characters.
                 */
                HashSet<SuffixTreeCharacter> otherUnallowedChars =
                        ((NotOneOfPatternChar) other).getUnallowedCharacters();
                return Collections.containsAll(otherUnallowedChars, unallowedCharacters);
            case ONEOF:
                /*
                 * if the other character is a "one of" character it is a subset
                 * if the set of unallowed characters of this character and the set
                 * of allowed characters of the other character are disjoint.
                 */
                HashSet<SuffixTreeCharacter> otherAllowedChars = ((OneOfPatternChar) other).getAllowedCharacters();
                return Collections.containsNone(unallowedCharacters, otherAllowedChars);
            default:
                throw new UnsupportedOperationException("Invalid input argument for subset:" + other);
        }
    }
}
