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
import java.util.HashMap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 *
 * @author mrumming
 */
public class RsfParser extends Parser {
    
    private final static Logger LOG = Logger.getLogger(RsfParser.class);

    private final String empty;

    {
        this.empty = " NONE\n";
    }

    public RsfParser(Reader inputFileReader) {
        this.inputFileReader = new BufferedReader(inputFileReader);
        this.initReader();
    }

    public RsfParser(Reader inputFileReader, ParserOutputFormat pof) {
        this.inputFileReader = new BufferedReader(inputFileReader);
        this.pof = pof;
    }

    /**
     * Check, whether file header fits
     */
    private void initReader() {
        try {
            if (this.inputFileReader.readLine().trim().matches("!!RICH_SEQUENCE.*")) {
                if (!this.inputFileReader.readLine().trim().matches("\\.\\.")) {
                    this.fileEnded = true;
                }
            } else {
                this.fileEnded = true;
            }
        } catch (IOException ex) {
            this.fileEnded = true;
        }
    }

    private HashMap<String, String> parseGDE() throws ParserException {
        HashMap<String, String> hT = new HashMap<String, String>();
        StringBuilder sBuf = new StringBuilder();
        boolean inSequence = false;

        try {
            this.recentLine = this.inputFileReader.readLine();
            while (!this.isNewSequence()) {
                if (inSequence) {
                    if (this.recentLine.trim().endsWith("}")) {
                        try {
                            do {
                                this.recentLine = this.inputFileReader.readLine().trim();
                            } while (this.recentLine.equals(""));
                        } catch (NullPointerException npe) {
                            //Do nothing
                        }
                        break;
                    } else {
                        sBuf.append(this.recentLine.trim());
                    }
                } else {
                    if (this.recentLine.trim().startsWith("name") && !hT.containsKey("name")) {
                        hT.put("name", this.recentLine.trim().split("\\s+")[1]);
                    } else if (this.recentLine.trim().startsWith("descrip") && !hT.containsKey("description")) {
                        String[] sArr_temp = this.recentLine.split("\\s+");
                        StringBuilder description = new StringBuilder();
                        for (int i = 1; i < sArr_temp.length; i++) {
                            description.append(sArr_temp[i]).append(" ");
                        }
                        hT.put("description", description.toString().trim());
                    } else if (this.recentLine.trim().split("\\s")[0].equals("sequence") && sBuf.length() == 0) {
                        inSequence = true;
                    } else if (this.recentLine.trim().startsWith("longname") && !hT.containsKey("longname")) {
                        String[] sArr_temp = this.recentLine.split("\\s+");
                        StringBuilder longname = new StringBuilder();
                        for (int i = 1; i < sArr_temp.length; i++) {
                            longname.append(sArr_temp[i]).append(" ");
                        }
                        hT.put("longname", longname.toString().trim());
                    }
                }
                this.recentLine = this.inputFileReader.readLine();
            }
        } catch (IOException ioe) {
            throw new ParserException("GDE input file is corrupt.");
        }

        hT.put("sequence", sBuf.toString().trim());

        return hT;
    }

    public BioseqRecord nextSeq() throws ParserException {
        HashMap<String, String> hT = this.parseGDE();

        BasicBioseqDoc bbd = null;

        bbd = new BasicBioseqDoc(hT.get("name"));
        if (hT.containsKey("description")) {
            bbd.addComment(hT.get("description"));
        } else if (hT.containsKey("longname")) {
            bbd.addComment(hT.get("longname"));
        }
        return new BioseqRecord(new Bioseq(this.reformat(hT.get("sequence"))), bbd, hT.get("name"));
    }

    @Override
    public boolean isKnownFormat() {
        try {
            if (this.pof.equals(ParserOutputFormat.RSF)) {
                if (this.startFastaParsing()) {
                    this.iSeq = this.sequenceHash.keySet().iterator();
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    if (this.fileEnded) {
                        return false;
                    } else {
                        this.recentLine = this.inputFileReader.readLine().trim();
                        return this.isNewSequence();
                    }
                } catch (IOException ex) {
                    LOG.debug(ex.getMessage());
                    return false;
                }
            }
        } catch (IOException ioe) {
            return false;
        }
    }

    @Override
    protected String nextSeqFasta() throws ParserException {
        HashMap<String, String> hT = this.parseGDE();
        StringBuilder sBuf = new StringBuilder(hT.get("sequence"));
        StringBuilder retBuf = new StringBuilder();

        retBuf.append(">").append(hT.get("name"));

        if (hT.containsKey("description")) {
            retBuf.append(" ").append(hT.get("description")).append(" ").append(sBuf.length()).append(" bp\n");
        } else {
            retBuf.append(" ").append(sBuf.length()).append(" bp\n");
        }
        for (int i = 0; i < sBuf.length(); i += 60) {
            if (i + 60 < sBuf.length()) {
                retBuf.append(this.reformat(sBuf.subSequence(i, i + 60).toString())).append("\n");
            } else {
                retBuf.append(this.reformat(sBuf.subSequence(i, sBuf.length()).toString()));
            }
        }
        return retBuf.toString();
    }

    @Override
    protected boolean isNewSequence() {
        boolean retB = true;

        try {
            switch (this.recentLine.trim().charAt(0)) {
                case '{':
                    break;
                default:
                    retB = false;
                    break;
            }
        } catch (StringIndexOutOfBoundsException sioobe) {
            retB = false;
            //this.fileEnded = true;
        } catch (NullPointerException npe) {
            retB = false;
            this.fileEnded = true;
        }

        return retB;
    }

    @Override
    public boolean readNext() {
        if (this.pof.equals(ParserOutputFormat.RSF)) {
            if (this.iSeq == null) {
                return false;
            } else {
                return this.iSeq.hasNext();
            }
        } else {
            return this.fileEnded ? false : this.isNewSequence();
        }
    }

    @Override
    protected String nextSeqFromFasta() throws ParserException {
        if (!this.iSeq.hasNext()) {
            throw new ParserException("No sequence available!");
        }

        StringBuilder sBuilder = new StringBuilder();


        sBuilder.append("!!RICH_SEQUENCE 1.0\n..\n");
        while (this.iSeq.hasNext()) {
            BioseqRecord bRecord = this.sequenceHash.get(this.iSeq.next());
            StringBuilder sequence = new StringBuilder(bRecord.getseq().toString());
            sBuilder.append("{\nname        ").append(bRecord.getID()).append("\n");
            sBuilder.append("descrip").append(this.empty);
            if (sequence.toString().matches("^[AGCTURYMKSWHBVDNX\\-]+$")) {
                sBuilder.append("type        DNA/RNA\n");
            } else {
                sBuilder.append("type        PROTEIN\n");
            }
            sBuilder.append("longnamename        ").append(bRecord.getID()).append("\n");
            sBuilder.append("sequence-ID").append(this.empty);
            sBuilder.append("checksum").append(this.empty);
            sBuilder.append("creation-date").append(this.empty);
            sBuilder.append("strand").append(this.empty);

            sBuilder.append("comments\n  This file was created via GDEParser directly from fasta\n"
                    + "  without any meta information. Placeholders are given above.\n");
            sBuilder.append("sequence\n");
            while (sequence.length() > 0) {
                if (sequence.length() >= 50) {
                    sBuilder.append("  ").append(sequence.subSequence(0, 50)).append("\n");
                } else {
                    sBuilder.append("  ").append(sequence).append("\n}\n");

                }
                sequence.delete(0, 50);
            }
        }

        this.allReadyRun = true;
        return sBuilder.toString();
    }
}
