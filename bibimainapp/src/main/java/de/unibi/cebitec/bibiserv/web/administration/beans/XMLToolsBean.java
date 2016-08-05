/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s):
 *
 */
package de.unibi.cebitec.bibiserv.web.administration.beans;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Bean class that provides getter methods for often used  preconfigured
 * documentbuilder and schema.
 * 
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class XMLToolsBean implements InitializingBean, ErrorHandler{
    
    private static Logger log = Logger.getLogger(XMLToolsBean.class);
    
    private final String URL = "http://bibiserv.cebitec.uni-bielefeld.de/xsd/bibiserv2/BiBiServAbstraction.xsd";
    private final String URI = "bibiserv:de.unibi.techfak.bibiserv.cms";
    
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    
    
    private SchemaFactory sf;
    private Schema s;
    
    
    public DocumentBuilder getBiBiServXSDDocumentBuilder(){
        return db;
    }
    
    
    public Schema getBiBiServXSDSchema(){
        return s;
    }
    
    public String getBiBiServURL(){
        return URL;
    }
    
    public String getBiBiServURI(){
        return URI;
    }
    
    public ErrorHandler getErrorHandler(){
        return this;
    }
    

    @Override
    public void afterPropertiesSet() throws Exception {
        URL url = new URL(URL);
        
        sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);       
        try {
            s = sf.newSchema(url);
            
            
        } catch (SAXException e){
            s = null;
            log.warn("Can't parse '"+url+"' as schema! Continue without schema validation!",e);
        }
        
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
       
        if (s != null){
            dbf.setSchema(s);
        }
        db = dbf.newDocumentBuilder();   
        db.setErrorHandler(this);
       
        log.info("XMLToolsBean initialized ...");
    }
    
    
     /** 
     * Method creating a document validating against BiBiServAbstraction Schema from a string 
     * @param in 
     * @return xml document
     */
    public Document createBiBiServDocFromString(String in) throws IOException, ParserConfigurationException, SAXException {     
        return db.parse(new InputSource(new StringReader(in)));  
    }
    /** 
     * Method creating a document validating against BiBiServAbstraction Schema from an inputstream 
     * @param is 
     * @return xml document
     */
    public Document createBiBiServDocFromIs(InputStream is) throws IOException, ParserConfigurationException, SAXException {     
        return db.parse(is);  
    }
    

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        throw new SAXException(exception); 
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        throw new SAXException(exception); 
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        throw new SAXException(exception); 
    }
    
}
