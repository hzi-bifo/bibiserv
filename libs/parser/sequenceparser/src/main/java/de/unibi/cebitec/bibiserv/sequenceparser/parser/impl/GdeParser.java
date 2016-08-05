/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.parser.impl;


import de.unibi.cebitec.bibiserv.sequenceparser.enums.GdeInputFormat;
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
public class GdeParser extends Parser {
    
    private static final Logger LOG = Logger.getLogger(GdeParser.class);

    private GdeInputFormat inputFormat;
    private final String empty;

    {
        this.inputFormat = GdeInputFormat.Unknown;
        this.empty = " \"\"\n";
    }

    public GdeParser(Reader inputFileReader) {
        this.inputFileReader = new BufferedReader(inputFileReader);
    }

    public GdeParser(Reader inputFileReader, ParserOutputFormat pof) {
        this.inputFileReader = new BufferedReader(inputFileReader);
        this.pof = pof;
    }

    public BioseqRecord nextSeq() throws ParserException {
        BioseqRecord bRecord = null;
        switch (this.inputFormat) {
            case FlatFile:
                bRecord = this.readFlatFile();
                break;
            case ASN_1:
                bRecord = this.readASN_1();
                break;
        }
        return bRecord;
    }

    @Override
    protected String nextSeqFasta() throws ParserException {
        switch (this.inputFormat) {
            case FlatFile:
                return this.readFlatFile_Fasta();
            case ASN_1:
                return this.readASN_1_Fasta();
        }
        return null;
    }

    private BioseqRecord readASN_1() throws ParserException {
        HashMap<String, String> hT = this.parseASN_1();
        BasicBioseqDoc bbd = null;
        StringBuilder sBuf = new StringBuilder(hT.get("sequence"));

        bbd = new BasicBioseqDoc(hT.get("name"));
        if (hT.containsKey("description")) {
            bbd.addComment(hT.get("description"));
        }

        return new BioseqRecord(new Bioseq(this.reformat(sBuf.toString())), bbd, hT.get("name"));
    }

    private HashMap<String, String> parseASN_1() throws ParserException {
        HashMap<String, String> hT = new HashMap<String, String>();
        StringBuilder sBuf = new StringBuilder();
        boolean inSequence = false;
        boolean seqStartMarkerSet = false;

        try {
            this.recentLine = this.inputFileReader.readLine();
            while (!this.isNewSequence()) {
                if (inSequence) {
                    if (seqStartMarkerSet) {
                        if (this.recentLine.trim().endsWith("\"")) {
                            sBuf.append(this.recentLine.split("\"")[0].trim());
                            inSequence = false;
                        } else {
                            sBuf.append(this.recentLine.trim());
                        }
                    }
                    if (this.recentLine.trim().startsWith("\"") && !seqStartMarkerSet) {
                        seqStartMarkerSet = true;
                        if (this.recentLine.trim().endsWith("\"")) {
                            sBuf.append(this.recentLine.trim().split("\"")[1]);
                            inSequence = false;
                        } else {
                            sBuf.append(this.recentLine.trim().split("\"")[1]);
                        }
                    }
                } else {
                    if (this.recentLine.trim().startsWith("name") && !hT.containsKey("name")) {
                        hT.put("name", this.recentLine.split("\"")[1]);
                    } else if (this.recentLine.trim().startsWith("descrip") && !hT.containsKey("description")) {
                        hT.put("description", this.recentLine.split("\"")[1]);
                    } else if (this.recentLine.trim().split("\\s")[0].equals("sequence") && sBuf.length() == 0) {
                        try {
                            sBuf.append(this.recentLine.split("\"")[1].trim());
                            seqStartMarkerSet = true;
                        } catch (ArrayIndexOutOfBoundsException aiooe) {
                            if (this.recentLine.matches("sequence\\s*\".*")) {
                                seqStartMarkerSet = true;
                            }
                        }
                        if (!this.recentLine.trim().matches("sequence\\s*\"\\s*[.&&[^\"]]*\\s*\"")) {
                            inSequence = true;
                        }
                    }
                }
                this.recentLine = this.inputFileReader.readLine();
            }
        } catch (IOException ex) {
            LOG.debug(ex.getMessage());
            throw new ParserException("GDE input file is corrupt.");
        }
        hT.put("sequence", sBuf.toString());
        return hT;
    }

    private String readASN_1_Fasta() throws ParserException {

        HashMap<String, String> hT = this.parseASN_1();
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

    private BioseqRecord readFlatFile() {
        String seqId = this.recentLine.trim().substring(1);
        BasicBioseqDoc bbd = new BasicBioseqDoc(seqId);
        StringBuilder sBuf = new StringBuilder();
        try {
            this.recentLine = this.inputFileReader.readLine();
            while (!this.isNewSequence()) {
                sBuf.append(this.recentLine);
                this.recentLine = this.inputFileReader.readLine();
            }
        } catch (IOException ex) {
           LOG.error(ex.getMessage(),ex);
        }
        return new BioseqRecord(new Bioseq(this.reformat(sBuf.toString())), bbd, seqId);
    }

    private String readFlatFile_Fasta() {
        StringBuilder retBuf = new StringBuilder();
        retBuf.append(">").append(this.recentLine.trim().substring(1));
        StringBuilder sBuf = new StringBuilder();
        try {
            this.recentLine = this.inputFileReader.readLine();
            while (!this.isNewSequence()) {
                sBuf.append(this.recentLine);
                this.recentLine = this.inputFileReader.readLine();
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(),ex);
        }
        retBuf.append(" ").append(sBuf.length()).append(" bp\n");
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
    public boolean isKnownFormat() {
        try {
            if (this.pof.equals(ParserOutputFormat.GDE_Flat) || this.pof.equals(ParserOutputFormat.GDE_Tagged)) {
                if (this.startFastaParsing()) {
                    this.iSeq = this.sequenceHash.keySet().iterator();
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    this.recentLine = this.inputFileReader.readLine();
                    this.isNewSequence();
                    return !inputFormat.equals(GdeInputFormat.Unknown);
                } catch (IOException ex) {
                    LOG.debug(ex.getMessage());
                    return (false);
                }
            }
        } catch (IOException ioe) {
            return false;
        }
    }

    @Override
    protected boolean isNewSequence() {
        boolean retB = true;

        try {
            switch (this.recentLine.trim().charAt(0)) {
                case '#':
                    this.inputFormat = GdeInputFormat.FlatFile;
                    break;
                case '@':
                    this.inputFormat = GdeInputFormat.FlatFile;
                    break;
                case '%':
                    this.inputFormat = GdeInputFormat.FlatFile;
                    break;
                /*TODO: '"' stands for text. This sequence type is not yet
                 * supported, because '"' is used as a marker for beginning
                 * and ending of text content in an ASN.1 formatted file in
                 * parsing process.
                 */

                //case '"':
                //  this.inputFormat = GdeInputFormat.FlatFile_Text;
                // break;
                case '{':
                    this.inputFormat = GdeInputFormat.ASN_1;
                    break;
                default:
                    retB = false;
                    break;
            }
        } catch (StringIndexOutOfBoundsException sioobe) {
            retB = false;
            //this.fileEnded = true;
        } catch (NullPointerException npe) {
            retB = true;
            this.fileEnded = true;
        }

        return retB;
    }

    @Override
    public boolean readNext() {
        if (this.pof.equals(ParserOutputFormat.GDE_Flat) || this.pof.equals(ParserOutputFormat.GDE_Tagged)) {
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
        if (this.pof.equals(ParserOutputFormat.GDE_Tagged)) {
            return this.fastaToTagged();
        } else {
            return this.fastaToFlat();
        }
    }

    private String fastaToFlat() {
        StringBuilder sBuilder = new StringBuilder();

        BioseqRecord bRecord = this.sequenceHash.get(this.iSeq.next());
        StringBuilder sequence = new StringBuilder(bRecord.getseq().toString());

        if (sequence.toString().matches("^[AGCTURYMKSWHBVDNX\\-]+$")) {
            sBuilder.append("#");
        } else {
            sBuilder.append("%");
        }
        sBuilder.append(bRecord.getID()).append("\n");
        while (sequence.length() > 0) {
            if (sequence.length() < 60) {
                sBuilder.append(sequence).append("\n");
            } else {

                sBuilder.append(sequence.subSequence(0, 60)).append("\n");
            }
            sequence.delete(0, 60);
        }
        sBuilder.deleteCharAt(sBuilder.length() - 1);
        return sBuilder.toString();
    }

    private String fastaToTagged() {
        StringBuilder sBuilder = new StringBuilder();

        BioseqRecord bRecord = this.sequenceHash.get(this.iSeq.next());
        StringBuilder sequence = new StringBuilder(bRecord.getseq().toString());

        sBuilder.append("{\nname        \"").append(bRecord.getID()).append("\"\n");
        if(sequence.toString().matches("^[AGCTURYMKSWHBVDNX\\-]+$")){
            sBuilder.append("type        \"DNA/RNA\"\n");
        } else {
            sBuilder.append("type        \"PROTEIN\"\n");
        }
        sBuilder.append("longnamename        \"").append(bRecord.getID()).append("\"\n");
        sBuilder.append("sequence-ID").append(this.empty);
        sBuilder.append("descrip").append(this.empty);
        sBuilder.append("creator").append(this.empty);
        sBuilder.append("creation-date").append(this.empty);
        sBuilder.append("direction").append(this.empty);
        sBuilder.append("strandedness").append(this.empty);
        sBuilder.append("comments        \"This file was created via GDEParser directly from fasta\n"
                + "without any meta information. Placeholders are given above.\n\"\n");
        sBuilder.append("sequence        \"\n");
        while (sequence.length() > 0) {
            if (sequence.length() >= 50) {
                sBuilder.append(sequence.subSequence(0, 50)).append("\n");
            } else {
                sBuilder.append(sequence).append("\"\n}");

            }
            sequence.delete(0, 50);
        }

        return sBuilder.toString();
    }
}
