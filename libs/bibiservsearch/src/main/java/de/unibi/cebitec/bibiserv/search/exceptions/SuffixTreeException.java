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
package de.unibi.cebitec.bibiserv.search.exceptions;

/**
 * This exception is thrown if malformed suffix trees occur or if there are
 * exceptions during suffix tree construction.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeException extends Exception {

    public SuffixTreeException(String message) {
        super(message);
    }

    public SuffixTreeException(Throwable cause) {
        super(cause);
    }

    public SuffixTreeException(String message, Throwable cause) {
        super(message, cause);
    }
}
