/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 *
 * @author rmadsack
 */
public class IDGenerator {

    /**
     * String of chars for generating unique random part of bibiid
     */
    private static final String ID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Generates a unique ID - for usage in XML documents as ID to IDREF pair
     *
     * @return String unique ID String
     */
    public static String generateId() {
        //prepare random String for unique IDs
        final Random rand = new Random();
        StringBuffer myRnd = new StringBuffer();
        myRnd.append("ID");
        for (int n = 0; n < 10; n++) {//length is 10 now ... ;)
            myRnd = myRnd.append(ID_CHARS.charAt(rand.nextInt(ID_CHARS.length())));
        }
        
        // use System Time with milliseconds to make more unique
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmssSSS");
        sdf.format(cal.getTime());
        myRnd.append(sdf.format(cal.getTime()));
        
        return myRnd.toString();
    }
}
