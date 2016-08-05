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
import java.util.ArrayList;
import java.util.Collection;

/**
 * This represents a character in a pattern that matches any input character.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class AnyPatternChar extends PatternChar {

    /**
     * Creates a new any pattern char.
     */
    public AnyPatternChar() {
        super(PatternCharType.ANY);
    }

    @Override
    public Tuple<Collection<SuffixTreeCharacter>, Collection<SuffixTreeCharacter>> getMatchingCharacters(Collection<? extends SuffixTreeCharacter> characters) {

        /**
         * for this character, all characters match, therefore we return a tuple
         * with all objects of the input in the first element.
         */
        Collection<SuffixTreeCharacter> first = (Collection<SuffixTreeCharacter>) characters;
        Collection<SuffixTreeCharacter> second = new ArrayList<>();
        return new Tuple<>(first, second);
    }

    @Override
    public int hashCode() {
        return PatternCharType.makeHashCodeUnique(PatternCharType.ANY, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AnyPatternChar other = (AnyPatternChar) obj;
        return true;
    }

    @Override
    public boolean matchSingleCharacter(SuffixTreeCharacter character) {
        return true;
    }

    @Override
    public String toString() {
        return ".";
    }

    @Override
    public boolean isSubset(PatternChar other) {
        //every character is a subset of an any char.
        return true;
    }
}
