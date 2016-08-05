package de.unibi.cebitec.bibiserv.web.jsf.pf;

import org.primefaces.component.menuitem.UIMenuItem;

/**
 * This is a DefaultMenuItem with additional information about the submenu it belongs to.
 * @author Jan Schmolke
 */
public class ExtMenuItem extends UIMenuItem {
    private String submenuId;
    
    /**
     * Gets the id of the submenu this menuitem belongs to.
     * @return ID as string.
     */
    public String getSubmenuId() {
        return submenuId;
    }

    /**
     * Sets the id of the submenu this item belongs to.
     * @param submenuId Id to set as string.
     */
    public void setSubmenuId(String submenuId) {
        this.submenuId = submenuId;
    }
    
}
