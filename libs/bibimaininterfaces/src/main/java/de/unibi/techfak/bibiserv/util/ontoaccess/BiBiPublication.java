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
package de.unibi.techfak.bibiserv.util.ontoaccess;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * Interface for the access to a publication object from the ontology, 
 * currently only giving minimal information for referencing
 * 
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 *            Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public interface BiBiPublication {

    /**
     * All authors for this publication
     * @return a list of BiBiPerson entries,
     */
    List<BiBiPerson> getAuthors();

    String getDoi();

    String getJournal();

    String getLocator();

    String getPubkey();

    Date getPublicationdate();

    String getTitle();

    String getType();

    URI getUrl();
    
    /**
     * Export current BiBiPublication.
     * 
     * @param format - Export format.
     * 
     * @return BiBiPublication in defined format. 
     */
    String getExport(String format);

    void setAuthors(List<BiBiPerson> authors);
    
    void addAuthor(BiBiPerson author);

    void setDoi(String doi);

    void setJournal(String journal);

    void setLocator(String locator);

    void setPubkey(String pubkey);

    void setPublicationdate(Date publicationdate);

    void setTitle(String title);

    void setType(String type);

    void setUrl(URI url);
    
}
