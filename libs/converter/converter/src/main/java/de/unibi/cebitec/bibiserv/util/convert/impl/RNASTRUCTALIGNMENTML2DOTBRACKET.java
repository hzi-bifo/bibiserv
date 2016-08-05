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

import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CONTENT;
import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.SequenceConverter;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.w3c.dom.Document;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.RnastructAlignmentML;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.RnastructurealignmentWithProgramType;
import net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.SequenceType;

/**
 * RNAStructAlignmentML to DotBracket converter
 * 
 * @author Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 */
public class RNASTRUCTALIGNMENTML2DOTBRACKET implements SequenceConverter {

    protected CONTENT content;

    @Override
    public void setContent(CONTENT content) {
        this.content = content;
    }

    @Override
    public Object convert(Object fromdata) throws ConversionException {
        try {
            RnastructAlignmentML raml = null;
            // fromdata could be a String ...
            if (fromdata instanceof java.lang.String) {
                JAXBContext jc;
                try {
                    jc = JAXBContext.newInstance("net.sourceforge.hobit.xsds._20060515.rnastructalignmentml");
                    raml = (RnastructAlignmentML) (((JAXBElement)jc.createUnmarshaller().unmarshal(new StringReader((String)fromdata))).getValue());
                } catch (JAXBException e) {
                    throw new ConversionException("Input detected as SequenceML(String), but can't be read!",e);
                } catch (ClassCastException e) {
                    throw new ConversionException("Submitted object is no valide AlignmentML");
                }             
            }else // or a SequenceML jaxb object ...
            if (fromdata instanceof net.sourceforge.hobit.xsds._20060515.rnastructalignmentml.RnastructAlignmentML) {
               try {
                    raml = (RnastructAlignmentML) fromdata;
                } catch (ClassCastException e) {
                    throw new ConversionException("Submitted object is no valide AlignmentML");
                }
            } else // or a SequenceML dom object ...
            if (fromdata instanceof Document) {
                
                JAXBContext jc;
                try {
                    jc = JAXBContext.newInstance("net.sourceforge.hobit.xsds._20060515.rnastructalignmentml");
                    Unmarshaller u = jc.createUnmarshaller();
                    Object o = u.unmarshal((Document) fromdata);

                    if (o instanceof JAXBElement) {
                       raml = (RnastructAlignmentML) (((JAXBElement) o).getValue());
                    } else {
                        raml = (RnastructAlignmentML) o;
                    }
                } catch (JAXBException e) {
                    throw new ConversionException("Input detected as AlignmentML(String), but can't be read!", e);
                } catch (ClassCastException e) {
                    throw new ConversionException("Submitted object is no valide AlignmentML");
                }
                
            } else {
                throw new ConversionException("Input cannot read as SequenceML and converted to Fasta/Pearson as 'java.lang.String'");
            }
            return toDotBracketAlignment(raml);
        } catch (ConversionException e) {
            throw new ConversionException("Input cannot converted from SequenceML to DotBracket!", e);
        }
    }
    
 
    public String toDotBracketAlignment(RnastructAlignmentML root) throws ConversionException {
        return toDotBracketALignment(0, root);

    }


    public String toDotBracketALignment(int number, RnastructAlignmentML root) throws ConversionException {

        List<RnastructurealignmentWithProgramType> list = root.getRnastructurealignment();

        if (list == null || list.isEmpty()) {
            throw new ConversionException("Export to DotBracket is impossible, current RNAStructAlignmentML object contains no data");
        }

        if (list.size() - 1 > number) {
            throw new ConversionException("Export of RNAStructAlignmentML set '"+number+"' is  impossible, current RNAStructAlignmentML object contains not enough sets.");
        }

        StringBuilder sb = new StringBuilder();
        
        RnastructurealignmentWithProgramType rsa = list.get(number);

        for (SequenceType st : rsa.getSequence()) {
            sb.append('>').append(st.getSeqID()).append(' ').append(st.getDescription()).append('\n');
            if (st.isSetAlignedFreeSequence()) {
                sb.append(st.getAlignedFreeSequence()).append('\n');
            } else if (st.isSetAlignedNucleicAcidSequence()) {
                sb.append(st.getAlignedNucleicAcidSequence().getValue()).append('\n');
            }
            if (st.isSetStructure()) { 
                sb.append(st.getStructure().getValue());
                if (st.getStructure().isSetEnergy()) {
                    Locale.setDefault(Locale.US);
                    DecimalFormat df = new DecimalFormat(".00");
                   
                   
                    sb.append(" (").append(df.format(st.getStructure().getEnergy())).append(')');
                }
                sb.append('\n');
            }
        }
        return sb.toString();
    }
     
}
