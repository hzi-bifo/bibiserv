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

import de.unibi.cebitec.bibiserv.search.exceptions.MalformedRegularExpressionException;
import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.index.WordID;
import de.unibi.cebitec.bibiserv.search.suffixtree.pattern.SearchPattern;
import de.unibi.cebitec.bibiserv.search.suffixtree.pattern.SuffixTreeMatcher;
import java.util.Collection;
import java.util.Stack;

/**
 * This class implements a suffix tree according to Ukkonnens algorithm.
 *
 * This suffix tree constructs all suffixes of a given string in O(n) runtime
 * and O(n) storage capacity.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTree {

    private static final String NEWLINE = System.getProperty("line.separator");
    /**
     * The root node of this suffix tree.
     */
    private SuffixTreeSplitNode root = new SuffixTreeSplitNode();

    /**
     * Constructs a new (empty) suffix tree.
     */
    public SuffixTree() {
    }

    /**
     * Appends all suffixes of the given word to the suffix tree.
     * This is an implicit call to SuffixTreeConstructor.
     *
     * @param wordID the id of the word that shall be appended.
     */
    public void appendToTree(WordID wordID) throws SuffixTreeException {
        SuffixTreeConstructor constructor = new SuffixTreeConstructor(wordID, this);
        constructor.constructTree();
    }

    /**
     * This method starts a matching process for this matcher. Utilizing the
     * suffix tree it returns all words that have a suffix which matches the
     * pattern.
     *
     * @param pattern the pattern that shall be searched in the tree. This
     * method supports regular expressions, but the regular expression language
     * used here is not the full Java syntax. The following constructs are
     * allowed:
     *
     * *: any number of occurences of the char before
     * [abc]: one of the characters a,b or c
     * [^abc]: not one of the characters a,b or c
     * .: any character
     *
     * Please note that the usage of such constructs increases the runtime of
     * a search in the tree.
     *
     * @param spellcorrectionAllowed if this is true, spellcorrection mechanisms
     * are used. This means that the pattern does not have to match the tree
     * exactly but it may vary from it to a certain extent
     * (see SuffixTreeMatcher for details).
     * @return a collection of search results.
     */
    public Collection<SuffixTreeSearchResult> getAllMatches(String pattern,
            boolean spellcorrectionAllowed)
            throws MalformedRegularExpressionException, SuffixTreeException {
        SuffixTreeMatcher matcher = new SuffixTreeMatcher(
                SearchPattern.compile(pattern), root);
        return matcher.getAllMatches(spellcorrectionAllowed);
    }

    protected SuffixTreeSplitNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return root.toString();
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
     * <pre>
     *  (
     *      |ABC->(end->"abc")
     *      |BC->(end->"abc")
     *      |C->(end->"abc")
     *  )
     * </pre>
     *
     * @return pretty print String.
     */
    public String prettyPrint() {
        return root.prettyPrint(0);
    }

    protected static void newLineForPrettyPrint(int depth, StringBuilder builder) {
        builder.append(NEWLINE);
        while (depth > 0) {
            builder.append("\t");
            depth--;
        }
    }
}