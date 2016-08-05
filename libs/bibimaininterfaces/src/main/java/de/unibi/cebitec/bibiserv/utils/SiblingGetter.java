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

import de.unibi.techfak.bibiserv.util.ontoaccess.TypeOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoRepresentationImplementation;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A helper-class for retrieving and caching of sibling for a given OntoRepresentation object. 
 * 
 * @author Thomas Gatter - tgatter(at)cebvitec.uni-bielefeld.de
 */
public class SiblingGetter {

    private static Map<String, List<OntoRepresentation>> siblingCache =
            new HashMap<String, List<OntoRepresentation>>();
    private static Map<String, List<OntoRepresentation>> siblingConvertableToCache =
            new HashMap<String, List<OntoRepresentation>>();
    private static Map<String, List<OntoRepresentation>> siblingConvertableFromCache =
            new HashMap<String, List<OntoRepresentation>>();
    private static Map<String, List<OntoRepresentation>> siblingStreamConvertableToCache =
            new HashMap<String, List<OntoRepresentation>>();
    private static Map<String, List<OntoRepresentation>> siblingStreamConvertableFromCache =
            new HashMap<String, List<OntoRepresentation>>();
    
    
     /**
      * Returns all sibling with same attributes.
      * @param onto Object to get sibling of.
      * @return list of all siblings.
      */
    public static List<OntoRepresentation> getSiblings(OntoRepresentation onto) {

        // test if cached
        if (siblingCache.containsKey(onto.getKey())) {
            
            List<OntoRepresentation> siblings = new ArrayList<OntoRepresentation>();
            siblings.addAll( siblingCache.get(onto.getKey()));
            return siblings;
        }

        // If not create new list. Cahching also takes place in the OntoRepresentation object, but
        // it should be possible for more than one OntoRepresentation object with the same key to exist.
        List<OntoRepresentation> siblings;
        try {
            siblings = onto.getSiblingRepresentations();

            Comparator c = new Comparator() { // eigene Vergleichsoperation!

                public int compare(Object arg0, Object arg1) {
                    String key1 = ((OntoRepresentation) arg0).getKey();
                    String key2 = ((OntoRepresentation) arg1).getKey();
                    return key1.compareTo(key2);
                }
            };

            Collections.sort(siblings, c);
        } catch (OntoAccessException ex) {
            // This only occures if the typekeys are wrong. THIS SHOULD NEVER HAPPEN IN A CORRECT TOOL!
            return new ArrayList<OntoRepresentation>();
        }

        // cache result
        siblingCache.put(onto.getKey(), siblings);

        List<OntoRepresentation> retSiblings = new ArrayList<OntoRepresentation>();
        retSiblings.addAll(siblings);
        return retSiblings;
    }
    
    
    /**
     * Returns all possible OntoRepresantions that the given representation con be converted to.
     * @param from Convert from this to result
     * @return all possible OntoRepresantions that the given representation con be converted to
     */
    public static List<OntoRepresentation> getSiblingsConvertableFrom(OntoRepresentation from) {
        // test if cached
        if (siblingConvertableToCache.containsKey(from.getKey())) {
            return siblingConvertableToCache.get(from.getKey());
        }
        
        // If not create new list.
        List<OntoRepresentation> siblings = getSiblings(from);
        Iterator<OntoRepresentation> siblingIterator = siblings.iterator();
        
        while(siblingIterator.hasNext()) {
            OntoRepresentation to = siblingIterator.next();
            try {
                     // filter out all sibling that can not be reached
                    TypeOntoQuestioner.getConverterChainFromKeyToKey(from.getKey(), to.getKey());
            } catch (OntoAccessException ex) {
                    // no chain, remove!
                    siblingIterator.remove();
            }
        }
        
        // cache result
        siblingConvertableToCache.put(from.getKey(), siblings);

        return siblings;
    }
    
    /**
     * Returns all possible OntoRepresantions that can be converted to the given representation.
     * @param to Convert from result to this
     * @return all possible OntoRepresantions that can be converted to the given representation
     */
    public static List<OntoRepresentation> getSiblingsConvertableTo(OntoRepresentation to) {
        // test if cached
        if (siblingConvertableFromCache.containsKey(to.getKey())) {
            return siblingConvertableFromCache.get(to.getKey());
        }
        
        // If not create new list.
        List<OntoRepresentation> siblings = getSiblings(to);
        Iterator<OntoRepresentation> siblingIterator = siblings.iterator();
        
        while(siblingIterator.hasNext()) {
            OntoRepresentation from = siblingIterator.next();
            try {
                     // filter out all sibling that can not be reached
                    TypeOntoQuestioner.getConverterChainFromKeyToKey(from.getKey(), to.getKey());
            } catch (OntoAccessException ex) {
                    // no chain, remove!
                    siblingIterator.remove();
            }
        }
        
        // cache result
        siblingConvertableFromCache.put(to.getKey(), siblings);

        return siblings;
    }
    
    /**
     * Returns all possible OntoRepresantions that the given representation con be converted to.
     * !!ONLY USING STREAMCONVERTER!!
     * @param from Convert from this to result
     * @return all possible OntoRepresantions that the given representation con be converted to
     */
    public static List<OntoRepresentation> getSiblingsStreamConvertableFrom(OntoRepresentation from) {
        // test if cached
        if (siblingStreamConvertableToCache.containsKey(from.getKey())) {
            return siblingStreamConvertableToCache.get(from.getKey());
        }
        
        // If not create new list.
        List<OntoRepresentation> siblings = getSiblings(from);
        Iterator<OntoRepresentation> siblingIterator = siblings.iterator();
        
        while(siblingIterator.hasNext()) {
            OntoRepresentation to = siblingIterator.next();
            try {
                     // filter out all sibling that can not be reached
                    TypeOntoQuestioner.getStreamConverterChainFromKeyToKey(from.getKey(), to.getKey());
            } catch (OntoAccessException ex) {
                    // no chain, remove!
                    siblingIterator.remove();
            }
        }
        
        // cache result
        siblingStreamConvertableToCache.put(from.getKey(), siblings);

        return siblings;
    }
    
    /**
     * Returns all possible OntoRepresantions that can be converted to the given representation.
     * !!ONLY USING STREAMCONVERTER!!
     * @param to Convert from result to this
     * @return all possible OntoRepresantions that can be converted to the given representation
     */
    public static List<OntoRepresentation> getSiblingsStreamConvertableTo(OntoRepresentation to) {
        // test if cached
        if (siblingStreamConvertableFromCache.containsKey(to.getKey())) {
            return siblingStreamConvertableFromCache.get(to.getKey());
        }
        
        // If not create new list.
        List<OntoRepresentation> siblings = getSiblings(to);
        Iterator<OntoRepresentation> siblingIterator = siblings.iterator();
        
        while(siblingIterator.hasNext()) {
            OntoRepresentation from = siblingIterator.next();
            try {
                     // filter out all sibling that can not be reached
                    TypeOntoQuestioner.getStreamConverterChainFromKeyToKey(from.getKey(), to.getKey());
            } catch (OntoAccessException ex) {
                    // no chain, remove!
                    siblingIterator.remove();
            }
        }
        
        // cache result
        siblingStreamConvertableFromCache.put(to.getKey(), siblings);

        return siblings;
    }
    
    
    public static void main(String[] args) throws URISyntaxException, OntoAccessException {
        OntoRepresentationImplementation ontoRepresentationImplementation = new OntoRepresentationImplementation("FastQAll_RNA");
        getSiblingsConvertableTo(ontoRepresentationImplementation);
        for (OntoRepresentation rep : getSiblingsConvertableFrom(ontoRepresentationImplementation)) {
            System.out.println(rep.getFormatLabel());
        }
    }
    
}
