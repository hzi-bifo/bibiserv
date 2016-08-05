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

import java.io.IOException;
import java.io.InputStream;

/**
 * The interface <b>Call</b> defines a basis type for typically
 * tool call from Java. Known Implementing classes are SGECall
 * and LocalCall.
 * 
 * @author Jan Krueger (jkrueger@techfak.uni-bielefeld.de)
 */
public interface Call {

    /**
     * Method call(String) is the simpler variant of method call(String []) 
     * 
     * @see call(String [])
     * 
     * @param exec - Commandline to be executed ...
     * @return Return true if call was succesfull, false otherwise
     */
    public boolean call(String exec);

    /**
     * Method call(String) process/executed the commands given
     * in a String array. The Stdout/Stderr output is redirected in
     * one stream each. The process is run in current wsstools spooldir.
     * 
     * @param exec - Commandline to be executed ...
     * @return Return true if call was succesfull, false otherwise
     */
    public boolean call(String[] exec);

    /**
     * Returns the StdErr stream from the executable.
     * 
     * @return Returns the StdErr stream of the executable.
     * @throws java.io.IOException if stream can't be open or is unreadable ...
     */
    public InputStream getStdErrStream() throws IOException;

    /**
     * Returns the StdOut stream from the executable.
     * 
     * @return Returns the StdOut stream of the executable.
     * @throws java.io.IOException if stream can't be open or is unreadable ...
     */
    public InputStream getStdOutStream() throws IOException;

    /**
     * Set a bibitools objetc to current Call object
     *
     * @param bibitools
     */
    public void setBiBiTools(BiBiTools bibitools);

    /**
     * Set a status object to current Call object.
     *
     * @param status
     */
    public void setStatus(Status status);
}
