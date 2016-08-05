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
 * Contributor(s): Thomas Gatter
 *
 */
package de.unibi.cebitec.bibiserv.util.streamvalidate.impl;


import de.unibi.cebitec.bibiserv.sequence.parser.AlignmentML.AlignmentMLSequenceToken;
import de.unibi.cebitec.bibiserv.sequence.parser.AlignmentML.AlignmentMLXMLParser;
import de.unibi.cebitec.bibiserv.sequence.parser.ForcedAbortOfPartValidation;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import de.unibi.cebitec.bibiserv.sequence.parser.XMLParser;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;

import de.unibi.cebitec.bibiserv.util.streamvalidate.AbstractXMLStreamValidator;
import de.unibi.cebitec.bibiserv.util.streamvalidate.AlignmentStreamValidator;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.exception.UnsupportedCardinalityException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.regex.Pattern;

/**
 * Implementation of general AlignenmentML valdiator.
 * 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class AlignmentML_Validator extends AbstractXMLStreamValidator implements AlignmentStreamValidator {

    public AlignmentML_Validator() {
        super();
        cardinality = CARDINALITY.multi;
        strictness = STRICTNESS.ambiguous;
    }

    @Override
    public String getXMLImplementation() {
      return "net.sourceforge.hobit.xsds._20060602.alignmentml";
    }
    
    @Override
    public ValidationResult validateThis(BufferedReader input, BufferedWriter output) {
        XMLParser parser;
        
        try {
            String tagType = AlignmentMLSequenceToken.alignedNucleicAcidSequence.getTagString();
            if(getContent()==CONTENT.AA) {
                tagType = AlignmentMLSequenceToken.alignedAminoAcidSequence.getTagString();
            }
            
            parser = new AlignmentMLXMLParser(input, output, getPatterntype(), tagType);
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
    

    @Override
    public boolean isAlignment() {
        return true;
    }
    
    @Override
    public void setAlignment(boolean alignment){
       // Alignment must be an alignment ... 
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

    @Override
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

    @Override
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

    @Override
    public void setContent(CONTENT content) {
        this.content = content;
        patterntype = null;
        pattern = null;
    }


}
