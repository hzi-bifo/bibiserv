
package de.unibi.cebitec.bibiserv.util.streamvalidate;

/**
 * The interface is needed because the amazon connection cannot be reset by closing the stream,
 * as this would force the reader to read till the end. Thus bort must be called directly on the amazon object.
 * @author gatter
 */
public interface StreamConnectionInterface {
     public void abort();
}
