
package de.unibi.cebitec.bibiserv.util.streamconvert;

import de.unibi.cebitec.bibiserv.sequence.parser.ConversionParser;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * This contains all converters that are implemented in the streamParser
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractStreamParserConverter extends AbstractStringStreamConverter {
  
     /**
     * Write string into output if output is set.
     * @param output The writer to write to.
     * @param str String to write
     * @throws SequenceParserException
     */
    protected void write(BufferedWriter output, String str) throws SequenceParserException {
        try {
            output.write(str);
        } catch (IOException ex) {
            throw new SequenceParserException("Failed to write to output file while validating:"+ ex);
        }
    }

    /**
     * Write newline into output.
     *
     * @param output The writer to write to.
     * @throws SequenceParserException
     */
    protected void writeNewLine(BufferedWriter output) throws SequenceParserException {
        try {
            output.newLine();
        } catch (IOException ex) {
            throw new SequenceParserException("Failed to write to output file while validating:"+ ex);
        }
    }
    
    abstract protected ConversionParser getParser(BufferedReader input, BufferedWriter output);
    
    @Override
    public void convert(Reader input, Writer output) throws ConversionException {
        
        BufferedReader binput = new BufferedReader(input);
        BufferedWriter boutput = new BufferedWriter(output);
        
        ConversionParser parser = getParser( binput, boutput );
        try {
            parser.convert();
        } catch(SequenceParserException ex) {
            throw new ConversionException("Conversion failed due to a parser error: "+ex.getMessage());
        }
        
        try {
            binput.close();
            boutput.close();
        } catch (IOException ex) {
            throw new ConversionException("Could not close the streams: "+ex); 
        }
       
        
        closeStreams(input, output);
    }
    
}
