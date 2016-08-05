/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.validate.impl;


import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat;
import de.unibi.cebitec.bibiserv.util.validate.IMsfFiletypeValidator;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;

/**
 *
 * @author mrumming
 */
public class MsfFiletypeValidator implements IMsfFiletypeValidator {

    public ValidationResult checkMsfFiletype(String input, InputFormat iFormat, ValidationResult vr) {
        switch (iFormat) {
            case MSF_PileUp:
                if (!input.trim().startsWith("PileUp")) {
                    vr = new ValidationResult(false, "Input is not in MSF/PileUp format.");
                }
                break;
            case MSF_Rich:
                if (!input.trim().startsWith("!!")) {
                    vr = new ValidationResult(false, "Input is not in MSF/Rich Sequence format.");
                }
                break;
            case MSF_Plain:
                if (!input.trim().startsWith("MSF")) {
                    vr = new ValidationResult(false, "Input is not in MSF/Plain format.");
                }
                break;
        }
        return vr;
    }
}
