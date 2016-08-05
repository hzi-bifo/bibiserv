/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.cebitec.bibiserv.sequenceparser.tools;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 *
 * @author rmadsack
 */
public class PatternController {

    private HashMap<PatternType, Pattern> patternList;


    public PatternController() {
        patternList = new HashMap<PatternType, Pattern>();
    }


    public final Pattern getPattern(PatternType seqType){
        if (patternList.get(seqType) == null){
            patternList.put(seqType, compilePattern(seqType));
        }
        return patternList.get(seqType);
    }

    private final Pattern compilePattern(PatternType seqType){
        return Pattern.compile(seqType.getPattern(), Pattern.CASE_INSENSITIVE);
    }



}
