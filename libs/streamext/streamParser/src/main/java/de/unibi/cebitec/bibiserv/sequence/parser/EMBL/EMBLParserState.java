
package de.unibi.cebitec.bibiserv.sequence.parser.EMBL;

import de.unibi.cebitec.bibiserv.sequence.parser.fasta.*;

/**
 * Enum to make the States of the Statemachine in EMBLParser readable. 
 * @author Thomas Gatter
 */
public enum EMBLParserState {
    
    seekSequenceStart,
    seekSequence,
    readSequence,
    allDone
    
}
