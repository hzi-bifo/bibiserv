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
package de.unibi.cebitec.bibiserv.util.streamvalidate;

import de.unibi.cebitec.bibiserv.sequence.parser.EMBL.EMBLParser;
import de.unibi.cebitec.bibiserv.sequence.parser.ForcedAbortOfPartValidation;
import de.unibi.cebitec.bibiserv.sequence.parser.GDEFlat.GDEFlatParser;
import de.unibi.cebitec.bibiserv.sequence.parser.GDETagged.GDETaggedParser;
import de.unibi.cebitec.bibiserv.sequence.parser.IG.IGParser;
import de.unibi.cebitec.bibiserv.sequence.parser.NBRF.NBRFParser;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParser;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import de.unibi.cebitec.bibiserv.sequence.parser.codata.CodataParser;
import de.unibi.cebitec.bibiserv.sequence.parser.fasta.FastaParser;
import de.unibi.cebitec.bibiserv.sequence.parser.fastq.FastQAllParser;
import de.unibi.cebitec.bibiserv.sequence.parser.fastq.FastQIlluminaParser;
import de.unibi.cebitec.bibiserv.sequence.parser.fastq.FastQSangerParser;
import de.unibi.cebitec.bibiserv.sequence.parser.fastq.FastQSolexaParser;
import de.unibi.cebitec.bibiserv.sequence.parser.genebank.GenebankParser;
import de.unibi.cebitec.bibiserv.sequence.parser.rsf.RsfParser;
import de.unibi.cebitec.bibiserv.sequence.parser.uniswissprod.UniSwissProdParser;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.CODATA;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.EMBL;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.Fasta;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.GDE_Flat;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.GDE_Tagged;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.GenBank;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.IG;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.NBRF;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.RSF;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.SWISSPROT;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.UniProt;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;

import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;


/**
 * AbstractSequenceValidator implements SequenceValidator Interface
 *
 * @author Richard Madsack, Madis Rumming, Jan Krueger -
 * jkrueger(at)cebitec.uni-bielefeld.de, Thomas Gatter -
 * tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractSequenceValidator extends AbstractStringStreamValidator implements SequenceStreamValidator {

    public AbstractSequenceValidator() {
    }

    @Override
    public ValidationResult validateThis(BufferedReader input, BufferedWriter output) {

        SequenceParser parser;
        try {
            parser = getParser(input, output);
        
            int sequencelength = -1;
            int numSequences = 0;

            int nextSequence;
            while ((nextSequence = parser.parseAndValidateNextBlock()) != -1) {
                numSequences++;
                if (numSequences == 1) {
                    sequencelength = nextSequence;
                }
                validationStep(sequencelength, nextSequence, numSequences);
            }

            if(numSequences ==0){
                return closeStreamsAndReturnValidationResult(input,output,false, "No Sequence was detected.");
            }
            if (cardinality == CARDINALITY.multi && numSequences < 2) {
                return closeStreamsAndReturnValidationResult(input,output, false, "Single sequence detected, but multi sequence data expected.");
            }
            
        } catch (SequenceParserException | ValidationException ex) {
            return closeStreamsAndReturnValidationResult(input,output,false, ex.getMessage());
        } catch (ForcedAbortOfPartValidation ex) {
             return closeStreamsAndReturnValidationResult(input,output, true, "The first part of the data was correctly validated as " + getPatterntype().name() + " with pattern: " + getPatterntype().getPattern()+".");
        }

        return closeStreamsAndReturnValidationResult(input,output, true, "Data correctly validated as " + getPatterntype().name() + " with pattern: " + getPatterntype().getPattern(), parser.getWarnings());
    }

    protected void validationStep(int sequenceLength, int nextSequenceLength, int numSequences) throws ValidationException {
        if (numSequences > 1 && cardinality == CARDINALITY.single) {
            throw new ValidationException("Multiple sequences detected, but only single sequence expected.");
        }
    }
    
    // ############################## private ##############################

    protected SequenceParser getParser(BufferedReader input, BufferedWriter output) throws ValidationException {
        SequenceParser parser;

        PatternType pattern = getPatterntype();
        switch (getInputFormat()) {
            case Fasta:
                parser = new FastaParser(input, output, pattern);
                break;
            case CODATA:
                parser = new CodataParser(input, output, pattern);
                break;
            case SWISSPROT:
            case UniProt:
                parser = new UniSwissProdParser(input, output, pattern);
                break;
            case EMBL:
                parser = new EMBLParser(input, output, pattern);
                break;
            case IG:
                parser = new IGParser(input, output, pattern);
                break;
            case NBRF:
                parser = new NBRFParser(input, output, pattern);
                break;
            case GDE_Flat:
                // GDE differentiates sequence beginnings by type
                //DNA,RNA
                String sequencebegin = "#";
                if(getContent()==CONTENT.AA) {
                    //AA
                    sequencebegin = "%"; 
                }
                parser = new GDEFlatParser(input, output, pattern,sequencebegin);
                break;
            case GDE_Tagged:
                parser = new GDETaggedParser(input, output, pattern);
                break;
            case GenBank:
                String typeString = "na";
                switch(getContent()) {
                    case DNA:
                        typeString = "dna";
                        break;
                    case RNA:
                        typeString = "rna";
                        break;
                }
                parser = new GenebankParser(input, output, pattern, typeString);
                break;
            case RSF:
                parser = new RsfParser(input, output, pattern);
                break;
            case FastQAll:
                parser = new FastQAllParser(input,output,pattern);
                break;
            case FastQSolexa:
                parser = new FastQSolexaParser(input,output,pattern);
                break;
            case FastQIllumina:
                parser = new FastQIlluminaParser(input,output,pattern);
                break;
            case FastQSanger:
                parser = new FastQSangerParser(input,output,pattern);
                break;
            default:
                throw new ValidationException("Format failure. '" + getInputFormat() + "' is not supported yet.");
        }
        if(setMaxRead) {
            parser.setMaximumCharsToValidate(maxRead);
        }
        return parser;
    }
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

    // ############################## abstract fkt  ##############################
    public abstract InputFormat getInputFormat();
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
}
