
package de.unibi.cebitec.bibiserv.sequence.parser.NBRF;

import de.unibi.cebitec.bibiserv.sequence.parser.IG.*;
import de.unibi.cebitec.bibiserv.sequence.parser.fasta.*;

/**
 * Enum to make the States of the Statemachine in FastaParser readable. 
 * @author Thomas Gatter
 */
public enum NBRFParserState {
    
    seekSequenceStart,
    commentLine,
    readSequence,
    allDone
    
}
