/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010,2011 BiBiServ Curator Team,
 * http://bibiserv.cebitec.uni-bielefeld.de, All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License at
 * http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. When distributing the software, include this
 * License Header Notice in each file. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 *
 * "Portions Copyrighted 2010,2011 BiBiServ Curator Team,
 * http://bibiserv.cebitec.uni-bielefeld.dev"
 *
 * Contributor(s):
 *
 */
package de.unibi.techfak.bibiserv.util.ontoaccess;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiFunction;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiTool;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.BiBiToolImplementation;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 * @ToDo: Javadoc is missing
 *
 *
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 */
public class ToolOntoQuestioner {

    private static final Logger LOG = Logger.getLogger(ToolOntoQuestioner.class);
    //The Ontology and Data file to query against
    private static final String DATAFILEURL = "http://bibiserv.cebitec.uni-bielefeld.de/ontologies/BiBiTools.owl";
    //private static final String DATAFILEURL = "file:///Users/sven/Desktop/BiBiServ/01_BiBiServ_Development/Semantics/ontologies/bibiserv_ontology/BiBiTools.owl";
    private static final String DATAFILERESOURCE = "/BiBiTools.owl";
    // create an empty ontology model 
    private static final OntModel model;
    // This set contains all registered tool objects in the system, indexed by
    // their URI. If an instance is neede, it should be taken from here, and
    // not by calling the constructor directly.
    private static Map<String, BiBiTool> toolCache = new HashMap<String, BiBiTool>();
    private static Map<String, Collection<String>> categoryCache;
    private static Map<String, Individual> toolIndCache = new HashMap<String, Individual>();
    private static Map<String, Individual> functionCache = new HashMap<String, Individual>();
    private static Map<String, Collection<BiBiTool>> irepCache = new HashMap<String, Collection<BiBiTool>>();
    private static Map<String, Collection<BiBiTool>> orepCache = new HashMap<String, Collection<BiBiTool>>();
    public static final String bibitoolsns = "http://bibiserv.cebitec.uni-bielefeld.de/ontologies/BiBiTools.owl#";
    //some properties
    //public static final Property hasexec;
    public static final Property hasfunction;
    public static final Property hasid;
    public static final Property hasAE;
    public static final Property hasdesc;
    public static final Property hasshortdesc;
    public static final Property haslabel;
    public static final Property belongstocategory;
    public static final Property consumesrep;
    public static final Property producesrep;
    //functions and io properties
    public static final Property hasInput;
    public static final Property hasOutput;
    public static final Property hasType;
    public static final Property hasTool;
    //some class references
    public static final OntClass bibitool;

    static { //initialization of model and dataset for all further questions, regardless if SPARQL or JENA
        Long start = System.currentTimeMillis();
        LOG.debug("STATIC INIT BLOCK OF TOOLONTOACCESS (" + (System.currentTimeMillis() - start) + " ms)");
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
            instream = ToolOntoQuestioner.class.getResourceAsStream(DATAFILERESOURCE);
        }
        LOG.debug("Stream initialized (" + (System.currentTimeMillis() - start) + " ms)");
        // create an empty ontology model using Pellet spec
        model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        LOG.debug("Model/Reasoner creation done (" + (System.currentTimeMillis() - start) + " ms)");
        model.read(instream, "");
        LOG.debug("Data read done (" + (System.currentTimeMillis() - start) + " ms)");


        //initialize static vars etc.
        bibitool = model.getOntClass(bibitoolsns + "BiBiTool");
        //hasexec = model.getProperty(bibitoolsns + "hasExecutable");
        hasfunction = model.getProperty(bibitoolsns + "hasFunction");
        belongstocategory = model.getProperty(bibitoolsns + "belongsToCategory");

        hasid = model.getProperty(bibitoolsns + "hasToolID");
        hasAE = model.getProperty(bibitoolsns + "hasAcronymExpansion");
        hasdesc = model.getProperty(bibitoolsns + "hasToolDescription");
        haslabel = model.getProperty(bibitoolsns + "hasToolLabel");
        hasshortdesc = model.getProperty(bibitoolsns + "hasToolShortdescription");
        producesrep = model.getProperty(bibitoolsns + "producesRep");
        consumesrep = model.getProperty(bibitoolsns + "consumesRep");

        hasInput = model.getProperty(bibitoolsns + "hasInput");
        hasOutput = model.getProperty(bibitoolsns + "hasOutput");
        hasType = model.getProperty(bibitoolsns + "hasType");
        hasTool = model.getProperty(bibitoolsns + "belongsToTool");
    }

    /**
     * Method to provide the Ontology Model
     * @return The model for the type ontology 
     */
    public static OntModel getModel() {
        return model;
    }

    /**
     * Clear all caches. Should be made into an atomic action...
     */
    public static void clearCaches() {
        toolIndCache.clear();
        functionCache.clear();
        irepCache.clear();
        orepCache.clear();
    }

    /**
     * simplified version of listTools, without the reload flag
     * @return 
     */
    public static Collection<BiBiTool> listTools() {
        return listTools(false);
    }

    public static BiBiTool getTool(String ID) throws OntoAccessException {
        if (toolCache.containsKey(ID)) {
            return toolCache.get(ID);
        } else {
            Individual t;
            try {
                t = getToolIndForURI(ID);
                BiBiTool bt = new BiBiToolImplementation(t.getURI());
                toolCache.put(t.getURI(), bt);
                return bt;
            } catch (OntoAccessException ex) {
                throw new OntoAccessException("Could not getTool with ID '" + ID + "'. Error was: " + ex.getLocalizedMessage());
            }
        }
    }

    /**
     * Function to return a list of all known tools within the ontology.
     * 
     * @param reload - setting this to 'true' causes a reload of the tool list
     * from the ontology. Useful after adding or deleting tools from the ontology
     * @return 
     */
    public static Collection<BiBiTool> listTools(Boolean reload) {
        //initialize the toolcache
        if ((toolCache == null) || reload) {
            toolCache = new HashMap<String, BiBiTool>();
            ExtendedIterator<Individual> ti = model.listIndividuals(bibitool);
            while (ti.hasNext()) {
                Individual individual = ti.next();
                String toolURI = individual.getURI();
                toolCache.put(toolURI, new BiBiToolImplementation(toolURI));
            }
        }
        Set toolset = new HashSet<BiBiTool>();
        Iterator<String> ti = toolCache.keySet().iterator();
        while (ti.hasNext()) {
            String key = ti.next();
            toolset.add(toolCache.get(key));
        }
        return toolset;
    }

    //The following functions provide class-checked and cached access 
    //to Individuals of certain classes in the ontology,
    public static Individual getToolIndForURI(String key) throws OntoAccessException {
        return getToolIndForURI(key, false);
    }

    public static Individual getToolIndForURI(String key, boolean refresh) throws OntoAccessException {
        //check for refresh request and cached value
        if (!refresh && toolIndCache.containsKey(key)) {
            return toolIndCache.get(key);
        } else { // otherwise get the data from the 
            Individual t = model.getIndividual(key);

            if (t != null && t.hasOntClass(bibitoolsns + "BiBiTool")) {
                toolIndCache.put(key, t);
                return t;
            } else {
                throw new OntoAccessException("'" + key + "' is not a Tool's key!");
            }
        }
    }

    public static Individual getFunctionForURI(String key) throws OntoAccessException {
        return getFunctionForURI(key, false);
    }

    public static Individual getFunctionForURI(String key, boolean refresh) throws OntoAccessException {
        if (!refresh && functionCache.containsKey(key)) {
            return functionCache.get(key);
        } else {
            Individual f = model.getIndividual(key);
            if (f != null && f.hasOntClass(bibitoolsns + "Function")) {
                functionCache.put(key, f);
                return f;
            } else {
                throw new OntoAccessException("'" + key + "' is not a function key!");
            }
        }
    }

    /**
     * Method to get a List of all Tools in a certain Category
     * @param cat
     * @return 
     */
    public static Collection<BiBiTool> listToolsForCategory(String cat, Boolean refresh) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    /**
     * Get a list of all tools which take a certain representation as Input
     * @param rep
     * @return 
     */
    public static Collection<BiBiTool> listToolsWithInputRep(OntoRepresentation rep) {
        String key = rep.getKey();
        Set o = new HashSet<BiBiTool>();
        if (irepCache.containsKey(key)) {
            return irepCache.get(key);
        } else {
            Individual r2 = TypeOntoQuestioner.getRepresentationForKey(key);
            Individual ontrep = model.getIndividual(r2.getURI());
            ResIterator i = model.listResourcesWithProperty(consumesrep, ontrep);

            while (i.hasNext()) {
                Resource resource = i.next();
                try {
                    o.add(getTool(resource.getURI()));
                } catch (OntoAccessException ex) {
                    java.util.logging.Logger.getLogger(ToolOntoQuestioner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            irepCache.put(key, o);
            return o;
        }
    }

    /**
     * Get a list of all tools which produce a certain representation as Output
     * @param rep
     * @return 
     */
    public static Collection<BiBiTool> listToolsWithOutputRep(OntoRepresentation rep) {
        String key = rep.getKey();
        Set o = new HashSet<BiBiTool>();

        if (orepCache.containsKey(key)) {
            return orepCache.get(key);
        } else {
            Individual r2 = TypeOntoQuestioner.getRepresentationForKey(key);
            Individual ontrep = model.getIndividual(r2.getURI());
            ResIterator i = model.listResourcesWithProperty(producesrep, ontrep);

            while (i.hasNext()) {
                Resource resource = i.next();
                try {
                    o.add(getTool(resource.getURI()));
                } catch (OntoAccessException ex) {
                    java.util.logging.Logger.getLogger(ToolOntoQuestioner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            orepCache.put(key, o);
            return o;
        }
    }

    //Workflow part finders 
    /**
     * Produces a List of Tools which can produce a tool's input 
     * @return A Collection of other BibiTools
     */
    public static Collection<BiBiTool> getPossibleWorkFlowPredecessors(BiBiTool tool) {
        Iterator<BiBiFunction> fit = tool.getFunctions().iterator();
        Set retval = new HashSet<BiBiTool>();
        while (fit.hasNext()) {
            try {
                BiBiFunction f = fit.next();
                //System.out.println("Function: " + f.getFunctionID());
                //get input reps
                OntoRepresentation ir = f.getInputRep();
                Set irs = new HashSet(ir.getSiblingRepresentations());
                //re-add original Reps
                irs.add(ir);
                //ask for tools with outputs from irs

                Iterator<OntoRepresentation> orsi = irs.iterator();
                while (orsi.hasNext()) {
                    OntoRepresentation oor = orsi.next();
                    retval.addAll(ToolOntoQuestioner.listToolsWithOutputRep(oor));
                }
            } catch (OntoAccessException ex) {
                java.util.logging.Logger.getLogger(ToolOntoQuestioner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retval;
    }

    /**
     * Produces a List of Tools which can consume a tool's output 
     * @return A Collection of other BibiTools
     */
    public static Collection<BiBiTool> getPossibleWorkFlowSuccessors(BiBiTool tool) {
        Iterator<BiBiFunction> fit = tool.getFunctions().iterator();
        Set retval = new HashSet<BiBiTool>();
        while (fit.hasNext()) {
            try {
                BiBiFunction f = fit.next();
                //System.out.println("Function: " + f.getFunctionID());
                //get input reps
                OntoRepresentation or = f.getOutputRep();
                Set ors = new HashSet(or.getSiblingRepresentations());
                //re-add original Reps
                ors.add(or);
                //ask for tools with outputs from irs

                Iterator<OntoRepresentation> irsi = ors.iterator();
                while (irsi.hasNext()) {
                    OntoRepresentation ior = irsi.next();
                    retval.addAll(ToolOntoQuestioner.listToolsWithInputRep(ior));
                }
            } catch (OntoAccessException ex) {
                java.util.logging.Logger.getLogger(ToolOntoQuestioner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retval;
    }

    /**
     * This Method searches the ontology for equivalent Tools, i.e, Tools which
     * have the same input and output in one of their functions
     * TODO: This needs to be rewritten to return specific Tool/Function pairs.
     * @param tool
     * @return 
     */
    public static Collection<BiBiTool> getEquivalentTools(BiBiTool tool) {
        Iterator<BiBiFunction> fit = tool.getFunctions().iterator();
        Set<BiBiTool> retval = new HashSet<BiBiTool>();
        
        while (fit.hasNext()) {
            try {
                BiBiFunction f = fit.next();
                //System.out.println("Function: " + f.getFunctionID());
                //get input and output reps
                OntoRepresentation ir = f.getInputRep();
                OntoRepresentation or = f.getOutputRep();
                //System.out.println("ir: " + ir.getKey());
                //System.out.println("or: " + or.getKey());

                //get siblings of i/o-reps
                Set irs = new HashSet(ir.getSiblingRepresentations());
                Set ors = new HashSet(or.getSiblingRepresentations());

                //re-add original Reps
                irs.add(ir);
                ors.add(or);

                //ask for tools with inputs from irs
                Set its = new HashSet<BiBiTool>();
                Iterator<OntoRepresentation> irsi = irs.iterator();
                while (irsi.hasNext()) {
                    OntoRepresentation ior = irsi.next();
                    //System.out.println("IOR: " + ior.getKey());
                    its.addAll(ToolOntoQuestioner.listToolsWithInputRep(ior));
                }

                //ask for tools with outputs from ors
                Set ots = new HashSet<BiBiTool>();
                Iterator<OntoRepresentation> orsi = ors.iterator();
                while (orsi.hasNext()) {
                    OntoRepresentation oor = orsi.next();
                    //System.out.println("OOR: " + oor.getKey());
                    ots.addAll(ToolOntoQuestioner.listToolsWithOutputRep(oor));
                }

                //find identical tools by merging the sets
                its.retainAll(ots);
                
                //put the result into the return set
                retval.addAll(its);
                
                //cleanup
                irs.clear();
                ors.clear();
                its.clear();
                ots.clear();
            } catch (OntoAccessException ex) {
                java.util.logging.Logger.getLogger(ToolOntoQuestioner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retval;
    }
}
