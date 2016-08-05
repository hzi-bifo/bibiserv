package de.unibi.cebitec.bibiserv.util.validate.exception;

/**
 *  JAXBManager
 *
 *  Thrown if validation fails on cunstructors
 *
 *  @author Thomas Gatter <tgatter@cebitec.uni-bielefeld.de>
 */
public class JAXBManagerException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     *  Constructs a new exception with null as its detail message.
     */
    public JAXBManagerException() {
        super();
    }

    /**
     *  Constructs a new exception with the specified detail message.
     *  @param message message String detail description retrieval by Throwable.getMessage() method.
     */
    public JAXBManagerException(String message) {
        super(message);
    }

    public JAXBManagerException(String message, Throwable t) {
        super(message, t);
    }

    public JAXBManagerException(Throwable t) {
        super(t);
    }
}