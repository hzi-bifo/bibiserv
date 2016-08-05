package de.unibi.techfak.bibiserv.web.beans.session;


import de.unibi.techfak.bibiserv.web.beans.DynamicSqlTableRow;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author gatter
 */
public abstract class AbstractSqlTableBean {

    private final String connectionString = getConnectionString();
    private final String connectionDriverClass = getDriverClass();
    private final String username = getUserName();
    private final String password = getUserPassword();
    private final String tableName = getTableName();
    private final String column_names = getSqlColumns();
    private final String column_show = getSqlNames();
    
    
    private static final String UNSORTED = "ui-icon-carat-2-n-s";
    private static final String ASC = "ui-icon-carat-2-n-s ui-icon-triangle-1-n";
    private static final String DESC = "ui-icon-carat-2-n-s ui-icon-triangle-1-s";
    
    
    LazySqlDataModel lazyModel = new LazySqlDataModel();
    private String[] sql_columns;
    private String sqlStatementColumns;
    
    private Map<Integer, String> selectedFilter = new HashMap<Integer, String>();
    private Map<Integer, String> sortStyle = new HashMap<Integer, String>();
    private SortOrder sortOrder = SortOrder.UNSORTED;
    private int sortField = -1;

    private List<ColumnModel> columns = new ArrayList<ColumnModel>();
    
    public LazySqlDataModel getLazyModel() {
        return lazyModel;
    }
  
    public AbstractSqlTableBean() {
        try {
            Class.forName(connectionDriverClass);
        } catch (ClassNotFoundException ex) {
           FacesContext.getCurrentInstance().addMessage(getId()+"_sql_status_msg", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", "SQL driver class not found!"));
           return;
        }
        createDynamicColumns();
    }
    
    public void createDynamicColumns() {
        String[] columnKeys = column_show.trim().split("\\s+");
        sql_columns = column_names.trim().split("\\s+");
        
        if(columnKeys.length != sql_columns.length) {
            FacesContext.getCurrentInstance().addMessage(getId()+"_sql_status_msg", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", "Column header and sql columns differ in length!"));
            return;
        }
        
        int index = 0;
        for (String columnKey : columnKeys) {
            String key = columnKey.trim();
            columns.add(new ColumnModel(columnKey, index));
            
            selectedFilter.put(index, "");
            sortStyle.put(index, UNSORTED);
            
            index++;
        }
        
        sqlStatementColumns = "";
        for(String column : sql_columns) {
            if(sqlStatementColumns.isEmpty()) {
                sqlStatementColumns += "";
            } else {
                sqlStatementColumns += ", ";
            }
            sqlStatementColumns +="\""+column+"\"";
        }
        sqlStatementColumns += "";
        
    }

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public Map<Integer, String> getSortStyle() {
        return sortStyle;
    }
    
    public Map<Integer, String> getSelectedFilter() {
        return selectedFilter;
    }
    
    public void setSort(int index) {
        
        SortOrder order = sortOrder;
        if(index != sortField && sortField!=-1) { // sort different column
            order = SortOrder.UNSORTED;
            sortStyle.put(sortField, UNSORTED);
        }
        
        sortField = index; // set new sortfield
        
        // what is the new style of this field
        String style = ASC;
        sortOrder = SortOrder.ASCENDING;
        switch(order) {
            case ASCENDING:
                style = DESC;
                sortOrder = SortOrder.DESCENDING;
                break;
            case DESCENDING:
                style = UNSORTED;
                sortOrder = SortOrder.UNSORTED;
                break;
        }
        sortStyle.put(index, style);
    }

    /**
     * Return space separated list of what should be displayed as column header.
     * Must be same number of items as getSqlColumns.
     * @return 
     */
    public abstract String getSqlNames();

    /**
     * Name of the table in the database
     * @return 
     */
    public abstract String getTableName();

    /**
     * Password of the database user
     * @return 
     */
    public abstract String getUserPassword();

    /**
     * Name of the database user
     * @return 
     */
    public abstract String getUserName();

    /**
     * Java-Class of the database driver.
     * @return 
     */
    public abstract String getDriverClass();

    /**
     * The connectionstring describing the connection to the database
     * @return 
     */
    public abstract String getConnectionString();

    /**
     * Return space separated list of all columns in table to show.
     * @return 
     */
    public abstract String getSqlColumns();

    /**
     * The id of the XHTML object. Must be unique for each page but is not regulated in any other way.
     * @return 
     */
    public abstract String getId();

    static public class ColumnModel implements Serializable {

        private String header;
        private int property;

        public ColumnModel(String header, int property) {
            this.header = header;
            this.property = property;
        }

        public String getHeader() {
            return header;
        }

        public int getProperty() {
            return property;
        }
    }

    public class LazySqlDataModel extends LazyDataModel<DynamicSqlTableRow> {

        public LazySqlDataModel() {
        }

        @Override
        public List<DynamicSqlTableRow> load(int offset, int pageSize, String sortFieldDISABLED, SortOrder sortOrderDISABLED, Map<String, Object> filtersDISABLED) {
            
            List<DynamicSqlTableRow> ret = new ArrayList<>();
            
            // built query Strings
            String sort = "";
            if(sortField!=-1 ){
                switch(sortOrder) {
                    case ASCENDING:
                        sort = " ORDER BY \""+sql_columns[sortField]+"\" ASC";
                        break;
                    case DESCENDING:
                        sort = " ORDER BY \""+sql_columns[sortField]+"\" DESC";
                        break;
                }
            }
            
            String where = "";
            for (Iterator<Integer> it = selectedFilter.keySet().iterator(); it.hasNext();) {
                
                int filterProperty = it.next();
                String filterValue = selectedFilter.get(filterProperty);
                if(filterValue.isEmpty()){
                    continue;
                }
                
                if(where.isEmpty()) {
                    where += " WHERE";
                } else {
                    where += " AND";
                }
                where += " CAST(\""+sql_columns[filterProperty]+"\" AS TEXT) LIKE '%"+filterValue+"%'";
            }
           /*  REMARK JK : SQL command 'LIMIT' seems not be an inofficial SQL statement 
            *  -> it's not supported by JAVADB (10.8.x)
            *  -> use OFFSET ... FETCH instead which seem to be in SQL2008 standard
            * 
            */
            //String limit = " LIMIT "+pageSize+" OFFSET "+offset;
            
            String limit = " OFFSET "+offset+" ROWS FETCH NEXT " + pageSize +" ROWS ONLY ";
            
            String count = "SELECT COUNT (*) FROM \""+tableName+"\""+where;
            String getData = "SELECT "+sqlStatementColumns+" FROM \""+tableName+"\""+where+sort+limit;
            
            Connection connection = null;
            Statement statement = null;
            try {
                connection = DriverManager.getConnection(connectionString, username,password);
                statement = connection.createStatement();
                
                System.out.println(count);
                ResultSet res = statement.executeQuery(count);
                res.next();
                int c = res.getInt(1);
                this.setRowCount(c);
                res.close();
                
                System.out.println(getData);
                res = statement.executeQuery(getData);
                int colsize = res.getMetaData().getColumnCount();
                while(res.next()) {
                    String[] row = new String[colsize];
                    for(int i=0;i < colsize;i++) {
                        row[i] = res.getString(i+1);
                    }
                    DynamicSqlTableRow rowOb = new DynamicSqlTableRow(row);
                    ret.add(rowOb);
                }
                res.close();
                
            } catch (SQLException ex) {
                FacesContext.getCurrentInstance().addMessage(getId()+"_sql_status_msg", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", "SQL Exception:"+ex));
            } finally {
                if(statement!=null) {
                    try {
                        statement.close();
                    } catch (SQLException ex) {
                    }
                }
                if(connection !=null) {
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                    }
                }
            }
            
            return ret;
        }
    }
    
}
