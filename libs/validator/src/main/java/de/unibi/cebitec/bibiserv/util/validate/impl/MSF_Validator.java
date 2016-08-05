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
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.validate.impl;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat;
import de.unibi.cebitec.bibiserv.util.validate.AbstractAlignmentValidator;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CARDINALITY;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.STRICTNESS;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.exception.UnsupportedCardinalityException;

/**
 * Abstract Implementation of a MSF Validator. MSF is an alignment only
 * format. Validator suppresses all sequence related features. 
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class MSF_Validator extends AbstractAlignmentValidator {

    public MSF_Validator() {
        super();
        super.setAlignment(true);
        super.setCardinality(CARDINALITY.multi);
        setStrictness(STRICTNESS.strict);
    } 

    @Override
    public ValidationResult doFurtherValidationSteps(ValidationResult vr) {
//        switch (getInputFormat()) {
//            case MSF_PileUp:
//                if (!getInput().trim().startsWith("PileUp")) {
//                    vr = new ValidationResult(false, "Data is not in MSF/PileUp format.");
//                }
//                break;
//            case MSF_Rich:
//                if (!getInput().trim().startsWith("!!")) {
//                    vr = new ValidationResult(false, "Data is not in MSF/Rich Sequence format.");
//                }
//                break;
//            case MSF_Plain:
//                if (!getInput().trim().startsWith("MSF")) {
//                    vr = new ValidationResult(false, "Data is not in MSF/Plain format.");
//                }
//                break;
//        }
        return vr;
    }

    @Override
    public InputFormat getInputFormat() {
        return InputFormat.MSF; // MSF Validator reads all MSF formats wether they are of plain, rich, pileup subtype
    }

    @Override
    public void setAlignment(boolean alignment) {
        super.setAlignment(isAlignment());
    }

    @Override
    public boolean isAlignment() {
        return true;
    }

    @Override
    public void setCardinality(CARDINALITY cardinality) {
        if (!cardinality.equals(CARDINALITY.multi)) {
            throw new UnsupportedCardinalityException("MSF is an alignment only format. Therefore any other cardinality than 'multi' aren't supported!");
        }
        super.setCardinality(getCardinality());
    }
}
