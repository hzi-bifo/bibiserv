
package de.unibi.cebitec.bibiserv.utils.connect;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import de.unibi.cebitec.bibiserv.utils.ValidationConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import de.unibi.cebitec.bibiserv.util.validate.ValidationException;
import java.io.IOException;

/**
 *
 * @author gatter
 */
public class AWSValidationConnection implements ValidationConnection{
    
     private AmazonS3Client client;
     private S3ObjectInputStream stream;
     private String bucket, file;
    
    public AWSValidationConnection(AmazonS3Client s3client, String bucket, String file){
        client = s3client;
        this.bucket = bucket;
        this.file = file;
    }
    
    @Override
    public BufferedReader getReader() throws ValidationException {
        try {
            S3Object connection = client.getObject( new GetObjectRequest(bucket, file));
            stream = connection.getObjectContent();    
        } catch ( AmazonClientException ase) {
            throw new ValidationException("Could not open input stream.");
        }
        return new BufferedReader(new InputStreamReader(stream));
    }
    
    public String getBucket() {
        return bucket;
    }

    public String getFile() {
        return file;
    }

    public AmazonS3Client getClient() {
        return client;
    }
    
    @Override
    public void abort() {
        stream.abort();
    }
    
}
