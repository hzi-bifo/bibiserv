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
import net.sourceforge.hobit.xsds._20090917.sequenceml.SequenceML;
import net.sourceforge.hobit.xsds._20090917.sequenceml.SequenceType;

/**
 * Converts fastq to squenceML
 * 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class SequenceMLConverter {
    
    public SequenceML convertToSeqenceML(String file) throws FastQConversionException{
        try {
            return convert(new FastQReader(file));
        } catch (FastQConversionRuntimeException ex) {
            throw new FastQConversionException(ex.getLocalizedMessage());
        }
    }
    
    public SequenceML convertToSeqenceML(File file) throws FastQConversionException{
        try {
            return convert(new FastQReader(file));
        } catch (FastQConversionRuntimeException ex) {
           throw new FastQConversionException(ex.getLocalizedMessage());
        }
    }

    private SequenceML convert(FastQReader reader) {
        
        SequenceML sequenceMl = new SequenceML();
        int index = 0;
        
        while (reader.hasNext()) {
            
            FastQRecord record = reader.next();
            SequenceType sequence = new SequenceType();
            
            index++;
            sequence.setSeqID("ID"+index);
            
            sequence.setName(record.getHeader());
            sequence.setNucleicAcidSequence(record.getSequence());
            sequence.getComment().add(record.getQuality());
            
            sequenceMl.getSequence().add(sequence);
        }
        return sequenceMl;
    }
    
}
