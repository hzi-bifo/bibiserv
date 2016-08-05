/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.gdeparser.parser.impl;

import de.unibi.cebitec.bibiserv.gdeparser.Parser;
import de.unibi.cebitec.bibiserv.gdeparser.enums.MsfType;
import de.unibi.cebitec.bibiserv.gdeparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.gdeparser.parser.exception.ParserException;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.Bioseq;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BasicBioseqDoc;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import org.apache.log4j.Logger;


/**
 *
 * @author mrumming
 */
public class MsfParser extends Parser {
    
     private final static Logger LOG = Logger.getLogger(MsfParser.class);

    private MsfType mType;
    private boolean started = false;

    {
        this.mType = MsfType.Unknown;
    }

    public MsfParser(Reader inputFileReader) {
        this.inputFileReader = new BufferedReader(inputFileReader);
    }

    public MsfParser(Reader inputFileReader, ParserOutputFormat pof) {
        this.inputFileReader = new BufferedReader(inputFileReader);
        this.pof = pof;
    }

    public BioseqRecord nextSeq() throws ParserException {
        return this.sequenceHash.get(this.iSeq.next());
    }

     @Override
    public boolean isKnownFormat() {
        try {
            if (this.pof.equals(ParserOutputFormat.MSF_PileUp) || this.pof.equals(ParserOutputFormat.MSF_Rich) || this.pof.equals(ParserOutputFormat.MSF_Plain)) {
                if (this.startFastaParsing()) {
                    this.iSeq = this.sequenceHash.keySet().iterator();
                    return true;
                } else {
                    return false;
                }
            } else {
                boolean retB = false;
                try {
                    do {
                        this.recentLine = this.inputFileReader.readLine().trim();
                        retB = this.isNewSequence();
                    } while (!retB);
                } catch (IOException | NullPointerException ex) {
                    LOG.debug(ex.getMessage());                 
                    return false;
                } 
                return retB;
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
        if (this.pof.equals(ParserOutputFormat.MSF_PileUp) || this.pof.equals(ParserOutputFormat.MSF_Rich) || this.pof.equals(ParserOutputFormat.MSF_Plain)) {
            return !this.allReadyRun;
        } else {
            if (!started) {
                int seqCount = this.countSequences();
                if (seqCount > 0) {
                    if (this.collectSequences(seqCount)) {
                        this.iSeq = this.sequenceHash.keySet().iterator();
                        this.started = true;
                        return this.iSeq.hasNext();
                    } else {
                        this.started = true;
                        return false;
                    }
                } else {
                    this.started = true;
                    return false;
                }
            } else {
                if (this.iSeq == null) {
                    return false;
                } else {
                    return this.iSeq.hasNext();
                }
            }
        }
    }

    private boolean collectSequences(int seqCount) {
        HashMap<String, StringBuilder> hTemp = new HashMap<String, StringBuilder>();
        try {
            this.recentLine = this.inputFileReader.readLine();
            while (this.recentLine != null) {
                if (this.recentLine.trim().length() > 0) {
                    String[] sTemp = this.recentLine.trim().split("\\s+");
                    if (sTemp.length > 1 && !sTemp[sTemp.length - 1].matches("\\d+")) {
                        StringBuilder sBuildTemp = new StringBuilder();
                        for (int i = 1; i < sTemp.length; i++) {
                            sBuildTemp.append(this.reformat(sTemp[i]));
                        }
                        if (hTemp.containsKey(sTemp[0])) {
                            hTemp.put(sTemp[0], hTemp.get(sTemp[0]).append(sBuildTemp));
                        } else {
                            hTemp.put(sTemp[0], sBuildTemp);
                        }
                    }
                }
                this.recentLine = this.inputFileReader.readLine();
            }
            for (Iterator<String> i = hTemp.keySet().iterator(); i.hasNext();) {
                String sTemp = i.next();
                this.sequenceHash.put(sTemp, new BioseqRecord(new Bioseq(hTemp.get(sTemp).toString()), new BasicBioseqDoc(sTemp), sTemp));
            }
            return sequenceHash.size() == seqCount ;
        } catch (IOException ex) {
            LOG.debug(ex.getMessage());
            
            return false;
        }
    }

    private int countSequences() {
        int seqCount = 0;
        try {
            this.recentLine = this.inputFileReader.readLine();
            do {
                if (this.recentLine.trim().startsWith("Name:")) {
                    seqCount++;
                }
                this.recentLine = this.inputFileReader.readLine();
            } while (!this.recentLine.trim().equals("//"));
        } catch (IOException | NullPointerException ex) {
            LOG.debug(ex.getMessage());          
            return 0;
        } 
        return seqCount;
    }

    @Override
    protected boolean isNewSequence() {
        boolean retB = true;
        if (this.recentLine.trim().startsWith("PileUp")) {
            this.mType = MsfType.PileUp;
        } else if (this.recentLine.trim().startsWith("MSF:")) {
            this.mType = MsfType.Plain;
        } else if (this.recentLine.trim().startsWith("!!NA_MULTIPLE_ALIGNMENT")
                || this.recentLine.trim().startsWith("!!AA_MULTIPLE_ALIGNMENT")) {
            this.mType = MsfType.Rich;
        } else {
            this.mType = MsfType.Unknown;
            retB = false;
        }

        //try {
        //switch (this.recentLine.trim().) {
        //  case '{':
        //    break;
        //default:
        //  break;
        //  }
        //} catch (StringIndexOutOfBoundsException sioobe) {
        //  retB = false;
        //this.fileEnded = true;
        //} catch (NullPointerException npe) {
        //  retB = false;
        //this.fileEnded = true;
        //}

        return retB;
    }

    @Override
    protected String nextSeqFromFasta() throws ParserException {
        StringBuilder sBuilder = new StringBuilder();
        int type = 0;



        int maxSeqLen = 0;
        int maxNameLen = 0;

        String[] names = new String[this.sequenceHash.size()];
        String[] sequences = new String[this.sequenceHash.size()];

        int temp = 0;
        for (String name : this.sequenceHash.keySet()) {
            names[temp] = name;
            temp++;
        }



        //Setup
        for (int i = 0; i < names.length; i++) {
            sequences[i] = this.sequenceHash.get(names[i]).getseq().toString();
            if (!sequences[i].toString().matches("^[AGCTURYMKSWHBVDNX\\-]+$")) {
                type = 1;
            }
            if (maxSeqLen < sequences[i].length()) {
                maxSeqLen = sequences[i].length();
            }
            if (maxNameLen < names[i].length()) {
                maxNameLen = names[i].length();
            }
        }


        switch (this.pof) {
            case MSF_Rich:
                if (type == 0) {
                    sBuilder.append("!!NA_MULTIPLE_ALIGNMENT 1.0\n");
                } else {
                    sBuilder.append("!!AA_MULTIPLE_ALIGNMENT 1.0\n");
                }
                sBuilder.append("PileUp of: @seqlist\n\n Symbol comparison table: NONE  CompCheck: NONE\n\n");
                sBuilder.append("                   GapWeight: NONE\n             GapLengthWeight: NONE\n\n");
                sBuilder.append(" NONE.msf  MSF: ").append(maxSeqLen).append("  Type: ");
                if (type == 0) {
                    sBuilder.append("A Check: NONE ..\n\n");
                } else {
                    sBuilder.append("P Check: NONE ..\n\n");
                }
                break;
            case MSF_PileUp:
                sBuilder.append("PileUp\n\n\n\n");
            case MSF_Plain:
                sBuilder.append("   MSF:  ").append(maxSeqLen).append("  Type: ");
                if (type == 0) {
                    sBuilder.append("A    Check:  NONE   ..\n\n");
                } else {
                    sBuilder.append("P    Check:  NONE   ..\n\n");
                }

        }

        for (int i = 0; i < names.length; i++) {
            sBuilder.append(" Name: ").append(names[i]).append("  Len: ").append(sequences[i].length()).append("  Check: NONE  Weight: NONE\n");
        }
        sBuilder.append("\n//");

        //Generation of sequence blocks
        while (maxSeqLen > 0) {
            sBuilder.append("\n\n");
            int maxActualSeqLen = 0;
            for (int i = 0; i < names.length; i++) {
                int recentSeqLen = sequences[i].length();
                sBuilder.append(names[i]);
                for (int j = 0; j < names[i].length() - maxNameLen; j++) {
                    sBuilder.append(" ");
                }
                sBuilder.append("     ");
                sBuilder.append(this.generateBlock(sequences[i], maxSeqLen));
                sBuilder.append("\n");
                if (sequences[i].length() <= 50) {
                    sequences[i] = "";
                } else {
                    sequences[i] = sequences[i].substring(50);
                }
                recentSeqLen -= sequences[i].length();
                if (recentSeqLen > maxActualSeqLen) {
                    maxActualSeqLen = recentSeqLen;
                }
            }
            maxSeqLen -= maxActualSeqLen;
        }
        this.allReadyRun = true;
        return sBuilder.toString();
    }

    //DONE
    private String generateBlock(String s, int len) {
        if (s.length() == 0) {
            StringBuilder sBuilder = new StringBuilder(" ---------- ---------- ---------- ---------- ----------");
            if (len >= 50) {
                return sBuilder.toString();
            } else {
                sBuilder.delete(this.computeLen(len), 55);
                return sBuilder.toString();
            }
        }
        if (len >= 50) {
            return this.generateSegments(s);
        } else {
            String retString = this.generateSegments(s);
            return retString.substring(0, this.computeLen(len));
        }
    }

    //DONE
    private int computeLen(int seqLen) {
        int retInt = 0;
        retInt += seqLen + seqLen / 10;
        if (seqLen % 10 != 0) {
            retInt += 1;
        }
        return retInt;
    }

    //TESTEN
    private String generateSegments(String s) {
        StringBuilder sBuilder = new StringBuilder();

        for (int i = 1; i < 6; i++) {
            sBuilder.append(" ");
            if (s.length() < i * 10) {
                sBuilder.append(s.substring(i * 10 - 10));
                for (int j = s.substring(i * 10 - 10).length(); j < 10; j++) {
                    sBuilder.append("-");
                }
                break;
            } else {
                sBuilder.append(s.substring(i * 10 - 10, i * 10));
            }
        }

        for (int i = sBuilder.length() / 11; i < 5; i++) {
            sBuilder.append(" ----------");
        }

        return sBuilder.toString();
    }
}
