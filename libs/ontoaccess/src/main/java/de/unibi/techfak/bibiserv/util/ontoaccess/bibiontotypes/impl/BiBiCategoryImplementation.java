/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team,
 * http://bibiserv.cebitec.uni-bielefeld.de, All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License at
 * http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. When distributing the software, include this
 * License Header Notice in each file. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 *
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */
package de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl;

import com.hp.hpl.jena.ontology.OntModel;
import de.unibi.techfak.bibiserv.util.ontoaccess.ToolOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiCategory;

/**
 *
 * @author sven
 */
public class BiBiCategoryImplementation implements BiBiCategory {

    private String myOntKey;
    private String myDescription;

    /**
     * Initializing Constructor for a Category
     * @param key 
     */
    public BiBiCategoryImplementation(String key) {
        myOntKey = key;
        OntModel m = ToolOntoQuestioner.getModel();
        //initialize the description of this Category
        myDescription = m.getIndividual(ToolOntoQuestioner.bibitoolsns + key).getPropertyValue(m.getProperty(ToolOntoQuestioner.bibitoolsns + "hasCategoryDescription")).asLiteral().getLexicalForm();
    }

    public String getDescription() {
        return myDescription;
    }

    public String getID() {
       return myOntKey;
    }
}
