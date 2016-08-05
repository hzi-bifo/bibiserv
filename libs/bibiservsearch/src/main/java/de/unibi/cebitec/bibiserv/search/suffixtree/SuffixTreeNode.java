/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Benjamin Paassen, CeBiTec,
 * http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 Benjamin Paassen"
 *
 * Contributor(s): Benjamin Paassen
 *
 */ 
package de.unibi.cebitec.bibiserv.search.suffixtree;

/**
 * This is an interface for nodes in a suffix tree according to Ukkonens
 * algorithm.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public interface SuffixTreeNode {
    
    @Override
    public String toString();
    
    /**
     * This is an alternative to a simple toString() call for SuffixTrees.
     * Pretty print formats the string output in newlines in a format similar
     * to XML. The tree hierarchy is visualized using tabs. A node is included
     * in brackets, an edge is visualized with a starting vertical line |, the
     * chars on the edge and a pointer -> to the node this edge is directed to.
     * end nodes are notated as (end -> "actualWordThisNodeIsReferencing").
     * 
     * e.g. the pretty print of the suffix tree for the word "abc" would look
     * like this:
     * 
     * <pre>
     *  (
     *      |ABC->(end->"abc")
     *      |BC->(end->"abc")
     *      |C->(end->"abc")
     *  )
     * </pre>
     * 
     * @param depth the tab depth this nodes pretty print should start with.
     * @return pretty print String.
     */
    public String prettyPrint(int depth);
    
    /**
     * 
     * @return true if this is an end node.
     */
    public boolean isEnd();
}
