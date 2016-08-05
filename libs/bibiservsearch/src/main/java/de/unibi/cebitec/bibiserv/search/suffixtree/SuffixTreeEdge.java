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

import de.unibi.cebitec.bibiserv.search.index.WordID;
import de.unibi.cebitec.bibiserv.search.util.IntPointer;

/**
 * This class implements an edge in a suffix tree according to Ukkonnens
 * algorithm.
 * And edge stores not the word itself but a reference to the word, the starting
 * point and the end point in the word.
 *
 * e.g. an edge might have a reference to the word "apple" and start=1 and
 * end=4. That means that this edge has the actual letters "ppl".
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeEdge {

    private WordID wordID;
    private IntPointer start;
    private IntPointer end;
    private SuffixTreeNode target;

    /**
     * Creates a new edge for a suffix tree.
     *
     * @param id an id referencing to this edges word.
     * @param start a pointer to an int referencing to the first char in the
     * referenced word that is part of this edge.
     * @param end a pointer to an int referencing to the last char in the
     * referenced word that is part of this edge.
     * @param target the node this edge points to.
     */
    public SuffixTreeEdge(WordID id, IntPointer start, IntPointer end, SuffixTreeNode target) {
        this.wordID = id;
        this.start = start;
        this.end = end;
        this.target = target;
    }

    public IntPointer getStart() {
        return start;
    }

    public void setStart(IntPointer start) {
        this.start = start;
    }

    public IntPointer getEnd() {
        return end;
    }

    public void setEnd(IntPointer end) {
        this.end = end;
    }

    public WordID getWordID() {
        return wordID;
    }

    public SuffixTreeNode getTarget() {
        return target;
    }

    public void setTarget(SuffixTreeNode target) {
        this.target = target;
    }

    /**
     * Returns the current length of this edge. The length is the number of
     * chars that stand on this edge of the suffix tree.
     *
     * @return the current length of this edge.
     */
    public int length() {
        int length = end.getContent() - start.getContent();
        return length;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getWordOnEdge());
        builder.append("->");
        //call targets toString method.
        builder.append(target.toString());
        return builder.toString();
    }

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
     * (
     * |ABC->(end->"abc")
     * |BC->(end->"abc")
     * |C->(end->"abc")
     * )
     * </pre>
     *
     * @param depth the tab depth this edges pretty print should start with.
     * @return pretty print String.
     */
    public String prettyPrint(int depth) {
        StringBuilder builder = new StringBuilder();
        builder.append(getWordOnEdge());
        builder.append("->");
        //call targets toString method.
        builder.append(target.prettyPrint(depth));
        return builder.toString();
    }

    /**
     * This method retrieves the actual characters on this edge.
     *
     * @return the actual word on this edge.
     */
    public String getWordOnEdge() {
        int startInt = start.getContent();
        int endInt = end.getContent();
        if (target instanceof SuffixTreeLeaf) {
            /*
             * if the target is an end note, we have a $ sign on the edge that
             * is not part of the actual word. Therefore we have to decrement
             * the end int.
             */
            endInt--;
        }
        if (endInt <= startInt) {
            /*
             * if the endInt got smaller or equal than the start int, return
             * an empty string.
             */
            return "";
        }
        //retrieve the actual word referenced by this edge.
        String actualWord = wordID.getWordReference().getContent();
        //return the substring that is on this edge.
        return actualWord.substring(startInt, endInt);
    }


}
