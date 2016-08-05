package de.unibi.cebitec.bibiserv.statistics.charts;

import de.unibi.cebitec.bibiserv.statistics.logging.database.DBGetter;
import de.unibi.cebitec.bibiserv.statistics.logging.database.MySQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;

/**
 * Bean for user statistics.
 *
 * @author jschmolke, gatter
 */
public class UserStatsBean {


    private DBGetter dbget;

    //absolute min/max from DB
    private Date absMinDate;
    private Date absMaxDate;
    //min/max from calendars
    private Date minDate;
    private Date maxDate;

    //selected item from selectonemenu in worldmaptab
    private int selectedItem = 0;
    private String activeTabtitle;
    private TreeMap<String, DatabaseTool> tools;
    private Map<String, Map<Long, Integer>> chartSeries;
    private HashMap<String, Integer> toolnameIndexes;
    //will be shown in datatable
    private LazyDataModel<DatabaseTool> lazyModel;
    //load DBentries in this
    private ArrayList<DatabaseEntry> dbEntries;
    //Tool Date Count
    //private TreeMap<Tuple<String, Long>, Integer> dateCounts;

    private TreeMap<String, Integer> countries;
    private TreeMap<String, Integer> os;
    private TreeMap<String, Integer> browsers;

    //overall top values
    private int total;
    private String topTool;
    private int tPerc;
    private String topCountry;
    private int cPerc;
    private String topOS;
    private int osPerc;
    private String topBrowser;
    private int brPerc;
    
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(UserStatsBean.class.getName());

    /**
     * Standardconstructor.
     */
    public UserStatsBean() {
        
        dbget = new DBGetter();
        
        //dateCounts = new TreeMap<>();
        countries = new TreeMap<>();
        os = new TreeMap<>();
        browsers = new TreeMap<>();
        tools = new TreeMap<>();
        dbEntries = new ArrayList<>();
        chartSeries = new HashMap<>();
        toolnameIndexes = new HashMap<>();
        // inital date values
        
        
        Calendar cal = Calendar.getInstance();
       
        maxDate = new Date(cal.getTimeInMillis());
        absMaxDate = new Date(cal.getTimeInMillis());
        
        minDate = new Date(decreaseOneMonth(cal).getTimeInMillis());
           
        cal.clear();
        cal.set(2014,0,1);
        absMinDate= new Date(cal.getTimeInMillis());
        
    }
    
    /**
     * Gets called when min/max gets changed. Updates chart and builds
     * json-string for the chart.
     *
     */
    public void getPoints() {
        String json = buildJson(chartSeries);

        RequestContext reqCtx = RequestContext.getCurrentInstance();
        reqCtx.addCallbackParam("series", json);
    }

    /**
     * Builds a json string to pass as data to the view.
     *
     * @param filteredPoints List of points.
     * @return String of data as json.
     */
    private String buildJson(Map<String, Map<Long, Integer>> filteredPoints) {

        StringBuilder str = new StringBuilder();
        str.append("[");

        boolean outerfirst = true;
        for (Entry<String,Map<Long,Integer>> entry : filteredPoints.entrySet()) {
            if (outerfirst) {
                outerfirst = false;
            } else {
                str.append(", ");
            }
            str.append("{ \"name\": \"").append(entry.getKey()).append("\",");
            str.append("\"data\": [");
            boolean innerfirst = true;
            Map<Long,Integer> points = entry.getValue();
            ArrayList<Long> list = new ArrayList<>(points.keySet());
            Collections.sort(list);
            for(Long l : list){  
            if (innerfirst) {
                    innerfirst = false;
                } else {
                    str.append(", ");
                }
                str.append("[").append(l).append(", ").append(points.get(l)).append("]");
            }
            str.append("] }");
        }

        str.append("]");
        return str.toString();
    }

    /**
     * Gets called when tab changed to worldmap or if selectmenu was changed in
     * worldmaptab. Calculates the counts per country and parses them into a
     * json for the highmaps data.
     */
    public void updateWorldmap() {

        try {
            if (selectedItem == 0) {
                dbEntries = dbget.getToolusageList(minDate, maxDate);
                //absMinDate = dbget.getAbsMinDateUsage();
            } else if (selectedItem == 1) {
                dbEntries = dbget.getDownloadList(minDate, maxDate);
                //absMinDate = dbget.getAbsMinDateDownload();
            } else {
                dbEntries = dbget.getToolclickList(minDate, maxDate);
                //absMinDate = dbget.getAbsMinDateClicks();
            }
        } catch (MySQLException e) {

            log.error("DBGetter getXXXList() failed!",e);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Sorry", "Something went wrong! Please contact support if problem persists."));
            return;
        }

        //We only need countries for the worldmap
        countries.clear();
        for (DatabaseEntry entry : dbEntries) {
            addCountry(entry.getCountry());
        }
        StringBuilder str = new StringBuilder();
        str.append("[");

        boolean innerfirst = true;

        for (Entry<String, Integer> entry : countries.entrySet()) {

            if (innerfirst) {
                innerfirst = false;
            } else {
                str.append(", ");
            }

            str.append("{ \"code\": \"").append(entry.getKey()).append("\",");
            str.append("\"value\": ").append(entry.getValue());
            str.append("}");
        }

        str.append("]");

        RequestContext reqCtx = RequestContext.getCurrentInstance();
        reqCtx.addCallbackParam("series", str.toString());
    }

    /**
     * Calculates the statistical values e.g. top values.
     */
    private void calcTools() {
        //clear all maps
        tools.clear();
        chartSeries.clear();
        countries.clear();
        browsers.clear();
        os.clear();

        DatabaseEntry de;
        String name;
        //Set up tools
        for (int i = 0; i < dbEntries.size(); i++) {
            de = dbEntries.get(i);
            name = de.getToolname();
            if (!tools.containsKey(name)) {

                tools.put(name, new DatabaseTool(name, de.getCountry(), de.getOs(), de.getBrowser(), de.getBrowserVersion()));
            } else {

                DatabaseTool dt = tools.get(name);
                dt.incTotal();
                dt.addCountry(de.getCountry());
                dt.addOS(de.getOs());
                dt.addBrowser(de.getBrowser(), de.getBrowserVersion());
            }

            if(chartSeries.containsKey(de.getToolname())){
                Map<Long,Integer> dates = chartSeries.get(de.getToolname());
                if(dates.containsKey(de.getDate().getTime())){
                    dates.put(de.getDate().getTime(), dates.get(de.getDate().getTime())+1);
                }else{
                    dates.put(de.getDate().getTime(), 1);
                }
            }else{
                chartSeries.put(de.getToolname(), new HashMap<Long,Integer>());
                chartSeries.get(de.getToolname()).put(de.getDate().getTime(), 1);
            }
            addCountry(de.getCountry());
            addOS(de.getOs());
            addBrowser(de.getBrowser());
        }

        //calculate top values for each tool and overall top values
        total = dbEntries.size();
        //best total of tool
        int topCount = 0;
        topTool = "N/A";
        tPerc = 0;
        for (Entry<String, DatabaseTool> tool : tools.entrySet()) {
            tool.getValue().calcAllTopValues();
            if (tool.getValue().getTotal() > topCount) {
                topTool = tool.getKey();
                topCount = tool.getValue().getTotal();
                tPerc = tool.getValue().getTotal() * 100 / total;
            }
        }

        Entry<String, Integer> maxEntry = null;
        //overall topCountry
        if (!countries.isEmpty()) {
            for (Entry<String, Integer> entry : countries.entrySet()) {
                if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                    maxEntry = entry;
                }
            }
            if (maxEntry != null) {
                topCountry = maxEntry.getKey();
                cPerc = maxEntry.getValue() * 100 / total;
            } else {
                topCountry = "N/A";
                cPerc = 0;
            }

        }

        //overall topOS
        if (!os.isEmpty()) {
            maxEntry = null;
            for (Entry<String, Integer> entry : os.entrySet()) {
                if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                    maxEntry = entry;
                }
            }
            if (maxEntry != null) {
                topOS = maxEntry.getKey();
                osPerc = maxEntry.getValue() * 100 / total;
            } else {
                topOS = "N/A";
                osPerc = 0;
            }

        }

        //overall topBrowser
        if (!browsers.isEmpty()) {
            maxEntry = null;
            for (Entry<String, Integer> entry : browsers.entrySet()) {
                if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                    maxEntry = entry;
                }
            }
            if (maxEntry != null) {
                topBrowser = maxEntry.getKey();
                brPerc = maxEntry.getValue() * 100 / total;
            } else {
                topBrowser = "N/A";
                brPerc = 0;
            }
        }

    }

    /**
     * Adds the country to the Treemap.
     *
     * @param name Name of the country.
     */
    public void addCountry(String name) {
        if (countries.containsKey(name)) {
            countries.put(name, countries.get(name) + 1);
        } else {
            countries.put(name, 1);
        }
    }

    /**
     * Adds the operating system to the Treemap.
     *
     * @param name Name of OS.
     */
    public void addOS(String name) {
        if (os.containsKey(name)) {
            os.put(name, os.get(name) + 1);
        } else {
            os.put(name, 1);
        }
    }

    /**
     * Adds the browser to the Treemap.
     *
     * @param name Browsername.
     */
    public void addBrowser(String name) {
        if (browsers.containsKey(name)) {
            browsers.put(name, browsers.get(name) + 1);
        } else {
            browsers.put(name, 1);
        }
    }

    /**
     * Gets called when the tab changed. Calls updateActiveTab().
     *
     * @param event The event which was fired.
     */
    public void onTabChange(TabChangeEvent event) {
        activeTabtitle = event.getTab().getTitle();
        updateActiveTab();
    }

    /**
     * Gets data from db and sets up the activetab. Gets called after tab
     * changed or calender changed.
     */
    private void updateActiveTab() {
        switch (activeTabtitle) {
            case "Worldmap":
                updateWorldmap();
           
                break;
            case "Downloads":
                try {
                    dbEntries = dbget.getDownloadList(minDate, maxDate);
                    //absMinDate = dbget.getAbsMinDateDownload();
                 
                    calcTools();
                    getPoints();
                    lazyModel = new LazyDatabaseToolModel((new ArrayList<>(tools.values())));
                } catch (MySQLException ex) {
                    log.error("getDownloadList() failed!",ex);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.addMessage(null, new FacesMessage("Sorry", "Something went wrong! Please contact support if problem persists."));
                }

                break;
            case "Toolusages":
                try {
                    dbEntries = dbget.getToolusageList(minDate, maxDate);
                    //absMinDate = dbget.getAbsMinDateUsage();
                
                    calcTools();
                    getPoints();
                    lazyModel = new LazyDatabaseToolModel((new ArrayList<>(tools.values())));

                } catch (MySQLException ex) {
                    log.error("getToolusageList() failed!",ex);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.addMessage(null, new FacesMessage("Sorry", "Something went wrong! Please contact support if problem persists."));   
                }
                break;
        }
    }

    /**
     * Gets the count of unknown countries.
     *
     * @return Number of unknown countries.
     */
    public int getUnknownCountries() {

        if (countries.containsKey("??")) {
            return countries.get("??");
        }
        return 0;
    }

    /**
     * Gets called when calendar for maxDate was clicked. No further check
     * necessary because mindate is set to minDate and inputText is readonly.
     *
     * @param event Fired event.
     */
    public void onMaxDateSelect(SelectEvent event) {
        this.maxDate = (Date)event.getObject();
        updateActiveTab();
    }

    /**
     * Gets called when calendar for minDate was clicked. No further check
     * necessary because maxdate is set to maxDate and inputText is readonly.
     *
     * @param event Fired event.
     */
    public void onMinDateSelect(SelectEvent event) {
        this.minDate = (Date)event.getObject();
        updateActiveTab();
    }

    /**
     * *************************************************************************
     * Getter/Setter
     *
     **************************************************************************
     */
    /**
     * Standardgetter.
     *
     * @return Current minimum date.
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * Standardsetter.
     *
     * @param minDate Date to set.
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
       
    }

    /**
     * Standardgetter.
     *
     * @return Current maximum date.
     */
    public Date getMaxDate() {
        return maxDate;
    }

    /**
     * Standardsetter.
     *
     * @param maxDate Date to set.
     */
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * Standardgetter.
     *
     * @return Name of active title in tabview.
     */
    public String getActiveTabtitle() {
        return activeTabtitle;
    }

    /**
     * Sets the title of the active tab.
     *
     * @param activeTabtitle Name of the tab.
     */
    public void setActiveTabtitle(String activeTabtitle) {
        this.activeTabtitle = activeTabtitle;
    }

    /**
     * Gets the tools Treemap.
     *
     * @return The treemap of tools.
     */
    public TreeMap<String, DatabaseTool> getTools() {
        return tools;
    }

    /**
     * Sets the Treemap.
     *
     * @param tools Treemap to set.
     */
    public void setTools(TreeMap<String, DatabaseTool> tools) {
        this.tools = tools;
    }
    
    /**
     * Gets the total number of downloads.
     *
     * @return Count of all downloads.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets the total number of all downloads.
     *
     * @param total Number to set.
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * The country which most downloads came from.
     *
     * @return The top country.
     */
    public String getTopCountry() {
        return topCountry;
    }

    /**
     * Sets the top country.
     *
     * @param topCountry Name to set.
     */
    public void setTopCountry(String topCountry) {
        this.topCountry = topCountry;
    }

    /**
     * Gets the top tool determined by its total.
     *
     * @return Name of tool.
     */
    public String getTopTool() {
        return topTool;
    }

    /**
     * Standardsetter.
     *
     * @param topTool Name of tool.
     */
    public void setTopTool(String topTool) {
        this.topTool = topTool;
    }

    /**
     * Standardgetter.
     *
     * @return Percentage of top tool.
     */
    public int gettPerc() {
        return tPerc;
    }

    /**
     * Standardsetter.
     *
     * @param tPerc Percentage of top tool.
     */
    public void settPerc(int tPerc) {
        this.tPerc = tPerc;
    }

    /**
     * Gets the operating system from which the most downloads came from.
     *
     * @return Name of the top OS.
     */
    public String getTopOS() {
        return topOS;
    }

    /**
     * Sets the top OS.
     *
     * @param topOS Name of top OS.
     */
    public void setTopOS(String topOS) {
        this.topOS = topOS;
    }

    /**
     * Gets the treemap with all countries.
     *
     * @return Treemap of all countries.
     */
    public TreeMap<String, Integer> getCountries() {
        return countries;
    }

    /**
     * Sets the treemap.
     *
     * @param countries treemap with all countries.
     */
    public void setCountries(TreeMap<String, Integer> countries) {
        this.countries = countries;
    }

    /**
     * Gets the Treemap of operating system name linked with number of
     * downloads.
     *
     * @return Treemap of operating systems.
     */
    public TreeMap<String, Integer> getOs() {
        return os;
    }

    /**
     * Sets the Treemap of operating systems.
     *
     * @param os The Treemap to set.
     */
    public void setOs(TreeMap<String, Integer> os) {
        this.os = os;
    }

    /**
     * Gets the percentage of the top country.
     *
     * @return Percent as integer.
     */
    public int getcPerc() {
        return cPerc;
    }

    /**
     * Sets the country percentage.
     *
     * @param cPerc Top percentage value.
     */
    public void setcPerc(int cPerc) {
        this.cPerc = cPerc;
    }

    /**
     * Gets the percentage of the top OS.
     *
     * @return Percentage as integer.
     */
    public int getOsPerc() {
        return osPerc;
    }

    /**
     * Sets the percentage of top OS.
     *
     * @param osPerc Percentage to set.
     */
    public void setOsPerc(int osPerc) {
        this.osPerc = osPerc;
    }

    /**
     * Standardgetter.
     *
     * @return Index of selected item.
     */
    public int getSelectedItem() {
        return selectedItem;
    }

    /**
     * Standardsetter.
     *
     * @param selectedItem Index of selected item.
     */
    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * Standardgetter.
     *
     * @return Map of names and der indices.
     */
    public HashMap<String, Integer> getToolnameIndexes() {
        return toolnameIndexes;
    }

    /**
     * Standardsetter.
     *
     * @param toolnameIndexes Map to set.
     */
    public void setToolnameIndexes(HashMap<String, Integer> toolnameIndexes) {
        this.toolnameIndexes = toolnameIndexes;
    }

    /**
     * Standardgetter.
     *
     * @return Map of browsers and their clicks.
     */
    public TreeMap<String, Integer> getBrowsers() {
        return browsers;
    }

    /**
     * Standardsetter.
     *
     * @param browsers Map to set.
     */
    public void setBrowsers(TreeMap<String, Integer> browsers) {
        this.browsers = browsers;
    }

    /**
     * Standardgetter.
     *
     * @return Name of top browser.
     */
    public String getTopBrowser() {
        return topBrowser;
    }

    /**
     * Standardsetter.
     *
     * @param topBrowser Name of browser.
     */
    public void setTopBrowser(String topBrowser) {
        this.topBrowser = topBrowser;
    }

    /**
     * Standardgetter.
     *
     * @return Percentage of the top browser.
     */
    public int getBrPerc() {
        return brPerc;
    }

    /**
     * Standardsetter.
     *
     * @param brPerc Percentage to set.
     */
    public void setBrPerc(int brPerc) {
        this.brPerc = brPerc;
    }

    /**
     * Standardgetter.
     *
     * @return Model for datatable.
     */
    public LazyDataModel<DatabaseTool> getLazyModel() {
        return lazyModel;
    }

    /**
     * Standardsetter.
     *
     * @param lazyModel Model to set.
     */
    public void setLazyModel(LazyDataModel<DatabaseTool> lazyModel) {
        this.lazyModel = lazyModel;
    }

    /**
     * Standardgetter.
     *
     * @return List of series for the linechart.
     */
    public Map<String, Map<Long, Integer>> getChartSeries() {
        return chartSeries;
    }

    /**
     * Standardsetter.
     *
     * @param chartSeries List to set.
     */
    public void setChartSeries(Map<String, Map<Long, Integer>> chartSeries) {
        this.chartSeries = chartSeries;
    }

    /**
     * Standardsetter.
     *
     * @return The absolute minimum date of the data.
     */
    public Date getAbsMinDate() {
        return absMinDate;
    }

    /**
     * Standardsetter.
     *
     * @param absMinDate Date to set as minimum.
     */
    public void setAbsMinDate(Date absMinDate) {
        this.absMinDate = absMinDate;
    }

    /**
     * Standardgetter.
     *
     * @return The absolute maximum date of the data.
     */
    public Date getAbsMaxDate() {
        return absMaxDate;
    }

    /**
     * Standardsetter.
     *
     * @param absMaxDate Maximum date to set.
     */
    public void setAbsMaxDate(Date absMaxDate) {
        this.absMaxDate = absMaxDate;
    }
    
    
    private Calendar decreaseOneMonth(Calendar current){
        Calendar cal = (Calendar)current.clone();
        
        
        int y = current.get(Calendar.YEAR);
        int m = current.get(Calendar.MONTH);
        int d = current.get(Calendar.DAY_OF_MONTH);
        
        if (m > 0) {
            m--;
        } else {
            m = 11;
            y--;
        }
        
        if (m == Calendar.FEBRUARY) {
            if (y % 4 == 0) { // Schaltjahr -> 29 Tage
                if (d > 29) {
                    d = 29;
                }
            } else {
                if (d > 28) {
                    d = 28;
                }
            }
        }
        
        if (m == Calendar.APRIL || m == Calendar.JUNE || m == Calendar.SEPTEMBER || m == Calendar.NOVEMBER) {
            if (d > 30) {
                d = 30;
            }
        }
        
        cal.set(y,m,d);
        
        return cal;
    }
}
