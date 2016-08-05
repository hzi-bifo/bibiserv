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
import java.util.Collection;

/**
 *
 * @author sven
 */
public interface BiBiTool {

    //a row of standard accessors;
    public String getToolID();

    public String getAcronymExpansion();

    public String getLabel();

    public String getDescription();

    public String getShortDescription();

    public String getOntoURI();
    /**
     * Function to retrieve the Categories this tool belongs to
     *
     * @return a List of Categories 
     */
    public Collection<BiBiCategory> getCategories();

    public Collection<BiBiFunction> getFunctions();

//    public Collection<OntoRepresentation> getInputReps();
//
//    public Collection<OntoRepresentation> getOutputReps();
}
