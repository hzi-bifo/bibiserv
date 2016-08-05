/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Sven Hartmeier
 *
 */ 

package de.unibi.cebitec.bibiserv.util.convert;

/**
 * ConversionException
 * 
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 */
public class ConversionException extends Exception {
    /**
     * Constructor, returns a new instance of ConversionException
     */
     public ConversionException() {
        super();
    }

     /**
      * Constructor, returns a new instance of ConversionException
      * @param message error message to be thrown
      */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * Constructor, returns a new instance of ConversionException
     * @param message error message to be thrown
     * @param ex previously thrown exception
     */
    public ConversionException(String message, Throwable ex) {
        super(message, ex);
    }
}
