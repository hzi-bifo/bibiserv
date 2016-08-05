package de.unibi.cebitec.bibiserv.web.administration.model;

/**
 *
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
 */
import de.unibi.cebitec.bibiserv.web.menu.UrlNodeUserObject;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 * THIS SHALL HOLD INFO FOR STRUCTURE EDITING. LETS SEE HOW WELL THIS GOES...
 */
public class StructureAdminUrlNodeUserObject extends UrlNodeUserObject {

    // url to show when a node is clicked
    private List nameList; //names of this category
    private List descriptionList; //descriptions of this category
    private List titleList; //title elements of this category's representation
    private List customContentList; //customContent elements of this  category's representation

    public StructureAdminUrlNodeUserObject(String data, TreeNode parent) {
        super(data,parent);
    }

    
    public List getCustomContentList() {
        return customContentList;
    }

    public void setCustomContentList(List customContentList) {
        this.customContentList = customContentList;
    }

    public List getDescriptionList() {
        return descriptionList;
    }

    public void setDescriptionList(List descriptionList) {
        this.descriptionList = descriptionList;
    }

    public List getNameList() {
        return nameList;
    }

    public void setNameList(List nameList) {
        this.nameList = nameList;
    }

    public List getTitleList() {
        return titleList;
    }

    public void setTitleList(List titleList) {
        this.titleList = titleList;
    }
}
