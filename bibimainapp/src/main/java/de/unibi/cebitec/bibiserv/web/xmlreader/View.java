/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2014 BiBiServ Curator Team"
 *
 * Contributor(s): Jan krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *
 */

package de.unibi.cebitec.bibiserv.web.xmlreader;

/**
 * Container class for ViewTypes
 * 
 * contains properties for name, whether this view should be visible and 
 * subtype.The later two are optional. 
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class View {
    
    
    private String type;
    
    private boolean visible;
    
    private String subtype;
    
    private String id;
   
    
    public View(){
    }
    
    
    public View(String type){
        this(type, null,true,null);
    }
  
    
    public View(String type, String id, boolean visible, String subtype){
        this.id = id;
        this.type = type;
        this.visible = visible;
        this.subtype = subtype;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }
    
    
    public void setId(String id){
        this.id=id;
    }
    
    public String getId(){
        return id;
    }
    
    @Override
    public String toString(){
        if (id != null) {
            return id;
        } 
        return type;
    }

    /**
     * Two ViewType are equal by definition if only their type is equal, independent 
     * of the other properties.
     * 
     * @todo Think about this comparison ... 
     *       a.type == b.type, if a.subtype == null || b.subtype == null
     *       a.type == b.type && a.subtype == b.subtype, if a.subtype != null && b.subtype != null
     * 
     *      Maybe this is a better comparison then currently one.
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof View){
            
            View v = (View)obj;
            
            if (v.getId() != null && id != null) {
                return v.getId().equals(id);
            }
            return v.getType().equals(type);
            
            
            //return ((View)obj).getType().equals(type);       
        }
        return false;
    }

    @Override
    protected Object clone()  {
        return new View(this.type,this.id,this.visible,this.subtype);
   }
    
   
}
