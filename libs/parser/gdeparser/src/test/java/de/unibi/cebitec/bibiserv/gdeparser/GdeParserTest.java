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
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class GdeParserTest {

    private String testfile1;
    private String testfile2;
    private String testfile3;
    private String specialCase;
    private Parser parser;
    private String first;
    private String second;
    private String special;
    private String first_fasta_flat;
    private String second_fasta_flat;
    private String first_fasta_ASN_1;
    private String second_fasta_ASN_1;
    private String fasta;
    private String gdeFastaFlat1;
    private String gdeFastaFlat2;
    private String gdeFastaTagged1;
    private String gdeFastaTagged2;

    {
        this.first = "CUAUCCAUGCGUUUCUUUGCUACUUUCUUUCUUCUAGCUAUGCUUGUCGUGGCUACUAAG"
                + "AUGGGACCAAUGAGAAUUGCAGAGGCAAGACAUUGCGAGUCGUUGAGCCAUCGUUUCAAG"
                + "GGACCAUGUACGAGAGAUAGCAAUUGUGCUUCGGUCUGUGAGACCGAAAGAUUUUCCGGU"
                + "GGCAAUUGCCAUGGAUUCCGUCGCCGUUGCUUUUGCACUAAGCCAUGCUAAAUGAGUAUU"
                + "AAAAAUUAUGUGUAAUAGAAGAAGUUUGAGAAAAAAAUUAUGUACUCUUGAAUAAAGUAC"
                + "ACUAUGAUUGUUCAAAGAUAUAUGUGGUGCUAGUUUUGUUUGUAAAACUAGUCGUGAUCU"
                + "UUGAAUUUAUAUGCAAUUAUGGUGCACUAGACUUGUUAAUUUCUUCAUGUGAUGUAUUUU"
                + "UUGCUCUUUUGUUAUGAAAUAUUAUGGAUAAAAUUUGUCUUUUAGUCUUU";

        this.second = "AAGCTTAACTACTAATGTATACACACACTCAAAGAATTTTACTTCTAAAAGATGTGGTAG"
                + "AATATTAAGCTAGCGTTTGGACCTAGATCTTGTTGAAACTTGAGAATTTGAGTTTTTGCA"
                + "ATTTAGTTGAAAAATAATTTTTGAAACTTCAAATTGTGTTTGGACATACATTTTACTTGA"
                + "AAAAAAATTGAAAGTACTTATTATCCTTAACTAAAATTATTGAGTACGAGTCTTGAAAAT"
                + "AAAGTCGTTTTTTATAACGAATGCACGAGTCCTTTAAAGTGGGACTTTCAATCACGAATC"
                + "CTCAATAATGGAACCAAAAATAATTGCTAGACACCAGGTTAGAAACCAAAGCAAAACAAT"
                + "ATTTTAACACTCTCGGCGACGAAATAATTACAGGCAATTTTTCCAACAGAATGAGTAATT"
                + "ACTTAAGGAATAGAGCAGATTACATGTGACACCTAATTAAAATACAGTAATAGGAGCAAG"
                + "TATAAAAACACTTACATGGGCATGATAAAGAAATTAAGTAGAACTGTGCTTGGCATAGCT"
                + "AGCTAGTATATCCTTTCTTTAAGCTGCTAACACTGGACTGACGTATTATTACAAGTAACC"
                + "ATGCGCAGAATTCTTTAACCACATACTCGTAAATAAGCGCCTATAAAACACCCCATATGC"
                + "TAGTTCCACCTTGCTAGTACTAAACTTCTTTCCACATTAATCTCCCTTCTCTCTCTTTCT"
                + "CTGTGTGAAGATGGGCCGCTCCATTCGTTTGTTCGCAACTTTCTTCCTTATTGCAATGCT"
                + "ATTTTTGTCCACTGGTTCGTCTCAGATCTTCCTTGTTAACGTTCTTTTCTTATACTACTT"
                + "CTATCACATGTAGCACATAGTTCTTCAATTAAAGACGCACCCTTTAACCATGTTGAGTTT"
                + "CTTTCTTTATTGCAACAACAGGCGGTCAATCTATTGAATATCAATGCATATTTATTTGAA"
                + "ATTTAAGTTATTACTTATATACACTATAGTAGTCGTATTTATTTTTATAGGTTAAAAGAT"
                + "AGAACGATTGTTATTATTATACTGTAACAATAACACCATATGGTCTTATTATGTATTATT"
                + "CCTGTAATATCTGATTCTTGATTTTAATTTTGCTTACTTATTCCAGAGATGGGACCAATG"
                + "ACAAGTGCAGAGGCAAGGACCTGTGAGTCACAGAGCCACCGTTTCCATGGAACATGTGTT"
                + "AGGGAGAGCAACTGTGCTTCCGTTTGCCAGACAGAGGGCTTCATCGGCGGCAACTGCCGT"
                + "GCGTTCCGCCGTCGTTGCTTCTGCACCAGAAACTGTTAGAAAATACTAAGAAAGTTGTTA"
                + "GTGAAATGACGCTGTTCCTGTAATACTGTAGTAGTTTCTTTTCTAAATTCCGATGAATAA"
                + "AGACTATCTCACACATGGTGGTGTTTAGTTATGGTTGTATGTATTCTATTTCTATGTCTC"
                + "TCTTTTTTTCGGTGCTGGTTATCTATTGAACGATAATTAATCATATTTATTAGTTATTAC"
                + "TATCTCCGCTCCAATTTAAATATTTTATTTTTATTTTAGTATGTCTTATAAAAGACTAGT"
                + "T";

        this.special = ">ST322R Potato mRNA for tuber protein (p322), put. proteinase inhibitor. 50 bp\n"
                + "UUGCUCUUUUGUUAUGAAAUAUUAUGGAUAAAAUUUGUCUUUUAGUCUUU";

        this.first_fasta_flat = ">ST322R 470 bp\n"
                + "CUAUCCAUGCGUUUCUUUGCUACUUUCUUUCUUCUAGCUAUGCUUGUCGUGGCUACUAAG\n"
                + "AUGGGACCAAUGAGAAUUGCAGAGGCAAGACAUUGCGAGUCGUUGAGCCAUCGUUUCAAG\n"
                + "GGACCAUGUACGAGAGAUAGCAAUUGUGCUUCGGUCUGUGAGACCGAAAGAUUUUCCGGU\n"
                + "GGCAAUUGCCAUGGAUUCCGUCGCCGUUGCUUUUGCACUAAGCCAUGCUAAAUGAGUAUU\n"
                + "AAAAAUUAUGUGUAAUAGAAGAAGUUUGAGAAAAAAAUUAUGUACUCUUGAAUAAAGUAC\n"
                + "ACUAUGAUUGUUCAAAGAUAUAUGUGGUGCUAGUUUUGUUUGUAAAACUAGUCGUGAUCU\n"
                + "UUGAAUUUAUAUGCAAUUAUGGUGCACUAGACUUGUUAAUUUCUUCAUGUGAUGUAUUUU\n"
                + "UUGCUCUUUUGUUAUGAAAUAUUAUGGAUAAAAUUUGUCUUUUAGUCUUU";

        this.second_fasta_flat = ">PETGTHIONI 1561 bp\n"
                + "AAGCTTAACTACTAATGTATACACACACTCAAAGAATTTTACTTCTAAAAGATGTGGTAG\n"
                + "AATATTAAGCTAGCGTTTGGACCTAGATCTTGTTGAAACTTGAGAATTTGAGTTTTTGCA\n"
                + "ATTTAGTTGAAAAATAATTTTTGAAACTTCAAATTGTGTTTGGACATACATTTTACTTGA\n"
                + "AAAAAAATTGAAAGTACTTATTATCCTTAACTAAAATTATTGAGTACGAGTCTTGAAAAT\n"
                + "AAAGTCGTTTTTTATAACGAATGCACGAGTCCTTTAAAGTGGGACTTTCAATCACGAATC\n"
                + "CTCAATAATGGAACCAAAAATAATTGCTAGACACCAGGTTAGAAACCAAAGCAAAACAAT\n"
                + "ATTTTAACACTCTCGGCGACGAAATAATTACAGGCAATTTTTCCAACAGAATGAGTAATT\n"
                + "ACTTAAGGAATAGAGCAGATTACATGTGACACCTAATTAAAATACAGTAATAGGAGCAAG\n"
                + "TATAAAAACACTTACATGGGCATGATAAAGAAATTAAGTAGAACTGTGCTTGGCATAGCT\n"
                + "AGCTAGTATATCCTTTCTTTAAGCTGCTAACACTGGACTGACGTATTATTACAAGTAACC\n"
                + "ATGCGCAGAATTCTTTAACCACATACTCGTAAATAAGCGCCTATAAAACACCCCATATGC\n"
                + "TAGTTCCACCTTGCTAGTACTAAACTTCTTTCCACATTAATCTCCCTTCTCTCTCTTTCT\n"
                + "CTGTGTGAAGATGGGCCGCTCCATTCGTTTGTTCGCAACTTTCTTCCTTATTGCAATGCT\n"
                + "ATTTTTGTCCACTGGTTCGTCTCAGATCTTCCTTGTTAACGTTCTTTTCTTATACTACTT\n"
                + "CTATCACATGTAGCACATAGTTCTTCAATTAAAGACGCACCCTTTAACCATGTTGAGTTT\n"
                + "CTTTCTTTATTGCAACAACAGGCGGTCAATCTATTGAATATCAATGCATATTTATTTGAA\n"
                + "ATTTAAGTTATTACTTATATACACTATAGTAGTCGTATTTATTTTTATAGGTTAAAAGAT\n"
                + "AGAACGATTGTTATTATTATACTGTAACAATAACACCATATGGTCTTATTATGTATTATT\n"
                + "CCTGTAATATCTGATTCTTGATTTTAATTTTGCTTACTTATTCCAGAGATGGGACCAATG\n"
                + "ACAAGTGCAGAGGCAAGGACCTGTGAGTCACAGAGCCACCGTTTCCATGGAACATGTGTT\n"
                + "AGGGAGAGCAACTGTGCTTCCGTTTGCCAGACAGAGGGCTTCATCGGCGGCAACTGCCGT\n"
                + "GCGTTCCGCCGTCGTTGCTTCTGCACCAGAAACTGTTAGAAAATACTAAGAAAGTTGTTA\n"
                + "GTGAAATGACGCTGTTCCTGTAATACTGTAGTAGTTTCTTTTCTAAATTCCGATGAATAA\n"
                + "AGACTATCTCACACATGGTGGTGTTTAGTTATGGTTGTATGTATTCTATTTCTATGTCTC\n"
                + "TCTTTTTTTCGGTGCTGGTTATCTATTGAACGATAATTAATCATATTTATTAGTTATTAC\n"
                + "TATCTCCGCTCCAATTTAAATATTTTATTTTTATTTTAGTATGTCTTATAAAAGACTAGT\n"
                + "T";

        this.first_fasta_ASN_1 = ">ST322R Potato mRNA for tuber protein (p322), put. proteinase inhibitor. 470 bp\n"
                + "CUAUCCAUGCGUUUCUUUGCUACUUUCUUUCUUCUAGCUAUGCUUGUCGUGGCUACUAAG\n"
                + "AUGGGACCAAUGAGAAUUGCAGAGGCAAGACAUUGCGAGUCGUUGAGCCAUCGUUUCAAG\n"
                + "GGACCAUGUACGAGAGAUAGCAAUUGUGCUUCGGUCUGUGAGACCGAAAGAUUUUCCGGU\n"
                + "GGCAAUUGCCAUGGAUUCCGUCGCCGUUGCUUUUGCACUAAGCCAUGCUAAAUGAGUAUU\n"
                + "AAAAAUUAUGUGUAAUAGAAGAAGUUUGAGAAAAAAAUUAUGUACUCUUGAAUAAAGUAC\n"
                + "ACUAUGAUUGUUCAAAGAUAUAUGUGGUGCUAGUUUUGUUUGUAAAACUAGUCGUGAUCU\n"
                + "UUGAAUUUAUAUGCAAUUAUGGUGCACUAGACUUGUUAAUUUCUUCAUGUGAUGUAUUUU\n"
                + "UUGCUCUUUUGUUAUGAAAUAUUAUGGAUAAAAUUUGUCUUUUAGUCUUU";

        this.second_fasta_ASN_1 = ">PETGTHIONI Petunia inflata gamma-thionin homolog gene, complete cds. 1561 bp\n"
                + "AAGCTTAACTACTAATGTATACACACACTCAAAGAATTTTACTTCTAAAAGATGTGGTAG\n"
                + "AATATTAAGCTAGCGTTTGGACCTAGATCTTGTTGAAACTTGAGAATTTGAGTTTTTGCA\n"
                + "ATTTAGTTGAAAAATAATTTTTGAAACTTCAAATTGTGTTTGGACATACATTTTACTTGA\n"
                + "AAAAAAATTGAAAGTACTTATTATCCTTAACTAAAATTATTGAGTACGAGTCTTGAAAAT\n"
                + "AAAGTCGTTTTTTATAACGAATGCACGAGTCCTTTAAAGTGGGACTTTCAATCACGAATC\n"
                + "CTCAATAATGGAACCAAAAATAATTGCTAGACACCAGGTTAGAAACCAAAGCAAAACAAT\n"
                + "ATTTTAACACTCTCGGCGACGAAATAATTACAGGCAATTTTTCCAACAGAATGAGTAATT\n"
                + "ACTTAAGGAATAGAGCAGATTACATGTGACACCTAATTAAAATACAGTAATAGGAGCAAG\n"
                + "TATAAAAACACTTACATGGGCATGATAAAGAAATTAAGTAGAACTGTGCTTGGCATAGCT\n"
                + "AGCTAGTATATCCTTTCTTTAAGCTGCTAACACTGGACTGACGTATTATTACAAGTAACC\n"
                + "ATGCGCAGAATTCTTTAACCACATACTCGTAAATAAGCGCCTATAAAACACCCCATATGC\n"
                + "TAGTTCCACCTTGCTAGTACTAAACTTCTTTCCACATTAATCTCCCTTCTCTCTCTTTCT\n"
                + "CTGTGTGAAGATGGGCCGCTCCATTCGTTTGTTCGCAACTTTCTTCCTTATTGCAATGCT\n"
                + "ATTTTTGTCCACTGGTTCGTCTCAGATCTTCCTTGTTAACGTTCTTTTCTTATACTACTT\n"
                + "CTATCACATGTAGCACATAGTTCTTCAATTAAAGACGCACCCTTTAACCATGTTGAGTTT\n"
                + "CTTTCTTTATTGCAACAACAGGCGGTCAATCTATTGAATATCAATGCATATTTATTTGAA\n"
                + "ATTTAAGTTATTACTTATATACACTATAGTAGTCGTATTTATTTTTATAGGTTAAAAGAT\n"
                + "AGAACGATTGTTATTATTATACTGTAACAATAACACCATATGGTCTTATTATGTATTATT\n"
                + "CCTGTAATATCTGATTCTTGATTTTAATTTTGCTTACTTATTCCAGAGATGGGACCAATG\n"
                + "ACAAGTGCAGAGGCAAGGACCTGTGAGTCACAGAGCCACCGTTTCCATGGAACATGTGTT\n"
                + "AGGGAGAGCAACTGTGCTTCCGTTTGCCAGACAGAGGGCTTCATCGGCGGCAACTGCCGT\n"
                + "GCGTTCCGCCGTCGTTGCTTCTGCACCAGAAACTGTTAGAAAATACTAAGAAAGTTGTTA\n"
                + "GTGAAATGACGCTGTTCCTGTAATACTGTAGTAGTTTCTTTTCTAAATTCCGATGAATAA\n"
                + "AGACTATCTCACACATGGTGGTGTTTAGTTATGGTTGTATGTATTCTATTTCTATGTCTC\n"
                + "TCTTTTTTTCGGTGCTGGTTATCTATTGAACGATAATTAATCATATTTATTAGTTATTAC\n"
                + "TATCTCCGCTCCAATTTAAATATTTTATTTTTATTTTAGTATGTCTTATAAAAGACTAGT\n"
                + "T";
    }

    public GdeParserTest() {
        try {
            this.testfile1 = readFile(new FileReader("src/test/testdata/testfile_1.gde"));
            this.testfile2 = readFile(new FileReader("src/test/testdata/testfile_2.gde"));
            this.testfile3 = readFile(new FileReader("src/test/testdata/testfile_3.gde"));
            this.specialCase = readFile(new FileReader("src/test/testdata/special_case.gde"));
            this.fasta = readFile(new FileReader("src/test/testdata/fasta.fas"));
            this.gdeFastaFlat1 = readFile(new FileReader("src/test/testdata/gde_fasta_flat_1.gde"));
            this.gdeFastaFlat2 = readFile(new FileReader("src/test/testdata/gde_fasta_flat_2.gde"));
            this.gdeFastaTagged1 = readFile(new FileReader("src/test/testdata/gde_fasta_tagged_1.gde"));
            this.gdeFastaTagged2 = readFile(new FileReader("src/test/testdata/gde_fasta_tagged_2.gde"));
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
    public void test_special_case() {
        this.parser = ParserFactory.createParser(ParserInputFormat.GDE, new StringReader(this.specialCase));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);

        assertTrue(this.parser.isKnownFormat());

        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextElement().equals(this.special));

        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextElement().equals(this.second_fasta_ASN_1));
    }

    @Test
    public void test_flatFile_fasta() {
        this.parser = ParserFactory.createParser(ParserInputFormat.GDE, new StringReader(this.testfile1));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());

        assertTrue(this.parser.nextElement().equals(this.first_fasta_flat));
        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextElement().equals(this.second_fasta_flat));
    }

    @Test
    public void test_ASN_1_fasta() {
        this.parser = ParserFactory.createParser(ParserInputFormat.GDE, new StringReader(this.testfile2));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());

        assertTrue(this.parser.nextElement().equals(this.first_fasta_ASN_1));
        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextElement().equals(this.second_fasta_ASN_1));
        assertFalse(this.parser.readNext());
    }

    @Test
    public void test_mixed_fasta() {
        this.parser = ParserFactory.createParser(ParserInputFormat.GDE, new StringReader(this.testfile3));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());

        assertTrue(this.parser.nextElement().equals(this.first_fasta_flat));
        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextElement().equals(this.second_fasta_ASN_1));
    }

    @Test
    public void test_flatFile() {
        this.parser = ParserFactory.createParser(ParserInputFormat.GDE, new StringReader(this.testfile1));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        try {
            BioseqRecord bRecord = this.parser.nextSeq();
            assertTrue(bRecord.getseq().toString().equals(this.first));
        } catch (ParserException ex) {
            Logger.getLogger(GdeParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertTrue(this.parser.readNext());
        try {
            BioseqRecord bRecord = this.parser.nextSeq();
            assertTrue(bRecord.getseq().toString().equals(this.second));
        } catch (ParserException ex) {
            Logger.getLogger(GdeParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void test_flatfile_pipeline() throws ParserException {
        LinkedList<BioseqRecord> sequenceList = new LinkedList<BioseqRecord>();

        this.parser = ParserFactory.createParser(ParserInputFormat.GDE, new StringReader(this.testfile1));

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
    public void test_ASN_1() {
        this.parser = ParserFactory.createParser(ParserInputFormat.GDE, new StringReader(this.testfile2));

        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        try {
            BioseqRecord bRecord = this.parser.nextSeq();
            assertTrue(bRecord.getseq().toString().equals(this.first));
        } catch (ParserException ex) {
            Logger.getLogger(GdeParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertTrue(this.parser.readNext());
        try {
            BioseqRecord bRecord = this.parser.nextSeq();
            assertTrue(bRecord.getseq().toString().equals(this.second));
        } catch (ParserException ex) {
            Logger.getLogger(GdeParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void test_ASN_1_pipeline() throws ParserException {
        LinkedList<BioseqRecord> sequenceList = new LinkedList<BioseqRecord>();

        this.parser = ParserFactory.createParser(ParserInputFormat.GDE, new StringReader(this.testfile2));

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
    public void test_mixed() {
        this.parser = ParserFactory.createParser(ParserInputFormat.GDE, new StringReader(this.testfile3));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        try {
            BioseqRecord bRecord = this.parser.nextSeq();
            assertTrue(bRecord.getseq().toString().equals(this.first));
        } catch (ParserException ex) {
            Logger.getLogger(GdeParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertTrue(this.parser.readNext());
        try {
            BioseqRecord bRecord = this.parser.nextSeq();
            assertTrue(bRecord.getseq().toString().equals(this.second));
        } catch (ParserException ex) {
            Logger.getLogger(GdeParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void test_mixed_pipeline() throws ParserException {
        LinkedList<BioseqRecord> sequenceList = new LinkedList<BioseqRecord>();

        this.parser = ParserFactory.createParser(ParserInputFormat.GDE, new StringReader(this.testfile3));
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
    public void testFromFastaFlat() throws ParserException {
        this.parser = ParserFactory.createToFastaParser(ParserOutputFormat.GDE_Flat, new StringReader(this.fasta));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextSeqFromFasta().equals(this.gdeFastaFlat1));
        assertTrue(this.parser.readNext());
        assertTrue(this.parser.nextSeqFromFasta().equals(this.gdeFastaFlat2));
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testFromFastaTagged() throws ParserException {
        this.parser = ParserFactory.createToFastaParser(ParserOutputFormat.GDE_Tagged, new StringReader(this.fasta));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        //this.compareStrings(this.gdeFastaTagged1, this.parser.nextSeqFromFasta());
        assertTrue(this.parser.nextSeqFromFasta().equals(this.gdeFastaTagged1));
        assertTrue(this.parser.readNext());
        //this.compareStrings(this.gdeFastaTagged1, this.parser.nextSeqFromFasta());
        assertTrue(this.parser.nextSeqFromFasta().equals(this.gdeFastaTagged2));
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
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();

    }
}
