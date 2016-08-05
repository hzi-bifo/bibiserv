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
package de.unibi.cebitec.bibiserv.utils;

import de.unibi.cebitec.bibiserv.util.validate.ValidationException;
import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.web.beans.session.AbstractInputBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides functions for finding the correct representation of an input string.
 * Warning: Currently only string supported. Might be needed to use Object in future
 * for non-string Bioformats. However this is currently a general BibiServ-Problem.
 * 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de 
 */
public class UniversalRepresentationFinder {

    /**
     * Find the type of input. Input needs to be of a type that is a sibling to target.
     * @param input input to test
     * @param target input needs to be this type or a sibling of this type
     * @return OntoRepresentation-Objects of type
     */
    public static List<OntoRepresentation> getOntoRepresentation(String input, OntoRepresentation target) {
        return getOntoRepresentation(input, target, null, null);
    }
    
    public static List<OntoRepresentation> getOntoRepresentation(String input, OntoRepresentation target,
            AbstractInputBean inputBean, String[] args) {
        return getOntoRepresentationObject(input, target, inputBean, args);
    }
    
    public static List<OntoRepresentation> getOntoRepresentation(ValidationConnection input, OntoRepresentation target,
            AbstractInputBean inputBean, String[] args) {
        return getOntoRepresentationObject(input, target, inputBean, args);
    }

    
    /**
     * Find the type of input. Input needs to be of a type that is a sibling to target.
     * @param input input to test
     * @param target input needs to be this type or a sibling of this type
     * @param inputBean if not null set values in this inputbean
     * @param args arguments for the inputBean if given
     * @return OntoRepresentation-Objects of the type
     */
    private static List<OntoRepresentation> getOntoRepresentationObject(Object input, OntoRepresentation target,
            AbstractInputBean inputBean, String[] args) {

        List<OntoRepresentation> possibleRepresentations = new ArrayList<>();
        
        // tagret must always be tested!
        possibleRepresentations.add(target);
        
        // add possible convertable siblings
        if (input instanceof ValidationConnection) {
            possibleRepresentations.addAll(SiblingGetter.getSiblingsStreamConvertableTo(target));
        } else {
            possibleRepresentations.addAll(SiblingGetter.getSiblingsConvertableTo(target));
        }

        List<OntoRepresentation> reps = new ArrayList<>();
        
        StringBuilder message = new StringBuilder();

        for (OntoRepresentation representation : possibleRepresentations) {
            // all validators except string as input, conversion to xml is done in input object!
            if (representation.getType() == OntoRepresentation.representationType.PRIMITIVE || representation.getType() == OntoRepresentation.representationType.XML) {

                try {
                    // try if it can be validated as that type, exception if not
                    if(input instanceof ValidationConnection)
                    {
                        UniversalValidator.validateFirstPart( ((ValidationConnection) input).getReader(), representation, (ValidationConnection) input);
                    } else {
                        UniversalValidator.validate(input, representation);
                    }

                    // if inputbean is given set all needed stuff
                    reps.add(representation);

                } catch (BiBiToolsException e) {
                    message.append("<tr><td>").append(representation.getKey()).append(
                            "</td><td>" ).append( e.getMessage() ).append( "</td></tr>");
                } catch (ValidationException e) {
                    message.append("<tr><td>").append(representation.getKey()).append(
                            "</td><td>" ).append( e.getMessage() ).append( "</td></tr>");
                }
            } 
        }
        if (inputBean != null) {
            inputBean.getInput().setInput(input);
            inputBean.getInput().setRepresentations(reps);
            // set random choosen
            if(reps.size() > 0) {
                inputBean.getInput().setChosen(reps.get(0));
            }
            setMessage(inputBean, args, message);
        }
        return reps;
    }

    private static void setMessage(AbstractInputBean inputBean, String[] args, StringBuilder message) {
        inputBean.getInput().setMessage("<table><tr><th>" + args[5] + "</th><th>" + args[6]
                + "</th></tr>" + message.toString() + "</table>");
    }   
}
