/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.tools;

/**
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.techfak.uni-bielefeld.de
 */
public enum ReadSeqFormat {
    
    
    Fasta(8),
    SWISSPROT_UNIPROT(4),
    EMBL(4),
    NBRF(3),
    CODATA(14),
    CLUSTAL(22);
    
    private int formatInt;

    private ReadSeqFormat(int formatInt) {
        this.formatInt = formatInt;
    }

    public int getFormatInt() {
        return formatInt;
    }

}
