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
package de.unibi.cebitec.bibiserv.util.fastqconvert;

import java.io.StringWriter;

/**
 * Represents a fastq record.
 * 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class FastQRecord {

    private static final String NEWLINE = System.getProperty("line.separator");
    
    private String header;
    private String sequence;
    private String qualityHeader;
    private String quality;

    public FastQRecord(final String seqHeaderPrefix, final String seqLine,
            final String qualHeaderPrefix, final String qualLine) {
        if (seqHeaderPrefix.length() > 0) {
            this.header = seqHeaderPrefix;
        }
        if (qualHeaderPrefix.length() > 0) {
            this.qualityHeader = qualHeaderPrefix;
        }
        this.sequence = seqLine;
        this.quality = qualLine;
    }

    public String getHeader() {
        return header;
    }

    public String getSequence() {
        return sequence;
    }

    public String getQualityHeader() {
        return qualityHeader;
    }

    public String getQuality() {
        return quality;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setQualityHeader(String qualityHeader) {
        this.qualityHeader = qualityHeader;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    
    
    
    @Override
    public String toString(){
        StringWriter writer = new StringWriter();
        writer.append(FastQConstants.SEQUENCE_HEADER);
        writer.append(getHeader()).append(NEWLINE);
        writer.append(getSequence()).append(NEWLINE);
        writer.append(FastQConstants.QUALITY_HEADER);
        writer.append(getQualityHeader() == null ? "" : getQualityHeader()).append(NEWLINE);
        writer.append(getQuality()).append(NEWLINE);
        return writer.toString();
    }
}
