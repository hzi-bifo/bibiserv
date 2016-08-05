/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2016 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2016 BiBiServ"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.visualizer.factory;

import de.unibi.cebitec.bibiserv.util.visualizer.Visualizer;
import de.unibi.techfak.bibiserv.util.ontoaccess.TypeOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;

/**
 *
 * @author Sven Hartmeiert (1st version - prototype), Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de, Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class VisualizerFactory {
    
   // private static Map<String, Class> classCache = new HashMap<String, Class>();
    

    public static Visualizer makeVisualizerFor(String vizKey) throws VisualizerFactoryException {
        try {
            
            Class theClass = null;
            
//            // if class is already cached get it from cache
//            if(classCache.containsKey(vizKey)){
//                theClass = classCache.get(vizKey);
//            } else {
                 //ask ontology for validator class for this typeKey, create and return new Object
                theClass = Class.forName(TypeOntoQuestioner.getVisualizerClassnameForKey(vizKey));
                // cache result
//                classCache.put(vizKey, theClass);
//            }

            return (Visualizer) theClass.newInstance();
        } catch (OntoAccessException ex) {
            throw new VisualizerFactoryException("Could not create Visualizer: no information in ontology for this Visualizer.", ex);
        } catch (ClassNotFoundException ex) {
            throw new VisualizerFactoryException("Could not create Visualizer: Visualizer class was not found in classpath.", ex);
        } catch (InstantiationException ex) {
            throw new VisualizerFactoryException("Could not create Visualizer: Visualizer class must be concrete.", ex);
        } catch (IllegalAccessException ex) {
            throw new VisualizerFactoryException("Could not create Visualizer: Visualizer class must have a no-arg constructor.", ex);
        }
    }
}
