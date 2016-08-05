
package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.basespace.json.BaseSpaceObject;

/**
 * Used to store data of one listing item for BaseSpace.
 * @author Thomas Gatter
 */
public class BaseSpaceItem {
    
    private BaseSpaceObject object;
    private int index;
    private String name;
    
    public BaseSpaceItem(BaseSpaceObject object, int index, String name) {
        this.object = object;
        this.index = index;
        this.name = name;
    } 

    public BaseSpaceObject getObject() {
        return object;
    }

    public void setObject(BaseSpaceObject object) {
        this.object = object;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
