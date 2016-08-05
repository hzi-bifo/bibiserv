/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.cebitec.bibiserv.util.convert.impl;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.InputFormat;
import de.unibi.cebitec.bibiserv.util.convert.SimpleFormatConverter;


/**
 * NBRF 2 FASTA converter
 * 
 * @author  Madis Rumming (initial release)
 *          Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 */
public class NBRF2FASTA extends SimpleFormatConverter {
    {
        formatdescription = "Fasta/Pearson as 'java.lang.String'";
        outputformat = InputFormat.Fasta;
    }

   

}
