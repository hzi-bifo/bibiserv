/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010/2011 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.controller;

import de.unibi.cebitec.bibiserv.statistics.logging.StatsLogger;
import de.unibi.cebitec.bibiserv.web.beans.session.CategoryBean;
import de.unibi.cebitec.bibiserv.web.beans.session.ErrorBean;
import de.unibi.cebitec.bibiserv.web.beans.session.ItemBean;
import de.unibi.cebitec.bibiserv.web.beans.session.MenuBean;
import de.unibi.cebitec.bibiserv.web.beans.session.NaviBean;
import de.unibi.cebitec.bibiserv.web.xmlreader.ItemContent;
import de.unibi.cebitec.bibiserv.web.xmlreader.RunnableItemContent;
import de.unibi.cebitec.bibiserv.web.xmlreader.View;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Class ModelAndViewSelector contains methods to select the appropriate view by
 * parsing/comparing the http-request with xml-db
 *
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
 * @author Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class ModelAndViewSelector {

    private static Logger log = Logger.getLogger(ModelAndViewSelector.class);
    @Inject
    private ErrorBean errorBean;
    @Inject
    private MenuBean menuBean;
    @Inject
    private NaviBean naviBean;
    @Inject
    private CategoryBean categoryBean;
    @Inject
    private StatsLogger statsLogger;
    @Inject
    private ItemBean itemBean;

    private String viewname;

    /**
     * Method takes httpServletRequest and identifies the target view (jsf-page
     * name) ...initiates a redirect if an URL is created with ".html"
     *
     * @param httpServletRequest
     */
    public void selectModelAndView(HttpServletRequest httpServletRequest) {

//        httpServletRequest.get
        //prepare needed hashes
        Map<String, List<String>> viewHash = menuBean.getMenuHolder().getViewHash();
        HashMap<String, ItemContent> itemContentHash = menuBean.getMenuHolder().getItemContentHash();

        //set default view to error message
        setViewName("/error.jsf", "An unexpected Error occured when trying to navigate to your requested URL.");
        String url = httpServletRequest.getRequestURI();

        //reset NaviBean to ensure a clean processing of the optional navigation bar content
        naviBean.reset();

        // log client info to database
        statsLogger.logClientInfo(httpServletRequest);

        //**********************
        //by Armin and Daniel
        //check if '/' or 'index.html' was requested
        if (url.equals("/") || url.equals("/index.html")) {
            setViewName("/category.jsf");
            categoryBean.setID("bibiserv");
            categoryBean.setViewHash(viewHash);

            //at this point the node with id ... will be higlighted
            menuBean.setCurrentId("bibiserv", httpServletRequest);
            return;
        }

        //remove ;jsessionid=692b6ba24173f0cfda716a1b432b session id from url
        String[] jsessionid = url.split(";jsessionid=");
        /*if (jsessionid.length > 1) {
         log.debug(";jsessionid=" + jsessionid[1] + " removed");
         }*/
        url = jsessionid[0];
        //creating array of url components last element is id of page/tool
        String id = null;
        String[] urlList = url.split("/");

        //remove html ending
        if (urlList.length > 0) {
            id = urlList[1];
        }

        // log click info to database
        statsLogger.logToolClick(httpServletRequest, id);

        /**
         * NAVIGATION TO CATEGORY PAGE different from start page
         */
        //viewHash contains categories & subcategories, never ITEMS...
        if (viewHash.containsKey(id)) {
            setViewName("/category.jsf");

            statsLogger.logToolClick(httpServletRequest, id);

            categoryBean.setID(id);

            //Here the displayed children are set to the list in category pages...
            categoryBean.setViewHash(viewHash);

            /**
             * setting current item and expanding menu in this way !
             */
            menuBean.setCurrentId(id, httpServletRequest);

            return;

        }
        /**
         * GOING TO A TOOL PAGE
         */
        if (itemContentHash.containsKey(id)) {

            //distinguish between Item and RunnableItems
            if (itemContentHash.get(id) instanceof RunnableItemContent) {

                RunnableItemContent ric = (RunnableItemContent) itemContentHash.get(id);

                //set id to menu and expand currently requested id in menu
                menuBean.setCurrentId(id, httpServletRequest);
                naviBean.setItemId(id);

                //building basic path to pages deployed with tools
                StringBuilder newUrl = new StringBuilder();
                newUrl.append("/applications/");
                newUrl.append(id);
                newUrl.append("/pages/");

                // create an empty view
                View view = ric.createViewByArguments(
                        httpServletRequest.getParameter("id"),
                        httpServletRequest.getParameter("viewType"),
                        httpServletRequest.getParameter("subType"));

                // and append it to naviBean
                naviBean.setView(view);

                switch (view.getType()) {
                    case "submission":
                        // gesonderte Behandlung (s.u.)
                        if (view.getSubtype() == null) {
                            //if number of functions =1 going directly to function page 1
                            // else navigate to submission page to ask the user which function he wants to use
                            if ((ric.getFunctions().size() == 1)) {
                                newUrl.append(ric.getFunctions().get(0));
                                newUrl.append("_p_1");
                            } else {

                                newUrl.append(view.getType());
                            }
                        } else {
                            String[] subtype = view.getSubtype().split("_p_");
                            String[] funct = view.getSubtype().split("_result|_visualization");
                            //If requested a resultpage....
                            if (view.getSubtype().endsWith("_result") && ric.getFunctions().contains(funct[0])) {
                                newUrl.append(view.getSubtype());
                            } //If requested a visualizer page....
                            else if (view.getSubtype().endsWith("_visualization") && ric.getFunctions().contains(funct[0])) {
                                newUrl.append(view.getSubtype());
                            }//CHECK IF USED subType (function) is valid ....
                            else if (ric.getFunctions().contains(subtype[0])) {
                                newUrl.append(view.getSubtype());
                            } // If none of the subtypes fits.. 
                            else {
                                if ((ric.getFunctions().size() == 1)) {
                                    newUrl.append(ric.getFunctions().get(0));
                                    newUrl.append("_p_1");
                                } else {
                                    newUrl.append(view.getType());
                                }
                            }
                        }
                        break;
                    case "reset_session":
                    case "download":
                    case "webservice":
                    case "references":
                    case "faq":
                    case "manual":
                    case "other":
                        newUrl.append(view.getType());
                        break;
                    case "RITC":
                        newUrl = new StringBuilder();
                        newUrl.append("runinthecloud");
                        break;
                    case "independent":
                    case "webstart":
                        newUrl.append(view.getId());
                        break;
                    default: // if type is unkown jump to startpage                 
                        newUrl.append("welcome");
                        break;

                }
                newUrl.append(".jsf");
                setViewName(newUrl.toString());
            } else {
                setViewName("/item.jsf");
                itemBean.setID(id);
                // setting current item and expanding menu in this way !
                menuBean.setCurrentId(id, httpServletRequest);

            }

            /**
             * SHOWING ERROR PAGE CAUSE NO TOOL INSTALLED
             */
        } else {
            setViewName("/error.jsf", "No Tool with the ID " + id + " is currently installed on BiBiserv. Please try out the tools listed in the Menu !");
            return;
        }
    }

    /**
     * Setting bean that contains error messages (DI) see applicationContext.xml
     *
     * @param errorBean
     */
    public void setErrorBean(ErrorBean errorBean) {
        this.errorBean = errorBean;
    }

    /**
     * Possibility to set the view name and an error message... used by
     * RequestFilter that retrieves an instance of ModelAndView>>Selectr by DI
     * to display error page!
     *
     * @param viewName in form /error.jsf
     * @param message Error message that will be displayed on Error-page
     */
    public void setViewName(String viewName, String message) {
        this.viewname = viewName;
        errorBean.setErrorMessage(message);
    }

    /**
     * Returns name of target view
     *
     * @return viewName Name of the target view
     */
    public String getViewName() {
        return viewname;
    }

    public void setViewName(String viewName) {
        this.viewname = viewName;
    }

    /**
     * Category Bean is set by DI. the beans gets the ID of the current Category
     * the beans will show the language dependent properties...
     *
     * @param categoryBean
     */
    public void setCategoryBean(CategoryBean categoryBean) {
        this.categoryBean = categoryBean;
    }

    public void setMenuBean(MenuBean menuBean) {
        this.menuBean = menuBean;
    }

    public void setNaviBean(NaviBean naviBean) {
        this.naviBean = naviBean;
    }

    public void setStatsLogger(StatsLogger statsLogger) {
        this.statsLogger = statsLogger;
    }

    public CategoryBean getCategoryBean() {
        return categoryBean;
    }

    public ErrorBean getErrorBean() {
        return errorBean;
    }

    public MenuBean getMenuBean() {
        return menuBean;
    }

    public NaviBean getNaviBean() {
        return naviBean;
    }

}
