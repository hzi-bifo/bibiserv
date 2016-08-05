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
package de.unibi.cebitec.bibiserv.sequence.parser.fastq;


import java.util.HashMap;
import java.util.Map;

/**
 * General class for converting the quality strings from fastq to fastq.
 * 
 * For infos on the conversions see here:
 * http://nar.oxfordjournals.org/content/early/2009/12/16/nar.gkp1137.full
 * 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class FastQQualityConverter {

    private Map<Character, Character> solexaToSanger;
    private Map<Character, Character> sangerToSolexa;

    /**
     * Initialise all needed tables in constructor.
     */
    public FastQQualityConverter() {
        this.solexaToSanger = createSolexaToSangerTable();
        this.sangerToSolexa = createSangerToSolexaTable();
    }


    public String convertQuality(String quality, FastQVariants v1,
            FastQVariants v2) {
        switch (v1) {
            case Sanger:
                switch (v2) {
                    case Sanger:
                        return quality;
                    case Illumina:
                        return sangerToIllumina(quality);
                    case Solexa:
                        return sangerToSolexa(quality);
                }
                break;
            case Illumina:
                switch (v2) {
                    case Sanger:
                        return illuminaToSanger(quality);
                    case Illumina:
                        return quality;
                    case Solexa:
                        return illuminaToSolexa(quality);
                }
                break;
            case Solexa:
                switch (v2) {
                    case Sanger:
                        return solexaToSanger(quality);
                    case Illumina:
                        return solexaToIllumina(quality);
                    case Solexa:
                        return quality;
                }
                break;
        }
        return "";
    }

    private String sangerToIllumina(String qualityStr) {
        StringBuilder converted = new StringBuilder();
        int offset = FastQVariants.Illumina.getOffset() - FastQVariants.Sanger.getOffset();

        for (int quality : qualityStr.toCharArray()) {
            if (quality <= FastQVariants.Illumina.getMax() - offset) {
                converted.append((char) (quality + offset));
            } else {
                // write warning caused by lossy conversion
                converted.append((char) (FastQVariants.Illumina.getMax()));
            }
        }
        return converted.toString();
    }

    private String sangerToSolexa(String qualityStr) {
        StringBuilder converted = new StringBuilder();
        for (Character quality : qualityStr.toCharArray()) {
            converted.append(this.sangerToSolexa.get(quality));
        }
        return converted.toString();
    }

    private String illuminaToSolexa(String qualityStr) {
        // first convert to sanger
        String sanger = illuminaToSanger(qualityStr);
        // now convert to solexa
        return sangerToSolexa(sanger);
    }

    private String illuminaToSanger(String qualityStr) {
        StringBuilder converted = new StringBuilder();
        int offset = FastQVariants.Illumina.getOffset() - FastQVariants.Sanger.getOffset();
        for (int quality : qualityStr.toCharArray()) {
            converted.append((char) (quality - offset));
        };
        return converted.toString();
    }

    private String solexaToSanger(String qualityStr) {
        StringBuilder converted = new StringBuilder();
        for (Character quality : qualityStr.toCharArray()) {

            converted.append(this.solexaToSanger.get(quality));
        }
        return converted.toString();
    }

    private String solexaToIllumina(String qualityStr) {
        // first convert to sanger
        String sanger = solexaToSanger(qualityStr);
        // now convert to illumina
        return sangerToIllumina(sanger);
    }

    private Map<Character, Character> createSolexaToSangerTable() {
        Map<Character, Character> converterTable =
                new HashMap<Character, Character>();
        // i = Solexa quality in ascii encoding
        for (int i = FastQVariants.Solexa.getOffset(); i <= FastQVariants.Solexa.getMax(); i++) {
            Character key = new Character((char) i);
            // calculate conversion, 64 is used because of solexa offset 59 + number -5 to -1 
            int converted = FastQVariants.Sanger.getOffset()
                    + (int) (Math.round(10
                    * Math.log10(Math.pow(10, ((i - 64) / 10.0)) + 1)));
            converterTable.put(key, (char) converted);
        }
        return converterTable;
    }

    private Map<Character, Character> createSangerToSolexaTable() {
        Map<Character, Character> converterTable =
                new HashMap<Character, Character>();
        // i = sanger quality in ascii encoding
        for (int i = FastQVariants.Sanger.getOffset(); i <= FastQVariants.Sanger.getMax(); i++) {
            Character key = new Character((char) i);

            int converted;

            // handel special case phred is 0, log10 creates 0 in this case, but should not
            if (i == FastQVariants.Sanger.getOffset()) {
                converted = FastQVariants.Solexa.getOffset();
            } else {

                // calculate conversion, 64 is used because of solexa offset 59 + numbers -5 to -1 
                converted = 64
                        + (int) (Math.round(10
                        * Math.log10(Math.pow(10, ((i - FastQVariants.Sanger.getOffset()) / 10.0))
                        - 1)));
            }
            // cut down too low values
            if (converted < FastQVariants.Solexa.getOffset()) {
                converted = FastQVariants.Solexa.getOffset();
            }
            // cut down too high values
            if (converted > FastQVariants.Sanger.getMax()) {
                converted = FastQVariants.Sanger.getMax();
            }
            converterTable.put(key, (char) converted);
        }
        return converterTable;
    }
}
