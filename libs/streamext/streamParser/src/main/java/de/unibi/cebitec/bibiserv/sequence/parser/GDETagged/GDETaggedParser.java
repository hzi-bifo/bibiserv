/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequence.parser.GDETagged;


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
public class GDETaggedParser extends AbstractSequenceParser {

    private GDETaggedParserState state;
    private Set<String> ids;
    private String idLine;
    private String sequenceTmp;
    private String lineTmp;
    private int sequenceLength;
    private boolean lastSequenceRead;
    private boolean inQoute;
    private boolean cleanSequenceStart;
    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int ID = 1;
    private static final int SEQUENCE = 2;
    private static final int FULL_IGNORED = 3;
    private static final int WRITE = 4;
    private static final int SEQUENCE_WIDTH = 80;
    /**
     * The maximum excepted line length for comments, to avoid memory overflow.
     * The does not apply for sequence data.
     */
    private static final int MAX_COMMENT_LINE = 10000000;
    /**
     * not allowed chars is comment (except quotes on start and end)
     */
    private static Pattern NOT_ALLOWED_IN_COMMENT = Pattern.compile("[\"{}]");
    private static Pattern FORCE_QUOTE = Pattern.compile("\\s");

    /**
     * Inherit the super constructor.
     */
    public GDETaggedParser(BufferedReader input, BufferedWriter output,
            PatternType patternType) {
        super(input, output, patternType);
        ids = new HashSet<String>();
        idLine = "";
        sequenceTmp = "";
        lastSequenceRead = false;
        inQoute = false;
        lineTmp="";
        cleanSequenceStart = false;
    }

    @Override
    public int parseAndValidateNextBlock() throws SequenceParserException, ForcedAbortOfPartValidation {
        // is there another possible sequence?
        if (lastSequenceRead) {
            return -1;
        }

        inQoute = false;
        sequenceLength = 0;
        state = GDETaggedParserState.seekBraceStart;

        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }
        if (state != GDETaggedParserState.allDone) {
            throw new SequenceParserException("No sequence or end of sequence was found!");
        }

        if (m == ReadLineMessage.noMoreLines) {
            lastSequenceRead = true;
        }

        if (sequenceLength == 0) {
            throw new SequenceParserException("No sequence-data for an id!");
        }

        return sequenceLength;
    }

    @Override
    protected int lineBeginSize() {
        return 11;
    }

    @Override
    protected void lineEmpty() throws SequenceParserException {
        if (state == GDETaggedParserState.readSequence) {
            logWarning("Empty line at line " + lineNumber + " ignored.");
        } else {
            if (inQoute) {
                writeNewLine();
            }
        }


    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {
        switch (state) {
            case seekBraceStart:
                if (value.equals("{")) {
                    state = GDETaggedParserState.seekId;
                    write("{");
                    writeNewLine();
                    return FULL_IGNORED;
                }
                throw new SequenceParserException("Parentese { followed by linebreak expected but found something else on line " + lineNumber + ".");
            case seekId:
                if (!inQoute && value.startsWith("sequence-ID")) {
                    state = GDETaggedParserState.seekSequenceStart;
                    return ID;
                }
                return WRITE;
            case readId:
                state = GDETaggedParserState.seekSequenceStart;
                return ID;
            case seekSequenceStart:
                if (!inQoute && value.startsWith("sequence")) {
                    state = GDETaggedParserState.readSequence;
                    cleanSequenceStart = true;
                    return SEQUENCE;
                }
                return WRITE;
            case readSequence:
                return SEQUENCE;
            case seekBraceStop:
                if (value.equals("}")) {
                    state = GDETaggedParserState.allDone;
                    write("}");
                    writeNewLine();
                    return FULL_IGNORED;
                }
                throw new SequenceParserException("Parentese } (followed by linebreak) expected but found something else on line " + lineNumber + ".");
            case allDone:
                return END_READLINE;
        }
        return SEQUENCE; // never reached
    }

    @Override
    protected void lineTmp(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case ID:
                // add up header for id extraction at end of line
                idLine += value;
                break;
            case SEQUENCE:
                // validate sequence
                cleanAndWriteSequence(value);
                break;
            case WRITE:
                lineTmp += value;
                if (lineTmp.length() > MAX_COMMENT_LINE) {
                    throw new SequenceParserException("In order to ensure that the file still can be parsed by the tool BibiServ2 only allows comment lines up to " + MAX_COMMENT_LINE + " characters. Line" + lineNumber + " is longer than this limit.");
                }
                break;
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case ID:
                // add up header for id extraction
                idLine += value;
                // extract id and test for double
                idLine = testIdLine(idLine);
                break;
            case SEQUENCE:
                // validate sequence
                cleanAndWriteSequence(value);
                break;
            case WRITE:
                lineTmp += value;
                writeAndCorrectLine(lineTmp);
                lineTmp = "";
                break;
        }
    }

    private String writeSequence(String sequence, boolean finalize) throws SequenceParserException {
        while (sequence.length() > SEQUENCE_WIDTH) {

            write(sequence.substring(0, SEQUENCE_WIDTH));
            writeNewLine();

            sequence = sequence.substring(SEQUENCE_WIDTH);
        }
        if (finalize) {

            write(sequence);
            write("\"");
            writeNewLine();

        }
        return sequence;
    }

    private String testIdLine(String header) throws SequenceParserException {
        //Remove front statement
        String entry = header.substring(11).trim();
        // Test for qoutes
        if (entry.startsWith("\"")) {
            // leading qoute, now there needs to be a closing qoute
            if (entry.endsWith("\"")) {
                // also an ending bracket
                String id = entry.substring(1, entry.length() - 1);
                if (id.isEmpty()) {
                    throw new SequenceParserException("Empty Id on line " + lineNumber + ".");
                }
                // test if unallowed chars ar in Id 
                Matcher m = NOT_ALLOWED_IN_COMMENT.matcher(id);
                if (m.find()) {
                    throw new SequenceParserException("Id '" + id + "' contains unnallowd chars ( \"{} ).");
                }

                if (ids.contains(id)) {
                    throw new SequenceParserException("Id '" + id + "' exists more than once.");
                }
                ids.add(id);
                // no corrections needed
                write("sequence-ID ");
                write(entry);
                writeNewLine();

                return "";
            }
            // no ending quote, read in id further
            state = GDETaggedParserState.readId;
            return header;
        }

        // no qoutes at all

        Matcher m = NOT_ALLOWED_IN_COMMENT.matcher(entry);
        if (m.find()) {
            throw new SequenceParserException("Id '" + entry + "' contains unnallowd chars ( \"{} ).");
        }
        if (entry.isEmpty()) {
            throw new SequenceParserException("Empty Id on line " + lineNumber + ".");
        }
        if (ids.contains(entry)) {
            throw new SequenceParserException("Id '" + entry + "' exists more than once.");
        }
        ids.add(entry);

        m = FORCE_QUOTE.matcher(entry);
        if (m.find()) {
            // no qoutes but whitespaces nevertheless
            entry = "\"" + entry + "\"";
        }
        write("sequence-ID ");
        write(entry);
        writeNewLine();
        return "";
    }

    private void cleanAndWriteSequence(String value) throws SequenceParserException {

        boolean finalize = false;
        state = GDETaggedParserState.readSequence;
        
        value = value.replace('~', '-');
        
        if (cleanSequenceStart) {
            cleanSequenceStart = false;
            value = value.substring(8).trim();
            write("sequence \"");
            
            finalize = true;
            state = GDETaggedParserState.seekBraceStop;
        }

        if (!inQoute && value.startsWith("\"")) {
            inQoute = true;
            value = value.substring(1);
            finalize = false;
            state = GDETaggedParserState.readSequence;
        }

        if (inQoute && value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
            finalize = true;
            inQoute = false;
            state = GDETaggedParserState.seekBraceStop;
        }

        if (!value.isEmpty() && !SequenceValidator.validate(patternType, value)) {
            throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name());
        }
        sequenceLength += value.length();
        sequenceTmp += value;
        sequenceTmp = writeSequence(sequenceTmp, finalize);
    }

    private void writeAndCorrectLine(String line) throws SequenceParserException {
        // is there a qoute open 
        if (inQoute) {
            Matcher m;
            if (line.endsWith("\"")) {
                // test for unallowed chars
                m = NOT_ALLOWED_IN_COMMENT.matcher(line.substring(0,line.length() - 1));
                inQoute = false;

            } else {
                m = NOT_ALLOWED_IN_COMMENT.matcher(line);
            }
            if (m.find()) {
                throw new SequenceParserException("Comment on line " + lineNumber + "contains unnallowd chars ( \"{} ).");
            }
            write(line);
            writeNewLine();
        } else { // no open qoute
            // split into type and rest
            String[] parts = line.trim().split("\\s+", 2);
            if (parts.length < 2) {
                throw new SequenceParserException("Record without content on line " + lineNumber + ".");
            }

            write(parts[0]);
            write(" ");

            String entry = parts[1];
            if (entry.startsWith("\"")) {
                inQoute = true;
                Matcher m;
                // leading qoute, now there needs to be a closing qoute
                if (entry.length()>1 && entry.endsWith("\"")) { // also an ending bracket
                    inQoute = false;
                    // test if unallowed chars ar in Id 
                    m = NOT_ALLOWED_IN_COMMENT.matcher(entry.substring(1, entry.length() - 1));
                } else {
                    m = NOT_ALLOWED_IN_COMMENT.matcher(entry.substring(1));
                }

                if (m.find()) {
                    throw new SequenceParserException("Comment on line " + lineNumber + "contains unnallowd chars ( \"{} ).");
                }

                // no ending quote, read in id further
                write(entry);
                writeNewLine();
            } else {
                Matcher m = NOT_ALLOWED_IN_COMMENT.matcher(entry);
                if (m.find()) {
                    throw new SequenceParserException("Comment on line " + lineNumber + "contains unnallowd chars ( \"{} ).");
                }
                // no qoutes at all
                m = FORCE_QUOTE.matcher(entry);
                if (m.find()) {
                    // no qoutes but whitespaces nevertheless
                    entry = "\"" + entry + "\"";
                }
                write(entry);
                writeNewLine();
            }
        }
    }
}
