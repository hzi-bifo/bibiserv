/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.validate;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat;

/**
 *
 * @author mrumming
 */
public interface IMsfFiletypeValidator {

    public ValidationResult checkMsfFiletype(String input, InputFormat iFormat, ValidationResult vr);
}
