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
package de.unibi.cebitec.bibiserv.web.beans.session;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Extended Properties implementation. 
 * - add support to easily handle Properties objects as bean within a Spring Context
 * - add support for extended Properties Syntax with recursive properties resolution like Ant
 *
 * With setting tradional flag to false (which is the default), the ExtendedProperties
 * object support recursive resolution of property values.
 *
 * What does recursive property resolution mean ?
 *
 * Any Pattern '$\{.+?\}' matching the property value will evaluated doing another
 * getProperty([MATCH]) call.
 *
 * @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de
 */
public class ExtendedProperties extends Properties {

    private static final Logger LOG = Logger.getLogger("de.unibi.techfak.bibiserv.web.beans.session.ExtendedProperties");
    private boolean traditional = false;

    @Override
    public String getProperty(String key) {

        String retval = null;

        if (traditional) {
            retval = super.getProperty(key);
        } else {
            // search for ${....} not using RegExp
            if (super.getProperty(key) != null) {
                int state = 0;
                int start = 0;
                int stop = 0;

                char[] c_arr = super.getProperty(key).toCharArray();
                StringBuilder value = new StringBuilder(super.getProperty(key));

                for (int pos = c_arr.length - 1; pos >= 0; --pos) {
                    switch (state) {
                        case 0: {
                            if (c_arr[pos] == '}') {
                                state = 1;
                                stop = pos;
                            }
                            break;
                        }
                        case 1: {
                            if (c_arr[pos] == '{') {
                                state = 2;

                            }
                            break;
                        }
                        case 2: {
                            if (c_arr[pos] == '$') {
                                start = pos;
                                state = 0;
                                String tmp = getProperty(String.valueOf(c_arr, start + 2, stop - start - 2));
                                value.replace(start, stop + 1, tmp == null ? "NULL" : tmp);
                            }
                            state = 0;
                            break;
                        }
                    }
                }
                retval = value.toString();
            }
        }
        return retval;
    }

    /**
     * Extension to use ExtendedProperties easily as (Spring-)Bean. Load Properties
     * from file.
     *
     * @param filename - Name of file containing properties to be be loaded.
     */
    public void setFile(String filename) {
        Reader fir = null;
        try {
            fir = new FileReader(filename);
            load(fir);
        } catch (IOException ex) {
            LOG.error("Could not load properties file: " + ex);
        } finally {
            try {
                if (fir != null) {
                    fir.close();
                }
            } catch (IOException ex) {
                LOG.error("Could not close properties file input reader: " + ex);
            }
        }
    }

    /**
     * Extension to use ExtendedProperties easily as (Spring-)Bean. Load Properties
     * from Resource.
     *
     * @param resourcename - Name of resource containing properties to be loaded.
     */
    public void setResource(String resourcename) {
        InputStream istream = null;
        try {
            istream = getClass().getResourceAsStream(resourcename);
            load(istream);
        } catch (IOException ex) {
            LOG.error("Could not load properties input stream: " + ex);
        } finally {
            try {
                if (istream != null) {
                    istream.close();
                }
            } catch (IOException ex) {
                LOG.error("Could not close properties input stream: " + ex);
            }
        }
    }

    /**
     * Set Extended Properties behaviour. True and ExtendedProperties object
     * act/reacts like Standard Java Properties. False (default) and the
     * extendedProperties notation is used.
     *
     * @param isTraditional - true activates traditional java properties behaviour.
     */
    public void setTraditional(boolean isTraditional) {
        traditional = isTraditional;
    }
}
