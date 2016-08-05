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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class implements a node in a suffix tree that has further edges starting
 * in it.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeSplitNode implements SuffixTreeNode {

    /**
     * HashMap of edges that start at this node.
     */
    private HashMap<SuffixTreeCharacter, SuffixTreeEdge> edges = new HashMap<>();
    /**
     * Suffix link to another split node.
     */
    private SuffixTreeSplitNode suffixLink;

    /**
     * Trivial consturctor.
     */
    public SuffixTreeSplitNode() {
    }

    /**
     * Adds an edge to this node.
     *
     * @param edge A new edge for this node.
     * @param firstChar the first char of this edge.
     */
    public void addEdge(SuffixTreeEdge edge, SuffixTreeCharacter firstChar) {
        edges.put(firstChar, edge);
    }

    /**
     * Retrieves the edge starting in this node beginning with the given char.
     *
     * @param firstChar first char of this edge.
     * @return the respective edge or null if no edge begins with the given
     * char.
     */
    public SuffixTreeEdge getEdge(SuffixTreeCharacter firstChar) {
        return edges.get(firstChar);
    }

    /**
     * Returns whether this node has an edge with the given first char.
     *
     * @param firstChar the first char this node might have an edge to.
     * @return true if an edge for this first char exists.
     */
    public boolean hasEdge(SuffixTreeCharacter firstChar) {
        return edges.containsKey(firstChar);
    }

    /**
     *
     * @return all characters that reference edges.
     */
    public HashSet<SuffixTreeCharacter> getAllEdges() {
        HashSet<SuffixTreeCharacter> copy = new HashSet<>();
        copy.addAll(edges.keySet());
        return copy;
    }

    protected void setSuffixLink(SuffixTreeSplitNode suffixLink) {
        this.suffixLink = suffixLink;
    }

    protected SuffixTreeSplitNode getSuffixLink() {
        return suffixLink;
    }

    @Override
    public String toString() {
        Collection<SuffixTreeEdge> actualEdges = edges.values();
        if (actualEdges.isEmpty()) {
            return "(empty)";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (SuffixTreeEdge edge : actualEdges) {
            builder.append("|");
            builder.append(edge.toString());
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String prettyPrint(int depth) {
        Collection<SuffixTreeEdge> actualEdges = edges.values();
        if (actualEdges.isEmpty()) {
            return "(empty)";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (SuffixTreeEdge edge : actualEdges) {
            SuffixTree.newLineForPrettyPrint(depth + 1, builder);
            builder.append("|");
            builder.append(edge.prettyPrint(depth + 1));
        }
        SuffixTree.newLineForPrettyPrint(depth, builder);
        builder.append(")");
        return builder.toString();
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}
