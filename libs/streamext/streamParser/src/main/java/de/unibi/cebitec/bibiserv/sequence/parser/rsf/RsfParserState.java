
package de.unibi.cebitec.bibiserv.sequence.parser.rsf;

import de.unibi.cebitec.bibiserv.sequence.parser.genebank.*;

/**
 *
 * @author gatter
 */
public enum RsfParserState {
    
    readRSF,
    readPoints,
    seekBraceStart,
    seekId,
    seekSequenceStart,
    readSequence,
    allDone
    
}
