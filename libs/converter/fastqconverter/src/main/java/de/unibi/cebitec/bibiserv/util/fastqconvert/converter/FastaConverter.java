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
package de.unibi.cebitec.bibiserv.util.fastqconvert.converter;

import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQConversionException;
import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQConversionRuntimeException;
import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQReader;
import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQRecord;
import java.io.File;

/**
 * Converts fastq to fasta (with loss of information)
 * 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class FastaConverter {
    
    private static final String NEWLINE = System.getProperty("line.separator");
    
    public String convertToFasta(String file) throws FastQConversionException{
        try {
            return convert(new FastQReader(file));
        } catch (FastQConversionRuntimeException ex) {
            throw new FastQConversionException(ex.getLocalizedMessage());
        }
    }
    
    public String convertToFasta(File file) throws FastQConversionException{
        try {
            return convert(new FastQReader(file));
        } catch (FastQConversionRuntimeException ex) {
           throw new FastQConversionException(ex.getLocalizedMessage());
        }
    }

    private String convert(FastQReader reader) {
        
        StringBuilder converted = new StringBuilder();
        while (reader.hasNext()) {
            converted.append(convertRecord(reader.next()));
        }
        return converted.toString();
    }
    
    private String convertRecord(FastQRecord record){
        
        StringBuilder fasta = new StringBuilder();
        fasta.append(">").append(record.getHeader()).append(NEWLINE);
        fasta.append(record.getSequence()).append(NEWLINE);
        return fasta.toString();
    }
    
}
