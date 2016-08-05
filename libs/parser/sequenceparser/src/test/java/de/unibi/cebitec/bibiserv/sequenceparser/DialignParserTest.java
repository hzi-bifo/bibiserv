/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser;

import de.unibi.cebitec.bibiserv.sequenceparser.parser.exception.ParserException;

import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserInputFormat;
import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord;
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
public class DialignParserTest {

    private String dialign_multi;
    private String dialign_single;
    private Parser parser;
    private String htl2;
    private String htl2_fasta;
    private String mmlv;
    private String mmlv_fasta;
    private String hepb;
    private String hepb_fasta;
    private String ecol;
    private String ecol_fasta;
    private String fasta;
    private String dialignFasta;

    {
        this.htl2 = "LDTAPCLFSDGS------PQKAAYVLWDQTIL---QQDITPLPSHETHSAQKGELLALICGL"
                + "RAAKPWPSLNIFLDSKYLIKYLHSLAIGAFLGTSAH----"
                + "-----QT---LQAALPPLLQGKTIYLHHVRSHT------N"
                + "LPDPISTFNEYTDSLILAPL--------------------"
                + "----------------------------";

        this.mmlv = "PDADHTWYTDGSSLLQEGQRKAGAAVTTETEVIWAKALDAG---T---SAQRAELIALTQAL"
                + "KMAEGKK-LNVYTDSRYAFATAHIHGEIYRRRGLLTSEGK"
                + "EIKNKDE---ILALLKALFLPKRLSIIHCPGHQ------K"
                + "GHSAEARGNRMADQAARKAAITETPDTSTLL---------"
                + "----------------------------";

        this.hepb = "RPGLCQVFADAT------PTGWGLVMGHQRMR---GTFSAPLPIHT------AELLAACFAR"
                + "SRSGAN---IIGTDN-------------------------"
                + "-----------SVVLSR--------------KYTSFPWLL"
                + "GCAANWI-LRGTSFVYVPSALNPADDPSRGRLGLSRPLLR"
                + "LPFRPTTGRTSLYADSPSVPSHLPDRVH";

        this.ecol = "MLKQVEIFTDGSCLGNPGPGGYGAILRYRGRE---KTFSAGYTRT---TNNRMELMAAIVAL"
                + "EALKEHCEVILSTDSQYVRQGITQWIHNWKKRGWKTADKK"
                + "PVKNVDLWQRLDAALGQ--------------HQIKWEWVK"
                + "GHAGHPE-NERCDELARAAAMNPTLEDTGYQVEV------"
                + "----------------------------";


        this.htl2_fasta = ">HTL2 210 bp\n"
                + "LDTAPCLFSDGS------PQKAAYVLWDQTIL---QQDITPLPSHETHSAQKGELLALIC\n"
                + "GLRAAKPWPSLNIFLDSKYLIKYLHSLAIGAFLGTSAH---------QT---LQAALPPL\n"
                + "LQGKTIYLHHVRSHT------NLPDPISTFNEYTDSLILAPL------------------\n"
                + "------------------------------";

        this.mmlv_fasta = ">MMLV 210 bp\n"
                + "PDADHTWYTDGSSLLQEGQRKAGAAVTTETEVIWAKALDAG---T---SAQRAELIALTQ\n"
                + "ALKMAEGKK-LNVYTDSRYAFATAHIHGEIYRRRGLLTSEGKEIKNKDE---ILALLKAL\n"
                + "FLPKRLSIIHCPGHQ------KGHSAEARGNRMADQAARKAAITETPDTSTLL-------\n"
                + "------------------------------";

        this.hepb_fasta = ">HEPB 210 bp\n"
                + "RPGLCQVFADAT------PTGWGLVMGHQRMR---GTFSAPLPIHT------AELLAACF\n"
                + "ARSRSGAN---IIGTDN------------------------------------SVVLSR-\n"
                + "-------------KYTSFPWLLGCAANWI-LRGTSFVYVPSALNPADDPSRGRLGLSRPL\n"
                + "LRLPFRPTTGRTSLYADSPSVPSHLPDRVH";

        this.ecol_fasta = ">ECOL 210 bp\n"
                + "MLKQVEIFTDGSCLGNPGPGGYGAILRYRGRE---KTFSAGYTRT---TNNRMELMAAIV\n"
                + "ALEALKEHCEVILSTDSQYVRQGITQWIHNWKKRGWKTADKKPVKNVDLWQRLDAALGQ-\n"
                + "-------------HQIKWEWVKGHAGHPE-NERCDELARAAAMNPTLEDTGYQVEV----\n"
                + "------------------------------";
    }

    public DialignParserTest() {
        try {
            this.dialign_multi = readFile(new FileReader("src/test/testdata/dialign_1.dia"));
            this.dialign_single = readFile(new FileReader("src/test/testdata/dialign_2.dia"));
            this.fasta = readFile(new FileReader("src/test/testdata/fasta.fas"));
            this.dialignFasta = readFile(new FileReader("src/test/testdata/dialign_fasta.dia"));
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
    public void testSingle2Fasta() {
        this.parser = ParserFactory.createParser(ParserInputFormat.DIALIGN, new StringReader(this.dialign_single));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.hasMoreElements());
        assertTrue(((String) this.parser.nextElement()).equals(this.htl2_fasta));
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testMulti2Fasta() {
        this.parser = ParserFactory.createParser(ParserInputFormat.DIALIGN, new StringReader(this.dialign_multi));
        this.parser.setOutputFormat(ParserOutputFormat.Fasta);
        assertTrue(this.parser.isKnownFormat());
        int i = 0;
        while (this.parser.hasMoreElements()) {
            i++;
            assertTrue(this.checkFasta((String) this.parser.nextElement()));
        }
        assertTrue(i == 4);
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testSingle() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.DIALIGN, new StringReader(this.dialign_single));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        BioseqRecord bRecord = this.parser.nextSeq();
        assertTrue(bRecord.getseq().toString().equals(this.htl2));
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testMulti() throws ParserException {
        this.parser = ParserFactory.createParser(ParserInputFormat.DIALIGN, new StringReader(this.dialign_multi));
        assertTrue(this.parser.isKnownFormat());
        int i = 0;
        while (this.parser.readNext()) {
            i++;
            BioseqRecord bRecord = this.parser.nextSeq();
            assertTrue(this.checkSequences(bRecord));
        }
        assertTrue(i == 4);
        assertFalse(this.parser.readNext());
    }

    @Test
    public void testFromFasta() throws ParserException {
        this.parser = ParserFactory.createToFastaParser(ParserOutputFormat.DIALIGN, new StringReader(this.fasta));
        assertTrue(this.parser.isKnownFormat());
        assertTrue(this.parser.readNext());
        assertTrue(this.dialignFasta.equals(this.parser.nextSeqFromFasta()));
        assertFalse(this.parser.readNext());
    }

    private boolean checkSequences(BioseqRecord bRecord) {
        if (bRecord.getID().equals("HTL2")) {
            return bRecord.getseq().toString().equals(this.htl2);
        } else if (bRecord.getID().equals("MMLV")) {
            return bRecord.getseq().toString().equals(this.mmlv);
        } else if (bRecord.getID().equals("HEPB")) {
            return bRecord.getseq().toString().equals(this.hepb);
        } else {
            return bRecord.getseq().toString().equals(this.ecol);
        }
    }

    private boolean checkFasta(String s) {
        if (s.equals(this.ecol_fasta)) {
            return true;
        } else if (s.equals(this.hepb_fasta)) {
            return true;
        } else if (s.equals(this.htl2_fasta)) {
            return true;
        } else if (s.equals(this.mmlv_fasta)) {
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
        return sb.toString();

    }
}
