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
package de.unibi.cebitec.bibiserv.search.suffixtree.pattern;

import de.unibi.cebitec.bibiserv.search.util.MultiMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

/**
 * This is a node (or a state) of a finite state automaton for pattern matching
 * in suffix trees.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class FiniteStateNode {

    /**
     * The next states referenced by the character that is needed to get to
     * them.
     *
     * This has the default capacity 2 because in normal cases never more than
     * two edges start at a FiniteStateNode.
     */
    private MultiMap<PatternChar, FiniteStateNode> nextStates = new MultiMap<>(2);
    /**
     * Is this an accepting finish state?
     */
    private boolean accepting;

    /**
     *
     * @param accepting Is this an accepting finish state?
     */
    public FiniteStateNode(boolean accepting) {
        this.accepting = accepting;
    }

    /**
     * Returns the next state for the given character.
     *
     * @param character a character that shall be matched.
     * @return the next state or null if the character is not accepted.
     */
    public HashSet<FiniteStateNode> getNext(PatternChar character) {
        return nextStates.get(character);
    }

    /**
     *
     * @return all pattern chars that lead to next states from this on.
     */
    public HashSet<PatternChar> getAllEdges() {
        return nextStates.keySet();
    }

    public HashSet<FiniteStateNode> getAllNextStates() {
        return nextStates.valueSet();
    }

    /**
     * Adds a new branch to this node. Please note that only one following state
     * per character is allowed.
     *
     * @param referencingCharacter character on the branch to the next state.
     * @param nextState the next state after the given branch.
     */
    public void addEdge(PatternChar referencingCharacter, FiniteStateNode nextState) {
        nextStates.put(referencingCharacter, nextState);
    }

    /**
     *
     * @return true if this is an accepting finishing state.
     */
    public boolean isAccepting() {
        return accepting;
    }

    /**
     *
     * @param accepting true if this is an accepting finishing state.
     */
    public void setAccepting(boolean accepting) {
        this.accepting = accepting;
    }

    @Override
    public String toString() {
        //already visited nodes.
        HashMap<FiniteStateNode, Integer> visitedNodes = new HashMap<>();
        //queue for next nodes.
        Queue<FiniteStateNode> next = new ArrayDeque<>();
        //string builder for the actual content.
        StringBuilder builder = new StringBuilder();
        //loop variables
        FiniteStateNode currentNode;
        Integer currentLabel;
        int counter = 1;
        //add initial state to queue.
        next.add(this);
        // ... and to the list of visited nodes.
        visitedNodes.put(this, 0);
        //start algorithm
        while ((currentNode = next.poll()) != null) {
            //append the label.
            builder.append(visitedNodes.get(currentNode));
            if (currentNode.isAccepting()) {
                //if this is an end node, just write "accepting"
                builder.append(" (accepting)");
            }
            //else reference all next nodes.
            builder.append(" : (");
            //sort the set of outgoing edges.
            ArrayList<PatternChar> edges = new ArrayList<>();
            edges.addAll(currentNode.nextStates.keySet());
            Collections.sort(edges, new PatternCharComparator());
            for (PatternChar key : edges) {
                //append all branches to other nodes
                builder.append("|");
                builder.append(key.toString());
                builder.append(" -> ");
                //get the labels of referenced states.
                ArrayList<Integer> referencedLabels = new ArrayList<>();
                for (FiniteStateNode nextState : currentNode.nextStates.get(key)) {
                    //append their respective labels if possible.
                    currentLabel = visitedNodes.get(nextState);
                    if (currentLabel == null) {
                        //if the node was not visited before, create a label for it
                        currentLabel = counter;
                        counter++;
                        //and add the state to the queue
                        next.add(nextState);
                        //and add it to the map of visited nodes.
                        visitedNodes.put(nextState, currentLabel);
                    }
                    referencedLabels.add(currentLabel);
                }
                //sort the list of labels.
                Collections.sort(referencedLabels);
                for (Integer label : referencedLabels) {
                    //append the label
                    builder.append(label);
                    builder.append(",");
                }
                //remove the last , 
                builder.delete(builder.length() - 1, builder.length());
            }
            builder.append("),");
        }

        if (nextStates.isEmpty()) {
            builder.append("empty)");
        } else {
            //remove last ,
            builder.delete(builder.length() - 1, builder.length());
        }
        return builder.toString();
    }
}

class PatternCharComparator implements Comparator<PatternChar> {

    @Override
    public int compare(PatternChar o1, PatternChar o2) {
        return o1.hashCode() - o2.hashCode();
    }
}