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
import de.unibi.techfak.bibiserv.util.ontoaccess.ToolOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiFunction;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sven
 */
public class BiBiFunctionImplementation implements BiBiFunction {

    //declare and iniyialize default content variables
    private String myFunctionID = "";
    private String myURI = "";
    private OntoRepresentation inrep = null;
    private OntoRepresentation outrep = null;
    private String myTool = "";

    public BiBiFunctionImplementation(String fkey) throws OntoAccessException {
        Individual f = ToolOntoQuestioner.getFunctionForURI(fkey);
        myURI = f.getURI();
        myFunctionID = f.getLocalName();
        try {
            inrep = new OntoRepresentationImplementation(f.getPropertyResourceValue(ToolOntoQuestioner.hasInput).as(Individual.class).getPropertyResourceValue(ToolOntoQuestioner.hasType).getLocalName());
            outrep = new OntoRepresentationImplementation(f.getPropertyResourceValue(ToolOntoQuestioner.hasOutput).as(Individual.class).getPropertyResourceValue(ToolOntoQuestioner.hasType).getLocalName());
            myTool = f.getPropertyResourceValue(ToolOntoQuestioner.hasTool).getURI();
        } catch (URISyntaxException ex) {
            Logger.getLogger(BiBiFunctionImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getFunctionID() {
        return myFunctionID;
    }

    public String getFunctionURI() {
        return myURI;
    }

    public OntoRepresentation getInputRep() {
        return inrep;
    }

    public OntoRepresentation getOutputRep() {
        return outrep;
    }

    public String getToolURI() {
        return myTool;
    }
}
