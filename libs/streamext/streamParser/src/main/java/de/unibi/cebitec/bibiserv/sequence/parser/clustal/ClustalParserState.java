
package de.unibi.cebitec.bibiserv.sequence.parser.clustal;

import de.unibi.cebitec.bibiserv.sequence.parser.codata.*;

/**
 * 
 * @author tgatter
 */
public enum ClustalParserState {
    
    readHeader,
    seekFirstSequence,
    readSequence,
    readOneEmptyLine,
    allDone
}
