/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.streamvalidate;

import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that already contains the generation of a buffered reader from strings.
 * 
 * @author Thomas Gatter - tgatter@cebitec.uni-bielefeld.de
 */
public abstract class AbstractStreamValidator implements StreamValidator{
    
    protected int maxRead;
    protected StreamConnectionInterface connection;
    protected boolean setMaxRead = false;
    
    /**
     * This has to be set in every ValidationResult validateThis(Object data) call.
     */
    StringWriter stringWriter = null;
 
    protected ValidationResult closeStreamsAndReturnValidationResult(BufferedReader input, BufferedWriter output, boolean valid, String message){
        return closeStreamsAndReturnValidationResult(input, output, valid, message, new ArrayList<String>());
    }
    
    protected ValidationResult closeStreamsAndReturnValidationResult(BufferedReader input, BufferedWriter output, boolean valid, String message, List<String> warnings){
        try {
            if(setMaxRead) {
                connection.abort();
            } else {
                input.close();
            }
            output.close();
            return new ValidationResult(valid, message, warnings);
        } catch (IOException ex) {
            return new ValidationResult(false, "An IO error occured while validating:"+ex, warnings);
        }
    }
    
    @Override
    public ValidationResult validateThis(Object input, int maxRead, StreamConnectionInterface connection){
        this.maxRead = maxRead;
        setMaxRead = true;
        this.connection = connection;
        return validateThis(input);
    }
    
    @Override
    public String getRepairedAndUnifiedOutput() throws ValidationException {
        if(stringWriter == null){
            throw new ValidationException("Writer was not set up. Irregular call of getRepairedAndUnifiedOutput function.");
        }
        return stringWriter.toString();
    }

}
