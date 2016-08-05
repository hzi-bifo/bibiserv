/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternCategory;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;



/**
 *
 * @author Richard
 */
public class GeneralSequenceType implements SequenceTypeInterface {

    private String seqId;
    private String description;
    private String sequence;
    private PatternType pattern;

    public GeneralSequenceType() {
        this(PatternType.isFreeSequence, null, null, null);
    }

    public GeneralSequenceType(PatternType pattern) {
        this(pattern, null, null, null);
    }

    public GeneralSequenceType(PatternType pattern, String id, String sequence) {
        this(pattern, id, null, sequence);
    }

    public GeneralSequenceType(PatternType pattern, String id, String description, String sequence) {
        this.pattern = pattern;
        this.seqId = id;
        this.description = description;
        this.sequence = sequence;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public boolean isSetDescription() {
        return !this.description.isEmpty() && this.description != null;
    }

    public void setSequenceCategory(PatternType category) {
        this.pattern = category;
    }

    public PatternCategory getSequenceCategory() {
        return this.pattern.getCategory();
    }

    public String getSequence() {
        return this.sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getSeqID() {
        return seqId;
    }

    public void setSeqID(String value) {
        this.seqId = value;
    }

    public boolean isSetSeqID() {
        return !this.seqId.isEmpty() && this.seqId != null;
    }

    public void unsetDescription() {
        this.description = null;
    }

    public void unsetSeqID() {
        this.seqId = null;
    }

    public void unsetSequence() {
        this.sequence = null;
    }

    public PatternType getPatternType() {
        return pattern;
    }

    public void setPatternType(PatternType pattern) {
        this.pattern = pattern;
    }
}
