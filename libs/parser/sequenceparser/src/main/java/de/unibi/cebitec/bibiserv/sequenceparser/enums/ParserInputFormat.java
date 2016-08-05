/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.cebitec.bibiserv.sequenceparser.enums;

import de.unibi.cebitec.bibiserv.sequenceparser.ParserFactory;
import java.io.Reader;

/**
 * Enums for supported input formats.
 * @author mrumming
 */
public enum ParserInputFormat {
    DIALIGN,
    Fasta,
    GDE,
    IG,
    MSF,
    PHYLIP,
    RSF,
    UNKNOWN,
    AMBIGOUS;

    public boolean isChoosenInput(Reader inputFileReader){
        try{
            return ParserFactory.createParser(this, inputFileReader).isKnownFormat();
        } catch (IllegalArgumentException iae){
            return false;
        }
    }
}
