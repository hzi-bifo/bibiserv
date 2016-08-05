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
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 *
 * @author shartmei
 */
public class TypeFormatBean {

    private String currentType = "";
    private String currentFormat = "";
    private List currentTfk = new ArrayList();
    private List<SelectItem> types = new ArrayList<SelectItem>();
    private List<SelectItem> formats = new ArrayList<SelectItem>();

    public TypeFormatBean() {
//        //init formats
//        Map fm = TypeOntoQuestioner.getAllFormats();
//        
//        for (Iterator it = fm.keySet().iterator(); it.hasNext();) {
//            Object object = it.next();
//            formats.add(new SelectItem(object, object.toString()));
//        }
//
//        //init types
//        Map tm = TypeOntoQuestioner.getAllTypes();
//        for (Iterator it = tm.keySet().iterator(); it.hasNext();) {
//            Object object = it.next();
//            types.add(new SelectItem(object, object.toString()));
//        }
    }

    /**
     * Get the value of currentType
     *
     * @return the value of currentType
     */
    public String getCurrentFormat() {
        return currentFormat;
    }

    public void setCurrentFormat(String currentFormat) {
        this.currentFormat = currentFormat;
    }

    public String getCurrentType() {
        return currentType;
    }

    public void setCurrentType(String currentType) {
        this.currentType = currentType;
    }

    public List getCurrentTfk() {
        return currentTfk;
    }

    /**
     * Get the value of types
     *
     * @return the value of types
     */
    public List<SelectItem> getTypes() {
        return Collections.unmodifiableList(types);
    }

    /**
     * Get the value of types
     *
     * @return the value of types
     */
    public List<SelectItem> getFormats() {
        return Collections.unmodifiableList(formats);
    }

    public void updateFormats(ValueChangeEvent event) {
//        String type = event.getNewValue().toString();
//        setCurrentType(type);
//        checkTFK();
//        if ("nothing".equals(type)) {
//            Map fm = TypeOntoQuestioner.getAllFormats();
//            for (Iterator it = fm.keySet().iterator(); it.hasNext();) {
//                Object object = it.next();
//                formats.add(new SelectItem(object, object.toString()));
//            }
//        } else {
//            try {
//                Map fm = TypeOntoQuestioner.getFormatsForType(type);
//                formats.clear();
//
//                for (Iterator it = fm.keySet().iterator(); it.hasNext();) {
//                    Object object = it.next();
//                    formats.add(new SelectItem(object, object.toString()));
//                }
//            } catch (OntoRepresentationException e) {
//                System.err.println("EX: " + e.getLocalizedMessage());
//            }
//        }
    }

    public void updateTypes(ValueChangeEvent event) {
//        String format = event.getNewValue().toString();
//        setCurrentFormat(format);
//        checkTFK();
//        if ("nothing".equals(format)) {
//            types.clear();
//            Map tm = TypeOntoQuestioner.getAllTypes();
//            for (Iterator it = tm.keySet().iterator(); it.hasNext();) {
//                Object object = it.next();
//                types.add(new SelectItem(object, object.toString()));
//            }
//        } else {
//            try {
//                Map tm = TypeOntoQuestioner.getTypesForFormat(format);
//                types.clear();
//
//                for (Iterator it = tm.keySet().iterator(); it.hasNext();) {
//                    Object object = it.next();
//                    types.add(new SelectItem(object, object.toString()));
//                }
//            } catch (OntoRepresentationException e) {
//                System.err.println("EX: " + e.getLocalizedMessage());
//            }
//        }
    }

    private void checkTFK() {
//        currentTfk.clear();
//        if ((!"nothing".equals(currentType)) && (!"nothing".equals(currentFormat))) {
//            try {
//                Map tfkm = TypeOntoQuestioner.getRepNameForTypeFormat(currentType, currentFormat);
//                for (Iterator it = tfkm.keySet().iterator(); it.hasNext();) {
//                    Object object = it.next();
//                    currentTfk.add(object);
//                }
//            } catch (OntoRepresentationException o) {
//                System.err.println("EX: " + o.getLocalizedMessage());
//            }
//        } else {
//            currentTfk.clear();
//        }
    }
}
