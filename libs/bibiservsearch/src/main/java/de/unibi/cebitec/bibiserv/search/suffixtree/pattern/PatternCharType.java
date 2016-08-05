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

/**
 * These are the different possible types a character in a pattern can have.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public enum PatternCharType {

    /**
     * The end of a search pattern.
     */
    END,
    /**
     * Wildcard.
     */
    ANY,
    /**
     * One of the given characters.
     */
    ONEOF,
    /**
     * Not one of the given characters.
     */
    NOTONEOF,
    /**
     * The exact given character.
     */
    EXACT;

    /**
     * Ensures that the given hash code can only be equal to hash codes of the
     * same type.
     *
     * @param type a PatternCharType.
     * @param rawHashCode the raw hash code.
     * @return unique hash code.
     */
    protected static int makeHashCodeUnique(PatternCharType type, int rawHashCode) {
        return rawHashCode * PatternCharType.values().length + type.ordinal();
    }
}
