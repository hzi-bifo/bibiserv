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
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.techfak.bibiserv.web.beans.session.MessagesInterface;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.NoSuchMessageException;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * This class defines a bean for accessing messages from a Spring MessageSource
 * given in the context. It allows fore reading of basic and parametrized
 * properties.
 * 
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 */
public class Messages implements MessagesInterface, MessageSourceAware {

    @Inject
    private MessageSource messageSource;
    private static Logger log = Logger.getLogger(Messages.class);

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * This method returns a basic property, identified by key
     *
     * @param key - the property key of the requested property
     * @return The requested property string.
     */
    public String property(String key) {
        return property(key, null);
    }

    /**
     * This method returns a parametrized property, identified by key and
     * parameters. The parameters need to be one string, containing the
     * different params in a comma-separated list. This constraint stems from
     * the inability of EL-language constructs to deliver real arrays to method
     * calls. 
     * 
     * Example: The property is defined like this:
     * "LongRangeValidator.NOT_IN_RANGE=Your value is not in the range from {0} to {1}."
     *
     * This can be parametrized with e.g. the values '0' and 'pretty much'
     * by inserting the following into the xhtml-document:
     * <ice:outputText escape="false" value="#{messages.property('LongRangeValidator.NOT_IN_RANGE','0,pretty much')}"/>
     * 
     * The resulting output would then be:
     * "Your value is not in the range from 0 to pretty much."
     *
     * @param key - the property key of the requested property
     * @param args_csv - the argument strings, as one comma-separated string
     * @return The requested property string, with argument replacement done.
     */
    public String property(String key, String args_csv) {
        //check argument string and split it into an argument array at ',' chars
        String args[] = null;
        if (args_csv != null) {
            String splitPattern = "\\s*,\\s*";
            args = args_csv.split(splitPattern);
        }
        try {
            Locale preflocale = getPreferredLocale();
            return messageSource.getMessage(key, args, preflocale);
        } catch (NoSuchMessageException ex) {
            //if requested locale was not found:try null locale...
            //if no such key is in properties default empty message is set!
            log.info("Content for property key '" + key + "' could not be found anywhere. Exception was: " + ex.getLocalizedMessage());
            return messageSource.getMessage(key, args, "", null);
        }
    }

    

    private Locale createLocaleFromLanguage(String languages) {
        Locale prefLocale = null;
        if (languages != null) {
            String[] langList = languages.split(",");
            //splitting comma seperated list of languages...
            String prefLang = langList[0];
            //took first element of list...like de-de or en etc...
            log.debug("Preferred language: " + prefLang);
            //split the prefLang between language and country if it was entered e.g. de-de
            String[] langAndCountry = prefLang.split("-");
            //check for level of definition and set locale accordingly
            switch (langAndCountry.length) {
                case 2:
                    //log.debug("Language code: " + langAndCountry[0] + " Country-Code: " + langAndCountry[1]);
                    prefLocale = new Locale(langAndCountry[0], langAndCountry[1]);
                    break;
                case 1:
                    //log.debug("Created Locale for language: " + langAndCountry[0]);
                    prefLocale = new Locale(langAndCountry[0]);
                    break;
                default:
                    //log.debug("Could not determine request language.");
                    break;
            }
        } else {
            log.debug("No language was specified in request.");
        }
        return prefLocale;
    }

    
    /**
     * Message for retreiving the prefferred language by http request header...
     * @param request
     * @return 
     */
    public Locale getPreferredLocale(HttpServletRequest request) {
        return this.getPreferredLocale(request.getHeader("accept-language"),null);
    }

    /**
     * This method checks browser and system settings and creates the currently
     * relevant locale
     *
     * @return currently preferred locale of user
     */
    public Locale getPreferredLocale() {
        if (FacesContext.getCurrentInstance() != null) {
            return this.getPreferredLocale(FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("accept-language"), FacesContext.getCurrentInstance());
        } else {       
            return null;
        }
    }

    /**
     * This method checks browser and system settings and creates the currently
     * relevant locale
     *
     * @return currently preferred locale of user
     */
    private Locale getPreferredLocale(String languages, FacesContext fc) {
        log.debug("Called getPreferredLocale ");

        Locale prefLocale = createLocaleFromLanguage(languages);
        if (prefLocale == null && fc != null) {
            prefLocale = fc.getViewRoot().getLocale();
        }


        return prefLocale;
    }
}
