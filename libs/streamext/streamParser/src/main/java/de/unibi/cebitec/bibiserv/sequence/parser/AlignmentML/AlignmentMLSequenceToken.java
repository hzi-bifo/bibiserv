package de.unibi.cebitec.bibiserv.sequence.parser.AlignmentML;

/**
 * All tag names allowed in Sequence tag by AlignmentMl
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public enum AlignmentMLSequenceToken {

    alignedAminoAcidSequence("alignedAminoAcidSequence"),
    alignedNucleicAcidSequence("alignedNucleicAcidSequence"),
    alignedFreeSequence("alignedFreeSequence");
    
    private String tagString;

    private AlignmentMLSequenceToken(String tagString) {
        this.tagString = tagString;
    }

    public String getTagString() {
        return tagString;
    }
    
    public static boolean contains(String tag) {
        for(AlignmentMLSequenceToken t:values()) {
            if(t.getTagString().equals(tag)) {
                return true;
            }
        }
        return false;
    }
}
