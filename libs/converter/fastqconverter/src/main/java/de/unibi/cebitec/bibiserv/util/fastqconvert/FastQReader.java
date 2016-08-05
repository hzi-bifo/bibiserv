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
/*
 * The MIT License
 *
 * Copyright (c) 2009 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.unibi.cebitec.bibiserv.util.fastqconvert;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.*;

/**
 * Reads a fastq file.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class FastQReader implements Iterator<FastQRecord>, Iterable<FastQRecord>,
        Closeable {

    final private BufferedReader reader;
    private FastQRecord nextRecord;
    private int line = 1;

    public FastQReader(File file) {
        try {
            reader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(
                    file)));
            nextRecord = readNextRecord();
        } catch (IOException ioe) {
            throw new FastQConversionRuntimeException(ioe.getMessage());
        }
    }

    public FastQReader(String input) {
        reader = new BufferedReader(new StringReader(input));
        nextRecord = readNextRecord();
    }

    private FastQRecord readNextRecord() {
        try {

            // Read sequence header
            final String seqHeader = reader.readLine();
            if (seqHeader == null) {
                return null;
            }
            if (isBlank(seqHeader)) {
                throw new FastQConversionRuntimeException(error(
                        "Missing sequence header"));
            }
            if (!seqHeader.startsWith(FastQConstants.SEQUENCE_HEADER)) {
                throw new FastQConversionRuntimeException(error("Sequence header must start with "
                        + FastQConstants.SEQUENCE_HEADER + ": " + seqHeader));
            }

            // Read sequence line
            final String seqLine = reader.readLine();
            checkLine(seqLine, "sequence line");

            // Read quality header
            final String qualHeader = reader.readLine();
            checkLine(qualHeader, "quality header");
            if (!qualHeader.startsWith(FastQConstants.QUALITY_HEADER)) {
                throw new FastQConversionRuntimeException(error("Quality header must start with "
                        + FastQConstants.QUALITY_HEADER + ": " + qualHeader));
            }

            // Read quality line
            final String qualLine = reader.readLine();
            checkLine(qualLine, "quality line");

            final FastQRecord frec = new FastQRecord(seqHeader.substring(1,
                    seqHeader.length()), seqLine,
                    qualHeader.substring(1, qualHeader.length()), qualLine);
            line += 4;
            return frec;

        } catch (IOException e) {
            throw new FastQConversionRuntimeException(String.format(
                    "Error reading file"), e);
        }
    }

    public boolean hasNext() {
        return nextRecord != null;
    }

    public FastQRecord next() {
        if (!hasNext()) {
            throw new NoSuchElementException("next() called when !hasNext()");
        }
        final FastQRecord rec = nextRecord;
        nextRecord = readNextRecord();
        return rec;
    }

    public void remove() {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    public Iterator<FastQRecord> iterator() {
        return this;
    }

    public int getLineNumber() {
        return line;
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new FastQConversionRuntimeException("IO problem in file ", e);
        }
    }

    private void checkLine(final String line, final String kind) {
        if (line == null) {
            throw new FastQConversionRuntimeException(error("File is too short - missing "
                    + kind + " line"));
        }
        if (isBlank(line)) {
            throw new FastQConversionRuntimeException(error("Missing " + kind));
        }
    }

    private String error(final String msg) {
        return msg + " at line " + line;
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
