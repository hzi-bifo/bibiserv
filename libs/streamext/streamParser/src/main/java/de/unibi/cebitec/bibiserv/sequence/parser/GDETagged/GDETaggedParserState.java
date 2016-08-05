/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequence.parser.GDETagged;

import de.unibi.cebitec.bibiserv.sequence.parser.codata.*;

/**
 *
 * @author gatter
 */
public enum GDETaggedParserState {
    
    seekBraceStart,
    seekId,
    readId,
    seekSequenceStart,
    readSequence,
    seekBraceStop,
    allDone
    
}
