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
 * This exception is thrown if the user input for a search query cannot be
 * parsed.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class InvalidSearchQueryException extends InvalidWordException {

    public InvalidSearchQueryException(String actualWord) {
        super(actualWord);
    }

    public InvalidSearchQueryException(String actualWord, Throwable cause) {
        super(actualWord, cause);
    }

    public InvalidSearchQueryException(String actualWord, String message) {
        super(actualWord, message);
    }

    public InvalidSearchQueryException(String actualWord, String message, Throwable cause) {
        super(actualWord, message, cause);
    }
}
