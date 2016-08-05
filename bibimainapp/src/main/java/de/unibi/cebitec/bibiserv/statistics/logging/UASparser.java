/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2014 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */

package de.unibi.cebitec.bibiserv.statistics.logging;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UserAgentInfo;
import java.io.IOException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * BeanWrapper for UASparser.  Make sure that UASparser init and dispose 
 * work out of box. Provide one public method to parse a single UserAgentString
 * 
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class UASparser implements InitializingBean, DisposableBean {

    private cz.mallat.uasparser.UASparser uasparser;
     
    /**
     * Parse a single UserAgent String and returns an UserAgentInfo object
     * 
     * @param uas - a single User Agent String
     * @return a UserAgentInfo object for further processing
     * 
     * @throws IOException in the case the String can't be read.
     */
    public UserAgentInfo parse(String uas) throws IOException{
        return uasparser.parse(uas);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
       uasparser = new cz.mallat.uasparser.UASparser(OnlineUpdater.getVendoredInputStream());    
    }

    @Override
    public void destroy() throws Exception {

    }
    
    
}
