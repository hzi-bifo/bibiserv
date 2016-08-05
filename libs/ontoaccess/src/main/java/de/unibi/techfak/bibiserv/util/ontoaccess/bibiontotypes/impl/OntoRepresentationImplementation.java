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
package de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl;


import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import de.unibi.cebitec.bibiserv.util.validate.Validator;
import de.unibi.cebitec.bibiserv.util.visualizer.Visualizer;
import de.unibi.cebitec.bibiserv.util.visualizer.factory.VisualizerFactory;
import de.unibi.cebitec.bibiserv.util.visualizer.factory.VisualizerFactoryException;
import de.unibi.techfak.bibiserv.util.ontoaccess.TypeOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.util.validate.factory.ValidatorFactory;
import de.unibi.techfak.bibiserv.util.validate.factory.ValidatorFactoryException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class OntoRepresentationImplementation implements OntoRepresentation {

    //the several properties of an OntoRepresentation
    private String myTypeKey = null;
    
    private representationType myType = null;
    private String myImplementationType = null;
    private URI myNameSpace = null;
    private URI mySchemaLocation = null;
    private content myContent = content.UNKNOWN;
    private datastructure myStruct = datastructure.UNKNOWN;
    private cardinalitylevel myCardinality = null;
    private strictness myStrictness = null;
    private Validator myValidator = null;
    private List<Visualizer> myVisualizers = null;
    private List<OntoRepresentation> siblist = null;
    private List<String> edamSynonyms = null;
    
    // label generation
  
    private String formatLabel;
    private String fullDescriptionLabel;
    private String extendedDescriptionLabel;

    /**
     * Creates a Java bean for a Representation in the Ontology according
     * to the Representation's key
     * @param typeKey - the internal label of the representation
     * @throws URISyntaxException
     * @throws OntoAccessException 
     */
    public OntoRepresentationImplementation(String typeKey) throws URISyntaxException, OntoAccessException {
        if (typeKey == null || typeKey.equalsIgnoreCase("")) {
            throw new OntoAccessException("Could not instatiate OntoRepresentation: type key was empty or null");
        }
        
        //load the individual from the ontology
        Individual rep = TypeOntoQuestioner.getRepresentationForKey(typeKey);
        
        if (TypeOntoQuestioner.isRepresentation(rep)) {           
            this.myTypeKey = typeKey;

            //fill the 'strictness' and 'cardinalityLevel' fields of the representation
            //load the content of this rep's prtoperties and convert them to the correct enum value
            RDFNode st = rep.getPropertyValue(TypeOntoQuestioner.hasStrictness);
            if (st != null) {
                this.myStrictness = strictness.valueOf(st.asLiteral().getLexicalForm().toUpperCase());
            }    
            RDFNode ca = rep.getPropertyValue(TypeOntoQuestioner.hasCardinality);
            if (ca != null) {
                this.myCardinality = cardinalitylevel.valueOf(ca.asLiteral().getLexicalForm().toUpperCase());
            }

            //get the content type of this representation and fill varaibles accordingly
            OntResource impType = (OntResource) rep.getPropertyValue(TypeOntoQuestioner.hasImplementationType);
            if ("JAXB-Type".equals(impType.asIndividual().getOntClass(true).getLocalName())) {
                this.myType = representationType.XML;
                this.myImplementationType = impType.getPropertyValue(TypeOntoQuestioner.hasImplementingClass).asLiteral().getLexicalForm();
                this.myNameSpace = new URI(impType.getPropertyValue(TypeOntoQuestioner.hasNamespace).asLiteral().getLexicalForm());
                this.mySchemaLocation = new URI(impType.getPropertyValue(TypeOntoQuestioner.hasSchemalocation).asLiteral().getLexicalForm());
                //} else if ("Primitive".equals(iv.getOntClass().getLocalName())) {
            } else if ("Primitive".equals(impType.asIndividual().getOntClass(true).getLocalName())) {
                this.myType = representationType.PRIMITIVE;
                this.myImplementationType = impType.getPropertyValue(TypeOntoQuestioner.hasImplementingClass).asLiteral().getLexicalForm();
            } else {
                throw new OntoAccessException("Could not instatiate OntoRepresentation: REPRESENTATIONTYPE for key '" + typeKey + "' WAS NOT KNOWN");
            }
            
            //get the biological content of this representation
            //in case of problems, set to 'UNKNOWN'
            OntClass conClass = TypeOntoQuestioner.getContentForRep(rep);
            if (conClass != null) {
                try {
                    myContent = content.valueOf(conClass.getLocalName().toUpperCase());
                } catch (Exception e) {
                    myContent = content.UNKNOWN;
                }
            }
      
            //get the Datastructure(s) of this representation
            //in case of problems, set to 'UNKNOWN'
            OntClass dsClass = TypeOntoQuestioner.getDatastructureForRep(rep);
            if (dsClass != null) {
                try {
                    myStruct = datastructure.valueOf(dsClass.getLocalName().toUpperCase());
                } catch (Exception e) {
                    myStruct = datastructure.UNKNOWN;
                }
            }
            
            RDFNode format = rep.getPropertyValue(TypeOntoQuestioner.hasFormatLabel);
            formatLabel = (format != null ? format.asLiteral().getLexicalForm() : "Unspecified");
            fullDescriptionLabel = TypeOntoQuestioner.createRepresentationLabel(rep);
            extendedDescriptionLabel = TypeOntoQuestioner.extendRepresentationlabel(rep, fullDescriptionLabel);
            
        } else {
            throw new OntoAccessException("Could not instatiate OntoRepresentation: Type key '" + typeKey + "' is not a representation key!");
        }
    }

    /**
     * Gets the type key name of this OntoRepresentation
     * @return - The key name of this OntoRepresentation
     */
    @Override
    public String getKey() {
        return myTypeKey;
    }

    /**
     * gets the Validator for this representation. Value is cached for this
     * instance of BiBiRepresentation
     * @return a Validator instance, ready to use; 
     * @throws ValidatorFactoryException 
     */
    @Override
    public Validator getValidator() throws ValidatorFactoryException {
        if (myValidator == null) {
            myValidator = ValidatorFactory.makeValidatorFor(myTypeKey);
        }
        return myValidator;
    }

    /**
     * 
     * @return a list of possible Visualizer classes for this representation
     * @throws ValidatorFactoryException 
     */
    @Override
    public List<Visualizer> getVisualizers() throws VisualizerFactoryException {
        if (myVisualizers == null) {
            myVisualizers = new ArrayList<Visualizer>();
            try {
                List<String> vl = TypeOntoQuestioner.getVisualizersForKey(myTypeKey);
                for (String vk : vl) {
                    Visualizer viz = VisualizerFactory.makeVisualizerFor(vk);
                    myVisualizers.add(viz);
                }
            } catch (OntoAccessException ex) {
                throw new VisualizerFactoryException(ex.getLocalizedMessage());
            }
        }
        return myVisualizers;
    }

    @Override
    public representationType getType() {
        return myType;
    }

    @Override
    public String getImplementationType() {
        if ((myType.equals(representationType.PRIMITIVE)) || (myType.equals(representationType.XML))) {
            return myImplementationType;
        } else {
            return null;
        }
    }

    @Override
    public URI getNameSpace() {
        if (myType.equals(representationType.XML)) {
            return myNameSpace;
        } else {
            return null;
        }
    }

    @Override
    public URI getSchemaLocation() {
        if (myType.equals(representationType.XML)) {
            return mySchemaLocation;
        } else {
            return null;
        }
    }

    @Override
    public strictness getStrictness() {
        return myStrictness;
    }

    @Override
    public cardinalitylevel getCardinality() {
        return myCardinality;
    }

    @Override
    public content getContent() {
        return myContent;
    }

    @Override
    public datastructure getStructure() {
        return myStruct;
    }

    @Override
    public List<OntoRepresentation> getSiblingRepresentations() throws OntoAccessException {
        //List<OntoRepresentation> siblist = new ArrayList<OntoRepresentation>();
        if (siblist == null) {
            siblist = new ArrayList<OntoRepresentation>();
            List<String> keylist = TypeOntoQuestioner.getSiblingKeysForKey(myTypeKey);
            if (keylist != null) {
                // work through the list, create new OntoRepresentations
                for (Iterator<String> it = keylist.iterator(); it.hasNext();) {
                    String keystring = it.next();
                    try {
                        siblist.add(new OntoRepresentationImplementation(keystring));
                    } catch (URISyntaxException ue) {
                        Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ue);
                    }
                }
            }
        }
        return siblist;
    }
    
    @Override
    public List<String> getEdamSynonyms() {
        if(edamSynonyms == null) {
            Individual rep = TypeOntoQuestioner.getRepresentationForKey(myTypeKey);
            edamSynonyms = TypeOntoQuestioner.listEdamSynomys(rep);
        }
        return edamSynonyms;
    }
    
    //-----------------------TG: Generated Format and Contentdescriptions-----------------------//
        
    @Override
    public String getFormatLabel(){
        return formatLabel;
    }
    
    @Override
    public String getContentDescription(){
        return fullDescriptionLabel;
    }
    
    @Override
    public String getExtendedDescriptionLabel(){
        return extendedDescriptionLabel;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.myType != null ? this.myType.hashCode() : 0);
        hash = 11 * hash + (this.myTypeKey != null ? this.myTypeKey.hashCode() : 0);
        hash = 11 * hash + (this.myValidator != null ? this.myValidator.hashCode() : 0);
        hash = 11 * hash + (this.myImplementationType != null ? this.myImplementationType.hashCode() : 0);
        hash = 11 * hash + (this.myNameSpace != null ? this.myNameSpace.hashCode() : 0);
        hash = 11 * hash + (this.mySchemaLocation != null ? this.mySchemaLocation.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OntoRepresentationImplementation other = (OntoRepresentationImplementation) obj;
        if (this.myType != other.myType) {
            return false;
        }
        if ((this.myTypeKey == null) ? (other.myTypeKey != null) : !this.myTypeKey.equals(other.myTypeKey)) {
            return false;
        }
        if (this.myValidator != other.myValidator && (this.myValidator == null || !this.myValidator.equals(other.myValidator))) {
            return false;
        }
        if ((this.myImplementationType == null) ? (other.myImplementationType != null) : !this.myImplementationType.equals(other.myImplementationType)) {
            return false;
        }
        if (this.myNameSpace != other.myNameSpace && (this.myNameSpace == null || !this.myNameSpace.equals(other.myNameSpace))) {
            return false;
        }
        if (this.mySchemaLocation != other.mySchemaLocation && (this.mySchemaLocation == null || !this.mySchemaLocation.equals(other.mySchemaLocation))) {
            return false;
        }
        return true;
    }

    /**
     * DO NOT USE UNLESS ABSOLUTELY NECESSARY.
     * Only for backward-compatibility!
     * @return
     * @deprecated
     */
    @Override
    @Deprecated
    public String getLabel() {
        return myTypeKey;
    }
}
