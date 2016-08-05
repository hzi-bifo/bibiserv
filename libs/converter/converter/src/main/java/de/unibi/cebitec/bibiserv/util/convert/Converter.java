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
 * "Portions Copyrighted 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Sven Hartmeier
 *
 */ 

package de.unibi.cebitec.bibiserv.util.convert;

/**
 * Interface for Classes with a converter function.
 * 
 * @author Sven Hartmeier - shartmei(aet)cebitec.uni-bielefeld.de
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public interface Converter {

    /**
     * Converts the given object to another converter-specific type
     * @param fromdata input to be converted
     * @return input converted to the converter-specific type
     * @throws ConversionException input could not be converted 
     */
    Object convert(Object fromdata) throws ConversionException;
    
    /**
     * Simple constant for adding of linebreaks ;-)
     */
    static final String LINEBREAK = System.getProperty("line.separator");
}
