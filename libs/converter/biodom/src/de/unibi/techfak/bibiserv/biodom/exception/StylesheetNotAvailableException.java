package de.unibi.techfak.bibiserv.biodom.exception;

/**
 * StylesheetNotAvailableException extends the default
 * Exceptin and should be thrown if an
 * requested XSLT- stylesheet for transforming an XML
 * document from one namespace to another namespace.
 * 
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 *
 */

public class StylesheetNotAvailableException extends Exception {
    
    

    /**
     * 
     */
    private static final long serialVersionUID = 2475243821424598117L;

       
    public StylesheetNotAvailableException(){
        super();
    }
    
    public StylesheetNotAvailableException(String errormessage){
        super(errormessage);
    }
    
}
