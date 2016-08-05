package de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes;

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

/**
 *
 * @author sven
 */
public interface BiBiFunction {

    //short ID of the tool
    public String getFunctionID();
    
    //full Ontology URL of this function
    public String getFunctionURI();

    //functions to get the input and output representations of this tool
    public OntoRepresentation getInputRep();
    public OntoRepresentation getOutputRep();
    
    //function to get the ontology URI of the Tool this function belongs to
    public String getToolURI();

}
