/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.convert.impl;

import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.Converter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class converts dotbracket/vienna structure data to .ct (connect)
 * structure data.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class DOTBRACKET2CT implements Converter {

    private static final String NEWLINE = System.getProperty("line.separator");
    /**
     * Column seperator.
     */
    private static final String COLUMNSEP = "   ";

    @Override
    public Object convert(Object fromdata) throws ConversionException {
        String inputdata;
        try {
            inputdata = (String) fromdata;
        } catch (ClassCastException e) {
            throw new ConversionException("Input could not be cast to String!", e);
        }
        // parse input.
        SimpleDotBracketParser parser = new SimpleDotBracketParser(inputdata);
        // get structures.
        ArrayList<DotBracketContent> structures = parser.parseData();
        // create stringbuilder for output.
        StringBuilder ctContent = new StringBuilder();
        for (DotBracketContent structure : structures) {
            //convert all structures.
            dotBracketStructureToCTContent(ctContent, structure);
            ctContent.append(NEWLINE);
            ctContent.append(NEWLINE);
        }
        return ctContent.toString();
    }

    private void dotBracketStructureToCTContent(StringBuilder ctContent,
            DotBracketContent structure) throws ConversionException {
        //get content
        char[] bases = structure.getBases();
        char[] dotBracketNotation = structure.getDotBracketNotation();
        //append header
        ctContent.append(COLUMNSEP);
        //append number of bases
        ctContent.append(Integer.toString(bases.length));
        //append energy header
        ctContent.append(" dG = ");
        ctContent.append(structure.getEnergy());
        ctContent.append(' ');
        //append dot bracket header information
        ctContent.append(structure.getHeader());
        ctContent.append(NEWLINE);
        //get base partners
        int[] basePartners = convertDotBracketNotationToBasePartners(dotBracketNotation);
        //retrieve max number length
        int numberLength = 1;
        int maxNumber = bases.length;
        while ((maxNumber = maxNumber / 10) > 0) {
            numberLength++;
        }

        //iterate over bases array:
        for (int currentBaseIndex = 1; currentBaseIndex <= bases.length; currentBaseIndex++) {
            ctContent.append(COLUMNSEP);
            //first column: base index.
            ctContent.append(toNormedString(currentBaseIndex, numberLength));
            ctContent.append(COLUMNSEP);
            //second column: base content.
            ctContent.append(bases[currentBaseIndex - 1]);
            ctContent.append(COLUMNSEP);
            //third column: base index - 1.
            ctContent.append(toNormedString(currentBaseIndex - 1, numberLength));
            ctContent.append(COLUMNSEP);
            //fourth column: base index + 1.
            ctContent.append(toNormedString(currentBaseIndex + 1, numberLength));
            ctContent.append(COLUMNSEP);
            //fifth column: base partner index.
            ctContent.append(toNormedString(basePartners[currentBaseIndex - 1], numberLength));
            ctContent.append(COLUMNSEP);
            //sixth column: base index.
            ctContent.append(toNormedString(currentBaseIndex, numberLength));
            ctContent.append(NEWLINE);
        }
    }

    private static int[] convertDotBracketNotationToBasePartners(char[] dotBracketContent) throws ConversionException {
        int[] basePartners = new int[dotBracketContent.length];
        HashMap<Character, Stack<Integer>> positionStacks = new HashMap<Character, Stack<Integer>>(8);
        Stack<Integer> paranethesePositions = new Stack<Integer>();
        Stack<Integer> curlyBracketPositions = new Stack<Integer>();
        Stack<Integer> squareBracketPositions = new Stack<Integer>();
        Stack<Integer> chevronPositions = new Stack<Integer>();
        positionStacks.put('(', paranethesePositions);
        positionStacks.put(')', paranethesePositions);
        positionStacks.put('[', squareBracketPositions);
        positionStacks.put(']', squareBracketPositions);
        positionStacks.put('{', curlyBracketPositions);
        positionStacks.put('}', curlyBracketPositions);
        positionStacks.put('<', chevronPositions);
        positionStacks.put('>', chevronPositions);
        int partner;
        for (int currentBaseIndex = 0; currentBaseIndex < dotBracketContent.length; currentBaseIndex++) {
            switch (dotBracketContent[currentBaseIndex]) {
                case '.':
                    basePartners[currentBaseIndex] = 0;
                    break;
                case '(':
                case '[':
                case '{':
                case '<':
                    positionStacks.get(dotBracketContent[currentBaseIndex]).push(currentBaseIndex);
                    break;
                case ')':
                case '}':
                case ']':
                case '>':
                    partner = positionStacks.get(dotBracketContent[currentBaseIndex]).pop();
                    basePartners[currentBaseIndex] = partner + 1;
                    basePartners[partner] = currentBaseIndex + 1;
                    break;
                default:
                    throw new ConversionException("Input contained invalid character: "
                            + Character.toString(dotBracketContent[currentBaseIndex]));
            }
        }
        return basePartners;
    }

    /**
     * Transforms an integer to a string with the given length.
     *
     * @param number an integer.
     * @param length the length the result string shall have.
     * @return string with the given length.
     */
    private static String toNormedString(int number, int length) {
        StringBuilder result = new StringBuilder();
        result.append(Integer.toString(number));
        int originallength = result.length();
        for (int i = 0; i < length - originallength; i++) {
            result.append(' ');
        }
        return result.toString();
    }
}

/**
 * Nested class to parse dotBracket structures.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
class SimpleDotBracketParser {

    String input;
    /**
     * Pattern to parse header data of dot bracket.
     */
    private static final Pattern HEADERPATTERN = Pattern.compile(">(.+)");
    /**
     * Pattern to parse base content of dot bracket.
     */
    private static final Pattern BASEPATTERN = Pattern.compile("[AGCURYMKSWHBVDNX\\-]+");
    //helping string to create dot bracket pattern.
    private static final String DOTBRACKETREGEX = "([(){}<>\\[\\].]+)";
    private static final String ENERGYREGEX = "(?:\\s*\\(?(-?\\d+(?:\\.\\d+)?)\\)?)?\\s*";
    private static final String SHORTSTRUCTURENOTATION = "(?:\\s+([_\\[\\]]+))?\\s*";
    /**
     * Pattern to parse dot bracket base pair content of dot bracket.
     */
    private static final Pattern DOTBRACKETPATTERN = Pattern.compile(ENERGYREGEX + DOTBRACKETREGEX + ENERGYREGEX + SHORTSTRUCTURENOTATION);

    /**
     * Constructs a new parser.
     *
     * @param input input data.
     */
    public SimpleDotBracketParser(String input) {
        this.input = input;
    }

    /**
     * Parses dot bracket data and returns list of DotBracketContent objects.
     *
     * @return list of DotBracketContent objects.
     * @throws ConversionException is thrown if data could not be read.
     */
    public ArrayList<DotBracketContent> parseData() throws ConversionException {

        ArrayList<DotBracketContent> structures = new ArrayList<DotBracketContent>();
        //check if the data is a non-null object
        if (input != null) {
            BufferedReader reader = new BufferedReader(new StringReader(input));
            try {
                //try to find structures
                while (readStructure(reader, structures));
            } catch (IOException e) {
                //This can't happen under normal circumstances.
                throw new ConversionException("Parser could not read input.", e);
            }
        }
        return structures;
    }

    /**
     * Reads data from a given reader and adds found dot bracket structure data
     * to a given list.
     *
     * @param reader input data reader.
     * @param structureList (empty) list to store structure data.
     * @return true if a structure has been found.
     * @throws IOException is thrown if an error occured during reading.
     */
    private boolean readStructure(BufferedReader reader, ArrayList<DotBracketContent> structureList) throws IOException {
        String currentLine;
        //read lines until first input is found.
        while ((currentLine = reader.readLine()) != null) {
            if (!currentLine.isEmpty() && !currentLine.matches(" +")) {
                break;
            }
        }
        //if stream has ended end the method.
        if (currentLine == null) {
            return false;
        }
        //construct parsing variables
        StringBuilder header = new StringBuilder();
        StringBuilder bases = new StringBuilder();
        StringBuilder dotBracket = new StringBuilder();
        String energy = null;
        //match header line(s).
        Matcher dotBracketLineMatcher = HEADERPATTERN.matcher(currentLine);
        while (dotBracketLineMatcher.matches()) {
            header.append(dotBracketLineMatcher.group(1));
            currentLine = reader.readLine();
            dotBracketLineMatcher = HEADERPATTERN.matcher(currentLine);
        }
        //if no header was found, return false
        if (header.toString().isEmpty()) {
            return false;
        }
        //match base line(s).
        dotBracketLineMatcher = BASEPATTERN.matcher(currentLine);
        while (dotBracketLineMatcher.matches()) {
            bases.append(dotBracketLineMatcher.group(0));
            currentLine = reader.readLine();
            dotBracketLineMatcher = BASEPATTERN.matcher(currentLine);
        }
        //if no bases were found, return false
        if (bases.toString().isEmpty()) {
            return false;
        }
        //match dot bracket line(s).
        dotBracketLineMatcher = DOTBRACKETPATTERN.matcher(currentLine);
        while (dotBracketLineMatcher.matches()) {
            //check if energy information could be found.
            if (dotBracketLineMatcher.group(1) != null) {
                energy = dotBracketLineMatcher.group(1);
            } else if (dotBracketLineMatcher.group(3) != null) {
                energy = dotBracketLineMatcher.group(3);
            }
            //append dot bracket content.
            dotBracket.append(dotBracketLineMatcher.group(2));
            //append short structure notation (if found)
            if (dotBracketLineMatcher.group(4) != null) {
                header.append(" short structure notation: ");
                header.append(dotBracketLineMatcher.group(4));
            }
            currentLine = reader.readLine();
            dotBracketLineMatcher = DOTBRACKETPATTERN.matcher(currentLine);
        }
        //if no dotBracket content was found, return false
        if (dotBracket.toString().isEmpty()) {
            return false;
        }

        //if we got here, we were able to parse a structure.
        DotBracketContent content;

        if (energy != null) {
            //if energy was found, include energy information.
            content = new DotBracketContent(header.toString(),
                    bases.toString().toCharArray(),
                    dotBracket.toString().toCharArray(),
                    energy);
        } else {
            //otherwise don't.
            content = new DotBracketContent(header.toString(),
                    bases.toString().toCharArray(),
                    dotBracket.toString().toCharArray());
        }
        structureList.add(content);

        return true;
    }
}

/**
 * Nested class to store dotbracket content for a single structure.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
class DotBracketContent {

    private static final String DEFAULTENERGYSTRING = "unknown";
    private String header;
    private char[] bases;
    private char[] dotBracketNotation;
    private String energy;

    public DotBracketContent(String header, char[] bases, char[] dotBracketNotation) {
        this(header, bases, dotBracketNotation, DEFAULTENERGYSTRING);
    }

    public DotBracketContent(String header, char[] bases, char[] dotBracketNotation, String energy) {
        this.header = header;
        this.bases = bases;
        this.dotBracketNotation = dotBracketNotation;
        this.energy = energy;
    }

    public String getHeader() {
        return header;
    }

    public char[] getBases() {
        return bases;
    }

    public char[] getDotBracketNotation() {
        return dotBracketNotation;
    }

    public String getEnergy() {
        return energy;
    }
}