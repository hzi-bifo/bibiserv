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


import de.unibi.cebitec.bibiserv.sequenceparser.parser.impl.ConnectParser;
import de.unibi.cebitec.bibiserv.sequenceparser.parser.impl.ConnectSingleStructureData;
import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.Converter;
import java.util.ArrayList;
import java.util.Set;

/**
 * This class converts .ct (connect) structure data to dotbracket-data.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class CT2DOTBRACKET implements Converter {

    private static final String NEWLINE = System.getProperty("line.separator");
    private static final char[] lbrackets = {'(', '[', '{', '<'};
    private static final char[] rbrackets = {')', ']', '}', '>'};

    @Override
    public Object convert(Object fromdata) throws ConversionException {
        String inputdata;
        try {
            inputdata = (String) fromdata;
        } catch (ClassCastException e) {
            throw new ConversionException("Input could not be cast to String!", e);
        }
        // parse input.
        ConnectParser parser = new ConnectParser(inputdata, false);
        parser.parseData();
        // get structures.
        ArrayList<ConnectSingleStructureData> structures = parser.getStructures();
        // create stringbuilder for output.
        StringBuilder dotBracketContent = new StringBuilder();
        for (ConnectSingleStructureData structure : structures) {
            //convert all structures.
            ctStructureToDotBracketContent(dotBracketContent, structure);
            dotBracketContent.append(NEWLINE);
            dotBracketContent.append(NEWLINE);
        }
        return dotBracketContent.toString();
    }

    private void ctStructureToDotBracketContent(StringBuilder dotBracketContent,
            ConnectSingleStructureData structure) {
        //append energy info.
        dotBracketContent.append(">");
        dotBracketContent.append(structure.getEnergyHeader());
        dotBracketContent.append(NEWLINE);

        //get indices contained in structure.
        Set<Integer> baseIndices = structure.getBaseIndices();
        //char array for line containing base content.
        int size = structure.getMaxIndex() - structure.getMinIndex() + 1;
        char[] bases = new char[size];
        //char array for line containing dotBracket content.
        char[] dotBracket = new char[size];
        //store last occurence for each bracket type.
        int[] lastOccurance = new int[lbrackets.length];
        //initialize array
        for (int currentBracket = 0;
                currentBracket < lbrackets.length; currentBracket++) {
            lastOccurance[currentBracket] = -1;
        }
        //counter variable for current bracket type
        int bracketCounter = 0;
        //index variable for result dot bracket array
        int arrayIndex = 0;
        //current base partner.
        int basePartner;
        //iterate over base indices in ct structure.
        for (int currentIndex = structure.getMinIndex(); currentIndex <= structure.getMaxIndex(); currentIndex++) {

            //set base
            bases[arrayIndex] = structure.getBase(currentIndex);
            //check if this base has a partner
            if (structure.getBasePartner(currentIndex) != null && (basePartner = structure.getBasePartner(currentIndex)) != 0) {
                //only set those that haven't been set yet.
                if (currentIndex < basePartner) {
                    //check if last occurances were passed. If so, reduce bracket counter.
                    //also do so if current base partner has a smaller index than
                    //the last occurance of the bracket.
                    for (int currentBracket = lbrackets.length - 1;
                            currentBracket >= 0; currentBracket--) {
                        if (lastOccurance[currentBracket] != -1) {
                            if (currentIndex > lastOccurance[currentBracket]) {
                                lastOccurance[currentBracket] = -1;
                                bracketCounter = currentBracket;
                            } else if (basePartner
                                    <= lastOccurance[currentBracket]) {
                                bracketCounter = currentBracket;
                            }
                        }
                    }
                    //check if new referenced base pairing partner is after
                    //partner for last occurence of current bracket. If so
                    //another kind of bracket has to be used.
                    while (lastOccurance[bracketCounter] != -1
                            && basePartner > lastOccurance[bracketCounter]) {
                        bracketCounter++;
                        if (bracketCounter >= lbrackets.length) {
                            //if all bracket types are used conversion is not possible.
                            return;
                        }
                    }
                    //use current bracket type to visualize base pairing.
                    dotBracket[arrayIndex] = lbrackets[bracketCounter];
                    dotBracket[basePartner - structure.getMinIndex()] =
                            rbrackets[bracketCounter];
                    //store last occurance.
                    lastOccurance[bracketCounter] = basePartner;
                }
            } else {
                // if there is no partner, set a dot.
                dotBracket[arrayIndex] = '.';
            }
            //increment array index.
            arrayIndex++;
        }
        //append content.
        dotBracketContent.append(bases);
        dotBracketContent.append(NEWLINE);
        dotBracketContent.append(dotBracket);
    }
}
