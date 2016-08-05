/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2013 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.web.xmlreader.BiBiServXMLReader;
import de.unibi.cebitec.bibiserv.web.xmlreader.ItemContent;
import de.unibi.cebitec.bibiserv.web.xmlreader.RunnableItemContent;
import de.unibi.techfak.bibiserv.cms.Tfaq;
import de.unibi.techfak.bibiserv.cms.TfaqItem;
import java.io.StringWriter;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

/**
 * Session bean container for current FAQ information
 *
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class FAQBean {

    @Inject
    @Named("bibiservXmlReader")
    private BiBiServXMLReader xmlrequest;
    private static Logger log = Logger.getLogger(FAQBean.class);

    public Tfaq getFaq(String id) {

        HashMap<String, ItemContent> ch = xmlrequest.getItemContentHash();

        if (ch.containsKey(id)) {
            // Typcheck ist an dieser Stelle nicht notwendig, da diese Bean nur von app (=runnableitem)
            // genutzt werden kann, d.h. wenn ein Eintrag existiert dann kann ich davon ausgehen das
            // er vom Type RunnableItemContent ist

            //((RunnableItemContent)ch.get(id)).getFaq().getFAQchapter().get(0).getFAQitem().get(0).getAnswer().get(0).
            return ((RunnableItemContent) ch.get(id)).getFaq();
        }
        // should never occure, but you never know ...
        return null;
    }

    public void setXmlrequest(BiBiServXMLReader xmlrequest) {
        this.xmlrequest = xmlrequest;
    }

    /* helper methods - a fast hack solution to convert a MicroHTML JAXb Answer object to plain text */
    public String FaqAnswertoString(TfaqItem.Answer answer) {
        try {
            Marshaller m = JAXBContext.newInstance("de.unibi.techfak.bibiserv.cms").createMarshaller();
           
            StringWriter sw = new StringWriter();
            m.marshal(new JAXBElement(new QName("html"), TfaqItem.Answer.class ,answer), sw);
            
            // remove xml directive, root element and any ns prefix
            String content = sw.toString();
            content = content.replaceAll("<\\?.+?>", "");
            content = content.replaceAll("xmlsns:.+?=\".+?\"","");
            content = content.replaceAll("</?html.*?>","");
            content = content.replaceAll("<[^/].*?:","<");
            content = content.replaceAll("</.+?:","</");
            return content;

        } catch (JAXBException e) {
            log.fatal(e.getMessage(), e);
        }
  
        
        return "Exception occurred ";
    }
}
