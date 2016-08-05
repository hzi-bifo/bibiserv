/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */
package de.unibi.cebitec.bibiserv.web.beans.app;

import de.unibi.cebitec.bibiserv.web.beans.session.Messages;
import de.unibi.cebitec.bibiserv.web.menu.UrlNodeUserObject;
import de.unibi.cebitec.bibiserv.web.xmlreader.ItemContent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
 */
public class MenuHolder {

    private static final Logger log = Logger.getLogger(MenuHolder.class);
    private UrlNodeUserObject treeModel;
    private Map<String, UrlNodeUserObject> langHash;
    private HashMap<String, List<String>> viewHash;
    private HashMap<String, ItemContent> itemContentHash;
    @Inject
    private Messages messages;
    private String currLang;
    private String prefLang="";
    UrlNodeUserObject newTreeModel;

    public UrlNodeUserObject getClonedModel(HttpServletRequest httpServletRequest) {
        this.prefLang = messages.getPreferredLocale(httpServletRequest).getLanguage();
        return createClonedModelForLanguage();
    }

    /**
     * Method creates a cloned version of the Tree-models
     * that were created at application startup
     *
     * @return DefaultTreeModel
     */
    public UrlNodeUserObject getClonedModel() {
        
        try {
            prefLang = messages.getPreferredLocale().getLanguage();
        } catch (Exception e) {
            /* log.error("Routine exception when requesting message without a populated facesContext: " + e) */
        } 
        return createClonedModelForLanguage();
    }

    private UrlNodeUserObject createClonedModelForLanguage() {
        //check if currently selected language is the preferred languange of user
        ///when unequel new model according to language will be selected
        //if (newTreeModel == null || !currLang.equals(prefLang)) {
            this.treeModel = getTreeModel(prefLang);

            this.newTreeModel = reverseTreeNode(treeModel,null);

            log.debug("return model from getClonedModel...");
        //}
        return newTreeModel;
    }


    private UrlNodeUserObject reverseTreeNode(UrlNodeUserObject oldNode,TreeNode parent) {
        UrlNodeUserObject newNode = oldNode.cloneThis(parent);
        for(TreeNode t : oldNode.getChildren()) {
            UrlNodeUserObject urlChild = (UrlNodeUserObject) t;
            reverseTreeNode(urlChild,newNode);
        }
        return newNode;
    }

//Hashmap containing all tree datastructures for all languages
    public Map<String, UrlNodeUserObject> getLangHash() {
        return langHash;
    }

    /**
     * Method for setting LangHash hash that contains languages as keys and according models as values
     * from MenuCreator
     *
     * @param langHash
     */
    public void setLangHash(Map<String, UrlNodeUserObject> langHash) {

        this.langHash = langHash;
        log.debug("langHash was set..." + langHash.size());
    }

    /**
     * Method for selection of right treeModel according to currently selected language in browser
     * @return DefaultTreeModel
     */
    private UrlNodeUserObject getTreeModel(String locale) {
        log.debug(">>> CALLED GET-TREE-MODEL");
        currLang = "";

        try {
            currLang = locale;
        } catch (Exception e) {
            log.error("An exception occured when requesting preferred locale of messages-bean" + e);
        }
        log.debug("Current language TreeModel: " + currLang);

        if (langHash.containsKey(currLang)) {
            log.debug("langHash contained key " + currLang);
            return (UrlNodeUserObject) langHash.get(currLang);
        } else {
            log.debug("langHash did not contain the required key " + currLang);
            return (UrlNodeUserObject) langHash.get("");

        }
    }

    public void setViewHash(HashMap<String, List<String>> viewHash) {
        this.viewHash = viewHash;
    }

    public Map<String, List<String>> getViewHash() {
        return viewHash;
    }

    public void setItemContentHash(HashMap<String, ItemContent> itemContentHash) {
        this.itemContentHash = itemContentHash;
    }

    public HashMap<String, ItemContent> getItemContentHash() {
        return itemContentHash;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public Messages getMessages() {
        return messages;
    }
}
