package de.unibi.techfak.bibiserv.biodom.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Simple (but better and easier to read) implementions of a "BioDOMFormatter"
 * 
 * 
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 * 
 */
public class BioDOMFormatter extends Formatter {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    @Override
    public String format(LogRecord arg0) {
        return "BIODOM(" + arg0.getLevel() + "):" + arg0.getMessage() + " at {" + arg0.getSourceClassName() + "}"
                + arg0.getSourceMethodName() + System.getProperty("line.separator");
    }

}
