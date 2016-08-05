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

import de.unibi.cebitec.bibiserv.util.visualizer.Visualizer;
import de.unibi.cebitec.bibiserv.util.visualizer.factory.VisualizerFactory;
import de.unibi.techfak.bibiserv.util.ontoaccess.TypeOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains functions to get all visualizers for a given typeKey
 * and for for building up an applying visualizer
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class UniversalVisualizer {
    
    private static final Map<String,List<String>> visualizerCache = new HashMap<String,List<String>>();
    
    /**
     * Returns all visualizer possible for the given type.
     * @param typeKey type to display with visualizers
     * @return List of names of all possible visualizers.
     */
    public static List<String> getVisualizer(String typeKey){
        
        // first test if already cached
        if(visualizerCache.containsKey(typeKey)){
            return visualizerCache.get(typeKey);
        }
        
        // not cached, build up cache
        try {
            // get from ontology
            List<String> result = TypeOntoQuestioner.getVisualizersForKey(typeKey);
            // sort, so that is always the same order
            Collections.sort(result);
           
            visualizerCache.put(typeKey, result);
            
            return result;
            
        } catch (OntoAccessException ex) {
            return new ArrayList<String>();
        }
    }
    
    /**
     * Creates a visualizer and uses its function to create the xhtml.
     * @param visualizer Name of the Visualizer to use.
     * @param output Object to display
     * @return the generated xhtml
     */
    public static String applyVisualizer(String visualizer, Object output)
    {
        try {
            Visualizer visualizerObject = VisualizerFactory.makeVisualizerFor(visualizer);
            
            return visualizerObject.showThis(output);
        } catch (Exception ex) {
           // TODO: Probably not the best way to tell the user visualizer failed.
           // But on the other hand this should not occur in  valid tool.
           return "Visualizer could not be created: "+ex; 
        }
    }

}
