/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010,2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010,2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.dev"
 *
 * Contributor(s):
 *
 */
package de.unibi.techfak.bibiserv.util.ontoaccess;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiEdge;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiObjectProperty;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.BiBiObjectPropertyImplementation;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;
import java.io.InputStream;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 * The general questioner class for all kinds of data reads from type ontology
 *
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de Jan Krueger -
 * @author jkrueger(at)cebitec.uni-bielefeld.de
 * @author tgatter(at)cebitec.uni-bielefeld.de
 */
public class TypeOntoQuestioner {

    private static final Logger LOG = Logger.getLogger(TypeOntoQuestioner.class);

    //The Ontology and Data file to query against
    private static final String DATAFILEURL = "http://bibiserv.cebitec.uni-bielefeld.de/ontologies/BiBiTypes.owl";
    private static final String DATAFILERESOURCE = "/BiBiTypes.owl";
    // the nametype of the ontology
    public static final String bibitypesns = "http://bibiserv.cebitec.uni-bielefeld.de/ontologies/BiBiTypes.owl#";
    
    // create an empty ontology model 
    private static final OntModel model;
    
    //several properties and classes used in the ontology. Centralized for easier access
    //Dataproperty: representations
    public static final DatatypeProperty hasCardinality;
    public static final DatatypeProperty hasStrictness;
    public static final DatatypeProperty hasEdamSynonym;
    //Dataproperty: labels
    public static final DatatypeProperty hasContentLabel;
    public static final DatatypeProperty hasDatastructureLabel;
    public static final DatatypeProperty hasFormatLabel;
    public static final DatatypeProperty hasCardinalityLabel;
    public static final DatatypeProperty hasStrictnessLabel;
    //Dataproperty: other 
    public static final DatatypeProperty hasImplementingClass;
    public static final DatatypeProperty hasNamespace;
    public static final DatatypeProperty hasSchemalocation;
    //Objectproperty
    public static final ObjectProperty hasImplementationType;
    public static final ObjectProperty hasValidator;
    public static final ObjectProperty hasVisualizer;
    //create property objects for converter properties
    public static final ObjectProperty convertsFrom;
    public static final ObjectProperty convertsTo;
    //some class references
        // axis
    public static final OntClass format;
    public static final OntClass content;
    public static final OntClass datastructure;
        //converter
    public static final OntClass converter;
    public static final OntClass streamconverter;
        //validator
    public static final OntClass validator;
    public static final OntClass implementationType;
    public static final OntClass jaxbType;

    static { //initialization of model and dataset for all further questions
        InputStream instream = null;
        // Get data file location
        // Try to load from path given in property
        // If this fails, use URL to well-known online location
        if (System.getProperty("ontolocation") == null) {
            instream = FileManager.get().open(DATAFILEURL);
        } else {
            instream = FileManager.get().open(System.getProperty("ontolocation"));
        }
        if (instream == null) { // If this also fails, use local fallback-file from resources
            instream = FileManager.get().open(DATAFILERESOURCE);
            //instream = OntoRepresentationImplementation.class.getResourceAsStream(DATAFILERESOURCE);
        }
       
        // create an empty ontology model using Pellet spec
        model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        // read in the owl file from stream
        model.read(instream, "");
        // prepare and cache all needed
        model.prepare();
        ((PelletInfGraph) model.getGraph()).classify();
        ((PelletInfGraph) model.getGraph()).realize();

        
        //several properties and classes used in the ontology. Centralized for easier access
        hasCardinality = model.getDatatypeProperty(bibitypesns + "hasCardinality");
        hasStrictness = model.getDatatypeProperty(bibitypesns + "hasStrictness");
        hasImplementingClass = model.getDatatypeProperty(bibitypesns + "hasImplementingClass");
        hasNamespace = model.getDatatypeProperty(bibitypesns + "hasNamespace");
        hasSchemalocation = model.getDatatypeProperty(bibitypesns + "hasSchemalocation");
        hasContentLabel = model.getDatatypeProperty(bibitypesns + "hasContentLabel");
        hasDatastructureLabel = model.getDatatypeProperty(bibitypesns + "hasDatastructureLabel");
        hasFormatLabel = model.getDatatypeProperty(bibitypesns + "hasFormatLabel");
        hasCardinalityLabel = model.getDatatypeProperty(bibitypesns + "hasCardinalityLabel");
        hasStrictnessLabel = model.getDatatypeProperty(bibitypesns + "hasStrictnessLabel");
        hasEdamSynonym = model.getDatatypeProperty(bibitypesns+"hasEdamSynonym");
        hasImplementationType = model.getObjectProperty(bibitypesns + "hasImplementationType");
        hasValidator = model.getObjectProperty(bibitypesns + "hasValidator");
        hasVisualizer = model.getObjectProperty(bibitypesns + "hasVisualizer");
        convertsFrom = model.getObjectProperty(bibitypesns + "convertsFrom");
        convertsTo = model.getObjectProperty(bibitypesns + "convertsTo");
        format = model.getOntClass(bibitypesns + "Format");
        content = model.getOntClass(bibitypesns + "Content");
        datastructure = model.getOntClass(bibitypesns + "Datastructure");
        converter = model.getOntClass(bibitypesns + "Converter");
        streamconverter = model.getOntClass(bibitypesns + "StreamConverter");
        validator = model.getOntClass(bibitypesns + "Validator");
        implementationType = model.getOntClass(bibitypesns + "implementationType");
        jaxbType = model.getOntClass(bibitypesns + "JAXB-Type");
    }

    /**
     * Method to provide the Ontology Model
     *
     * @return The model for the type ontology
     */
    public static OntModel getModel() {
        return model;
    }

    /**
     * Method to check if a certain individual is a representation
     *
     * @param key Individual as String name
     * @return
     */
    public static boolean isRepresentation(final String key) {
        Individual ind = model.getIndividual(bibitypesns + key);
        return isRepresentation(ind);
        
    }
     /**
     * Method to check if a certain individual is a representation
     *
     * @param ind Individual as Jena RDF Individual
     * @return
     */
    public static boolean isRepresentation(Individual ind) {
        if (ind != null && ind.hasOntClass(content) && ind.hasOntClass(format) && ind.hasOntClass(datastructure)) {
            return true;
        }
        return false;
    }
    

    public static Map<String, String> getJAXBImplementingClasses() {
        HashMap<String, String> retMap = new HashMap<String, String>();
        for (Iterator<Individual> it = model.listIndividuals(jaxbType); it.hasNext();) {
            Individual ji = it.next();
            retMap.put(ji.getLocalName(), ji.getPropertyValue(hasImplementingClass).asLiteral().getLexicalForm());
        }
        return retMap;
    }

    public static Individual getRepresentationForKey(final String key) {
        return model.getIndividual(bibitypesns + key);
    }

    /*
     * This block of functions is used to get the different possible
     * superclasses of a representation along the the three axes of Format,
     * Datastructure and Content. One can ask for the direct superclass or the
     * upmost class in the class hierarchy of each axis.
     */
    public static OntClass getFormatForRepresentation(Individual rep) throws OntoAccessException {
        return getFormatForRepresentation(rep, false);
    }

    public static OntClass getFormatForRepresentation(Individual rep, boolean findRoot) throws OntoAccessException {
        return getSpecialClassForRep(format, rep, findRoot);
    }

    public static OntClass getDatastructureForRep(Individual rep) throws OntoAccessException {
        return getDatastructureForRep(rep, false);
    }

    public static OntClass getDatastructureForRep(Individual rep, boolean findRoot) throws OntoAccessException {
        return getSpecialClassForRep(datastructure, rep, findRoot);
    }

    public static OntClass getContentForRep(Individual rep) throws OntoAccessException {
        return getContentForRep(rep, false);
    }

    public static OntClass getContentForRep(Individual rep, boolean findRoot) throws OntoAccessException {
        return getSpecialClassForRep(content, rep, findRoot);
    }

    /**
     * this function does the actual class lookup the usual caching mechanism is
     * in place
     * 
     * workaround for an insufficiency in Jena included: 
     * Does not know OWL2 constructs as classes!
     * see: http://tech.groups.yahoo.com/group/jena-dev/message/47199
     * 
     * meaning: listOntClasses will loop over "NamedIndividual", but Jena cannot interpret this as OntClass throwing an Convertererror
     * setting setStrictMode to false is an easy fix and Jena will ignore this
     * 
     * @param rootclass the ontclass of the category
     * @param rep the individual to look for
     * @param findtop true: finds the class closest to category, farthest from individual
     *                false: finds the class closest to individual
     */
    private static OntClass getSpecialClassForRep(OntClass rootclass, Individual rep, boolean findTop) throws OntoAccessException {

        if (isRepresentation(rep)) {
            model.setStrictMode(false); //part 1 of hack
            // listOntClasses: True only list directly adjacent, should only be one for each category, just filter category
            //                 False list all, only one directly adjacent to category 
            for (ExtendedIterator<OntClass> it = rep.listOntClasses(!findTop); it.hasNext();) {
                OntClass cl = it.next();
                if (!cl.equals(rootclass) && cl.hasSuperClass(rootclass, findTop)) {
                    model.setStrictMode(true); //part 2 of hack, reset to strictmode again
                    return cl;
                }
            }
            model.setStrictMode(true); //part 2 of hack, reset to strictmode again
        } else {
            throw new OntoAccessException("Problem in 'getSpecialClassForRepresentation':  '" + rep.getLocalName() + "' is not a representation!");
        }
        throw new OntoAccessException("Problem in 'getSpecialClassForRepresentation':  No Class found for '" + rep.getLocalName() + "'!");
    }
    

    /**
     * Special calls for getOtherInfoFrom_F_C_or_DS. A representation can have more than one super-class.
     * 
     * These functions list all classes for one category.
     */
    public static List<OntClass> getFormatsForRepresentation(Individual rep) throws OntoAccessException {
        return getSpecialClassesForRep(format, rep);
    }

    public static List<OntClass> getDatastructuresForRep(Individual rep) throws OntoAccessException {
        return getSpecialClassesForRep(datastructure, rep);
    }

    public static List<OntClass> getContentsForRep(Individual rep) throws OntoAccessException {
        return getSpecialClassesForRep(content, rep);
    }
    
    
   
    
    /**
     * Does the actual listing.
     * 
     * workaround for an insufficiency in Jena included: 
     * Does not know OWL2 constructs as classes!
     * see: http://tech.groups.yahoo.com/group/jena-dev/message/47199
     * 
     * meaning: listOntClasses will loop over "NamedIndividual", but Jena cannot interpret this as OntClass throwing an Convertererror
     * 
     * @param rootclass the ontclass of the category
     * @param rep the individual to look for
     * @return List of all subclasses in category
     * @throws OntoAccessException 
     */
    private static List<OntClass> getSpecialClassesForRep(OntClass rootclass, Individual rep) throws OntoAccessException {

        List<OntClass> retval = new ArrayList<OntClass>();
        if (isRepresentation(rep)) {
            model.setStrictMode(false); //part 1 of hack
            for (ExtendedIterator<OntClass> it = rep.listOntClasses(false); it.hasNext();) {
                OntClass cl = it.next();
                if (!cl.equals(rootclass) && cl.hasSuperClass(rootclass, false)) {
                    retval.add(cl);
                }
            }
            model.setStrictMode(true); //part 2 of hack, reset to strictmode again
        } else {
            throw new OntoAccessException("Problem in 'getSpecialClassForRepresentation':  '" + rep.getLocalName() + "' is not a representation!");
        }
        return retval;
    }
    
    
    /**
     * Creates a Label for the given representation-individual
     * @param i the representation to generate label for
     */
    public static String createRepresentationLabel(Individual i) {
       
        // don't try and make a label if this is no representation
        if (!isRepresentation(i)) {
            return "";
        }
        
        RDFNode propertyValue = i.getPropertyValue(TypeOntoQuestioner.hasCardinalityLabel);
        String cardinalityLabel = (propertyValue != null ? propertyValue.asLiteral().getLexicalForm() : "");
        
        propertyValue = i.getPropertyValue(TypeOntoQuestioner.hasStrictnessLabel);
        String strictnessLabel = (propertyValue != null ? propertyValue.asLiteral().getLexicalForm() : "");
        
        propertyValue = i.getPropertyValue(TypeOntoQuestioner.hasDatastructureLabel);
        String structureLabel = (propertyValue != null ? propertyValue.asLiteral().getLexicalForm() : "");
        
        propertyValue = i.getPropertyValue(TypeOntoQuestioner.hasContentLabel);
        String contentLabel = (propertyValue != null ? propertyValue.asLiteral().getLexicalForm() : "");
        
        String fullDescriptionLabel = "";
        if (!cardinalityLabel.isEmpty()) {
            fullDescriptionLabel += cardinalityLabel + " ";
        }
        fullDescriptionLabel += structureLabel + " containing ";
        if (!strictnessLabel.isEmpty()) {
            fullDescriptionLabel += strictnessLabel + " ";
        }
        fullDescriptionLabel += contentLabel;

        return fullDescriptionLabel;
    }
    
    /**
     * Extends an existing Representationlabel with the format name
     * @param i Individula to extend for
     * @param representationLabel the label up until now
     * @return extended label
     */
    public static String extendRepresentationlabel(Individual i, String representationLabel) {
        
        // don't try and make a label if this is no representation
        if (!isRepresentation(i)) {
            return "";
        }
        
        RDFNode propertyValue = i.getPropertyValue(TypeOntoQuestioner.hasFormatLabel);
        String formatLabel = (propertyValue != null ? propertyValue.asLiteral().getLexicalForm() : "");
        
        return formatLabel+": "+representationLabel;
    }
    
     /**
     * Creates an extended Label for the given representation-individual (adds format to label)
     * @param i the representation to generate label for
     */
    public static String createExtendedRepresentationLabel(Individual i) {
        return extendRepresentationlabel(i, createRepresentationLabel(i));
    }
    
    /**
     * Returns a list of all edam-synonyms for a given representation
     * @param i the representation to get get synonyms for
     * @return list of edam-synonyms, each as string containing an URI
     */
    public static List<String> listEdamSynomys(Individual i) {
        
        List<String> synonyms = new ArrayList<String>();
        
        if (!isRepresentation(i)) {
            return synonyms;
        }
        
        Iterator synIt = i.listPropertyValues(TypeOntoQuestioner.hasEdamSynonym); 
        while (synIt.hasNext()) {
                Literal val = (Literal) synIt.next();
                synonyms.add(val.getLexicalForm());
        } 
        
        return synonyms;
    }
    
    /**
     * This method can be used to get information about the compatible ontology
     * classes along the three axes of Format, Content, and DataStructure. It
     * takes one to three parameters and returns a Map with four keys:
     * "formats", "contents", "datastructures" and "representations" Each of
     * these keys references a
     *
     * @see{Set} of Strings containing the local names of the respective
     * compatible elements. These sets can be used to display the compatible
     * elements.
     *
     * @param in_format - the local name of a subclass of Format in the ontology
     * @param in_content - the local name of a subclass of "Content" in the
     * ontology
     * @param in_datastructure - the local name of a subclass of "DataStructure"
     * in the ontology
     *
     * @return a map with four keys ("formats", "contents", "datastructures" and
     * "representations"), each referencing a Set of local names
     */
    public static Map<String, Collection> getOtherInfoFrom_F_C_or_DS(String in_format, String in_content, String in_datastructure) {

        Map<String, Collection> retmap = new HashMap<String, Collection>();

        OntClass inf, inds, inc = null;
        //initialize the return lists
        Collection<BiBiObjectProperty> flist = new HashSet<BiBiObjectProperty>();
        Collection<BiBiObjectProperty> clist = new HashSet<BiBiObjectProperty>();
        Collection<BiBiObjectProperty> dslist = new HashSet<BiBiObjectProperty>();
        Collection<BiBiObjectProperty> replist = new HashSet<BiBiObjectProperty>();
        //init some Individual Sets
        Set<Individual> fset = new HashSet<Individual>();
        Set<Individual> cset = new HashSet<Individual>();
        Set<Individual> dsset = new HashSet<Individual>();
        Set<Individual> repset = new HashSet<Individual>();

        //get Individuals which belong to the given format
        if (in_format != null) {
            inf = model.getOntClass(bibitypesns + in_format);
            if (inf != null) {
                fset = model.listIndividuals(inf).toSet();
            }
        }

        //get Individuals which are of the given Content class
        if (in_content != null) {
            inc = model.getOntClass(bibitypesns + in_content);
            if (inc != null) {
                cset = model.listIndividuals(inc).toSet();
            }
        }

        //get Individuals which are of the given DataStructure class
        if (in_datastructure != null) {
            inds = model.getOntClass(bibitypesns + in_datastructure);
            if (inds != null) {
                dsset = model.listIndividuals(inds).toSet();
            }
        }

        // now the many ifs to create the correct output result
        if (in_format == null && in_datastructure == null && in_content == null) {

            //if everything is empty, return ALL THE THINGS
            Iterator<OntClass> FI = format.listSubClasses();
            //formats
            while (FI.hasNext()) {
                OntClass ontClass = FI.next();
                if (!"Nothing".equals(ontClass.getLocalName())) {
                    flist.add( new BiBiObjectPropertyImplementation(ontClass.getLocalName(), getLabelDataProperty(TypeOntoQuestioner.hasFormatLabel, ontClass)));
                           
                }
            }

            Iterator<OntClass> CI = content.listSubClasses();
            //formats
            while (CI.hasNext()) {
                OntClass ontClass = CI.next();
                if (!"Nothing".equals(ontClass.getLocalName())) {
                    clist.add(new BiBiObjectPropertyImplementation(ontClass.getLocalName(), getLabelDataProperty(TypeOntoQuestioner.hasContentLabel, ontClass)));
                }
            }

            Iterator<OntClass> DSI = datastructure.listSubClasses();
            //formats
            while (DSI.hasNext()) {
                OntClass ontClass = DSI.next();
                if (!"Nothing".equals(ontClass.getLocalName())) {
                    dslist.add(new BiBiObjectPropertyImplementation(ontClass.getLocalName(), getLabelDataProperty(TypeOntoQuestioner.hasDatastructureLabel, ontClass)));
                }
            }

            //reps
            Set allReps = content.listInstances(false).toSet();
            allReps.retainAll(format.listInstances(false).toSet());
            allReps.retainAll(datastructure.listInstances(false).toSet());
            
             Iterator<Individual> repit = allReps.iterator();
             while(repit.hasNext()) {
                 Individual i = repit.next();
                 replist.add(new BiBiObjectPropertyImplementation( i.getLocalName(), createExtendedRepresentationLabel(i)));
             }

            // now we prepare the return map. Some parts of it may be empty
            retmap.put("formats", flist);
            retmap.put("contents", clist);
            retmap.put("datastructures", dslist);
            retmap.put("representations", replist);

            return retmap;

        } else if (in_format != null && in_datastructure != null && in_content != null) {
            // this is the full question, only return a value in the reps list
            repset.addAll(fset);
            repset.retainAll(dsset);
            repset.retainAll(cset);
        } else {
            if (in_format != null) {
                //we were asked with a format, so we add all those representations
                repset.addAll(fset);
                if (in_datastructure != null) { //we need content. 
                    //Create intersection of format and ds sets
                    repset.retainAll(dsset);
                    //ask all members for content class
                } else if (in_content != null) {//we need DS
                    //Create intersection of format and content sets
                    repset.retainAll(cset);
                }
                //prepare return map
            } else if (in_content != null) {
                repset.addAll(cset); //contents are minimum
                if (in_datastructure != null) { //if also asked for DS, 
                    repset.retainAll(dsset); //prepare intersection of C and DS
                }
            } else if (in_datastructure != null) {
                repset.addAll(dsset);
            }
        }


        if (!repset.isEmpty()) {
            Iterator<Individual> repit = repset.iterator();
            try {
                // iterate over all reps left, create lists of their respective
                // class names and put corresponding  into the return lists
                while (repit.hasNext()) {
                    Individual rep = repit.next();
                    //sanity check because of Format Proxies: Is this really a representation?
                    if (isRepresentation(rep)) {
                        List<OntClass> formats = getFormatsForRepresentation(rep);
                        for (OntClass ont : formats) {
                            flist.add(new BiBiObjectPropertyImplementation(ont.getLocalName(), getLabelDataProperty(TypeOntoQuestioner.hasFormatLabel, ont)));
                        }
                        List<OntClass> contents = getContentsForRep(rep);
                        for (OntClass ont : contents) {
                            clist.add(new BiBiObjectPropertyImplementation(ont.getLocalName(), getLabelDataProperty(TypeOntoQuestioner.hasContentLabel, ont)));
                        }
                        List<OntClass> dataStructures = getDatastructuresForRep(rep);
                        for (OntClass ont : dataStructures) {
                            dslist.add(new BiBiObjectPropertyImplementation(ont.getLocalName(), getLabelDataProperty(TypeOntoQuestioner.hasDatastructureLabel, ont)));
                        }

                        replist.add(new BiBiObjectPropertyImplementation( rep.getLocalName(), createExtendedRepresentationLabel(rep)));
                    }
                }
            } catch (OntoAccessException oex) {
                System.err.println("EXCEPTION IN getOtherInfoFrom_F_C_or_DS: " + oex.getLocalizedMessage());
            }
        }
        // now we prepare the return map. Some parts of it may be empty
        retmap.put("formats", flist);
        retmap.put("contents", clist);
        retmap.put("datastructures", dslist);
        retmap.put("representations", replist);

        return retmap;
    }

    public static List<String> getSiblingKeysForKey(final String key) {
        List<String> siblings = new ArrayList<String>();

        //check if we've got a result and if it is a representation object
        if (isRepresentation(key)) {
            //load representation individual from Ontology
            Individual ind = model.getIndividual(bibitypesns + key);

            //load Datastructure and Content classes of rep from ontology
            OntClass DS, C;
            try {
                DS = getDatastructureForRep(ind);
                C = getContentForRep(ind, false);
            } catch (OntoAccessException e) {
                //If anything goes wrong here, we return null
                return null;
            }

            //get Lists of Individuals with the same DS and C, then put
            //them in Sets, then create the intersection of these sets
            Iterator<Individual> DSit = model.listIndividuals(DS);
            Iterator<Individual> Cit = model.listIndividuals(C);

            Set<Individual> DSS = new HashSet<Individual>();
            while (DSit.hasNext()) {
                DSS.add(DSit.next());
            }

            //transfer Content individuals to Set
            Set<Individual> CS = new HashSet<Individual>();
            while (Cit.hasNext()) {
                CS.add(Cit.next());
            }

            //create Intersection of Content and Datastructure sets
            DSS.retainAll(CS);

            if (C.hasSubClass()) {
                //cleanup subclass individuals
                Iterator<OntClass> csc = C.listSubClasses();
                while (csc.hasNext()) {
                    OntClass ontClass = csc.next();
                    Iterator<Individual> scit = model.listIndividuals(ontClass);
                    while (scit.hasNext()) {
                        Individual ri = scit.next();
                        DSS.remove(ri);
                    }
                }
            }

            Iterator<Individual> it = DSS.iterator();
            while (it.hasNext()) {
                Individual possibleRep = it.next();

                Boolean identicalProperties = true;
                //check for identical properties.
                //get all subprops of representationProperties
                ExtendedIterator spi = model.getOntProperty(
                        bibitypesns + "representationProperties").listSubProperties(true);
                while (spi.hasNext()) {
                    OntProperty tp = (OntProperty) spi.next();
                    //check if both individuals either do or do not 
                    //have this property (NOT XOR)
                    if (!(possibleRep.hasProperty(tp) ^ ind.hasProperty(tp))) { // For ^, the result value is true if the operand values are different; otherwise, the result is false. 
                        //and compare both property values if they are not null
                        if ((possibleRep.getPropertyValue(tp) == null && ind.getPropertyValue(tp) != null)
                                || (possibleRep.getPropertyValue(tp) != null && ind.getPropertyValue(tp) == null)
                                || (possibleRep.getPropertyValue(tp) != null && ind.getPropertyValue(tp) != null && !possibleRep.getPropertyValue(tp).equals(ind.getPropertyValue(tp)))) {

                            identicalProperties = false;
                        }
                    } else {
                        identicalProperties = false;
                    }
                }
                if (identicalProperties) {
                    siblings.add(possibleRep.getLocalName());
                }
            }

        } else { //if we do not have any matching Individuals, return an empty
            System.err.println("Not a rep: " + key);
            return null;
        }

        List retlist = new ArrayList(siblings);
        retlist.remove(key);
        return retlist;
    }

    /**
     * Function to find the validator implementation class for a given
     * representation key
     *
     * @param key Representationtype which we want to have a valind class for
     * @return A string containing the correct valind's class name
     */
    public static String getValidatorClassnameForKey(String key) throws OntoAccessException {
        //try to load the individual representation from the Ontology
        Individual ind = model.getIndividual(bibitypesns + key);

        //we do many checks here and return correctly messaged Exceptions in case of errors

        //check if we've got a result and if it is a representation object
        if (isRepresentation(ind)) {
            Individual valind = model.getIndividual(ind.getPropertyValue(hasValidator).toString());
            if (valind != null) { //check if we've got a valind
                RDFNode classnamenode = valind.getPropertyValue(hasImplementingClass);
                if (classnamenode != null) { //check if we have a property for the classname
                    String classstring = classnamenode.asLiteral().getLexicalForm();
                    if (!classstring.isEmpty()) { //check if we have a non-empty value for our class string
                        return classstring;
                    } else {
                        throw new OntoAccessException("Validator classname for '" + key + "' is empty. Contact administrators!");
                    }
                } else {
                    throw new OntoAccessException("Validator for '" + key + "' has no implementing class. Contact administrators!");
                }
            } else {
                throw new OntoAccessException("Could not find Validator for '" + key + "' in ontology. Contact administrators!");
            }
        } else {
            throw new OntoAccessException("Key '" + key + "' is not a representation key. No Validator searchable.");
        }
    }

    /**
     * Function to find the converter implementation class for a conversion
     * between two given representation keys
     *
     * @param fromKey Representationtype from which we want to convert
     * @param toKey Representationtype into which we want to convert
     * @return A string containing the correct converter's class name
     */
    public static String getConverterClassnameFor(final String fromKey, final String toKey) throws OntoAccessException {
        //create individuals for in/output
        Individual inType = model.getIndividual(bibitypesns + fromKey);
        Individual outType = model.getIndividual(bibitypesns + toKey);
        if (inType != null && outType != null) {
            //find the formats of the input and output types
            OntClass inFormat = getFormatForRepresentation(inType, true);
            OntClass outFormat = getFormatForRepresentation(outType, true);
            Iterator conviIterator = converter.listInstances();
            if (conviIterator.hasNext()) {
                while (conviIterator.hasNext()) {
                    Individual convi = (Individual) conviIterator.next();
                    Individual cf = convi.getPropertyValue(convertsFrom).as(Individual.class);
                    Individual ct = convi.getPropertyValue(convertsTo).as(Individual.class);
                    if (cf.getOntClass(true).equals(inFormat) && ct.getOntClass(true).equals(outFormat)) {
                        return convi.getPropertyValue(hasImplementingClass).asLiteral().getLexicalForm();
                    }
                }
            }
            //If there is no hit, we end up here and throw an Exception to warn the user
            throw new OntoAccessException("Could not find converter from '" + fromKey + "' to '" + toKey + "'.");
        } else {
            throw new OntoAccessException("Could not find representation for keys!");
        }
    }
    
    
     /**
     * Function to find the stream converter implementation class for a conversion
     * between two given representation keys
     *
     * @param fromKey Representationtype from which we want to convert
     * @param toKey Representationtype into which we want to convert
     * @return A string containing the correct converter's class name
     */
    public static String getStreamConverterClassnameFor(final String fromKey, final String toKey) throws OntoAccessException {
        //create individuals for in/output
        Individual inType = model.getIndividual(bibitypesns + fromKey);
        Individual outType = model.getIndividual(bibitypesns + toKey);
        if (inType != null && outType != null) {
            //find the formats of the input and output types
            OntClass inFormat = getFormatForRepresentation(inType, true);
            OntClass outFormat = getFormatForRepresentation(outType, true);
            Iterator conviIterator = streamconverter.listInstances();
            if (conviIterator.hasNext()) {
                while (conviIterator.hasNext()) {
                    Individual convi = (Individual) conviIterator.next();
                    Individual cf = convi.getPropertyValue(convertsFrom).as(Individual.class);
                    Individual ct = convi.getPropertyValue(convertsTo).as(Individual.class);
                    if (cf.getOntClass(true).equals(inFormat) && ct.getOntClass(true).equals(outFormat)) {
                        return convi.getPropertyValue(hasImplementingClass).asLiteral().getLexicalForm();
                    }
                }
            }
            //If there is no hit, we end up here and throw an Exception to warn the user
            throw new OntoAccessException("Could not find converter from '" + fromKey + "' to '" + toKey + "'.");
        } else {
            throw new OntoAccessException("Could not find representation for keys!");
        }
    }

    //
    /**
     * method for finding a (possibly only one-link long) chain of converters
     * between two representations
     *
     * @param fromKey
     * @param toKey
     * @return a List of pairs of representations for which converters do exist,
     * in the correct order for calling them
     * @throws OntoAccessException
     */
    public static List<BiBiEdge> getConverterChainFromKeyToKey(final String fromKey, final String toKey) throws OntoAccessException {

        // a map to store the formats of all members of the representation family
        Map<OntClass, String> f2r = new HashMap<OntClass, String>();

        //create a default directed graph
        DirectedGraph<String, BiBiEdge> convGraph = new DefaultDirectedGraph<String, BiBiEdge>(BiBiEdge.class);

        //Siblings holen
        List<String> family = getSiblingKeysForKey(fromKey);

        //in-type selber noch dazu holen
        family.add(fromKey);

        //Walk through family.
        //a) Store each rep key as a Node in the Graph
        //b) Store each key with its format in the lookup map

        for (String sibling : family) {
            convGraph.addVertex(sibling);
            Individual ind = model.getIndividual(bibitypesns + sibling);
            f2r.put(getFormatForRepresentation(ind, true), sibling);
        }
        //Alle Converter anschauen
        //Input und Output-Formate mit der Hashmap vergleichen
        //Wenn beide drin sind, dann eine Kante zwischen den beiden beteiligten
        //representations einfÃ¼gen
        ExtendedIterator ci = converter.listInstances();

        if (ci.hasNext()) {
            while (ci.hasNext()) {
                Individual conv = (Individual) ci.next();

                //Die From und To-Felder des Converters auslesen
                Individual targetTo = (Individual) conv.getPropertyResourceValue(convertsTo).as(Individual.class);
                OntClass toClass = targetTo.getOntClass(true);
                Individual targetFrom = (Individual) conv.getPropertyResourceValue(convertsFrom).as(Individual.class);
                OntClass fromClass = targetFrom.getOntClass(true);

                if (f2r.containsKey(fromClass) && f2r.containsKey(toClass)) {
                    convGraph.addEdge(f2r.get(fromClass), f2r.get(toClass));
                }
            }
        }

        // compute the shortest path
        List path = DijkstraShortestPath.findPathBetween(convGraph, fromKey, toKey);

        if (path != null) { //if we have a result,  return it
            return path;
        } else {
            throw new OntoAccessException("Could not find converter from '" + fromKey + "' to '" + toKey + "'.");
        }

    }
    
    /**
     * method for finding a (possibly only one-link long) chain of stream converters
     * between two representations
     *
     * @param fromKey
     * @param toKey
     * @return a List of pairs of representations for which converters do exist,
     * in the correct order for calling them
     * @throws OntoAccessException
     */
    public static List<BiBiEdge> getStreamConverterChainFromKeyToKey(final String fromKey, final String toKey) throws OntoAccessException {

        // a map to store the formats of all members of the representation family
        Map<OntClass, String> f2r = new HashMap<OntClass, String>();

        //create a default directed graph
        DirectedGraph<String, BiBiEdge> convGraph = new DefaultDirectedGraph<String, BiBiEdge>(BiBiEdge.class);

        //Siblings holen
        List<String> family = getSiblingKeysForKey(fromKey);

        //in-type selber noch dazu holen
        family.add(fromKey);

        //Walk through family.
        //a) Store each rep key as a Node in the Graph
        //b) Store each key with its format in the lookup map

        for (String sibling : family) {
            convGraph.addVertex(sibling);
            Individual ind = model.getIndividual(bibitypesns + sibling);
            f2r.put(getFormatForRepresentation(ind, true), sibling);
            //LOG.debug("added "+ind.getLocalName()+" ("+System.currentTimeMillis()+")");
        }
        //Alle Converter anschauen
        //Input und Output-Formate mit der Hashmap vergleichen
        //Wenn beide drin sind, dann eine Kante zwischen den beiden beteiligten
        //representations einfÃ¼gen
        ExtendedIterator ci = streamconverter.listInstances();

        if (ci.hasNext()) {
            while (ci.hasNext()) {
                Individual conv = (Individual) ci.next();

                //Die From und To-Felder des Converters auslesen
                Individual targetTo = (Individual) conv.getPropertyResourceValue(convertsTo).as(Individual.class);
                OntClass toClass = targetTo.getOntClass(true);
                Individual targetFrom = (Individual) conv.getPropertyResourceValue(convertsFrom).as(Individual.class);
                OntClass fromClass = targetFrom.getOntClass(true);

                if (f2r.containsKey(fromClass) && f2r.containsKey(toClass)) {
                    convGraph.addEdge(f2r.get(fromClass), f2r.get(toClass));
                }
            }
        }

        // compute the shortest path
        List path = DijkstraShortestPath.findPathBetween(convGraph, fromKey, toKey);

        if (path != null) { //if we have a result, store it in the cache and return it
            return path;
        } else {
            throw new OntoAccessException("Could not find converter from '" + fromKey + "' to '" + toKey + "'.");
        }
    }

    /**
     * Function to return a visualizer implementation class for a given
     * visualizer id
     *
     * @param vizkey which we want to have the visualizer class name
     * @return A string containing the correct visualizer class name
     */
    public static String getVisualizerClassnameForKey(String vizkey) throws OntoAccessException {
        //try to load the individual representation from the Ontology
        Individual vis = model.getIndividual(bibitypesns + vizkey);

        //check if we've got a result and if it is a representation object
        if (vis != null && vis.hasOntClass(bibitypesns + "Visualizer")) {
            RDFNode classnamenode = vis.getPropertyValue(hasImplementingClass);
            if (classnamenode != null) { //check if we have a property for the classname
                String classstring = classnamenode.asLiteral().getLexicalForm();
                if (!classstring.isEmpty()) { //check if we have a non-empty value for our class string
                    return classstring;
                } else {
                    throw new OntoAccessException("Visualizer classname for '" + vizkey + "' is empty. Contact administrators!");
                }
            } else {
                throw new OntoAccessException("Visualizer for '" + vizkey + "' has no implementing class. Contact administrators!");
            }
        } else {
            throw new OntoAccessException("Could not find Visualizer for '" + vizkey + "' in ontology. Contact administrators!");
        }
    }

    /**
     * Function to find the visualizers for a given representation key
     *
     * @param key Representationtype which we want to have all visualizers for
     * @return A List of Visualizer individual names for further questioning
     */
    public static List<String> getVisualizersForKey(String key) throws OntoAccessException {

        Individual ind = model.getIndividual(bibitypesns + key);
        
        //prepare a List for return values
        List<String> retval = new ArrayList<String>();
        //check if we've got a result and if it is a representation object
        if (isRepresentation(ind)) {

            //load it's hasVisualizer properties
            Iterator visus = ind.listPropertyValues(hasVisualizer);

            if (visus != null) {
                while (visus.hasNext()) {
                    //get the object of the statement
                    Resource vis = (Resource) visus.next();
                    if (vis != null) { //check for null
                        //and get the correct name
                        String viskey = vis.getLocalName();
                        retval.add(viskey);
                    } else {
                        throw new OntoAccessException("Found Visualizer for '" + key + "' in ontology, but it was 'NULL'. Contact administrators!");
                    }
                }
                return retval;
            } else {
                throw new OntoAccessException("Could not find any Visualizer for '" + key + "' in ontology. Contact administrators!");
            }
        } else {
            throw new OntoAccessException("Key '" + key + "' is not a representation key. No Visualizer searchable.");
        }
    }

    
    
    /**
     * Little helper class to get a Label-Dataproperty from an ontclass.
     * @param prop the property to extract
     * @param ont the Ontclass to get it from
     * @return 
     */
    private static String getLabelDataProperty(DatatypeProperty prop, OntClass ont) {
        //label properties can only be retried using an individual
        // probably not the best way but only needed for wizard
        Iterator listInstances = ont.listInstances(true);
        
        String value = ont.getLocalName();
        if(listInstances.hasNext()) {
            Individual ind = (Individual) listInstances.next();
            RDFNode propertyValue = ind.getPropertyValue(prop);
            if(propertyValue != null) {
                value = propertyValue.asLiteral().getLexicalForm();
            }
        }
        return value;
    }
    
    
    
    /********************************************************
     * Functions for Type/Format choosing from GUI          *
     ********************************************************/
    
    /**
     * Function to list all available data formats stored in the ontology
     *
     * @return a tree of key/label pairs of the formats in the ontology
     */
    public static TreeNode getAllFormats() {
        OntClass typebase = model.getOntClass(bibitypesns + "Format");
        return createSubtree(typebase, hasFormatLabel);
    }

    /**
     * function to list all available bioinformatical DataStructures stored in
     * the ontology
     *
     * @return a tree of key/label pairs of the formats in the ontology
     */
    public static TreeNode getAllDatastructures() {
        OntClass typebase = model.getOntClass(bibitypesns + "Datastructure");
        return createSubtree(typebase, hasDatastructureLabel);
    }

    /**
     * function to list all available content types stored in the ontology
     *
     * @return a tree with key/label pairs for the content types
     */
    public static TreeNode getAllContents() {
        OntClass typebase = model.getOntClass(bibitypesns + "Content");
        return createSubtree(typebase, hasContentLabel);
    }

    /**
     * function to list all available implementation types stored in the
     * ontology
     *
     * @return a list with the local names of the imp-types
     */
    public static List<String> getAllImplementationTypes() {
        return getIndividualsOfClass(implementationType);
    }

    /**
     * function to list all available Validators stored in the ontology
     *
     * @return a list with the local names of the Validators
     */
    public static List<String> getAllValidators() {
        return getIndividualsOfClass(validator);
    }

    public static List<String> getAllRepresentations() {
        Set allReps = content.listInstances(false).toSet();
        allReps.retainAll(format.listInstances(false).toSet());
        allReps.retainAll(datastructure.listInstances(false).toSet());
        
        ArrayList<String> retval = new ArrayList<String>();
        if (!allReps.isEmpty()) {
            Iterator<Individual> it = allReps.iterator();
            while (it.hasNext()) {
                Individual ind = it.next();
                retval.add(ind.getLocalName());
            }
        }
        return retval;
    }

    private static List<String> getIndividualsOfClass(OntClass baseclass) {
        ArrayList<String> retval = new ArrayList<String>();
        List instList = baseclass.listInstances(false).toList();
        if (!instList.isEmpty()) {
            Iterator<Individual> it = instList.iterator();
            while (it.hasNext()) {
                Individual ind = it.next();
                retval.add(ind.getLocalName());
            }
        }
        return retval;
    }

    /**
     * this method creates a tree structure of a subclass hierarchy of a given
     * base class, putting the value of a given property as value into the user
     * object of each tree node the value of an
     *
     * @param baseclass - The OntClass of the tree root
     * @param thisprop - the property that shall determine the node's user
     * objects
     * @return a MutableTreeNode containing the subtree of the class hierarchy
     * starting at the given base class
     */
    private static MutableTreeNode createSubtree(OntClass baseclass, Property thisprop) {
        HashMap<String, String> map;
        DefaultMutableTreeNode localRoot = new DefaultMutableTreeNode();

        if (!baseclass.listInstances(true).toList().isEmpty()) {
            map = new HashMap<String, String>();
            RDFNode r = baseclass.listInstances(true).toList().get(0).getPropertyValue(thisprop);
            if (r != null) {
                map.put(baseclass.getLocalName(), r.asLiteral().toString());
            } else {
                //else use the key itself
                map.put(baseclass.getLocalName(), baseclass.getLocalName());
            }
            localRoot.setUserObject(map);
        }
        if (baseclass.hasSubClass()) {
            for (Iterator<OntClass> i = baseclass.listSubClasses(true); i.hasNext();) {
                OntClass nc = i.next();
                if (!"Nothing".equals(nc.getLocalName()) && !"representation".equals(nc.getLocalName())) {
                    localRoot.add(createSubtree(nc, thisprop));
                }
            }
        }
        return localRoot;
    }
}
