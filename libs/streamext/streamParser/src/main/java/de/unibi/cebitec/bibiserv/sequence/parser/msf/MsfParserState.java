
package de.unibi.cebitec.bibiserv.sequence.parser.msf;

/**
 * 
 * @author tgatter
 */
public enum MsfParserState {
    
    readHeader,
    seekNames,
    readNames,
    seekFirstSequence,
    readSequence,
    allDone
}
