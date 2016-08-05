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
 * Contributor(s): Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *
 */
package de.unibi.cebitec.bibiserv.util.convert;

import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CONTENT;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqFormats;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqWriterIface;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.Readseq;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 *  Base class for a couple of converter classes using readseq.
 * 
 * @author  Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *          Richard Madsack (initial release)
 *          
 */
public abstract class SimpleFormatConverter implements SequenceConverter {

    protected String formatdescription;
    protected InputFormat outputformat;
    protected CONTENT content;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setContent(CONTENT content) {
        this.content = content;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object convert(Object fromdata) throws ConversionException {
        String inputdata;
        try {
            inputdata = (String) fromdata;
        } catch (ClassCastException e) {
            throw new ConversionException("Input could not be cast to correct type " + formatdescription+"!", e);
        }
        //check if the data is a non-null object
        if (inputdata == null) {
            throw new ConversionException("Input was 'null'. Cannot convert to "+formatdescription+"!");
        } else {
            try {
                return convertInput(new StringReader(inputdata), outputformat);
            } catch (IOException e) {
                throw new ConversionException("A readSeq-error occured during conversion to "+formatdescription+"!", e);
            }
        }
    }

    /**
     * Calls ReadSeq with given inputsteam and tries to convert to given format. Be sure ur data is convertable to the given format by using the inputIsConvertable(reader) method
     * @param inputReader sequence data
     * @param format format to convert to
     * @return sequence data converted to format
     * @throws IOException a readseq error occured
     */
    private Object convertInput(StringReader inputReader, InputFormat format) throws IOException {
        StringWriter outputWriter = new StringWriter();
        int outid = BioseqFormats.formatFromName(format.toString());
        BioseqWriterIface seqwriter = BioseqFormats.newWriter(outid);
        seqwriter.setOutput(outputWriter);
        seqwriter.writeHeader();
        Readseq rd = new Readseq();
        rd.setInputObject(inputReader);
        if (rd.isKnownFormat() && rd.readInit()) {
            rd.readTo(seqwriter);
            seqwriter.writeTrailer();
            //rd.deleteTempFiles();
            //System.out.println("size: " + rd.tempFiles.size());
            return outputWriter.toString();
        } else {
            throw new IOException();
        }
    }
}
