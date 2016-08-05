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
package de.unibi.cebitec.bibiserv.statistics.ws.response;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract data type for one web service response.
 * Contains the requested statistical type and its results.
 * 
 * @author Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 */
public class StatsWsReponse {

    private StatsMethodEnum methodName;
    private List<SingleTimerangeResult> timerangeResults = new LinkedList<SingleTimerangeResult>();

    public StatsWsReponse() {
    }
    
    public StatsMethodEnum getMethodName() {
        return methodName;
    }

    public void setMethodName(StatsMethodEnum methodName) {
        this.methodName = methodName;
    }

    public List<SingleTimerangeResult> getTimerangeResults() {
        return timerangeResults;
    }

    public void addTimerangeResult(SingleTimerangeResult timerangeResult) {
        timerangeResults.add(timerangeResult);
    }

    public void setTimerangeResults(List<SingleTimerangeResult> timerangeResults) {
        this.timerangeResults = timerangeResults;
    }
}
