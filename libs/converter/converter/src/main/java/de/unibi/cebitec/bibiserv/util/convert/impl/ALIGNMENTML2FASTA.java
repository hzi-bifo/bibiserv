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
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import net.sourceforge.hobit.xsds._20060602.alignmentml.AlignmentML;
import net.sourceforge.hobit.xsds._20060602.alignmentml.AlignmentWithProgramType;
import net.sourceforge.hobit.xsds._20060602.alignmentml.SequenceType;
import org.w3c.dom.Document;

/**
 * AlignmentML to Fasta converter
 * 
 * @author Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ALIGNMENTML2FASTA implements SequenceConverter {

    protected CONTENT content;

    @Override
    public void setContent(CONTENT content) {
        this.content = content;
    }

    @Override
    public Object convert(Object fromdata) throws ConversionException {
        try {
            AlignmentML aml = null;
            // fromdata could be a String ...
            if (fromdata instanceof java.lang.String) {
                JAXBContext jc;
                try {
                    jc = JAXBContext.newInstance(net.sourceforge.hobit.xsds._20060602.alignmentml.AlignmentML.class);
                    aml = (AlignmentML) (jc.createUnmarshaller().unmarshal(new StringReader((String) fromdata)));
                } catch (JAXBException e) {
                    throw new ConversionException("Input detected as AlignmentML(String), but can't be read!", e);
                } catch (ClassCastException e) {
                    throw new ConversionException("Submitted object is no valide AlignmentML");
                }
            } else // or a SequenceML jaxb object ...
            if (fromdata instanceof AlignmentML) {
                try {
                    aml = (AlignmentML) fromdata;
                } catch (ClassCastException e) {
                    throw new ConversionException("Submitted object is no valide AlignmentML");
                }
            } else
            if (fromdata instanceof Document) {

                JAXBContext jc;
                try {
                    jc = JAXBContext.newInstance(net.sourceforge.hobit.xsds._20060602.alignmentml.AlignmentML.class);
                    Unmarshaller u = jc.createUnmarshaller();
                    Object o = u.unmarshal((Document) fromdata);

                    if (o instanceof JAXBElement) {
                       aml = (AlignmentML) (((JAXBElement) o).getValue());
                    } else {
                        aml = (AlignmentML) o;
                    }
                } catch (JAXBException e) {
                    throw new ConversionException("Input detected as AlignmentML(Document), but can't be read!", e);
                } catch (ClassCastException e) {
                    throw new ConversionException("Submitted object is no valide AlignmentML");
                }

            } else {
                throw new ConversionException("Input cannot read as AlignmentML and converted to Fasta/Pearson as 'java.lang.String'");
            }
            return toFasta(aml);

        } catch (ConversionException e) {
            throw new ConversionException("Input cannot converted from AlignmentML to Fasta/Pearson!", e);
        }
    }

    /**
     * Converts the 1st alignment to FASTA
     *
     * @param root root of the dom tree
     * @return String sequences as FASTA
     * @exception BioDOMException
     *                if error on DOM
     */
    public String toFasta(AlignmentML root) throws ConversionException {
        StringBuffer fasta = new StringBuffer();
        List<AlignmentWithProgramType> alignments = root.getAlignment();

        for (int i = 0; i < alignments.size(); i++) {
            fasta.append(toFasta(i, root));
        }

        return fasta.toString();
    }

 
    /**
     * Converts a by-number selected alignment to FASTA
     *
     * @param root root of the dom tree
     * @param no
     *            int no of alignment
     * @return String sequences as FASTA
     * @exception BioDOMException
     *                if error on DOM
     */
    public String toFasta(final int no, AlignmentML root) throws ConversionException {

        // parse to fasta
        StringBuffer fasta = new StringBuffer();
        AlignmentWithProgramType alignment = root.getAlignment().get(no);
        List<SequenceType> sequences = alignment.getSequence();

        char[] tmpfasta;
        for (SequenceType seq : sequences) {
            // adding sequence id
            fasta = fasta.append(">").append(seq.getSeqID());

            // adding description
            if (seq.isSetDescription()) {
                fasta = fasta.append(" ").append(seq.getDescription());
            }
            // adding newline
            fasta = fasta.append(System.getProperty("line.separator"));

            // adding sequence
            if (seq.isSetAlignedNucleicAcidSequence()) {
                tmpfasta = seq.getAlignedNucleicAcidSequence().getValue().toCharArray();

            } else if (seq.isSetAlignedAminoAcidSequence()) {
                tmpfasta = seq.getAlignedAminoAcidSequence().getValue().toCharArray();

            } else {
                tmpfasta = seq.getAlignedFreeSequence().toCharArray();

            }

            /* TODO: Add a more efficient method for line-wrapping the sequences (than char-by-char)!! */
            //define line length
            final int wrapwidth = 72;
            //rmadsack: is this a more efficent method for line-wrapping?
            int maxIterations = tmpfasta.length / wrapwidth;
            int remainder = tmpfasta.length % wrapwidth;

            for (int splitPart = 0; splitPart < maxIterations; splitPart++) {
                char[] tmpArray = new char[wrapwidth];
                System.arraycopy(tmpfasta, (splitPart * wrapwidth), tmpArray, 0, wrapwidth);
                fasta.append(new String(tmpArray) + LINEBREAK);
            }

            if (remainder > 0) {
                char[] tmpArray = new char[remainder];
                System.arraycopy(tmpfasta, (maxIterations * wrapwidth), tmpArray, 0, remainder);
                fasta.append(new String(tmpArray) + LINEBREAK);
            }

        }
        fasta.append(LINEBREAK);
        return fasta.toString();
    }
}
