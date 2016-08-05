/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternCategory;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import net.bioservices.schemas._2005.biotypes.T_AlignedAminoAcidSequence;
import net.sourceforge.hobit.xsds._20060602.alignmentml.AlignedNucleotideSequence;
import net.sourceforge.hobit.xsds._20060602.alignmentml.SequenceType;


/**
 *
 * @author Richard
 */
public class AML_SequenceTypeImpl extends SequenceType implements SequenceTypeInterface {

    private PatternType pattern;

    public AML_SequenceTypeImpl() {
        this(PatternType.isFreeSequence);
    }

    public AML_SequenceTypeImpl(PatternType pattern) {
        this(pattern, null, null, null);
    }

    public AML_SequenceTypeImpl(PatternType pattern, String id, String description, String sequence) {
        this.pattern = pattern;
        this.setSeqID(id);
        this.setDescription(description);
        this.setSequence(sequence);
    }

    public PatternCategory getSequenceCategory() {
        return pattern.getCategory();
    }

    public String getSequence() {
        switch (getSequenceCategory()) {
            case AminoAcid:
                return getAlignedAminoAcidSequence().getValue();
            case NucleicAcid:
                return getAlignedNucleicAcidSequence().getValue();
            case Free:
                return getAlignedFreeSequence();
            //TODO: default??
            default:
                return null;
        }
    }

    public void setSequence(String sequence) {
        switch (getSequenceCategory()) {
            case AminoAcid:
                net.bioservices.schemas._2005.biotypes.ObjectFactory biotypesObjectFactory = new net.bioservices.schemas._2005.biotypes.ObjectFactory();
                T_AlignedAminoAcidSequence t_alignedAAsequence = biotypesObjectFactory.createT_AlignedAminoAcidSequence();
                 t_alignedAAsequence.setValue(sequence);
                setAlignedAminoAcidSequence(t_alignedAAsequence);
                break;
            case NucleicAcid:
                net.sourceforge.hobit.xsds._20060602.alignmentml.ObjectFactory alignmentMLObjectFactory = new net.sourceforge.hobit.xsds._20060602.alignmentml.ObjectFactory();
                AlignedNucleotideSequence alignedSequence = alignmentMLObjectFactory.createAlignedNucleotideSequence();
                alignedSequence.setValue(sequence);
                setAlignedNucleicAcidSequence(alignedSequence);
                break;
            case Free:
                setAlignedFreeSequence(sequence);
                break;
        }

    }

    public PatternType getPatternType() {
        return pattern;
    }

    public void setPatternType(PatternType pattern) {
        this.pattern = pattern;
    }
}
