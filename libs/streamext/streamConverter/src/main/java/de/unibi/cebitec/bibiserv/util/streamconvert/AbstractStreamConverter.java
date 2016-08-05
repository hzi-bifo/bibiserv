/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.streamconvert;

import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 *
 * @author gatter
 */
public abstract class AbstractStreamConverter implements StreamConverter{

    protected SequenceValidator.CONTENT content;

    @Override
    public void setContent(SequenceValidator.CONTENT content) {
        this.content = content;
    }
 
    protected void closeStreams(Reader input, Writer output) throws ConversionException{
        try {
            input.close();
            output.close();
        } catch (IOException ex) {
            throw new ConversionException("Could not close the streams: "+ex); 
        }
    }
    
    protected String returnOutput(Writer output) throws ConversionException{
            StringWriter w = (StringWriter) output;
            return w.toString();
    }
    
}
