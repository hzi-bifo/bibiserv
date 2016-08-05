package de.unibi.cebitec.bibiserv.web.menu;

/**
 *
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
 * @author Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 */
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * The UrlNodeUserO. is a class that is used by the MenuCreator...
 * The UrlNodeUserObject object is responsible for storing extra data
 * for an url.  The url along with text is bound to a ice:commandLink object which
 * will launch a new browser window pointed to the url.
 */
public class UrlNodeUserObject extends DefaultTreeNode { 

    private EmbeddedData embeddedData = new EmbeddedData();

    // url to show when a node is clicked
    public UrlNodeUserObject(String name, TreeNode parent) {
        super(null, parent);
        this.setData(this.embeddedData);
        this.embeddedData.text = name;
    }

    public UrlNodeUserObject getRoot() {
        UrlNodeUserObject parent = this;
        while (parent.getParent() != null) {
            parent = (UrlNodeUserObject) parent.getParent();
        }
        return parent;
    }
    
    public UrlNodeUserObject getPath() {
        UrlNodeUserObject parent = this;
        while (parent.getParent() != null) {
            parent = (UrlNodeUserObject) parent.getParent();
        }
        return parent;
    }

    public UrlNodeUserObject cloneThis(TreeNode parent) {
        UrlNodeUserObject newUNUO = new UrlNodeUserObject(this.getText(), parent);
        newUNUO.setExpanded(this.isExpanded());
        newUNUO.setUrl(this.getUrl());
        newUNUO.setText(this.getText());
        newUNUO.setId(this.getId());
        newUNUO.setIsMainCategory(this.isIsMainCategory());
        newUNUO.setIsMiddleCategory(this.isIsMiddleCategory());
        return newUNUO;
    }

    /**
     * Gets the url value of this IceUserObject.
     *
     * @return string representing a URL.
     */
    public String getUrl() {
        return this.embeddedData.url;
    }

    /**
     * Sets the URL.
     *
     * @param url a valid URL with protocol information such as
     *            http://icesoft.com
     */
    public void setUrl(String url) {
        this.embeddedData.url = url;
    }

    public boolean isIsMainCategory() {
        return this.embeddedData.isMainCategory;
    }

    public void setIsMainCategory(boolean isMainCategory) {
        this.embeddedData.isMainCategory = isMainCategory;
    }

    public boolean isIsMiddleCategory() {
        return this.embeddedData.isMiddleCategory;
    }

    public void setIsMiddleCategory(boolean isMiddleCategory) {
        this.embeddedData.isMiddleCategory = isMiddleCategory;
    }

    public String getId() {
        return this.embeddedData.id;
    }

    public void setId(String id) {
        this.embeddedData.id = id;
    }

    public String getText() {
        return this.embeddedData.text;
    }

    public void setText(String text) {
        this.embeddedData.text = text;
    }

    public class EmbeddedData {

        private String url;
        private boolean isMainCategory = false;
        private boolean isMiddleCategory = false;
        private String id;
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isIsMainCategory() {
            return isMainCategory;
        }

        public void setIsMainCategory(boolean isMainCategory) {
            this.isMainCategory = isMainCategory;
        }

        public boolean isIsMiddleCategory() {
            return isMiddleCategory;
        }

        public void setIsMiddleCategory(boolean isMiddleCategory) {
            this.isMiddleCategory = isMiddleCategory;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
