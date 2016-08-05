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

package de.unibi.cebitec.bibiserv.util.streamconvert.impl;

import de.unibi.cebitec.bibiserv.sequence.parser.ConversionParser;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import de.unibi.cebitec.bibiserv.sequence.parser.fasta.FastaToAllConverter;
import de.unibi.cebitec.bibiserv.sequence.parser.fasta.FastaToAllConverterCallback;
import de.unibi.cebitec.bibiserv.util.streamconvert.AbstractStreamParserConverter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Fasta 2 NBRF converter
 * 
 * @author  Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class FASTA2NBRF extends AbstractStreamParserConverter {

    FastaToAllConverterCallback callback = new FastaToAllConverterCallback() {

        @Override
        public void fileBegin(BufferedWriter writer) throws SequenceParserException {
            // nothing
        }

        @Override
        public void fileEnd(BufferedWriter writer) throws SequenceParserException {
            //nothing
        }

        @Override
        public void idFound(BufferedWriter writer, String id) throws SequenceParserException {
            write(writer,">XX;");
            write(writer, id);
            writeNewLine(writer);
        }

        @Override
        public void headerExtraInfoFound(BufferedWriter writer, String info) throws SequenceParserException {
            write(writer, info);
            writeNewLine(writer);
        }

        @Override
        public void beginSequence(BufferedWriter writer) throws SequenceParserException {
            // nothing
        }

        @Override
        public void endSequence(BufferedWriter writer) throws SequenceParserException {
            write(writer,"*");
            writeNewLine(writer);
        }
    };
    
    @Override
    protected ConversionParser getParser(BufferedReader input, BufferedWriter output) {
        return new FastaToAllConverter(input,output, callback);
    }
}
