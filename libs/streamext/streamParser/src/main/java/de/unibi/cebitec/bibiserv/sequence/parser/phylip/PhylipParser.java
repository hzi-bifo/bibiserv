package de.unibi.cebitec.bibiserv.sequence.parser.phylip;

import de.unibi.cebitec.bibiserv.sequence.parser.AbstractSequenceParser;
import de.unibi.cebitec.bibiserv.sequence.parser.ForcedAbortOfPartValidation;
import de.unibi.cebitec.bibiserv.sequence.parser.ReadLineMessage;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.SequenceValidator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser for the alignment only format phylip (interleaved).
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class PhylipParser extends AbstractSequenceParser {

    private PhylipParserState state;
    private Map<Integer, String> indexToSequenceTmp;
    private Map<Integer, Integer> indexToLength;
    private Map<Integer, String> indexToId;
    private Set<String> ids;
    private String headerTmp;
    // index of the current sequence, increased by linebreak
    private int index;
    // to goal as read in first line how many bases are expected
    private int goalLength;
    // to goal as read in first line how many sequences are expected
    private int goalSequenceNum;
    private boolean lastSequenceRead;
    private boolean firstRead;
    private boolean idExtracted;
    private boolean writeIds;
    
    private static final int SEQUENCE_WIDTH = 50;
    private static final int NAME_LENGTH = 10;
    private static final Pattern IGNORED_IN_SEQUENCE = Pattern.compile("\\s+");
    private static final Pattern SPACE_INSERT = Pattern.compile("..........");
    private static final int HEADER = 1;
    private static final int SEQUENCE = 2;

    /**
     * Inherit the super constructor.
     */
    public PhylipParser(BufferedReader input, BufferedWriter output,
            PatternType patternType) {
        super(input, output, patternType);
        state = PhylipParserState.readHeader;
        lastSequenceRead = false;
        firstRead = true;
        idExtracted = false;
        
        writeIds = true;
        
        goalLength = -1;
        goalSequenceNum = -1;
        headerTmp = "";

        ids = new HashSet<String>();
        indexToLength = new HashMap<Integer, Integer>();
        indexToSequenceTmp = new HashMap<Integer, String>();
        indexToId = new TreeMap<Integer, String>();
    }

    @Override
    public int parseAndValidateNextBlock() throws SequenceParserException, ForcedAbortOfPartValidation {
        // is there another possible block?
        if (lastSequenceRead) {
            return -1;
        }

        if (!firstRead) {
            state = PhylipParserState.seekFirstSequence;
        }

        index = 0;

        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }

        if ((state != PhylipParserState.allDone && m != ReadLineMessage.noMoreLines) || (m == ReadLineMessage.noMoreLines && state != PhylipParserState.allDone && state != PhylipParserState.readSequence)) {
            throw new SequenceParserException("No sequence or end of sequence was found!");
        }

        if (goalSequenceNum != index) {
            throw new SequenceParserException("Header specified different number of sequences than where actually found!");
        }

        writePossibleBlocks();

        if (m == ReadLineMessage.noMoreLines) {

            finalizeIds();

            lastSequenceRead = true;
        }
        firstRead = false;
        return index;
    }

    @Override
    protected int lineBeginSize() {
        // needed to extract the names in first block
        return NAME_LENGTH;
    }

    @Override
    protected void lineEmpty() throws SequenceParserException {
        switch (state) {
            case readSequence:
                state = PhylipParserState.allDone;
                break;
            case readHeader:
                throw new SequenceParserException("Header line was expected but empty line found.");
        }
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {
        switch (state) {
            case readHeader:
                state = PhylipParserState.seekFirstSequence;
                return HEADER;
            case seekFirstSequence:
                state = PhylipParserState.readSequence;
                return SEQUENCE;
            case readSequence:
                return SEQUENCE;
            case allDone:
                return END_READLINE;

        }
        return SEQUENCE;
    }

    @Override
    protected void lineTmp(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case HEADER:
                headerTmp += value;
                break;
            case SEQUENCE:
                validate(value);
                break;
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case HEADER:
                headerTmp += value;
                validateHeader(headerTmp);
                write(headerTmp);
                writeNewLine();
                headerTmp = "";
                break;
            case SEQUENCE:
                validate(value);

                if (index > 0) {
                    if (!indexToLength.get(index - 1).equals(indexToLength.get(index))) {
                        throw new SequenceParserException("Sequences differ in length for one block on line " + lineNumber + ".");
                    }
                }

                index++;
                idExtracted = false;
                break;
        }
    }

    private void validate(String value) throws SequenceParserException {
        if (firstRead && !idExtracted) {
            // in the first block ids need to be specified
            String id = value.substring(0, NAME_LENGTH);
            id = id.split("\\s+", 2)[0];
            testID(id);
            idExtracted = true;
            value = value.substring(id.length());
            indexToLength.put(index, 0);
            indexToSequenceTmp.put(index, "");
        }

        Matcher m = IGNORED_IN_SEQUENCE.matcher(value);
        value = m.replaceAll("");

        if (!value.isEmpty() && !SequenceValidator.validate(patternType, value)) {
            throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name() + " : " + value);
        }

        indexToLength.put(index, indexToLength.get(index) + value.length());
        indexToSequenceTmp.put(index, indexToSequenceTmp.get(index) + value);
    }

    private void validateHeader(String headerTmp) throws SequenceParserException {
        String[] split = headerTmp.trim().split("\\s+");
        if (split.length < 2) {
            throw new SequenceParserException("First line does not contain number and length of the sequences.");
        }
        try {
            goalSequenceNum = Integer.parseInt(split[0]);
            goalLength = Integer.parseInt(split[1]);
        } catch (NumberFormatException ex) {
            throw new SequenceParserException("First line does not contain number and length of the sequences.");
        }
    }

    private void testID(String id) throws SequenceParserException {
        if (id.isEmpty()) {
            throw new SequenceParserException("Empty Id on line " + lineNumber + ".");
        }
        if (ids.contains(id)) {
            throw new SequenceParserException("Id '" + id + "' exists more than once.");
        }
        ids.add(id);
        indexToId.put(index, id);
    }

    private String writeSequence(String sequence, int index, boolean writeId) throws SequenceParserException {
        if (sequence.length() >= SEQUENCE_WIDTH) {
            if(writeId) {
                writeId(indexToId.get(index));
            }
            Matcher m = SPACE_INSERT.matcher(sequence.substring(0, SEQUENCE_WIDTH));
            String withspaces = m.replaceAll("$0 ");
            write(withspaces);
            writeNewLine();
            sequence = sequence.substring(SEQUENCE_WIDTH);
        }

        return sequence;
    }

    private void writeId(String id) throws SequenceParserException {
        StringBuilder b = new StringBuilder(id);
        for (int i = id.length(); i < NAME_LENGTH + 1; i++) {
            b.append(' ');
        }
        write(b.toString());
    }

    private void finalizeIds() throws SequenceParserException {

        for (int len : indexToLength.values()) {
            if (len != goalLength) {
                throw new SequenceParserException("Header specified different length of sequences than was actually found!");
            }
        }

        for (String sequence : indexToSequenceTmp.values()) {
            if(!sequence.isEmpty()) {
                Matcher m = SPACE_INSERT.matcher(sequence);
                String withspaces = m.replaceAll("$0 ");
                write(withspaces);
                writeNewLine();
            }
        }
    }

    private void writePossibleBlocks() throws SequenceParserException {
        while (true) {
            for (Map.Entry<Integer, String> entry : indexToSequenceTmp.entrySet()) {
                if (entry.getValue().length() < SEQUENCE_WIDTH) {
                    return;
                }
                entry.setValue(writeSequence(entry.getValue(),entry.getKey(),writeIds));
            }
            writeNewLine();
            writeIds = false;
        }
    }
}
