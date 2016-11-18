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
package de.unibi.techfak.bibiserv.deploy.tools;

import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;

/**
 * AbstractVerfifyTask is an abstraced class used as base for all implementing
 * classes.
 *
 * This class extends the ant Task class.
 *
 *
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 */
public abstract class AbstractVerifyTask extends Task {

    private File runnableitemfile;
    private TrunnableItem runnableitem;
    private JAXBElement<TrunnableItem> jaxbe;

    @Override
    public void execute() throws BuildException {
        if (getRunnableitemfile() == null) {
            throw new BuildException("Attribute runnableitemfile is mandatory!");
        }
    }

    /**
     * Set Tooldescription (=runnableitem) file. Function load file and create a
     * corresponding JAXB object.
     *
     *
     * @param file
     * @throws JAXBException, if file can't be marshalled to a JAXB object
     * @throws IOException if file doesn't ecists or can't be read
     */
    public void setRunnableitemfile(File file) throws JAXBException, IOException {
        runnableitemfile = file;
        if (!runnableitemfile.isFile() || !runnableitemfile.canRead()) {
            throw new IOException("File " + runnableitemfile.toString() + " can't read!");
        }
        JAXBContext ctx = JAXBContext.newInstance(de.unibi.techfak.bibiserv.cms.TrunnableItem.class);
        Unmarshaller um = ctx.createUnmarshaller();
        jaxbe = (JAXBElement) um.unmarshal(file);
        runnableitem = jaxbe.getValue();
    }

    /**
     * Get tooldescription (=runnableitem) file. 
     *
     * @return Return tooldesription file
     */
    public File getRunnableitemfile() {
        return runnableitemfile;
    }

    /**
     * Get tooldescription (JAXB) object
     *
     * @return Return tooldescription object.
     */
    public TrunnableItem getRunnableitem() {
        return runnableitem;
    }

    /**
     * Return W3C DOM document representatiopn from tooldescription.
     *
     * @return Return W3C DOM document representation from tooldescription.
     * @throws JAXBException, if JAXB object can't be marshalled to DOM document
     * @throws ParserConfigurationException, if
     */
    public Document getRunnableitemAsDocument() throws JAXBException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document document = dbf.newDocumentBuilder().newDocument();

        JAXBContext ctx = JAXBContext.newInstance(de.unibi.techfak.bibiserv.cms.TrunnableItem.class);
        Marshaller m = ctx.createMarshaller();
        m.marshal(jaxbe, document);

        return document;
    }
}
