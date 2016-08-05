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
 * This class marks the end of a search pattern.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class PatternEndChar extends PatternChar {

    public PatternEndChar() {
        super(PatternCharType.END);
    }

    @Override
    public int hashCode() {
        return PatternCharType.makeHashCodeUnique(PatternCharType.END, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ".*";
    }

    @Override
    public Tuple<Collection<SuffixTreeCharacter>, Collection<SuffixTreeCharacter>> getMatchingCharacters(Collection<? extends SuffixTreeCharacter> characters) {
        throw new UnsupportedOperationException("Pattern End Char was asked for matching characters. Pattern End Chars have no matching characters.");
    }

    @Override
    public boolean matchSingleCharacter(SuffixTreeCharacter character) {
        throw new UnsupportedOperationException("Pattern End Char was asked for matching characters. Pattern End Chars have no matching characters.");
    }

    @Override
    public boolean isSubset(PatternChar other) {
        throw new UnsupportedOperationException("Pattern end char was asked for subsets.");
    }
}
