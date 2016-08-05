package de.unibi.techfak.bibiserv.biodom;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBox;
/**
 * An (non theadsafe Implementation of Sax ErrorHandler
 * 
 * @author Jan Krueger <jkrueger@techfak.uni-bielefeld.de>
 * 
 * @version $Id: ValidationErrorHandler.java,v 1.5 2006/07/31 13:26:26 jankrueger Exp $
 */
public class ValidationErrorHandler implements ErrorHandler {
	
	
	
	/**
     *  private static Logger
	 */
	private static Logger log = Logger.getLogger(ValidationErrorHandler.class.getName());
	
    /**
     * private instance of a warning box
     */
	private BioDOMWarningBox warningbox = null;
	
    /**
     * private boolean error 
     */
	private boolean error = false;
    /**
     * private boolean fatal
     */
	private boolean fatal = false;
    
    /**
     * private boolean warn
     */
	private boolean warn = false;
	
    /**
     * default constructor - create a new ErrorHandler with an
     * new WarningBox
     */
	public ValidationErrorHandler(){
		this(new BioDOMWarningBox());
	}
	
    /**
     * constructor - create a new ErrorHandler
     * 
     * @param warningbox
     */
	public ValidationErrorHandler(BioDOMWarningBox warningbox){
		this.warningbox = warningbox;
	}
	
	
    /** 
     * reset the status of 
     *
     */
	public void reset(){
		warn = false;
		error = false;
		fatal = false;
	}
	
    /** 
     * @return True if ErrorHandler is in an error state
     */
	public boolean isErrorState () {
		return error;
	}
    
	/**
     * @return True, if ErrorHandler is in an fatal state
	 */
	public boolean isFatalState(){
		return fatal;
	}
	
    /**
     * 
     * @return True, if ErrorHandler is in an warn state
     */
	public boolean isWarnState(){
		return warn;
	}
	
    /**
     * 
     * @return Return a reference of current used WarningBox
     */
	public BioDOMWarningBox getWarningBox(){
		return warningbox;
	}
	
    /**
     * 
     * @param warningbox - WarnignBox to be set for current ErrorHandler
     */
    public void setWarningBox(BioDOMWarningBox warningbox){
        this.warningbox = warningbox;
    }
	
	
  /**
   * is called in the event of a recoverable error
   * @param e SAXParseException 
 
   */
  public void error(final SAXParseException e) throws SAXParseException {
	  warn = error = true;
	  log.severe(e.getMessage());
      warningbox.appendWarning(this,e.getMessage());
  }
  /**  
   * is called in the event of a non-recoverable error
   * @param e SAXParseException 
   * @throws SAXParseException, if an fatal error occurs
   */
  public void fatalError(final SAXParseException e) throws SAXParseException{
	  warn = error = fatal = true;
	  log.severe(e.getMessage());
	  throw e;
  }
  /**
   * is called in the event of a warning
   * @param e SAXParseException 
   */
  public void warning(final SAXParseException e) throws SAXParseException {
	  warn = true;
	  log.warning(e.getMessage());
	  warningbox.appendWarning(this,e.getMessage());
  }
}
