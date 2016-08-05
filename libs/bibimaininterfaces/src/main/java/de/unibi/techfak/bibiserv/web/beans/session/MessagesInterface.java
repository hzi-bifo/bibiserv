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
package de.unibi.techfak.bibiserv.web.beans.session;

/**
 *
 * @author Sven Hartmeier - shartmei[aet]cebitec.uni-bielefeld.de
 */
public interface MessagesInterface {

    /**
     * This method returns a basic property, identified by key
     *
     * @param key - the property key of the requested property
     * @return The requested property string.
     */
    public String property(String key);

    /**
     * This method returns a parametrized property, identified by key and
     * parameters. The parameters need to be one string, containing the
     * different params in a comma-separated list.
     */
    public String property(String key, String args_csv);
}
