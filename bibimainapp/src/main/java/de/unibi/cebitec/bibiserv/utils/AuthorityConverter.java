package de.unibi.cebitec.bibiserv.utils;

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 BiBiServ Curator Team"
 *
 * Contributor(s):  Christian Henke
 *
 */

import de.unibi.cebitec.bibiserv.web.administration.beans.Authority;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(forClass = Authority.class,value="authorityConverter")
public class AuthorityConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        try {
            return Authority.valueOf(string);
        } catch (EnumConstantNotPresentException e) {
            throw new IllegalArgumentException("Invalid authority string provided to converter!");
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof Authority) {
            return ((Authority) o).name();
        } else {
            throw new IllegalArgumentException("Object has to be of type Authority!");
        }
    }
}
