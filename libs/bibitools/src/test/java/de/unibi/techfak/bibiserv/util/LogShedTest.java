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

package de.unibi.techfak.bibiserv.util;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jkrueger
 */
public class LogShedTest {

    private LogShed logshed;

    public LogShedTest() {
        logshed = new LogShed();
        logshed.error("one");
        logshed.fatal("two");
        logshed.info("three");
        logshed.warn("four");
    }

    @Before
    public void before() {
        logshed.setLogLevel(LogShed.LogLevel.info);
    }

    @Test
    public void testGetLogList() {
        System.out.println("getLogList ");
        assertTrue(logshed.getLogList().size() == 4);
        logshed.setLogLevel(LogShed.LogLevel.fatal);
        assertTrue(logshed.getLogList().size() == 1);
        logshed.setLogLevel(LogShed.LogLevel.warn);
        assertTrue(logshed.getLogList().size() == 3);
        logshed.setLogLevel(LogShed.LogLevel.error);
        assertTrue(logshed.getLogList().size() == 2);

    }

    @Test
    public void testtoString() {
        System.out.println("toString (also getLogList, enableTimeStamp)");
        String result = logshed.toString();
        String expected = "[error] - one" + System.getProperty("line.separator")
                + "[fatal] - two" + System.getProperty("line.separator")
                + "[info] - three" + System.getProperty("line.separator")
                + "[warn] - four" + System.getProperty("line.separator");
        assertEquals(result, expected);

    }
}
