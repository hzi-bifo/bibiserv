/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de "
 *
 * Contributor(s): Thomas Gatter
 *
 */
package de.unibi.cebitec.bibiserv.util.streamvalidate;

import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.Validator;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * An extension of the ordinary validator interface containing additional functions
 * for stream validation.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public interface StreamValidator extends Validator{
    
    ValidationResult validateThis(BufferedReader input, BufferedWriter output);
    
    /**
     * Validates only the first X chars.
     * Validation ends exactly when the char X was read and NOT when it was validated.
     * Since only new data is read when the previous chars are validated this
     * means that always about X-Y chars are indeed validated and Y much smaller
     * X. WARNING: The exact amount of read chars is not deterministic, but
     * rather depends on the availability in the stream and the data itself.
     * @param maxRead number of parts X
     * @param The Validationobject. This is needed to close the connection. Closing the stream forces amazon to read to till the end of the stream. 
     * @return 
     */
    ValidationResult validateThis(Object o, int maxRead, StreamConnectionInterface connection);
    
    /**
     * All Stream validators try to correct common formatting errors and with this
     * reduce the number of possible outputs.
     * If the function  ValidationResult validateThis(Object o) was used for validation,
     * this will return the unified String.
     * If the function ValidationResult validateThis(BufferedReader input, BufferedWriter output)
     * was used for validation this will end with an error message.
     * @return The unified corrected String.
     * @throws ValidationException if called before validation or if validation was called with 
     *          ValidationResult validateThis(BufferedReader input, BufferedWriter output).
     */
    String getRepairedAndUnifiedOutput() throws ValidationException;
    
}
