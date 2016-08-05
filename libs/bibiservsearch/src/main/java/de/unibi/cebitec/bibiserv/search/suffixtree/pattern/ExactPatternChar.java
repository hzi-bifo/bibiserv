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
 * This class represents a character in a pattern that matches exactly one
 * character.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class ExactPatternChar extends PatternChar {

    private final SuffixTreeCharacter content;

    public ExactPatternChar(SuffixTreeCharacter content) {
        super(PatternCharType.EXACT);
        this.content = content;
    }

    public SuffixTreeCharacter getContent() {
        return content;
    }

    @Override
    public Tuple<Collection<SuffixTreeCharacter>, Collection<SuffixTreeCharacter>> getMatchingCharacters(Collection<? extends SuffixTreeCharacter> characters) {
        /**
         * for this character, only the exact content matches, therefore we
         * check if the exact content is contained in the input set and it as
         * matching.
         */
        Collection<SuffixTreeCharacter> first = new ArrayList<>();
        Collection<SuffixTreeCharacter> second = (Collection<SuffixTreeCharacter>) characters;
        if (second.remove(content)) {
            first.add(content);
        }
        return new Tuple<>(first, second);
    }

    @Override
    public int hashCode() {
        return PatternCharType.makeHashCodeUnique(PatternCharType.EXACT, content.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExactPatternChar other = (ExactPatternChar) obj;
        if (this.content != other.content && (this.content == null || !this.content.equals(other.content))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return content.toString();
    }

    @Override
    public boolean matchSingleCharacter(SuffixTreeCharacter character) {
        return character.equals(content);
    }

    @Override
    public boolean isSubset(PatternChar other) {
        //only an equal char is a subset of an exact pattern char.
        return this.equals(other);
    }
}
