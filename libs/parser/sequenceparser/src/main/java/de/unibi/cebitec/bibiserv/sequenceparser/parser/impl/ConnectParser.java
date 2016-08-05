/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
package de.unibi.cebitec.bibiserv.sequenceparser.parser.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses ct structure data (either single or multiple sequences) and
 * also does validation if necessary.
 *
 * @author Benjamin Paassen - bpaassen(at)cebitec.uni-bielefeld.de
 */
public class ConnectParser {

    /**
     * the input as String.
     */
    String input = null;
    /**
     * true if input shall be validated.
     */
    boolean doValidation;
    /**
     * message containing information about the validation if validation shall
     * be done.
     */
    String validationMessage;
    /**
     * boolean containing validation result if validation shall be done.
     */
    boolean valid;
    /**
     * Lock to make validation thread safe.
     */
    ReentrantLock validationLock = new ReentrantLock();
    /**
     * list containing all parsed structures.
     */
    ArrayList<ConnectSingleStructureData> structures =
            new ArrayList<ConnectSingleStructureData>();
    /**
     * Lock to make list access thread safe.
     */
    ReentrantLock basePairLock = new ReentrantLock();
    /**
     * Regular expression for separators between .ct columns.
     */
    private static final String COLSEPREGEX = "\\s+";
    /**
     * Regular expression for a group of numbers in a .ct column.
     */
    private static final String NUMBERGROUP = "([0-9]+)";
    /**
     * Regular expression for a group containing a base in one letter notation.
     */
    private static final String BASEGROUP = "([ACTUG])";
    /**
     * Regular expression for a row in a ct. file.
     */
    private static final String ROWREGEX = "\\s*" + NUMBERGROUP + //first column (base index)
            COLSEPREGEX + BASEGROUP + //second column (base)
            COLSEPREGEX + NUMBERGROUP + //third column (base index - 1)
            COLSEPREGEX + NUMBERGROUP + //fourth column (base infex + 1)
            COLSEPREGEX + NUMBERGROUP + //fifth column (base partner)
            COLSEPREGEX + NUMBERGROUP + "\\s*"; //sixth column (base index)
    private static final Pattern ROWPATTERN = Pattern.compile(ROWREGEX);

    /**
     * Creates a new parser.
     *
     * @param input input data that shall be parsed.
     * @param doValidation true if input shall be validated.
     */
    public ConnectParser(String input, boolean doValidation) {
        this.input = input;
        this.doValidation = doValidation;
    }

    /**
     * Orders the parser to parse the input data.
     */
    public void parseData() {

        //check if the data is a non-null object
        if (input == null) {
            if (doValidation) {
                setInvalid("Input was 'null'");
            }
        } else {
            BufferedReader reader = new BufferedReader(new StringReader(input));
            try {
                //try to find first structure.
                if (readStructure(reader, true)) {
                    //if first structure was found, try to find additional ones.
                    while (readStructure(reader, false));
                }
            } catch (IOException e) {
                //This can't happen under normal circumstances.
                if (doValidation) {
                    setInvalid("Internal error: ");
                }
            }
        }
    }

    public boolean readStructure(BufferedReader reader, boolean first) throws IOException {
        String currentLine;
        //read lines until first input is found.
        while ((currentLine = reader.readLine()) != null) {
            if (!currentLine.isEmpty() && !currentLine.matches("\\s+")) {
                break;
            }
        }
        //if stream has ended end the method.
        if (currentLine == null) {
            if (doValidation) {
                if (first) {
                    //if this was the first structure in the input data, the input is empty.
                    setInvalid("Input was emtpy.");
                } else {
                    //if it wasn't, every content before this (empty) one was valid.
                    //Thus the whole input is valid.
                    setValid("Input is valid .ct data!");
                }
            }
            return false;
        }

        if (doValidation) {
            //check if first line with content contains energy information.
            if (!currentLine.contains("dG") && !currentLine.contains("Energy") && !currentLine.contains("ENERGY")) {
                setInvalid("Input contains no information about energy in the header!");
                return false;
            }
        }
        //create new structure object.
        ConnectSingleStructureData structure = new ConnectSingleStructureData(currentLine);
        //prepare validation variables.
        int currentBaseIndex;
        int minBaseIndex;
        int[] columns = new int[6];
        boolean lastLine = false;
        //read first content line and try to match it.
        currentLine = reader.readLine();
        Matcher ctContentLineMatcher = ROWPATTERN.matcher(currentLine);
        if (ctContentLineMatcher.matches()) {

            //store the first base index.
            currentBaseIndex = Integer.parseInt(ctContentLineMatcher.group(1));
            minBaseIndex = currentBaseIndex;

            //read the content until the content does not match the pattern anymore.
            while (currentLine != null && ctContentLineMatcher.matches()) {
                if (doValidation) {
                    if (lastLine) {
                        //if this should be the last line and the matcher still matched, the content is invalid.
                        setInvalid("Column 4 contained invalid base pair index.");
                        return false;
                    }
                }
                //check every column entry (except base entry)
                for (int currentColIndex = 1; currentColIndex <= 6; currentColIndex++) {
                    if (currentColIndex != 2) {
                        columns[currentColIndex - 1] =
                                Integer.parseInt(ctContentLineMatcher.group(currentColIndex));
                    }
                }
                if (doValidation) {
                    //validate rows.
                    if (columns[0] != currentBaseIndex) {
                        setInvalid("Column 1 contained invalid base pair index.");
                        return false;
                    }
                    if (columns[2] != currentBaseIndex - 1) {
                        setInvalid("Column 3 contained invalid base pair index.");
                        return false;
                    }
                    if (columns[3] != currentBaseIndex + 1) {
                        if (columns[3] == 0) {
                            //if the index is zero, this has to be the last line of the content.
                            lastLine = true;
                        } else {
                            setInvalid("Column 4 contained invalid base pair index.");
                            return false;
                        }
                    }
                    if (columns[4] < minBaseIndex && columns[4] != 0) {
                        setInvalid("Input contained invalid base pairing partner index (index too large).");
                        return false;
                    }
                    if (columns[5] != currentBaseIndex) {
                        setInvalid("Column 6 contained invalid base pair index.");
                        return false;
                    }
                }
                //store base.
                structure.put(currentBaseIndex, ctContentLineMatcher.group(2).charAt(0), columns[4]);

                //read new line.
                currentLine = reader.readLine();
                if (currentLine != null) {
                    ctContentLineMatcher = ROWPATTERN.matcher(currentLine);
                }
                currentBaseIndex++;
            }

            if (doValidation) {
                //validate base pairing partners.
                if (!validateBasePairPartners(structure, minBaseIndex, currentBaseIndex - 1)) {
                    return false;
                }
            }

            //add Structure object to list.
            basePairLock.lock();
            try {
                this.structures.add(structure);
            } finally {
                basePairLock.unlock();
            }
        } else {
            if (doValidation) {
                //if the matcher did not even match the first line, the content is invalid.
                setInvalid("Input contained no actual content data.");
            }
            return false;
        }
        return true;
    }

    /**
     * Checks if the base pair partners of a given structure are valid.
     *
     * @param structure structure input.
     */
    private boolean validateBasePairPartners(ConnectSingleStructureData structure, int minBaseIndex, int maxBaseIndex) {
        //if the matcher does not match anymore, check if the base pairs are valid.
        Integer currentBasePartner;
        for (Integer key : structure.getBaseIndices()) {
            currentBasePartner = structure.getBasePartner(key);
            //check if partner exists
            if (currentBasePartner != null && currentBasePartner != 0) {
                if (currentBasePartner > maxBaseIndex) {
                    setInvalid("Input contains reference to invalid base pair partner (index too large)");
                    return false;
                }
                if (currentBasePartner < minBaseIndex) {
                    setInvalid("Input contains reference to invalid base pair partner (index too low)");
                    return false;
                }
                //the partner of the partner should be the key again.
                currentBasePartner = structure.getBasePartner(currentBasePartner);
                if (currentBasePartner == null) {
                    setInvalid("Input contains reference to invalid base pair partner (partner is missing)");
                    return false;
                }
                if (!currentBasePartner.equals(key)) {
                    setInvalid("Input contains reference to invalid base pair partner (reference is one-sided)");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Marks the validation as invalid.
     *
     * @param message reason for invalidation.
     */
    private void setInvalid(String message) {
        validationLock.lock();
        try {
            valid = false;
            validationMessage = message;
        } finally {
            validationLock.unlock();
        }
    }

    /**
     * Marks the validation as valid.
     *
     * @param message reason for validation.
     */
    private void setValid(String message) {
        validationLock.lock();
        try {
            valid = true;
            validationMessage = message;
        } finally {
            validationLock.unlock();
        }
    }

    public ArrayList<ConnectSingleStructureData> getStructures() {
        basePairLock.lock();
        try {
            return structures;
        } finally {
            basePairLock.unlock();
        }
    }

    public boolean isValid() {
        validationLock.lock();
        try {
            return valid;
        } finally {
            validationLock.unlock();
        }
    }

    public String getValidationMessage() {
        validationLock.lock();
        try {
            return validationMessage;
        } finally {
            validationLock.unlock();
        }
    }
}
