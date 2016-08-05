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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */
package de.unibi.cebitec.bibiserv.server.manager.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class for some zip operation. 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class ZIPTool {

    /**
     * Read file content into a byte array buffer
     *
     * @param file - the file to be read.
     * @return byte [] of input file length, containing the file content
     * @throws IOException in the case the file size > Integer.MAX_VALUE or the file doesn' exit or the file can't be read.
     */
    public static byte[] filetobytearray(File file) throws IOException {
        // check the file size
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File size is too large !");
        }
        // create a byte array to hold the file data
        byte[] buf = new byte[(int) length];

        // read in bytes from file
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        int offset = 0;
        int numread = 0;
        while (offset < buf.length && (numread = in.read(buf, offset, buf.length - offset)) >= 0) {
            offset += numread;
        }

        in.close();
        return buf;
    }


    /**
     * Unzip a zipped resource to given path
     *
     * @param buf - byte [] buffer containing zipped resource
     * @param path - target path
     * @throws IOException - in the case the resouce can not be unzip ...
     */
    public static void unzipbytearray(byte[] buf, File path) throws IOException {
        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(buf));
        ZipEntry zipentry;
        final int BUFFERSIZE = 4096;
        while ((zipentry = zin.getNextEntry()) != null) {
            File tmp = new File(path, zipentry.getName());
            if (zipentry.isDirectory()) {
                tmp.mkdirs();
            } else {
                byte[] BUFFER = new byte[BUFFERSIZE];
                int count;
                BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(tmp), BUFFERSIZE);
                while ((count = zin.read(BUFFER, 0, BUFFERSIZE)) != -1) {
                    os.write(BUFFER, 0, count);
                }
                os.flush();
                os.close();
            }
        }
        zin.close();
    }

    /**
     * Extract (unzip) a specified entry from  zipped buffer.
     *
     * @param buf - The zipped byte[] buffer
     * @param entry - Name of entry to be extract
     * @return Returns a byte [] buffer of the unzipped entry.
     * @throws Exception in a case the entry was not found.
     */
    public static byte [] extractNamedEntryfromZippedBuffer(byte[] buf, String entry) throws Exception {
        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(buf));
        ZipEntry zipentry;

        byte[] tmp = null;

        while ((zipentry = zin.getNextEntry()) != null) {
            if (zipentry.getName().equals(entry)) {
                tmp = new byte[(int) zipentry.getSize()];
                int readbytes = 0;
                while (tmp.length > readbytes) {
                    readbytes  += zin.read(tmp, readbytes, tmp.length-readbytes);
                }
                break;
            }
        }
        zin.close();
        // if zipstream does not contain a matching entry, the tmp byte [] is null.
        if (tmp == null) {
            throw new Exception("Zipped Buffer does not contain an entry '"+entry+"'!");
        }
       
        return tmp;
    }

    /**
     * Generate a md5sum of given buffer.
     *
     * @param buf - byte [] buffer
     * @return Returns the md5 sum as byte [] for the given buffer
     * @throws Exception
     */
    public static byte [] md5sum(byte [] buf) throws Exception{
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(buf);
        return digest.digest();
    }
}
