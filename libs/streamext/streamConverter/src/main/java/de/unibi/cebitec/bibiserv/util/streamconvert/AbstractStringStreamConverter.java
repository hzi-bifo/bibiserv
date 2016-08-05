
package de.unibi.cebitec.bibiserv.util.streamconvert;

import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Contains the basic functions that all converter based on strings need.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 * 
 */
public abstract class AbstractStringStreamConverter extends AbstractStreamConverter{

    
    @Override
    public String convert(Object data) throws ConversionException{
        Writer output = new StringWriter();
        if(data instanceof String){
            BufferedReader input = new BufferedReader(new StringReader((String) data));
            convert(input, output);
            return returnOutput(output);
        } else if (data instanceof Reader){
            Reader input = (Reader) data;
            convert(input, output);
            return returnOutput(output);
        }
        throw new ConversionException("Unsupported input type.");
    }
    
}
