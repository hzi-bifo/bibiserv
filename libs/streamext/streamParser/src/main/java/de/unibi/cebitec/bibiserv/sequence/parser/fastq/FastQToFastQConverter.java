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
public class FastQToFastQConverter extends AbstractStreamHandler implements ConversionParser{

    private FastQParserState state;
    private FastQVariants from;
    private FastQVariants to;
    private int sequenceLength;
    private int qualityLength;
    private FastQQualityConverter qualityConverter;
    
    /**
     * constants for lineBegin, lineTmp and lineEnd
     */
    private static final int WRITE = 1;
    private static final int QUALITY = 2;
    private static final int SEQUENCE = 3;

    /**
     * Inherit the super constructor.
     */
    public FastQToFastQConverter(BufferedReader input, BufferedWriter output,
            FastQVariants from, FastQVariants to) {
        super(input, output);
        qualityConverter = new FastQQualityConverter();
        this.from = from;
        this.to = to;
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
                    return WRITE;
                }
                throw new SequenceParserException("Header needed but found something else on line " + lineNumber + ".");
            case readSequence:
                // + can also appear in quality string, but not in sequence, so first plus is a definite second header
                if (value.startsWith("+")) {
                    state = FastQParserState.readQuality;
                    return WRITE;
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
            case WRITE:
                // add up header for id extraction at end of line
                write(value);
                break;
            case SEQUENCE:
                sequenceLength += value.length();
                write(value);
                break;
            case QUALITY:
                value = qualityConverter.convertQuality(value, from, to);
                qualityLength += value.length();
                write(value);
                break;
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        switch (ident) {
            case WRITE:
                // add up header for id extraction at end of line
                write(value);
                writeNewLine();
                break;
            case SEQUENCE:
                sequenceLength += value.length();
                write(value);
                writeNewLine();
                break;
            case QUALITY:
                value = qualityConverter.convertQuality(value, from, to);
                qualityLength += value.length();
                write(value);
                writeNewLine();
                break;
        }
    }
}
