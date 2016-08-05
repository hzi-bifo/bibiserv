/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.parser.impl;

import de.unibi.cebitec.bibiserv.sequenceparser.Parser;
import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.sequenceparser.parser.exception.ParserException;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.Bioseq;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BasicBioseqDoc;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 *
 * @author mrumming
 */
public class IgParser extends Parser {

    private final static Logger LOG = Logger.getLogger(IgParser.class);
    
    public IgParser(Reader inputFileReader) {
        this.inputFileReader = new BufferedReader(inputFileReader);
    }

    public IgParser(Reader inputFileReader, ParserOutputFormat pof) {
        this.inputFileReader = new BufferedReader(inputFileReader);
        this.pof = pof;
    }

    @Override
    public BioseqRecord nextSeq() throws ParserException {
        return this.sequenceHash.get(iSeq.next());
    }

    @Override
    public boolean isKnownFormat() {
        try {
            if (this.pof.equals(ParserOutputFormat.IG)) {
                if (this.startFastaParsing()) {
                    this.iSeq = this.sequenceHash.keySet().iterator();
                    return true;
                } else {
                    return false;
                }
            }
            if (this.collectSequences()) {
                this.iSeq = this.sequenceHash.keySet().iterator();
                return true;
            } else {
                return false;
            }

        } catch (IOException ioe) {
            return false;
        }
    }

    @Override
    protected String nextSeqFasta() throws ParserException {
        BioseqRecord bRecord = this.sequenceHash.get(this.iSeq.next());

        StringBuilder sBuf = new StringBuilder(bRecord.getseq().toString());
        StringBuilder retBuf = new StringBuilder();

        retBuf.append(">").append(bRecord.getID().trim());

        retBuf.append(" ").append(sBuf.length()).append(" bp\n");

        for (int i = 0; i < sBuf.length(); i += 60) {
            if (i + 60 < sBuf.length()) {
                retBuf.append(sBuf.subSequence(i, i + 60)).append("\n");
            } else {
                retBuf.append(sBuf.subSequence(i, sBuf.length()));
            }
        }
        return retBuf.toString();
    }

    @Override
    public boolean readNext() {
        if (this.iSeq == null) {
            return false;
        } else {
            return this.iSeq.hasNext();
        }
    }

    private boolean collectSequences() {
        String name = "";
        StringBuilder seqBuilder = new StringBuilder();
        try {
            while ((this.recentLine = this.inputFileReader.readLine()) != null) {
                if (name.equals("")) {
                    if (!this.recentLine.trim().startsWith(";")) {
                        name = this.recentLine.trim().split(" ")[0];
                    }
                } else {
                    if (this.recentLine.trim().matches(".*\\d+")) {
                        seqBuilder.append(this.recentLine.trim().substring(0, this.recentLine.trim().length() - 1).replaceAll(" ", ""));
                        this.sequenceHash.put(name, new BioseqRecord(new Bioseq(this.reformat(seqBuilder.toString())), new BasicBioseqDoc(name), name));
                        name = "";
                        seqBuilder = new StringBuilder();
                    } else {
                        seqBuilder.append(this.recentLine.trim().replaceAll(" ", ""));
                    }
                }
            }
            if (!this.sequenceHash.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException | ArrayIndexOutOfBoundsException ex) {
            LOG.debug(ex.getMessage());
            return false;
        } 
    }

    @Override
    protected boolean isNewSequence() {
        return true;
    }

    @Override
    protected String nextSeqFromFasta() throws ParserException {
        StringBuilder sBuilder = new StringBuilder();
        if (!this.iSeq.hasNext()) {
            throw new ParserException("No sequence available!");
        }

        BioseqRecord bRecord = this.sequenceHash.get(this.iSeq.next());
        StringBuilder sequence = new StringBuilder(bRecord.getseq().toString());

        sBuilder.append(bRecord.getID()).append("\n");
        while (sequence.length() > 0) {
            if (sequence.length() <= 60) {
                if (sequence.length() == 60) {
                    sBuilder.append(sequence).append("\n1\n");
                } else {
                    sBuilder.append(sequence).append("1\n");
                }
            } else {

                sBuilder.append(sequence.subSequence(0, 60)).append("\n");
            }
            sequence.delete(0, 60);
        }
        sBuilder.deleteCharAt(sBuilder.length() - 1);

        return sBuilder.toString();
    }
}
