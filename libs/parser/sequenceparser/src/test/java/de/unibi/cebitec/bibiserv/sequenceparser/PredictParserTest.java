/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser;

import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserInputFormat;
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
public class PredictParserTest {

    private String gde1;
    private String gde2;
    private String gde3;
    private String msfPileUp;
    private String msfPlain;
    private String msfRich;
    private String msfBroken1;
    private String msfBroken2;
    private String phylip_1;
    private String phylip_2;
    private String phylip_3;
    private String rsf_single_seq;
    private String rsf_double_seq;
    private String rsf_broken_seq;
    private String dialign_single;
    private String dialign_multi;
    private String ig_single;
    private String ig_multi;
    private String ig_alignment;


    

    

    public PredictParserTest() {
        try {
            this.gde1 = readFile(new FileReader("src/test/testdata/testfile_1.gde"));
            this.gde2 = readFile(new FileReader("src/test/testdata/testfile_2.gde"));
            this.gde3 = readFile(new FileReader("src/test/testdata/testfile_3.gde"));
            this.msfPileUp = readFile(new FileReader("src/test/testdata/msf_pileup.msf"));
            this.msfPlain = readFile(new FileReader("src/test/testdata/msf_plain.msf"));
            this.msfRich = readFile(new FileReader("src/test/testdata/msf_rich.msf"));
            this.msfBroken1 = readFile(new FileReader("src/test/testdata/msf_broken_1.msf"));
            this.msfBroken2 = readFile(new FileReader("src/test/testdata/msf_broken_2.msf"));
            this.phylip_1 = readFile(new FileReader("src/test/testdata/phylip_1.phy"));
            this.phylip_2 = readFile(new FileReader("src/test/testdata/phylip_2.phy"));
            this.phylip_3 = readFile(new FileReader("src/test/testdata/phylip_3.phy"));
            this.rsf_single_seq = readFile(new FileReader("src/test/testdata/rsf_dna_single.rsf"));
            this.rsf_double_seq = readFile(new FileReader("src/test/testdata/rsf_dna_multi.rsf"));
            this.rsf_broken_seq = readFile(new FileReader("src/test/testdata/rsf_single_broken.rsf"));
            this.dialign_multi = readFile(new FileReader("src/test/testdata/dialign_1.dia"));
            this.dialign_single = readFile(new FileReader("src/test/testdata/dialign_2.dia"));
            this.ig_single = readFile(new FileReader("src/test/testdata/ig_single.ig"));
            this.ig_multi = readFile(new FileReader("src/test/testdata/ig_multi.ig"));
            this.ig_alignment = readFile(new FileReader("src/test/testdata/ig_alignment.ig"));
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
    public void testNew(){
    }

    @Test
    public void testNew2(){
    }



    @Test
    public void testAllAsString(){
        assertTrue(ParserFactory.predictParser(this.gde1)==ParserInputFormat.GDE);
        //Should be tested as GDE, but format might also be IG!
        assertTrue(ParserFactory.predictParser(this.gde2)==ParserInputFormat.AMBIGOUS);
        assertTrue(ParserFactory.predictParser(this.gde3)==ParserInputFormat.AMBIGOUS);

        //Should be tested as MSF, but format might also be IG!
        assertTrue(ParserFactory.predictParser(this.msfPileUp)==ParserInputFormat.AMBIGOUS);
        assertTrue(ParserFactory.predictParser(this.msfPlain)==ParserInputFormat.AMBIGOUS);
        assertTrue(ParserFactory.predictParser(this.msfRich)==ParserInputFormat.AMBIGOUS);
        //Should be tested as UNKNOWN, but format might also be IG!
        assertTrue(ParserFactory.predictParser(this.msfBroken2)==ParserInputFormat.IG);
        //Should be UNKNOWN, but only header is checked! Might be MSF or IG!
        assertTrue(ParserFactory.predictParser(this.msfBroken1)==ParserInputFormat.AMBIGOUS);

        assertTrue(ParserFactory.predictParser(this.phylip_1)==ParserInputFormat.PHYLIP);
        assertTrue(ParserFactory.predictParser(this.phylip_2)==ParserInputFormat.PHYLIP);
        assertTrue(ParserFactory.predictParser(this.phylip_3)==ParserInputFormat.PHYLIP);

        //Should be tested as RSF, but format might also be IG!
        assertTrue(ParserFactory.predictParser(this.rsf_single_seq)==ParserInputFormat.AMBIGOUS);
        assertTrue(ParserFactory.predictParser(this.rsf_double_seq)==ParserInputFormat.AMBIGOUS);
        //Should be tested as UNKNOWN, but format might also be IG!
        assertTrue(ParserFactory.predictParser(this.rsf_broken_seq)==ParserInputFormat.IG);

        assertTrue(ParserFactory.predictParser(this.dialign_single)==ParserInputFormat.DIALIGN);
        assertTrue(ParserFactory.predictParser(this.dialign_multi)==ParserInputFormat.DIALIGN);

        assertTrue(ParserFactory.predictParser(this.ig_single)==ParserInputFormat.IG);
        assertTrue(ParserFactory.predictParser(this.ig_multi)==ParserInputFormat.IG);
        assertTrue(ParserFactory.predictParser(this.ig_alignment)==ParserInputFormat.IG);
    }

    @Test
    public void testAllAsReader(){
        assertTrue(ParserFactory.predictParser(new StringReader(this.gde1))==ParserInputFormat.GDE);
        //Should be tested as GDE, but format might also be IG!
        assertTrue(ParserFactory.predictParser(new StringReader(this.gde2))==ParserInputFormat.AMBIGOUS);
        assertTrue(ParserFactory.predictParser(new StringReader(this.gde3))==ParserInputFormat.AMBIGOUS);

        //Should be tested as MSF, but format might also be IG!
        assertTrue(ParserFactory.predictParser(new StringReader(this.msfPileUp))==ParserInputFormat.AMBIGOUS);
        assertTrue(ParserFactory.predictParser(new StringReader(this.msfPlain))==ParserInputFormat.AMBIGOUS);
        assertTrue(ParserFactory.predictParser(new StringReader(this.msfRich))==ParserInputFormat.AMBIGOUS);
        //Should be tested as UNKNOWN, but format might also be IG!
        assertTrue(ParserFactory.predictParser(new StringReader(this.msfBroken2))==ParserInputFormat.IG);
        //Should be UNKNOWN, but only header is checked! Might be MSF or IG!
        assertTrue(ParserFactory.predictParser(new StringReader(this.msfBroken1))==ParserInputFormat.AMBIGOUS);

        assertTrue(ParserFactory.predictParser(new StringReader(this.phylip_1))==ParserInputFormat.PHYLIP);
        assertTrue(ParserFactory.predictParser(new StringReader(this.phylip_2))==ParserInputFormat.PHYLIP);
        assertTrue(ParserFactory.predictParser(new StringReader(this.phylip_3))==ParserInputFormat.PHYLIP);

        //Should be tested as RSF, but format might also be IG!
        assertTrue(ParserFactory.predictParser(new StringReader(this.rsf_single_seq))==ParserInputFormat.AMBIGOUS);
        assertTrue(ParserFactory.predictParser(new StringReader(this.rsf_double_seq))==ParserInputFormat.AMBIGOUS);
        //Should be tested as UNKNOWN, but format might also be IG!
        assertTrue(ParserFactory.predictParser(new StringReader(this.rsf_broken_seq))==ParserInputFormat.IG);

        assertTrue(ParserFactory.predictParser(new StringReader(this.dialign_single))==ParserInputFormat.DIALIGN);
        assertTrue(ParserFactory.predictParser(new StringReader(this.dialign_multi))==ParserInputFormat.DIALIGN);

        assertTrue(ParserFactory.predictParser(new StringReader(this.ig_single))==ParserInputFormat.IG);
        assertTrue(ParserFactory.predictParser(new StringReader(this.ig_multi))==ParserInputFormat.IG);
        assertTrue(ParserFactory.predictParser(new StringReader(this.ig_alignment))==ParserInputFormat.IG);
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
