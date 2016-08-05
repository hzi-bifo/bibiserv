package de.unibi.techfak.bibiserv.biodom.exception;

/**
 *  BioDOMException
 *
 *  Thrown if validation fails on cunstructors
 *
 *  @author Henning Mersch <hmersch@techfak.uni-bielefeld.de>
 */
public class BioDOMException extends Exception {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
   *  Constructs a new exception with null as its detail message.
   */  
  public BioDOMException() {
	super();
  }

  /**
   *  Constructs a new exception with the specified detail message.
   *  @param message message String detail description retrieval by Throwable.getMessage() method.
   */  
  public BioDOMException(String message) {
	super(message);
  }

}
