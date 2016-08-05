package de.unibi.cebitec.bibiserv.sequence.parser.GDEFlat;

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
 * The parser to read all fasta types.
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class GDEFlatParser extends AbstractSequenceParser {

    private GDEFlatParserState state;
    private Set<String> ids;
    private String headerTmp;
    private String sequenceTmp;
    private String sequenceBegin;
    private int sequenceLength;
    private boolean lastSequenceRead;
    private static final int SEQUENCE_WIDTH = 80;
    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int JUST_WRITE = 1;
    private static final int HEADER = 2;
    private static final int SEQUENCE = 3;

    /**
     * Inherit the super constructor.
     */
    public GDEFlatParser(BufferedReader input, BufferedWriter output,
            PatternType patternType, String sequenceBegin) {
        super(input, output, patternType);
        ids = new HashSet<String>();
        headerTmp = "";
        sequenceTmp = "";
        lastSequenceRead = false;
        this.sequenceBegin = sequenceBegin;
    }

    @Override
    public int parseAndValidateNextBlock() throws SequenceParserException, ForcedAbortOfPartValidation {

        // is there another possible sequence?
        if (lastSequenceRead) {
            return -1;
        }

        sequenceLength = 0;
        state = GDEFlatParserState.seekSequenceStart;

        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }
        if (state != GDEFlatParserState.readSequence) {
            throw new SequenceParserException("No sequence was found!");
        }

        if (m == ReadLineMessage.noMoreLines) {
            lastSequenceRead = true;
        }
        writeSequence(sequenceTmp, true);
        sequenceTmp = "";

        if (sequenceLength == 0) {
            throw new SequenceParserException("No sequence-data for an id!");
        }

        return sequenceLength;
    }

    @Override
    protected int lineBeginSize() {
        // only one char is needed for fasta (; or >)
        return 1;
    }

    @Override
    protected void lineEmpty() {
        if (state == GDEFlatParserState.readSequence) {
            logWarning("Empty line at line " + lineNumber + " ignored.");
        }
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {

        switch (state) {
            case seekSequenceStart:
                //test if sequence of real type or mask sequence
                if (value.startsWith(sequenceBegin) || value.startsWith("@")) {
                    state = GDEFlatParserState.readSequence;
                    return HEADER;
                }
                // text sequence
                if (value.startsWith("\"")) {
                    return JUST_WRITE;
                }
                throw new SequenceParserException("Header of correct type " + sequenceBegin + " needed but found something else on line " + lineNumber + ".");
            case readSequence:
                if (value.startsWith("#") || value.startsWith("%") || value.startsWith("\"") || value.startsWith("@")) {
                    return END_READLINE;
                }
                return SEQUENCE;
            case readText:
                if (value.startsWith("#") || value.startsWith("%") || value.startsWith("\"") || value.startsWith("@")) {
                    state = GDEFlatParserState.seekSequenceStart;
                    return lineBegin(value);
                }
                return JUST_WRITE;
        }
        return SEQUENCE; // never reached
    }

    @Override
    protected void lineTmp(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case HEADER:
                // add up header for id extraction at end of line
                headerTmp += value;
                break;
            case SEQUENCE:
                // validate sequence
                value = value.replace('~', '-');
                if (!SequenceValidator.validate(patternType, value)) {
                    throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name());
                }
                ;
                sequenceLength += value.length();
                sequenceTmp += value;
                sequenceTmp = writeSequence(sequenceTmp, false);
                break;
            case JUST_WRITE:
                write(value);
                break;
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case HEADER:
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
                    throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name() + " : " + value);
                }
                value = value.replace('~', '-');
                sequenceLength += value.length();
                sequenceTmp += value;
                sequenceTmp = writeSequence(sequenceTmp, false);
                break;
            case JUST_WRITE:
                write(value);
                writeNewLine();
                break;
        }

    }

    private void testHeader(String header) throws SequenceParserException {
        String id = header.substring(1);
        if (id.isEmpty()) {
            throw new SequenceParserException("Empty Id on line " + lineNumber + ".");
        }
        if (ids.contains(id)) {
            throw new SequenceParserException("Id '" + id + "' exists more than once.");
        }
        ids.add(id);
    }

    private String writeSequence(String sequence, boolean finalize) throws SequenceParserException {
        while (sequence.length() > SEQUENCE_WIDTH) {
            write(sequence.substring(0, SEQUENCE_WIDTH));
            writeNewLine();
            sequence = sequence.substring(SEQUENCE_WIDTH);
        }
        if (finalize) {
            write(sequence);
            writeNewLine();
        }
        return sequence;
    }
}
