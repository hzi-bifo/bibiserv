/**
 * 
 */
package de.unibi.techfak.bibiserv.biodom.warning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is a simple container that collects and tracks 
 * warnings from various BioDOM classes. It uses the delegate
 * design pattern.
 * 
 * @date 2006-02-21
 * @version 1.0
 * @author Kai Loewenthal <kloewent@techfak.uni-bielefeld.de>
 */
public class BioDOMWarningBox implements BioDOMWarningBoxInterface {
	
	/**
	 * default format string
	 */
	public final static String DEFAULTFORMAT = "#CLASS# - #WARNING#" + System.getProperty("line.separator");
	
	/**
	 * synonym for true
	 */
	public final static boolean KEEP = true;
	
	/**
	 * synonym for false
	 */
	public final static boolean CLEAR = false;
	
	/**
	 * this is the hashmap where the warnings are stored in.
	 * key is the class, value is an ArrayList containing the warnings
	 */
	private Map<Class, List<String>> warningHash;
	
	public BioDOMWarningBox(){
		warningHash = new Hashtable<Class, List<String>>();
	}
	
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#appendWarning(java.lang.Object, java.lang.String)
	 */
	public void appendWarning(final Object source, final String message) {
		// get class
		final Class sourceclass = source.getClass();
		// empty List
		List<String> warningList = null;
		// if class is already a key -> just get the List
		if(warningHash.containsKey(sourceclass)){
			warningList = warningHash.get(sourceclass);
			// else add the List with class as a key
		}else{
			warningList = new ArrayList<String>();
			warningHash.put(sourceclass, warningList);
		}
		// add message to List
		warningList.add(message);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings()
	 */
	public List<String> getWarnings() {
		return getWarnings(null, DEFAULTFORMAT, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(java.lang.Class)
	 */
	public List<String> getWarnings(final Class source) {
		return getWarnings(source, DEFAULTFORMAT, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(java.lang.Object)
	 */
	public List<String> getWarnings(final Object source) {
		return getWarnings(source.getClass(), DEFAULTFORMAT, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(boolean)
	 */
	public List<String> getWarnings(final boolean keep) {
		return getWarnings(null, DEFAULTFORMAT, keep);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(java.lang.String)
	 */
	public List<String> getWarnings(final String format) {
		return getWarnings(null, format, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(java.lang.String, boolean)
	 */
	public List<String> getWarnings(final String format, final boolean keep) {
		return getWarnings(null, format, keep);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(java.lang.Class, java.lang.String)
	 */
	public List<String> getWarnings(final Class source, final String format) {
		return getWarnings(source, format, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(java.lang.Object, java.lang.String)
	 */
	public List<String> getWarnings(final Object source, final String format) {
		return getWarnings(source.getClass(), format, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(java.lang.Class, boolean)
	 */
	public List<String> getWarnings(final Class source, final boolean keep) {
		return getWarnings(source, DEFAULTFORMAT, keep);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(java.lang.Object, boolean)
	 */
	public List<String> getWarnings(final Object source, final boolean keep) {
		return getWarnings(source.getClass(), DEFAULTFORMAT, keep);    
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(java.lang.Object, java.lang.String, boolean)
	 */
	public List<String> getWarnings(final Object source, final String format, final boolean keep) {
		return getWarnings(source.getClass(), format, keep);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getWarnings(java.lang.Class, java.lang.String, boolean)
	 */
	public List<String> getWarnings(final Class source, String format, final boolean keep) {
		// set format if required
		if((format == null) ||( format.equals(""))){
			format = DEFAULTFORMAT;
		}
		
		// Here we check if the request is for a specific Class or just for all warnings.
		// If a source class is given, we show only the warnings from that class
		if (source != null){
			// Here we check if there are any warnings from the given source class present.
			// If not, we just return 'null'...
			if(!warningHash.containsKey(source)) return null;
			// ... otherwise, we process the request
			final List<String> retList = formatWarnings(format, warningHash.get(source), source);
			// deleting old warnings if required
			if(!keep){
				clearWarnings(source);
			}
			return retList;
		}
		// If we reach this point, no source calss was given,
		// so we have to show ALL warnings
		// get class keys from hash
		final Set<Class> classKeys = warningHash.keySet();
		// If there are no classKeys present, we have no warnings and can return 'null'
		if((classKeys == null) || (classKeys.size() == 0)) return null;     	
		// converting to array
		final Class[] keys = classKeys.toArray(new Class[classKeys.size()]);
		final List<String> retList = new ArrayList<String>();
		// merging the arraylists
		for(int i = 0; i < keys.length; i++){
			retList.addAll(formatWarnings(format, warningHash.get(keys[i]), keys[i]));
		}
		// deleting old warnings if required
		if(!keep){
			clearWarnings();
		}
		// return result list
		return retList;
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage()
	 */
	public String getMessage() {
		return getMessage(null, DEFAULTFORMAT, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(java.lang.Object)
	 */
	public String getMessage(final Object source) {
		return getMessage(source.getClass(), DEFAULTFORMAT, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(java.lang.Class)
	 */
	public String getMessage(final Class source) {
		return getMessage(source, DEFAULTFORMAT, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(java.lang.String)
	 */
	public String getMessage(final String format) {
		return getMessage(null, format, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(boolean)
	 */
	public String getMessage(final boolean keep) {
		return getMessage(null, DEFAULTFORMAT, keep);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(java.lang.Object, java.lang.String)
	 */
	public String getMessage(final Object source, final String format) {
		return getMessage(source.getClass(), format, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(java.lang.Class, java.lang.String)
	 */
	public String getMessage(final Class source, final String format) {
		return getMessage(source, format, KEEP);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(java.lang.Object, boolean)
	 */
	public String getMessage(final Object source, final boolean keep) {
		return getMessage(source.getClass(), DEFAULTFORMAT, keep);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(java.lang.Class, boolean)
	 */
	public String getMessage(final Class source, final boolean keep) {
		return getMessage(source, DEFAULTFORMAT, keep);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(java.lang.String, boolean)
	 */
	public String getMessage(final String format, final boolean keep) {
		return getMessage(null, format, keep);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(java.lang.Object, java.lang.String, boolean)
	 */
	public String getMessage(final Object source, final String format, final boolean keep) {
		return getMessage(source.getClass(), format, keep);
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#getMessage(java.lang.Class, java.lang.String, boolean)
	 */
	public String getMessage(final Class source, final String format, final boolean keep) {
		// get all warnings in an ArrayList
		final List<String> warningList = getWarnings(source, format, keep);
		// If it is  null, then we had no warnings and return 'null'
		if(warningList == null) return null;
		// ...otherwise, we prepare the warning string and return it
		// append warnings
		String warningString = "";
		for(int i = 0; i < warningList.size(); i++){
			warningString = warningString + warningList.get(i);
		}
		// return warnings
		return warningString;
		
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#containsWarning()
	 */
	public boolean containsWarning() {
		// If the number of all of warnings is > 0, we return 'true' and leave
		if (countWarnings() > 0) return true;
		//otherwise, we return false
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#containsWarning(java.lang.Object)
	 */
	public boolean containsWarning(final Object source) {
		return containsWarning(source.getClass());
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#containsWarning(java.lang.Class)
	 */
	public boolean containsWarning(final Class source){
		// If the number of warnings for this class is > 0, we return 'true' and leave
		if (countWarnings(source) > 0) return true;
		//otherwise, we return false
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#countWarnings()
	 */
	public int countWarnings() {
		//  getting class keys from hash
		final Set<Class> classKeys = warningHash.keySet();
		int counter = 0;
		if((classKeys != null)&&(classKeys.size() > 0)){
			// converting to array
			final Class[] keys = classKeys.toArray(new Class[classKeys.size()]);
			List<String> tmpList = null;
			// merging the arraylists
			for(int i = 0; i < keys.length; i++){
				tmpList = warningHash.get(keys[i]);
				if((tmpList != null)&&(tmpList.size() > 0)){
					counter = counter + tmpList.size();
				}
			}
		}
		return counter;
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#countWarnings(java.lang.Object)
	 */
	public int countWarnings(final Object source) {
		return countWarnings(source.getClass());
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#countWarnings(java.lang.Class)
	 */
	public int countWarnings(final Class source) {
		if(warningHash.containsKey(source)){
			final List<String> warnings = warningHash.get(source);
			if((warnings != null)&&(warnings.size() > 0)){
				return warnings.size();
			}
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#clearWarnings()
	 */
	public void clearWarnings() {
		warningHash = new Hashtable<Class, List<String>>();
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#clearWarnings(java.lang.Object)
	 */
	public void clearWarnings(final Object source) {
		clearWarnings(source.getClass());   
	}
	
	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBoxInterface#clearWarning(java.lang.Class)
	 */
	public void clearWarnings(final Class source){
		if(warningHash.containsKey(source)){
			warningHash.remove(source);
		}
	}
	
	///////////////////////////////////////
	// private methods
	///////////////////////////////////////
	
	/**
	 * private method that formats a single string acording to the format 
	 * string. 
	 * @param format        - the format string. use #CLASS# and #WARNING#
	 *                        as placeholder for classname and warnings
	 * @param warning       - the string that should be formated
	 * @param source        - the Class from which the warning origins
	 * @return String       - the formated string
	 */
	private String formatWarning(final String format, final String warning, final Class source){
		String retString = new String(format);
		retString = retString.replaceAll("#CLASS#", source.getName());
		retString = retString.replaceAll("#WARNING#", warning);
		return retString;
	}
	
	/**
	 * private method that formats a List of Strings acording to the format string
	 * @param format        - the format string. use #CLASS# and #WARNING# as 
	 *                        placeholder for classname and warnings
	 * @param warnings      - the List of Strings that should be formated
	 * @param source        - the source class
	 * @return List<String> - the formated string
	 */
	private List<String> formatWarnings(final String format, final List<String> warnings, final Class source){
		final List<String> retList = new ArrayList<String>();
		for(int i = 0; i < warnings.size(); i++){
			retList.add(formatWarning(format, warnings.get(i), source));
		}
		return retList;
	}
}
