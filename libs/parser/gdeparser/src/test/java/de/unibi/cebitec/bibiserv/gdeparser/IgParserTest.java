/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.gdeparser;

import de.unibi.cebitec.bibiserv.gdeparser.parser.exception.ParserException;

import java.util.LinkedList;

import de.unibi.cebitec.bibiserv.gdeparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.gdeparser.enums.ParserInputFormat;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.SeqFileInfo;
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
public class IgParserTest {

    private String ig_single;
    private String ig_multi;
    private String ig_alignment;
    private Parser parser;
    private String first;
    private String second;
    private String third;
    private String first_fasta;
    private String second_fasta;
    private String third_fasta;

    private String fasta;
    private String igFasta1;
    private String igFasta2;

    {
        this.first = "ATGGCGAAGGGCGAGTTTGTTCGGACGAAGCCTCACGTGAACGTGGGGACATGGCGAAGG"
                + "AAAAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAAC";

        this.second = "AT-GC-AA-GA-AAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAACATGGCGAAGG"
                + "AAAAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAAC";

        this.third = "--AT-GC-AA-GA-AAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAACATGGCGAA"
                + "AACGCGTAGTAGCGACTCG----CGTAGC--CGATC----";


        this.first_fasta = ">Tthermophi 100 bp\n"
                + "ATGGCGAAGGGCGAGTTTGTTCGGACGAAGCCTCACGTGAACGTGGGGACATGGCGAAGG\n"
                + "AAAAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAAC";

        this.second_fasta = ">the 100 bp\n"
                + "AT-GC-AA-GA-AAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAACATGGCGAAGG\n"
                + "AAAAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAAC";
        this.third_fasta = ">TeH 100 bp\n"
                + "--AT-GC-AA-GA-AAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAACATGGCGAA\n"
                + "AACGCGTAGTAGCGACTCG----CGTAGC--CGATC----";

    }

    public IgParserTest() {
        try {
            this.ig_single = readFile(new FileReader("src/test/testdata/ig_single.ig"));
            this.ig_multi = readFile(new FileReader("src/test/testdata/ig_multi.ig"));
            this.ig_alignment = readFile(new FileReader("src/test/testdata/ig_alignment.ig"));
            this.fasta = readFile(new FileReader("src/test/testdata/fasta.fas"));
            this.igFasta1 = readFile(new FileReader("src/test/testdata/ig_fasta_1.ig"));
            this.igFasta2 = readFile(new FileReader("src/test/testdata/ig_fasta_2.ig"));
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
    public void ig2FastaAlignmentTest() {
        this.parser = ParserFactory.createParser(ParserInputFormat.IG, new StringReader(this.ig_alignment));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);

        assertTrue(this.parser.isKnownFormat());

        assertTrue(this.parser.readNext());
        String output = (String) this.parser.nextElement();
        assertTrue(this.checkFasta(output));

        assertTrue(this.parser.readNext());
        output = (String) this.parser.nextElement();
        assertTrue(this.checkFasta(output));

        assertFalse(this.parser.readNext());
    }

    @Test
    public void ig2FastaMultiTest() {
        this.parser = ParserFactory.createParser(ParserInputFormat.IG, new StringReader(this.ig_multi));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);

        assertTrue(this.parser.isKnownFormat());

        assertTrue(this.parser.readNext());
        String output = (String) this.parser.nextElement();
        assertTrue(this.checkFasta(output));

        assertTrue(this.parser.readNext());
        output = (String) this.parser.nextElement();
        assertTrue(this.checkFasta(output));

        assertFalse(this.parser.readNext());
    }

    @Test
    public void ig2FastaSingleTest() {
        this.parser = ParserFactory.createParser(ParserInputFormat.IG, new StringReader(this.ig_single));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);

        assertTrue(this.parser.isKnownFormat());

        assertTrue(this.parser.readNext());
        String output = (String) this.parser.nextElement();
        assertTrue(this.checkFasta(output));

        assertFalse(this.parser.readNext());
    }

    @Test
    public void igTestSingle() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.IG, new StringReader(this.ig_single));
        assertTrue(this.parser.isKnownFormat());

        assertTrue(this.parser.readNext());
        BioseqRecord bRecord = this.parser.nextSeq();
        assertTrue(this.checkSequences(bRecord));

        assertFalse(this.parser.readNext());

    }

    @Test
    public void igTestMulti() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.IG, new StringReader(this.ig_multi));
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
    public void igTestAlignment() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.IG, new StringReader(this.ig_alignment));
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
    public void testPipeline() throws ParserException {
        LinkedList<BioseqRecord> sL = this.pipeline(new StringReader(this.ig_multi));
        assertTrue(sL.size() == 2);
    }

    private LinkedList<BioseqRecord> pipeline(StringReader sr) throws ParserException {
        LinkedList<BioseqRecord> sequenceList = new LinkedList<BioseqRecord>();

        this.parser = ParserFactory.createParser(ParserInputFormat.IG, sr);

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

    @Test
    public void testFromFasta() throws ParserException {
        this.parser = ParserFactory.createToFastaParser(ParserOutputFormat.IG, new StringReader(this.fasta));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextSeqFromFasta().equals(this.igFasta1));
        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextSeqFromFasta().equals(this.igFasta2));
        assertFalse(this.parser.readNext());
    }

    private boolean checkSequences(BioseqRecord bRecord) {
        if (bRecord.getID().equals("Tthermophi")) {
            return bRecord.getseq().toString().equals(this.first);
        } else if (bRecord.getID().equals("the")) {
            return bRecord.getseq().toString().equals(this.second);
        } else {
            return bRecord.getseq().toString().equals(this.third);
        }
    }

    private boolean checkFasta(String s) {
        if (s.equals(this.first_fasta)) {
            return true;
        } else if (s.equals(this.second_fasta)) {
            return true;
        } else if (s.equals(this.third_fasta)) {
            return true;
        } else {
            return false;
        }
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
