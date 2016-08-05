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
package de.unibi.cebitec.bibiserv.util.validate.impl;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.exception.ParserToolException;
import de.unibi.cebitec.bibiserv.util.validate.*;
import de.unibi.cebitec.bibiserv.util.validate.exception.UnsupportedContentException;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General DotBracket_Validator, supports RNAStrucure and RNAStructureAlignment.
 * 
 * 
 * @author  Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *          Sven Hartmeier - shartmei(at)cebitec. uni-bielefeld.de (initial release)
 *          Thomas Gatter - tgatter(at)cebitec. uni-bielefeld.de
 */
public class DotBracket_Validator implements AlignmentValidator {

    // the input as String
    String input = null;
    //validationresult for output
    private ValidationResult vr;
    private final Pattern strictrna = Pattern.compile(PatternType.BT_rnaSequence.getPattern());
    private final Pattern strictalignmentrna = Pattern.compile(PatternType.BT_gappedRnaSequence.getPattern());
    private final Pattern structure_with_energy = Pattern.compile(PatternType.BT_gappedRnaSecondaryStructureSequenceWithEnergy.getPattern());
    private final Pattern sequencePattern = Pattern.compile(PatternType.BT_gappedAmbiguousRnaSequence.getPattern());
    // RegExp for Structure line
    private static final Pattern rnastructPattern = Pattern.compile("^[\\(\\)\\[\\]\\{\\}\\<\\>.]+.*$");
    // RegExp for Structure with Energy line
    private static final Pattern rnastructWithEnergyPattern = Pattern.compile("^([\\(\\)\\[\\]\\{\\}\\<\\>.]+)(?:\\s+\\(?(-?\\d+(?:\\.\\d+)?)\\)?)?(?:\\s+([_\\[\\]]+))?\\s*$");
    /**
     * Constant for structure type : structure
     */
    public static final int STRUCTURE = 1;
    /**
     * Constant for structure type : shape
     */
    public static final int SHAPE = 2;

    public DotBracket_Validator() {
        alignment = false;
        cardinality = CARDINALITY.single;
        strictness = STRICTNESS.ambiguous;
    }

    @Override
    public ValidationResult validateThis(Object data) {

        try {
            input = (String) data;
        } catch (ClassCastException ex) {
            vr = new ValidationResult(false, "Input could not be cast to correct type 'java.lang.String'");
        }

        //check if the data is a non-null object
        if (input == null) {
            vr = new ValidationResult(false, "Input was 'null'");
        } else {
            try {
                // check if we have to expect alignment data
                if (isAlignment()) {

                    List<String> sequences = validateAndRetrieveAlignedNucleicAcidSequences(new StringReader(input));

                    // check cardinality
                    if (cardinality.equals(CARDINALITY.single) && sequences.size() != 1) {
                        return new ValidationResult(false, "Data is in DotBracket format, but contains more than one sequence/structures!");
                    }
                    if (cardinality.equals(CARDINALITY.multi) && sequences.size() < 2) {
                        return new ValidationResult(false, "Data is in DotBracket format, but contains less than two sequences/structures!");
                    }

                    // check strictness for each sequence/structure (only in case of STRICTNESS.strict, STICTNESS.ambigious is part RNAStructML schema defintion
                    if (strictness.equals(STRICTNESS.strict)) {
                        for (String seq : sequences) {
                            // check sequence
                            if (!strictalignmentrna.matcher(seq).find()) {
                                return new ValidationResult(false, " Sequencedata doesn't match strict aligned RNA! ");
                            }
                            // check structure -- not neccessary
                        }
                    }
                    vr = new ValidationResult(true, "Input is alid aligned DotBracket data!");

                } else {

                    List<String> sequences = validateAndRetrieveNucleicAcidSequences(new StringReader(input));
                    // now in more detail

                    // check cardinality
                    if (cardinality.equals(CARDINALITY.single) && sequences.size() != 1) {
                        return new ValidationResult(false, "Data is in DotBracket format, but contains more than one sequence/structures!");
                    }
                    if (cardinality.equals(CARDINALITY.multi) && sequences.size() < 2) {
                        return new ValidationResult(false, "Data is in DotBracket format, but contains less than two sequences/structures!");
                    }

                    // check strictness for each sequence/structure (only in case of STRICTNESS.strict, STICTNESS.ambigious is part RNAStructML schema defintion
                    if (strictness.equals(STRICTNESS.strict)) {
                        for (String seq : sequences) {
                            // check sequence
                            if (!strictrna.matcher(seq).find()) {
                                return new ValidationResult(false, " Sequencedata doesn't match strict RNA! ");
                            }
                            // check structure -- not neccessary
                        }
                    }
                    vr = new ValidationResult(true, "Input is valid DotBracket data!");
                }
            } catch (ParserToolException ex) {
                vr = new ValidationResult(false, "Input could not be validated as DotBracket String ! Exception message was: " + ex.getLocalizedMessage());
            } catch (IOException ex) {
                vr = new ValidationResult(false, "Input could not be validated due to an IOException! Exception message was: " + ex.getLocalizedMessage());
            }
        }
        return vr;
    }
    private boolean alignment;

    // ############# implementation of Interface AlignmentValidator #############
    @Override
    public boolean isAlignment() {
        return alignment;
    }

    @Override
    public void setAlignment(boolean alignment) {
        this.alignment = alignment;
        cardinality = CARDINALITY.multi; // alignment allways have cardinality multi (2+ sequences)

    }
    private STRICTNESS strictness;

    @Override
    public STRICTNESS getStrictness() {
        return strictness;
    }

    @Override
    public void setStrictness(STRICTNESS strictness) {
        this.strictness = strictness;
    }
    private CARDINALITY cardinality;

    @Override
    public CARDINALITY getCardinality() {
        return cardinality;
    }

    @Override
    public void setCardinality(CARDINALITY cardinality) {
        this.cardinality = cardinality;
    }

    @Override
    public CONTENT getContent() {
        return CONTENT.RNA;
    }

    @Override
    public void setContent(CONTENT content) {
        if (!content.equals(CONTENT.RNA)) {
            throw new UnsupportedContentException("DotBracket_Validator only supports RNA as content!");
        }
    }

    /**
     * Loops through the structure to validate it and return all encountered Aligned Nucleic Acid Sequences
     * @param dotbracketFasta Reader of Input String
     * @return all AlignedNucleicAcidSequences
     * @throws ParserToolException input is not validated as aligned DotBracked
     * @throws IOException could not read from Reader dotbracketFasta
     */
    public List<String> validateAndRetrieveAlignedNucleicAcidSequences(Reader dotbracketFasta) throws ParserToolException, IOException {
        // parse dot bracket
        LineNumberReader br = new LineNumberReader(new BufferedReader(dotbracketFasta));
        String line = null;
        String id = null;

        String sequencedata = null;
        String structuredata = null;

        List<String> sequences = new ArrayList();

        int mode = 0;
        while ((line = br.readLine()) != null) {

            if (line.isEmpty()) {
                // skip empty line! exception otherwise!
            } else {

                switch (mode) {
                    case 0:
                        if (line.charAt(0) == '>') {
                            final String[] idDesc = line.substring(1).trim().split(" ", 2);
                            // get id
                            if (idDesc.length > 0) {
                                id = idDesc[0];
                            } else {
                                id = "(no ID)";
                            }
                            mode = 1;
                        } else {
                            throw new ParserToolException("ID (and description) expected!");
                        }
                        break;
                    case 1:
                        Matcher seqmatch = sequencePattern.matcher(line);
                        mode = 2;
                        if (seqmatch.matches()) {
                            sequencedata = line;
                            break;
                        }
                    case 2:
                        Matcher structmatch = structure_with_energy.matcher(line);
                        if (structmatch.matches()) {
                            structuredata = structmatch.group(1);
                            mode = 3;
                        } else {
                            throw new ParserToolException("Either Sequencedata or Structuredata expected!");
                        }

                    case 3:

                        if (sequencedata != null && sequencedata.length() != structuredata.length()) {
                            throw new ParserToolException("DotBracket data with id " + id + " contains sequence data (length :" + sequencedata.length() + ") and structure data (length :" + structuredata.length() + ") with different length!");
                        }

                        if (sequencedata != null) {
                            sequences.add(sequencedata);
                        }

                        structuredata = null;
                        sequencedata = null;

                        mode = 0;

                        break;
                }
            }
        }
        br.close();
        return sequences;
    }

    /**
     * Loops through the structure to validate it and return all encountered Nucleic Acid Sequences
     * @param dotbracketFasta Reader of Input String
     * @return all NucleicAcidSequences
     * @throws ParserToolException input is not validated as aligned DotBracked
     * @throws IOException could not read from Reader dotbracketFasta
     */
    public List<String> validateAndRetrieveNucleicAcidSequences(final Reader dotBracketFasta)
            throws ParserToolException, IOException {
        LineNumberReader br = new LineNumberReader(new BufferedReader(dotBracketFasta));
        String line = null;
        String id = null;
        String sequence = null;
        List<String> structure = new ArrayList<String>();
        List<String> idList = new ArrayList<String>();

        List<String> sequences = new ArrayList<String>();
        int linenumber = -1;

        while ((line = br.readLine()) != null) {
            ++linenumber;
            if (!(line.equals(""))) {
                /* if line does NOT start with ">"
                assuming it's contains sequence or dotbracket information
                information */
                if (line.charAt(0) != '>') { // 
                    if (id != null) {
                        /* if Pattern matches, assuming it contains
                         * RNAStructureInformation
                         */
                        if (rnastructPattern.matcher(line).matches()) {
                            structure.add(line);
                            /* else it must be sequence content */
                        } else {
                            if (sequence == null) {
                                sequence = line;
                            } else {
                                /* if sequence was previous set,
                                 * the structure line contains a 
                                 * invalid character */
                                throw new ParserToolException("line " + linenumber + ": DotBracket expected (line contains an invalid character)");
                            }
                        }
                    } else {
                        throw new ParserToolException("line " + linenumber + ": No id before structure/sequence starts!");
                    }
                    /* line start with ">" or it is the last line
                    if any id exists, we've parsed an sequence before and can
                    add it to current dom */
                } else {
                    if (id != null) {
                        if (sequence != null) {
                            sequences.add(sequence);
                        }
                        if (structure.size() > 0) {
                            for (String struct : structure) {
                                Matcher m = rnastructWithEnergyPattern.matcher(struct);
                                if (m.find()) {
                                    if //struct and energy and shape
                                            (m.group(3) != null && m.group(2) != null && m.group(1) != null) {
                                        checkDotbracket(m.group(1));
                                        checkShape(m.group(3));

                                    } else if //struct and energy
                                            (m.group(2) != null && m.group(1) != null) {
                                        checkDotbracket(m.group(1));
                                    } else if // only structure
                                            (m.group(1) != null) {
                                        checkDotbracket(m.group(1));
                                    }
                                } else {
                                    throw new ParserToolException("line " + linenumber + " contains unexpected char(s)!");
                                }
                            }
                        } else {
                            throw new ParserToolException("line " + linenumber + ": No structure information before next structure starts!");
                        }
                    }
                    final String[] idDesc = line.substring(1).trim().split(" ", 2);
                    // get id
                    if (idDesc.length > 0) {
                        id = idDesc[0];
                        if (idList.contains(id)) {
                            throw new ParserToolException("Id " + id + " exits twice!");
                        } else {
                            idList.add(id);
                        }
                    } else {
                        id = "(no ID)";
                    }
                    // clear structure list
                    structure.clear();
                    // clear sequence
                    sequence = null;
                }
            }
        }
        br.close();
        //process last entry
        if (id != null) {
            if (sequence != null) {
                sequences.add(sequence);
            }
            if ((structure.size() > 0)) {
                for (String struct : structure) {
                    Matcher m = rnastructWithEnergyPattern.matcher(struct);
                    m.find();
                    if //struct and energy and shape
                            (m.group(3) != null && m.group(2) != null && m.group(1) != null) {
                        checkDotbracket(m.group(1));
                        checkShape(m.group(3));
                    } else if //struct and energy
                            (m.group(2) != null && m.group(1) != null) {
                        checkDotbracket(m.group(1));
                    } else if // only structure
                            (m.group(1) != null) {
                        checkDotbracket(m.group(1));
                    }
                }
            } else {
                throw new ParserToolException("line " + linenumber + ": No structure information before next structure starts!");
            }
        }

        return sequences;
    }

    /**
     * This method checks a shape for syntactical correctness
     * @param shapeString
     * @return true if not empty and syntactical correct, false otherwise
     * @throws ParserToolException
     */
    private boolean checkShape(final String shapeString) throws ParserToolException {
        return checkStructureOrShape(shapeString, SHAPE);
    }

    /**
     * This method checks a dotbracket string for syntactical correctness 
     * @param dotbracketString
     * @return true if not empty and syntactical correct, false otherwise 
     */
    private boolean checkDotbracket(final String dotbracketString) throws ParserToolException {
        return checkStructureOrShape(dotbracketString, STRUCTURE);
    }

    /**
     * This method checks a dotbracket string or a shape string for syntactical
     * correctness
     * @param checkString   - structure in dotbracket format or shape
     * @param type          - STRUCTURE for structure, SHAPE for shape
     * @return true if not empty and syntactical correct, false otherwise
     */
    private boolean checkStructureOrShape(final String checkString, final int type) throws ParserToolException {


        if ((checkString == null) || (checkString.equals(""))) {
            // checks if structure is empty
            return false;
        }
        final char[] tokens = checkString.toCharArray();
        if ((type != STRUCTURE) && (type != SHAPE)) {

            throw new ParserToolException("Error! Invalid type in checkStructureOrShape! Type : '" + type + "'");
        }
        int counterSimple = 0;    // counter for '('-brackets
        int counterEdged = 0;     // counter for '['-brackets
        int counterTwisted = 0;   // counter for '{'-brackets
        int counterPointed = 0;   // counter for '<'-brackets
        String currenttoken;

        for (int i = 0; i < tokens.length; i++) {
            currenttoken = "" + tokens[i];
            //log.config("current token : '" + currenttoken + "'");
            switch (tokens[i]) {
                // incr opening brackets counter
                case '(':
                    counterSimple++;
                    break;
                case '[':
                    counterEdged++;
                    break;
                case '{':
                    counterTwisted++;
                    break;
                case '<':
                    counterPointed++;
                    break;

                // decr the brackets counter 
                case ')':
                    counterSimple--;
                    if (counterSimple < 0) {
                        // to few opening brackets
                        return false;
                    }
                    break;
                case ']':
                    counterEdged--;
                    if (counterEdged < 0) {
                        // to few opening brackets
                        return false;
                    }
                    break;
                case '}':
                    counterTwisted--;
                    if (counterTwisted < 0) {
                        // to few opening brackets
                        return false;
                    }
                    break;
                case '>':
                    counterPointed--;
                    if (counterPointed < 0) {
                        // to few opening brackets
                        return false;
                    }
                    break;
                case '.':  // ignore case STRUCTURE
                    if (type == STRUCTURE) {
                        break;
                    }
                    //if we reach this point, there is an error and return false...
                    return false;
                case '_':  // ignore case SHAPE
                    if (type == SHAPE) {
                        break;
                    }
                        //if we reach this point, there is an error and return false...

                    return false;
                default:  // invalid symbol found
                    return false;
            }
        }
        if (counterSimple + counterEdged + counterTwisted + counterPointed > 0) {
            // check if some brackets are still open...
            return false;
        }

        return true;
    }
}
