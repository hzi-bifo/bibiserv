package de.unibi.cebitec.bibiserv.util.streamvalidate.impl;

import de.unibi.cebitec.bibiserv.util.streamvalidate.AbstractStringStreamValidator;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * This is just a dummy. TODO: actually write this!
 *
 * @author gatter
 */
public class GTF_Validator extends AbstractStringStreamValidator {

    @Override
    public ValidationResult validateThis(BufferedReader input, BufferedWriter output) {
        if(!setMaxRead) {
            try {
                char[] buffer = new char[1024];
                int len;
                while ((len = input.read(buffer)) >= 0) {
                    output.write(buffer, 0, len);
                }
            } catch (IOException ex) {
                return closeStreamsAndReturnValidationResult(input, output, false, "An IO exception occured while validating.");
            }
        }
        return closeStreamsAndReturnValidationResult(input, output, true, "Always true. This is a dummy validator.");
    }
}
