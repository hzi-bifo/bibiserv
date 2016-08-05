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
 * "Portions Copyrighted 2010 BiBiServ"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes;

import de.unibi.cebitec.bibiserv.util.validate.Validator;
import de.unibi.cebitec.bibiserv.util.visualizer.Visualizer;
import de.unibi.cebitec.bibiserv.util.visualizer.factory.VisualizerFactoryException;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;
import de.unibi.techfak.bibiserv.util.validate.factory.ValidatorFactoryException;
import java.net.URI;
import java.util.List;

/**
 *
 * @author Sven Hartmeier
 */
public interface OntoRepresentation {

    /**
     * Enumeration of possible classes of representation
     */
    enum representationType {

        XML, PRIMITIVE
    };

    /**
     * Enumeration of possible validation strictness levels of a representation
     */
    enum strictness {

        STRICT, AMBIGUOUS;
    }

    /**
     * Enumeration of possible cardinality levels of a representation,
     * single, multiple
     */
    enum cardinalitylevel {

        SINGLE, MULTI, HONEYBADGER;
    }

    /**
     * Enumeration of possible contents of a representation,
     * NA, DNA, RNA, AA (so far)
     */
    enum content {

        NA, DNA, RNA, AA, UNKNOWN
    }

    /**
     * Enumeration of possible datastructures of a representation,
     * Alignment, Sequence, Structure (so far)
     */
    enum datastructure {

        SEQUENCE, ALIGNMENT, STRUCTURE, STRUCTUREALIGNMENT, UNKNOWN
    }

    /**
     * Gets the type key name of this OntoRepresentation
     * @return - the key name of this OntoRepresentation
     */
    public String getKey();

    /**
     * Gives the representation type (one of XML, PRIMITIVE);
     * @return the representation type
     */
    public representationType getType();

    /**
     * Gets the implementation (primitive) type for this OntoRepresentation
     * @return - the implementation type String or NULL for XML types
     */
    public String getImplementationType();

    //Give Null on standard type
    public URI getNameSpace();

    public URI getSchemaLocation();

    /**
     * Returns the label of the format of the representation.
     * @return label of the format
     */
    public String getFormatLabel();
    
     /**
     * Returns localName of the Representation individual.
     * @return localName of the Representation
     */
    @Deprecated
    public String getLabel();
    
    /**
     * Returns a generated String containing a human-readable content description
     * @return human-readable content description
     */
    public String getContentDescription();
    
    /**
     * Returns a generated String containing a human-readable content description extended by format name
     * @return human-readable content description and format name
     */
    public String getExtendedDescriptionLabel();
    
    /**
     * Gets a Validator object for this OntoRepresentation
     * @return - a Validator object which can validate data of this representation type
     */
    public Validator getValidator() throws ValidatorFactoryException;

    /**
     * Gets the Visualizers this OntoRepresentation
     * @return - a List of Visualizer objects which can process data of this representation type
     */
    public List<Visualizer> getVisualizers() throws VisualizerFactoryException;

    /**
     * 
     * @return - the strictness level (as an enum) of this representation 
     */
    public strictness getStrictness();

    /**
     * 
     * @return - the cardinality (as an enum) of this representation 
     */
    public cardinalitylevel getCardinality();

    /**
     * 
     * @return  - the content (as an enum) of this representation 
     */
    public content getContent();

    /**
     * 
     * @return  - the datastructure(s) enum(s) of this representation 
     */
    public datastructure getStructure();

    /**
     * Provides all OntoRepresentations which are convertible to this one.
     * If there are no sibling representations, the List is empty!
     * @return - a List of OntoRepresentations
     */
    public List<OntoRepresentation> getSiblingRepresentations() throws OntoAccessException;
    
    /**
     * Returns a list of Urls (one per String) of synonyms in the Edam Ontology.
     * @return 
     */
    public List<String> getEdamSynonyms();
}
