
package de.unibi.cebitec.bibiserv.utils;

import de.unibi.cebitec.bibiserv.util.streamvalidate.StreamConnectionInterface;
import de.unibi.cebitec.bibiserv.util.validate.ValidationException;
import java.io.BufferedReader;


/**
 * This is a wrapper for URL connections and S3Objects to
 * @author gatter
 */
public interface ValidationConnection extends StreamConnectionInterface{
    
    public BufferedReader getReader() throws ValidationException; 

}
