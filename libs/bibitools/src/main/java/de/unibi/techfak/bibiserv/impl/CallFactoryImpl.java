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

package de.unibi.techfak.bibiserv.impl;

import de.unibi.techfak.bibiserv.*;
import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Default Implementation of abstract class CallFactory
 * 
 * @author Jan Krueger (jkrueger@techfak.uni-bielefeld.de)
 */
public class CallFactoryImpl extends CallFactory {

    private static Logger log = Logger.getLogger(CallFactoryImpl.class);
    /* A private feature propertieslist to store features*/
    private Properties features = new Properties();

    /**
     * Default Constructor. Should not use in productive environment,
     * use the factory class instead:
     * <code>
     * CallFactory cf = CallFactory.newInstance() ;
     * </code>
     * 
     * This Implemention is default AND fallback implemention of the abstract
     * class @see CallFactory.
     * 
     * Features supported by this implementation :
     * 
     * <table>
     *  <tr>
     *      <th>Feature</th><th>value</th><th>description</th>
     *  </tr>
     *  <tr>
     *      <td>LocalCall</td><td>anything</td>
     *      <td>if set a @see LocalCall object will be returned ... </td>
     *  </tr>
     * </table>
     * 
     */
    public CallFactoryImpl() {
    }

    /**
     * @see CallFactory.newCall(wsstools)
     */
    @Override
    public Call newCall(BiBiTools wsstools) throws BiBiToolsException {
        return newCall(wsstools, wsstools.getStatus());
    }

    /**
     * @see CallFactory.newCall(wsstools,status);
     */
    @Override
    public Call newCall(BiBiTools wsstools, Status status) throws BiBiToolsException {
        String classname = null;
        try {
            // if Property DefaultCallClass is set, return an instance of this class ...
            if (wsstools.getProperty("DefaultCallClass") != null) {
                classname = wsstools.getProperty("DefaultCallClass");
            } else if (getFeature("LocalCall") != null) {
                classname = wsstools.getProperty("LocalCallClass");
            } else {
                classname = wsstools.getProperty("GridCallClass");
            }
            // additional test!
            if (classname == null) {
                throw new BiBiToolsException("Call classname is null! Be sure that properties 'DefaultCallClass', 'LocalCallClass' and 'GridCallClass' are set in bibiserv.properties");
            }
            Call call = (Call) Class.forName(classname).newInstance();
            call.setBiBiTools(wsstools);
            call.setStatus(status);
            return call;
        } catch (NoClassDefFoundError e ) {
            throw new BiBiToolsException("An NoClassDefFoundException occured while creating a new instance of class \"" + classname + "\"", e);
        } catch (ClassNotFoundException e) {
            throw new BiBiToolsException("An ClassNotFoundException occured while creating a new instance of class \"" + classname + "\"", e);
        } catch (IllegalAccessException e) {
            throw new BiBiToolsException("An IllegalAccessException occured while creating a new instance of class \"" + classname + "\"", e);
        } catch (InstantiationException e) {
            throw new BiBiToolsException("An InstantiationException occured while creating a new instance of class \"" + classname + "\"", e);
        }
    }

    /**
     * @see CallFactory.setFeature
     */
    @Override
    public void setFeature(String key, String value) {
        features.setProperty(key, value);

    }

    /**
     * @see CallFactory.getFeature
     */
    @Override
    public String getFeature(String key) {
        return features.getProperty(key);
    }
}
