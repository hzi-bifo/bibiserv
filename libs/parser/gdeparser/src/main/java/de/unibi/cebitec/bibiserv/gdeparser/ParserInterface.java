/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.cebitec.bibiserv.gdeparser;

import de.unibi.cebitec.bibiserv.gdeparser.parser.exception.ParserException;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord;

import java.io.Reader;

/**
 * Interface for a sequence parser.
 * @author mrumming
 */
public interface ParserInterface {

    /**
     * Sets input.
     * @param inputFile Input as Reader
     */
    public void setInputObject(Reader inputFile);

    /**
     * Checks, whether another sequence can be read or not.
     * @return True, if new input set is available, else false.
     */
    public boolean readNext();

    /**
     * Returns sequence and meta information of given input.
     * @return Contains sequence and meta information.
     * @throws ParserException Is thrown, if input data is malformed.
     */
    public BioseqRecord nextSeq() throws ParserException;

    /**
     * First check, whether given input data can be read or not.
     * @return True, if input is unterstood and can be read, false else.
     */
    public boolean isKnownFormat();

}
