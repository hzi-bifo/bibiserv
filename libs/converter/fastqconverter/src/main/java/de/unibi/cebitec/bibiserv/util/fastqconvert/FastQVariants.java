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

/**
 * Basic Information of the fastq types.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public enum FastQVariants {
     
    Sanger(33,126),
    Illumina(64,126),
    Solexa(59,126);
    
    private int offset;
    private int max;
    
    private FastQVariants(int offset, int max){
        this.offset = offset;
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public int getOffset() {
        return offset;
    }
    
}
