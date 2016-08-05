/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2011 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import java.util.LinkedList;
import javax.inject.Inject;
import org.apache.log4j.Logger;

/**
 * ItemBean - a simple bean used by the item view to show item elements.
 * 
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class ItemBean {

    private static Logger log = Logger.getLogger(ItemBean.class);
    private String id = null;
  
    @Inject
    private Messages messages;

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }
    

    public String getDescription() {
        return (id == null)?"[getDescription] - No Item id set!":messages.property(getId() + "_description");
    }

    public String getName() {
        
        return (id == null)?"[getCategoryName] - No Item id set!":messages.property(getId() + "_name");
    }

    

    public String getCustomContent() {
        //
        return (id == null)?"[getCustomContent] - No Item id set!":messages.property(getId() + "_customContent");
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

  
}
