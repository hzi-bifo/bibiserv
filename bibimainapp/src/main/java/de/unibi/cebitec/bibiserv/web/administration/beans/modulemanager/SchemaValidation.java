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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 * Christian Henke - chenke@cebitec.uni-bielefeld.de
 */
package de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class SchemaValidation {

    private final static Logger LOG = Logger.getLogger(SchemaValidation.class);
    private String message;

    public SchemaValidation() {
        this.message = "";
    }

    public boolean validate(URL schemaurl, byte[] targetXMLFile) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(schemaurl);
        Validator validator = schema.newValidator();
        Source source = new StreamSource(new ByteArrayInputStream(targetXMLFile));

        this.message = "";
        try {
            validator.validate(source);
            this.message = "OK";
            return true;
        } catch (SAXException ex) {
            this.message = ex.getMessage();
            LOG.error("Error while validating against schema: " + ex.toString());
            return false;
        }

    }

    public String getMessage() {
        return this.message;
    }
}
