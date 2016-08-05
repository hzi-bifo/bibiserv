package de.unibi.cebitec.bibiserv.statistics.charts;

import java.util.Comparator;
import org.primefaces.model.SortOrder;

/**
 * Sorter for DatabaseTools.
 * Used by LazyDatabaseToolModel.
 * @author jschmolke
 */
public class LazyDatabaseToolSorter implements Comparator<DatabaseTool>{
    private String sortField;
    private SortOrder sortOrder;
    
    /**
     * Standardconstructor.
     * @param sortField Name of field that shall be sorted.
     * @param sortOrder Ordering.
     */
    public LazyDatabaseToolSorter(String sortField,SortOrder sortOrder){
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
    
    @Override
    public int compare(DatabaseTool tool1, DatabaseTool tool2) {
        try{
            Object value1 = 1, value2 = 1; 
            switch(sortField.toUpperCase()){
                case "TOTAL": value1=tool1.getTotal();value2=tool2.getTotal();break;
                case "OSPERC": value1=tool1.getOsPerc();value2=tool2.getOsPerc();break; 
                case "CPERC": value1=tool1.getcPerc();value2=tool2.getcPerc();break;
                case "BRPERC": value1=tool1.getBrPerc();value2=tool2.getBrPerc();break;
                case "TOPOS": value1=tool1.getTopOS();value2=tool2.getTopOS();break;
                case "TOPCOUNTRY": value1=tool1.getTopCountry();value2=tool2.getTopCountry();break;
                case "TOPBROWSER": value1=tool1.getTopBrowser();value2=tool2.getTopBrowser();break;
                case "TOOLNAME": value1=tool1.getToolname();value2=tool2.getToolname();break;
            }
            
            int value = ((Comparable)value1).compareTo(value2);
            
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }catch(Exception e){
            throw new RuntimeException();
        }
    }    
}
