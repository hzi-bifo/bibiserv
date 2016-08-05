package de.unibi.techfak.bibiserv.biodom;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Schema;

import org.w3c.dom.Document;

import de.unibi.techfak.bibiserv.biodom.exception.BioDOMException;
import de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBox;

public interface AbstractBioDOMInterface {

	/**
	 * change LogLevel of internal Logger - DEBUG if use default logger
	 * 
	 * @param level for logging the processing
	 */
	public abstract void setLogLevel(Level level);

	/**
	 * validates a DOM Document
	 * @return boolean true if success
	 */
	public abstract boolean validate();

	
	/**
	 * validates a DOM Document against a given XML Schema
	 * 
	 * @param xsd
	 *            URL of the XML Schema for validating against.
	 * @return boolean true if success
	 */
	public abstract boolean validate(URL xsd);
	
	/**
	 * validates a DOM Document against a given XML Schema
	 * 
	 * @param is
	 *            InputStream containing the XML Schema data for validating against.
	 * @return boolean true if success
	 */
	public abstract boolean validate(InputStream is);

	/**
	 * converts a String (maybe read from a file) to a DOM Object
	 * 
	 * @param xmlstring XML to convert to DOM
	 * @exception BioDOMException if String is not proper XML
	 */
	public abstract void setDom(String xmlstring) throws BioDOMException;

	/**
	 * give new instance of DOM
	 * @throws Throws BioDOMException on failure.
	 */
	public abstract void setDom(Document submitted_dom) throws BioDOMException;

	/**
	 * get current DOM (without validation)
	 * @throws Throws BioDOMException on failure.
	 */
	public abstract Document getDom() throws BioDOMException;
	
	/**
	 * get current DOM
	 * @param validate - validate dom
	 * @throws Throws BioDOMException on failure.
	 */
	public abstract Document getDom( boolean validate ) throws BioDOMException;

	/**
	 * Returns a String representation of current DOM object.
	 * 
	 * @return Returns a String representation of current DOM object.
	 * 
	 */
	public abstract String toString();
	
    /**
     * @return Returns the warningBox.
     */
    public abstract BioDOMWarningBox getWarningBox();

    /**
     * @param warningBox The warningBox to set.
     */
    public abstract void setWarningBox(BioDOMWarningBox warningBox);
    
    /**
     * Returns the namespace of currently used BioDOM object
     */
    public abstract String getNS();
    
    /**
     * Returns the (remote) namespace location as String.
     * @return Returns the namespace location.
     */
    public abstract String getNSlocation();
    
    /**
     * Returns the Schema for current BioDOM object.
     * @return Returns the Schema for current BioDOM object.
     */
    public abstract Schema getSchema();
    
    
    /**
     * Returns a DocumentBuilder (validating, namespaceaware, schema set) instance of current BioDOM object
     * @return Returns a DocumentBuilder instance of current BioDOM object
     */
    public abstract DocumentBuilder getDocumentBuilder();
    
    
}