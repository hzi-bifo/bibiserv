/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted  2013 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.util;

import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * StringUtil class
 *
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class StringUtil {

    /**
     * Join a Collection of Strings using a delimiter
     *
     * @param s : Collection <String>
     * @param delimiter : String
     * @return joined String
     */
    public static String join(AbstractCollection<String> s, String delimiter) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        Iterator<String> iter = s.iterator();
        StringBuilder builder = new StringBuilder(iter.next());
        while (iter.hasNext()) {
            builder.append(delimiter).append(iter.next());
        }
        return builder.toString();
    }

    /**
     * Join a collection of Strings using a delimiter
     *
     * @param s : Collection<String>
     * @return joined String
     */
    public static String join(AbstractCollection<String> s) {
        return join(s, "");
    }

    /**
     * Join an Array of Strings
     *
     * @param s : String []
     * @param delimiter : String
     * @return joined String
     */
    public static String join(String s[], String delimiter) {
        if (s == null || s.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s[0]);
        for (int i = 1; i < s.length; ++i) {
            sb.append(delimiter).append(s[i]);
        }
        return sb.toString();
    }

    /**
     * Join an Array of Strings
     *
     * @param s : String []
     * @return joined String
     */
    public static String join(String s[]) {
        return join(s, "");
    }

    /**
     * Generate a random string (with some given constraints) that can be used as (simple) password generator
     * 
     * This code is copied from http://stackoverflow.com/questions/4090021/need-a-secure-password-generator-recommendation.
     * 
     * @param minLength - minimum length of string
     * @param maxLength - maximum length of string
     * @param minLCaseCount - minimum count of lower case characters "abcdefgijkmnopqrstwxyz"
     * @param minUCaseCount - minimum count of upper case character "ABCDEFGHJKLMNPQRSTWXYZ"
     * @param minNumCount - minimum count of number characters "23456789"
     * @param minSpecialCount - minimum count of special character  "*$-+?_&=!%{}/"
     * @return 
     */
    public static String generateRandomString(int minLength, int maxLength, int minLCaseCount, int minUCaseCount, int minNumCount, int minSpecialCount) {
        char[] randomString;

        String LCaseChars = "abcdefgijkmnopqrstwxyz";
        String UCaseChars = "ABCDEFGHJKLMNPQRSTWXYZ";
        String NumericChars = "23456789";
        String SpecialChars = "*$-+?_&=!%{}/";

        Map<String, Integer> charGroupsUsed = new HashMap<>();
        charGroupsUsed.put("lcase", minLCaseCount);
        charGroupsUsed.put("ucase", minUCaseCount);
        charGroupsUsed.put("num", minNumCount);
        charGroupsUsed.put("special", minSpecialCount);

    // Because we cannot use the default randomizer, which is based on the
        // current time (it will produce the same "random" number within a
        // second), we will use a random number generator to seed the
        // randomizer.
    // Use a 4-byte array to fill it with random bytes and convert it then
        // to an integer value.
        byte[] randomBytes = new byte[4];

        // Generate 4 random bytes.
        new Random().nextBytes(randomBytes);

        // Convert 4 bytes into a 32-bit integer value.
        int seed = (randomBytes[0] & 0x7f) << 24
                | randomBytes[1] << 16
                | randomBytes[2] << 8
                | randomBytes[3];

        // Create a randomizer from the seed.
        Random random = new Random(seed);

        // Allocate appropriate memory for the password.
        int randomIndex = -1;
        if (minLength < maxLength) {
            randomIndex = random.nextInt((maxLength - minLength) + 1) + minLength;
            randomString = new char[randomIndex];
        } else {
            randomString = new char[minLength];
        }

        int requiredCharactersLeft = minLCaseCount + minUCaseCount + minNumCount + minSpecialCount;

        // Build the password.
        for (int i = 0; i < randomString.length; i++) {
            String selectableChars = "";

            // if we still have plenty of characters left to acheive our minimum requirements.
            if (requiredCharactersLeft < randomString.length - i) {
                // choose from any group at random
                selectableChars = LCaseChars + UCaseChars + NumericChars + SpecialChars;
            } else // we are out of wiggle room, choose from a random group that still needs to have a minimum required.
            {
                // choose only from a group that we need to satisfy a minimum for.
                for (Map.Entry<String, Integer> charGroup : charGroupsUsed.entrySet()) {
                    if ((int) charGroup.getValue() > 0) {
                        if (null != charGroup.getKey()) switch (charGroup.getKey()) {
                            case "lcase":
                                selectableChars += LCaseChars;
                                break;
                            case "ucase":
                                selectableChars += UCaseChars;
                                break;
                            case "num":
                                selectableChars += NumericChars;
                                break;
                            case "special":
                                selectableChars += SpecialChars;
                                break;
                        }
                    }
                }
            }

            // Now that the string is built, get the next random character.
            randomIndex = random.nextInt((selectableChars.length()) - 1);
            char nextChar = selectableChars.charAt(randomIndex);

            // Tac it onto our password.
            randomString[i] = nextChar;

            // Now figure out where it came from, and decrement the appropriate minimum value.
            if (LCaseChars.indexOf(nextChar) > -1) {
                charGroupsUsed.put("lcase", charGroupsUsed.get("lcase") - 1);
                if (charGroupsUsed.get("lcase") >= 0) {
                    requiredCharactersLeft--;
                }
            } else if (UCaseChars.indexOf(nextChar) > -1) {
                charGroupsUsed.put("ucase", charGroupsUsed.get("ucase") - 1);
                if (charGroupsUsed.get("ucase") >= 0) {
                    requiredCharactersLeft--;
                }
            } else if (NumericChars.indexOf(nextChar) > -1) {
                charGroupsUsed.put("num", charGroupsUsed.get("num") - 1);
                if (charGroupsUsed.get("num") >= 0) {
                    requiredCharactersLeft--;
                }
            } else if (SpecialChars.indexOf(nextChar) > -1) {
                charGroupsUsed.put("special", charGroupsUsed.get("special") - 1);
                if (charGroupsUsed.get("special") >= 0) {
                    requiredCharactersLeft--;
                }
            }
        }
        return new String(randomString);
    }

}
