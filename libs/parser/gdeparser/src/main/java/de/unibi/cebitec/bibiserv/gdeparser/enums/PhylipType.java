/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.cebitec.bibiserv.gdeparser.enums;

/**
 *
 * @author mrumming
 */
public enum PhylipType {
    /**
     * All sequences in blocls. Every line stands for a unique sequence.
     * Standardnames - length = 10
     */
    InterleavedStandardnames,
    /**
     * All sequences in blocls. Every line stands for a unique sequence.
     * Names are seperated from sequences with spaces.
     */
    InterleavedSpacesNames,
    /**
     * Every Sequence as one block.
     * Standardnames - length = 10
     */
    SequentialStandardnames,
    /**
     * Every Sequence as one block.
     * Names are seperated from sequences with spaces.
     */
    SequentialSpacesNames,
    /**
     * Set as standard. Needs heuristic for type determination.
     */
    Unknown;
}
