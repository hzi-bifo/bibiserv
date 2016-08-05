/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de "
 *
 * Contributor(s): Thomas Gatter
 *
 */
package de.unibi.cebitec.bibiserv.util.convert.impl;

import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.Converter;
import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQConversionException;
import de.unibi.cebitec.bibiserv.util.fastqconvert.converter.FastaConverter;
import java.io.File;

/**
 * Convert all fastq variants to fasta.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class FASTQALL2FASTA implements Converter{

    @Override
    public Object convert(Object fromdata) throws ConversionException {
        if (fromdata instanceof java.lang.String) {
            
            FastaConverter converter = new FastaConverter();
            try {
                return converter.convertToFasta((String) fromdata);
            } catch (FastQConversionException ex) {
                throw new ConversionException(ex.getLocalizedMessage());
            }
        } else if(fromdata instanceof File) {
            FastaConverter converter = new FastaConverter();
            try {
                return converter.convertToFasta((File) fromdata);
            } catch (FastQConversionException ex) {
                throw new ConversionException(ex.getLocalizedMessage());
            }
        }
        throw new ConversionException("Submitted object is no a fastq-string");
    }
    
}
