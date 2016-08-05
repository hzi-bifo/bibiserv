
package de.unibi.cebitec.bibiserv.sequence.parser;

/**
 * The is returned by the readLine function of SequenceParser depending on result.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public enum ReadLineMessage {
    
    lineRead,
    abortByLineBegin,
    noMoreLines
    
}
