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
package de.unibi.cebitec.bibiserv.util.convert;

import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQConversionException;
import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQVariants;
import de.unibi.cebitec.bibiserv.util.fastqconvert.converter.FastQConverter;
import java.io.File;

/**
 * For interconverting all fastq variants.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractFastQConverter implements Converter {

    private FastQVariants from = getVariantFrom();
    private FastQVariants to = getVariantTo();
    
    @Override
    public Object convert(Object fromdata) throws ConversionException {
        if (fromdata instanceof java.lang.String) {
            
            FastQConverter converter = new FastQConverter();
            try {
                return converter.convertFastQToFastQ((String) fromdata, from,to);
            } catch (FastQConversionException ex) {
                throw new ConversionException(ex.getLocalizedMessage());
            }
        } else if(fromdata instanceof File) {
            FastQConverter converter = new FastQConverter();
            try {
                return converter.convertFastQToFastQ((File) fromdata, from, to);
            } catch (FastQConversionException ex) {
                throw new ConversionException(ex.getLocalizedMessage());
            }
        }
        throw new ConversionException("Submitted object is no a fastq-string");
    }

    abstract protected FastQVariants getVariantFrom();
    abstract protected FastQVariants getVariantTo();
    
}
