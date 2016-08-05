/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */ 

package de.unibi.techfak.bibiserv;

import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import de.unibi.techfak.bibiserv.impl.CallFactoryImpl;
import org.apache.log4j.Logger;

/**
 * An CallFactory can be used to create @see Call objects.
 * @author jkrueger
 */
public abstract class CallFactory {

    /** private static instance of a logger (log4j)*/
    private static Logger log = Logger.getLogger(CallFactory.class);
    /** system property which declares the name of implementing Factory class */
    private static final String property = "de.unibi.techfak.bibiserv.CallFactory";

    /**
     * Default Constructor
     */
    protected CallFactory() {
    }

    /**
     * Abstract method newCall(). Returns a Call object. Must be implemented
     * by the extending class. Uses the status object defined within the wsstools
     * object, to give feedback about the call.
     * 
     * @param wsstools - A reference to an wsstools object.
     * @return A new Call object
     *
     * @throws a BiBitoolsException in the case that a Call object can't be instantiated
     */
    abstract public Call newCall(BiBiTools wsstools) throws BiBiToolsException;

    abstract public Call newCall(BiBiTools wsstools, Status status) throws BiBiToolsException;

    /**
     * Set a Feature, a Call object must fullfill. What kind of features are
     * supported by the CallFactory depends on the implemenation.
     * 
     * @param key - name(key) of the feature
     * @param value - value of the feature
     */
    abstract public void setFeature(String key, String value);

    /**
     * Get a Fetaure. @see setFeature(String,String);
     * @param key Key of of the Feature to be returned
     * @return value or null if key not present
     */
    abstract public String getFeature(String key);

    /** Create a new instance of an CallFactory object. Which implemention
     *  of the abstract class ClassFactory is used, depends on the system
     *  property named "de.unibi.techfak.bibiserv.CallFactory".
     *  If this property is not set, as fallback the default implementation
     *  is used.
     * 
     * @return Returns a new CallFactory instance.
     */
    public static CallFactory newInstance() {
        String factoryclassname = System.getProperty(property);

        if (factoryclassname != null) {
            try {
                return (CallFactory) Class.forName(factoryclassname).newInstance();

            } catch (ClassNotFoundException e) {
                log.error(factoryclassname + " not found!\n" + e.getMessage());
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            } catch (InstantiationException e) {
                log.error(factoryclassname + " can't be instantiated!\n" + e.getMessage());
            }
        } else {
            log.warn("System Property " + property + " not set!");
        }
        log.info("... continue using the fallback implementation ...");
        return (new CallFactoryImpl());
    }
}
