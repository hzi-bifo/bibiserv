package de.unibi.techfak.bibiserv.web.beans;



public class DynamicSqlTableRow {

        private String[] list;
        
        public DynamicSqlTableRow(String[] list) {
            this.list = list;
        }
        
        public String getListElement(int index) {
            if(index > list.length-1 || index < 0) {
                return "";
            }
            
            return list[index];
        }
}

