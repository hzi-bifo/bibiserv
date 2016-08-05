/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2012 BiBiServ Curator Team"
 *
 * Contributor(s): Daniel Hagemeier, Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.menu;

import com.hp.hpl.jena.vocabulary.DCTypes;
import de.unibi.cebitec.bibiserv.web.beans.app.MenuHolder;
import de.unibi.cebitec.bibiserv.web.xmlreader.BiBiServXMLReader;
import de.unibi.cebitec.bibiserv.web.xmlreader.ItemContent;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import de.unibi.cebitec.bibiserv.web.beans.session.Messages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.TreeNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class creating the Main-Menu of BibiServ using the BiBiServXMLReader and
 * sending XPATh requests The class also creates a Hash which has category-ids
 * as keys and a list of child-element ids as values
 *
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
 */
public class ToolMenuCreator implements InitializingBean {
    
    private UrlNodeUserObject rootTreeNode;
    @Inject
    private MenuHolder menuHolder;
    @Inject
    @Named("bibiservXmlReader")
    private BiBiServXMLReader xmlrequest;
    private HashMap<String, UrlNodeUserObject> langHash;
    private HashMap<String, List<String>> viewHash;
    private HashMap<String, List<String>> toolViewHash;
    private List<String> toolViews;
    private List<String> childrenIds;
    private static Logger log = Logger.getLogger(ToolMenuCreator.class);
    @Inject
    private Messages messages;
    
    public final static int lexicographical = 1;
    public final static int lexicographical_separate = 2;
    public final static int natural = 4;

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
     * Setting MenuHolder by DI of Spring
     *
     * @param menuHolder
     */
    public void setMenuHolder(MenuHolder menuHolder) {
        this.menuHolder = menuHolder;
    }
    
    public MenuHolder getMenuHolder() {
        return menuHolder;
        
    }

    /**
     * Init-method is called after all properties were set by DI
     * afterPropertiesSet()
     *
     */
    public void init() {
        log.debug("ToolMenuCreator init");

        //viewHash contains the ids of all categories and thus allows a fast
        //identification of valid category ids
        this.viewHash = new HashMap<>();

        //toolViewhash contains the ids (and attached views) of all tools which
        //have views
        this.toolViewHash = new HashMap<>();

        //Code for language retrieval from Database
        //find all languages in structure-document
        this.langHash = new HashMap<>();

        //put entry for default language into hashmap
        langHash.put("", null);

        //taking all languages from strucure xml
        try {
            String langQuery = "//@xml:lang";
            NodeList lang = xmlrequest.getNodeListStructure(langQuery);
            
            for (int i = 0; i < lang.getLength(); i++) {
                langHash.put(lang.item(i).getTextContent(), null);
            }
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }

        //now generate a tree for each language ...
        for (String language : langHash.keySet()) {
                
            Node rootnode = xmlrequest.getRootElement();
            String rootId = rootnode.getAttributes().getNamedItem("id").getTextContent();
            String rootName;

            //getting name fro tags without lang-attribute
            if (!language.isEmpty()) {
                rootName = messages.getMessageSource().getMessage(rootId + "_name", null, new Locale(language));
            } //getting name for elements with an lang attribute with current value of language in loop
            else {
                rootName = messages.getMessageSource().getMessage(rootId + "_name", null, null);
            }
            
            rootTreeNode = new UrlNodeUserObject(rootName, null);
            rootTreeNode.setExpanded(true);
            rootTreeNode.setUrl("/");
            rootTreeNode.setId(rootId);
            rootTreeNode.setText(rootName);
            //calling buildTree Method for root node...
            rootTreeNode = buildTree(rootnode, rootTreeNode, language);

            //mark the top-level categories for JSF check
            //ugly code for marking of first vs. middle/end cats
            boolean top = true;
            for (TreeNode e : rootTreeNode.getChildren()) {
                UrlNodeUserObject nodeObject = (UrlNodeUserObject) e;
                nodeObject.setIsMainCategory(true);
                if (top) {
                    top = false;
                } else {
                    nodeObject.setIsMiddleCategory(true);
                }
            }

            //now putting tree-model into langHash
            langHash.put(language, rootTreeNode);
            //reset model
            rootTreeNode = null;
            
        }//end of language-loop

        menuHolder.setItemContentHash(xmlrequest.getItemContentHash());
        menuHolder.setViewHash(viewHash);
        menuHolder.setLangHash(langHash);
        
    }

    //Setter for Dependency Injection via Spring
    public void setXmlrequest(BiBiServXMLReader xmlrequest) {
        this.xmlrequest = xmlrequest;
        
    }

    /**
     * Method that builds the Menu tree of BiBiServ recursively
     *
     * @param categoryIdSet
     * @param root
     * @return
     */
    private UrlNodeUserObject buildTree(Node categorynode, UrlNodeUserObject root, String lang) {
        UrlNodeUserObject branch;

        /* 3 Listen um die verschiedenen Sortierungsoptionen des 'order' tags ohne
         viel Rechenaufwand zu realisieren */
        List<String> childIds = new ArrayList<>();
        List<String> catIds = new ArrayList<>();
        List<String> itemIds = new ArrayList<>();
        HashMap<String, Node> nodesById = new HashMap<>();
        
        int order = lexicographical_separate;
        
        final String categoryId = categorynode.getAttributes().getNamedItem("id").getTextContent();
        String id;
        
        // get all possible childnodes
        NodeList children = categorynode.getChildNodes();
        // and iterate over them
        for (int j = 0; j < children.getLength(); j++) {
            
            Node n = children.item(j);
            // since we search for order, category and itemref element nodes, we can restrict the search 
            // as specified 'order' tag comes first ...
            if (n.getNodeType() == 1) {
                switch (n.getLocalName()) {
                    case "order":
                        switch (n.getTextContent()) {
                            case "lexicographical":
                                order = lexicographical;
                                break;
                            case "lexicographical_separate":
                                order = lexicographical_separate;
                                break;
                            case "natural":
                                order = natural;
                                break;
                        }
                        break;                  
                    case "category":
                        id = n.getAttributes().getNamedItem("id").getTextContent();
                        childIds.add(id);
                        catIds.add(id);
                        nodesById.put(id, n);
                        break;
                    case "itemRef":
                        id = n.getTextContent();
                        childIds.add(id);
                        itemIds.add(id);
                        nodesById.put(id, n);
                        break;
                }
            }
        }
        // order all childid's before adding to view hash
        if (order == lexicographical) {
            Collections.sort(childIds);
        } else if (order == lexicographical_separate) {
            Collections.sort(catIds);
            Collections.sort(itemIds);
            childIds = catIds;
            childIds.addAll(itemIds);
        }
        // childIds to viewHash
        viewHash.put(categoryId, childIds);

        // build menu tree
        for (String cid : childIds) {
            Node n = nodesById.get(cid);
            if (n.getLocalName().equals("category")) {
                String categoryName;
                if (lang.isEmpty()) {
                    categoryName = messages.getMessageSource().getMessage(cid + "_name", null, "", null);
                } else {
                    categoryName = messages.getMessageSource().getMessage(cid + "_name", null, "", new Locale(lang));
                }
                branch = createNode(categoryName, cid, false, root);
                buildTree(n, branch, lang);
            } else {
                buildTreeItem(n, root, lang);
            }
        }
        return root;
    }

    /**
     * buildtree method for an itemref element
     *
     * @param itemRefNod
     * @param root
     * @parm lang
     * @return
     */
    private UrlNodeUserObject buildTreeItem(Node itemRefNode, UrlNodeUserObject root, String lang) {
        
        String itemId = itemRefNode.getTextContent();
        // Check if referenced item is also available in database
        if (xmlrequest.getItemContentHash().containsKey(itemId)) {
            // get Item from database
            ItemContent currentItemContent = xmlrequest.getItemContentHash().get(itemId);
            // deteremine name 
            String itemName = currentItemContent.getItemName();
            // create item node and add it to tree
            createNode(itemName, itemId, false, root);
            
        }
        return root;
    }
    
    public HashMap<String, List<String>> getToolViewHash() {
        return toolViewHash;
        
    }

    /**
     * method for creating nodes
     *
     * @param nodeName
     * @param nodeId
     * @param root
     * @param leaf
     * @param expanded
     * @return
     */
    private UrlNodeUserObject createNode(String nodeName, String nodeId, Boolean expanded, TreeNode parent) {
        UrlNodeUserObject childObject = new UrlNodeUserObject(nodeName, parent);
        childObject.setExpanded(expanded);
        childObject.setId(nodeId);
        childObject.setUrl("/" + nodeId);
        childObject.setText(nodeName);
        
        return childObject;
    }
    
    public HashMap<String, List<String>> getViewHash() {
        return viewHash;
    }
    
    public BiBiServXMLReader getBibiservXmlReader() {
        return xmlrequest;
    }

    /**
     * method of interface InitializingBean calls init method that starts
     * creation of Menu at application-start
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
