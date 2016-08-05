/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.gdeparser;

import de.unibi.cebitec.bibiserv.gdeparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.gdeparser.enums.ParserInputFormat;
import de.unibi.cebitec.bibiserv.gdeparser.parser.exception.ParserException;
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
public class RsfParserTest {

    private String single_seq;
    private String double_seq;
    private String broken_seq;
    private Parser parser;
    private String first;
    private String second;
    private String first_fasta;
    private String second_fasta;
    private String fasta;
    private String rsfFasta1;

    {
        this.first = "TAATACAGAGATCCGAAAGAACTCTAAGGAAAACTTGTTTTGGAAAAGTATGGAACCGAG"
                + "TTTTATATTCTACACCGGTACCCTCTTGCTGTCCGGCCTTTCTACACAATGCCATGTCGT"
                + "GACAACGAGTTGTATAGCAACTCATTTGATGTTTTCATTAGAGGGGAGGAGATAATTTCA"
                + "GGAGCTCAACGTGTGCACATACCTGAACTTTTGGAGGCACGTGCAACTGCATGTGGGATT"
                + "GATCTCAAAACCATATCATCATACATTGATTCCTTCAGGTATGGTGCGCCTCCACATGGC"
                + "GGGATTGGAGTTGGATTGGAACGTGTTGTGATGCTTTTTTGTGGCCTTGATAACATTCGT"
                + "AAAGTCTCACTTTTCCCACGTGACCTTCG";

        this.second = "ACACAGAGGTGCAACCATGGTGCTGTCCGCTGCTGACAAGAACAACGTCAAGGGCATCTT"
                + "CACCAAAATCGCCGGCCATGCTGAGGAGTATGGCGCCGAGACCTTGGAAAGGATGTTCAC"
                + "CACCTACCCCCCAACCAAGACCTACTTCCCCCACTTCGATCTGTCACACGGCTCCGCTCA";


        this.first_fasta = ">DQ160058 Taraxacum officinale TO52-2 (To52-2) mRNA, partial cds. 389 bp\n"
                + "TAATACAGAGATCCGAAAGAACTCTAAGGAAAACTTGTTTTGGAAAAGTATGGAACCGAG\n"
                + "TTTTATATTCTACACCGGTACCCTCTTGCTGTCCGGCCTTTCTACACAATGCCATGTCGT\n"
                + "GACAACGAGTTGTATAGCAACTCATTTGATGTTTTCATTAGAGGGGAGGAGATAATTTCA\n"
                + "GGAGCTCAACGTGTGCACATACCTGAACTTTTGGAGGCACGTGCAACTGCATGTGGGATT\n"
                + "GATCTCAAAACCATATCATCATACATTGATTCCTTCAGGTATGGTGCGCCTCCACATGGC\n"
                + "GGGATTGGAGTTGGATTGGAACGTGTTGTGATGCTTTTTTGTGGCCTTGATAACATTCGT\n"
                + "AAAGTCTCACTTTTCCCACGTGACCTTCG";

        this.second_fasta = ">chkhba 180 bp\n"
                + "ACACAGAGGTGCAACCATGGTGCTGTCCGCTGCTGACAAGAACAACGTCAAGGGCATCTT\n"
                + "CACCAAAATCGCCGGCCATGCTGAGGAGTATGGCGCCGAGACCTTGGAAAGGATGTTCAC\n"
                + "CACCTACCCCCCAACCAAGACCTACTTCCCCCACTTCGATCTGTCACACGGCTCCGCTCA";
    }

    public RsfParserTest() {
        try {
            this.single_seq = readFile(new FileReader("src/test/testdata/rsf_dna_single.rsf"));
            this.double_seq = readFile(new FileReader("src/test/testdata/rsf_dna_multi.rsf"));
            this.broken_seq = readFile(new FileReader("src/test/testdata/rsf_single_broken.rsf"));
            this.fasta = readFile(new FileReader("src/test/testdata/fasta.fas"));
            this.rsfFasta1 = readFile(new FileReader("src/test/testdata/rsf_fasta.rsf"));
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
    public void test_single() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.RSF, new StringReader(this.single_seq));

        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());

        BioseqRecord bRecord = this.parser.nextSeq();
        assertTrue(bRecord.getseq().toString().equals(this.first));
        assertFalse(this.parser.readNext());
    }

    @Test
    public void test_double() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.RSF, new StringReader(this.double_seq));

        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());

        BioseqRecord bRecord = this.parser.nextSeq();
        assertTrue(bRecord.getseq().toString().equals(this.first));

        assertTrue(this.parser.readNext());

        bRecord = this.parser.nextSeq();
        assertTrue(bRecord.getseq().toString().equals(this.second));
        assertFalse(this.parser.readNext());
    }

    @Test
    public void test_single_fasta() {
        this.parser = ParserFactory.createParser(ParserInputFormat.RSF, new StringReader(this.single_seq));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);

        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextElement().equals(this.first_fasta));
        assertFalse(this.parser.readNext());
    }

    @Test
    public void test_double_fasta() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.RSF, new StringReader(this.double_seq));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);

        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());

        assertTrue(this.parser.nextElement().equals(this.first_fasta));
        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextElement().equals(this.second_fasta));
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testpipeline() throws ParserException {
        LinkedList<BioseqRecord> sequenceList = new LinkedList<BioseqRecord>();

        this.parser = ParserFactory.createParser(ParserInputFormat.RSF, new StringReader(this.double_seq));

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
        assertTrue(sequenceList.get(0).getseq().toString().equals(this.first));
        assertTrue(sequenceList.get(1).getseq().toString().equals(this.second));
        assertTrue(sequenceList.size() == 2);
    }

    @Test
    public void testFromFasta() throws ParserException {
        this.parser = ParserFactory.createToFastaParser(ParserOutputFormat.RSF, new StringReader(this.fasta));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        assertTrue(this.rsfFasta1.equals(this.parser.nextSeqFromFasta()));
        assertFalse(this.parser.readNext());
    }

    private String readFile(FileReader fr) throws IOException {
        BufferedReader r = new BufferedReader(fr);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();

    }
}
