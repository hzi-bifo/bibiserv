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
package de.unibi.cebitec.bibiserv.util.streamvalidate.impl;



import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat;
import de.unibi.cebitec.bibiserv.util.streamvalidate.AbstractAlignmentOnlyValidator;
import de.unibi.cebitec.bibiserv.util.validate.exception.UnsupportedCardinalityException;

/**
 * General Implementation of a Clustal Validator. Clustal is an alignment only
 * format. Validator suppresses all sequence related features. 
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class Clustal_Validator extends AbstractAlignmentOnlyValidator {

    public Clustal_Validator() {
        super();
        super.setAlignment(true);
        super.setCardinality(CARDINALITY.multi);
        setStrictness(STRICTNESS.strict);
    }

    @Override
    public InputFormat getInputFormat() {
        return InputFormat.Clustal;
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
            throw new UnsupportedCardinalityException("Clustal is an alignment only format. Therefore any other cardinality than 'multi' aren't supported!");
        }
        super.setCardinality(getCardinality());
    }
}
