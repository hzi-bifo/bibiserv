/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unibi.cebitec.bibiserv.server.manager.context;

import de.unibi.cebitec.bibiserv.server.manager.Core;
import javax.servlet.ServletContextEvent;
import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;

public class BiBiContextLoaderListener extends ContextLoaderListener {

    private static Logger log = Logger.getLogger("de.unibi.cebitec.bibiserv.server.manager.context");

    /**
     * Initialize the root web application context.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        log.debug("init Context in ContextLoaderListener");
        Core core = Core.getInstance();
        try {
            core.initalizeFromDB();
            core.initializeModulesFromDb();
        } catch (Exception e) {
            log.error("Core initialize from Database failed ... " + e.getMessage(),e);
        }
    }
}
