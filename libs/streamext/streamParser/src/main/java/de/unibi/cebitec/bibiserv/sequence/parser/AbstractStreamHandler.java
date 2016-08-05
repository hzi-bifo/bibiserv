
package de.unibi.cebitec.bibiserv.sequence.parser;


import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * This class is there to handle reading the inputStream and writing to the OutputStream
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractStreamHandler {
    
    protected BufferedWriter output;
    protected BufferedReader input;
    protected PatternType patternType;
     
    /**
     * Stores already read data till it can be used.
     */
    protected String readbuffer;
    /**
     * Stores if the stream ended.
     */
    protected boolean streamEnded;
    /**
     * The current line number.
     */
    protected int lineNumber;
    /**
     * The String-Identifier returned by lineBegin that causes readLine to stop
     * immediately.
     */
    protected final static int END_READLINE = -1;
    /**
     * The regex to detect a line.
     */
    protected final static Pattern LINE_REGEX = Pattern.compile("(^.*?)(\\r\\n|\\r|\\n)([\\s\\S]*$)");
    protected LineSplitter splitter;
    
    /**
     * Number of chars read.
     */
    private int length;
    /**
     * Maximum number of chars to read, -1 for endless 
     */
    protected int maxLength = -1;
    
    
    public AbstractStreamHandler(BufferedReader input, BufferedWriter output) {
        this.input = input;
        this.output = output;
        readbuffer = "";
        streamEnded = false;
        lineNumber = 0;
        splitter = new LineSplitter();
        length = 0;
    }
    
    /**
     * Reads in the next line of input calling: lineBeginSize ==> function
     * returning how many chars are needed to identify a line beginning.
     * Non-empty lines below this limit will cause an exception. lineBegin ==>
     * This when a new line starts and lineBeginNumber chars are ready. Returns
     * an Integer-Identifier for the next two functions. lineTmp ==> When Data
     * for this line is waiting but the end is not yet reached this function
     * will be called passing the Integer-Identifier as argument. lineEnd ==>
     * When the end of the line is reached this function will be called passing
     * the Integer-Identifier as argument. lineEmpty ==> Every time an empty
     * line is detected;
     *
     * @return what caused the end of the method
     */
    protected ReadLineMessage readLine() throws SequenceParserException, ForcedAbortOfPartValidation {

        // test if reading is possible
        if (streamEnded && readbuffer.isEmpty()) {
            return ReadLineMessage.noMoreLines;
        }

        // increase current line
        lineNumber++;

        boolean done = false;
        while (!done) {
            // test if already a full line is ready
            
            if (splitter.find(readbuffer)) {
                
                // avoid seperation of line breaks
                if (!streamEnded && splitter.getGroup3().isEmpty() && splitter.getGroup2().equals("\r")) {
                    readbuffer += readInput();
                    continue;
                    // did we read an empty line?
                } else if (splitter.getGroup1().isEmpty()) {
                    lineEmpty();
                    readbuffer = splitter.getGroup3();
                    return ReadLineMessage.lineRead;
                    // none-empty correct line without meaby seperated line break
                } else {
                    
                    // full line, possibly smaller than lineBeginSize!
                    int ident = lineBegin(splitter.getGroup1());
                    // don't-read-the-line message from lineBegin!
                    if (ident == END_READLINE) {
                        return ReadLineMessage.abortByLineBegin;
                    }

                    lineEnd(ident, splitter.getGroup1());
                    readbuffer = splitter.getGroup3();
                    return ReadLineMessage.lineRead;
                }
            }
            // are there enough chars for a lineBegin call?
            if (readbuffer.length() < lineBeginSize()) {
                // not enough chars and no more available?
                if (streamEnded) {
                    if(readbuffer.isEmpty()) {
                        return ReadLineMessage.noMoreLines;
                    }
                    throw new SequenceParserException("Invalid syntax on line " + lineNumber + " :\"" + readbuffer + "\".");
                }
                readbuffer += readInput();
            } else {
                done = true;
            }
        }

        // there is no full line (no line break but possible stream end)
        // and enough data for a lineBegin call at this point

        int ident = lineBegin(readbuffer);
        // don't-read-the-line message from lineBegin!
        if (ident == END_READLINE) {
            return ReadLineMessage.abortByLineBegin;
        }

        // now read till end of line or file
        while (true) {

            // test if end of line is found
            if (splitter.find(readbuffer)) {

                // avoid seperation of line breaks
                if (!streamEnded && splitter.getGroup3().isEmpty() && splitter.getGroup2().equals("\r")) {
                    readbuffer += readInput();
                    continue;
                }

                lineEnd(ident, splitter.getGroup1());
                readbuffer = splitter.getGroup3();
                return ReadLineMessage.lineRead;

            }

            if (streamEnded) {
                lineEnd(ident, readbuffer);
                readbuffer = "";
                return ReadLineMessage.lineRead;
            }
            lineTmp(ident, readbuffer);
            readbuffer = readInput();
        }
    }

    /**
     * Reads the next available chars from input. When stream ended streamEnded
     * is set to true;
     *
     * @return the read data
     * @throws SequenceParserException
     */
    private String readInput() throws SequenceParserException, ForcedAbortOfPartValidation {

        if (streamEnded) {
            return "";
        }

        char[] buffer = new char[1024];
        int length;
        try {
            length = input.read(buffer);
        } catch (IOException ex) {
            throw new SequenceParserException("An IO error occured while validating:", ex);
        }

        if (length != -1) {
            
            this.length += length;
            if(maxLength>0 && this.length>=maxLength) {
                throw new ForcedAbortOfPartValidation();
            }
            
            // add newly read to readbuffer
            return new String(buffer, 0, length);
        }
        streamEnded = true;
        return "";
    }
    
    /**
     * Write string into output if output is set.
     *
     * @param out String to write
     * @throws SequenceParserException
     */
    protected void write(String out) throws SequenceParserException {
        try {
            output.write(out);
        } catch (IOException ex) {
            throw new SequenceParserException("Failed to write to output file while validating:"+ ex);
        }
    }

    /**
     * Write newline into output.
     *
     * @throws SequenceParserException
     */
    protected void writeNewLine() throws SequenceParserException {
        try {
            output.newLine();
        } catch (IOException ex) {
            throw new SequenceParserException("Failed to write to output file while validating:"+ ex);
        }
    }
    
        /**
     * Returns how many chars are needed to identify a line beginning
     *
     * @return
     */
    abstract protected int lineBeginSize();

    /**
     * Called every time an empty line is detected.
     */
    abstract protected void lineEmpty() throws SequenceParserException;

    /**
     * Called when a new non-empty line is detected.
     *
     * @param value String value of the first lineBeginSize() chars or less if
     *              line ended before that.
     * @return an identifier to pass to lineTmp and lineEnd
     */
    abstract protected int lineBegin(String value) throws SequenceParserException;

    /**
     * Called when a line is not fully read in yet but some data is already
     * available.
     *
     * @param ident identifier as return by lineBegin prior to this call.
     * @param value the String that was read up until now
     */
    abstract protected void lineTmp(int ident, String value) throws SequenceParserException;

    /**
     * Called when the line was fully read.
     *
     * @param ident identifier as return by lineBegin prior to this call.
     * @param value the String that was read up until now. This does not have to
     * be the full line, but rather everything read since the last lineTmp call.
     */
    abstract protected void lineEnd(int ident, String value) throws SequenceParserException;
    
}
