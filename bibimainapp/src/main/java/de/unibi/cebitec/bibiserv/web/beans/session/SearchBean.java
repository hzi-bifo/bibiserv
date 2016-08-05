/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2012 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger, Benjamin Paassen
 *
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.search.BiBiServSearch;
import de.unibi.cebitec.bibiserv.search.OutputSearchResult;
import de.unibi.cebitec.bibiserv.search.exceptions.InvalidWordException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.apache.log4j.Logger;

/**
 * SearchBean, used by search field and search result pages
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SearchBean {

    /**
     * Keys for messages.
     */
    private static final String NORESULTSKEY = "de.unibi.techfak.bibiserv.bibimainapp.session.search.NORESULTS";
    private static final String ERRORKEY = "de.unibi.techfak.bibiserv.bibimainapp.session.search.ERROR";
    private static final String HELPKEY = "de.unibi.techfak.bibiserv.bibimainapp.session.search.HELP";
    /**
     * Logger for internal errors.
     */
    private static Logger log = Logger.getLogger(SearchBean.class);
    /**
     * The search query the user has given.
     */
    private String searchPattern = "";
    /**
     * The users search result.
     */
    private List<OutputSearchResult> searchResult = new ArrayList<OutputSearchResult>();
    /**
     * should the single matches for each result be shown?
     */
    private boolean showMatches = false;
    /**
     * Error messages that are displayed to the user if a search fails.
     */
    private String errorMessage = "";
    /**
     * Messages bean for accessing properties file.
     */
    @Inject
    private Messages messages;

    /**
     * Get the value of errorMessage
     *
     * @return the value of errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    public List<OutputSearchResult> getSearchResult() {
        return searchResult;
    }

    public boolean isShowMatches() {
        return showMatches;
    }

    public void toggleShowMatches() {
        showMatches = !showMatches;
    }

    /**
     * This method redirects to the help page.
     */
    public void showHelp() {
        //redirect to help page.
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();
        String url = extContext.encodeActionURL(ctx.getApplication().
                getViewHandler().getActionURL(ctx, "/searchHelp.xhtml"));
        try {
            extContext.redirect(url);
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * This method calls the actual search.
     */
    public void search() {
        try {
            //call search.
            BiBiServSearch search = BiBiServSearch.getInstance();
            searchResult = search.search(searchPattern);
            if (searchResult.isEmpty()) {
                //if the result is empty, show an error message.
                errorMessage = messages.property(NORESULTSKEY,
                        searchPattern.replaceAll(",", ""));
            }
        } catch (InvalidWordException ex) {
            searchResult = new ArrayList<OutputSearchResult>();
            //store error message.
            errorMessage = messages.property(ERRORKEY,
                    searchPattern.replaceAll(",", "")) + ex.getMessage();

        } finally {
            //do a redirect in any case.
            FacesContext ctx = FacesContext.getCurrentInstance();
            ExternalContext extContext = ctx.getExternalContext();
            String url = extContext.encodeActionURL(ctx.getApplication().
                    getViewHandler().getActionURL(ctx, "/search.xhtml"));
            try {
                extContext.redirect(url);
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }
}
