/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010,2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010,2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.dev"
 *
 * Contributor(s): Jan Krueger, Benjamin Paassen
 *
 */
package de.unibi.cebitec.bibiserv.util.visualizer;

import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import de.unibi.techfak.bibiserv.exception.IdNotFoundException;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 * @author Benjamin Paassen
 * 
 * 
 * AbstractVisualizer is a parent-class for all Visualizer-Implementations.
 * If a visualizer does not implement a showThis-method an error is thrown when
 * the method is used.
 */

public abstract class AbstractVisualizer implements Visualizer {

    public BiBiTools initialize(InputStream runnableItem) throws BiBiToolsException, IdNotFoundException, FileNotFoundException {

        BiBiTools tool = new BiBiTools(runnableItem);
        
        return tool;

    }

    @Override
    public String showThis(Object data) throws Exception{
        throw new UnsupportedOperationException("This Visualizer is not implemented yet.");
    }
}
