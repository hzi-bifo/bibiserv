package de.unibi.cebitec.bibiserv.statistics.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Implementation of LazyDataModel used by DevStatsBean for the list
 * presented in datatable.
 * This version works for DatabaseTool.
 * @author jschmolke
 */
public class LazyDatabaseToolModel extends LazyDataModel<DatabaseTool>{
    private List<DatabaseTool> tools;
    
    /**
     * Standardconstructor.
     * @param tools Tools to show.
     */
    public LazyDatabaseToolModel(ArrayList<DatabaseTool> tools){
        this.tools = tools;
    }

    /**
     * Returns the tool with given key.
     * @param rowKey Key of row.
     * @return The tool.
     */
    @Override
   public DatabaseTool getRowData(String rowKey){
       for(DatabaseTool tool: tools){
           if(tool.getToolname().equals(rowKey)){
               return tool;
           }
       }
       return null;
   }

    /**
     * Gets the key for given tool.
     * @param tool Tool.
     * @return Rowkey.
     */
    @Override
   public Object getRowKey(DatabaseTool tool){
       return tool.getToolname();
   }
    
    /**
     * Loadmethod.
     * @param first First object.
     * @param pageSize Paginator size.
     * @param sortField Field that should be sorted.
     * @param sortOrder Ordering.
     * @param filters List of filters.
     * @return List of tools to show.
     */
    
    @Override
    public List<DatabaseTool> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<DatabaseTool> data = new ArrayList<>();
        
        //filter
        for(DatabaseTool tool : tools){
            boolean match =true;
            for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
                try{
                    String filterProperty = it.next();
                    String filterValue = (String)filters.get(filterProperty);
                    String fieldValue=" ";
                    
                    switch(filterProperty.toUpperCase()){
                        case "TOOLNAME":fieldValue = tool.getToolname();break;
                        case "TOPOS":fieldValue = tool.getTopOS();break;
                        case "TOPCOUNTRY":fieldValue = tool.getTopCountry();break;
                        case "TOPBROWSER":fieldValue = tool.getTopBrowser();break;
                        case "CPERC":fieldValue = String.valueOf(tool.getcPerc());break;
                        case "OSPERC":fieldValue = String.valueOf(tool.getOsPerc());break;
                        case "BRPERC":fieldValue = String.valueOf(tool.getBrPerc());break;                        
                    }

                    if(filterValue == null || fieldValue.contains(filterValue)){
                        match = true;
                    }else{
                        match = false;
                        break;
                    }
                }catch(Exception e){
                    match = false;
                }
            }
            if(match){
                data.add(tool);
            }
        }
        
        //sort
        if(sortField != null){
            Collections.sort(data,new LazyDatabaseToolSorter(sortField,sortOrder));
        }
        
        //rowCount
        int dataSize = data.size();
        this.setRowCount(dataSize);
        
        //paginate
        if(dataSize > pageSize){
            try{
                return data.subList(first,first + pageSize);
            }catch(IndexOutOfBoundsException e){
                return data.subList(first, first + (dataSize % pageSize));
            }
        }else{
            return data;
        }
        
    }
}
