/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.administration.beans;

import de.unibi.cebitec.bibiserv.statistics.charts.ChartCalc;
import de.unibi.cebitec.bibiserv.statistics.ws.Stats;
import de.unibi.cebitec.bibiserv.statistics.ws.response.StatsMethodEnum;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Armin Toepfer atoepfer(at)cebitec.uni-bielefeld.de
 */
@ManagedBean(name = "chartBean")
@SessionScoped
public class ChartBean {

    private String timepoint = "Today";
    private String timerange = "24H";
    private List<String> timepoints = new LinkedList<String>();
    
    private String category = StatsMethodEnum.CATEGORYCLICKS.toString().toLowerCase();
    
    private PieChartModel pcm;
    private CartesianChartModel ccm;
    
    private String type = "pie";
    
    @ManagedProperty(value = "#{chartCalc}")
    private ChartCalc calc;
    
    @ManagedProperty(value = "#{statsBean}")
    private Stats statsBean;

    public void updateChart() {
        if (type.equals("pie")) {
            this.pcm = this.calc.updatePieChart(timepoint,category);
        } else if (type.equals("line")) {
            this.ccm = this.calc.updateCartesianChart(timerange,category);
        } else if (type.equals("bar") || type.equals("column")) {
            this.ccm = this.calc.updateCartesianChart(timepoints,category);
        }
    }

    public void typeChanged(ValueChangeEvent e) {
        this.type = e.getNewValue().toString();
        this.updateChart();
    }

    public void timerangeChanged(ValueChangeEvent e) {
        this.timerange = e.getNewValue().toString();
        this.updateChart();
    }

    public void timepointsChanged(ValueChangeEvent e) {
        this.timepoint = e.getNewValue().toString();
        this.updateChart();
    }
    
    public void timepointChanged(ValueChangeEvent e) {
        this.updateChart();
    }

    public void categoryChanged(ValueChangeEvent e) {
        this.category = e.getNewValue().toString();
        this.updateChart();
      
    }

    public String getCategory() {
        return category;
    }

    public String getTimerange() {
        return timerange;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTimerange(String timerange) {
        this.timerange = timerange;
    }

    public PieChartModel getPieChartModel() {
        if (this.pcm == null) {
            this.updateChart();
        }
        return pcm;
    }

    public CartesianChartModel getCartesianChartModel() {
        if (this.ccm == null) {
            this.updateChart();
        }
        return ccm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTimepoint(String timepoint) {
        this.timepoint = timepoint;
    }

    public String getTimepoint() {
        return timepoint;
    }

    public ChartCalc getCalc() {
        return calc;
    }

    public void setCalc(ChartCalc calc) {
        this.calc = calc;
    }

    public List<String> getTimepoints() {
        return timepoints;
    }

    public void setTimepoints(List<String> timepoints) {
        this.timepoints = timepoints;
        this.updateChart();
    }

    public Stats getStatsBean() {
        return statsBean;
    }

    public void setStatsBean(Stats statsBean) {
        this.statsBean = statsBean;
    }
    
    public List<String> getTools() {
        return this.statsBean.toolList();
    }
}