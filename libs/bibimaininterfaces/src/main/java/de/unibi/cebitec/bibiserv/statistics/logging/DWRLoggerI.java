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

/**
 * Acts as an information holder for client information provided by DWR.
 * 
 * @author Armin Toepfer (atoepfer@cebitec.uni-bielefeld.de)
 */
public interface DWRLoggerI {

    /**
     * Provides name of the browser.
     *
     * @return name of the browser.
     */
    String getBrowserName();

    /**
     * Provides screen height.
     *
     * @return screen height.
     */
    String getScreenHeight();

    /**
     * Provides screen width.
     *
     * @return screen width.
     */
    String getScreenWidth();

    /**
     * Provides user agent of the browser.
     *
     * @return browser's user agent.
     */
    String getUserAgent();

    /**
     * Provides name of the operation system.
     *
     * @return name of the operation system.
     */
    String getOs();

    /**
     * Provides version of the browser.
     *
     * @return version of the browser.
     */
    String getBrowserVersion();

    /**
     * Setter for all variables at once.
     *
     * @param screenWidth width of screen
     * @param screenHeight height of screen
     * @param userAgent userAgent gets split into browser version, browser name
     *  and os.
     */
    void setInfo(String screenWidth, String screenHeight, String userAgent);
}
