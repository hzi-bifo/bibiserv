package de.unibi.techfak.bibiserv.biodom.exception;

/**
 *  BioDOMWarning
 *
 *  Thrown if validation fails noncritical
 *
 *  @author Kai Loewenthal <kloewent@techfak.uni-bielefeld.de>
 */
public class BioDOMWarning extends BioDOMException {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
   *  Constructs a new exception with an empty message.
   */  
  public BioDOMWarning() {
    super();
  }

  /**
   *  Constructs a new exception with the specified message.
   *  @param message - message String retrieval by Throwable.getMessage() method.
   */  
  public BioDOMWarning(String message) {
    super(message);
  }

}