/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternCategory;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;


/**
 *
 * @author rmadsack
 */
public interface SequenceTypeInterface {

    /**
     * Gets the value of the description property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription();

    /**
     * Sets the value of the description property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value);

    public boolean isSetDescription();

    /**
     * Gets the value of the seqID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSeqID();

    /**
     * Sets the value of the seqID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSeqID(String value);

    public boolean isSetSeqID();

    public PatternType getPatternType();

    public void setPatternType(PatternType pattern);

    public PatternCategory getSequenceCategory();

    public String getSequence();

    public void setSequence(String sequence);
}
