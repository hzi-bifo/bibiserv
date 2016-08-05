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
import de.unibi.cebitec.bibiserv.search.util.Tuple;
import java.util.Collection;

/**
 * This is an internal class for pattern matching in suffix trees. Each
 * PatternChar
 * represents one character in a pattern.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public abstract class PatternChar {

    private final PatternCharType type;

    /**
     * Constructs a new pattern char.
     *
     * @param type the type of this pattern character.
     */
    public PatternChar(PatternCharType type) {
        this.type = type;
    }

    /**
     * @return this PatternChars type.
     */
    public PatternCharType getType() {
        return type;
    }

    /**
     * This method decided whether a given character matches this one.
     *
     * @param character a given character that might match this pattern
     * character.
     * @return true if it does match.
     */
    public abstract boolean matchSingleCharacter(SuffixTreeCharacter character);

    /**
     * This method chooses from a collection of characters every single one that
     * matches the pattern character and returns the matching characters as well
     * as the remaining characters.
     *
     * @param characters a collection of characters.
     * @return a tuple containing the collection of matching characters (as
     * first) and the collection of not matching characters (as second).
     */
    public abstract Tuple<Collection<SuffixTreeCharacter>, Collection<SuffixTreeCharacter>> getMatchingCharacters(
            Collection<? extends SuffixTreeCharacter> characters);

    /**
     * Analyzes if the given PatternChar is a subset of this one. e.g. if this
     * PatternChar is . (any) and the other char is an a, this would be true.
     *
     * @param other the other character that might be a subset of this one.
     * @return true if the given character is a subset of this one.
     */
    public abstract boolean isSubset(PatternChar other);

    /**
     * Tries to identify this patternChar or the other one as super set and
     * returns the super set.
     *
     * @param other another character that might be a sub- or a superset of this
     * one.
     * @return the superset or null of neither this nor the other character is a
     * subset of one another.
     */
    public PatternChar findSuperSet(PatternChar other) {
        if (isSubset(other)) {
            return this;
        }
        if (other.isSubset(this)) {
            return other;
        }
        return null;
    }
}
