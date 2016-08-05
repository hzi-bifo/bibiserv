package de.unibi.cebitec.bibiserv.sequence.parser.fasta;

import de.unibi.cebitec.bibiserv.sequence.parser.AbstractStreamHandler;
import de.unibi.cebitec.bibiserv.sequence.parser.ConversionParser;
import de.unibi.cebitec.bibiserv.sequence.parser.ForcedAbortOfPartValidation;
import de.unibi.cebitec.bibiserv.sequence.parser.ReadLineMessage;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The parser to read all fasta types.
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class FastaToAllConverter extends AbstractStreamHandler implements ConversionParser {

    private FastaParserState state;
    private String headerTmp;
    
    private FastaToAllConverterCallback callback;
    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int IGNORE = 1;
    private static final int HEADER = 2;
    private static final int SEQUENCE = 3;
    private boolean beginSequence;

    /**
     * Inherit the super constructor.
     */
    public FastaToAllConverter(BufferedReader input, BufferedWriter output,
            FastaToAllConverterCallback callback) {
        super(input, output);
        this.callback = callback;
        headerTmp = "";
        beginSequence = false;
    }

    @Override
    public void convert() throws SequenceParserException {

        callback.fileBegin(output);
        state = FastaParserState.seekSequenceStart;
        
        ReadLineMessage m;
        try {
            while ((m = readLine()) == ReadLineMessage.lineRead) {
                // just do it, conversion is called automatically
            }
        } catch (ForcedAbortOfPartValidation ex) {
            throw new SequenceParserException("Forced abort specified.");
        }
        callback.endSequence(output);
        callback.fileEnd(output);
    }

    @Override
    protected int lineBeginSize() {
        // only one char is needed for fasta (; or >)
        return 1;
    }

    @Override
    protected void lineEmpty() {
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {

        // comment line
        if (value.startsWith(";")) {
            return IGNORE;
        }

        switch (state) {
            case seekSequenceStart:
                if (value.startsWith(">")) {
                    state = FastaParserState.readSequence;
                    return HEADER;
                }
                throw new SequenceParserException("Header needed but found something else on line " + lineNumber + ".");
            case readSequence:
                if (value.startsWith(">")) {
                    state = FastaParserState.seekSequenceStart;
                    callback.endSequence(output);
                    return lineBegin(value);
                }
                if(beginSequence) {
                    callback.beginSequence(output);
                    beginSequence = false;
                } else {
                    writeNewLine();
                }
                return SEQUENCE;
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
                callHeader(headerTmp);
                headerTmp = "";
                beginSequence = true;
                break;
            case SEQUENCE:
                write(value);
                break;
        }

    }

    private void callHeader(String header) throws SequenceParserException {
        String[] parts = header.substring(1).split("\\s", 2);
        String id = parts[0];
        if (id.isEmpty()) {
            throw new SequenceParserException("Empty Id on line " + lineNumber + ".");
        }
        callback.idFound(output, id);
        
        String extra="";
        if(parts.length>1) {
            extra = parts[1];
        }
        callback.headerExtraInfoFound(output, extra);
    }

}
