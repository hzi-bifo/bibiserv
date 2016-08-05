package de.unibi.cebitec.bibiserv.statistics.charts;

import java.util.Date;

/**
 * Container for one entry of the database.
 * Used by various beans to store the information of one row
 * for further usage in the statistics.
 * Do not forget to update the sorter and model if you
 * change something on the variables.
 * @author jschmolke
 */
public class DatabaseEntry {
    private String toolname;
    private Date date;
    private int status;
    private String os;
    private String country;
    private String browser;
    private String browserVersion;
    private int runtime;
    private String interfaceType;
    private String parameter;

    /**
     * Constructor for ToolStatusEntry
     * @param toolname Name of tool.
     * @param date Date of call.
     * @param status Statuscode.
     * @param os Operating system of user.
     * @param country Country of user.
     * @param browser Browser from user.
     * @param brVersion Version of browser.
     * @param runtime Runtime of job.
     * @param interfaceType Call came from (REST/Web)
     * @param parameter Parameter that were called.
     */
    public DatabaseEntry(String toolname, Date date, int status, String os, String country,String browser, String brVersion, int runtime, String interfaceType, String parameter) {
        this.toolname = toolname;
        this.date = date;
        this.status = status;
        this.os = os;
        this.country = country;
        this.browser = browser;
        this.browserVersion = brVersion;
        this.runtime = runtime;
        this.interfaceType = interfaceType;
        this.parameter = parameter;
    }
    
    /**
     * Constructor for DatabaseEntry.
     * @param toolname Name of tool
     * @param date Date of call
     * @param os Name of operating system.
     * @param country Name of country.
     * @param browser Name of browser.
     * @param brVersion Version of browser.
     */
    public DatabaseEntry(String toolname, Date date, String os, String country, String browser, String brVersion) {
        this.toolname = toolname;
        this.date = date;
        this.os = os;
        this.country = country;
        this.browser = browser;
        this.browserVersion = brVersion;
    }

/*******************************************************************************
 * 
 * Getter and Setter
 * 
 ******************************************************************************/    
    
    /**
     * Gets the name of the tool
     * the entry belongs to.
     * @return Name of tool.
     */
    public String getToolname(){
        return toolname;
    }

    /**
     * Sets the name of the tool.
     * @param toolname Name as string.
     */
    public void setToolname(String toolname) {
        this.toolname = toolname;
    }

    /**
     * Gets the date of toolcall in longformat.
     * @return Date as util.date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date of the call.
     * @param date Date as util.date.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the statuscode of the job.
     * @return Status as integer.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the statuscode of the job.
     * @param status Statuscode as integer.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the operating system.
     * @return Name as string.
     */
    public String getOs() {
        return os;
    }

    /**
     * Sets the name of the operating system.
     * @param os Name to set.
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * Gets the name of the country.
     * @return Name as string.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the name of the country.
     * @param country Name of country.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Standardgetter.
     * @return Name of browser.
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * Standardsetter.
     * @param browser Name of browser.
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * Standardgetter.
     * @return Name of browser version.
     */
    public String getBrowserVersion() {
        return browserVersion;
    }

    /**
     * Standardsetter.
     * @param browserVersion Name of version.
     */
    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    /**
     * Gets the runtime of the job.
     * @return Time as int.
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * Sets the time the job took.
     * @param runtime Time to set.
     */
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    /**
     * Gets the name of the interface
     * the tool was called from.
     * @return Name of interface,
     */
    public String getInterfaceType() {
        return interfaceType;
    }

    /**
     * Sets the name of the interface
     * that called the tool.
     * @param interfaceType Name of interface.
     */
    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * Gets the parameter with which the
     * tool was called.
     * @return Parameter as String.
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Sets the parameter the tool was called with.
     * @param parameter Parameter as string.
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    
}