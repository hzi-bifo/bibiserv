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

import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.index.WordID;
import de.unibi.cebitec.bibiserv.search.util.IntPointer;

/**
 * This class is able to append a new word to a given suffix tree according
 * to Ukkonens algorithm in O(n) runtime.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeConstructor {

    /**
     * The id of the word that is currently appended to the suffix tree.
     */
    private WordID id;
    /**
     * The actual word as character array.
     */
    private SuffixTreeCharacter[] characters;
    /**
     * The root node of this suffix tree.
     */
    private SuffixTreeSplitNode root;
    /**
     * If - during suffix tree construction - a char occurs that we have seen
     * before, we store the number of those characters we have seen before here.
     */
    private int remainder = 0;
    /**
     * The active point for suffix tree construction is characterized by three
     * attributes, the active length, the active edge and the active node.
     * It is necessary to effectively calculate splits of edges.
     */
    /**
     * If the remainder is greater zero we just have read at least one character
     * for which an edge exists in our tree already. This char references this
     * edge.
     */
    private SuffixTreeCharacter activeEdge;
    /**
     * The active length points along the activeEdge and notes how many
     * characters just occured anew along this edge (this is similar to
     * remainder, but there are situations where active length and remainder can
     * differ as you will see below)
     */
    private int activeLength = 0;
    /**
     * The active node is the root note for the active edge.
     */
    private SuffixTreeSplitNode activeNode;
    /**
     * This is the node that might be needed for the creation of suffix links.
     */
    private SuffixTreeSplitNode suffixNode;
    /**
     * This int stores the index of the current character that is appended to
     * the tree.
     */
    private int charIndex = 0;

    /**
     * Creates a SuffixTreeConstructor for the given suffixTree and the given
     * word that shall be appended to it.
     *
     * @param id id of the word that shall be appended to the tree.
     * @param tree the tree the word shall be appended too.
     */
    public SuffixTreeConstructor(WordID id, SuffixTree tree) {
        this.id = id;
        this.root = tree.getRoot();
        this.activeNode = root;
    }

    /**
     * This method does the actual tree construction for the given tree and is
     * an implementation of Ukkonens algorithm for the construction of suffix
     * trees.
     */
    protected void constructTree() throws SuffixTreeException {
        /**
         **************************
         * INITILIAZATION SECTION *
         **************************
         */
        //pointer to the end of the current word.
        IntPointer endPtr = new IntPointer(1);
        //pointer to the starting point of a new edge.
        IntPointer start;
        //current character
        SuffixTreeCharacter currentChar;
        //newly appended edge
        SuffixTreeEdge newEdge;
        //new leaf
        SuffixTreeLeaf newLeaf;
        //get word that shall be appended to the tree.
        String actualWord = id.getWordReference().getContent();
        //get its characters as array.
        characters = SuffixTreeCharacter.transformString(actualWord);
        /**
         **********************
         * UKKONENS ALGORITHM *
         **********************
         */
        while (charIndex < characters.length) {
            //increment end pointer
            endPtr.setContent(charIndex + 1);
            //get the current character
            currentChar = characters[charIndex];
            if (!root.hasEdge(currentChar)) {
                /*
                 * if no edge for the current character exists, create a new
                 * one.
                 */
                //create a new int pointer for the starting point.
                start = new IntPointer(charIndex);
                //create a new leaf at the end of the edge.
                newLeaf = new SuffixTreeLeaf();
                //create a new edge.
                newEdge = new SuffixTreeEdge(id, start, endPtr, newLeaf);
                //add edge to the current node.
                root.addEdge(newEdge, currentChar);
                /*
                 * add the reference to the current word to the end node
                 * The only exception to this rule is the case that we are now
                 * considering the branch that only has the word end sign ($) at
                 * end. If so this is a suffix of every word and storing the
                 * references would not make any sense.
                 */
                if (!currentChar.isEnd()) {
                    newLeaf.addWordID(id);
                }
                //check if splits have to be done.
                checkForSplits(start, endPtr);
            } else {
                handleExistingChar(endPtr);
            }
            suffixNode = null;
            charIndex++;
        }
        /*
         ******************
         * POSTPROCESSING *
         ******************
         */
        /*
         * decrement index and remainder. We do this because we
         * do not want to consider the end char ($) in post processing.
         */
        remainder--;
        /*
         * if we got here and the remainder is still greater zero, our new word
         * is a suffix of an existing word in the tree.
         *
         * In that case we just have to add a reference to the current word id
         * to all relevant end nodes.
         */
        while (remainder > 0) {
            /*
             * The active node has to be set. Otherwise there is an error in the
             * algorithm.
             */
            if (activeEdge == null) {
                throw new SuffixTreeException("INTERNAL ERROR: While postprocessing "
                        + "suffix tree for " + actualWord + " the remainder "
                        + "got bigger than zero but no active edge was set.");
            }
            /*
             * go along active edge and add the current word reference to the
             * respective end node.
             */
            //get the active edge
            SuffixTreeEdge actualActiveEdge = activeNode.getEdge(activeEdge);
            try {
                //get the end node.
                SuffixTreeLeaf endNode = (SuffixTreeLeaf) actualActiveEdge.getTarget();
                //append the new word.
                endNode.addWordID(id);
            } catch (ClassCastException ex) {
                //if we could not cast this to an end node, throw an exception.
                throw new SuffixTreeException("INTERNAL ERROR: While constructing "
                        + "suffix tree for " + actualWord + " an active edge in "
                        + "post processing not reference an end node", ex);
            }
            //correct active point.
            correctActivePoint();
        }
    }

    /**
     * This method checks if splits have to be done and does them if necessary.
     *
     * @param start starting point for a new edge.
     * @param endPtr the pointer referencing the end point for new edges.
     */
    private void checkForSplits(final IntPointer start,
            final IntPointer endPtr) throws SuffixTreeException {
        while (remainder > 0) {
            if (activeEdge != null) {
                //if the remainder is greater zero, order the active edge to split.
                splitEdge(start, endPtr);
                correctActivePoint();
            } else {
                /**
                 * If the active edge is not defined, look if there is an
                 * edge for the current character.
                 */
                if (activeNode.hasEdge(characters[charIndex])) {
                    //if it does, just check for suffix links and break the loop.
                    checkForSuffixLinks(activeNode);
                    break;
                } else {
                    //if it does not, create a new edge.
                    //create a new leaf at the end of the edge.
                    SuffixTreeLeaf newLeaf = new SuffixTreeLeaf();
                    //add word reference to leaf.
                    newLeaf.addWordID(id);
                    //create a new edge.
                    SuffixTreeEdge newEdge = new SuffixTreeEdge(id, start, endPtr, newLeaf);
                    //append the edge to the active node.
                    activeNode.addEdge(newEdge, characters[charIndex]);
                    //check if a suffix link has to be created.
                    checkForSuffixLinks(activeNode);
                    correctActivePoint();
                }
            }
        }
    }

    /**
     * This method checks if a suffix link has to be created and does so if
     * needed.
     *
     * @param newSuffixNode the node a suffix link might be necessary for.
     */
    private void checkForSuffixLinks(SuffixTreeSplitNode newSuffixNode) {
        if (suffixNode != null) {
            suffixNode.setSuffixLink(newSuffixNode);
        }
        suffixNode = newSuffixNode;
    }

    /**
     * This method splits the active edge if necessary. Say we have an existing
     * sufix tree likes this
     *
     * <pre>
     * (
     * |ABC->(end->"abc")
     * |BC->(end->"abc")
     * |C->(end->"abc")
     * )
     * </pre>
     *
     * and we want to append the word "abd". In that case the edge "ABC" has to
     * be split after "AB", a new node has to be inserted after "AB" and two new
     * edges have to be attached to it, one labeled "C" and one "D".
     *
     * This method does exactly that.
     *
     * @param start the pointer referencing the starting point for new edges.
     * @param endPtr the pointer referencing the end point for new edges.
     */
    private void splitEdge(final IntPointer start,
            final IntPointer endPtr) {
        //get the active edge
        SuffixTreeEdge actualActiveEdge = activeNode.getEdge(activeEdge);
        /**
         * Do the actual split.
         */
        //first get the point where the node should be split.
        IntPointer splitEnd = new IntPointer(
                actualActiveEdge.getStart().getContent() + activeLength);
        //retrieve the old target node.
        SuffixTreeNode oldNode = actualActiveEdge.getTarget();
        //create a new node.
        SuffixTreeSplitNode newSplitNode = new SuffixTreeSplitNode();
        //check for suffix links.
        checkForSuffixLinks(newSplitNode);
        //create a new edge starting at the new node and ending at the old node.
        SuffixTreeEdge splitEdge = new SuffixTreeEdge(actualActiveEdge.getWordID(),
                splitEnd, actualActiveEdge.getEnd(), oldNode);
        //add it to the new node.
        //retrieve first char.
        SuffixTreeCharacter firstChar;
        String wordOnEdge = splitEdge.getWordOnEdge();
        if (wordOnEdge.isEmpty()) {
            firstChar = SuffixTreeCharacter.getEndChar();
        } else {
            firstChar = SuffixTreeCharacter.transformChar(wordOnEdge.charAt(0));
        }
        newSplitNode.addEdge(splitEdge, firstChar);
        //also create a new edge for the new character.
        // ... with a new end node for it
        SuffixTreeLeaf newEndNode = new SuffixTreeLeaf();
        newEndNode.addWordID(id);
        SuffixTreeEdge split2Edge = new SuffixTreeEdge(id, start, endPtr, newEndNode);
        //also add it to the new node.
        newSplitNode.addEdge(split2Edge, characters[charIndex]);
        //set the old edges end point to the split end.
        actualActiveEdge.setEnd(splitEnd);
        //set the splitted edges target to the new node.
        actualActiveEdge.setTarget(newSplitNode);
    }

    /**
     * This methods corrects the active point after a split.
     */
    private void correctActivePoint() throws SuffixTreeException {
        //decrement remainder
        remainder--;
        if (activeNode == root) {
            /*
             * If the active node is root we do not have to utilize suffix
             * links. Just decrement active length and recalculate active
             * point.
             */
            activeLength--;
            if (activeLength <= 0) {
                //if the active length got zero, reset the active edge.
                activeEdge = null;
            } else {
                //otherwise set the active edge.
                activeEdge = characters[charIndex - activeLength];
            }
        } else {
            /*
             * If the active node is not root, however, try to use suffix links.
             */
            if (activeNode.getSuffixLink() != null) {
                activeNode = activeNode.getSuffixLink();
            } else {
                //if no suffix link exists, use root.
                activeNode = root;
            }
        }
        //jump if necessary
        checkForJumps();
    }

    /**
     * If - during construction - we see a char again for which an edge already
     * exists in the tree this method is called.
     *
     * @param endPtr the pointer referencing the end point for new edges.
     */
    private void handleExistingChar(final IntPointer endPtr)
            throws SuffixTreeException {
        //check if mismatches occured.
        checkForMismatches(endPtr);
        /* 
         * increment remainder and active length. By doing this we note that
         * the current edge has occured and how many chars we still have to append
         * to the tree.
         */
        remainder++;
        activeLength++;
        /* 
         * set the active edge again if necessary. This might be necessary after
         * mismatch corrections.
         */
        if (activeEdge == null && activeLength > 0) {
            //if the active edge was not set before, set it.
            activeEdge = characters[charIndex];
        }
        // check for jumps.
        checkForJumps();
    }

    /**
     * This method checks if a mismatch occurs while appending the current
     * character and does necessary splits if it does.
     *
     * @param endPtr the pointer referencing the end point for new edges.
     */
    private void checkForMismatches(IntPointer endPtr) throws SuffixTreeException {
        //try to match the character.
        if (!matchEdge()) {
            //if it does not match, do splits.
            checkForSplits(new IntPointer(charIndex), endPtr);
        }
    }

    /**
     * This method does the actual matching for the correctMismatches-method.
     *
     * It gets the content of the current edge and checks if the currently
     * appended character and the character at the active node at position
     * activeLength are equal.
     *
     * @return true if the active edge matches the currently appended character
     * and false if it does not.
     */
    private boolean matchEdge() {
        switch (activeLength) {
            case 0:
                /* 
                 * if active length is zero, try to find an edge starting with the
                 * current character.
                 */
                return activeNode.hasEdge(characters[charIndex]);
            default:
                //if active length greater zero, match against the edge.
                //first get the active edges content.
                SuffixTreeEdge actualActiveEdge = activeNode.getEdge(activeEdge);
                char[] activeEdgeContent = actualActiveEdge.getWordOnEdge().toCharArray();
                //get the match char on edge.
                SuffixTreeCharacter edgeChar;
                try {
                    edgeChar = SuffixTreeCharacter.transformChar(activeEdgeContent[activeLength]);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    //if the index is out of bounds, the char is an end char.
                    edgeChar = SuffixTreeCharacter.getEndChar();
                }
                return edgeChar.equals(characters[charIndex]);
        }
    }

    /**
     * If we have changed anything in the active point this method has to be
     * called.
     *
     * Its function is to check if the currently active edge is long enough
     * for the active edge and jump if necessary.
     */
    private void checkForJumps() throws SuffixTreeException {
        if (activeEdge != null) {
            //retrieve actual active edge and its length.
            SuffixTreeEdge actualActiveEdge = activeNode.getEdge(activeEdge);
            int activeEdgeLength = actualActiveEdge.length();
            /*
             * if the active length is longer or equal than the length of the
             * active edge, do a jump (as long as the target edge is not an end
             * node)
             */
            while (activeEdgeLength <= activeLength && !actualActiveEdge.getTarget().isEnd()) {
                /*
                 * If it is not an end node, we can jump to the next node along
                 * the active edge.
                 */
                doJump(actualActiveEdge, activeEdgeLength);
                if (activeEdge != null) {
                    actualActiveEdge = activeNode.getEdge(activeEdge);
                    activeEdgeLength = actualActiveEdge.length();
                } else {
                    /* 
                     * if the active edge is not defined, ensure that the loop
                     * does not activate again.
                     */
                    activeEdgeLength = 1;
                }
            }
        }
    }

    /**
     * Does a jump along the currently active edge. This means that the active
     * point is set to the target of the currently active edge, the active
     * length is reduced by the length of the currently active edge and the
     * active edge then is retrieved anew using the first character of the
     * remaining word.
     *
     * @param actualActiveEdge the active edge.
     * @param activeEdgeLength the active edges length.
     * @param charIndex index of the character that is currently appended to the
     * tree.
     * @param endPtr an int pointer to the end of the current branch.
     */
    private void doJump(SuffixTreeEdge actualActiveEdge, int activeEdgeLength)
            throws SuffixTreeException {
        //set the new active node.
        try {
            activeNode = (SuffixTreeSplitNode) actualActiveEdge.getTarget();
        } catch (ClassCastException ex) {
            throw new SuffixTreeException("INTERNAL ERROR: While constructing a "
                    + "suffix tree for " + id.getWordReference().getContent()
                    + " the target node of a jump was not a split node.", ex);
        }
        // decrement active length by the length of the edge we have jumped.
        activeLength -= activeEdgeLength;
        // recalculate the active edge
        if (activeLength <= 0) {
            activeEdge = null;
        } else {
            activeEdge = characters[charIndex - activeLength];
        }
    }
}
