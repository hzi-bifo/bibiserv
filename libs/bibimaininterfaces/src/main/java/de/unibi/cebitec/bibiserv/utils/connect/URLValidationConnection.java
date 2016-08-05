
package de.unibi.cebitec.bibiserv.utils.connect;

import de.unibi.cebitec.bibiserv.utils.ValidationConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import de.unibi.cebitec.bibiserv.util.validate.ValidationException;
import java.net.URL;


/**
 *
 * @author gatter
 */
public class URLValidationConnection implements ValidationConnection{


    private BufferedReader reader;
    protected URL url;
    
    
    public URLValidationConnection(URL url){
        this.url = url;
    }
    
    @Override
    public BufferedReader getReader() throws ValidationException{
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            return reader;
        } catch (IOException ex) {
            throw new ValidationException("Could not open input stream. "+ex);
        }
    }


    public String getUrl() {
        return url.toExternalForm();
    }

    @Override
    public void abort() {
        try {
            reader.close();
        } catch (IOException ex) {
           //ignore
        }
    }

}
