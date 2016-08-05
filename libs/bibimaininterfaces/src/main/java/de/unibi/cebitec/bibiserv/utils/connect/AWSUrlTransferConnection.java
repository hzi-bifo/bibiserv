
package de.unibi.cebitec.bibiserv.utils.connect;

import com.amazonaws.services.s3.AmazonS3Client;

/**
 * This is a special connection that is only used by the S3TransferInpurBean.
 * It works exactly like a normal AWSValidationConnection, but usage indicates a
 * different behaviour for generating the bash-file.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class AWSUrlTransferConnection extends AWSValidationConnection {
    
    public AWSUrlTransferConnection(AmazonS3Client s3client, String bucket, String file) {
        super(s3client, bucket, file);
    }
    
    public AWSUrlTransferConnection(AWSValidationConnection c){
        super(c.getClient(),c.getBucket(),c.getFile());
    }
}
