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
import de.unibi.cebitec.bibiserv.util.validate.AbstractXMLValidator;
import de.unibi.cebitec.bibiserv.util.validate.AlignmentValidator;
import de.unibi.cebitec.bibiserv.util.validate.JAXBManager;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.exception.UnsupportedCardinalityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.hobit.xsds._20060602.alignmentml.AlignmentML;
import net.sourceforge.hobit.xsds._20060602.alignmentml.AlignmentWithProgramType;
import net.sourceforge.hobit.xsds._20060602.alignmentml.SequenceType;

/**
 * Implementation of general AlignenmentML valdiator.
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *         Richard Madsack (previous release)
 */
public class AlignmentML_Validator extends AbstractXMLValidator implements AlignmentValidator {

    public AlignmentML_Validator() {
        super();
        cardinality = CARDINALITY.multi;
        strictness = STRICTNESS.ambiguous;
    }

    @Override
    public JAXBManager getXMLInstance() {
        
      return new JAXBManager("net.sourceforge.hobit.xsds._20060602.alignmentml");
        
    }

    @Override
    public boolean isAlignment() {
        return true;
    }
    
    @Override
    public void setAlignment(boolean alignment){
       // Alignment must be an alignment ... 
    }

    @Override
    public ValidationResult validateThis(Object data) {
        ValidationResult vr = super.validateThis(data);
        // if still valid check if SequenceML object contains only one AA sequence
        if (vr.isValid()) {

            AlignmentML aml = (AlignmentML) getXMl().getJaxbRootElement();


            // test if all contained sequences follows set content
            Matcher m;


            for (AlignmentWithProgramType awpt : aml.getAlignment()) {
                for (SequenceType st : awpt.getSequence()) {
                    switch (getContent()) {
                        case AA:
                            if (!st.isSetAlignedAminoAcidSequence()) {
                                return new ValidationResult(false, "Sequence with id "
                                        + st.getSeqID() + " contains no aligned aminoacid sequence data!");
                            }
                            // SequenceML is specfied for ambigious data, so we must 
                            // only check for strict data (if expected).
                            if (getStrictness().equals(STRICTNESS.strict)) {
                                m = getPattern().matcher(st.getAlignedAminoAcidSequence().getValue());
                                if (!m.find()) {
                                    return new ValidationResult(false, "Sequence with id '"
                                            + st.getSeqID() + "' contains ambigious aligned aminoacid sequence "
                                            + "data, but expected strict aligned aminoacid data ( "
                                            + getPatterntype().getPattern() + ")!");
                                }
                            }
                            break;
                        case NA:
                            if (!st.isSetAlignedNucleicAcidSequence()) {
                                return new ValidationResult(false, "Sequence with id "
                                        + st.getSeqID() + " contains no aligned nucleicacid sequence data!");
                            }
                            // SequenceML is specified for ambigious data, see above
                            if (getStrictness().equals(STRICTNESS.strict)) {
                                m = getPattern().matcher(st.getAlignedNucleicAcidSequence().getValue());
                                if (!m.find()) {
                                    return new ValidationResult(false, "Sequence with id '"
                                            + st.getSeqID() + "' contains aligned ambigious nucleicacid sequence "
                                            + "data, but expected aligned strict nucleicacid data ( "
                                            + getPatterntype().getPattern() + ")!");
                                }
                            }
                            break;

                        // RNA and DNA are both subsets of NA, so we have to check sequence content 
                        // wether it is strict or ambigious data
                        case RNA:
                            if (!st.isSetAlignedNucleicAcidSequence()) {
                                return new ValidationResult(false, "Sequence with id "
                                        + st.getSeqID() + " contains no aligned nucleicacid sequence data!");
                            }
                            m = getPattern().matcher(st.getAlignedNucleicAcidSequence().getValue());
                            if (!m.find()) {
                                return new ValidationResult(false, "Sequence with id '"
                                        + st.getSeqID() + "' contains  aligned nucleicacid sequence "
                                        + "data, but expected " + getStrictness() + " aligned ribonucleicacid data ( "
                                        + getPatterntype().getPattern() + ")!");
                            }
                            break;
                        case DNA:
                            if (!st.isSetAlignedNucleicAcidSequence()) {
                                return new ValidationResult(false, "Sequence with id "
                                        + st.getSeqID() + " contains no aligned nucleicacid sequence data!");
                            }
                            m = getPattern().matcher(st.getAlignedNucleicAcidSequence().getValue());
                            if (!m.find()) {
                                return new ValidationResult(false, "Sequence with id '"
                                        + st.getSeqID() + "' contains aligned nucleicacid sequence "
                                        + "data, but expected " + getStrictness() + " aligned deoxyribonucleicacid data ( "
                                        + getPatterntype().getPattern() + ")!");
                            }
                            break;
                    }
                }
            }
        }

        return vr;
    }

    // ############################## proteced ##############################
    protected PatternType patterntype;

    protected PatternType getPatterntype() {

        if (patterntype == null) {
            switch (getContent()) {
                case NA:
                    if (getStrictness().equals(STRICTNESS.strict)) {
                        patterntype = PatternType.BT_gappedNucleotideSequence;

                    } else {
                        patterntype = PatternType.BT_gappedAmbiguousNucleotideSequence;
                    }
                    break;
                case DNA:
                    if (getStrictness().equals(STRICTNESS.strict)) {
                        patterntype = PatternType.BT_gappedDnaSequence;
                    } else {
                        patterntype = PatternType.BT_gappedAmbiguousDnaSequence;
                    }
                    break;
                case RNA:
                    if (getStrictness().equals(STRICTNESS.strict)) {
                        patterntype = PatternType.BT_gappedRnaSequence;
                    } else {
                        patterntype = PatternType.BT_gappedAmbiguousRnaSequence;
                    }
                    break;
                case AA:
                    if (getStrictness().equals(STRICTNESS.strict)) {
                        patterntype = PatternType.BT_gappedAminoAcidOneLetterSequence;
                    } else {
                        patterntype = PatternType.BT_gappedAmbiguousAminoAcidOneLetterSequence;
                    }
                    break;
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
    // ############################## getter/setter ##############################
    STRICTNESS strictness;

    @Override
    public STRICTNESS getStrictness() {
        return strictness;
    }

    public void setStrictness(STRICTNESS strictness) {
        this.strictness = strictness;
        patterntype = null;
        pattern = null;
    }
    CARDINALITY cardinality;

    @Override
    public CARDINALITY getCardinality() {
        return cardinality;
    }

    public void setCardinality(CARDINALITY cardinality) {
        if (!cardinality.equals(CARDINALITY.multi)) {
            throw new UnsupportedCardinalityException("AlignmentML_Validator is an alignment only validator, therfore only 'multi' is supported as cardinality value!");
        }
        this.cardinality = cardinality;

    }
    CONTENT content;

    @Override
    public CONTENT getContent() {
        return content;
    }

    public void setContent(CONTENT content) {
        this.content = content;
        patterntype = null;
        pattern = null;
    }
}
