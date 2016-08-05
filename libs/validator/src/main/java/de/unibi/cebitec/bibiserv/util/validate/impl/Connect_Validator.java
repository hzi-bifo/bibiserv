/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.validate.impl;

import de.unibi.cebitec.bibiserv.sequenceparser.parser.impl.ConnectParser;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.Validator;

/**
 * Validator for .ct-format. This validator is able to validate single as well
 * as multiple input .ct structures in one single file.
 *
 * @author Benjamin Paassen - bpaassen(at)cebitec.uni-bielefeld.de
 */
public class Connect_Validator implements Validator {

    @Override
    public ValidationResult validateThis(Object data) {
        String input;
        ValidationResult vr;
        //get string content.
        try {
            input = (String) data;
            ConnectParser parser = new ConnectParser(input, true);
            parser.parseData();
            vr = new ValidationResult(parser.isValid(), parser.getValidationMessage());
        } catch (ClassCastException ex) {
            vr = new ValidationResult(false,
                    "Input could not be cast to correct type 'java.lang.String'");
        }

        return vr;
    }
}
