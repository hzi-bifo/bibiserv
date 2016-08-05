package de.unibi.cebitec.bibiserv.sequence.parser.uniswissprod;

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
 * A Parser to validate and correct Uni- and Swissprod files.
 *
 * @author Thomas Gatter - tgatter@cebitec.uni-bielefeld.de
 */
public class UniSwissProdParser extends AbstractSequenceParser {

    private UniSwissProdParserState state;
    private Set<String> ids;
    private String headerTmp;
    private String sequenceHeaderTmp;
    private String sequenceTmp;
    private int sequenceLength;
    private int targetLength;
    private boolean lastSequenceRead;
    private boolean correctLineStart;
    //constants for output
    private static final int SEQUENCE_WIDTH = 60;
    private static final Pattern SPACE_INSERT = Pattern.compile("..........");
    private static final String SEQUENCE_INDENT = "     ";
    private static final int SPACES_TO_CONTENT = 3;
    /**
     * Pattern for ignored chars in sequence
     */
    private static final Pattern IGNORED_IN_SEQUENCE = Pattern.compile("[\\s\\d]");
    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int JUST_WRITE = 1;
    private static final int HEADER = 2;
    private static final int SEQUENCEHEADER = 3;
    private static final int SEQUENCE = 4;
    private static final int DO_NOTHING = 5;

    /**
     * Inherit the super constructor.
     */
    public UniSwissProdParser(BufferedReader input, BufferedWriter output,
            PatternType patternType) {
        super(input, output, patternType);
        ids = new HashSet<String>();
        headerTmp = "";
        sequenceTmp = "";
        sequenceHeaderTmp = "";
        lastSequenceRead = false;
        correctLineStart = false;
    }

    @Override
    public int parseAndValidateNextBlock() throws SequenceParserException, ForcedAbortOfPartValidation {
        // is there another possible sequence?
        if (lastSequenceRead) {
            return -1;
        }

        sequenceLength = 0;
        state = UniSwissProdParserState.seekSequenceStart;

        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }
        if (state != UniSwissProdParserState.allDone) {
            throw new SequenceParserException("No sequence or end of sequence was found!");
        }

        if (m == ReadLineMessage.noMoreLines) {
            lastSequenceRead = true;
        }

        if (sequenceLength == 0) {
            throw new SequenceParserException("No sequence-data for an id!");
        }
        
        if (targetLength != sequenceLength){
            throw new SequenceParserException("Length in SQ line does not match the real length of the sequence.");
        }

        return sequenceLength;
    }

    @Override
    protected int lineBeginSize() {
        // guarantee that the spaces are contained in first lineTmp or linEnd call
        return 2 + SPACES_TO_CONTENT;
    }

    @Override
    protected void lineEmpty() throws SequenceParserException {
        if (state != UniSwissProdParserState.seekSequenceStart) {
            warnings.add("Empty line " + lineNumber + " ignored.");
        }
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {
        if (state != UniSwissProdParserState.readSequence && value.length() < 2) {
            throw new SequenceParserException("Line identifier expected but found something else on line " + lineNumber + ".");
        }

        switch (state) {
            case seekSequenceStart:
                if (!value.startsWith("ID")) {
                    throw new SequenceParserException("Sequence ID expected but found something else on line " + lineNumber + ".");
                }
                state = UniSwissProdParserState.seekSequence;
                correctLineStart = true;
                return HEADER;
            case seekSequence:
                correctLineStart = true;
                if (value.startsWith("SQ")) {
                    state = UniSwissProdParserState.readSequence;
                    return SEQUENCEHEADER;
                }
                return JUST_WRITE;
            case allDone:
                return END_READLINE;
            case readSequence:
                if (value.startsWith("//")) {
                    state = UniSwissProdParserState.allDone;
                    writeSequence(sequenceTmp, true);
                    write("//");
                    writeNewLine();
                    writeNewLine();
                    return DO_NOTHING;
                }
                return SEQUENCE;

        }
        // never reached
        return JUST_WRITE;
    }

    @Override
    protected void lineTmp(int ident, String value) throws SequenceParserException {

        if (correctLineStart) {
            value = correctLine(value);
        }

        switch (ident) {
            case HEADER:
                headerTmp += value;
                break;
            case SEQUENCEHEADER:
                sequenceHeaderTmp += value;
                break;
            case JUST_WRITE:
                write(value);
                break;
            case SEQUENCE:
                // validate sequence
                cleanAndWriteSequence(value);
                break;
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        if (correctLineStart) {
            value = correctLine(value);
        }

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
            case SEQUENCEHEADER:
                sequenceHeaderTmp += value;
                String[] split = sequenceHeaderTmp.trim().split("\\s+");
                if (split.length<3) {
                    throw new SequenceParserException("SQ line does not contain all needed tokens on line " + lineNumber + ".");
                }
                if (!split[0].startsWith("SEQUENCE")) {
                    throw new SequenceParserException("SQ line does not contain \"Sequence\" token on line " + lineNumber + ".");
                }
                if (!split[2].startsWith("AA")) {
                    throw new SequenceParserException("SQ line does not contain \"AA\" token on line " + lineNumber + ".");
                }
                try {
                    targetLength = Integer.parseInt(split[1]);
                } catch(NumberFormatException e) {
                    throw new SequenceParserException("SQ line does not contain a valid number of aminoacids on line " + lineNumber + ".");
                }
                
                write(sequenceHeaderTmp);
                writeNewLine();
                sequenceHeaderTmp = "";
                break;
            case SEQUENCE:
                cleanAndWriteSequence(value);
                break;
            case JUST_WRITE:
                write(value);
                writeNewLine();
                break;
        }
    }

    private void cleanAndWriteSequence(String value) throws SequenceParserException {
        Matcher m = IGNORED_IN_SEQUENCE.matcher(value);
        String cleanSequence = m.replaceAll("");

        if (!cleanSequence.isEmpty() && !SequenceValidator.validate(patternType, cleanSequence)) {
            throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name());
        }
        sequenceLength += cleanSequence.length();
        sequenceTmp += cleanSequence;
        sequenceTmp = writeSequence(sequenceTmp, false);
    }

    private String writeSequence(String sequence, boolean finalize) throws SequenceParserException {
        while (sequence.length() > SEQUENCE_WIDTH) {

            write(SEQUENCE_INDENT);

            Matcher m = SPACE_INSERT.matcher(sequence.substring(0, SEQUENCE_WIDTH));
            write(m.replaceAll("$0 "));
            
            writeNewLine();
            sequence = sequence.substring(SEQUENCE_WIDTH);
        }
        if (finalize) {

            write(SEQUENCE_INDENT);

            Matcher m = SPACE_INSERT.matcher(sequence);
            String blocks = m.replaceAll("$0 ");
            write(blocks);

            StringBuilder tmp = new StringBuilder();
            for (int i = 0; i < SEQUENCE_WIDTH + 6 - blocks.length(); i++) {
                tmp.append(' ');
            }
            write(tmp.toString());

            writeNewLine();
        }
        return sequence;
    }

    /**
     * Writes the current base number after the sequence line.
     *
     * @throws SequenceParserException
     */
    private void testHeader(String header) throws SequenceParserException {
        String id = header.trim().split("\\s")[0];
        if (id.isEmpty()) {
            throw new SequenceParserException("Empty Id on line " + lineNumber + ".");
        }
        if (ids.contains(id)) {
            throw new SequenceParserException("Id '" + id + "' exists more than once.");
        }
        ids.add(id);
    }

    private String correctLine(String value) throws SequenceParserException {
        write(value.substring(0, 2));
        value = value.substring(2);
        for (int i = 0; i < SPACES_TO_CONTENT; i++) {
            if (i >= value.length() || value.charAt(i) != ' ') {
                for (int j = i; j < SPACES_TO_CONTENT; j++) {
                    write(" ");
                }
                break;
            }
        }
        correctLineStart = false;

        return value;
    }
}
