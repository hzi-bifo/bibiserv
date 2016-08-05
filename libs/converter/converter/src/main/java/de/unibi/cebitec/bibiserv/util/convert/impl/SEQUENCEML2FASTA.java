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
import net.sourceforge.hobit.xsds._20090917.sequenceml.SequenceML;
import net.sourceforge.hobit.xsds._20090917.sequenceml.SequenceType;
import org.w3c.dom.Document;

/**
 * SequenceML to Fasta converter
 * 
 * @author Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 *         Thomas Gatter -  tgatter(aet)cebitec.uni-bielefeld.de
 */
public class SEQUENCEML2FASTA implements SequenceConverter {

    protected CONTENT content;

    @Override
    public void setContent(CONTENT content) {
        this.content = content;
    }

    @Override
    public Object convert(Object fromdata) throws ConversionException {

        SequenceML sml = null;
        // fromdata could be a String ...
        if (fromdata instanceof java.lang.String) {
            sml = new SequenceML();
            JAXBContext jc;
            try {
                jc = JAXBContext.newInstance(SequenceML.class);
                sml = (SequenceML) (jc.createUnmarshaller().unmarshal(new StringReader((String) fromdata)));
            } catch (JAXBException e) {
                throw new ConversionException("Input detected as SequenceML(String), but can't be read!", e);
            } catch (ClassCastException e) {
                throw new ConversionException("Submitted object is no valide SequenceML");
            }

        } else // or a SequenceML jaxb object ...
        if (fromdata instanceof SequenceML) {
            try {
                sml = (SequenceML) ((net.sourceforge.hobit.xsds._20090917.sequenceml.SequenceML) fromdata);
            } catch (ClassCastException e) {
                throw new ConversionException("Submitted object is no valide SequenceML");
            }
        } else // or a SequenceML dom object ...
        if (fromdata instanceof Document) {
            JAXBContext jc;
            try {
                jc = JAXBContext.newInstance(net.sourceforge.hobit.xsds._20060602.alignmentml.AlignmentML.class);
                Unmarshaller u = jc.createUnmarshaller();
                Object o = u.unmarshal((Document) fromdata);

                if (o instanceof JAXBElement) {
                    sml = (SequenceML) (((JAXBElement) o).getValue());
                } else {
                    sml = (SequenceML) o;
                }
            } catch (JAXBException e) {
                throw new ConversionException("Input detected as SeqeunceML(Document), but can't be read!", e);
            } catch (ClassCastException e) {
                throw new ConversionException("Submitted object is no valide SeqeunceML");
            }
        } else {
            throw new ConversionException("Input cannot read as SequenceML and converted to Fasta/Pearson as 'java.lang.String'");
        }
        return toFasta(72, sml);


    }

    public String toFasta(final int wrapwidth, SequenceML root) throws ConversionException {

        StringBuffer fasta = new StringBuffer();
        char[] tmpfasta;
        // log.debug("trying to get all Elements 'sequence'");

        final List<SequenceType> sequences = root.getSequence();
        for (SequenceType t_SequenceType : sequences) {
            // every sequence must have an ID
            fasta = fasta.append(">" + t_SequenceType.getSeqID().replaceAll(" ", ""));
            // add description if defined
            if (t_SequenceType.getDescription() != null) {
                final String description = t_SequenceType.getDescription();
                fasta = fasta.append(" " + description);
            }
            fasta = fasta.append(LINEBREAK);
            if (t_SequenceType.isSetAminoAcidSequence()) {
                tmpfasta = t_SequenceType.getAminoAcidSequence().toCharArray();
            } else if (t_SequenceType.isSetNucleicAcidSequence()) {
                tmpfasta = t_SequenceType.getNucleicAcidSequence().toCharArray();
            } else {
                tmpfasta = t_SequenceType.getFreeSequence().toCharArray();
            }
            //define line length
            if (wrapwidth == -1) {
                fasta.append(tmpfasta);
            } else if (wrapwidth > 0) {

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
            } else {
                throw new ConversionException("SEQUNCEML2FASTA.toFasta(int wrapwidth) : wrapwidth must be -1 or > 0");
            }
        }
        fasta = fasta.append(LINEBREAK);

        return (fasta.toString());
    }
}
