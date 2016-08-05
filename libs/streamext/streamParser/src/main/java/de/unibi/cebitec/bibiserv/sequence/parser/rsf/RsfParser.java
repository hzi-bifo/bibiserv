
package de.unibi.cebitec.bibiserv.sequence.parser.rsf;


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
 * A parser for RSF files. 
 *
 * @author Thomas Gatter - tgatter@cebitec.uni-bielefeld.de
 */
public class RsfParser extends AbstractSequenceParser {

    private RsfParserState state;
    private Set<String> ids;
    private String idLine;
    private String sequenceTmp;
    private int sequenceLength;
    private boolean lastSequenceRead;
    private boolean firstSequence;
    
    private boolean removeSequence;
    

    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int ID = 1;
    private static final int SEQUENCE = 2;
    private static final int FULL_IGNORED = 3;
    private static final int WRITE = 4;
    
    /**
     * not allowed chars is sequence
     */

    private static final int SEQUENCE_WIDTH = 60;
    private static final String SEQUENCE_INDENT = "   ";
    
    private static final Pattern IGNORED_IN_SEQUENCE = Pattern.compile("\\s+");
    

    /**
     * Inherit the super constructor.
     */
    public RsfParser(BufferedReader input, BufferedWriter output,
            PatternType patternType) {
        super(input, output, patternType);
        ids = new HashSet<String>();
        idLine = "";
        sequenceTmp = "";
        lastSequenceRead = false;
        removeSequence= false;
        state = RsfParserState.readRSF;
        firstSequence = true;
    }

    @Override
    public int parseAndValidateNextBlock() throws SequenceParserException, ForcedAbortOfPartValidation {
        // is there another possible sequence?
        if (lastSequenceRead) {
            return -1;
        }
        
        if(!firstSequence) {
            state = RsfParserState.seekBraceStart;
        }
        
        sequenceLength = 0;

        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }
        if (state != RsfParserState.allDone) {
            throw new SequenceParserException("No sequence or end of sequence was found!");
        }

        if (m == ReadLineMessage.noMoreLines) {
            lastSequenceRead = true;
        }

        if (sequenceLength == 0) {
            throw new SequenceParserException("No sequence-data for an id!");
        }
        
        firstSequence = false;

        return sequenceLength;
    }

    @Override
    protected int lineBeginSize() {
        return 15;
    }

    @Override
    protected void lineEmpty() throws SequenceParserException {
        logWarning("Empty line at line " + lineNumber + " ignored.");
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {
        switch (state) {
            case readRSF:
                if (value.startsWith("!!RICH_SEQUENCE")) {
                    state = RsfParserState.readPoints;
                    return WRITE;
                }
                throw new SequenceParserException("First line does not start with the \"!!RICH_SEQUENCE\" token.");
            case readPoints:
                if (value.startsWith("..")) {
                    state = RsfParserState.seekBraceStart;
                    return WRITE;
                }
                throw new SequenceParserException("Second line does not start with the \"..\" token.");
            case seekBraceStart:
                if (value.equals("{")) {
                    state = RsfParserState.seekId;
                    write("{");
                    writeNewLine();
                    return FULL_IGNORED;
                }
                throw new SequenceParserException("Parentese { followed by linebreak expected but found something else on line " + lineNumber + ".");
            case seekId:
                if (value.startsWith("name")) {
                    state = RsfParserState.seekSequenceStart;
                    return ID;
                }
                return WRITE;
            case seekSequenceStart:
                if (value.trim().split("//s+",2)[0].equals("sequence")) {
                    write("sequence");
                    writeNewLine();       
                    removeSequence = true;
                    state = RsfParserState.readSequence;
                    return SEQUENCE;
                }
                return WRITE;
            case readSequence:
                if (value.equals("}")) {
                    state = RsfParserState.allDone;
                    sequenceTmp = writeSequence(sequenceTmp,true);
                    write("}");
                    writeNewLine();
                    return FULL_IGNORED;
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
            case ID:
                // add up idline for id extraction at end of line
                idLine += value;
                break;
            case SEQUENCE:
                // validate sequence
                cleanAndWriteSequence(value);
                break;
            case WRITE:
                write(value);
                break;
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case ID:
                // add up id for id extraction
                idLine += value;
                // extract id and test for double
                testIdLine(idLine);
                idLine = "";
                break;
            case SEQUENCE:
                // validate sequence
                cleanAndWriteSequence(value);
                break;
            case WRITE:
                write(value);
                writeNewLine();
                break;
        }
    }

    private void testIdLine(String header) throws SequenceParserException {
        //Remove front statement
        String id = header.substring(4).trim();

        if (id.isEmpty()) {
            throw new SequenceParserException("Empty Id on line " + lineNumber + ".");
        }

        if (ids.contains(id)) {
            throw new SequenceParserException("Id '" + id + "' exists more than once.");
        }
        ids.add(id);

        write("name ");
        write(id);
        writeNewLine();

    }


    private void cleanAndWriteSequence(String value) throws SequenceParserException {
        
        if(removeSequence) {
            removeSequence = false;
            value = value.substring(8);
        }
        
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
            write(sequence.substring(0, SEQUENCE_WIDTH));

            writeNewLine();
            sequence = sequence.substring(SEQUENCE_WIDTH);
        }
        if (finalize) {

            write(SEQUENCE_INDENT);
            write(sequence);
            writeNewLine();
            sequence = "";
        }
        return sequence;
    }

}
