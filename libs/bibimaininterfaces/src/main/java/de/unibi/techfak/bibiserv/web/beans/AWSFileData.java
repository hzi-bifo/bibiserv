
package de.unibi.techfak.bibiserv.web.beans;

import java.net.URL;

/**
 * This Object is a container for an aws file. This kind of data can't be saved in a session bean used by every input!
 * @author Thomas Gatter -  tgatter(at)cebitec.uni-bielefel.de
 */
public class AWSFileData {
    
    private String bucket, file;
    private URL s3url;

    public AWSFileData() {
        bucket = "";
        file = "";
        s3url = null;
    }
    
    public URL getS3url() {
        return s3url;
    }

    public void setS3url(URL s3url) {
        this.s3url = s3url;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
    
    
}
