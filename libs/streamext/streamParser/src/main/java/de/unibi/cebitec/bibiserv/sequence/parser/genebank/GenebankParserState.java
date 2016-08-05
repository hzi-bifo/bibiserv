
package de.unibi.cebitec.bibiserv.sequence.parser.genebank;

/**
 *
 * @author gatter
 */
public enum GenebankParserState {
    
    seekLocus,
    seekId,
    seekSequenceStart,
    readSequence,
    allDone
    
}
