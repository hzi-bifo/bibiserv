package de.unibi.cebitec.bibiserv.web.controller;

import de.unibi.cebitec.bibiserv.web.beans.session.SessionTimeout;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.context.ApplicationContext;

/**
 * Handling expiration of Sessions primefaces yet does not support handling of
 * session expiration. therefore we took this solution from web to treat he
 * sessions..
 *
 * @author dhagemei
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class LifeCycleListener implements PhaseListener {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(LifeCycleListener.class);
    
    private ModelAndViewSelector mvSelector;
    private ApplicationContext context;

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    /**
     * A Session can expire in both phases of the jsf lifecycle... ...if a
     * cookie was deleted it appears in afterPhase when a request is stated
     * after some time ...it already appears in beforePhase
     *
     * @param event
     */
    @Override
    public void beforePhase(PhaseEvent event) {
        redirectIfSessionExpired(event);

    }

    /**
     * A Session can expire in both phases of the jsf lifecycle... ...if a
     * cookie was deleted it appears in afterPhase when a request is stated
     * after some time ...it already appears in beforePhase
     *
     * @param event
     */
    @Override
    public void afterPhase(PhaseEvent event) {
        redirectIfSessionExpired(event);

    }

    public void redirectIfSessionExpired(PhaseEvent event) {

        FacesContext facesCtx = event.getFacesContext();
        ExternalContext extCtx = facesCtx.getExternalContext();
        HttpSession session = (HttpSession) extCtx.getSession(false);
        boolean newSession = (session == null) || (session.isNew());
        boolean postback = !extCtx.getRequestParameterMap().isEmpty();
        boolean timedout = postback && newSession;

        if (timedout) {
           try {
                // set reqeusted url to bean for link in sessionTimeout.xhtml
                SessionTimeout sessionTimeout = (SessionTimeout) facesCtx.getApplication().
                        evaluateExpressionGet(facesCtx, "#{sessionTimeoutBean}", SessionTimeout.class);
                sessionTimeout.setCleanedUrl(extCtx.getRequestServletPath());
                
                if (!extCtx.isResponseCommitted()) { // if response is already committed we can't do anything ...
                    extCtx.redirect("/sessionTimeout.jsf");
                } else {
                    log.info("Respone is already committed - can't redirect!");
                }
               } catch (Throwable t) {
                    switch (log.getLevel().toInt()) {
                        case (Priority.DEBUG_INT) : 
                            log.debug("Exception while handling Session Timeout :"+ t.getMessage(), t);
                            break;
                        case (Priority.INFO_INT) :
                            log.info("Exception while handling Session Timeout :"+ t.getMessage());
                            break;
                    }
               }
        }
      
    }
}
