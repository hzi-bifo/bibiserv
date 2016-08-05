package de.unibi.cebitec.bibiserv.sequence.parser.fastq;

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
 *
 * @author Thomas Gatter - Converts one fastq-variant to another one
 */
public class FastQToFastaConverter extends AbstractStreamHandler implements ConversionParser{

    private FastQParserState state;

    private int sequenceLength;
    private int qualityLength;
    private boolean firstheader;
    
    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int QUALITY = 2;
    private static final int SEQUENCE = 3;
    private static final int NOTHING = 4;
    private static final int HEADER = 5;

    /**
     * Inherit the super constructor.
     */
    public FastQToFastaConverter(BufferedReader input, BufferedWriter output) {
        super(input, output);
        firstheader = false;
    }

    @Override
    public void convert() throws SequenceParserException {

        sequenceLength = 0;
        qualityLength = 0;
        
        state = FastQParserState.seekSequenceStart;

        ReadLineMessage m;
        try {
            while ((m = readLine()) == ReadLineMessage.lineRead) {
                // just do it, conversion called automatically
            }
        } catch (ForcedAbortOfPartValidation ex) {
             throw new SequenceParserException("Forced abort specified.");
        }
    }

    @Override
    protected int lineBeginSize() {
        // only one char is needed for fastq @ or +)
        return 1;
    }

    @Override
    protected void lineEmpty() {
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {

        switch (state) {
            case seekSequenceStart:
                if (value.startsWith("@")) {
                    state = FastQParserState.readSequence;
                    firstheader = true;
                    return HEADER;
                }
                throw new SequenceParserException("Header needed but found something else on line " + lineNumber + ".");
            case readSequence:
                // + can also appear in quality string, but not in sequence, so first plus is a definite second header
                if (value.startsWith("+")) {
                    state = FastQParserState.readQuality;
                    return NOTHING;
                }
                return SEQUENCE;
            case readQuality:
                // @ can also appear in quality strings
                if (value.startsWith("@") && qualityLength >= sequenceLength) {
                    state = FastQParserState.seekSequenceStart;
                    sequenceLength = 0;
                    qualityLength = 0;
                    return lineBegin(value);
                }
                return QUALITY;
        }
        return SEQUENCE; // never reached
    }

    @Override
    protected void lineTmp(int ident, String value) throws SequenceParserException {
         switch (ident) {
            case SEQUENCE:
                sequenceLength += value.length();
                write(value);
                break;
            case QUALITY:
                qualityLength += value.length();
                break;
            case HEADER:
                if(firstheader) {
                    firstheader = false;
                    value = value.substring(1);
                    write(">");
                }
                write(value);
                break;           
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case SEQUENCE:
                sequenceLength += value.length();
                write(value);
                writeNewLine();
                break;
            case QUALITY:
                qualityLength += value.length();
                break;
            case HEADER:
                if(firstheader) {
                    firstheader = false;
                    value = value.substring(1);
                    write(">");
                }
                write(value);
                writeNewLine();
                break;           
        }
    }
}
