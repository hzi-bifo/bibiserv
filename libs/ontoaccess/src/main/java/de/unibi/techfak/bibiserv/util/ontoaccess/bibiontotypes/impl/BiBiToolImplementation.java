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

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import de.unibi.techfak.bibiserv.util.ontoaccess.ToolOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiCategory;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiFunction;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiTool;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sven
 */
public class BiBiToolImplementation implements BiBiTool {

    //declare and iniyialize default content variables
    private String myToolID = "";
    private String myToolURI = "";
    //these should better be maps, from language code to actual content, for
    //international contents
    private String myLabel = "";
    private String myAE = ""; // AcronymExpansion :-)
    private String myDescription = "";
    private String myShortDescription = "";
    private Set<BiBiCategory> myCategories = new HashSet();
    private Set<BiBiFunction> myFunctions = new HashSet();

    public BiBiToolImplementation(String toolkey) {
        //load the individual from the ontology
        try {
            Individual tool = ToolOntoQuestioner.getToolIndForURI(toolkey);
            myToolURI=toolkey;
            //fill the standard values
            if (tool.hasProperty(ToolOntoQuestioner.hasid)) {
                myToolID = tool.getPropertyValue(ToolOntoQuestioner.hasid).asLiteral().getLexicalForm();
            }
            //System.out.println("myToolID: "+myToolID);
            if (tool.hasProperty(ToolOntoQuestioner.haslabel)) {
                myLabel = tool.getPropertyValue(ToolOntoQuestioner.haslabel).asLiteral().getLexicalForm();
            }
            //System.out.println("myLabel: "+myLabel);
            if (tool.hasProperty(ToolOntoQuestioner.hasAE)) {
                myAE = tool.getPropertyValue(ToolOntoQuestioner.hasAE).asLiteral().getLexicalForm();
            }
            // System.out.println("myAE: "+myAE);
            if (tool.hasProperty(ToolOntoQuestioner.hasdesc)) {
                myDescription = tool.getPropertyValue(ToolOntoQuestioner.hasdesc).asLiteral().getLexicalForm();
            }
            // System.out.println("myDescription: "+myDescription);
            if (tool.hasProperty(ToolOntoQuestioner.hasshortdesc)) {
                myShortDescription = tool.getPropertyValue(ToolOntoQuestioner.hasshortdesc).asLiteral().getLexicalForm();
            }
            //  System.out.println("myShortDescription: "+myShortDescription);
            //get categories
            NodeIterator cit = tool.listPropertyValues(ToolOntoQuestioner.belongstocategory);
            while (cit.hasNext()) {
                Individual node = cit.next().as(Individual.class);
                //      System.out.println("Trying Node "+ node.getLocalName());
                myCategories.add(new BiBiCategoryImplementation(node.getLocalName()));
            }

            //get functions
            NodeIterator fit = tool.listPropertyValues(ToolOntoQuestioner.hasfunction);
            while (fit.hasNext()) {
                Individual node = fit.next().as(Individual.class);
                myFunctions.add(new BiBiFunctionImplementation(node.getURI()));

            }
        } catch (OntoAccessException ex) {
            //TODO: better Exception handling
            Logger.getLogger(BiBiToolImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Set<BiBiCategory> getCategories() {
        return myCategories;
    }

    public Collection<BiBiFunction> getFunctions() {
        return myFunctions;
    }

    public String getAcronymExpansion() {
        return myAE;
    }

    public String getDescription() {
        return myDescription;
    }

    public String getLabel() {
        return myLabel;
    }

    public String getShortDescription() {
        return myShortDescription;
    }

    public String getToolID() {
        return myToolID;
    }
//    public Collection<OntoRepresentation> getInputReps() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public Collection<OntoRepresentation> getOutputReps() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    public String getOntoURI() {
        return myToolURI;
    }
}
