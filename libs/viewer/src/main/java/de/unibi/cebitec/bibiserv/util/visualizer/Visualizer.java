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

package de.unibi.cebitec.bibiserv.util.visualizer;

/**
 * Visualizer interface. All implemeneting Visualizer-classes must have an
 * "showThis" method.
 *
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 */
public interface Visualizer {

    /**
     * Takes the given data and returns a block of HTML containing
     * the visualization results (e.g. images or applets)
     *
     * @param data - data to be visualized
     * @return HTML code block for result display
     * */
    public String showThis(Object data) throws Exception;

}
