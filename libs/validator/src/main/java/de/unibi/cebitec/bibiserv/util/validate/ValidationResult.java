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
 * "Portions Copyrighted 2016 Jan Krueger"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.validate;

import java.util.ArrayList;
import java.util.List;

/**
 * ValidationResult class. A ValidationResult object contains information about
 * a previous validation process.
 *
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class ValidationResult {

    private boolean valid;
    private String message;
    private List<String> warnings;

    /**
     * Create a new ValidationResult object that contains information about
     * a validation process.
     *
     * @param valid - validation process result
     * @param message - validation process message
     */
    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
        this.warnings =  new ArrayList<>();
    }
    
    /**
     * Create a new ValidationResult object that contains information about
     * a validation process.
     * 
     * @param valid  validation process result
     * @param message validation process message
     * @param warnings validation process warnings 
     */
    public ValidationResult(boolean valid, String message, List<String> warnings){
        this.valid = valid;
        this.message = message;
        this.warnings =  warnings;
    }

    /**
     * Return an available message.
     *
     * @return Return an available message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Return the result of the validation process.
     *
     * @return Return the result of the validation process.
     */
    public boolean isValid() {
        return valid;
    }
    
    
    /**
     * Return the result of the
     * @return 
     */
    public List<String> getWarnings(){
        return warnings;
    }
}
