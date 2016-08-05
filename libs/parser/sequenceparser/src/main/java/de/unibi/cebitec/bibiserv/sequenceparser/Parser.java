/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser;

import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.sequenceparser.parser.exception.ParserException;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.Readseq;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.SeqFileInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Parser for biological sequence data.
 * @author mrumming
 */
public abstract class Parser implements ParserInterface, Enumeration {

    protected BufferedReader inputFileReader;
    protected ParserOutputFormat pof;
    protected String recentLine;
    protected boolean fileEnded;
    protected HashMap<String, BioseqRecord> sequenceHash;
    protected Iterator<String> iSeq;
    protected boolean allReadyRun;

    {
        this.recentLine = "";
        this.fileEnded = false;
        this.pof = ParserOutputFormat.BioseqRecord;
        this.sequenceHash = new HashMap<String, BioseqRecord>();
        this.allReadyRun = false;
    }

    /**
     * {@inheritDoc}
     */
    public void setInputObject(Reader inputFile) {
        this.inputFileReader = new BufferedReader(inputFile);
    }

    protected boolean startFastaParsing() throws IOException {
        boolean retB = true;
        LinkedList<BioseqRecord> sequenceList = new LinkedList<BioseqRecord>();

        Readseq rd = new Readseq();
        rd.setInputObject(inputFileReader);


        if (rd.isKnownFormat() && rd.readInit()) {
            while (rd.readNext()) {
                SeqFileInfo info = rd.nextSeq();
                if (info != null && info.hasid()) {
                    sequenceList.add(new BioseqRecord(info));
                } else {
                    break;
                }
            }
        }

        if (sequenceList.isEmpty()) {
            retB = false;
        } else {
            for (BioseqRecord bRecord : sequenceList) {
                if (bRecord.hasid() && bRecord.hasseq()) {
                    sequenceHash.put(bRecord.getID(), bRecord);
                }
            }
            if (this.sequenceHash.isEmpty()) {
                retB = false;
            } else {
                this.iSeq = this.sequenceHash.keySet().iterator();
            }
        }
        return retB;
    }

    /**
     * Returns next Output. This is the only way getting other output formats
     * than BioseqRecord. Use type casting!
     * @return Based on choosen ParserOutputFormat
     */
    @Override
    public Object nextElement() {
        try {
            switch (pof) {
                case BioseqRecord:
                    return this.nextSeq();
                case Fasta:
                    return this.nextSeqFasta();
                default:
                    return this.nextSeqFromFasta();
            }
        } catch (ParserException ex) {
            System.err.println("ParserException appeared. Possibly no sequence information available.");
            return null;
        }
    }

    /**
     * Returns output format.
     * @return Output format.
     */
    public ParserOutputFormat getOutputFormat() {
        return this.pof;
    }

    /**
     * Sets output format.
     * @param outputFormat
     */
    public void setOutputFormat(ParserOutputFormat outputFormat) {
        this.pof = outputFormat;
    }

    /**
     * {@inheritDoc}
     */
    public boolean readNext() {
        return this.fileEnded ? false : this.isNewSequence();
    }

    /**
     * Checks, whether another sequence can be read or not.
     * @return True, if new input set is available, else false.
     */
    public boolean hasMoreElements() {
        return readNext();
    }

    /**
     * Returns fasta formated string with sequence and short description (if
     * available) in header.
     * @return Output in fasta format
     * @throws ParserException Is thrown, if input data is malformed.
     */
    protected abstract String nextSeqFasta() throws ParserException;

    /**
     * Returns output in desired format
     * @return Output in desired format
     * @throws ParserException Is thrown, if input data is malformed.
     */
    protected abstract String nextSeqFromFasta() throws ParserException;

    /**
     *
     * @return True, if read in line marks a new sequence. False, else.
     */
    protected abstract boolean isNewSequence();

    /**
     * Reformats given sequence. All letters are switched to upper case.
     * Characters '.', '~', '_' and '?' are replaced by '-'.
     * @param s String to be reformated
     * @return Formated sequence
     */
    protected String reformat(String s) {
        return s.replaceAll("\\s", "").replaceAll("\\.|~|_|\\?", "-").toUpperCase();
    }
}
