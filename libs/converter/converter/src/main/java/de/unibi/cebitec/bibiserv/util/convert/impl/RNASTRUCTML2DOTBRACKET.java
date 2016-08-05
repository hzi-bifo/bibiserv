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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import net.sourceforge.hobit.xsds._20060201.rnastructml.RnastructML;
import net.sourceforge.hobit.xsds._20060201.rnastructml.RnastructureWithProgramType;
import net.sourceforge.hobit.xsds._20060201.rnastructml.SequenceType;
import net.sourceforge.hobit.xsds._20060201.rnastructml.ShapeType;
import org.w3c.dom.Document;
import net.sourceforge.hobit.xsds._20060201.rnastructml.StructureType;

/**
 * RNAStructML to DotBracket converter
 * 
 * @author Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 *         Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class RNASTRUCTML2DOTBRACKET implements SequenceConverter {

    protected CONTENT content;

    @Override
    public void setContent(CONTENT content) {
        this.content = content;
    }

    @Override
    public Object convert(Object fromdata) throws ConversionException {
        try {
            RnastructML rml = null;
            // fromdata could be a String ...
            if (fromdata instanceof java.lang.String) {
                rml = new RnastructML();
                JAXBContext jc;
                try {
                    jc = JAXBContext.newInstance(net.sourceforge.hobit.xsds._20060201.rnastructml.RnastructML.class);
                    rml = (RnastructML) (jc.createUnmarshaller().unmarshal(new StringReader((String) fromdata)));
                } catch (JAXBException e) {
                    throw new ConversionException("Input detected as RNAStructML(String), but can't be read!", e);
                } catch (ClassCastException e) {
                    throw new ConversionException("Submitted object is no valide RNAStructML");
                }
            } else // or a SequenceML jaxb object ...
            if (fromdata instanceof RnastructML) {
                rml = (RnastructML) ((net.sourceforge.hobit.xsds._20060201.rnastructml.RnastructML) fromdata);
            } else // or a SequenceML dom object ...
            if (fromdata instanceof Document) {

                JAXBContext jc;
                try {
                    jc = JAXBContext.newInstance(net.sourceforge.hobit.xsds._20060201.rnastructml.RnastructML.class);
                    Unmarshaller u = jc.createUnmarshaller();
                    Object o = u.unmarshal((Document) fromdata);

                    if (o instanceof JAXBElement) {
                        rml = (RnastructML) (((JAXBElement) o).getValue());
                    } else {
                        rml = (RnastructML) o;
                    }
                } catch (JAXBException e) {
                    throw new ConversionException("Input detected as RNAStructML(Document), but can't be read!", e);
                } catch (ClassCastException e) {
                    throw new ConversionException("Submitted object is no valide RNAStructML");
                }

            } else {
                throw new ConversionException("Input cannot read as SequenceML and converted to Fasta/Pearson as 'java.lang.String'");
            }
            return toDotBracketFasta(rml);
        } catch (ConversionException e) {
            throw new ConversionException("Input cannot converted from SequenceML to DotBracket!", e);
        }
    }

    /**
     * method that returns a DotBracketFasta representation for the given root
     * @return String           - DotBracketFasta String
     * @throws ConversionException  - on error
     */
    public String toDotBracketFasta(RnastructML root) throws ConversionException {
        
        createStructureIdsInDom(root);
        StringBuffer retDotBracketFasta = new StringBuffer();
        final List<String> rnastructures = getRnastructureIds(root);
        for (String rnastructure : rnastructures) {
            final Hashtable<String, Object> sequence = getSequence(rnastructure, root);
            final List<Hashtable<String, Object>> structures = getStructures(rnastructure, root);

            // start with fasta header
            retDotBracketFasta.append('>');
            // check if this structure has a sequence;
            if (sequence != null) {
                // build complete header 
                StringBuffer header = new StringBuffer();
                if (sequence.containsKey("seqID")) {
                    header.append(sequence.get("seqID"));
                }
                if (sequence.containsKey("name")) {
                    header.append(' ').append(sequence.get("name"));
                }
                if (sequence.containsKey("description")) {
                    header.append(' ').append(sequence.get("description"));
                }
                // and add it
                retDotBracketFasta.append(header).append(LINEBREAK);
                // add sequence data
                retDotBracketFasta.append(sequence.get("sequence"));
            }
            retDotBracketFasta.append(LINEBREAK);

            for (Hashtable structure : structures) {
                if (structure.containsKey("structure")) {
                    retDotBracketFasta.append(structure.get("structure"));
                    if (structure.get("energy") != null) {
                        retDotBracketFasta.append(" (").append(structure.get("energy")).append(')');
                    }
                    if (structure.get("shaperef") != null) {
                        ShapeType shape = (ShapeType) structure.get("shaperef");
                        retDotBracketFasta.append(" ").append(shape.getValue());
                    }
                    retDotBracketFasta.append(LINEBREAK);
                } else {
                    throw new ConversionException("Error! Missing structure key in hashtable!");
                }
            }

        }
        return retDotBracketFasta.toString();
    }

    /**
     * Adds to all structures in the dom without an id a new id for
     * identification. 
     * @param root root of the dom
     */
    private void createStructureIdsInDom(RnastructML root) {

        // adding ids to hash
        List<RnastructureWithProgramType> structureList = root.getRnastructure();
        String idString = null;

        for (RnastructureWithProgramType t_Rnastructure : structureList) {
            if (isSetRnaStructureID(t_Rnastructure)) {
                idString = getRnaStructureID(t_Rnastructure);
            } else {
                idString = null;
            }

            if ((idString == null) || (idString.equals(""))) {

                idString = IDGenerator.generateId();
                setRnaStructureID(t_Rnastructure, idString);

            }
        }

    }

    /**
     * returns the ids of structures in the dom.
     * @param root root of the dom
     * @return list of all structure ids
     */
    public List<String> getRnastructureIds(RnastructML root) {

        List<RnastructureWithProgramType> rnastructureList = root.getRnastructure();

        List<String> ids = null;

        if (rnastructureList.isEmpty()) {
            return null;
        } else {
            ids = new ArrayList<String>();
            for (RnastructureWithProgramType t_Rnastructure : rnastructureList) {

                ids.add(getRnaStructureID(t_Rnastructure));
            }
        }
        return ids;
    }

    /**
     * Returns the id of the given structure.
     * @param t_rnastructure
     * @return id of the given structure
     */
    private String getRnaStructureID(RnastructureWithProgramType t_rnastructure) {
        Map<QName, String> attributes = t_rnastructure.getOtherAttributes();
        String id = attributes.get(new QName("id"));
        return id;
    }

    /**
     * getter method. returns a Hashtable containing the sequence information.
     * Keys in the Hash are generated only for information items contained 
     * in the sequence element!
     * key/values:
     * seqID / String                   - id of the sequence
     * name / String                    - name of the sequence
     * synonyms / List<String>          - synonyms to the name
     * description / String             - description of the sequence
     * sequence / String                - the sequence string, this element is 
     *                                    not in the Hash, if the sequenceType 
     *                                    is EMPTYSEQUENCE
     * comment / String                 - String with comment
     *              
     * @param rnastructure_id           - id of the rnastructure element
     * @return Hashtable<String,Object> - contains the sequence information
     * @throws ConversionException          - on error
     */
    public Hashtable<String, Object> getSequence(final String rnastructure_id, RnastructML root) throws ConversionException {

        // get the correct rnastructure_id  
        RnastructureWithProgramType rnastructure = getRnaStructureByID(rnastructure_id, root);

        SequenceType sequence;
        if (rnastructure != null) {
            sequence = rnastructure.getSequence();
        } else {
            //the given id is not present
            throw new ConversionException("Error! Given rnastructure id not found in document! " + rnastructure_id);
        }

        // get the correct sequence element
        if (sequence == null) {
            // no sequence element in rnastructml element
            return null;
        }

        //extract information from the sequence element
        final Hashtable<String, Object> sequenceHash = new Hashtable<String, Object>();

        // attributes
        if (sequence.isSetSeqID()) {
            sequenceHash.put("seqID", sequence.getSeqID());
        }
        // name element        
        if (sequence.isSetName()) {
            sequenceHash.put("name", sequence.getName());
        }

        // synonyms        
        if (sequence.isSetSynonyms()) {
            final ArrayList<String> synonyms = new ArrayList<String>(sequence.getSynonyms());
            sequenceHash.put("synonyms", synonyms.toArray(new String[synonyms.size()]));
        }

        // description element        
        if (sequence.isSetDescription()) {
            sequenceHash.put("description", sequence.getDescription());
        }

        // the sequences        
        if (sequence.isSetNucleicAcidSequence()) {
            sequenceHash.put("sequence", sequence.getNucleicAcidSequence());
        } else {
            if (sequence.isSetFreeSequence()) {
                sequenceHash.put("sequence", sequence.getFreeSequence());
            }
        }

        // crossrefs
        // TODO

        // comment        
        if (sequence.isSetComment()) {
            sequenceHash.put("comment", sequence.getComment());
        }
        // return the result
        return sequenceHash;
    }

     /**
     * getter method. returns a List<Hashtable<String, Object>> containing the structure information
     * key/values (per Hashtable):
     * structure / String                   - The structure in dotbracket format
     * energy / Double                      - The energy value of the structure
     * probability / Double                 - The probability of the structure
     * shaperef / String                    - Reference to a shape (same as shape id)
     * shapeType / String                   - "structure"
     *              
     * @param rnastructure_id               - id of the rnastructure element
     * @return List<Hashtable<String, Object>  - contains the structure information, 
     *                                        null if there are no such elements
     * @throws ConversionException              - on error
     */
    public List<Hashtable<String, Object>> getStructures(final String rnastructure_id, RnastructML root) throws ConversionException {

        RnastructureWithProgramType rnastructure = getRnaStructureByID(rnastructure_id, root);

        List<StructureType> structurelist;
        if (rnastructure != null) {
            structurelist = getStructureList(rnastructure);
        } else {
            //ID of the requested is not present
            throw new ConversionException("Error! Given rnastructure id not found in document! " + rnastructure_id);
        }

        // Here, we check if there are any structureNodes present
        // If there aren't any, we log a debug message and return null...
        if (structurelist.isEmpty()) {
            return null;
        }
        // ...otherwise, we continue processing this request and return the requested data
        final List<Hashtable<String, Object>> retHashArray = new ArrayList<Hashtable<String, Object>>();

        for (StructureType structure : structurelist) {
            Hashtable<String, Object> structureElement = new Hashtable<String, Object>();
            structureElement.put("shapeType", "structure");
            if (structure.isSetEnergy()) {
                structureElement.put("energy", structure.getEnergy());
            }
            if (structure.isSetProbability()) {
                structureElement.put("probability", structure.getProbability());
            }
            if (structure.isSetShaperef()) {
                structureElement.put("shaperef", structure.getShaperef());
            }
            if (structure.isSetValue()) {
                structureElement.put("structure", structure.getValue());
            }
            retHashArray.add(structureElement);
        }
        // return filled hasharray
        return retHashArray;
    }

    /**
     * Returns a RNAStructure with, if existists, the given id, otherwise null
     * @param rnastructureID unique identification of a RNAStructure
     * @param root root of the RnastructML
     * @return RNAStructure with ID:rnastructureID or null
     */
    private RnastructureWithProgramType getRnaStructureByID(String rnastructureID, RnastructML root) {
        for (RnastructureWithProgramType t_rnastructure : root.getRnastructure()) {
            if (rnastructureID.equals(getRnaStructureID(t_rnastructure))) {
                return t_rnastructure;
            }
        }
        return null;
    }

    private List<StructureType> getStructureList(RnastructureWithProgramType t_rnastructure) {
        List<StructureType> structurelist = new ArrayList<StructureType>();
        for (Object object : t_rnastructure.getShapeOrStructure()) {
            if (object.getClass().equals(StructureType.class)) {
                structurelist.add((StructureType) object);
            }
        }
        return structurelist;

    }

    private void setRnaStructureID(RnastructureWithProgramType t_rnastructure, String id) {
        t_rnastructure.getOtherAttributes().put(new QName("id"), id);
    }

    private boolean isSetRnaStructureID(RnastructureWithProgramType t_rnastructure) {
        if (getRnaStructureID(t_rnastructure) != null) {
            return true;
        }
        return false;
    }
}
