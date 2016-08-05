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
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.apache.log4j.Logger;

/**
 * Bean representation for a category.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *         Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
           Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 */
public class CategoryBean {

    private static Logger log = Logger.getLogger(CategoryBean.class);
    private String id;
    private List<String> childIds;
    @Inject
    private Messages messages;

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }
    private Map<String, List<String>> viewHash;

    public String getCategoryDescription() {
        return messages.property(getId() + "_categoryDescription");
    }

    public String getCategoryName() {
        return messages.property(getId() + "_name");
    }

    public String getCustomContent() {
        return messages.property(getId() + "_representation_customContent");
    }

    public List<List<String>> getChildElements() {
        List<List<String>> out = new ArrayList<>();
        childIds = viewHash.get(getId()); 
       if (childIds.size() > 0) {
            for (int i = 0; i < childIds.size(); i++) {
                
                List<String> pair = new ArrayList<>();
         
                pair.add(messages.property(childIds.get(i) + "_name"));
                //if the id is in the view hash it is the id of a category and it has a category description
                if (viewHash.containsKey(childIds.get(i))) {
                    pair.add(messages.property(childIds.get(i) + "_categoryDescription"));
                } else {
                    //id both in view hash and must therefore be a id of a tool having only a short description
                    pair.add(messages.property(childIds.get(i) + "_shortDescription"));
                }
                pair.add(childIds.get(i));
                out.add(pair);
            }
        }

        return out;
    }

    public String getToolId() {
        return id;

    }

    public void setID(String id) {
        this.id = id;

    }

    public String getId() {
        return id;
    }

    public void setViewHash(Map<String, List<String>> viewHash) {
        this.viewHash = viewHash;
    }
}
