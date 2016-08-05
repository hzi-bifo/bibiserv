
package de.unibi.cebitec.bibiserv.sequence.parser.GDEFlat;

import de.unibi.cebitec.bibiserv.sequence.parser.fasta.*;

/**
 * Enum to make the States of the Statemachine in FastaParser readable. 
 * @author Thomas Gatter
 */
public enum GDEFlatParserState {
    
    seekSequenceStart,
    readSequence,
    readText
    
}
