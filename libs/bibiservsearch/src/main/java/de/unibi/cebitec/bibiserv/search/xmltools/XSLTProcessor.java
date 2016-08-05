/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Benjamin Paassen, CeBiTec,
 * http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 Benjamin Paassen"
 *
 * Contributor(s): Benjamin Paassen
 *
 */
package de.unibi.cebitec.bibiserv.search.xmltools;

import java.io.InputStream;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * This class is an API for other classes to run xslt scripts.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class XSLTProcessor {

    /**
     * Processes the given source content using an xslt script and writes the
     * output to a given writer.
     *
     * @param script the enum identifier of the xslt script that shall be used.
     * @param source the source DOM the script shall be used on.
     * @param output the output stream the data shall be written to.
     * @throws TransformerException is thrown if the xslt transformation itself
     * throws an exception.
     */
    public static void runXSLTScript(final XSLTScriptEnum script,
            final DOMSource source, final Writer output)
            throws TransformerConfigurationException, TransformerException {
        //wrap the writer in a result object.
        Result xsltOutput = new StreamResult(output);
        //run other method.
        runXSLTScript(script, source, xsltOutput);
    }

    /**
     * Processes the given source content using an xslt script and writes the
     * output to a given xslt result.
     *
     * @param script the enum identifier of the xslt script that shall be used.
     * @param source the source DOM the script shall be used on.
     * @param output the Result object the transformation output shall be
     * written to.
     * @throws TransformerException is thrown if the xslt transformation itself
     * throws an exception.
     */
    public static void runXSLTScript(final XSLTScriptEnum script,
            final DOMSource source, final Result output)
            throws TransformerConfigurationException, TransformerException {
        //wrap the xslt script in a Source object.
        InputStream xsltStream = XSLTProcessor.class.getResourceAsStream(script.getFileName());
        if (xsltStream == null) {
            TransformerException ex =
                    new TransformerException("XSLT script "
                    + script.getFileName() + " could not be found.");
            throw ex;
        }
        StreamSource xsltSource = new StreamSource(xsltStream);
        //Get a TransformerFactory object
        TransformerFactory xformFactory = TransformerFactory.newInstance();
        //Get an XSL Transformer object
        Transformer transformer = xformFactory.newTransformer(xsltSource);
        //do the actual transformation.
        transformer.transform(source, output);
    }
}
