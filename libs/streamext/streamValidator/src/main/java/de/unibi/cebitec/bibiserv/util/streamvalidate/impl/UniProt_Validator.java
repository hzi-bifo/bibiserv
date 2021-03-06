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
import de.unibi.cebitec.bibiserv.util.streamvalidate.AbstractSequenceValidator;
import de.unibi.cebitec.bibiserv.util.validate.exception.UnsupportedContentException;

/**
 * General Implementation of a UNIPROT Validator. Assumes "honeybadger (not specified)" as  
 * default for cardinality, "ambigious" as default for strictness.
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class UniProt_Validator extends AbstractSequenceValidator {

    public UniProt_Validator() {
        super();
        setCardinality(CARDINALITY.honeybadger);
        setStrictness(STRICTNESS.ambiguous);
        super.setContent(CONTENT.AA);
    }


    @Override
    public void setContent(CONTENT content) {
        if (!content.equals(CONTENT.AA)) {
            throw new UnsupportedContentException("SWISPPROT format/container supports only AminoAcid sequence data!");
        }
        super.setContent(content);
    }

    @Override
    public InputFormat getInputFormat() {
        return InputFormat.UniProt;
    }
}
