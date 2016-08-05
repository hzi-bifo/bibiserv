package de.unibi.cebitec.bibiserv.sequence.parser.fastq;

import de.unibi.cebitec.bibiserv.sequence.parser.AbstractSequenceParser;
import de.unibi.cebitec.bibiserv.sequence.parser.ForcedAbortOfPartValidation;
import de.unibi.cebitec.bibiserv.sequence.parser.ReadLineMessage;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.SequenceValidator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Thomas Gatter - Validates a fastqfile. Removes all empty lines and
 * linebreaks in sequence-data and quality data.
 */
public abstract class FastQParserBase extends AbstractSequenceParser {

    private FastQParserState state;
    private Set<String> ids;
    private String headerTmp;
    private int sequenceLength;
    private int qualityLength;
    private boolean lastSequenceRead;
    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int HEADER_ONE = 1;
    private static final int SEQUENCE = 2;
    private static final int HEADER_TWO = 3;
    private static final int QUALITY = 4;

    abstract protected String getName();

    abstract protected FastQVariants getVariant();

    /**
     * Inherit the super constructor.
     */
    public FastQParserBase(BufferedReader input, BufferedWriter output,
            PatternType patternType) {
        super(input, output, patternType);
        ids = new HashSet<String>();
        headerTmp = "";
        lastSequenceRead = false;
    }

    @Override
    public int parseAndValidateNextBlock() throws SequenceParserException, ForcedAbortOfPartValidation {

        // is there another possible sequence?
        if (lastSequenceRead) {
            return -1;
        }

        qualityLength = 0;
        sequenceLength = 0;
        state = FastQParserState.seekSequenceStart;

        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }
        if (state != FastQParserState.readQuality) {
            throw new SequenceParserException("No sequence was found!");
        }

        if (m == ReadLineMessage.noMoreLines) {
            lastSequenceRead = true;
        }

        if (sequenceLength == 0) {
            throw new SequenceParserException("No sequence-data for an id!");
        }

        if (sequenceLength != qualityLength) {
            throw new SequenceParserException("Sequence and Quality differ in length!" + " Linenumber: "+lineNumber);
        }

        return sequenceLength;
    }

    @Override
    protected int lineBeginSize() {
        // only one char is needed for fastq @ or +)
        return 1;
    }

    @Override
    protected void lineEmpty() {
        if (state == FastQParserState.readSequence || state == FastQParserState.readQuality) {
            logWarning("Empty line at line " + lineNumber + " ignored.");
        }
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {

        switch (state) {
            case seekSequenceStart:
                if (value.startsWith("@")) {
                    state = FastQParserState.readSequence;
                    return HEADER_ONE;
                }
                throw new SequenceParserException("Header needed but found something else on line " + lineNumber + ".");
            case readSequence:
                // + can also appear in quality string, but not in sequence, so first plus is a definite second header
                if (value.startsWith("+")) {
                    state = FastQParserState.readQuality;
                    writeNewLine();
                    return HEADER_TWO;
                }
                return SEQUENCE;
            case readQuality:
                // @ can also appear in quality strings
                if (value.startsWith("@") && qualityLength >= sequenceLength) {
                    writeNewLine();
                    return END_READLINE;
                }
                return QUALITY;
        }
        return SEQUENCE; // never reached
    }

    @Override
    protected void lineTmp(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case HEADER_ONE:
                // add up header for id extraction at end of line
                headerTmp += value;
                break;
            case SEQUENCE:
                // validate sequence
                if (!SequenceValidator.validate(patternType, value)) {
                    throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name() + " Linenumber: "+lineNumber);
                }
                sequenceLength += value.length();
                write(value);
                break;
            case QUALITY:
                if (!validateQuality(value)) {
                    throw new SequenceParserException("Data was erroneous, Quality did not correctly validate as " + getName()+ " Linenumber: "+lineNumber);
                }
                qualityLength += value.length();
                write(value);
                break;
            // do nothing for HEADER_TWO
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case HEADER_ONE:
                // add up header for id extraction
                headerTmp += value;
                // extract id and test for double
                testHeader(headerTmp);

                // write to output
                write(headerTmp);
                writeNewLine();
                headerTmp = "";
                break;
            case SEQUENCE:
                // validate sequence
                if (!value.isEmpty() && !SequenceValidator.validate(patternType, value)) {
                    throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name() + " : " + value + " Linenumber: "+lineNumber);
                }
                ;
                sequenceLength += value.length();
                write(value);
                break;
            case QUALITY:
                // validate sequence
                if (!value.isEmpty() && !validateQuality(value)) {
                    throw new SequenceParserException("Data was erroneous, Quality did not correctly validate as " + getName() + " Linenumber: "+lineNumber);
                }
                qualityLength += value.length();
                write(value);
                break;
            case HEADER_TWO:
                // ignore second id 
                write("+");
                writeNewLine();
                break;
        }
    }

    private void testHeader(String header) throws SequenceParserException {
        String id = header.substring(1); //.split("\\s")[0];
        if (id.isEmpty()) {
            throw new SequenceParserException("Empty Id on line " + lineNumber + ".");
        }
        if (ids.contains(id)) {
            throw new SequenceParserException("Id '" + id + "' exists more than once." + " Linenumber: "+lineNumber);
        }
        ids.add(id);
    }

    private boolean validateQuality(String quality) {
        FastQVariants variant = getVariant();
        
        if(variant == FastQVariants.dontValidate) {
            return true;
        }
        
        for (int c : quality.toCharArray()) {
            if (c < variant.getOffset() || c > variant.getMax()) {
                return false;
            }
        }
        return true;
    }
}
