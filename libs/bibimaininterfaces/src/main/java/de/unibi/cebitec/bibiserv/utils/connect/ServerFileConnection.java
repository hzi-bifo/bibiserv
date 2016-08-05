
package de.unibi.cebitec.bibiserv.utils.connect;

import de.unibi.cebitec.bibiserv.util.validate.ValidationException;
import de.unibi.cebitec.bibiserv.utils.ValidationConnection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * A connection
 * @author Thomas Gatter - tgatter(at)cebitec.uni-beielefeld.de
 */
public class ServerFileConnection implements ValidationConnection{
    
    private BufferedReader reader;
    private String uri; 

    public ServerFileConnection(String uri){
        this.uri = uri;
    }
    
    
    @Override
    public BufferedReader getReader() throws ValidationException {
        try {
            reader = new BufferedReader(new FileReader(uri));
            return reader;
        } catch (IOException ex) {
            throw new ValidationException("Could not open input stream. "+ex);
        }
    }

    @Override
    public void abort() {
        try {
            reader.close();
        } catch (IOException ex) {
           //ignore
        }
    }
    
    public String getUri() {
        return uri;
    }
}
