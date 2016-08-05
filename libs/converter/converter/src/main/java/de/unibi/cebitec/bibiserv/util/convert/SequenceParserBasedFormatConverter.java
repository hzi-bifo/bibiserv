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
package de.unibi.cebitec.bibiserv.util.convert;

import de.unibi.cebitec.bibiserv.sequenceparser.Parser;
import de.unibi.cebitec.bibiserv.sequenceparser.ParserFactory;
import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserInputFormat;
import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserOutputFormat;
import de.unibi.cebitec.bibiserv.sequenceparser.parser.exception.ParserException;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator.CONTENT;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat;
import java.io.IOException;
import java.io.StringReader;

/**
 * Abstract GdeParserBasedFormat Converter
 * 
 * @author Madis Rumming - mrumming(aet)cebitec.uni-bielefeld.de (initial implementation)
 *         Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 *         Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public abstract class SequenceParserBasedFormatConverter implements SequenceConverter {

    
    protected ParserInputFormat parserinputformat;
    protected String inputformatdescription;
    
    protected ParserOutputFormat parseroutputformat;
    protected String outputformatdescription;
    
    protected InputFormat outputformat;
    
    private CONTENT content;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convert(Object fromdata) throws ConversionException {
        String inputdata;
        try {
            inputdata = (String) fromdata;
        } catch (ClassCastException ex) {
            throw new ConversionException("Input could not be cast to correct type " +inputformatdescription, ex);
        }
        //check if the data is a non-null object
        if (inputdata == null) {
            throw new ConversionException("Input was 'null'. Cannot convert nothing!");
        } else {
            try {
                
                    return convertInput(new StringReader(inputdata), outputformat);
               

            } catch (ParserException e) {
                throw new ConversionException("A GdeParser-error occured during conversion to "+outputformatdescription, e);
            }
        }
    }
    
     /**
     * {@inheritDoc}
     */
    @Override
    public void setContent(CONTENT content) {
        this.content = content;
    }

    
    
    /**
     * Calls ReadSeq with given inputstream and tries to convert to given format. Be sure your data is convertable to the given format by using the inputIsConvertable(reader) method
     * @param inputReader sequence data
     * @param format format to convert to
     * @return sequence data converted to format
     * @throws IOException a readseq error occured
     */
    private Object convertInput(StringReader inputReader, InputFormat outputFormat) throws ParserException {
        Parser gdeParser;
        if (outputFormat == InputFormat.Fasta) {
            gdeParser = ParserFactory.createParser(parserinputformat, inputReader);
            gdeParser.setOutputFormat(ParserOutputFormat.Fasta);
        } else {
            switch (outputFormat) {
                case Dialign:
                case GDE_Flat:
                case GDE_Tagged:
                case IG:
                case MSF_PileUp:
                case MSF_Plain:
                case MSF_Rich:
                case CODATA:
                case Phylip:
                case RSF:
                    gdeParser = ParserFactory.createToFastaParser(parseroutputformat, inputReader);
                    break;
                default:
                    throw new ParserException("Outputformat " + outputFormat + " is not recognized by ParserFactory(). Use readseq instead.");
            }
        }

        StringBuilder sBuilder = new StringBuilder();
        if (gdeParser.isKnownFormat()) {
            while (gdeParser.readNext()) {
                sBuilder.append((String) gdeParser.nextElement()).append("\n");
            }
        } else {
            throw new ParserException();
        }
        return sBuilder.toString();
    }

}
