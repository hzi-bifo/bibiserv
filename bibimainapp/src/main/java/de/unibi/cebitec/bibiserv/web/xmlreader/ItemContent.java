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
 * Contributor(s): Jan krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *
 */
package de.unibi.cebitec.bibiserv.web.xmlreader;

import org.w3c.dom.Document;

/**
 * ItemContent is a container class for all kind of 'items' supported by the 
 * BiBiServAbstraction XML. First version was one Containerclass for all kind 
 * of supported item and was written by Daniel Hagemeier and extended by Jan Krueger.
 * 
 * Class was complete rewritten by Jan Krueger. ItemContent is the abstract base 
 * class. Concrete Implementation were TextItemContent, RunnableItemContent and 
 * LinkedItemContent.
 * 
 * 
 * <ul>
 *  <li><em>runnableitem</em> supports attributes itemname and viewTypes </li>
 *  
 *  <li><em>item</em> supports attribute itemname</li>
 * 
 *  <li><em>linkeditem</em> supports attributes itemname, server and port</li>
 * 
 * <ul>
 *
 *
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public abstract class ItemContent {

    /* name of item -supported by runnableitem, item, linkeditem */
    private String itemName = null;

    private Document doc;
    
    /**
     * Returns the name of current item
     *
     * @return
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Set the name of item
     *
     * @param itemName
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Returns the xml representation of current item
     * 
     * @return 
     */
    public Document getDoc() {
        return doc;
    }

    /**
     * Set the xml representation of current item
     * 
     * @param doc 
     */
    public void setDoc(Document doc) {
        this.doc = doc;
    }

    
}