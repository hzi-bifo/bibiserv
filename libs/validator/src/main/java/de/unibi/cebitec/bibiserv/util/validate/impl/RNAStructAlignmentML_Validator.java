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

import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.STRICTNESS;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.AbstractXMLValidator;
import de.unibi.cebitec.bibiserv.util.validate.JAXBManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.RnastructAlignmentML;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.RnastructurealignmentWithProgramType;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.SequenceType;


/**
 * General RNAStructAlignemtML_Validator. Supports strict and ambigous RNA sequence data.
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de 
 *         
 */
public class RNAStructAlignmentML_Validator extends AbstractXMLValidator {

    @Override
    public JAXBManager getXMLInstance() {
        
      return new JAXBManager("net.sourceforge.hobit.xsds._20060515.rnastructalignmentml");
        
    }

    @Override
    public ValidationResult validateThis(Object data) {
        ValidationResult vr = super.validateThis(data);
        if (vr.isValid()) {
            RnastructAlignmentML raml = (RnastructAlignmentML) getXMl().getJaxbRootElement();

            for (RnastructurealignmentWithProgramType rwpt : raml.getRnastructurealignment()) {

                
                
                for (SequenceType st : rwpt.getSequence()) {
                    if (!st.isSetAlignedNucleicAcidSequence()) {
                        return new ValidationResult(false, "RNAStructML doesn't contain any ribonucleicacid sequence data (wether strict or ambigous)!");
                    }
                    Matcher m = getPattern().matcher(st.getAlignedNucleicAcidSequence().getValue());
                    if (!m.find()) {
                        return new ValidationResult(false, "Sequence with id '"
                                + st.getSeqID() + "' contains ribonucleicacid sequence "
                                + "data, but expected " + getStrictness() + " ribonucleicacid data ( "
                                + getPatterntype().getPattern() + ")!");
                    }
                }



            }
        }
        return vr;
    }

    public ValidationResult validateThis(Object data, STRICTNESS strictness) {
        return validateThis(data);
    }
    private STRICTNESS strictness;

    public STRICTNESS getStrictness() {
        return strictness;
    }

    public void setStrictness(STRICTNESS strictness) {
        this.strictness = strictness;
        patterntype = null;
        pattern = null;
    }
    // ############################## protected ##############################
    protected PatternType patterntype;

    protected PatternType getPatterntype() {
        if (patterntype == null) {
            if (getStrictness().equals(STRICTNESS.strict)) {
                patterntype = PatternType.BT_gappedRnaSequence;
            } else {
                patterntype = PatternType.BT_gappedAmbiguousRnaSequence;
            }
        }
        return patterntype;
    }
    protected Pattern pattern;

    protected Pattern getPattern() {
        if (pattern == null) {
            pattern = Pattern.compile(getPatterntype().getPattern());
        }
        return pattern;
    }
}
