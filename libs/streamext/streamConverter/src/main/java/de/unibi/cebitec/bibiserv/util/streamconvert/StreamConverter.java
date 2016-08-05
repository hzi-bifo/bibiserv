
package de.unibi.cebitec.bibiserv.util.streamconvert;

import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.SequenceConverter;
import java.io.Reader;
import java.io.Writer;

/**
 * Extends the normal converter in a way that it is possible to convert the stream directly,
 * not returning the converted result, but writing it to file/pipe.
 * 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public interface StreamConverter extends SequenceConverter {
    
    /**
     * Reads in input and writes conversion to BufferedWriter 
     * @param input Input as reader.
     * @param output Output as writer.
     * @throws ConversionException On error.
     */
    public void convert(Reader input, Writer output) throws ConversionException;
    
}
