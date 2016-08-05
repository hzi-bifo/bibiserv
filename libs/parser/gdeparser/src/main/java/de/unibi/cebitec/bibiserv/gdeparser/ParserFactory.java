/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.gdeparser;

import de.unibi.cebitec.bibiserv.gdeparser.enums.ParserInputFormat;
import de.unibi.cebitec.bibiserv.gdeparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.gdeparser.parser.impl.DialignParser;
import de.unibi.cebitec.bibiserv.gdeparser.parser.impl.GdeParser;
import de.unibi.cebitec.bibiserv.gdeparser.parser.impl.IgParser;
import de.unibi.cebitec.bibiserv.gdeparser.parser.impl.MsfParser;
import de.unibi.cebitec.bibiserv.gdeparser.parser.impl.PhylipParser;
import de.unibi.cebitec.bibiserv.gdeparser.parser.impl.RsfParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Factory for parsers.
 *
 * @author mrumming
 */
public class ParserFactory {

    /**
     * Factory for parser contruction.
     * @param pif Type of Parser
     * @return Parser of selected type
     */
    public static Parser createParser(ParserInputFormat pif, Reader inputFileReader) {
        switch (pif) {
            case DIALIGN:
                return new DialignParser(inputFileReader);
            case GDE:
                return new GdeParser(inputFileReader);
            case IG:
                return new IgParser(inputFileReader);
            case MSF:
                return new MsfParser(inputFileReader);
            case PHYLIP:
                return new PhylipParser(inputFileReader);
            case RSF:
                return new RsfParser(inputFileReader);
            default:

        }
        throw new IllegalArgumentException("The selected parser " + pif + " is not recognized.");
    }

    /**
     * Factory for parser contruction, where input is in Fasta format and output
     * should be in format of parser.
     * @param pof Type of Parser
     * @return Parser of selected type
     */
    public static Parser createToFastaParser(ParserOutputFormat pof, Reader inputFileReader) {
        switch (pof) {
            case DIALIGN:
                return new DialignParser(inputFileReader, pof);
            case GDE_Tagged:
                return new GdeParser(inputFileReader, pof);
            case GDE_Flat:
                return new GdeParser(inputFileReader, pof);
            case IG:
                return new IgParser(inputFileReader, pof);
            case MSF_Plain:
                return new MsfParser(inputFileReader, pof);
            case MSF_PileUp:
                return new MsfParser(inputFileReader, pof);
            case MSF_Rich:
                return new MsfParser(inputFileReader, pof);
            case PHYLIP:
                return new PhylipParser(inputFileReader, pof);
            case RSF:
                return new RsfParser(inputFileReader, pof);
        }
        throw new IllegalArgumentException("The selected parser " + pof + " is not recognized.");
    }

    public static ParserInputFormat predictParser(String input) {

        ParserInputFormat[] pifArr = ParserInputFormat.values();
        ParserInputFormat retPif = ParserInputFormat.UNKNOWN;

        for (int i = 0; i < pifArr.length; i++) {
            if (pifArr[i].isChoosenInput(new StringReader(input))) {
                if (retPif == ParserInputFormat.UNKNOWN) {
                    retPif = pifArr[i];
                } else {
                    retPif = ParserInputFormat.AMBIGOUS;
                }
            }
        }
        return retPif;
    }

    public static ParserInputFormat predictParser(Reader inputFileReader) {
        return predictParser(buildString(inputFileReader));
    }

    private static String buildString(Reader inputFileReader) {
        StringBuilder sBuilder = new StringBuilder();
        BufferedReader bReader = new BufferedReader(inputFileReader);
        boolean b = true;
        while (b) {
            try {
                String line = bReader.readLine();
                if (line != null) {
                    sBuilder.append(line).append("\n");
                } else {
                    b = false;
                }
            } catch (IOException ex) {
                b = false;
            }
        }
        return sBuilder.toString();
    }
}
