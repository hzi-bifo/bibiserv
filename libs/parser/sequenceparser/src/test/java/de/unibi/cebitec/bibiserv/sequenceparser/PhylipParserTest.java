/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser;

import de.unibi.cebitec.bibiserv.sequenceparser.parser.exception.ParserException;

import java.util.LinkedList;

import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserInputFormat;
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
public class PhylipParserTest {

    private String phylip_1;
    private String phylip_2;
    private String phylip_3;
    private Parser parser;
    private String first;
    private String second;
    private String first_fasta;
    private String second_fasta;
    private String fasta;
    private String phylipFasta;

    {
        this.first = "AT-GC-AA-GA-AAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAACATGGCGAAGG"
                + "AAAAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAAC";
        this.second = "ATGGCGAAGGGCGAGTTTGTTCGGACGAAGCCTCACGTGAACGTGGGGACATGGCGAAGG"
                + "AAAAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAAC";

        this.first_fasta = ">the 100 bp\n"
                + "AT-GC-AA-GA-AAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAACATGGCGAAGG\n"
                + "AAAAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAAC";

        this.second_fasta = ">Tthermophi 100 bp\n"
                + "ATGGCGAAGGGCGAGTTTGTTCGGACGAAGCCTCACGTGAACGTGGGGACATGGCGAAGG\n"
                + "AAAAATTTGTGAGAACAAAACCGCATGTTAACGTTGGAAC";
    }

    public PhylipParserTest() {
        try {
            this.phylip_1 = readFile(new FileReader("src/test/testdata/phylip_1.phy"));
            this.phylip_2 = readFile(new FileReader("src/test/testdata/phylip_2.phy"));
            this.phylip_3 = readFile(new FileReader("src/test/testdata/phylip_3.phy"));
            this.fasta = readFile(new FileReader("src/test/testdata/fasta.fas"));
            this.phylipFasta = readFile(new FileReader("src/test/testdata/phylip_fasta.phy"));
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
    public void phylip2FastaTest() {
        this.parser = ParserFactory.createParser(ParserInputFormat.PHYLIP, new StringReader(this.phylip_2));
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
    public void phylipTestTwo() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.PHYLIP, new StringReader(this.phylip_2));
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
    public void phylipTest() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.PHYLIP, new StringReader(this.phylip_1));
        assertTrue(this.parser.isKnownFormat());
        for (int i = 0; i < 7; i++) {
            assertTrue(this.parser.readNext());
            assertTrue(this.parser.nextSeq().getClass().getName().equals("de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord"));
        }
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testPipeline() throws ParserException {
        LinkedList<BioseqRecord> sL = this.pipeline(new StringReader(this.phylip_1));
        assertTrue(sL.size() == 7);
        assertTrue(sL.get((int) Math.random() * 7 + 1).getseq().toString().length() == 100);
    }

    @Test
    public void testSpecial() {
        this.parser = ParserFactory.createParser(ParserInputFormat.PHYLIP, new StringReader(this.phylip_3));
        assertTrue(this.parser.isKnownFormat());

    }

    @Test
    public void testFromFasta() throws ParserException {
        this.parser = ParserFactory.createToFastaParser(ParserOutputFormat.PHYLIP, new StringReader(this.fasta));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        assertTrue(this.phylipFasta.equals(this.parser.nextSeqFromFasta()));
        assertFalse(this.parser.readNext());
    }

    private LinkedList<BioseqRecord> pipeline(StringReader sr) throws ParserException {
        LinkedList<BioseqRecord> sequenceList = new LinkedList<BioseqRecord>();

        this.parser = ParserFactory.createParser(ParserInputFormat.PHYLIP, sr);

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

    private boolean checkSequences(BioseqRecord bRecord) {
        if (bRecord.getID().equals("the")) {
            return bRecord.getseq().toString().equals(this.first);
        } else {
            return bRecord.getseq().toString().equals(this.second);
        }
    }

    private boolean checkFasta(String s) {
        if (s.equals(this.first_fasta)) {
            return true;
        } else if (s.equals(this.second_fasta)) {
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
