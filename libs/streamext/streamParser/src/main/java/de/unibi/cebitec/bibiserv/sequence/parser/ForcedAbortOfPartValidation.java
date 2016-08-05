
package de.unibi.cebitec.bibiserv.sequence.parser;

/**
 * This Exception gets thrown when only the first X chars to to be validated. 
 * It will be thrown exactly when the char X was read and NOT when it was validated.
 * Since only new data is read when the previous chars are validated this means that
 * always about X-Y chars are indeed validated and Y much smaller X.
 * WARNING: The exact amount of read chars is not deterministic, but rather depends
 * on the availability in the stream and the data itself.
 * 
 * @author gatter
 */
public class ForcedAbortOfPartValidation extends Exception {

    /**
     * Creates a new instance of
     * <code>ForcedAbortOfPartValidation</code> without detail message.
     */
    public ForcedAbortOfPartValidation() {
    }

    /**
     * Constructs an instance of
     * <code>ForcedAbortOfPartValidation</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public ForcedAbortOfPartValidation(String msg) {
        super(msg);
    }
}
