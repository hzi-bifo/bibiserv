/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010 - 2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.dev"
 *
 * Contributor(s): Sven Hartmeier, Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.util.ontoaccess;

import de.unibi.cebitec.bibiserv.util.bibtexparser.BibtexParser;
import de.unibi.cebitec.bibiserv.util.bibtexparser.ParseException;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPerson;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPublication;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * @ToDo: Javadoc is missing
 * 
 * Currently this class is only a data container for tool references and tool authors. Nothing
 * can be "questioned", nor this class is used something else beside the bibimainapp. If this
 * won't be change in near future, I think we can move this "data container" to the place
 * were it is used (-> bibimainapp)
 * 
 *
 *
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de Jan Krueger -
 * jkrueger(at)cebitec.uni-bielefeld.de
 */
public class PnPOntoQuestioner {

    private static final Logger LOG = Logger.getLogger(PnPOntoQuestioner.class);
    //The Ontology and Data file to query against
    private static final String DATAFILEURL = "http://bibiserv.cebitec.uni-bielefeld.de/ontologies/BiBiTypes.owl";
    private static final String DATAFILERESOURCE = "/BiBiTypes.owl";
    // create an empty ontology model 
    //private static final OntModel model;
    //Map for storing references for tools
    private static final Map<String, List<BiBiPublication>> refmap = new HashMap();
    //Map for storing authors for tools
    private static final Map<String, List<BiBiPerson>> authormap = new HashMap();
//    private static Map convChainCache = new HashMap<String, List<BiBiEdge>>();
//    private static Map siblingCache = new HashMap<String, List<String>>();
    public static final String bibipnpsns = "http://bibiserv.cebitec.uni-bielefeld.de/ontologies/BiBiPublications.owl#";

    static { //initialization of model and dataset for all further questions, regardless if SPARQL or JENA
        Long start = System.currentTimeMillis();
        LOG.debug("STATIC INIT BLOCK OF PNPONTOACCESS (" + (System.currentTimeMillis() - start) + " ms)");
//        InputStream instream = null;
//        // Get data file location
//        // Try to load from path given in property
//        // If this fails, use URL to well-known online location
//        if (System.getProperty("ontolocation") == null) {
//            instream = FileManager.get().open(DATAFILEURL);
//        } else {
//            instream = FileManager.get().open(System.getProperty("ontolocation"));
//        }
//        if (instream == null) { // If this also fails, use local fallback-file from resources
//            instream = PnPOntoQuestioner.class.getResourceAsStream(DATAFILERESOURCE);
//        }
//        LOG.debug("Stream initialized (" + (System.currentTimeMillis() - start) + " ms)");
//        // create an empty ontology model using Pellet spec
//        model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
//        LOG.debug("Model/Reasoner creation done (" + (System.currentTimeMillis() - start) + " ms)");
//        model.read(instream, "");
//        LOG.debug("Data read done (" + (System.currentTimeMillis() - start) + " ms)");

    }

//    /**
//     * Method to provide the Ontology Model
//     * @return The model for the type ontology 
//     */
//    public static OntModel getModel() {
//        return model;
//    }
//
    /**
     * Function that returns the primary reference for a tool. Return 'null' in
     * the case no reference found for given tool id.
     *
     * @param toolID - Id of tool
     * @return Return a BiBiPublication object or null
     */
    public static BiBiPublication getPrimaryReference(String toolID) {
        List<BiBiPublication> l = getReferences(toolID);
        //return either the first element of the referenc list ot null for empty references
        return (l.size() > 0 ? l.get(0) : null);
    }

    /**
     * Function to list all publication references for a tool stored in the
     * ontology. The primary reference is the first element in returned list.
     * Returns an empty list in the case no reference(s) found for given tool
     * id.
     *
     * @param toolId - Id of tool
     * @return A list of BiBiPublication objects for this tool.
     */
    public static List<BiBiPublication> getReferences(String toolID) {
        return (refmap.containsKey(toolID) ? (List<BiBiPublication>) refmap.get(toolID) : null);
    }

    /**
     * Add a list of references (in BiBTex format) to reference map as
     * BiBiPublication object
     *
     * @param name - Name of the tool the references belonging to
     * @param reflist - List of BiBiTex entries
     */
    public static void addBibsFromRunnableItem(String name, List<String> reflist) {

        List<BiBiPublication> retlist = new ArrayList<BiBiPublication>();
        StringBuilder refbuf = new StringBuilder();
        for (String string : reflist) {
            refbuf.append(string);
            refbuf.append("\n");
        }
        BibtexParser parser = new BibtexParser(new StringReader(refbuf.toString()));
        try {
            parser.parse();
            retlist = parser.getPublicationObjects();
        } catch (ParseException ex) {
            System.err.println("Given String could not be parsed as bibreference. Error was:\n" + ex.getLocalizedMessage() + "\nExiting!");
        }
        synchronized (refmap) {
            refmap.put(name, retlist);
        }
    }

    /**
     * Remove all Reference entries belonging to given tool
     *
     * @param name Name of tool the references belonging to
     */
    public static void removeBibsFromRunnableItem(String name) {
        synchronized (refmap) {
            refmap.remove(name);
        }
    }
    
    /**
     * Function that returns all author(s) given as list of BiBiPersons
     * 
     * @param name - name of the tool
     * 
     * @return Returns a list of BiBiPersons
     */
    public static List<BiBiPerson> getAuthors(String name) {
        return authormap.get(name);
    }

    /**
     * Function adding an author to a tool.
     * 
     * @param name
     * @param tp 
     */
    public static void addAuthor(String name, BiBiPerson tp) {

        if (authormap.containsKey(name)) {
            List<BiBiPerson> lp = authormap.get(name);
            synchronized (lp) {
                lp.add(tp);
            }
        } else {
            List<BiBiPerson> lp = new ArrayList();
            lp.add(tp);
            synchronized (authormap) {
                authormap.put(name, lp);
            }
        }
    }

    /**
     * Function adding a list of authors to a tool.
     * 
     * @param name
     * @param lbp 
     */
    public static void addAuthors(String name, List<BiBiPerson> lbp) {
        if (authormap.containsKey(name)) {
            List<BiBiPerson> lp = authormap.get(name);
            synchronized (lp) {
                lp.addAll(lbp);
            }
        } else {
            synchronized (authormap) {
                authormap.put(name, lbp);
            }
        }

    }
    
    /**
     * Remove a tool from the authormap
     * 
     * @param name 
     */
    public static void removeAuthors(String name){
        if (authormap.containsKey(name)) {
            synchronized(authormap) {
                authormap.remove(name);
            }
        }
    }
}
