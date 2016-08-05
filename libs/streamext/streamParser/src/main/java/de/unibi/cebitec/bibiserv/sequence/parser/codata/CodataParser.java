/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequence.parser.codata;

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
 * A parser for codata pir files. This will ignore and remove all punctuation
 * and corrects numbers as well as white spaces.
 *
 * @author Thomas Gatter - tgatter@cebitec.uni-bielefeld.de
 */
public class CodataParser extends AbstractSequenceParser {

    private CodataParserState state;
    private Set<String> ids;
    private String headerTmp;
    private String sequenceTmp;
    private int sequenceWritten;
    private int sequenceLength;
    private boolean lastSequenceRead;
    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int ENTRY = 1;
    private static final int SEQUENCE = 2;
    private static final int FULL_IGNORED = 3;
    private static final int WRITE_IGNORED = 4;
    
    
    private static final int SEQUENCE_WIDTH = 30;
    
    
    private static final Pattern IGNORED_IN_SEQUENCE_PUNCTUATION = Pattern.compile("[().,=/]+");
    private static final Pattern IGNORED_IN_SEQUENCE_NORMAL = Pattern.compile("[\\s\\d]");
    private static String NUMBER_HEADER = "                 5        10        15        20        25        30";
    private static final int FRONT_SPACES = 8;
    private static final Pattern SPACE_INSERT = Pattern.compile(".");

    /**
     * Inherit the super constructor.
     */
    public CodataParser(BufferedReader input, BufferedWriter output,
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

        // file start symbol
        if(ids.size()==0){
            write("\\\\\\");
            writeNewLine();
        }
        
        sequenceLength = 0;
        sequenceWritten = 0;
        state = CodataParserState.seekIdEntry;

        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }
        if (state != CodataParserState.readEnd) {
            throw new SequenceParserException("No sequence or end of sequence was found!");
        }

        if (m == ReadLineMessage.noMoreLines) {
            write("\\\\\\");
            writeNewLine();
            lastSequenceRead = true;
        }

        if (sequenceLength == 0) {
            throw new SequenceParserException("No sequence-data for an id!");
        }

        return sequenceLength;
    }

    @Override
    protected int lineBeginSize() {
        return 8;
    }

    @Override
    protected void lineEmpty() throws SequenceParserException {
        if (state == CodataParserState.readSequence) {
            logWarning("Empty line at line " + lineNumber + " ignored.");
        }
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {
        switch (state) {
            case seekIdEntry:
                if (value.startsWith("ENTRY")) {
                    state = CodataParserState.seekSequenceStart;
                    return ENTRY;
                }
                return FULL_IGNORED;
            case seekSequenceStart:
                if (value.startsWith("SEQUENCE")) {
                    write("SEQUENCE");
                    writeNewLine();
                    write(NUMBER_HEADER);
                    writeNewLine();
                    state = CodataParserState.readSequence;
                    return FULL_IGNORED;
                }
                return WRITE_IGNORED;
            case readSequence:
                if (value.startsWith("///")) {
                    writeSequence(sequenceTmp, true);
                    sequenceTmp = "";
                    write("///");
                    writeNewLine();
                    state = CodataParserState.readEnd;
                    return FULL_IGNORED;
                }
                return SEQUENCE;
            case readEnd:
                return END_READLINE;
        }
        return SEQUENCE; // never reached
    }

    @Override
    protected void lineTmp(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case ENTRY:
                // add up header for id extraction at end of line
                headerTmp += value;
                break;
            case SEQUENCE:
                // validate sequence
                cleanAndWriteSequence(value);
                break;
            case WRITE_IGNORED:
                write(value);
                break;
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case ENTRY:
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
                cleanAndWriteSequence(value);
                break;
            case WRITE_IGNORED:
                write(value);
                writeNewLine();
                break;
        }
    }

    private String writeSequence(String sequence, boolean finalize) throws SequenceParserException {
        while (sequence.length() > SEQUENCE_WIDTH) {
            writeSequenceNumber();

            Matcher m = SPACE_INSERT.matcher(sequence.substring(0, SEQUENCE_WIDTH));
            write(m.replaceAll("$0 "));

            writeNewLine();
            sequence = sequence.substring(SEQUENCE_WIDTH);
        }
        if (finalize) {
            writeSequenceNumber();

            Matcher m = SPACE_INSERT.matcher(sequence);
            write(m.replaceAll("$0 "));

            writeNewLine();
        }
        return sequence;
    }

    /**
     * Writes the current front base number.
     *
     * @throws SequenceParserException
     */
    private void writeSequenceNumber() throws SequenceParserException {
        String num = Integer.toString(sequenceWritten * SEQUENCE_WIDTH + 1);
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < FRONT_SPACES - num.length(); i++) {
            number.append(' ');
        }
        number.append(num);
        number.append(' ');
        write(number.toString());
        sequenceWritten++;
    }

    private void testHeader(String header) throws SequenceParserException {
        String[] entry = header.substring(1).split("\\s+");
        
        if(entry.length<2) {
            throw new SequenceParserException("No Id found on line "+lineNumber+".");
        }
        
        String id = entry[1];
        if (ids.contains(id)) {
            throw new SequenceParserException("Id '" + id + "' exists more than once.");
        }
        ids.add(id);
    }

    private void cleanAndWriteSequence(String value) throws SequenceParserException {

        // remove Punctuation
        Matcher m = IGNORED_IN_SEQUENCE_PUNCTUATION.matcher(value);
        String noPunctuation = m.replaceAll("");

        if (value.length() != noPunctuation.length()) {
            logWarning("Punctuatios have been removed on line " + lineNumber + ".");
        }

        m = IGNORED_IN_SEQUENCE_NORMAL.matcher(noPunctuation);
        String cleanSequence = m.replaceAll("");

        if (!cleanSequence.isEmpty() && !SequenceValidator.validate(patternType, cleanSequence)) {
            throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name());
        }
        sequenceLength += cleanSequence.length();
        sequenceTmp += cleanSequence;
        sequenceTmp = writeSequence(sequenceTmp, false);
    }
}
