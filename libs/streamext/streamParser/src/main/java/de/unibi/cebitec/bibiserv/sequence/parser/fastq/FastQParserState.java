
package de.unibi.cebitec.bibiserv.sequence.parser.fastq;

/**
 * Enum to make the States of the Statemachine in FastqParser readable. 
 * @author Thomas Gatter
 */
public enum FastQParserState {
    
    seekSequenceStart,
    readSequence,
    readQuality
}
