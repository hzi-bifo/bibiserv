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
package de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl;

import de.unibi.cebitec.bibiserv.util.bibtexparser.BibTexEntries;
import de.unibi.cebitec.bibiserv.util.bibtexparser.BibTexType;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPerson;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPublication;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of BiBiPublication Interface 
 * 
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 *          Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *          Thomas Gatter  - tgatter(at)cebitec.uni-bielefeld.de
 */
public class BiBiPublicationImplementation implements BiBiPublication {

    private String pubkey = "";
    private List<BiBiPerson> authors = new ArrayList<BiBiPerson>();
    private String title = "";
    private String journal = "";
    private String publisher = "";
    private String school = "";
    private String institution = "";
    private String note = "";
    private Date publicationdate = new Date();
    private URI url;
    private BibTexType type = BibTexType.article;
    private String doi = "";
    private final String br = System.getProperty("line.separator");
    private static final SimpleDateFormat formatYear = new SimpleDateFormat(
            "yyyy");

    @Override
    public List<BiBiPerson> getAuthors() {
        return authors;
    }

    @Override
    public void setAuthors(List<BiBiPerson> authors) {
        this.authors = authors;
    }

    @Override
    public String getDoi() {
        return doi;
    }

    @Override
    public void setDoi(String doi) {
        this.doi = doi;
    }

    @Override
    public String getJournal() {
        return journal;
    }

    @Override
    public void setJournal(String journal) {
        this.journal = journal;
    }

    @Override
    public String getPubkey() {
        return pubkey;
    }

    @Override
    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    @Override
    public Date getPublicationdate() {
        return publicationdate;
    }

    @Override
    public void setPublicationdate(Date publicationdate) {
        this.publicationdate = publicationdate;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public URI getUrl() {
        return url;
    }

    @Override
    public void setUrl(URI url) {
        this.url = url;
    }

    @Override
    public void addAuthor(BiBiPerson author) {
        this.authors.add(author);
    }

    @Override
    public String getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String getPublisher() {
        return publisher;
    }

    @Override
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String getSchool() {
        return school;
    }

    @Override
    public void setSchool(String school) {
        this.school = school;
    }

    @Override
    public BibTexType getType() {
        return type;
    }

    @Override
    public void setType(BibTexType type) {
        this.type = type;
    }

    /**
     * Implementation of getExport function. Supports three different formats:
     * bibtex, html and plain
     * 
     * @param format - one of bibtex, html or plain
     * 
     * @return Representation as string
     */
    @Override
    public String getExport(String format) {

        // build 
        StringBuilder ab = new StringBuilder();
        boolean first = true;
        if (getAuthors().size() == 1) { 
            BiBiPerson bp = getAuthors().get(0);
            ab.append(bp.getGivenname()).append(" ").append(bp.getFamily_name());
            
        } else {
            for (BiBiPerson bp : getAuthors()) {
                if (first) {
                    first = false;
                } else {
                    ab.append(" and ");
                }
                ab.append(bp.getFamily_name());
                if (!bp.getGivenname().isEmpty()) {
                    ab.append(", ").append(bp.getGivenname());
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        if ("html".equalsIgnoreCase(format)) {
            // authors
            sb.append("<em>").append(ab).append("</em>");
            // title (and url)
            sb.append(" <strong>");
            if (!doi.isEmpty()) {
                sb.append("<a href=\"http://dx.doi.org/").append(doi).append(
                        "\">").append(title).
                        append("</a>");

            } else if (url != null) {
                sb.append("<a href=\"").append(url).append("\">").append(title).
                        append("</a>");
            } else {
                sb.append(title);
            }
            sb.append("</strong>");

            // create the rest of the content
            switch (type) {
                case article:
                    sb.append(", ").append(journal);
                    if (!note.isEmpty()) {
                        sb.append(", ").append(note);
                    }
                    break;
                case book:
                    sb.append(", ").append(publisher);
                    if (!note.isEmpty()) {
                        sb.append(", ").append(note);
                    }
                    break;
                case inproceedings:
                    if (!note.isEmpty()) {
                        sb.append(", ").append(note);
                    }
                    if (!publisher.isEmpty()) {
                        sb.append(", ").append(publisher);
                    }
                    break;
                case manual:
                    if (!note.isEmpty()) {
                        sb.append(", ").append(note);
                    }
                case mastersthesis:
                case phdthesis:
                    sb.append(", ").append(school);
                    if (!note.isEmpty()) {
                        sb.append(", ").append(note);
                    }
                    break;
                case proceedings:
                    if (!note.isEmpty()) {
                        sb.append(", ").append(note);
                    }
                    if (!publisher.isEmpty()) {
                        sb.append(", ").append(publisher);
                    }
                    break;
                case techreport:
                    sb.append(", ").append(institution);
                    if (!note.isEmpty()) {
                        sb.append(", ").append(note);
                    }
                    break;
            }
            sb.append(", ").append(formatYear.format(publicationdate));
        } else if ("bibtex".equalsIgnoreCase(format)) {
            // init content with id
            sb.append("@").append(type.getName()).append("{").append(pubkey).
                    append(",").append(br);
            // create the rest of the content
            switch (type) {
                case article:
                    sb.append(BibTexEntries.author).append("={").append(ab).
                            append("},").append(br);
                    sb.append(BibTexEntries.title).append("={").append(title).
                            append("},").append(br);
                    sb.append(BibTexEntries.journal).append("={").append(journal).
                            append("},").append(br);
                    sb.append(BibTexEntries.year).append("={").append(formatYear.
                            format(publicationdate)).append("}");
                    if (!note.isEmpty()) {
                        sb.append(",").append(br).append(BibTexEntries.note).
                                append("={").append(note).append("}");
                    }
                    break;
                case book:
                    sb.append(BibTexEntries.author).append("={").append(ab).
                            append("},").append(br);
                    sb.append(BibTexEntries.title).append("={").append(title).
                            append("},").append(br);
                    sb.append(BibTexEntries.publisher).append("={").append(
                            publisher).append("},").append(br);
                    sb.append(BibTexEntries.year).append("={").append(formatYear.
                            format(publicationdate)).append("}");
                    if (!note.isEmpty()) {
                        sb.append(",").append(br).append(BibTexEntries.note).
                                append("={").append(note).append("}");
                    }
                    break;
                case inproceedings:
                    sb.append(BibTexEntries.author).append("={").append(ab).
                            append("},").append(br);
                    sb.append(BibTexEntries.title).append("={").append(title).
                            append("},").append(br);
                    sb.append(BibTexEntries.year).append("={").append(formatYear.
                            format(publicationdate)).append("}");
                    if (!note.isEmpty()) {
                        sb.append(",").append(br).append(BibTexEntries.note).
                                append("={").append(note).append("}");
                    }
                    if (!publisher.isEmpty()) {
                        sb.append(",").append(br).append(BibTexEntries.publisher).
                                append("={").append(publisher).append("}");
                    }
                    break;
                case manual:
                    sb.append(BibTexEntries.author).append("={").append(ab).
                            append("},").append(br);
                    sb.append(BibTexEntries.title).append("={").append(title).
                            append("},").append(br);
                    sb.append(BibTexEntries.year).append("={").append(formatYear.
                            format(publicationdate)).append("}");
                    if (!note.isEmpty()) {
                        sb.append(",").append(br).append(BibTexEntries.note).
                                append("={").append(note).append("}");
                    }
                    break;
                case mastersthesis:
                case phdthesis:
                    sb.append(BibTexEntries.author).append("={").append(ab).
                            append("},").append(br);
                    sb.append(BibTexEntries.title).append("={").append(title).
                            append("},").append(br);
                    sb.append(BibTexEntries.year).append("={").append(formatYear.
                            format(publicationdate)).append("},").append(br);
                    sb.append(BibTexEntries.school).append("={").append(school).
                            append("}");
                    if (!note.isEmpty()) {
                        sb.append(",").append(br).append(BibTexEntries.note).
                                append("={").append(note).append("}");
                    }
                    break;
                case proceedings:
                    sb.append(BibTexEntries.author).append("={").append(ab).
                            append("},").append(br);
                    sb.append(BibTexEntries.title).append("={").append(title).
                            append("},").append(br);
                    sb.append(BibTexEntries.year).append("={").append(formatYear.
                            format(publicationdate)).append("}");
                    if (!note.isEmpty()) {
                        sb.append(",").append(br).append(BibTexEntries.note).
                                append("={").append(note).append("}");
                    }
                    if (!publisher.isEmpty()) {
                        sb.append(",").append(br).append(BibTexEntries.publisher).
                                append("={").append(publisher).append("}");
                    }
                    break;
                case techreport:
                    sb.append(BibTexEntries.author).append("={").append(ab).
                            append("},").append(br);
                    sb.append(BibTexEntries.title).append("={").append(title).
                            append("},").append(br);
                    sb.append(BibTexEntries.year).append("={").append(formatYear.
                            format(publicationdate)).append("},").append(br);
                    sb.append(BibTexEntries.institution).append("={").append(
                            institution).append("}");
                    if (!note.isEmpty()) {
                        sb.append(",").append(br).append(BibTexEntries.note).
                                append("={").append(note).append("}");
                    }
                    break;
            }
            if (!doi.isEmpty()) {
                sb.append(",").append(br).append(BibTexEntries.doi).append("={").
                        append(doi).append("}");
            }
            if (url != null) {
                sb.append(",").append(br).append(BibTexEntries.url).append("={").
                        append(url).append("}");
            }
            sb.append("}");
        } else {
            sb.append(ab).append(",").append(title);
            if (journal != null && !journal.isEmpty()) {
                sb.append(" in ").append(journal);
            }
            sb.append("[").append(formatYear.format(publicationdate)).append("]");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return getExport("plain");
    }
}
