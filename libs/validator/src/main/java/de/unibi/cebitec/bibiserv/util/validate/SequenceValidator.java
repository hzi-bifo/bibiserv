/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de "
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.validate;

/**
 * Extends a validator with sequence properties
 * 
 * 
 * @author Jan Krueger -  jkrueger(at)cebitec.uni-bielefeld.de
 */
public interface SequenceValidator extends Validator {

    public enum STRICTNESS {

        strict, ambiguous
    };

    public enum CARDINALITY {

        single, multi, honeybadger
    }

    public enum CONTENT {

        NA, DNA, RNA, AA
    }

    /**
     * Return how "strict" the sequence data will be validated.
     * 
     * @return Strictness value;
     */
    public STRICTNESS getStrictness();
    
    
    /**
     * Set how "strict" the sequence data will be validated.
     * 
     * @param Strictness value
     */
    public void setStrictness(STRICTNESS strictness);
    
    

    /**
     * Return the cardinality value of the sequence data
     * 
     * @return Cardinality value
     */
    public CARDINALITY getCardinality();
    
    
    /** 
     * Set the cardinality value of the sequence data
     * 
     * @param Cardinality value
     */
    public void setCardinality(CARDINALITY cardinality);

    /**
     * Return the base content type  the sequence data is validated against.
     * 
     * @return Content value
     */
    public CONTENT getContent();

    
    /**
     * Set the base content type  the sequence data is validated against.
     * 
     * @param Content value
     */
    public void setContent(CONTENT content);
    
}
