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
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.AbstractXMLValidator;
import de.unibi.cebitec.bibiserv.util.validate.JAXBManager;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator;

import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.hobit.xsds._20090917.sequenceml.SequenceML;
import net.sourceforge.hobit.xsds._20090917.sequenceml.SequenceType;

/**
 * Implementation of general SequenceML valdiator.
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *         Richard Madsack (previous release)
 */
public class SequenceML_Validator extends AbstractXMLValidator implements SequenceValidator {

    public SequenceML_Validator() {
        super();
        strictness = STRICTNESS.ambiguous;
        cardinality = CARDINALITY.honeybadger;
    }

      @Override
    public JAXBManager getXMLInstance() {
         
      return new JAXBManager("net.sourceforge.hobit.xsds._20090917.sequenceml");
        
    }

    @Override
    public ValidationResult validateThis(Object data) {
        // call super class to validate 
        ValidationResult vr = super.validateThis(data);
        // if still valid check if SequenceML object contains only one AA sequence
        if (vr.isValid()) {

            SequenceML sml = (SequenceML) getXMl().getJaxbRootElement();
            List<SequenceType> l_o_s = sml.getSequence();
            // test cardinality
            switch (cardinality) {
                case single:
                    if (l_o_s.size() != 1) {
                        return new ValidationResult(false, "Data is in SequenceML format but "
                                + "contains more or less than one sequences! Expected is one "
                                + "DNA sequence in SequenceML format!");
                    }
                    break;
                case multi:
                    if (l_o_s.size() < 2) {
                        return new ValidationResult(false, "Data is in SequenceML "
                                + "format but contains less than two sequences! "
                                + "Expected are two or mor sequences in SequenceML "
                                + "format!");
                    }
            }


            // now test if all contained sequences follows set content
            Matcher m;
            for (SequenceType st : l_o_s) {
                switch (content) {
                    case AA:
                        if (!st.isSetAminoAcidSequence()) {
                            return new ValidationResult(false, "Sequence with id "
                                    + st.getSeqID() + " contains no AminoAcid sequence data!");
                        }
                        // SequenceML is specfied for ambigious data, so we must 
                        // only check for strict data (if expected).
                        if (strictness.equals(STRICTNESS.strict)) {
                            m = getPattern().matcher(st.getAminoAcidSequence());
                            if (!m.find()) {
                                return new ValidationResult(false, "Sequence with id '"
                                        + st.getSeqID() + "' contains ambigious aminoacid sequence "
                                        + "data, but expected strict aminoacid data ( "
                                        + getPatterntype().getPattern() + ")!");
                            }
                        }
                        break;
                    case NA:
                        if (!st.isSetNucleicAcidSequence()) {
                            return new ValidationResult(false, "Sequence with id "
                                    + st.getSeqID() + " contains no NucleicAcid sequence data!");
                        }
                        // SequenceML is specified for ambigious data, see above
                        if (strictness.equals(STRICTNESS.strict)) {
                            m = getPattern().matcher(st.getNucleicAcidSequence());
                            if (!m.find()) {
                                return new ValidationResult(false, "Sequence with id '"
                                        + st.getSeqID() + "' contains ambigious nucleicacid sequence "
                                        + "data, but expected strict nucleicacid data ( "
                                        + getPatterntype().getPattern() + ")!");
                            }
                        }
                        break;

                    // RNA and DNA are both subsets of NA, so we have to check sequence content 
                    // wether it is strict or ambigious data
                    case RNA:
                        if (!st.isSetNucleicAcidSequence()) {
                            return new ValidationResult(false, "Sequence with id "
                                    + st.getSeqID() + " contains no NucleicAcid sequence data!");
                        }
                        m = getPattern().matcher(st.getNucleicAcidSequence());
                        if (!m.find()) {
                            return new ValidationResult(false, "Sequence with id '"
                                    + st.getSeqID() + "' contains  nucleicacid sequence "
                                    + "data, but expected " + strictness + " ribonucleicacid data ( "
                                    + getPatterntype().getPattern() + ")!");
                        }
                        break;
                    case DNA:
                        if (!st.isSetNucleicAcidSequence()) {
                            return new ValidationResult(false, "Sequence with id "
                                    + st.getSeqID() + " contains no NucleicAcid sequence data!");
                        }
                        m = getPattern().matcher(st.getNucleicAcidSequence());
                        if (!m.find()) {
                            return new ValidationResult(false, "Sequence with id '"
                                    + st.getSeqID() + "' contains  nucleicacid sequence "
                                    + "data, but expected " + strictness + " deoxyribonucleicacid data ( "
                                    + getPatterntype().getPattern() + ")!");
                        }
                        break;
                }
            }
        }

        return vr;
    }


    // ########################################## getter/setter ##########################################
    private STRICTNESS strictness;

    @Override
    public void setStrictness(STRICTNESS strictness) {
        this.strictness = strictness;
        patterntype = null;
        pattern = null;
    }

    @Override
    public STRICTNESS getStrictness() {
        return strictness;
    }
    private CARDINALITY cardinality;

    @Override
    public void setCardinality(CARDINALITY cardinality) {
        this.cardinality = cardinality;
    }

    @Override
    public CARDINALITY getCardinality() {
        return cardinality;
    }
    private CONTENT content;

    @Override
    public void setContent(CONTENT content) {
        this.content = content;
        pattern = null;
        patterntype = null;
    }

    @Override
    public CONTENT getContent() {
        return content;
    }
    // ############################## proteced ##############################
    protected PatternType patterntype;

    protected PatternType getPatterntype() {

        if (patterntype == null) {
            switch (content) {
                case NA:
                    if (strictness.equals(STRICTNESS.strict)) {
                        patterntype = PatternType.BT_nucleotideSequence;

                    } else {
                        patterntype = PatternType.BT_ambiguousNucleotideSequence;
                    }
                    break;
                case DNA:
                    if (strictness.equals(STRICTNESS.strict)) {
                        patterntype = PatternType.BT_dnaSequence;
                    } else {
                        patterntype = PatternType.BT_ambiguousDnaSequence;
                    }
                    break;
                case RNA:
                    if (strictness.equals(STRICTNESS.strict)) {
                        patterntype = PatternType.BT_rnaSequence;
                    } else {
                        patterntype = PatternType.BT_ambiguousRnaSequence;
                    }
                    break;
                case AA:
                    if (strictness.equals(STRICTNESS.strict)) {
                        patterntype = PatternType.BT_aminoAcidOneLetterSequence;
                    } else {
                        patterntype = PatternType.BT_ambiguousAminoAcidOneLetterSequence;
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
}
