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
 * Contributor(s): Daniel Hagemeier, Christian Henke
 *
 */
package de.unibi.cebitec.bibiserv.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * From: BiBiServXMLReader 
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de,
 *			Christian Henke <chenke@cebitec.uni-bielefeld.de>
 */
public class XMLDocumentReader {

  private static Logger log = Logger.getLogger(XMLDocumentReader.class);

  public static Document parseDocument(InputStream in) {
	DocumentBuilder builder = null;
	Document document = null;
	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	domFactory.setNamespaceAware(true);
	try {
	  builder = domFactory.newDocumentBuilder();
	} catch (ParserConfigurationException ex) {
	  log.error("ParserConfigurationException in createDoc. Message: " + ex);
	}
	try {
	  //Workaround to prevent UTF-8 malformed sequence exception
	  //Create an UTF-8 String from given InputStream and convert String
	  //back to InputSource.
	  StringBuilder sb = new StringBuilder();
	  String line;
	  try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		while ((line = reader.readLine()) != null) {
		  sb.append(line);
		}
	  } finally {
		in.close();
	  }
	  document = builder.parse(new InputSource(new StringReader(sb.toString())));
	} catch (SAXException ex) {
	  log.error("SAX-Exception in createDoc. Message: " + ex);
	} catch (IOException ex) {
	  log.error("IO-Exception in createDoc. Message: " + ex);
	}
	return document;
  }
}
