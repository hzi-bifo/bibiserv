package de.unibi.cebitec.bibiserv.sequence.parser;

import java.lang.reflect.Field;

/**
 * This is a class used to find the first line in a given string and to return
 * the corresponding matched groups.
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class LineSplitter {

    private String group1;
    private String group2;
    private String group3;
    Field field;

    public LineSplitter() {
        group1 = "";
        group2 = "";
        group3 = "";
        try {
            field = String.class.getDeclaredField("value");
            field.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException ex) {
            // can't happen
        }
    }

    /**
     * Searches in the given String for the first line break (\r\n, \n, \r).
     * Fills in the groups as follows: Group1 => everything up to linebreak
     * Group2 => the line break Group3 => everything after the first linebreak
     *
     * @param search the string to search in
     * @return true: a linebreak was found, false: no linebreak in current
     * string
     */
    public boolean find(String search) throws SequenceParserException {

        char[] ca;
        try {
            ca = (char[]) field.get(search);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            // can not happen
            return false;
        }
        for (int j = 0; j < ca.length; j++) {
            try {
                if (ca[j] == '\r') {
                    group1 = search.substring(0, j);

                    // test for \n\r
                    if (j + 1 < ca.length && ca[j + 1] == '\n') {
                        group2 = "\r\n";
                        group3 = search.substring(j + 2);
                    } else {
                        group2 = "\r";
                        group3 = search.substring(j + 1);
                    }
                    return true;
                } else if (ca[j] == '\n') {
                    group1 = search.substring(0, j);
                    group2 = "\n";
                    group3 = search.substring(j + 1);
                    return true;
                }
            } catch (StringIndexOutOfBoundsException e) {
                throw new SequenceParserException("Chars: "+ ca + " Length " + ca.length + " String: " + search + " Length " + search.length() + " Message: "+e);
            }
        }
        return false;


        /* Uncomment for Paranoid Mode */
//        char[] ca = {};
//        int trys = 10;
//        while (ca.length != search.length() && trys>0) { // a bit paranoid mode, because there seem to be runtime effects
//            try {
//                ca = (char[]) field.get(search);
//            } catch (IllegalArgumentException | IllegalAccessException ex) {
//                // can not happen
//                return false;
//            }
//            trys--; // a bit paranoid mode, because there seem to be runtime effects
//        }
//        
//        for (int j = 0; j < ca.length && j< search.length(); j++) {
//            if( ca[j] == '\r'){
//                group1 = search.substring(0, j);
//                
//                // test for \n\r
//                if(j+1<ca.length && j+1<search.length() && ca[j+1]=='\n'){
//                    group2 = "\r\n";
//                    group3 = search.substring(j+2);
//                } else {
//                    group2 = "\r";
//                    group3 = search.substring(j+1); 
//                }
//                return true;
//            } else if(ca[j]=='\n' ) {
//                group1 = search.substring(0, j);
//                group2 = "\n";
//                group3 = search.substring(j+1);
//                return true;
//            }
//        }
//        return false;
    }

    public String getGroup1() {
        return group1;
    }

    public String getGroup2() {
        return group2;
    }

    public String getGroup3() {
        return group3;
    }
}
