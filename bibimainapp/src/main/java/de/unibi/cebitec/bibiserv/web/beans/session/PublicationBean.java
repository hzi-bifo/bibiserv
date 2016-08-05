/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2011-2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de, 
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
 * "Portions Copyrighted 2011-2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 * 
 * Contributor(s):
 * 
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.techfak.bibiserv.util.ontoaccess.PnPOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPerson;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPublication;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Bean to access publications, authors from Ontology. Caches once requested
 * Publications to increase performance.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class PublicationBean {

    private Map<String, List<BiBiPublication>> cache = new HashMap();

    public BiBiPublication getPrimaryReference(String id) {
        if (getReferences(id) != null && getReferences(id).size() > 0) {
            return getReferences(id).get(0);
        }
        return null;
    }

    public List<BiBiPublication> getReferences(String id) {
        if (!cache.containsKey(id)) {
            cache.put(id, PnPOntoQuestioner.getReferences(id));
        }
        return cache.get(id);
    }

    public StreamedContent getBibTex(String id, String key) {
        BiBiPublication pub = search(getReferences(id), key);
        return new DefaultStreamedContent(new ByteArrayInputStream(pub.getExport("bibtex").getBytes()), "text/plain", pub.getPubkey() + ".bib");
    }

    public StreamedContent getBibTex(BiBiPublication pub) {
        return new DefaultStreamedContent(new ByteArrayInputStream(pub.getExport("bibtex").getBytes()), "text/plain", pub.getPubkey() + ".bib");
    }

    /**
     * Return the responsible author (which is 1st author of all authors)
     * 
     * @param id - id of the tool
     * @return 
     */
    public BiBiPerson getResponsibleAuthor(String id) {
        List<BiBiPerson> l = PnPOntoQuestioner.getAuthors(id);
        if (l != null && !l.isEmpty()) {
            return l.get(0);
        }
        return null;
    }

    /**
     * Return all authors as list of BiBiPerson object 
     * 
     * @param id = id of the tool
     * @return 
     */
    public List<BiBiPerson> getAuthors(String id) {
        List<BiBiPerson> l = PnPOntoQuestioner.getAuthors(id);
        return l;
    }

    /**
     * Return all authors as comma separated string
     * 
     * @param id - id of the tool
     * @return 
     */
    public String getAuthorsAsString(String id) {
        List<BiBiPerson> l = PnPOntoQuestioner.getAuthors(id);
        StringBuilder sb = new StringBuilder();
        if (l != null) {
            for (int i = 0; i < l.size(); ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(l.get(i).getGivenname().charAt(0)).append(". ").append(l.get(i).getFamily_name());
            }
        }
        return sb.toString();
    }

    /**
     * private helper method that search for a publication in given list and
     * given publication key.
     *
     * @param list
     * @param key
     * @return
     *
     */
    private BiBiPublication search(List<BiBiPublication> list, String key) {
        for (BiBiPublication pub : list) {
            if (pub.getPubkey().equalsIgnoreCase(key)) {
                return pub;
            }
        }
        return null;
    }
}
