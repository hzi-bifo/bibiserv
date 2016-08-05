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
 * This exception is thrown whenever an invalid word is given as input.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class InvalidWordException extends Exception {

    /**
     * the word that was invalid.
     */
    private final String actualWord;

    public InvalidWordException(String actualWord) {
        super(createMessage(actualWord));
        this.actualWord = actualWord;
    }

    public InvalidWordException(String actualWord, Throwable cause) {
        super(createMessage(actualWord), cause);
        this.actualWord = actualWord;
    }

    public InvalidWordException(String actualWord, String message) {
        super(createMessage(actualWord) + " " + message);
        this.actualWord = actualWord;
    }

    public InvalidWordException(String actualWord, String message, Throwable cause) {
        super(createMessage(actualWord) + " " + message, cause);
        this.actualWord = actualWord;
    }

    private static String createMessage(String word) {
        return "String " + word + " could not be processed!";
    }

    /**
     *
     * @return the word that was invalid.
     */
    public String getActualWord() {
        return actualWord;
    }
}
