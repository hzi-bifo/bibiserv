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
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 *
 * @author mrumming
 */
public class DialignParser extends Parser {

    
    private static final Logger LOG = Logger.getLogger(DialignParser.class);
    
    public DialignParser(Reader inputFileReader) {
        this.inputFileReader = new BufferedReader(inputFileReader);
    }

    public DialignParser(Reader inputFileReader, ParserOutputFormat pof) {
        this.inputFileReader = new BufferedReader(inputFileReader);
        this.pof = pof;
    }

    public BioseqRecord nextSeq() throws ParserException {
        return this.sequenceHash.get(iSeq.next());
    }

    public boolean isKnownFormat() {
        try {
            if (this.pof.equals(ParserOutputFormat.DIALIGN)) {
                if (this.startFastaParsing()) {
                    this.iSeq = this.sequenceHash.keySet().iterator();
                    return true;
                } else {
                    return false;
                }
            } else {
                if (this.collectSequences()) {
                    this.iSeq = this.sequenceHash.keySet().iterator();
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IOException iex) {
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
            if (this.pof.equals(ParserOutputFormat.DIALIGN)) {
                return !this.allReadyRun;
            } else {
                return this.iSeq.hasNext();
            }
        }
    }

    private boolean collectSequences() {
        List<String> names = new LinkedList<String>();
        List<StringBuilder> sequences = new LinkedList<StringBuilder>();
        boolean retB = true;
        try {
            while ((this.recentLine = this.inputFileReader.readLine()) != null) {
                if (!this.recentLine.trim().matches("[\\**\\s*]*")) {
                    String[] sTempArr = this.recentLine.trim().split("\\s+");
                    int index = 0;
                    if (names.contains(sTempArr[0])) {
                        index = names.indexOf(sTempArr[0]);
                    } else {
                        names.add(sTempArr[0]);
                        sequences.add(new StringBuilder());
                        index = names.size() - 1;
                    }
                    int beginSeq = 1;
                    if (sTempArr[1].matches("\\d+")) {
                        beginSeq = 2;
                    }
                    for (; beginSeq < sTempArr.length; beginSeq++) {
                        sequences.get(index).append(sTempArr[beginSeq]);
                    }
                }
            }
            for (int i = 0; i < sequences.size(); i++) {
                if (sequences.get(0).length() != sequences.get(i).length()) {
                    return false;
                } else {
                    this.sequenceHash.put(names.get(i), new BioseqRecord(new Bioseq(this.reformat(sequences.get(i).toString().trim())), new BasicBioseqDoc(names.get(i)), names.get(i)));
                }
            }
            return retB;
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

        int maxSeqLen = 0;
        int maxNameLen = 0;
        List<Integer> positions = new LinkedList<Integer>();

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
            if (maxSeqLen < sequences[i].length()) {
                maxSeqLen = sequences[i].length();
            }
            if (maxNameLen < names[i].length()) {
                maxNameLen = names[i].length();
            }
            positions.add(1);
        }

        //Generation of sequence blocks
        while (maxSeqLen > 0) {
            int maxActualSeqLen = 0;
            for (int i = 0; i < names.length; i++) {
                int recentSeqLen = sequences[i].length();
                sBuilder.append(names[i]);
                String s = this.generateBlock(sequences[i], maxSeqLen);
                if (positions.get(i) == -1 || this.countChars(s) == 0) {
                    sBuilder.append("          ");
                } else {
                    for (int j = 0; j < 10 + maxNameLen - names[i].length() - positions.get(i).toString().length(); j++) {
                        sBuilder.append(" ");
                    }
                    sBuilder.append(positions.get(i));
                }
                sBuilder.append("  ");
                sBuilder.append(s);
                sBuilder.append("\n");
                if (sequences[i].length() <= 50) {
                    sequences[i] = "";
                    positions.set(i, -1);
                } else {
                    sequences[i] = sequences[i].substring(50);
                    positions.set(i, positions.get(i) + this.countChars(s));
                }
                recentSeqLen -= sequences[i].length();
                if (recentSeqLen > maxActualSeqLen) {
                    maxActualSeqLen = recentSeqLen;
                }
            }
            maxSeqLen -= maxActualSeqLen;
            sBuilder.append("\n\n");
        }


        this.allReadyRun = true;
        return sBuilder.toString();
    }

    //DONE
    private int countChars(String s) {
        String[] sArr = s.split("[- ]");
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < sArr.length; i++) {
            sBuilder.append(sArr[i]);
        }
        return (sBuilder.length());
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

    //DONE - unused
    private int analyzeSeqStart(String s) {
        if (s.startsWith("-")) {
            return s.length() - s.split("[ \\-]+", 2)[1].length();
        } else {
            return 1;
        }
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
