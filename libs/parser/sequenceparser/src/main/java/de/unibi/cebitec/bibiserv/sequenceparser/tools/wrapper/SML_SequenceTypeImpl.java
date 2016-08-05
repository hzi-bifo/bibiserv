/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternCategory;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import net.sourceforge.hobit.xsds._20090917.sequenceml.*;






/**
 *
 * @author Richard
 */
public class SML_SequenceTypeImpl extends SequenceType implements SequenceTypeInterface {

    private PatternType pattern;

    public SML_SequenceTypeImpl() {
        this(PatternType.isFreeSequence);
    }
    
    public SML_SequenceTypeImpl(PatternType pattern) {
        this(pattern, null, null, null);
    }

    public SML_SequenceTypeImpl(PatternType pattern, String id, String description, String sequence) {
        this.pattern = pattern;
        this.setSeqID(id);
        this.setDescription(description);
        this.setSequence(sequence);
    }

    public PatternType getPatternType() {
        return pattern;
    }

    public void setPatternType(PatternType pattern) {
        this.pattern = pattern;
    }

    public PatternCategory getSequenceCategory() {
        return pattern.getCategory();
    }

    public String getSequence() {
        switch (getSequenceCategory()) {
            case AminoAcid:
                return getAminoAcidSequence();
            case NucleicAcid:
                return getNucleicAcidSequence();
            case Free:
                return getFreeSequence();
            
            default:
                return null;
        }
    }

    public void setSequence(String sequence) {
        switch (getSequenceCategory()) {
            case AminoAcid:
                setAminoAcidSequence(sequence);
                break;
            case NucleicAcid:
                setNucleicAcidSequence(sequence);
                break;
            case Free:
                setFreeSequence(sequence);
                break;
        }

    }

}
