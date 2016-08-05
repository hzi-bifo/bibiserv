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
 */
package de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes;

import de.unibi.cebitec.bibiserv.util.bibtexparser.BibTexType;
import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * Interface for the access to a publication object from the ontology, 
 * currently only giving minimal information for referencing
 * 
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 *            Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *          Thomas Gatter - tgatter(at)cebtic.uni-bielefeld.de
 */
public interface BiBiPublication {

    
    /**
     * All authors for this publication
     * @return a list of BiBiPerson entries,
     */
    List<BiBiPerson> getAuthors();
    void setAuthors(List<BiBiPerson> authors);
    void addAuthor(BiBiPerson author);
    
    
    String getTitle();
    void setTitle(String title);
    
    
    Date getPublicationdate();
    void setPublicationdate(Date publicationdate);
    
    
    String getJournal();
    void setJournal(String journal);
    
    
    String getPublisher();
    void setPublisher(String publisher);
    
    
    String getSchool();
    void setSchool(String school);
    
    
    String getInstitution();
    void setInstitution(String institution);
    
    
    String getDoi();
    void setDoi(String doi);

    
    URI getUrl();
    void setUrl(URI url);

    
    String getNote();
    void setNote(String note);
    
    
    String getPubkey();
    void setPubkey(String pubkey);
    
    
    BibTexType getType();
    void setType(BibTexType type);
    

    /**
     * Export current BiBiPublication.
     * 
     * @param format - Export format.
     * 
     * @return BiBiPublication in defined format. 
     */
    String getExport(String format);

}
