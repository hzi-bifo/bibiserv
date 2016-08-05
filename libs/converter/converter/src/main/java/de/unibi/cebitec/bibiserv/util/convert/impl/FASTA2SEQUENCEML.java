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

import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputReader;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.exception.ParserToolException;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SML_SequenceTypeImpl;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SequenceCollection;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SequenceTypeInterface;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CONTENT;
import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.SequenceConverter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import net.sourceforge.hobit.xsds._20090917.sequenceml.SequenceML;

/**
 * Fasta to SequenceML converter.
 *  
 * @author Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 *         Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class FASTA2SEQUENCEML implements SequenceConverter {

    private PatternType pt = PatternType.isFreeSequence;

    @Override
    public void setContent(CONTENT content) {
        if (content.equals(CONTENT.AA)) {
            pt = PatternType.BT_ambiguousAminoAcidOneLetterSequence;
        } else {
            pt = PatternType.BT_ambiguousNucleotideSequence;
        }
    }

    @Override
    public Object convert(Object fromdata) throws ConversionException {
        if (!(fromdata instanceof String)) {
            throw new ConversionException("'java.lang.String' as input expected!");
        }
        try {
            SequenceML sml = new SequenceML();
            sml = fastaToSequenceML( new StringReader((String) fromdata), pt);
            return sml;
        } catch (Exception e) {
            throw new ConversionException("Cannot convert input (in Fasta format) to SequenceML!", e);
        }
    }

    public SequenceML fastaToSequenceML(final Reader fasta, final PatternType seqType) throws ParserToolException, IOException {

        SequenceML root = new SequenceML();
        
        InputReader input = new InputReader();
        SML_SequenceTypeImpl sequenceTypeImpl = new SML_SequenceTypeImpl(seqType);
        SequenceCollection seqCol;

        seqCol = input.readSequenceFasta(fasta, sequenceTypeImpl);

        for (SequenceTypeInterface sequence : seqCol.getSequences().values()) {
            root.getSequence().add((SML_SequenceTypeImpl) sequence);
        }
        return root;

    }
}
