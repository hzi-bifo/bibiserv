/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2012 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * A simple Password Generator class.
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class PasswordGenerator {
 
   public static  String generate(int length){
        Random r = new SecureRandom();
        char [] pwd = new char [length];
        for (int c = 0; c < length; ++c){
            int rnd = r.nextInt(62);
            //System.out.println("["+c+"] - "+ rnd);
            if (rnd <10) { // numbers
                pwd[c] = (char)(48+rnd);
            } else if (rnd >= 10 && rnd < 36) { //uppercase letters
                pwd[c] = (char)(55+rnd);
            } else { //lowercase letters
                pwd[c] = (char)(61+rnd);
            }
        }

        return new String(pwd);
    }

}
