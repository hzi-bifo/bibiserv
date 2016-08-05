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
package de.unibi.techfak.bibiserv.util.convert.factory;

import de.unibi.cebitec.bibiserv.util.convert.Converter;
import de.unibi.techfak.bibiserv.util.ontoaccess.TypeOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;


/**
 * Creates an instance of a converter for given names of from and to.
 * 
 * @author Sven Hartmeier
 * @author Thomas Gatter -  tgatter(at)techfak.uni-bielefeld.de 
 *
 */
public class ConverterFactory {

    /**
     * A caching structure for known converter-class names.
     */
//    private static Map<String, Map<String, String>> classNameCache =
//            new HashMap<String, Map<String, String>>();

    public static Converter makeConverter(String fromTypeKey, String toTypeKey) throws
            ConverterFactoryException {
        try {
            // first test if class name was already cached
            String myConverterClassName = null;
//            if (classNameCache.containsKey(fromTypeKey)) {
//                Map<String, String> innerMap = classNameCache.get(fromTypeKey);
//                if (innerMap.containsKey(toTypeKey)) {
//                    myConverterClassName = innerMap.get(toTypeKey);
//                }
//            }
            // it was not cached, so get it from ontoaccess and cach it
//            if (myConverterClassName == null) {
                myConverterClassName = TypeOntoQuestioner.getConverterClassnameFor(fromTypeKey,
                        toTypeKey);

//                Map<String, String> innerMap;
//                if (classNameCache.containsKey(fromTypeKey)) {
//                    innerMap = classNameCache.get(fromTypeKey);
//                } else {
//                    innerMap = new HashMap<String, String>();
//                    classNameCache.put(fromTypeKey, innerMap);
//                }
//                innerMap.put(toTypeKey, myConverterClassName);
//            }

            Class cl = Class.forName(myConverterClassName);
            return (Converter) cl.newInstance();
        } catch (OntoAccessException ex) {
            throw new ConverterFactoryException(
                    "Could not create Converter: no information in ontology for this converter.", ex);
        } catch (ClassNotFoundException ex) {
            throw new ConverterFactoryException(
                    "Could not create Converter: converter class was not found in classpath.", ex);
        } catch (InstantiationException ex) {
            throw new ConverterFactoryException(
                    "Could not create Converter: converter class must be concrete.", ex);
        } catch (IllegalAccessException ex) {
            throw new ConverterFactoryException(
                    "Could not create Converter: converter class must have a no-arg constructor.",ex);
        }
    }
}
