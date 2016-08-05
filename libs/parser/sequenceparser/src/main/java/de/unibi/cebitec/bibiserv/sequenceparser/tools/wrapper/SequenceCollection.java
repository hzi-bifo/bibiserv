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
public interface SequenceCollection {

    /**
     * Gets the value of the sequence property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sequence property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSequence().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SequenceTypeInterface }
     *
     *
     */
    public LinkedHashMap<String, SequenceTypeInterface> getSequences();

    public boolean isSetSequence();

    public void unsetSequence();

    public void addSequence(SequenceTypeInterface sequenceType);

    public PatternType getPatternType() throws ParserToolException;

    /**
     *
     * @param id -
     *            id to be test
     * @return true if the id is unqiue in this object, false otherwise
     */
    public boolean idIsUnique(final String id);

    public boolean isSingleSequence();

    public boolean isEmpty();
    
    public boolean isAllEqualLength();

}
