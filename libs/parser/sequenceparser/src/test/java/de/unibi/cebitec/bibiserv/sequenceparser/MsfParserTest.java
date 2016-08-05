/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser;

import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserInputFormat;
import de.unibi.cebitec.bibiserv.sequenceparser.parser.exception.ParserException;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.SeqFileInfo;
import java.util.LinkedList;
import java.io.StringReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mrumming
 */
public class MsfParserTest {

    private String msfPileUp;
    private String msfPlain;
    private String msfRich;
    private String msfBroken1;
    private String msfBroken2;
    private Parser parser;
    private String first;
    private String second;
    private String first_fasta;
    private String second_fasta;
    private String fasta;
    private String msfFastaPileUp;
    private String msfFastaPlain;
    private String msfFastaRich;

    {
        this.first = "TAATACAGAGATCCGAAAGAACTCTAAGGAAAACTTGTTTTGGAAAAGTATGGAACCGAG"
                + "TTTTATATTCTACACCGGTACCCTCTTGCTGTCCGGCCTTTCTACACAATGCCATGTCGT"
                + "GACAACGAGTTGTATAGCAACTCATTTGATGTTTTCATTAGAGGGGAGGAGATAATTTCA";

        this.second = "ACACAGAGGTGCAACCATGGTGCTGTCCGCTGCTGACAAGAACAACGTCAAGGGCATCTT"
                + "CACCAAAATCGCCGGCCATGCTGAGGAGTATGGCGCCGAGACCTTGGAAAGGATGTTCAC"
                + "------------------------------------------------------------";

        this.first_fasta = ">DQ160058 389 bp\n"
                + "TAATACAGAGATCCGAAAGAACTCTAAGGAAAACTTGTTTTGGAAAAGTATGGAACCGAG\n"
                + "TTTTATATTCTACACCGGTACCCTCTTGCTGTCCGGCCTTTCTACACAATGCCATGTCGT\n"
                + "GACAACGAGTTGTATAGCAACTCATTTGATGTTTTCATTAGAGGGGAGGAGATAATTTCA";


        this.second_fasta = ">chkhba 180 bp\n"
                + "ACACAGAGGTGCAACCATGGTGCTGTCCGCTGCTGACAAGAACAACGTCAAGGGCATCTT\n"
                + "CACCAAAATCGCCGGCCATGCTGAGGAGTATGGCGCCGAGACCTTGGAAAGGATGTTCAC\n"
                + "------------------------------------------------------------";
    }

    public MsfParserTest() {
        try {
            this.msfPileUp = readFile(new FileReader("src/test/testdata/msf_pileup.msf"));
            this.msfPlain = readFile(new FileReader("src/test/testdata/msf_plain.msf"));
            this.msfRich = readFile(new FileReader("src/test/testdata/msf_rich.msf"));
            this.msfBroken1 = readFile(new FileReader("src/test/testdata/msf_broken_1.msf"));
            this.msfBroken2 = readFile(new FileReader("src/test/testdata/msf_broken_2.msf"));
            this.fasta = readFile(new FileReader("src/test/testdata/fasta.fas"));
            this.msfFastaPileUp = readFile(new FileReader("src/test/testdata/msf_fasta_pileup.msf"));
            this.msfFastaPlain = readFile(new FileReader("src/test/testdata/msf_fasta_plain.msf"));
            this.msfFastaRich = readFile(new FileReader("src/test/testdata/msf_fasta_rich.msf"));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPileUp() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.MSF, new StringReader(this.msfPileUp));

        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        BioseqRecord bRecord = this.parser.nextSeq();
        assertTrue(this.checkSequences(bRecord));

        assertTrue(this.parser.readNext());
        bRecord = this.parser.nextSeq();
        assertTrue(this.checkSequences(bRecord));
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testPlain() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.MSF, new StringReader(this.msfPlain));

        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        BioseqRecord bRecord = this.parser.nextSeq();
        assertTrue(this.checkSequences(bRecord));

        assertTrue(this.parser.readNext());
        bRecord = this.parser.nextSeq();
        assertTrue(this.checkSequences(bRecord));

        assertFalse(this.parser.readNext());
    }

    @Test
    public void testRich() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.MSF, new StringReader(this.msfRich));

        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        BioseqRecord bRecord = this.parser.nextSeq();
        assertTrue(this.checkSequences(bRecord));

        assertTrue(this.parser.readNext());
        bRecord = this.parser.nextSeq();
        assertTrue(this.checkSequences(bRecord));

        assertFalse(this.parser.readNext());
    }

    @Test
    public void testBroken_1() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.MSF, new StringReader(this.msfBroken1));

        assertTrue(this.parser.isKnownFormat());
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testBroken_2() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.MSF, new StringReader(this.msfBroken2));

        boolean b = this.parser.isKnownFormat();
        assertFalse(b);
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testPipelinePileUp() throws ParserException {
        LinkedList<BioseqRecord> sequenceList = this.pipeline(new StringReader(this.msfPileUp));

        assertTrue(this.checkSequences(sequenceList.get(0)));
        assertTrue(this.checkSequences(sequenceList.get(1)));
        assertTrue(sequenceList.size() == 2);
    }

    @Test
    public void testPipelinePlain() throws ParserException {
        LinkedList<BioseqRecord> sequenceList = this.pipeline(new StringReader(this.msfPlain));

        assertTrue(this.checkSequences(sequenceList.get(0)));
        assertTrue(this.checkSequences(sequenceList.get(1)));
        assertTrue(sequenceList.size() == 2);
    }

    @Test
    public void testPipelineRich() throws ParserException {
        LinkedList<BioseqRecord> sequenceList = this.pipeline(new StringReader(this.msfRich));

        assertTrue(this.checkSequences(sequenceList.get(0)));
        assertTrue(this.checkSequences(sequenceList.get(1)));
        assertTrue(sequenceList.size() == 2);
    }

    @Test
    public void testFromFastaPlain() throws ParserException {
        this.parser = ParserFactory.createToFastaParser(ParserOutputFormat.MSF_Plain, new StringReader(this.fasta));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        assertEquals(this.parser.nextSeqFromFasta(),this.msfFastaPlain);
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testFromFastaPileUp() throws ParserException {
        this.parser = ParserFactory.createToFastaParser(ParserOutputFormat.MSF_PileUp, new StringReader(this.fasta));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        assertEquals(this.parser.nextSeqFromFasta(),this.msfFastaPileUp);
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testFromFastaRich() throws ParserException {
        this.parser = ParserFactory.createToFastaParser(ParserOutputFormat.MSF_Rich, new StringReader(this.fasta));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextSeqFromFasta().equals(this.msfFastaRich));
        assertFalse(this.parser.readNext());
    }

    private boolean checkSequences(BioseqRecord bRecord) {
        if (bRecord.getID().equals("DQ160058")) {
            return bRecord.getseq().toString().equals(this.first);
        } else {
            return bRecord.getseq().toString().equals(this.second);
        }
    }

    private LinkedList<BioseqRecord> pipeline(StringReader sr) throws ParserException {
        LinkedList<BioseqRecord> sequenceList = new LinkedList<BioseqRecord>();

        this.parser = ParserFactory.createParser(ParserInputFormat.MSF, sr);

        if (this.parser.isKnownFormat()) {
            while (this.parser.readNext()) {
                SeqFileInfo info = this.parser.nextSeq();

                if (info != null && info.hasid()) {
                    sequenceList.add(new BioseqRecord(info));
                } else {
                    break;
                }
            }
        }


        if (sequenceList.isEmpty() || !sequenceList.getFirst().hasseq()) {
            throw new ParserException("No sequence information detected!");
        }
        return sequenceList;
    }

    private String readFile(FileReader fr) throws IOException {
        BufferedReader r = new BufferedReader(fr);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();

    }
}
