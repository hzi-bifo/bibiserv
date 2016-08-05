/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.techfak.bibiserv.cms.Texample;
import de.unibi.cebitec.bibiserv.util.validate.Validator;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.web.beans.Input;
import javax.faces.event.ActionEvent;

/**
 * This is a simple interface to ensure basic functionality of all Inputs.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public interface InputBeanInterface {
  
    
    // used by tool
    public boolean supportsStreamedInput();

    public boolean isSkipValidation();

    public Input getInput();
    
    public void setInput(Input input);

    public void setValidator(Validator v);
    public Validator getValidator();
    public ValidationResult getValidationResult();
    
    public void determineSourceAndValidate(String[] args, OntoRepresentation target, String functionid);

    public boolean isValid();
    public void setValid(boolean valid);

    public boolean isValidated();
    public void setValidated(boolean validated);

    public void setShowInfo(boolean showInfo);

    public void showInfoAction(ActionEvent event);
    
    public void reset();
    
    public boolean checkAndSet(Texample.Prop p);
    
    /**
     * A function provided to be called to set textual input similar to example,
     * but to be called in a dynamic way for tool-chaining.
     * @param input 
     */
    public void setTextualInput(String input);
    
    /**
     * A function provided to be called to set an input similar to example,
     * however defining a file from another spool directory.
     * This should only be called in a dynamic way for tool-chaining.
     * @param filename  
     */
    public void setFileInput(String filename, String lastToolname);
    
    /**
     * Is called for each function using this input.
     * @param functionid id of the calling function
     */
    public void register(String functionid);
     /**
     * Is called at the death of a function using this input.
     * @param functionid id of the calling function
     */
    public void unregister(String functionid);
    
    // for formatchooser
    public String getChosenInput();
     
    public void setChosenInput(String chosenInput);
    
    public void inputchange();

}
