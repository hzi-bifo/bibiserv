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
package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.techfak.bibiserv.util.Pair;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import java.util.List;
import org.primefaces.model.StreamedContent;

/**
 * An interface for the toolresults tag.
 * Specifies all function needed to display and execute downloads and visualizations.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public interface ToolFunctionResult {
    
    /**
     * Returns the latest by applyVisualizer generated visualization string. 
     * @return the latest by applyVisualizer generated visualization string
     */
    String getVisualization();
    
    /**
     * Gets the output as type type and generates the visualization string with a
     * visualization of type visualizer. The visualization string can then be acces via
     * getVisualization.
     * 
     * @param type Type output has to be
     * @param visualizer name of the visualizer to use.
     * @return true if success.
     */
    boolean applyVisualizer(OntoRepresentation type, String visualizer);
    
    
    /**
     * List up all possible combinations of output type and corresponding visualizer.
     * @return 
     */
    List<Pair<OntoRepresentation,List<String>>> getAllPossibleResults();
    
    
    /**
     * return stream of the resultfile in given format
     * @param type format to get
     * @return 
     */
    StreamedContent getResult(OntoRepresentation type);
    
    /**
     * Return all additional output files.
     * @return 
     */
    List<Pair<String,Pair<String,String>>> getAllOutputFiles();
    
    /**
     * Return an additional output file as stream
     * @param filename
     * @param contenttype
     * @return 
     */
    StreamedContent getAdditionalResult(String filename, String contenttype);
 
    /**
     * Returns a list of pairs <history, state> for each download/upload sorted by the index.
     * @return 
     */
    List<Pair<String, String>> getUploadDownloadData();
        
}
