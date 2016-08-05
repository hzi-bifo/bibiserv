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
package de.unibi.cebitec.bibiserv.util.streamvalidate.impl;

import de.unibi.cebitec.bibiserv.sequence.parser.ForcedAbortOfPartValidation;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceML.SequenceMLSequenceToken;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceML.SequenceMLXMLParser;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import de.unibi.cebitec.bibiserv.sequence.parser.XMLParser;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;

import de.unibi.cebitec.bibiserv.util.streamvalidate.AbstractXMLStreamValidator;
import de.unibi.cebitec.bibiserv.util.streamvalidate.SequenceStreamValidator;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;


import java.util.regex.Pattern;

/**
 * Implementation of general SequenceML valdiator.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de Richard Madsack
 * (previous release)
 */
public class SequenceML_Validator extends AbstractXMLStreamValidator implements SequenceStreamValidator {

    public SequenceML_Validator() {
        super();
        strictness = STRICTNESS.ambiguous;
        cardinality = CARDINALITY.honeybadger;
    }

    @Override
    public String getXMLImplementation() {
        return "net.sourceforge.hobit.xsds._20090917.sequenceml";
    }

    @Override
    public ValidationResult validateThis(BufferedReader input, BufferedWriter output) {
        XMLParser parser;
        
        try {
            String tagType = SequenceMLSequenceToken.nucleicAcidSequence.getTagString();
            if(getContent()==CONTENT.AA) {
                tagType = SequenceMLSequenceToken.aminoAcidSequence.getTagString();
            }
            boolean multi = false;
            boolean single = false;
            switch(getCardinality()) {
                case multi:
                    multi = true;
                    break;
                case single:
                    single = true;
                    break;    
            } 
            
            parser = new SequenceMLXMLParser(input, output, getPatterntype(), tagType, multi, single);
            if(setMaxRead) {
                  parser.setMaximumCharsToValidate(maxRead);
            }
            parser.parseAndValidate();
    
        } catch (SequenceParserException ex) {
            return closeStreamsAndReturnValidationResult(input, output, false, ex.getMessage());
        } catch (ForcedAbortOfPartValidation ex) {
             return closeStreamsAndReturnValidationResult(input,output, true, "The first part of the was data correctly validated as " + getPatterntype().name() + " with pattern: " + getPatterntype().getPattern()+".");
        }
        return closeStreamsAndReturnValidationResult(input, output, true, "Data correctly validated as " + getPatterntype().name() + " with pattern: " + getPatterntype().getPattern(), parser.getWarnings());
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
