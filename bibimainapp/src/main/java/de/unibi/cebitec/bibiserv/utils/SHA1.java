/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger - jkrueger@cebitec.uni-bielefeld.de
 *
 */
package de.unibi.cebitec.bibiserv.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * A simple utility class that provides some statics methods to create a SHA1 key
 * for given String/ ByteArray.
 *
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class SHA1 {

    public static String SHAsum(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return SHAsum(text, "iso-8859-1");

    }

    public static String SHAsum(String text, String encoding) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return SHAsum(text.getBytes(encoding));
    }

    public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return byteArray2Hex(md.digest(convertme));
    }

    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            System.out.println("usage: \njava " + SHA1.class.getName() + " String [encoding]");

        } else {
            if (args.length == 1) {
                System.out.println(SHAsum(args[0]));
            } else {
                System.out.println(SHAsum(args[0], args[1]));
            }
        }
    }
}
