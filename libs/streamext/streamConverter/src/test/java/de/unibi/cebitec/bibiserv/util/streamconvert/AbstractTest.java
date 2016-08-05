/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.streamconvert;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.custommonkey.xmlunit.XMLTestCase;
import org.w3c.dom.Document;

/**
 * Provides some Utilities methods
 *
 *
 * @author jkrueger(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractTest extends XMLTestCase {

    /**
     * read file content and return it as String
     *
     * @param fr
     * @return
     * @throws IOException
     */
    protected String readFile(FileReader fr) throws IOException {
        BufferedReader r = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();

    }

    /**
     * read resource content and return as String
     *
     * @param resource
     * @return
     * @throws IOException
     */

    protected String readFromResource(String resource) throws IOException {

        BufferedReader r = new BufferedReader(new InputStreamReader(AbstractTest.class.getResourceAsStream(resource)));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();

    }


    /**
     * skip all whitespace and newline chars
     *
     * @param input
     * @return
     */
    protected String trim(String input) {
        return input.replaceAll("[\\s|\\n]", "");
    }

    
    /**
     * skip CLUSTALW Version String
     * @param input
     * @return 
     */
    protected String trimclustal(String input) {
        return trim(input).replaceAll("CLUSTALW\\(.+?\\)", "CLUSTALW(???)");
    }

    
    /**
     * skip DEFINTION 
     * @param input
     * @return 
     */
    protected String trimgenbank(String input) {
        return trim(input.replaceAll("DEFINITION.*?\\n", ""));
    }

    
    /**
     * skip everything after starting ">" since it is only comment (and not specified)
     * @param input
     * @return 
     */
    protected String trimnbrf(String input) {
        return trim(input.replaceAll(">.*?\\n", ">"));
    }

    
    /**
     * skip everything between "name" and "sequence"
     * @param input
     * @return 
     */
    protected String trimrsf(String input) {
        Pattern p = Pattern.compile("\\nname\\s+(.+?)\\s*\\n", Pattern.MULTILINE);
        Matcher m = p.matcher(input);
        if (m.find()) {
            return trim(input.replaceAll("\\n", " ").replaceAll("name.*?\\ssequence\\s", "name" + m.group(1) + "sequence"));
        }
        return input;
    }

    
    /**
     * skip maybe unspecified informations like length,width,...
     * @param input
     * @return 
     */
    protected String trimmsf(String input) {
        return trim(input.replaceAll("(Len|Weight|Check):\\s+?.+?\\s", "").replaceAll("[~\\.]", "-"));
    }

    
    /**
     * skip descriptions, comment lines
     * @param input
     * @return 
     */
    protected String trimembl(String input) {
        return trim(input.replaceAll("DE .*?\n", "").replaceAll(";.+?;", "").replaceAll("XX.*?\n", ""));
    }

    
    /**
     * replace generated id/shaperef with ???
     * @param input
     * @return 
     */
    protected String trimrml(String input) {
        return (input.replaceAll("id=\".+?\"", "id=\"???\"").replaceAll("shaperef=\".+?\"", "shaperef=\"???\""));
    }

    /**
     * Converts the given JAXBElement to string for testing.
     *
     * @param element the root element to convert to string
     * @param instance for JAXBContext.newInstance
     * @param schemaLocation location for Marshaller.JAXB_SCHEMA_LOCATION
     * @param xsd URL of xsd for Marshaller.JAXB_SCHEMA_LOCATION
     * @return string
     * @throws JAXBException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    protected String jaxbElementToString(Object element, String instance, String schemaLocation, String xsd) throws JAXBException, ParserConfigurationException, IOException {

        JAXBContext jc = JAXBContext.newInstance(instance);

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation + " " + "");
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        dbfactory.setNamespaceAware(true);
        DocumentBuilder DB = dbfactory.newDocumentBuilder();
        Document dom = DB.newDocument();
        m.marshal(element, dom);
        final StringWriter strWtr = new StringWriter();
        final OutputFormat format = new OutputFormat(dom, "UTF-8", true);
        final XMLSerializer output = new XMLSerializer(strWtr, format);
        output.serialize(dom);

        return strWtr.toString();
    }

}
