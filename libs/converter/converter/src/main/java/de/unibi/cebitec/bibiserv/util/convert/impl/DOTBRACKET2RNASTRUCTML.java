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
package de.unibi.cebitec.bibiserv.util.convert.impl;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.IDGenerator;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CONTENT;
import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.SequenceConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.hobit.xsds._20060201.rnastructml.RnastructML;
import net.sourceforge.hobit.xsds._20060201.rnastructml.RnastructureWithProgramType;
import net.sourceforge.hobit.xsds._20060201.rnastructml.SequenceType;
import net.sourceforge.hobit.xsds._20060201.rnastructml.ObjectFactory;
import net.sourceforge.hobit.xsds._20060201.rnastructml.ShapeType;
import net.sourceforge.hobit.xsds._20060201.rnastructml.StructureType;


/**
 * DotBracket to RNAStructML converter.
 *  
 * @author Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 *         Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de 
 */
public class DOTBRACKET2RNASTRUCTML implements SequenceConverter {

    private CONTENT content;
    // RegExp for Structure line
    private static final Pattern rnastructPattern = Pattern.compile("^[\\(\\)\\[\\]\\{\\}\\<\\>.]+.*$");
    // RegExp for Structure with Energy line
    private static final Pattern rnastructWithEnergyPattern = Pattern.compile("^([\\(\\)\\[\\]\\{\\}\\<\\>.]+)(?:\\s+\\(?(-?\\d+(?:\\.\\d+)?)\\)?)?(?:\\s+([_\\[\\]]+))?\\s*$");
    // thje root of the dom to create
    private RnastructML jaxbRnastructmlRoot;
    
    /* hashtables to keep track of IDs
     * The id of each structure in rnaStructureHash is mapped to two hashmaps 
     * containing the ids of all shapes and all sequence of this structure
     * Each structure is initialized in appendEmptyRNAStructure(). The ids
     * of the sequences and structures are than added in appendSequences() and
     * appendShape().
     */
    private HashMap<String, HashMap> rnastructureHash = new HashMap<String, HashMap>();
    private static final String SHAPESHASHNAME = "shapesHash";
    private static final String SEQUENCEHASHNAME = "sequenceHash";
    // hastable keeps track of all current structures, Maps the id of the 
    // structure to its object 
    private HashMap<String, RnastructureWithProgramType> structureListHash = new HashMap<String, RnastructureWithProgramType>();

    @Override
    public void setContent(CONTENT content) {
        this.content = content;
    }

    @Override
    public Object convert(Object fromdata) throws ConversionException {
        if (!(fromdata instanceof String)) {
            throw new ConversionException("'java.lang.String' as input expected!");
        }
        try {

            ObjectFactory rnastructMLObjectFactory = new ObjectFactory();
            jaxbRnastructmlRoot = rnastructMLObjectFactory.createRnastructML();
            fromDotBracketFasta(new StringReader((String) fromdata));

            // return the dom
            return jaxbRnastructmlRoot;

        } catch (Exception e) {
            throw new ConversionException("Cannot convert input (in Fasta format) to RNAStructML!", e);
        }
    }

    /** method that appends sequence and structure information to RNAStructML
     *  by extracting the data from a DotBracketFasta string. DotBracketFasta must
     *  follow the following format rules:
     *  >IDSTRING DESCRIPTION
     *  SEQUENCESTRING
     *  DOTBRACKETSTRING ENERGY(optional)  - this line can occurs one or more
     *  >IDSTRING2 DESCRIPTION2
     *  SEQUENCESTRING2
     *  DOTBRACKETSTRING2 ENERGY(optional) - this line can occurs one or more
     *  
     * @param dotbracketFasta        - DotBracketFasta Reader, see format above
     * @throws ConversionException   - on error
     * @throws IOException           - on IOError
     */
    public void fromDotBracketFasta(final Reader dotBracketFasta)
            throws ConversionException, IOException {
        LineNumberReader br = new LineNumberReader(new BufferedReader(dotBracketFasta));
        String line = null;
        String id = null;
        String description = null;
        String sequence = null;
        List<String> structure = new ArrayList<String>();
        int linenumber = -1;

        while ((line = br.readLine()) != null) {
            ++linenumber;
            if (!line.equals("")) {
                /* if line does NOT start with ">"
                assuming it's contains sequence or dotbracket information
                information */
                if (line.charAt(0) != '>') { // 
                    if (id != null) {
                        /* if Pattern matches, assuming it contains
                         * RNAStructureInformation
                         */
                        //if (rnastructWithEnergyPattern.matcher(line).matches()) {
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
                                throw new ConversionException("line " + linenumber + ": DotBracket expected (line contains an invalid character)");
                            }
                        }
                    } else {
                        throw new ConversionException("line " + linenumber + ": No id before structure/sequence starts!");
                    }
                    /* line start with ">" or it is the last line
                    if any id exists, we've parsed an sequence before and can
                    add it to current dom */
                } else {
                    if (id != null) {
                        String ID = appendEmptyRNAStructure();
                        if (sequence != null) {
                            appendSequence(sequence, id, description, ID);
                        }
                        if (structure.size() > 0) {
                            for (String struct : structure) {
                                Matcher m = rnastructWithEnergyPattern.matcher(struct);
                                if (m.find()) {
                                    if //struct and energy and shape
                                            (m.group(3) != null && m.group(2) != null && m.group(1) != null) {
                                        appendStructure(m.group(1), null, Double.parseDouble(m.group(2)), appendShape(m.group(3), ID), ID);
                                    } else if //struct and energy
                                            (m.group(2) != null && m.group(1) != null) {
                                        appendStructure(m.group(1), null, Double.parseDouble(m.group(2)), null, ID);
                                    } else if // only structure
                                            (m.group(1) != null) {
                                        appendStructure(m.group(1), null, null, null, ID);
                                    }
                                } else {
                                    throw new ConversionException("line " + linenumber + " contains unexpected char(s)!");
                                }
                            }
                        } else {
                            throw new ConversionException("line " + linenumber + ": No structure information before next structure starts!");
                        }
                    }
                    // removing leading whitespaces and get id and description
                    final String[] idDesc = line.substring(1).trim().split(" ", 2);
                    // get id
                    if (idDesc.length > 0) {
                        id = idDesc[0];
                    } else {
                        id = IDGenerator.generateId();
                    }
                    // get description if given
                    if (idDesc.length > 1) {
                        description = idDesc[1];
                    } else {
                        description = null;
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
            String ID = appendEmptyRNAStructure();
            if (sequence != null) {
                appendSequence(sequence, id, description, ID);
            }
            if (structure.size() > 0) {
                for (String struct : structure) {
                    Matcher m = rnastructWithEnergyPattern.matcher(struct);
                    m.find();
                    if //struct and energy and shape
                            (m.group(3) != null && m.group(2) != null && m.group(1) != null) {
                        appendStructure(m.group(1), null, Double.parseDouble(m.group(2)), appendShape(m.group(3), ID), ID);
                    } else if //struct and energy
                            (m.group(2) != null && m.group(1) != null) {
                        appendStructure(m.group(1), null, Double.parseDouble(m.group(2)), null, ID);
                    } else if // only structure
                            (m.group(1) != null) {
                        appendStructure(m.group(1), null, null, null, ID);
                    }
                }
            } else {
                throw new ConversionException("line " + linenumber + ": No structure information before next structure starts!");
            }

        }
        linenumber = -1;
    }

    /**
     * method that adds a new structure (in dotbracket format) to a rnastructure
     * element
     * @param dotBracket        - String in dotbracket format
     * @param structProb        - Double probability of the structure
     * @param energy            - Double energy of the structure
     * @param shaperef          - reference to a shape, must be the shape id (!)
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public void appendStructure(final String dotBracket, final Double structProb,
            final Double energy, final String shaperef, String rnastructure_id) throws ConversionException {


        // check if the structure is syntactical correct not need, already validated!

        // get the correct rnastructure element
        // check if this shape does already exist in the given rnastructure
        if (!rnastructureHash.containsKey(rnastructure_id)) {
            throw new ConversionException("Error! Given rnastructure id '" + rnastructure_id + "' does not exist!");
        }


        RnastructureWithProgramType rnastructure = getRnaStructureByID(rnastructure_id);
        // construct structure element
        ObjectFactory rnastructMLObjectFactory = new ObjectFactory();
        StructureType newStructure = rnastructMLObjectFactory.createStructureType();
        newStructure.setValue(dotBracket);// this one is required!

        // adding all attributes that are not empty
        if (structProb != null) {
            newStructure.setProbability(structProb);
        }
        if (energy != null) {
            newStructure.setEnergy(energy);
        }
        if (shaperef != null) {
            newStructure.setShaperef(getShapeByShapeID(rnastructure, shaperef));
        }

        // adding structure to rnastructure
        rnastructure.getShapeOrStructure().add(newStructure);
    }

    /**
     * method that adds a rnasequence to a rnastructure element
     * @param rnasequence       - String rna sequence, only rna chars are allowed
     * @param seqId             - String sequence id, e.g. from fasta
     * @param description       - String that describes the sequence, e.g. fasta comment
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws ConversionException
     */
    public void appendSequence(final String rnasequence, String seqId,
            final String description, String rnastructure_id) throws ConversionException {


        // check for invalid sequence data
        if (((rnasequence == null) || (rnasequence.equals("")))) {
            throw new ConversionException("Error! Empty sequence given!");
        }

        // get the correct rnastructure element, create a new one if no id was
        // given
        if (rnastructure_id == null) {
            rnastructure_id = appendEmptyRNAStructure();
        } else {
            // check if this shape does already exist in the given rnastructure
            if (!rnastructureHash.containsKey(rnastructure_id)) {
                throw new ConversionException("Error! Given rnastructure id '" + rnastructure_id + "' does not exist!");
            }
        }

        RnastructureWithProgramType rnastructure = getRnaStructureByID(rnastructure_id);
        //rmadsack: notwendig? oder immer direkt ein neues Sequence Element?
        //check if sequence is set for given rnastructure
        if (!rnastructure.isSetSequence()) {
            ObjectFactory rnastructMLObjectFactory = new ObjectFactory();

            SequenceType newSequence = rnastructMLObjectFactory.createSequenceType();
            rnastructure.setSequence(newSequence);
        }

        SequenceType sequence = rnastructure.getSequence();

        // add description element
        if (description != null) {
            sequence.setDescription(description);
        }

        //add the nucleic Sequence
        sequence.setNucleicAcidSequence(rnasequence);


        // adding attributes that are not empty
        if (seqId != null) {
            sequence.setSeqID(seqId);
        } else {
            seqId = IDGenerator.generateId();
            sequence.setSeqID(seqId);
        }
        final HashMap superHash = rnastructureHash.get(rnastructure_id);
        final HashMap seqHash = (HashMap) superHash.get(SEQUENCEHASHNAME);
        //here we check for double sequence IDs and throw an exception if we find doubles
        if (seqHash.containsKey(seqId)) {
            throw new ConversionException("Error! Double sequence id! SeqId: '" + seqId + "'");
        }
        //otherwise, all is well and we can add this sequence  ID
        seqHash.put(seqId, 1);
    }

    /**
     * method that appends a given shape to the referenced rnastructure element.
     * returns the shape (!) id
     * @param shape             - String shape, see above for format
     * @param rnastructure_id   - String id of the rnastructure element
     * @return String           - id of the shape (!)
     * @throws ConversionException
     */
    public String appendShape(final String shape, String rnastructure_id) throws ConversionException {

        // checking if shape is syntactical correct not needed, already done in validator

        // get the correct rnastructure element,
        // check if this shape does already exist in the given rnastructure
        if (rnastructureHash.containsKey(rnastructure_id)) {
            final HashMap<String, HashMap<String, Object>> superHash = rnastructureHash.get(rnastructure_id);
            final HashMap<String, Object> shapes = superHash.get(SHAPESHASHNAME);
            if (shapes.containsKey(shape)) {
                // return the id... shape is already in the rnastructure
                return (String) shapes.get(shape);
            }
        } else {
            // fatal error
            throw new ConversionException("Referencing an invalid rnastructure : '" + rnastructure_id + "'");
        }

        RnastructureWithProgramType rnastructure = getRnaStructureByID(rnastructure_id);

        // construct shape element
        ObjectFactory rnastructMLObjectFactory = new ObjectFactory();
        ShapeType newShape = rnastructMLObjectFactory.createShapeType();
        newShape.setValue(shape);


        String shapeId = IDGenerator.generateId();
        newShape.setId(shapeId);

        //rmadack: notwendig als ID zu deklarieren?
        //shapeElem.setIdAttributeNS(null,ATT_ID, true);

        // adding shape to rnastructure
        rnastructure.getShapeOrStructure().add(newShape);

        // adding shape to hash...
        final HashMap<String, HashMap<String, String>> superHash = rnastructureHash.get(rnastructure_id);
        final HashMap<String, String> elementHash = superHash.get(SHAPESHASHNAME);
        elementHash.put(shape, shapeId); //shape is the id in the hash NOT the id... ;)

        // returning shape_id
        return shapeId;
    }

    /**
     * method that adds an empty rnastructure element to the root element (jaxbRnastructmlRoot).
     * the method returns the id of the added element.
     * @return String   - id of the added rnastructure element 
     */
    public String appendEmptyRNAStructure() {

        // creates an empty rnastructure element

        ObjectFactory rnastructMLObjectFactory = new ObjectFactory();
        RnastructureWithProgramType emptyRnastructure = rnastructMLObjectFactory.createRnastructureWithProgramType();

        final String idString = IDGenerator.generateId();

        // set RNA Structure Id
        structureListHash.put(idString, emptyRnastructure);

        //rmadsack: muss die ID als true deklariert werden?
        //rnastructureElem.setIdAttributeNS(null,ATT_ID, true);

        jaxbRnastructmlRoot.getRnastructure().add(emptyRnastructure);
        // adding id to hash
        final HashMap<String, HashMap> superHash = new HashMap<String, HashMap>();
        final HashMap sequenceHash = new HashMap();
        final HashMap shapesHash = new HashMap();
        superHash.put(SEQUENCEHASHNAME, sequenceHash);
        superHash.put(SHAPESHASHNAME, shapesHash);
        rnastructureHash.put(idString, superHash);
        return idString;
    }

    /**
     * Returns a RNAStructure with, if existists, the given id, otherwise null
     * @param rnastructureID unique identification of a RNAStructure
     * @return RNAStructure with ID:rnastructureID or null
     */
    private RnastructureWithProgramType getRnaStructureByID(String rnastructureID) {
        RnastructureWithProgramType ret;
        if ((ret = structureListHash.get(rnastructureID)) != null) {
            return ret;
        }
        return null;
    }

    
    private List<ShapeType> getShapeList(RnastructureWithProgramType t_rnastructure) {
        List<ShapeType> shapelist = new ArrayList<ShapeType>();
        for (Object object : t_rnastructure.getShapeOrStructure()) {
            if (object.getClass().equals(ShapeType.class)) {
                shapelist.add((ShapeType) object);
            }
        }
        return shapelist;

    }

    private ShapeType getShapeByShapeID(RnastructureWithProgramType t_rnastructure, String shapeID) {
        for (ShapeType shape : getShapeList(t_rnastructure)) {
            if (shape.isSetId()) {
                if (shapeID.equals(shape.getId())) {
                    return shape;
                }
            }
        }
        return null;
    }
}
