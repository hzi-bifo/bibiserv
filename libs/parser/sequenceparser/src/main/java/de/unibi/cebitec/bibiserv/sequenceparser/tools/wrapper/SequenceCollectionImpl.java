/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.exception.ParserToolException;
import java.util.LinkedHashMap;

/**
 *
 * @author Richard
 */
public class SequenceCollectionImpl extends LinkedHashMap<String, SequenceTypeInterface> implements SequenceCollection {

    public SequenceCollectionImpl() {
        super();
    }

    public PatternType getPatternType() throws ParserToolException {
        PatternType pattern = null;
        if (isEmpty()) {
            return null;
        } else {
            for (SequenceTypeInterface seqType : this.values()) {
                if (pattern == null) {
                    pattern = seqType.getPatternType();
                    continue;
                }
                if (pattern.equals(seqType.getPatternType())) {
                    continue;
                } else {
                    throw new ParserToolException("Two different PatternTypes within one SequenceCollection detected. All sequences have to be of the same Type.");
                }
            }
            return pattern;
        }
    }

    public boolean idIsUnique(String id) {
        return !containsKey(id);
    }

    public boolean isSetSequence() {
        return !isEmpty();
    }

    public void unsetSequence() {
        clear();
    }

    public LinkedHashMap<String, SequenceTypeInterface> getSequences() {
        return this;
    }

    public void addSequence(SequenceTypeInterface sequenceType) {
        put(sequenceType.getSeqID(), sequenceType);
    }

    public boolean isSingleSequence() {
        return size() == 1;
    }

    // This functions only checks senquence length, but not content.
    public boolean isAllEqualLength() {
        boolean isAlignment = true;
        int seqLength = -1;
        if (isSingleSequence()) {
            return false;
        }
        for (SequenceTypeInterface seqType : this.values()) {
            int seqTypeLength = seqType.getSequence().length();
            if (seqLength == -1) {
                seqLength = seqTypeLength;
            }
            if (seqLength != seqTypeLength) {
                isAlignment = false;
                break;
            } else {
                continue;
            }
        }
        return isAlignment;
    }
}
