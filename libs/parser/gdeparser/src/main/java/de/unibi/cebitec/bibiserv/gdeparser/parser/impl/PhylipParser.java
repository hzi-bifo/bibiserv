/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.gdeparser.parser.impl;

import de.unibi.cebitec.bibiserv.gdeparser.Parser;
import de.unibi.cebitec.bibiserv.gdeparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.gdeparser.enums.PhylipType;
import de.unibi.cebitec.bibiserv.gdeparser.parser.exception.ParserException;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.Bioseq;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BasicBioseqDoc;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import org.apache.log4j.Logger;


/**
 *
 * @author mrumming
 */
public class PhylipParser extends Parser {

    private final static Logger LOG = Logger.getLogger(PhylipParser.class);
    
    private PhylipType pType;
    private boolean started = false;
    private int seqCount;
    private int seqLength;

    {
        //CHANGE!!!
        //this.pType = PhylipType.Unknown;
        this.pType = PhylipType.InterleavedStandardnames;
        //CHANGE_END
        this.seqCount = 0;
        this.seqLength = 0;
    }

    public PhylipParser(Reader inputFileReader) {
        this.inputFileReader = new BufferedReader(inputFileReader);
        this.initParser();
    }

    public PhylipParser(Reader inputFileReader, ParserOutputFormat pof) {
        this.inputFileReader = new BufferedReader(inputFileReader);
        this.pof = pof;
    }

    private void initParser() {
        try {
            this.recentLine = this.inputFileReader.readLine();
            String[] sArr = this.recentLine.trim().split("\\s+");
            this.seqCount = Integer.parseInt(sArr[0]);
            this.seqLength = Integer.parseInt(sArr[1]);
        } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException | NullPointerException ex) {
            LOG.debug("Error initialize parser :"+ex.getMessage());
            this.resetParser();
        }
    }

    private void resetParser() {
        this.seqCount = 0;
        this.seqLength = 0;
    }

    @Override
    public BioseqRecord nextSeq() throws ParserException {
        return this.sequenceHash.get(iSeq.next());
    }

    @Override
    public boolean isKnownFormat() {
        try {
            if (this.pof.equals(ParserOutputFormat.PHYLIP)) {
                if (this.startFastaParsing()) {
                    this.iSeq = this.sequenceHash.keySet().iterator();
                    return true;
                } else {
                    return false;
                }
            } else {
                if (this.seqCount != 0) {
                    switch (this.pType) {
                        case InterleavedStandardnames:
                            if (this.collectSequencesInterleavedStandard()) {
                                this.iSeq = this.sequenceHash.keySet().iterator();
                                return true;
                            } else {
                                return false;
                            }
                        //case InterleavedSpacesNames:
                        //  return this.collectSequencesInterleavedSpaces();
                        //case SequentialStandardnames:
                        //  return this.collectSequencesSequentialStandard();
                        //case SequentialSpacesNames:
                        //  return this.collectSequencesSequentialSpaces();
                        default:
                            return false;
                    }
                } else {
                    return false;
                }
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
            if (this.pof.equals(ParserOutputFormat.PHYLIP)) {
                return !this.allReadyRun;
            } else {
                return this.iSeq.hasNext();
            }
        }
    }

    private boolean collectSequencesInterleavedStandard() {
        String[] names = new String[this.seqCount];
        StringBuilder[] sequences = new StringBuilder[this.seqCount];
        try {
            for (int i = 0; i < this.seqCount; i++) {
                this.recentLine = this.inputFileReader.readLine();
                if (this.recentLine.length() > 10) {
                    if (this.recentLine.trim().split("\\s+").length >= 2) {
                        String[] splitted = this.recentLine.trim().split("\\s+");
                        names[i] = splitted[0];
                        sequences[i] = new StringBuilder(this.reformat(splitted[1]));
                        for (int j = 2; j < splitted.length; j++) {
                            sequences[i].append(this.reformat(splitted[j]));
                        }
                    } else {
                        names[i] = this.recentLine.trim().substring(0, 10).trim();
                        sequences[i] = new StringBuilder(this.reformat(this.recentLine.trim().subSequence(10, this.recentLine.trim().length()).toString()));
                    }
                } else {
                    names[i] = this.recentLine.trim();
                    sequences[i] = new StringBuilder();
                }

            }
            this.recentLine = this.inputFileReader.readLine();
            while (this.recentLine != null) {
                while (this.recentLine.length() == 0) {
                    this.recentLine = this.inputFileReader.readLine();
                    if (this.recentLine == null) {
                        break;
                    }
                }
                for (int i = 0; i < this.seqCount; i++) {
                    try {
                        sequences[i].append(this.reformat(this.recentLine.trim()));
                    } catch (NullPointerException npe) {
                        //do something useful or nothing ;)
                    }
                    this.recentLine = this.inputFileReader.readLine();
                }
            }



            for (int i = 0; i < this.seqCount; i++) {
                this.sequenceHash.put(names[i], new BioseqRecord(new Bioseq(sequences[i].toString().trim()), new BasicBioseqDoc(names[i]), names[i]));
            }
            if (this.sequenceHash.size() == this.seqCount) {
                boolean bTemp = true;
                for (int i = 0; i < this.seqCount; i++) {
                    if (sequences[i].length() != this.seqLength) {
                        bTemp = false;
                    }
                }
                return bTemp;
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

    public void setPType(PhylipType pType) {
        this.pType = pType;
    }

    public PhylipType getPType() {
        return this.pType;
    }

    @Override
    protected String nextSeqFromFasta() throws ParserException {
        StringBuilder sBuilder = new StringBuilder();
        String[] names = new String[this.sequenceHash.size()];
        String[] sequences = new String[this.sequenceHash.size()];

        //Setup
        int maxSeqLen = 0;
        int temp = 0;
        for (String name : this.sequenceHash.keySet()) {
            String nameBefore = name;
            while (name.length() != 10) {
                if (name.length() > 10) {
                    name = name.substring(0, name.length() - 1);
                } else {
                    name = name + " ";
                }
            }
            names[temp] = name;
            sequences[temp] = this.sequenceHash.get(nameBefore).getseq().toString();
            if (maxSeqLen < sequences[temp].length()) {
                maxSeqLen = sequences[temp].length();
            }
            temp++;
        }

        //First line
        sBuilder.append(names.length).append(" ").append(maxSeqLen).append("\n");

        while (maxSeqLen > 0) {
            for (int i = 0; i < names.length; i++) {
                sBuilder.append(names[i]);
                if (sequences[i].length() >= 50) {
                    sBuilder.append(sequences[i].substring(0, 50)).append("\n");
                    sequences[i] = sequences[i].substring(50);
                } else {
                    sBuilder.append(this.buildBlock(sequences[i], maxSeqLen)).append("\n");
                    sequences[i] = "";
                }
            }
            sBuilder.append("\n\n");
            maxSeqLen -= 50;
        }



        this.allReadyRun = true;
        return sBuilder.toString();
    }

    private String buildBlock(String s, int maxSeqLen) {
        if (s.length() == 0) {
            if(maxSeqLen>50){
                return "--------------------------------------------------";
            } else {
                return this.buildGaps(maxSeqLen);
            }
        } else {
            StringBuilder sBuilder = new StringBuilder(s);
            if(s.length() != maxSeqLen){
                sBuilder.append(this.buildGaps(50-s.length()));
            }
            return sBuilder.toString();
        }
    }

    private String buildGaps(int len){
        StringBuilder sBuilder = new StringBuilder();
        for(int i=0; i<len; i++){
            sBuilder.append("-");
        }
        return sBuilder.toString();
    }
}
