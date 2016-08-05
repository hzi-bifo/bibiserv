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
 * "Portions Copyrighted 2010 BiBiServ Curator Team"
 *
 * Contributor(s): Armin Toepfer
 *
 */
package de.unibi.cebitec.bibiserv.statistics.charts;

import de.unibi.cebitec.bibiserv.statistics.ws.Stats;
import de.unibi.cebitec.bibiserv.statistics.ws.response.SingleCategoryResult;
import de.unibi.cebitec.bibiserv.statistics.ws.response.SingleTimerangeResult;
import de.unibi.cebitec.bibiserv.statistics.ws.response.StatsMethodEnum;
import de.unibi.cebitec.bibiserv.statistics.ws.response.StatsWsReponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 */
@ManagedBean(name = "chartCalc")
@ApplicationScoped
public class ChartCalc {

    @ManagedProperty(value = "#{statsBean}")
    private Stats statsBean;

    public PieChartModel updatePieChart(String timepoint, String category) {
        PieChartModel pcm = new PieChartModel();
        final StatsWsReponse categoryclicks = chooseCategory(timepoint, category);
        if (categoryclicks != null
                && categoryclicks.getTimerangeResults() != null
                && categoryclicks.getTimerangeResults().size() >= 1
                && categoryclicks.getTimerangeResults().get(0).getSingleCategoryResults() != null) {
            List<SingleCategoryResult> scrs = categoryclicks.getTimerangeResults().get(0).getSingleCategoryResults();
            for (SingleCategoryResult scr : scrs) {
                pcm.set(scr.getCategory(), scr.getCount());
            }
        }
        return pcm;
    }

    public CartesianChartModel updateCartesianChart(String timerange, String category) {
        StringBuilder sb = new StringBuilder();
        if (timerange.equals("24H")) {
            for (int i = 0; i < 24; i += 2) {
                sb.append("24H").append(i).append(",");
            }
        } else if (timerange.equals("60M")) {
            for (int i = 0; i < 60; i += 5) {
                sb.append("60M").append(i).append(",");
            }
        }
        return createCartesianModel(sb.toString(), true, category);
    }

    public CartesianChartModel updateCartesianChart(List<String> timepoints, String category) {
        StringBuilder sb = new StringBuilder();
        for (String t : timepoints) {
            sb.append(t).append(",");
        }
        return createCartesianModel(sb.toString(), false, category);
    }

    private CartesianChartModel createCartesianModel(String input, boolean cutTimerange, String category) {
        CartesianChartModel ccm = new CartesianChartModel();
        StatsWsReponse response = chooseCategory(input, category);
        
        Map<String, ChartSeries> css = new HashMap<>();
        List<String> categories_all = new LinkedList<>();
        List<String> categories_notOoccured = new LinkedList<>();
        for (SingleTimerangeResult str : response.getTimerangeResults()) {
            for (SingleCategoryResult scr : str.getSingleCategoryResults()) {
                if (!categories_all.contains(scr.getCategory())) {
                    categories_all.add(scr.getCategory());
                }
            }
        }
        for (SingleTimerangeResult str : response.getTimerangeResults()) {
            categories_notOoccured = new LinkedList<>(categories_all);
            final String shortTimerange = cutTimerange ? str.getTimerange().substring(3) : str.getTimerange();
            //ccm.getCategories().add(shortTimerange);
            for (SingleCategoryResult scr : str.getSingleCategoryResults()) {
                if (!css.containsKey(scr.getCategory())) {
                    ChartSeries chartSeries = new ChartSeries(scr.getCategory());
                    css.put(scr.getCategory(), chartSeries);
                    ccm.addSeries(chartSeries);
                }
                css.get(scr.getCategory()).set(shortTimerange, scr.getCount());
                categories_notOoccured.remove(scr.getCategory());
            }
            for (String c : categories_notOoccured) {
                if (!css.containsKey(c)) {
                    ChartSeries chartSeries = new ChartSeries(c);
                    css.put(c, chartSeries);
                    ccm.addSeries(chartSeries);
                }
                css.get(c).set(shortTimerange, 0.0);
            }
        }
        return ccm;
    }

    private StatsWsReponse chooseCategory(String input, String category) {
        return StatsMethodEnum.valueOf(category.toUpperCase()).getResult(statsBean, input);
    }

    public Stats getStatsBean() {
        return statsBean;
    }

    public void setStatsBean(Stats statsBean) {
        this.statsBean = statsBean;
    }
}
