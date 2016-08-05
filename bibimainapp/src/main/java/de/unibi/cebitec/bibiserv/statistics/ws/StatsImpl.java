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
package de.unibi.cebitec.bibiserv.statistics.ws;

import de.unibi.cebitec.bibiserv.statistics.ws.response.StatsMethodEnum;
import de.unibi.cebitec.bibiserv.statistics.ws.response.StatsWsReponse;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.apache.log4j.Logger;

/**
 * Web service endpoint implementation for
 * de.unibi.cebitec.bibiserv.statistics.ws.Stats
 * 
 * @author Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 */
@ManagedBean(name = "statsBean")
@ApplicationScoped
public class StatsImpl implements Stats {

    private final static Logger LOG = Logger.getLogger("de.unibi.cebitec.bibiserv.statistics");
    private StatsConnector statsConnector = new StatsConnector();

    //Debug only
    public StatsImpl() {
        LOG.debug("Stats WS init");
    }

    @Override
    public StatsWsReponse categoryclicks(String timeranges) {
        return this.statsConnector.getStats(StatsMethodEnum.CATEGORYCLICKS, timeranges.split(","), null);
    }

    @Override
    public StatsWsReponse submissions(String timeranges) {
        return this.statsConnector.getStats(StatsMethodEnum.SUBMISSIONS, timeranges.split(","), null);
    }

    @Override
    public StatsWsReponse submissions_exa(String timeranges) {
        return this.statsConnector.getStats(StatsMethodEnum.SUBMISSIONS_EXA, timeranges.split(","), null);
    }

    @Override
    public StatsWsReponse submissions_std(String timeranges) {
        return this.statsConnector.getStats(StatsMethodEnum.SUBMISSIONS_STD, timeranges.split(","), null);
    }

    @Override
    public StatsWsReponse submissions_exa_std(String timeranges) {
        return this.statsConnector.getStats(StatsMethodEnum.SUBMISSIONS_EXA_STD, timeranges.split(","), null);
    }

    @Override
    public StatsWsReponse toolclicks(String timeranges) {
        return this.statsConnector.getStats(StatsMethodEnum.TOOLCLICKS, timeranges.split(","), null);
    }

    @Override
    public StatsWsReponse browser(String timeranges) {
        return this.statsConnector.getStats(StatsMethodEnum.BROWSER, timeranges.split(","), null);
    }

    @Override
    public StatsWsReponse os(String timeranges) {
        return this.statsConnector.getStats(StatsMethodEnum.OS, timeranges.split(","), null);
    }

    @Override
    public StatsWsReponse resolution(String timeranges) {
        return this.statsConnector.getStats(StatsMethodEnum.RESOLUTION, timeranges.split(","), null);
    }

    @Override
    public StatsWsReponse singleTool(String timeranges, String toolname) {
        return this.statsConnector.getStats(StatsMethodEnum.SINGLETOOL, timeranges.split(","), toolname);
    }

    @Override
    public List<String> toolList() {
        return this.statsConnector.toolList();
    }

    @Override
    public Result echo() {
        return new Result("bla");
    }
}
