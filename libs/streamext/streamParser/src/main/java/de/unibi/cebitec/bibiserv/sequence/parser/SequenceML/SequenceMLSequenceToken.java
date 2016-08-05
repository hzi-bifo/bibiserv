package de.unibi.cebitec.bibiserv.sequence.parser.SequenceML;

import de.unibi.cebitec.bibiserv.sequence.parser.AlignmentML.*;

/**
 * All tag names allowed in Sequence tag by AlignmentMl
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public enum SequenceMLSequenceToken {

    aminoAcidSequence("aminoAcidSequence"),
    nucleicAcidSequence("nucleicAcidSequence"),
    freeSequence("freeSequence"),
    emptySequence("emptySequence");
    
    private String tagString;

    private SequenceMLSequenceToken(String tagString) {
        this.tagString = tagString;
    }

    public String getTagString() {
        return tagString;
    }
    
    public static boolean contains(String tag) {
        for(SequenceMLSequenceToken t:values()) {
            if(t.getTagString().equals(tag)) {
                return true;
            }
        }
        return false;
    }
}
