
package de.unibi.cebitec.bibiserv.sequence.parser.fasta;

import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import java.io.BufferedWriter;

/**
 * This is passed to the FastaConverter. Each function is called according to its name.
 * Argument is always the current writer the converted output is written to and 
 * in some cases the token that was found.
 * 
 * @author Thomas Gatter
 */
public interface FastaToAllConverterCallback {
    
    public void fileBegin(BufferedWriter writer) throws SequenceParserException;
    public void fileEnd(BufferedWriter writer) throws SequenceParserException;
    public void idFound(BufferedWriter writer, String id) throws SequenceParserException;
    public void headerExtraInfoFound(BufferedWriter writer, String info) throws SequenceParserException;
    public void beginSequence(BufferedWriter writer) throws SequenceParserException;
    public void endSequence(BufferedWriter writer) throws SequenceParserException;
    
    
}
