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
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CONTENT;
import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.SequenceConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.AlignedNucleotideSequence;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.RnastructAlignmentML;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.RnastructurealignmentWithProgramType;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.SequenceType;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.StructureType;

/**
 * DotBracket to RNAStructAlignmentML converter.
 *  
 * @author Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 *         Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class DOTBRACKET2RNASTRUCTALIGNMENTML implements SequenceConverter {

    private CONTENT content;
    private Pattern structure_with_energy = Pattern.compile(PatternType.BT_gappedRnaSecondaryStructureSequenceWithEnergy.getPattern());
    private Pattern sequence = Pattern.compile(PatternType.BT_gappedAmbiguousRnaSequence.getPattern());

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
            RnastructAlignmentML root;
            root = fromDotBracketAlignment(new StringReader((String) fromdata));
            
            // convert to JAXBElement and return
            return new JAXBElement(new QName("http://hobit.sourceforge.net/xsds/20060515/rnastructAlignmentML","rnastructAlignmentML"),RnastructurealignmentWithProgramType.class,root);
            
        } catch (Exception e) {
            throw new ConversionException("Cannot convert input (in Fasta format) to RNAStructAlignmentML!", e);
        }
    }

    /**
     * Method that imports from dotbracket to RNAstructAlignment format.
     * 
     * @param dotbracketFasta
     * @throws ConversionException
     * @throws IOException 
     */
    public RnastructAlignmentML fromDotBracketAlignment(Reader dotbracketFasta) throws ConversionException, IOException {
        // parse dot bracket
        LineNumberReader br = new LineNumberReader(new BufferedReader(dotbracketFasta));
        String line = null;
        String id = null;
        String description = null;
        String sequencedata = null;
        String structuredata = null;
        String energy = null;
        
        RnastructAlignmentML root  = new RnastructAlignmentML();

        RnastructurealignmentWithProgramType rnastructurealignment = new RnastructurealignmentWithProgramType();
        rnastructurealignment.setComment("Generated by 'appendFromDotBracketAlignment'!");
        List<SequenceType> rnastructurealignmentlist = rnastructurealignment.getSequence();

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
                                id = IDGenerator.generateId();
                            }
                            // get description if given
                            if (idDesc.length > 1) {
                                description = idDesc[1];
                            } else {
                                description = null;
                            }
                            mode = 1;
                        } else {
                            throw new ConversionException("ID (and description) expected!");
                        }
                        break;
                    case 1:
                        Matcher seqmatch = sequence.matcher(line);
                        mode = 2;
                        if (seqmatch.matches()) {
                            sequencedata = line;
                            break;
                        }
                    case 2:
                        Matcher structmatch = structure_with_energy.matcher(line);
                        if (structmatch.matches()) {
                            structuredata = structmatch.group(1);
                            energy = structmatch.group(2);
                            mode = 3;
                        } else {
                            throw new ConversionException("Either Sequencedata or Structuredata expected!");
                        }

                    case 3:
                        rnastructurealignmentlist.add(createSequenceTyp(id, description, energy, sequencedata, structuredata));
                        mode = 0;
                        energy = null;
                        structuredata = null;
                        sequencedata = null;
                        break;
                }
            }
        }
        br.close();

        root.getRnastructurealignment().add(rnastructurealignment);
        return root;
    }
    
    
    private SequenceType createSequenceTyp(String id, String description, String energy, String sequencedata, String structuredata) throws ConversionException {
        SequenceType newSt = new SequenceType();
        newSt.setSeqID(id);
        newSt.setDescription(description);

        // -- sequencedata and structuredata must have equal size
        if (sequencedata != null && sequencedata.length() != structuredata.length()) {
            throw new ConversionException("DotBracket data with id " + id + " contains sequence data (length :" + sequencedata.length() + ") and structure data (length :" + structuredata.length() + ") with different length!");
        }

        // -- 
        if (sequencedata != null) {
            AlignedNucleotideSequence ans = new AlignedNucleotideSequence();
            ans.setValue(sequencedata);
            newSt.setAlignedNucleicAcidSequence(ans);
        }
        // --
        StructureType stt = new StructureType();
        stt.setValue(structuredata);
        if (energy != null) {
            try {
                stt.setEnergy(Double.parseDouble(energy));
            } catch (NumberFormatException e) {
                throw new ConversionException(e.getMessage());
            }
        }
        newSt.setStructure(stt);

        return newSt;
    }
}
