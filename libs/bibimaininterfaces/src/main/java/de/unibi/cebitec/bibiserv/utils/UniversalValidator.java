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

import de.unibi.cebitec.bibiserv.util.streamvalidate.StreamValidator;
import de.unibi.cebitec.bibiserv.util.validate.AlignmentValidator;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator;
import de.unibi.cebitec.bibiserv.util.validate.ValidationException;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.Validator;
import de.unibi.cebitec.bibiserv.util.validate.impl.RNAStructAlignmentML_Validator;
import de.unibi.cebitec.bibiserv.util.validate.impl.RNAStructML_Validator;
import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.util.validate.factory.ValidatorFactoryException;
import java.util.HashMap;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation.datastructure;
import java.io.StringReader;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


/**
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class UniversalValidator {

    public  static final int partValidationLength = 1200*1024;
    
    
    /**
     * A HashMap for caching a converter chain for known key-pairs. Order is From, To, Chain.
     */
    private static final Map<String, Validator> validatorCache = new HashMap<>();

    public static String getValidatorImlementation(OntoRepresentation rep) throws BiBiToolsException {
        Validator validator = null;

        if (validatorCache.containsKey(rep.getKey())) {
            validator = validatorCache.get(rep.getKey());
        }
        if (validator == null) {
            try {
                validator = rep.getValidator();
            } catch (ValidatorFactoryException ex) {
                throw new BiBiToolsException(703, "No Validator for type " + rep.getKey()
                        + " available: Message was " + ex);
            }

            if (validator == null) {
                throw new BiBiToolsException(703, "No Validator for type " + rep.getKey()
                        + " available. Validator is null.");
            }

            validatorCache.put(rep.getKey(), validator);
        }

        return validator.getClass().getCanonicalName();
    }
    
    
    /**
     * Validates the given Object for type rep.
     * @param object Object to validate.
     * @param rep Type to validate against.
     * @throws BiBiToolsException - no validator could be created
     * @throws ValidationException - object is unvalid
     */
    public static void validateFirstPart(Object object, OntoRepresentation rep, ValidationConnection con) throws BiBiToolsException,
            ValidationException {
        validate(object, rep, null, true, con);
    }
        /**
     * Validates the given Object for type rep.
     * @param object Object to validate.
     * @param rep Type to validate against.
     * @return
     * @throws BiBiToolsException - no validator could be created
     * @throws ValidationException - object is unvalid
     */
     public static Object validate(Object object, OntoRepresentation rep) throws BiBiToolsException,
            ValidationException {
         return validate(object, rep, null, false, null);
     }
     
     /**
      * Validates the given Object with given validator.
      * 
      * @param object Object to validate
      * @param v Validator to be used 
      * @return
      * @throws BiBiToolsException
      * @throws ValidationException 
      */
     public static Object validate(Object object, Validator v) throws BiBiToolsException, ValidationException {
         return validate(object, null, v, false, null);
     }
     
     /**
      * Convenience validate method to be used in generated BiBiTools apps. 
      * Either OntoRepresentation or Validator should be a null object.
      * 
      * @param object Object to validate
      * @param rep Type to validate against
      * @param v Validator to be used 
      * @return
      * @throws BiBiToolsException
      * @throws ValidationException 
      */
     public static Object validate(Object object, OntoRepresentation rep, Validator v) throws BiBiToolsException, ValidationException {
         return validate(object, rep, v, false, null);
     }
    
     /**
      * Internal method that is used by other validate function.
      * 
      * jkrueger (02/04/15) :
      * Add support for an external validator. If external validator is given 
      * (not null) it used. The Ontorepresentation could be (but mustn't ) a null 
      * object ...
      * 
      * @param object
      * @param rep
      * @param validator
      * @param part
      * @param con
      * @return
      * @throws BiBiToolsException
      * @throws ValidationException 
      */
    private static Object validate(Object object,  OntoRepresentation rep, Validator validator, boolean part, ValidationConnection con) throws BiBiToolsException,
            ValidationException {

        
        if (validator == null) {
     
            // check if rep is set, that shouldn't be happen, but never know, anything can happen :-)
            if (rep == null) {
                throw new BiBiToolsException(703, "OntoRepresentation object is unset!");
            }
     
            // is the even an implementation of this type? 
            if (rep.getImplementationType() == null) {
                throw new BiBiToolsException(703, "No ImplementationsType for type "
                    + rep.getKey() + " available.");
            }

      

            // first check if this is already cached
            if (validatorCache.containsKey(rep.getKey())) {
                validator = validatorCache.get(rep.getKey());
            }

            // if not create it once and cache it if it is correct
            if (validator == null) {
                try {
                    validator = rep.getValidator();
                } catch(ValidatorFactoryException ex) {
                throw new BiBiToolsException(703, "No Validator for type " + rep.getKey()
                        + " available: Message was "+ex);
            }

            if (validator == null) {
                throw new BiBiToolsException(703, "No Validator for type " + rep.getKey()
                        + " available. Validator is null.");
            }

            validatorCache.put(rep.getKey(), validator);
        }
        }

        // determine validator class, from most specified (Alignmentvalidator) to most general (Validator)
        if (validator instanceof AlignmentValidator) {

            // Wenn eine Validator vom Typ AlignmentValidator ist dann kann er trotzdem
            // als SequenceValidator benutzt werden wenn es das Format unterstuetzt.
            // (Der Fasta Validator ist z.B. ein solcher Validator). In diesem Fall
            // muessen die Eigenschaften "Structure", "Content", "Cardinality" und "Strictness"
            // die Validator Eigenschaften festlegen.

            AlignmentValidator align = (AlignmentValidator) validator;

            // check if structure is an alignment or structurealignment and set property
            if (rep.getStructure().equals(datastructure.ALIGNMENT) || rep.getStructure().equals(
                    datastructure.STRUCTUREALIGNMENT)) {
                align.setAlignment(true);
                // cardinality is multi (=default)
            } else {
                align.setAlignment(false);
                // check cardinality
                if (rep.getCardinality() != null) {
                    switch (rep.getCardinality()) {
                        case HONEYBADGER:
                            align.setCardinality(SequenceValidator.CARDINALITY.honeybadger);
                            break;
                        case MULTI:
                            align.setCardinality(SequenceValidator.CARDINALITY.multi);
                            break;
                        case SINGLE:
                            align.setCardinality(SequenceValidator.CARDINALITY.single);
                            break;
                    }
                }
            }

            // set content
            switch (rep.getContent()) {
                case AA:
                    align.setContent(SequenceValidator.CONTENT.AA);
                    break;
                case NA:
                    align.setContent(SequenceValidator.CONTENT.NA);
                    break;
                case DNA:
                    align.setContent(SequenceValidator.CONTENT.DNA);
                    break;
                case RNA:
                    align.setContent(SequenceValidator.CONTENT.RNA);
                    break;
                case UNKNOWN:
                    break;
            }
            // set strictness
            if (rep.getStrictness() != null) {
                switch (rep.getStrictness()) {
                    case AMBIGUOUS:
                        align.setStrictness(SequenceValidator.STRICTNESS.ambiguous);
                        break;
                    case STRICT:
                        align.setStrictness(SequenceValidator.STRICTNESS.strict);
                        break;
                }
            }
        } else if (validator instanceof SequenceValidator) {

            SequenceValidator seq = (SequenceValidator) validator;

            if (rep.getStructure().equals(datastructure.ALIGNMENT) || rep.getStructure().equals(
                    datastructure.STRUCTUREALIGNMENT)) {
                throw new BiBiToolsException(703, "Validator " + validator.getClass().
                        getName()
                        + " is an SequenceValidator specified for representation '" + rep.getKey()
                        + "' supports no alignment and structurealignment as structure!");
            }
            // check cardinality
            if (rep.getCardinality() != null) {
                switch (rep.getCardinality()) {
                    case HONEYBADGER:
                        seq.setCardinality(SequenceValidator.CARDINALITY.honeybadger);
                        break;
                    case MULTI:
                        seq.setCardinality(SequenceValidator.CARDINALITY.multi);
                        break;
                    case SINGLE:
                        seq.setCardinality(SequenceValidator.CARDINALITY.single);
                        break;
                }
            }
            // set content
            switch (rep.getContent()) {
                case AA:
                    seq.setContent(SequenceValidator.CONTENT.AA);
                    break;
                case NA:
                    seq.setContent(SequenceValidator.CONTENT.NA);
                    break;
                case DNA:
                    seq.setContent(SequenceValidator.CONTENT.DNA);
                    break;
                case RNA:
                    seq.setContent(SequenceValidator.CONTENT.RNA);
                    break;
                case UNKNOWN:
                    break;
            }
            // set strictness
            if (rep.getStrictness() != null) {
                switch (rep.getStrictness()) {
                    case AMBIGUOUS:
                        seq.setStrictness(SequenceValidator.STRICTNESS.ambiguous);
                        break;
                    case STRICT:
                        seq.setStrictness(SequenceValidator.STRICTNESS.strict);
                        break;
                }
            }
        } else if (validator instanceof RNAStructML_Validator) {
            RNAStructML_Validator seq = (RNAStructML_Validator) validator;
           // set strictness
            if (rep.getStrictness() != null) {
                switch (rep.getStrictness()) {
                    case AMBIGUOUS:
                        seq.setStrictness(SequenceValidator.STRICTNESS.ambiguous);
                        break;
                    case STRICT:
                        seq.setStrictness(SequenceValidator.STRICTNESS.strict);
                        break;
                }
            }
        } else if (validator instanceof RNAStructAlignmentML_Validator) {
            RNAStructAlignmentML_Validator seq = (RNAStructAlignmentML_Validator) validator;
           // set strictness
            if (rep.getStrictness() != null) {
                switch (rep.getStrictness()) {
                    case AMBIGUOUS:
                        seq.setStrictness(SequenceValidator.STRICTNESS.ambiguous);
                        break;
                    case STRICT:
                        seq.setStrictness(SequenceValidator.STRICTNESS.strict);
                        break;
                }
            }
        }

        ValidationResult validationresult;
        if(part && validator instanceof StreamValidator) {
            validationresult = ((StreamValidator) validator).validateThis(object, partValidationLength, con);
        } else {
            validationresult = validator.validateThis(object);
        }
        if (!validationresult.isValid()) {
            throw new ValidationException(validationresult.getMessage());
        }
        
        if(validator instanceof StreamValidator) {
                try {
                    String result = ((StreamValidator) validator).getRepairedAndUnifiedOutput();
                    if(rep.getType() == OntoRepresentation.representationType.XML) {
                        return string2Jaxb(result, Class.forName(rep.getImplementationType()));
                    }
                    return result;
                } catch (de.unibi.cebitec.bibiserv.util.streamvalidate.ValidationException | ClassNotFoundException | JAXBException ex) {
                    throw new ValidationException(ex.getMessage());
                }
        }
        
        return object;
    }
    
        
     /**
     * Converts a string to a jaxb element using the given jaxbclass.
     *
     * @param content - String to be converted to jaxb element
     * @param jaxbclass - class of the jaxbrootelement
     * @return Return the jaxb element (as object)
     *
     * @throws throws an JAXBException in the case of an error.
     */
    private static Object string2Jaxb(String content, Class jaxbclass) throws JAXBException {
        JAXBContext jaxbc = JAXBContext.newInstance(jaxbclass);
        Unmarshaller um = jaxbc.createUnmarshaller();
        JAXBElement jaxbe = (JAXBElement) um.unmarshal(new StringReader(content));
        return jaxbe.getValue();
    }
}
