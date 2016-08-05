/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequence.parser.genebank;


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
 * A parser for GDETagged files. 
 *
 * @author Thomas Gatter - tgatter@cebitec.uni-bielefeld.de
 */
public class GenebankParser extends AbstractSequenceParser {

    private GenebankParserState state;
    private Set<String> ids;
    private String idLine;
    private String sequenceTmp;
    private int sequenceLength;
    private boolean lastSequenceRead;
    
    private String expectedType; 
    private int expectedLength;
    private int sequenceWritten;
    private boolean removeOrigin;
    

    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int ID = 1;
    private static final int SEQUENCE = 2;
    private static final int FULL_IGNORED = 3;
    private static final int WRITE = 4;
    private static final int LOCUS = 5;
    
    /**
     * not allowed chars is sequence
     */
    private static Pattern IGNORED_IN_SEQUENCE = Pattern.compile("[\\s\\d]+");
    private static final int SEQUENCE_WIDTH = 60;
    private static final Pattern SPACE_INSERT = Pattern.compile("..........");
    private static final int NUMBER_WIDTH = 9;
    

    /**
     * Inherit the super constructor.
     */
    public GenebankParser(BufferedReader input, BufferedWriter output,
            PatternType patternType, String type) {
        super(input, output, patternType);
        ids = new HashSet<String>();
        idLine = "";
        sequenceTmp = "";
        lastSequenceRead = false;
        expectedType = type;
        removeOrigin= false;
    }

    @Override
    public int parseAndValidateNextBlock() throws SequenceParserException, ForcedAbortOfPartValidation {
        // is there another possible sequence?
        if (lastSequenceRead) {
            return -1;
        }

        expectedLength = -1;
        sequenceLength = 0;
        sequenceWritten = 0;
        state = GenebankParserState.seekLocus;

        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }
        if (state != GenebankParserState.allDone) {
            throw new SequenceParserException("No sequence or end of sequence was found!");
        }

        if (m == ReadLineMessage.noMoreLines) {
            lastSequenceRead = true;
        }

        if (sequenceLength == 0) {
            throw new SequenceParserException("No sequence-data for an id!");
        }
        
        if(sequenceLength != expectedLength){
            throw new SequenceParserException("Sequence length in LOCUS line and actuall length of sequence are different.");
        }

        return sequenceLength;
    }

    @Override
    protected int lineBeginSize() {
        return 9;
    }

    @Override
    protected void lineEmpty() throws SequenceParserException {
        logWarning("Empty line at line " + lineNumber + " ignored.");
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {
        switch (state) {
            case seekLocus:
                if (value.startsWith("LOCUS")) {
                    state = GenebankParserState.seekId;
                    return LOCUS;
                }
                return WRITE;
            case seekId:
                if (value.startsWith("ACCESSION")) {
                    state = GenebankParserState.seekSequenceStart;
                    return ID;
                }
                return WRITE;
            case seekSequenceStart:
                if (value.startsWith("ORIGIN")) {
                    write("ORIGIN");
                    writeNewLine();       
                    removeOrigin = true;
                    state = GenebankParserState.readSequence;
                    return SEQUENCE;
                }
                return WRITE;
            case readSequence:
                if (value.startsWith("//")) {
                    state = GenebankParserState.allDone;
                    sequenceTmp = writeSequence(sequenceTmp,true);
                    write("//");
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
            case LOCUS:
                // add up header for info extraction at end of line
                idLine+=value;
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
            case LOCUS:
                idLine += value;
                // extract info
                testLocus(idLine);
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


    private void testLocus(String idLine) throws SequenceParserException {
        
        write("LOCUS ");
        idLine = idLine.substring(5).trim();
        String[] entries = idLine.split("\\s+");
        if(entries.length < 4) {
            throw new SequenceParserException("LOCUS lines does not contain all needed Data ([Name] [#Base] bp [Type]) on line "+lineNumber+".");
        }
        try{
            expectedLength = Integer.parseInt(entries[1]);
        } catch(NumberFormatException e) {
            throw new SequenceParserException("LOCUS lines does not contain sequence length on line "+lineNumber+".");
        }
        if(!entries[3].toLowerCase().contains(expectedType.toLowerCase())) {
            throw new SequenceParserException("Type "+entries[3]+" does not equal expected type "+expectedType+" on LOCUST line "+lineNumber+".");
        }
        write(idLine);
        writeNewLine();
    }

    private void testIdLine(String header) throws SequenceParserException {
        //Remove front statement
        String id = header.substring(9).trim();

        if (id.isEmpty()) {
            throw new SequenceParserException("Empty Id on line " + lineNumber + ".");
        }

        if (ids.contains(id)) {
            throw new SequenceParserException("Id '" + id + "' exists more than once.");
        }
        ids.add(id);

        write("ACCESSION ");
        write(id);
        writeNewLine();

    }


    private void cleanAndWriteSequence(String value) throws SequenceParserException {
        
        if(removeOrigin) {
            removeOrigin = false;
            value = value.substring(6);
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

            writeSequenceNumber();

            Matcher m = SPACE_INSERT.matcher(sequence.substring(0, SEQUENCE_WIDTH));
            write(m.replaceAll("$0 "));

            writeNewLine();
            sequence = sequence.substring(SEQUENCE_WIDTH);
        }
        if (finalize) {

            writeSequenceNumber();

            Matcher m = SPACE_INSERT.matcher(sequence);
            String blocks = m.replaceAll("$0 ");
            write(blocks);
            
            writeNewLine();
            sequence = "";
        }
        return sequence;
    }

    /**
     * Writes the current base number after the sequence line.
     *
     * @throws SequenceParserException
     */
    private void writeSequenceNumber() throws SequenceParserException {

        String num = Integer.toString(sequenceWritten * SEQUENCE_WIDTH +1 );
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < NUMBER_WIDTH - num.length(); i++) {
            number.append(' ');
        }
        number.append(num);
        number.append(' ');
        write(number.toString());
        sequenceWritten++;

    }

}
