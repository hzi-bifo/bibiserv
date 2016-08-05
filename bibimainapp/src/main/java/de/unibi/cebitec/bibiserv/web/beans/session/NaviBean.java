/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2014 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted 2010-2014 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.web.xmlreader.BiBiServXMLReader;
import de.unibi.cebitec.bibiserv.web.xmlreader.ItemContent;
import de.unibi.cebitec.bibiserv.web.xmlreader.RunnableItemContent;
import de.unibi.cebitec.bibiserv.web.xmlreader.View;
import de.unibi.techfak.bibiserv.BiBiTools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;

/**
 * AppNavigationBean, to give content for the view link navigation. Offers a
 * TagCloudModel on the base of the current Item and View.
 *
 *
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
 */
public class NaviBean {

    private static final Logger log = Logger.getLogger(NaviBean.class);
    private String itemId = null;
    private View view = null;
    
    @Inject
    @Named("bibiservXmlReader")
    private BiBiServXMLReader xmlrequest;
    @Inject
    private Messages messages;

    /**
     * setMessages sets Messages-bean by DI Bean is needed for resolving the
     * i18n names of menu-elements
     *
     * @param messages
     */
    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    /**
     * getMessages sets Messages-bean by DI Bean is needed for resolving the
     * i18n names of menu-elements
     *
     * @return
     */
    public Messages getMessages() {
        return messages;
    }

    /**
     * Set View object of current page.
     * 
     * @param view 
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Get View object of current page.
     * 
     * @return view
     */
    public View getView() {
        return view;
    }
    
    
    /**
     *  set Id of current ITem
     */
    public void setItemId(String id){
        this.itemId = id;
    }
    
    public String getItemId(){
        return itemId;
    }
    
    public void setXmlrequest(BiBiServXMLReader xmlrequest) {
        this.xmlrequest = xmlrequest;
    }

    /**
     * Create strings for navigation for available (and visible) view Types of submitted tool
     * id
     *
     * @return
     * 
     * @deprecated Attention this functions is deprecated and replaced by getViewLinks. to 
     */
    @Deprecated
    public List<View> getViewTypeLinks() {
        return getViewLinks();
    }
    
    
    /**
     * Return a list of all "visible" views which are available as vertical tab.
     * 
     * @return 
     */
    public List<View> getViewLinks(){
        List<View> retval = new ArrayList();
        HashMap<String, ItemContent> ich = xmlrequest.getItemContentHash();
        if (ich.containsKey(itemId) && ich.get(itemId) instanceof RunnableItemContent) {
            // iterate over all viewtypes and return all "visible" view names
            for (View vt : ((RunnableItemContent) ich.get(itemId)).getViews()) {
                if (vt.isVisible()) {           
                    if (!vt.getId().equals("ritc") || (vt.getId().equals("ritc") && (BiBiTools.getProperties().getProperty("RITC","false").equalsIgnoreCase("true")))) {
                        retval.add(vt);
                    }       
                }
            }
        }
        return retval;
    }

    /**
     * Reset NaviBean values ... in fact only the view.getId() is set to an empty
     * String. GetViewTypeLinks doesn't match anything and returns an empty list
     * in that case.
     */
    public void reset() {
        itemId = null;
        view = null;
    }

    /**
     * Return a Tag Cloud Model. Create a tag cloud model from current
     * RunnableItemContent and underlaying structure description.
     *
     * @todo : implement it in a more efficient way ...
     *
     * @return Returns a tag cloud model.
     */
    public TagCloudModel getTagCloudModel() {
        List<DefaultTagCloudItem> list = new ArrayList();

        TagCloudModel model = new DefaultTagCloudModel();
        HashMap<String, ItemContent> ich = xmlrequest.getItemContentHash();
        for (String key : ich.keySet()) {
            if (key.equals(itemId) && ich.get(itemId) instanceof RunnableItemContent) {
                RunnableItemContent ric = (RunnableItemContent) ich.get(itemId);
                list.add(new DefaultTagCloudItem(ich.get(itemId).getItemName(), "/"+key, 5)); // add three times
                list.add(new DefaultTagCloudItem(ich.get(itemId).getItemName(), "/"+key, 5));
                list.add(new DefaultTagCloudItem(ich.get(itemId).getItemName(), "/"+key, 5));
                if (view.getId() == null) { // must be welcome page
                    list.add(new DefaultTagCloudItem("Welcome", "/" + key, 4));
                    list.add(new DefaultTagCloudItem("Welcome", "/" + key, 4));
                    list.add(new DefaultTagCloudItem("Welcome", "/" + key, 4));
                    for (View v : ric.getViews()) {
                        list.add(new DefaultTagCloudItem(getNameForView(v), "/" + key + "?id=" + v.getId(), 3));
                        list.add(new DefaultTagCloudItem(getNameForView(v), "/" + key + "?id=" + v.getId(), 3));
                    }
                } else {
                    for (View v : ric.getViews()) {
                        if (v.getId().equals(view.getId())) {
                            list.add(new DefaultTagCloudItem(getNameForView(v), "/" + key + "?id=" + v.getId(), 4));
                            list.add(new DefaultTagCloudItem(getNameForView(v), "/" + key + "?id=" + v.getId(), 4));
                            list.add(new DefaultTagCloudItem(getNameForView(v), "/" + key + "?id=" + v.getId(), 4));
                        } else {
                            list.add(new DefaultTagCloudItem(getNameForView(v), "/" + key + "?id=" + v.getId(), 3));
                            list.add(new DefaultTagCloudItem(getNameForView(v), "/" + key + "?id=" + v.getId(), 3));
                        }
                    }
                }
            } else {
                list.add(new DefaultTagCloudItem(ich.get(key).getItemName(), "/" + key, new Double(Math.ceil(Math.random() * 2)).intValue()));
            }
        }
  
        /*
         * shuffle tags
         */
        while (list.size() > 0) {
            int index = new Double(Math.floor(list.size() * Math.random())).intValue();
            model.addTag(list.remove(index));

        }
        return model;
    }

    /**
     * Helper method : return a named String for a given view
     *
     * @param view
     * @return
     */
    
    public String getNameForView(View view) {
         // default 
        String name = messages.property("de.unibi.techfak.bibiserv.bibimainapp."+view.getType().toUpperCase());
        // tpage e.g. webstart 
        if (name.isEmpty()) {
            name = messages.property(view.getId()+"_title");

        } 
        // other elements
        if (name.isEmpty()) {
            name = messages.property(view.getId()+"_name");
        }
        // fallback - return type as name
        if (name.isEmpty()) {
            name = view.getType();
        }
        return name;
    }
    
    @Deprecated
    public String getNameForViewType(View view) {
       return getNameForView(view);
    }
}
