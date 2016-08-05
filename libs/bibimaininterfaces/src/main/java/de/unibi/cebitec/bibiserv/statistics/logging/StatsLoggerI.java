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
package de.unibi.cebitec.bibiserv.statistics.logging;

import javax.servlet.http.HttpServletRequest;


/**
 * Interface to store statistical information in database.
 *
 * 
 * @author Jan Krueger (jkrueger@cebitec.uni-bielefeld.de), Armin Toepfer (atoepfer@cebitec.uni-bielefeld.de) (previous version)
 */
public interface StatsLoggerI {

    
    
    /**
     * Log Client Info
     * 
     * @param request - ServletEquest
     */
     public void logClientInfo(HttpServletRequest request);
     
     /**
      * Log Tool Click 
      * 
      * @param request - ServletRequest
      * @param id  - TOOL ID the log should be count for
      */
     public void logToolClick(HttpServletRequest request, String id);
     
    /** Log Download Info 
     *  
     * @param id - Tool ID the log should be count for
     * @param name - unique download name (normally file name)
     * @param url - valid download url (extern or intern)
     */
    public void logDownload(String id, String name, String url);
    /**
     * Log Tool usage (link a bibiserv id to a session id)
     * 
     * @param sessionid
     * @param bibiservid 
     */
    public void logToolUsage(String sessionid, String bibiservid);
    
    /**
     * Calls method to save click on a category in database.
     *
     * @param category Name of the category.
     * @param sessionId Session id.
     * @param ip IP of the user.
     */
    @Deprecated
    void logCategory(String category, String sessionId, String ip);

    /**
     * Calls method to save runtime of a tool submission in database.
     *
     * @param toolname Name of the tool.
     * @param id BiBiServ id.
     * @param seconds Runtime in seconds.
     * @param statusCode Status code provided by tool.
     * @param description Description of the status code.
     */
    @Deprecated
    void logRuntime(String toolname, String id, long seconds, int statusCode, String description);

     /**
     * Calls method to save runtime of a tool submission in database. Should be used if facescontext
      * isn't available to detect the sessionid during runtime
     *
     * @param toolname Name of the tool.
     * @param sessionId the session id 
     * @param id BiBiServ id.
     * @param seconds Runtime in seconds.
     * @param statusCode Status code provided by tool.
     * @param description Description of the status code.
     */
    @Deprecated
    void logRuntime(String toolname, String sessionId, String id, long seconds, int statusCode, String description);

    /**
     * Calls method to save a tool submission in database.
     *
     * @param toolname Name of the tool.
     * @param isDefault Boolean if the default parameters have been used.
     * @param isExample Boolean if the example input(s) and default parameters
     *  have been used.
     */
    @Deprecated
    void logSubmit(String toolname, boolean isDefault, boolean isExample);

    /**
     * Calls method to save a tool submission in database.Should be used if facescontext
     * isn't available to detect the sessionid during runtime
     *
     * @param toolname Name of the tool.
     * @param sessionId the sessionId
     * @param isDefault Boolean if the default parameters have been used.
     * @param isExample Boolean if the example input(s) and default parameters
     *  have been used.
     */
    @Deprecated
    void logSubmit(String toolname, String sessionId, boolean isDefault, boolean isExample);

    /**
     * Calls method to save click on a tool in database.
     *
     * @param appName Name of the tool.
     * @param appView View of the tool.
     * @param sessionId Session id.
     * @param ip IP of the user.
     */
    @Deprecated
    void logTool(String appName, String appView, String sessionId, String ip);

    /**
     * Calls method to save a tool error which is a call of a non existing tool
     * or a non existing tool view.
     *
     * @param appName Name of the tool.
     * @param appView View of the tool.
     * @param ip IP of the user.
     */
    @Deprecated
    void logToolError(String appName, String appView, String ip);

    /**
     * Getter for the view name / tool name.
     *
     * @return name of the view / tool.
     */
    String getViewName();

    /**
     * Getter for the view type.
     *
     * @return type of the view.
     */
    String getViewType();

    /**
     * Setter for the view name / tool name.
     *
     * @param viewName name of the view / tool.
     */
    void setViewName(String viewName);

    /**
     * Setter for the view type.
     *
     * @param viewType type of the view.
     */
    void setViewType(String viewType);
}
