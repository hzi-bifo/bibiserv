/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
All rights reserved.

The contents of this file are subject to the terms of the Common
Development and Distribution License("CDDL") (the "License"). You
may not use this file except in compliance with the License. You can
obtain a copy of the License at http://www.sun.com/cddl/cddl.html

See the License for the specific language governing permissions and
limitations under the License.  When distributing the software, include
this License Header Notice in each file.  If applicable, add the following
below the License Header, with the fields enclosed by brackets [] replaced
by your own identifying information:

"Portions Copyrighted 2011 BiBiServ"

Contributor(s):
 */
package de.unibi.techfak.bibiserv.util.dependencyparser;

import de.unibi.techfak.bibiserv.util.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class ParameterWrapper {

    private HashMap<String, List<String>> paramcon = new HashMap<String, List<String>>();

    public void setParameter(List<Pair<String, String>> parampairlist) {
        paramcon.clear();
        if (parampairlist == null) {
            return;
        }
        for (Pair<String, String> p : parampairlist) {
            put(p);
        }
    }

    public void setParameter(File file) throws DependencyException {
        try {
            /* read param from (xml-file)*/
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            /* read param from file */
            Document param = db.parse(file);
            setParameter(param.getDocumentElement());
        } catch (Exception e) {
            throw new DependencyException(e, DependencyExceptionEnum.setParameter, "");
        }
    }

    public void setParameter(Element param) throws DependencyException {
        paramcon.clear();
        try {
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            NodeList nl = (NodeList) xpath.evaluate("//*/@id", param, XPathConstants.NODESET);

            for (int i = 0; i < nl.getLength(); ++i) {
                Element id_e = (Element) nl.item(i);

                String id = id_e.getTextContent();

                Element e = (Element) xpath.evaluate("//*[@id='" + id + "']", param, XPathConstants.NODE);
                if (e == null) {
                    put(id, "");
                } else {
                    put(id, e.getTextContent());
                }

            }
        } catch (Exception e) {
            throw new DependencyException(e,DependencyExceptionEnum.setParameter, "");
        }
    }

    public boolean idExists(String id) {
        return paramcon.containsKey(id);
    }

    public List<String> getValue(String id) {
        return paramcon.get(id);
    }

    public Set<String> getIdList() {
        return paramcon.keySet();
    }

    public void clear() {
        paramcon.clear();
    }

    /*  ----------- private methods --------------- */
    private void put(String key, String value) {
        put(new Pair<String, String>(key, value));
    }

    private void put(Pair<String, String> p) {
        List<String> values;
        if (paramcon.containsKey(p.getKey())) {
            values = paramcon.get(p.getKey());
        } else {
            values = new ArrayList<String>();
            paramcon.put(p.getKey(), values);
        }
        values.add(p.getValue());
    }
}
