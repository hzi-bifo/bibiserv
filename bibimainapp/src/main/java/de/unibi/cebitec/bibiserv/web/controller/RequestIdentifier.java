/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2015 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import org.apache.log4j.Logger;

/**
 * Parses /web/WEB-INF/classes/filterList.ssv and provides methods to identify
 * incoming requests. Enhanced with caching.
 * 
 * @author Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 *         Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
@ManagedBean("requestIdentifier")
@ApplicationScoped
public class RequestIdentifier {

    private static final int UPDATE_INTERVAL = 1000 * 60 * 5;
    private static final String FILTER_LIST_PATH = "filterList.ssv";
    private static final Logger log = Logger.getLogger("de.unibi.techfak.bibiserv.web.controller.RequestIdentifier");
    private List<String> prefixList = null;
    private List<String> substringList = null;
    private List<String> suffixList = null;
    private long lastParsed;

    public RequestIdentifier() {
        this.parse();
    }

    public final void parse() {
        this.lastParsed = System.currentTimeMillis();
        InputStream filterList = this.getClass().getClassLoader().getResourceAsStream(FILTER_LIST_PATH);
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new InputStreamReader(filterList));
            try {
                String line = null;
                FilterEnum state = null;
                while ((line = input.readLine()) != null) {
                    if (line.startsWith("#")) {
                        line = line.replaceFirst("#", "");
                        if (line.startsWith("prefix")) {
                            state = FilterEnum.PREFIX;
                        } else if (line.startsWith("substring")) {
                            state = FilterEnum.SUBSTRING;
                        } else if (line.startsWith("suffix")) {
                            state = FilterEnum.SUFFIX;
                        }
                    } else {
                        switch (state) {
                            case PREFIX:
                                prefixList = Arrays.asList(line.split(";"));
                                break;
                            case SUBSTRING:
                                substringList = Arrays.asList(line.split(";"));
                                break;
                            case SUFFIX:
                                suffixList = Arrays.asList(line.split(";"));
                                break;
                            default:
                                break;
                        }
                    }
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            log.error("Error parsing file ", ex);
        }
    }

    public boolean isHit(final String url) {
        if (System.currentTimeMillis() - lastParsed > UPDATE_INTERVAL) {
            this.parse();
        }
        boolean hit = false;
        for (String prefix : prefixList) {
            if (url.startsWith(prefix)) {
                return true;
            }
        }
        for (String substring : substringList) {
            if (url.contains(substring)) {
                return true;
            }
        }
        for (String suffix : suffixList) {
            if (url.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private enum FilterEnum {

        PREFIX,
        SUBSTRING,
        SUFFIX
    }
}
