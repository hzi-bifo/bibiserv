package de.unibi.cebitec.bibiserv.sequence.parser.IG;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The parser to read all fasta types.
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class IGParser extends AbstractSequenceParser {

    private IGParserState state;
    private Set<String> ids;
    private String headerTmp;
    private String sequenceTmp;
    private String sequenceTerminator;
    private int sequenceLength;
    private boolean lastSequenceRead;
    private static final int SEQUENCE_WIDTH = 80;
    private static final Pattern IGNORED_IN_SEQUENCE = Pattern.compile("[\\s]");
    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int COMMENT_LINE = 1;
    private static final int HEADER = 2;
    private static final int SEQUENCE = 3;

    /**
     * Inherit the super constructor.
     */
    public IGParser(BufferedReader input, BufferedWriter output,
            PatternType patternType) {
        super(input, output, patternType);
        ids = new HashSet<String>();
        headerTmp = "";
        sequenceTmp = "";
        lastSequenceRead = false;
    }

    @Override
    public int parseAndValidateNextBlock() throws SequenceParserException, ForcedAbortOfPartValidation {

        // is there another possible sequence?
        if (lastSequenceRead) {
            return -1;
        }

        sequenceLength = 0;
        state = IGParserState.seekSequenceStart;

        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }
        if (state != IGParserState.allDone) {
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
        // only one char is needed for comment ;
        return 1;
    }

    @Override
    protected void lineEmpty() {
        logWarning("Empty line at line " + lineNumber + " ignored.");
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {

        switch (state) {
            case seekSequenceStart:
                if (value.startsWith(";")) {
                    return COMMENT_LINE;
                }

                state = IGParserState.readSequence;
                return HEADER;
            case readSequence:
                if (value.startsWith(";")) {
                    throw new SequenceParserException("Comment lines are not permitted in during a sequence on line " + lineNumber + ". This might occur because the sequence was not terminated by 1 or 2.");
                }
                return SEQUENCE;
            case allDone:
                return END_READLINE;
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
                validateAndWriteSequence(value);
                break;
            case COMMENT_LINE:
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
                validateAndWriteSequence(value);
                break;
            case COMMENT_LINE:
                write(value);
                writeNewLine();
                break;
        }

    }

    private void testHeader(String header) throws SequenceParserException {
        if (header.contains("\\s")) {
            throw new SequenceParserException("Whitespaces are not allowed in ID line on line " + lineNumber + "");
        }
        String id = header;
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
            write(sequenceTerminator);
            writeNewLine();
        }
        return sequence;
    }

    private void validateAndWriteSequence(String value) throws SequenceParserException {

        Matcher m = IGNORED_IN_SEQUENCE.matcher(value);
        String cleaned = m.replaceAll("");

        if (value.endsWith("1") || value.endsWith("2")) {
            sequenceTerminator = cleaned.substring(cleaned.length() - 1);
            cleaned = cleaned.substring(0, cleaned.length() - 1);
            state = IGParserState.allDone;
        }

        if (!cleaned.isEmpty() && !SequenceValidator.validate(patternType, cleaned)) {
            throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name());
        }

        sequenceLength += cleaned.length();
        sequenceTmp += cleaned;
        sequenceTmp = writeSequence(sequenceTmp, false);
    }
}
