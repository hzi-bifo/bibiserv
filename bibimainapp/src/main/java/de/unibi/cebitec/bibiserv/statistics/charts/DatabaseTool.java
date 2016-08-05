package de.unibi.cebitec.bibiserv.statistics.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * This class is a container for calculating toolstatistics.
 * The correct use would be to create an object of this class and then e.g. 
 * for every toolentry you add the given information with the add methods (country,os...)
 * and call incTotal() for each entry.
 * Be aware that you must call the calculate method on the created object
 * to have the top values set. 
 * If you follow the steps correctly you can be sure everything will be counted
 * correctly.
 * Do not forget to update the sorter and model if you change
 * something on the variables.
 * @author jschmolke
 */
public class DatabaseTool {

    private String toolname;
    private int total;
    //country
    private String topCountry;
    private int cPerc;
    //os
    private String topOS;
    private int osPerc;  
    //browser
    private String topBrowser;
    private int brPerc;
    
    //Map of all countries
    private Map<String, Integer> countries;
    //Map of all operating systems
    private Map<String, Integer> os;
    //Map of browsers with total count, and subversions and their totals
    private Map<String, Tuple<Integer, Map<String, Integer>>> browsers;

    /**
     * Constructor.
     *
     * @param name Name of tool.
     */
    public DatabaseTool(String name) {
        this.toolname = name;
        this.os = new HashMap<>();
        this.countries = new TreeMap<>();
        this.browsers = new HashMap<>();
    }

    /**
     * Standardconstructor
     *
     * @param name Name of tool.
     * @param country Name of the country the tool was downloaded from.
     * @param os Name of operating system.
     * @param browser Name of browser.
     * @param brVersion Version of the browser.
     */
    public DatabaseTool(String name, String country, String os, String browser, String brVersion) {
        this.toolname = name;
        this.topCountry = country;
        this.cPerc = 100;
        this.topOS = os;
        this.osPerc = 100;
        this.topBrowser=browser;
        this.brPerc=100;
        this.total = 1;
        this.os = new HashMap<>();
        this.os.put(os, 1);
        this.countries = new TreeMap<>();
        this.countries.put(country, 1);
        this.browsers = new HashMap<>();
        TreeMap<String, Integer> tmp = new TreeMap<>();
        tmp.put(brVersion, 1);
        this.browsers.put(browser, new Tuple(1, tmp));
    }

    /**
     * Increases the total count of downloads.
     */
    public void incTotal() {
        this.total++;
    }

    /**
     * Adds a country to the hashmap and increases its count.
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
     * Adds an operating system to the hashmap and increases its count.
     *
     * @param name
     */
    public void addOS(String name) {
        if (os.containsKey(name)) {
            os.put(name, os.get(name) + 1);
        } else {
            os.put(name, 1);
        }
    }

    /**
     * Adds a browser and his version to the hashmap and increases their count.
     *
     * @param browser Browsername.
     * @param brVersion Browserversion.
     */
    public void addBrowser(String browser, String brVersion) {
        if (browsers.containsKey(browser)) {
            Tuple<Integer, Map<String, Integer>> bro = browsers.get(browser);
            bro.setFirst(bro.getFirst()+1);
            if (bro.getSecond().containsKey(brVersion)) {
                bro.getSecond().put(brVersion, bro.getSecond().get(brVersion)+1);
            }else{
                bro.getSecond().put(brVersion, 1);
            }
        } else {
            TreeMap<String, Integer> tmp = new TreeMap<>();
            tmp.put(brVersion, 1);
            this.browsers.put(browser, new Tuple(1, tmp));
        }
    }

    /**
     * Calculates the top values for this tool.
     */
    public void calcAllTopValues() {
        Entry<String, Integer> maxEntry = null;
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
        
        Tuple<String,Integer> mxEntry;
        if (!browsers.isEmpty()) {
            mxEntry = null;
            for (Entry<String, Tuple<Integer,Map<String,Integer>>> entry : browsers.entrySet()) {
                if (mxEntry == null || entry.getValue().getFirst() > mxEntry.getSecond()) {
                    mxEntry = new Tuple(entry.getKey(),entry.getValue().getFirst());
                }
            }
            if (mxEntry != null) {
                topBrowser = mxEntry.getFirst();
                brPerc = mxEntry.getSecond() * 100 / total;
            } else {
                topBrowser = "N/A";
                brPerc = 0;
            }

        }

    }
    
/*******************************************************************************
 * 
 * Getter and Setter
 * 
 ******************************************************************************/

    /**
     * Gets the name of the tool.
     *
     * @return Name as string.
     */
    public String getToolname() {
        return toolname;
    }

    /**
     * Sets the name of the tool.
     *
     * @param toolname Name as string.
     */
    public void setToolname(String toolname) {
        this.toolname = toolname;
    }

    /**
     * Gets the name of the top country.
     *
     * @return Name of top country.
     */
    public String getTopCountry() {
        return topCountry;
    }

    /**
     * Sets the name of the top country.
     *
     * @param topCountry name as string.
     */
    public void setTopCountry(String topCountry) {
        this.topCountry = topCountry;
    }

    /**
     * Gets the name of the top operating system.
     *
     * @return Name as string.
     */
    public String getTopOS() {
        return topOS;
    }

    /**
     * Sets the name of the top OS.
     *
     * @param topOS Name as string.
     */
    public void setTopOS(String topOS) {
        this.topOS = topOS;
    }

    /**
     * Gets the total number of downloads of this tool.
     *
     * @return Number of downloads.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets the total number of downloads.
     *
     * @param total Count as integer.
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Standardgetter.
     * @return Name of top browser.
     */
    public String getTopBrowser() {
        return topBrowser;
    }

    /**
     * Standardsetter.
     * @param topBrowser Name of browser.
     */
    public void setTopBrowser(String topBrowser) {
        this.topBrowser = topBrowser;
    }

    /**
     * Percentage of top browser.
     * @return Percent as integer.
     */
    public int getBrPerc() {
        return brPerc;
    }

    /**
     * Setter for percentage of top browser.
     * @param brPerc Percent as integer.
     */
    public void setBrPerc(int brPerc) {
        this.brPerc = brPerc;
    }

    /**
     * Gets all information about the browsers.
     * @return The hashmap.
     */
    public Map<String, Tuple<Integer, Map<String, Integer>>> getBrowsers() {
        return browsers;
    }

    /**
     * Standardsetter.
     * @param browsers Map to set.
     */
    public void setBrowsers(Map<String, Tuple<Integer, Map<String, Integer>>> browsers) {
        this.browsers = browsers;
    }

    /**
     * Gets the percentage of the top country.
     *
     * @return Percentage as int.
     */
    public int getcPerc() {
        return cPerc;
    }

    /**
     * Sets the percentage of the top country.
     *
     * @param cPerc Percentage as integer.
     */
    public void setcPerc(int cPerc) {
        this.cPerc = cPerc;
    }

    /**
     * Gets the percentage of the top operating system.
     *
     * @return Percentage as integer.
     */
    public int getOsPerc() {
        return osPerc;
    }

    /**
     * Sets the percentage of the top os.
     *
     * @param osPerc Percentage as integer.
     */
    public void setOsPerc(int osPerc) {
        this.osPerc = osPerc;
    }

    /**
     * Gets the hashmap of all countries with their number of downloads.
     *
     * @return The hashmap.
     */
    public Map<String, Integer> getCountries() {
        return countries;
    }

    /**
     * Sets the hashmap of countries which downloaded the tool.
     *
     * @param countries Hashmap to set.
     */
    public void setCountries(TreeMap<String, Integer> countries) {
        this.countries = countries;
    }

    /**
     * Gets the hashmap of all operating systems which downloaded the tool and
     * their number of downloads.
     *
     * @return The hashmap.
     */
    public Map<String, Integer> getOs() {
        return os;
    }

    /**
     * Sets the hashmap of operating systems.
     *
     * @param os The hashmap to set.
     */
    public void setOs(TreeMap<String, Integer> os) {
        this.os = os;
    } 
    
    /**
     * Gets the countries as ordered list of entries.
     * Needed as Primefaces DataTable cannot work with HashMaps.
     * @return List of entries.
     */
    public ArrayList<Entry<String,Integer>> getCountryList(){
        ArrayList tmp  = new ArrayList<>(this.countries.entrySet());
        Collections.sort(tmp,Entry_StringInteger_Comparator);
        return tmp;
    }
    
    /**
     * Gets the os-map as ordered list of entries.
     * Needed as Primefaces DataTable cannot work with HashMaps.
     * @return List of entries.
     */
    public ArrayList<Entry<String,Integer>> getOsList(){
        ArrayList tmp = new ArrayList<>(this.os.entrySet());
        Collections.sort(tmp,Entry_StringInteger_Comparator);
        return tmp;
    }
    
    /**
     * Gets the browsers as ordered list of entries.
     * Needed as Primefaces DataTable cannot work with HashMaps.
     * @return List of entries.
     */
    public ArrayList<Entry<String,Tuple<Integer,Map<String,Integer>>>> getBrowserList(){
        ArrayList tmp = new ArrayList<>(this.browsers.entrySet());
        Collections.sort(tmp,Entry_Tupel_Comparator);
        return tmp;
    }
    
    
    /* Comparator used order Entries of Type <String,Integer> in descending order */
    private static Comparator Entry_StringInteger_Comparator = new Comparator<Entry<String,Integer>>() {

            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return -o1.getValue().compareTo(o2.getValue());
            }
        };
    
    /* Comparator used order Entries of Type <String,Tuple<Integer,T>> in descending order */
    private static Comparator Entry_Tupel_Comparator = new Comparator<Entry<String,Tuple<Integer,Map<String,Integer>>>>() {

            @Override
            public int compare(Entry<String,Tuple<Integer,Map<String,Integer>>> o1, Entry<String,Tuple<Integer,Map<String,Integer>>> o2) {
                return -o1.getValue().getFirst().compareTo(o2.getValue().getFirst());
            }
        };
    
}
