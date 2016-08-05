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

import de.unibi.cebitec.bibiserv.statistics.ws.Stats;

/**
 * Enum for the different statistical types a user may request.
 * 
 * @author Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 */
public enum StatsMethodEnum {
    CATEGORYCLICKS() {
        @Override
        public StatsWsReponse getResult(Stats stats, String timerange, String... toolname) {
            return stats.categoryclicks(timerange);
        }
    },
    TOOLCLICKS() {
        @Override
        public StatsWsReponse getResult(Stats stats, String timerange, String... toolname) {
            return stats.toolclicks(timerange);
        }
    },
    SUBMISSIONS() {
        @Override
        public StatsWsReponse getResult(Stats stats, String timerange, String... toolname) {
            return stats.submissions(timerange);
        }
    },
//    SUBMISSIONS_COMPLETE,
    SUBMISSIONS_EXA() {
        @Override
        public StatsWsReponse getResult(Stats stats, String timerange, String... toolname) {
            return stats.submissions_exa(timerange);
        }
    },
    SUBMISSIONS_STD() {
        @Override
        public StatsWsReponse getResult(Stats stats, String timerange, String... toolname) {
            return stats.submissions_std(timerange);
        }
    },
    SUBMISSIONS_EXA_STD() {
        @Override
        public StatsWsReponse getResult(Stats stats, String timerange, String... toolname) {
            return stats.submissions_exa_std(timerange);
        }
    },
    BROWSER() {
        @Override
        public StatsWsReponse getResult(Stats stats, String timerange, String... toolname) {
            return stats.browser(timerange);
        }
    },
    OS() {
        @Override
        public StatsWsReponse getResult(Stats stats, String timerange, String... toolname) {
            return stats.os(timerange);
        }
    },
    RESOLUTION() {
        @Override
        public StatsWsReponse getResult(Stats stats, String timerange, String... toolname) {
            return stats.resolution(timerange);
        }
    },
    SINGLETOOL() {
        @Override
        public StatsWsReponse getResult(Stats stats, String timerange, String... toolname) {
            return stats.singleTool(timerange, toolname[0]);
        }
    };
    
    public abstract StatsWsReponse getResult(Stats stats, String timerange, String... toolname);
}
