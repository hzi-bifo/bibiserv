/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2014 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2014 BiBiServ Curator Team"
 *
 * Contributor(s): Jan krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *
 */
package de.unibi.cebitec.bibiserv.web.xmlreader;

import de.unibi.techfak.bibiserv.cms.Tfaq;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * Concrete Implementation of an ItemContent for a runnable item node.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class RunnableItemContent extends ItemContent {

    // put all properties for text item content class here
    /* view(type)s supported by this item - supported by runnableitem*/
    private final List<View> views = new ArrayList();

    /* Subtypes of submission view */
    private final List<String> functions = new ArrayList();

    /* webstart elements  - map of webstart id and jnlp elements */
    private final Map<String, Element> webstart = new HashMap();

    /*FAQ element */
    private Tfaq faq = null;

    private static final Logger LOG = Logger.getLogger(RunnableItemContent.class);

    /**
     * Return a List of supported viewss
     *
     * @return
     */
    public List<View> getViews() {
        return views;
    }

    /**
     * Return a list of supported Functions
     *
     * @return
     */
    public List<String> getFunctions() {
        return functions;
    }

    /**
     * Return a list of supported webstarts
     *
     * @return
     */
    public Map<String, Element> getWebstart() {
        return webstart;
    }

    /**
     * Return a FAQ JAXB element, which might be empty if runnable item doesn't
     * contain a FAQ element.
     *
     * @return
     */
    public Tfaq getFaq() {
        if (faq == null) {
            try {
                Unmarshaller um = JAXBContext.newInstance("de.unibi.techfak.bibiserv.cms").createUnmarshaller();
                JAXBElement<TrunnableItem> jaxbe = (JAXBElement<TrunnableItem>) um.unmarshal(getDoc());
                TrunnableItem ri = jaxbe.getValue();

                if (ri.isSetFaq()) {
                    faq = ri.getFaq();
                } else {
                    faq = new Tfaq();
                }

            } catch (JAXBException e) {
                LOG.fatal("While adding tool's reference to the ontology, the given dom document couldn't be parsed as tool description XML. Error was:\n" + e.getLocalizedMessage(), e);
            }

        }

        return faq;
    }

    /**
     * Returns the view thats fits the given arguments. If no view found return
     * a new View with null id and "unknown" type/subtype;
     *
     * Function uses following strategy: - a given id has precedence before
     * type/subtype argument - ...
     *
     *
     * @param id
     * @param type
     * @param subtype
     * @return
     */
    public View createViewByArguments(String id, String type, String subtype) {
        // create an unknown view
        View view = new View("unknown", null, false, "unkown");

        for (View v : views) {
            if (id != null && v.getId().equals(id)) {
                view = (View) v.clone();
                break;
            } else if (v.getType().equals(type)) {
                view.setType(type);
                view.setSubtype(subtype);
                view.setId(v.getId());
                break;
            }
        }
        return view;

    }

}
