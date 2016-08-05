/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2013 BiBiServ"
 *
 * Contributor(s):
 *
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.web.beans.app.MenuHolder;
import de.unibi.cebitec.bibiserv.web.menu.ToolMenuCreator;
import de.unibi.cebitec.bibiserv.web.menu.UrlNodeUserObject;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.ejb.CreateException;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.InitializingBean;

/**
 * PoJo class for left side menu.
 * 
 * @author Jan Krueger - jkrueger(at)cebitec,uni-bielefeld.de
 * 
 * previous version by Daniel Hagemeier and Armin Toepfer 
 */
public class MenuBean implements InitializingBean{

    private static Logger log = Logger.getLogger(MenuBean.class);
    private UrlNodeUserObject treeModel;
    @Inject
    @Named("toolMenuCreator")
    private ToolMenuCreator tmc;
    private String currentItemId = "";
    private final HashMap<String, String> pathMap = new HashMap<>();
    private String currentId;
    
    private Locale currentLocale;
    private final Map<String, UrlNodeUserObject> idToNodeMap = new HashMap<>();

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }


    /**
     * Method required for DI
     *
     * @return
     */
    public MenuHolder getMenuHolder() {
        return tmc.getMenuHolder();
    }

    /**
     * Method required for DI
     *
     * @param tmc
     */
    public void setToolMenuCreator(ToolMenuCreator tmc) {
        this.tmc = tmc;
    }

    /**
     * ID of currently requested tool
     *
     * @return
     */
    public String getCurrentId() {
        return currentId;
    }

    /**
     * Setting ID of currently requested tool/category etc. Method invokes
     * expanding of nodes from tool menu
     *
     * @param newCurrentID
     */
    public void setCurrentId(String newCurrentID, HttpServletRequest httpServletRequest) {
        try {
            //setting Item ID if the requested id is from an item...
            if (tmc.getToolViewHash().containsKey(newCurrentID)) {
                setCurrentItemId(newCurrentID);           
            } else {
                setCurrentItemId("");
            }
            this.currentId = newCurrentID;
            //expand and select node and expand parents
            searchAndExpandCurrentItem(currentId, treeModel.getRoot());
        } catch (Exception e) {
            log.fatal("An Exception occured when setting the currentId in MenuBean: " + e);
        }
    }

    /**
     * Return CurrentItemId, which is only a value beside an empty string if the
     * item with the currentid is linked (
     *
     * @see setCurrentId)
     *
     * @return
     */
    public String getCurrentItemId() {
        return currentItemId;
    }

    /**
     *
     * @param currentItemId
     */
    public void setCurrentItemId(String currentItemId) {
        this.currentItemId = currentItemId;
    }

    public void refresh(ActionEvent ae) throws CreateException {
        tmc.init();
        setMainMenu(getMenuHolder().getClonedModel());
    }

    public void setMainMenu(UrlNodeUserObject model) throws CreateException {
        if (model == null) {
            throw new CreateException("MenuBean could not be created, NULL Urls not allowed! ");
        } else {
            treeModel = model;
        }
    }
    
    /**
     * Method that is called from JSF-Pages for retrieval of tree-object 
     *
     * @return
   
     */
    public UrlNodeUserObject getMainMenu()  {
        return treeModel;
    }

    /**
     * private method to open the currently selected part of main menu
     *
     * gets called internally from setCurrentItemId
     *
     * @param nodeId
     * @param node
     */
    private void searchAndExpandCurrentItem(String nodeId, UrlNodeUserObject node) {
        /**
         * Going through all nodes of menu
         */
        for (TreeNode t : node.getChildren()) {
            UrlNodeUserObject unuo = (UrlNodeUserObject) t;
            
            if (unuo.getId().equals(nodeId)) {
                unuo.setExpanded(true);
                unuo.setSelected(true);
            } else {
                unuo.setExpanded(false);
                unuo.setSelected(false);
            }
 
            // recursive call if any childs exists ...
            if (!unuo.getChildren().isEmpty()) {
                searchAndExpandCurrentItem(nodeId, unuo);
            }
            // check if child is expanded -> expand current node as well
            if (unuo.isExpanded()) {
                node.setExpanded(true);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        treeModel = getMenuHolder().getClonedModel();
    }
}
