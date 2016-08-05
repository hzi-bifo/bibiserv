/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequence.parser.codata;

/**
 *
 * @author gatter
 */
public enum CodataParserState {
    
    seekIdEntry,
    seekSequenceStart,
    readSequence,
    readEnd
    
}
