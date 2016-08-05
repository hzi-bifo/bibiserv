/**
 * 
 */
package de.unibi.techfak.bibiserv.biodom.warning;

import java.util.List;

/**
 * This is the interface for a simple container class
 * that collects and tracks the warnings from various 
 * BioDOM classes. It should be implemented using the
 * delegate design pattern.
 * 
 * @date 2006-02-20
 * @version 1.0
 * @author Kai Loewenthal <kloewent@techfak.uni-bielefeld.de>
 */
public interface BioDOMWarningBoxInterface {

     /**
     * this is the method for appending a warning message from a source class to
     * the warning list
     * 
     * @param source -
     *            the source object
     * @param message -
     *            the warning message string
     */
    void appendWarning(Object source, String message);

    /**
     * this method returns a string[] containing all warnings or null, if there
     * was no warning.
     * 
     * @return List - the warning messages with sources
     */
    List<String> getWarnings();

    /**
     * this method returns a string[] containing all warnings origined from a
     * source class or null if no such warning exists.
     * 
     * @param source -
     *            the origin
     * @return List  - the warning messages with source
     */
    List<String> getWarnings(Class source);
    
    /**
     * this method returns a string[] containing all warnings origined from a
     * source class or null if no such warning exists.
     * 
     * @param source -
     *            the origin object
     * @return List  - the warning messages with source
     */
    List<String> getWarnings(Object source);
    
    /**
     * this method returns a string[] containing all warnings or null, if there
     * was no warning. Can delete the warnings that were returned if you
     * set keep to false.
     * 
     * @param keep  - specifies if the warnings are deleted after method call
     * @return List - the warning messages with sources
     */
    List<String> getWarnings(boolean keep);
    
    /**
     * this method returns a string[] containing all warnings or null if no 
     * warning exists. The format string formats the messages.
     * 
     * @param format - format string for the messages. use #CLASS# as placeholder 
     *                 for classname and #WARNING# for the warning messages
     * @return List  - the warning messages with source
     */    
    List<String> getWarnings(String format);

    
    /**
     * this method returns a string[] containing all warnings origined from a
     * source class or null if no such warning exists. Can delete warnings
     * that were returned if you set keep to false.
     * 
     * @param source -
     *            the origin
     * @param keep   - specifies if the warning are deleted after method call
     * @return List  - the warning messages with source
     */    
    List<String> getWarnings(Class source, boolean keep);

    /**
     * this method returns a string[] containing all warnings origined from a
     * source class or null if no such warning exists. Can delete warnings
     * that were returned if you set keep to false.
     * 
     * @param source -
     *            the origin object
     * @param keep   - specifies if the warning are deleted after method call
     * @return List  - the warning messages with source
     */    
    List<String> getWarnings(Object source, boolean keep);

    /**
     * this method returns a string[] containing all warnings 
     * or null if no warning exists. Can delete warnings
     * that were returned if you set keep to false. The format string formats 
     * the messages.
     * 
     * @param format - format string for the messages. use #CLASS# as placeholder 
     *                 for classname and #WARNING# for the warning messages
     * @param keep   - specifies if the warning are deleted after method call
     * @return List  - the warning messages with source
     */    
    List<String> getWarnings(String format, boolean keep);

    /**
     * this method returns a string[] containing all warnings origined from a
     * source class or null if no such warning exists. The format string formats 
     * the messages.
     * 
     * @param source -
     *            the origin
     * @param format - format string for the messages. use #CLASS# as placeholder 
     *                 for classname and #WARNING# for the warning messages
     * @return List  - the warning messages with source
     */    
    List<String> getWarnings(Class source, String format);

    /**
     * this method returns a string[] containing all warnings origined from a
     * source class or null if no such warning exists. The format string formats 
     * the messages.
     * 
     * @param source -
     *            the origin object
     * @param format - format string for the messages. use #CLASS# as placeholder 
     *                 for classname and #WARNING# for the warning messages
     * @return List  - the warning messages with source
     */    
    List<String> getWarnings(Object source, String format);
    
    /**
     * this method returns a string[] containing all warnings origined from a
     * source class or null if no such warning exists. Can delete warnings
     * that were returned if you set keep to false. The format string formats 
     * the messages.
     * 
     * @param source -
     *            the origin
     * @param format - format string for the messages. use #CLASS# as placeholder 
     *                 for classname and #WARNING# for the warning messages
     * @param keep   - specifies if the warning are deleted after method call
     * @return List  - the warning messages with source
     */    
    List<String> getWarnings(Class source, String format, boolean keep);
    
    /**
     * this method returns a string[] containing all warnings origined from a
     * source class or null if no such warning exists. Can delete warnings
     * that were returned if you set keep to false. The format string formats 
     * the messages.
     * 
     * @param source -
     *            the origin object
     * @param format - format string for the messages. use #CLASS# as placeholder 
     *                 for classname and #WARNING# for the warning messages
     * @param keep   - specifies if the warning are deleted after method call
     * @return List  - the warning messages with source
     */    
    List<String> getWarnings(Object source, String format, boolean keep);

    /**
     * this method deletes all current warnings.
     */
    void clearWarnings();
    
    /**
     * this method deletes all current warnings origined from a
     * source class
     * @param source    - the source class
     */
    void clearWarnings(Class source);

    /**
     * this method deletes all current warnings origined from a
     * source object
     * @param source    - the source object
     */
    void clearWarnings(Object source);

    /**
     * this method returns all the warnings in one string. the warnings are
     * separated by newlines. the method returns null if no warning occured
     * 
     * @return string - all warnings in one sting
     */
    String getMessage();

    /**
     * this method returns all the warnings in one string. the warnings are
     * separated by newlines. the method returns null if no warning occured
     * if you set keep to false, then all returned warnings are deleted
     * 
     * @return string - all warnings in one sting
     */
    String getMessage(boolean keep);
    
    /**
     * this method returns all the warnings origined from the source class in
     * one string. the warnings are separated by newlines. the method returns
     * null if no warning occured
     * 
     * @param source -
     *            the origin of the warnings
     * @return string - all warnings in one sting
     */
    String getMessage(Class source);

    /**
     * this method returns all the warnings origined from the source object in
     * one string. the warnings are separated by newlines. the method returns
     * null if no warning occured
     * 
     * @param source -
     *            the origin object of the warnings
     * @return string - all warnings in one sting
     */
    String getMessage(Object source);

    /**
     * this method returns all the warnings in one string. the warnings are
     * formated according to the format string: #CLASS# will be replaced by the
     * classname, #WARNING# by the warning message. the method returns null if
     * no warning occured
     * 
     * @param format -
     *            format string, use #CLASS# as placeholder for classname,
     *            #WARNING# as placeholder for the warning message
     * @return string - formated string
     */
    String getMessage(String format);

    /**
     * this method returns all the warnings origined from the source class in
     * one string. the warnings are separated by newlines. the method returns
     * null if no warning occured
     * if you set keep to false, then all returned warnings are deleted
     * 
     * @param source -
     *            the origin of the warnings
     * @param keep   - specifies if warnings should be deleted
     * @return string - all warnings in one sting
     */
    String getMessage(Class source, boolean keep);
    
    /**
     * this method returns all the warnings origined from the source object in
     * one string. the warnings are separated by newlines. the method returns
     * null if no warning occured
     * if you set keep to false, then all returned warnings are deleted
     * 
     * @param source -
     *            the origin object of the warnings
     * @param keep   - specifies if warnings should be deleted
     * @return string - all warnings in one sting
     */
    String getMessage(Object source, boolean keep);

    /**
     * this method returns all the warnings in one string. the warnings are
     * formated according to the format string: #CLASS# will be replaced by the
     * classname, #WARNING# by the warning message. the method returns null if
     * no warning occured
     * if you set keep to false, then all returned warnings are deleted
     * 
     * @param format -
     *            format string, use #CLASS# as placeholder for classname,
     *            #WARNING# as placeholder for the warning message
     * @param keep   - specifies if the warnings should be deleted after 
     *                 method call
     * @return string - formated string
     */
    String getMessage(String format, boolean keep);
    
    /**
     * this method returns all the warnings origined from the source class in
     * one string. the warnings are formated according to the format string:
     * #CLASS# will be replaced by the classname, #WARNING# by the warning
     * message. the method returns null if no warning occured
     * 
     * @param source -
     *            the origin of the warnings
     * @param format -
     *            format string, use #CLASS# as placeholder for classname,
     *            #WARNING# as placeholder for the warning message
     * @return string - formated string
     */
    String getMessage(Class source, String format);

    /**
     * this method returns all the warnings origined from the source object in
     * one string. the warnings are formated according to the format string:
     * #CLASS# will be replaced by the classname, #WARNING# by the warning
     * message. the method returns null if no warning occured
     * 
     * @param source -
     *            the origin object of the warnings
     * @param format -
     *            format string, use #CLASS# as placeholder for classname,
     *            #WARNING# as placeholder for the warning message
     * @return string - formated string
     */
    String getMessage(Object source, String format);

    /**
     * this method returns all the warnings origined from the source class in
     * one string. the warnings are formated according to the format string:
     * #CLASS# will be replaced by the classname, #WARNING# by the warning
     * message. the method returns null if no warning occured
     * if you set keep to false, then all returned warnings are deleted
     * 
     * @param source -
     *            the origin of the warnings
     * @param format -
     *            format string, use #CLASS# as placeholder for classname,
     *            #WARNING# as placeholder for the warning message
     * @param keep   - specifies if the warnings should be deleted after 
     *                 method call
     * @return string - formated string
     */
    String getMessage(Class source, String format, boolean keep);

    /**
     * this method returns all the warnings origined from the source object in
     * one string. the warnings are formated according to the format string:
     * #CLASS# will be replaced by the classname, #WARNING# by the warning
     * message. the method returns null if no warning occured
     * if you set keep to false, then all returned warnings are deleted
     * 
     * @param source -
     *            the origin object of the warnings
     * @param format -
     *            format string, use #CLASS# as placeholder for classname,
     *            #WARNING# as placeholder for the warning message
     * @param keep   - specifies if the warnings should be deleted after 
     *                 method call
     * @return string - formated string
     */
    String getMessage(Object source, String format, boolean keep);

    /**
     * returns true if warning occured, false otherwise
     * 
     * @return boolean - see above
     */
    boolean containsWarning();

    /**
     * returns true if warnings origined from the source class occured, false
     * otherwise
     * 
     * @param source -
     *            the origin of the warnings
     * @return boolean - see above
     */
    boolean containsWarning(Class source);

    /**
     * returns true if warnings origined from the source object occured, false
     * otherwise
     * 
     * @param source -
     *            the origin of the warnings
     * @return boolean - see above
     */
    boolean containsWarning(Object source);

    /**
     * counts the contained warnings
     * 
     * @return int - number of warnings
     */
    int countWarnings();

    /**
     * counts the contained warnings origined from the source class
     * 
     * @param source -
     *            the origin of the warnings
     * @return int - number of warnings
     */
    int countWarnings(Class source);
    
    /**
     * counts the contained warnings origined from the source object
     * 
     * @param source -
     *            the origin of the warnings
     * @return int - number of warnings
     */
    int countWarnings(Object source);
}
