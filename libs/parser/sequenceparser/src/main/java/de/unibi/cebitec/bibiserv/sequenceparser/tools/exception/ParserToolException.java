package de.unibi.cebitec.bibiserv.sequenceparser.tools.exception;

/**
 *  ParserToolException
 *
 *  Thrown if validation fails on cunstructors
 *
 *  @author Thomas Gatter <tgatter@cebitec.uni-bielefeld.de>
 */
public class ParserToolException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     *  Constructs a new exception with null as its detail message.
     */
    public ParserToolException() {
        super();
    }

    /**
     *  Constructs a new exception with the specified detail message.
     *  @param message message String detail description retrieval by Throwable.getMessage() method.
     */
    public ParserToolException(String message) {
        super(message);
    }

    public ParserToolException(String message, Throwable t) {
        super(message, t);
    }

    public ParserToolException(Throwable t) {
        super(t);
    }
}
