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
package de.unibi.techfak.bibiserv.util.validate.factory;

import de.unibi.cebitec.bibiserv.util.validate.Validator;
import de.unibi.techfak.bibiserv.util.ontoaccess.TypeOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shartmei
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class ValidatorFactory {
    
    // disabled caching because of new fast ontology
   // private static Map<String, String> classNameCache = new HashMap<String,String>();

    public static Validator makeValidatorFor(String typeKey) throws ValidatorFactoryException {
        String classname = null;
        try {
          
//            // first try if it was already cached
//            if(classNameCache.containsKey(typeKey)){
//                classname = classNameCache.get(typeKey);
//            }
            
//             if not cached get it one time and cache it
//            if(classname==null){
                classname = TypeOntoQuestioner.getValidatorClassnameForKey(typeKey);
//                classNameCache.put(typeKey, classname);
//            }
            
            //ask ontology for validator class for this typeKey, create and return new Object
            Class theClass = Class.forName(classname);
            return (Validator) theClass.newInstance();
        } catch (OntoAccessException ex) {
            throw new ValidatorFactoryException("Could not create Validator: no information in ontology for this validator.", ex);
        } catch (ClassNotFoundException ex) {
            throw new ValidatorFactoryException("Could not create Validator: validator class was not found in classpath. "+classname+".", ex);
        } catch (InstantiationException ex) {
            throw new ValidatorFactoryException("Could not create Validator: validator class must be concrete.", ex);
        } catch (IllegalAccessException ex) {
            throw new ValidatorFactoryException("Could not create Validator: validator class must have a no-arg constructor.", ex);
        }
    }
}
