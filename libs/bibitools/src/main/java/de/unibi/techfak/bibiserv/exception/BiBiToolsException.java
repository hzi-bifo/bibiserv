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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */ 

package de.unibi.techfak.bibiserv.exception;

import de.unibi.techfak.bibiserv.Status;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BiBiToolsException is thrown by the BiBiTool, if any exception occurred during
 * using the BiBiTools. The main reason for this exception is "laziness" :-)
 * Every Exception within the BiBiServ is handeled as BiBiToolException, the
 * (generated) source code
 *
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 */
public class BiBiToolsException extends Exception {

    private int faultcode;
    private String faultstring;

    /**
     * Empty Default Constructor
     */
    public BiBiToolsException() {
        super();
    }

    /**
     * Constructor
     *
     * @param faultstring - faultstring to be set, faultcode set to 700
     */
    public BiBiToolsException(String faultstring) {
        this.faultcode = 700;
        this.faultstring = faultstring;
    }

    /**
     * Constructor
     *
     * @param faultstring - faultstring to be set, faultcode set to 700
     * @param e - Throwable
     */
    public BiBiToolsException(String faultstring, Throwable e) {
        this.faultcode = 700;
        this.faultstring = faultstring;
        initCause(e);
    }

    /**
     * Constructor
     *
     * @param faultcode - faultcode to be set
     * @param faultstring - faultstring to be set
     */
    public BiBiToolsException(int faultcode, String faultstring) {
        this.faultcode = faultcode;
        this.faultstring = faultstring;
    }

    /**
     * Constructor
     *
     * @param faultcode - faultcode to be set
     * @param faultstring - faultstring to be set
     * @param e -  Throwable
     */
    public BiBiToolsException(int faultcode, String faultstring, Throwable e) {
        this.faultcode = faultcode;
        this.faultstring = faultstring;
        initCause(e);
    }

    /** Constructor
     *
     * @param status - status object, faultcode will be status.getStatuscode(), faultstring will be status.getDescription()
     */
    public BiBiToolsException(Status status) {
        try {
            this.faultcode = status.getStatuscode();
            this.faultstring = status.getDescription();
        } catch (DBConnectionException | IdNotFoundException ex) {
           this.faultcode = 722;
           this.faultstring = "Internal Resource Error";
        } 
    }

    @Override
    public String getMessage() {
        return "(" + faultcode + ") " + faultstring;
    }

    /**
     * Return the fault code of this exception.
     *
     * @return Return the fault code of this exception.
     */
    public int returnFaultCode() {
        return faultcode;
    }

    /**
     * Return the fault string of this exception.
     *
     * @return Return the fault description of this exception.
     */
    public String returnFaultString() {
        return faultstring;
    }
}
