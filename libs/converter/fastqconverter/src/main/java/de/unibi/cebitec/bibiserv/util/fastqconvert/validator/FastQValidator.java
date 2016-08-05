/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Thomas Gatter
 *
 */
package de.unibi.cebitec.bibiserv.util.fastqconvert.validator;

import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQConversionRuntimeException;
import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQReader;
import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQRecord;
import de.unibi.cebitec.bibiserv.util.fastqconvert.FastQVariants;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides static functions for validating recorts and whole files/strings
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class FastQValidator {

    private static final Pattern sequencePattern = Pattern.compile("^[AGCTURYMKSWHBVDN]+$");
    String validationMessage;
    
    public FastQValidator(){
        this.validationMessage = "";
    }
    
    public boolean validate(String file, FastQVariants variant) {
        try {
            return validateAll(new FastQReader(file), variant);
        } catch (FastQConversionRuntimeException ex) {
            validationMessage = "Error while parsing fastq:"+ ex;
            return false;
        }
    }

    public boolean validate(File file, FastQVariants variant) {
        try {
            return validateAll(new FastQReader(file), variant);
        } catch (FastQConversionRuntimeException ex) {
            validationMessage = "Error while parsing fastq:"+ ex;
            return false;
        }
    }

    private boolean validateAll(FastQReader reader, FastQVariants variant) {
        validationMessage = "";
        while(reader.hasNext()){
            if(!validateRecord(reader.next(), variant)){
                return false;
            }
        }
        return true;
    }

    private boolean validateRecord(FastQRecord record,
            FastQVariants variant) {
        
        // Check sequence and quality lines are same length
        if (record.getSequence().length() != record.getQuality().length()) {
            validationMessage += "Sequence and Quality with different length on id "
                    +record.getHeader();
            return false;
        }
        
        // test if quality scores or valid
        for(int c:record.getQuality().toCharArray()){
            if(c<variant.getOffset() || c>variant.getMax()){
                validationMessage += "Quality contains unvalid character "
                    +record.getHeader();
                return false;
            }
        }
        
        // test if sequence is correct
        Matcher match = sequencePattern.matcher(record.getSequence());
        if(!match.matches()){
            validationMessage += "Sequence contains unvalid character "
                    +record.getHeader();
            return false;
        }
        
        return true;
    }

    public String getValidationMessage() {
        return validationMessage;
    }


}
