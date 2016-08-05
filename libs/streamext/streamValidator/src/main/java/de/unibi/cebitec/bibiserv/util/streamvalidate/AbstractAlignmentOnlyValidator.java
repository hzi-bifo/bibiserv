package de.unibi.cebitec.bibiserv.util.streamvalidate;

import de.unibi.cebitec.bibiserv.sequence.parser.AbstractSequenceParser;
import de.unibi.cebitec.bibiserv.sequence.parser.ForcedAbortOfPartValidation;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import de.unibi.cebitec.bibiserv.sequence.parser.clustal.ClustalParser;
import de.unibi.cebitec.bibiserv.sequence.parser.msf.MsfParser;
import de.unibi.cebitec.bibiserv.sequence.parser.phylip.PhylipParser;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.Clustal;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.MSF;
import static de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat.Phylip;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;

import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * This is a general Validator for alignment only formats. For formats allowing
 * alignments and non-alignments please see AbstractAlignmentValidator
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractAlignmentOnlyValidator extends AbstractStringStreamValidator implements AlignmentStreamValidator {

    @Override
    public ValidationResult validateThis(BufferedReader input, BufferedWriter output) {
        AbstractSequenceParser parser;
        try {
            parser = getParser(input, output);
        } catch (ValidationException ex) {
            return new ValidationResult(false, ex.getMessage());
        }

        try {
            boolean firstread = true;
            int numSequences = 0;
            int numSequencesLastTime;
            
            while ((numSequencesLastTime = parser.parseAndValidateNextBlock()) != -1) {
                
                if(numSequencesLastTime<2){
                    throw new ValidationException("Single sequence or no sequence detected, but multi sequence data expected.");
                }
                if(firstread){
                    numSequences = numSequencesLastTime;
                    firstread = false;
                } else {
                    if(numSequences != numSequencesLastTime){
                        throw new ValidationException("Number of sequences varies throughout the file.");
                    }
                }
            }
        } catch (SequenceParserException | ValidationException ex) {
            return closeStreamsAndReturnValidationResult(input,output,false, ex.getMessage());
        } catch (ForcedAbortOfPartValidation ex) {
             return closeStreamsAndReturnValidationResult(input,output, true, "The first part of the was data correctly validated as " + getPatterntype().name() + " with pattern: " + getPatterntype().getPattern()+".");
        }

        return closeStreamsAndReturnValidationResult(input,output, true, "Data correctly validated as " + getPatterntype().name() + " with pattern: " + getPatterntype().getPattern(), parser.getWarnings());
        
    }

    // ############################## private ##############################
    protected AbstractSequenceParser getParser(BufferedReader input, BufferedWriter output) throws ValidationException {
        AbstractSequenceParser parser;

        PatternType pattern = getPatterntype();
        switch (getInputFormat()) {
            case Clustal:
                parser = new ClustalParser(input, output, pattern);
                break;
            case Phylip:
                parser = new PhylipParser(input, output, pattern);
                break;
            case MSF:
                boolean aa = false;
                 if(getContent()==CONTENT.AA) {
                    //AA
                    aa = true;
                 }
                parser = new MsfParser(input, output, pattern,aa);
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
        return CARDINALITY.multi;
    }

    public void setCardinality(CARDINALITY cardinality) {
        this.cardinality = CARDINALITY.multi;
    }
    
    boolean alignment;
    @Override
    public boolean isAlignment() {
        return true;
    }

    @Override
    public void setAlignment(boolean alignment) {
        this.alignment = true;
    }
}
