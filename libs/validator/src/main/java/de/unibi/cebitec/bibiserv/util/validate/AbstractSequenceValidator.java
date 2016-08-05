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
 * "Portions Copyrighted 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Richard Madsack, Madis Rumming, Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.validate;

import de.unibi.cebitec.bibiserv.sequenceparser.parser.exception.ParserException;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputReader;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.exception.ParserToolException;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.GeneralSequenceType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SequenceCollection;

import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CARDINALITY;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.STRICTNESS;
import java.io.IOException;
import java.io.StringReader;

/**
 * AbstractSequenceValidator implements SequenceValidator Interface 
 * 
 * @author Richard Madsack, Madis Rumming, Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de 
 */
public abstract class AbstractSequenceValidator implements SequenceValidator {

    // the input as String
    private String input = null;
    private InputReader inputReader;

    public AbstractSequenceValidator() {
        inputReader = new InputReader();
    }

    @Override
    public ValidationResult validateThis(Object data) {
        ValidationResult vr = standardSequenceValidationSteps(data);
        if (vr.isValid()) {
            vr = doFurtherValidationSteps(vr);
        }
        return vr;
    }

    protected ValidationResult standardSequenceValidationSteps(Object data) {
        try {
            input = (String) data;
        } catch (ClassCastException ex) {
            return new ValidationResult(false, "Data could not be cast to correct type 'java.lang.String'. Exception was: " + ex.getLocalizedMessage());
        }

        //check if the data is a non-null object
        if (input == null) {
            return new ValidationResult(false, "Data was 'null'");
        } else {
            try {

                GeneralSequenceType seqType = new GeneralSequenceType(getPatterntype());

                switch (getInputFormat()) {
                    case Clustal:
                        setSequenceCollection(inputReader.readClustal(new StringReader(input), seqType));
                        break;
                    case Dialign:
                        setSequenceCollection(inputReader.readSequenceDialign(new StringReader(input), seqType));
                        break;
                    case EMBL:
                        setSequenceCollection(inputReader.readSequenceEMBL(new StringReader(input), seqType));
                        break;
                    case Fasta:
                        setSequenceCollection(inputReader.readSequenceFasta(new StringReader(input), seqType));
                        break;
                    case GDE:
                        setSequenceCollection(inputReader.readSequenceGDE(new StringReader(input), seqType));
                        break;
                    case IG:
                        setSequenceCollection(inputReader.readSequenceIG(new StringReader(input), seqType));
                        break;
                    case MSF:
                        setSequenceCollection(inputReader.readSequenceGCG_MSF_PileUp(new StringReader(input), seqType));
                        break;
                    case NBRF:
                        setSequenceCollection(inputReader.readSequenceNBRF(new StringReader(input), seqType));
                        break;
                    case CODATA:
                        setSequenceCollection(inputReader.readSequenceCODATA(new StringReader(input), seqType));
                        break;
                    case Phylip:
                        setSequenceCollection(inputReader.readSequencePhylip(new StringReader(input), seqType));
                        break;
                    case RSF:
                        setSequenceCollection(inputReader.readSequenceGCG9_RSF(new StringReader(input), seqType));
                        break;
                    case SWISSPROT:
                        setSequenceCollection(inputReader.readSequenceSWISSPROT_UNIPROT(new StringReader(input), seqType));
                        break;
                    case UniProt:
                        setSequenceCollection(inputReader.readSequenceSWISSPROT_UNIPROT(new StringReader(input), seqType));
                        break;
                    case GenBank:
                        return new ValidationResult(false, "Format support will be integrated in a future release!");
                    default:
                        return new ValidationResult(false, "Format failure. '" + getInputFormat() + "' is not supported yet.");
                }



                switch (cardinality) {
                    case single:
                        if (!sequenceCollection.isSingleSequence()) {
                            return new ValidationResult(false, "Mulitple sequences detected, but only single sequence expected.");
                        }
                        break;
                    case multi:
                        if (sequenceCollection.isSingleSequence()) {
                            return new ValidationResult(false, "Single sequence detected, but multi sequence data expected.");
                        }
                        break;
                    case honeybadger:
                        // do nothing ...
                        break;
                }




                boolean valide = de.unibi.cebitec.bibiserv.sequenceparser.tools.SequenceValidator.validateSequenceCollection(sequenceCollection);
                if (valide) {
                    return new ValidationResult(true, "Data correctly validated as " + getPatterntype().name() + " with pattern: " + getPatterntype().getPattern());
                } else {
                    return new ValidationResult(false, "Data was erroneous, did not correctly validate as " + getPatterntype().name());
                }

            } catch (IOException e) {
                return new ValidationResult(false, "Data could not be validated due to an IOException! Exception message was: " + e.getLocalizedMessage());
            } catch (ParserToolException e) {
                return new ValidationResult(false, "Data could not be validated due to an ParserToolException! Exception message was: " + e.getLocalizedMessage());
            } catch (ParserException e) {
                return new ValidationResult(false, "Data could not be validated due to an ParserException! Exception message was: " + e.getLocalizedMessage());
            }
        }
    }
    // ############################## private ##############################
    private PatternType patterntype;

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
    // ############################## getter/setter ##############################
    private CONTENT content;

    @Override
    public CONTENT getContent() {
        return content;
    }

    public void setContent(CONTENT content) {
        patterntype = null; // reset patterntype
        this.content = content;
    }
    private STRICTNESS strictness;

    @Override
    public STRICTNESS getStrictness() {
        patterntype = null; // reset patterntype
        return strictness;
    }

    public void setStrictness(STRICTNESS strictness) {
        this.strictness = strictness;
    }
    private CARDINALITY cardinality;

    @Override
    public CARDINALITY getCardinality() {
        return cardinality;
    }

    public void setCardinality(CARDINALITY cardinality) {
        this.cardinality = cardinality;
    }
    private SequenceCollection sequenceCollection;

    public SequenceCollection getSequenceCollection() {
        return sequenceCollection;
    }

    // ############################## abstract functions ##############################
    public abstract ValidationResult doFurtherValidationSteps(ValidationResult vr);

    public abstract InputFormat getInputFormat();

    // ############################## deprecated functions ##############################
    @Deprecated
    public ValidationResult standardSequenceValidationSteps(Object data, boolean checkSingle, InputFormat inputFormat) {
        return standardSequenceValidationSteps(data);
    }

    // to be removed ...
    private void setSequenceCollection(SequenceCollection seqCol) {
        this.sequenceCollection = seqCol;
    }

    public String getInput() {
        return input;
    }
}
