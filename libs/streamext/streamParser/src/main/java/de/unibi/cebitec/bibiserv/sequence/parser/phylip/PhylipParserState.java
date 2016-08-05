
package de.unibi.cebitec.bibiserv.sequence.parser.phylip;

import de.unibi.cebitec.bibiserv.sequence.parser.clustal.*;
import de.unibi.cebitec.bibiserv.sequence.parser.codata.*;

/**
 * 
 * @author tgatter
 */
public enum PhylipParserState {
    
    readHeader,
    seekFirstSequence,
    readSequence,
    allDone
}
