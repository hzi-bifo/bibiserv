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
 *
 */
package de.unibi.cebitec.bibiserv.util.convert.impl;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputReader;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.exception.ParserToolException;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SequenceCollection;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.AML_SequenceTypeImpl;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SequenceTypeInterface;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CONTENT;
import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.SequenceConverter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.hobit.xsds._20060602.alignmentml.AlignmentML;
import net.sourceforge.hobit.xsds._20060602.alignmentml.AlignmentWithProgramType;
import net.sourceforge.hobit.xsds._20060602.alignmentml.ObjectFactory;

/**
 * Fasta to AlignmentML converter.
 *  
 * @author Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 *         Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class FASTA2ALIGNMENTML implements SequenceConverter{

    
    
    private PatternType pt = PatternType.isFreeSequence;
    
    @Override
    public void setContent(CONTENT content) {
        if (content.equals(CONTENT.AA)) {
            pt = PatternType.BT_gappedAmbiguousAminoAcidOneLetterSequence;
        } else {
            pt = PatternType.BT_gappedAmbiguousNucleotideSequence;
        }
    }

    @Override
    public Object convert(Object fromdata) throws ConversionException {
        if (! (fromdata instanceof String)) {
            throw new ConversionException("'java.lang.String' as input expected!");
        }
        try {
            AlignmentML root;
            root = FastaToAlignment(new StringReader((String)fromdata), pt);   
            
             // convert to JAXBElement and return
            return root;//new JAXBElement(new QName("http://hobit.sourceforge.net/xsds/20060602/alignmentML","alignmentml"),AlignmentWithProgramType.class,root);
        } catch (Exception e) {
            throw new ConversionException("Cannot convert input (in Fasta format) to AlignmentML!",e);
        }
    }
    
    /**
     *  Appends from FASTA to DOM (guessing the sequenceType)
     * @param fasta the fast string
     * @param seqType pattern of the sequence
     * @return the dom
     * @throws ConversionException on error
     */
    public AlignmentML FastaToAlignment(final Reader fasta, final PatternType seqType)
            throws ConversionException {

        
         AlignmentML root = new AlignmentML();
        
        InputReader inputReader = new InputReader();
        SequenceCollection seqCol;
        AML_SequenceTypeImpl sequenceTypeImpl = new AML_SequenceTypeImpl(seqType);

        try {

            seqCol = inputReader.readSequenceFasta(fasta, sequenceTypeImpl);
                    
        } catch (IOException ex) {
            throw new ConversionException("IOException occurred while reading from fasta reader" + System.getProperty("line.separator") + ex.getMessage());
        } catch (ParserToolException ex){
            throw new ConversionException(ex.getMessage());
        }

        //when this point is reached, input data was validated correctly
        //go on with adding information from fasta file to jaxb-Element
        ObjectFactory alignmentMLObjectFactory = new ObjectFactory();
        AlignmentWithProgramType newAlignment = alignmentMLObjectFactory.createAlignmentWithProgramType();
        List<AML_SequenceTypeImpl> amlSeqTypeList = new ArrayList<AML_SequenceTypeImpl>();

        for (SequenceTypeInterface seq : seqCol.getSequences().values()) {
            amlSeqTypeList.add((AML_SequenceTypeImpl) seq);
        }

        newAlignment.getSequence().addAll(amlSeqTypeList);

       
        //add alignment to list of root element
        root.getAlignment().add(newAlignment);
        return root;

    }
        
 
    
    
}
