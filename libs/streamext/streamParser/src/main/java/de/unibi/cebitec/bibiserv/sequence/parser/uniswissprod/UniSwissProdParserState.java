
package de.unibi.cebitec.bibiserv.sequence.parser.uniswissprod;

import de.unibi.cebitec.bibiserv.sequence.parser.EMBL.*;
import de.unibi.cebitec.bibiserv.sequence.parser.fasta.*;

/**
 * Enum to make the States of the Statemachine in Uni and Swiss prod Parser readable. 
 * @author Thomas Gatter
 */
public enum UniSwissProdParserState {
    
    seekSequenceStart,
    seekSequence,
    readSequence,
    allDone
    
}
