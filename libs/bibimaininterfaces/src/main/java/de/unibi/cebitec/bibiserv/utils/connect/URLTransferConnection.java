
package de.unibi.cebitec.bibiserv.utils.connect;

import java.net.URL;

/**
 * This is a special connection that is only used by the S3TransferInpurBean.
 * It works exactly like a normal URLValidationConnection, but usage indicates a
 * different behaviour for generating the bash-file.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class URLTransferConnection extends URLValidationConnection {
    
    public URLTransferConnection(URL url) {
        super(url);
    }
    
    public URLTransferConnection(URLValidationConnection c){
        super(c.url);
    }
}
