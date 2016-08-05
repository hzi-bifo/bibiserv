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

import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.index.WordID;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeCharacter;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeEdge;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeLeaf;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeNode;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeSearchResult;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeSplitNode;
import de.unibi.cebitec.bibiserv.search.util.Tuple;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

/**
 * This class searches through a suffix tree for all matches of a given Pattern
 * and returns those matches.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeMatcher {

    /**
     * the pattern that shall be found.
     */
    private final SearchPattern pattern;
    /**
     * the start node from which one the matcher shall search.
     */
    private final SuffixTreeSplitNode startNode;

    /**
     * Constructs a new SuffixTreeMatcher
     *
     * @param pattern the pattern that shall be found.
     * @param tree the suffix tree the pattern shall be found in.
     */
    public SuffixTreeMatcher(SearchPattern pattern, SuffixTreeSplitNode startNode) {
        this.pattern = pattern;
        this.startNode = startNode;
    }

    /**
     * This method starts a matching process for this matcher. Utilizing the
     * suffix tree it returns all words that have a suffix which matches the
     * pattern to a certain extent. How far the matching word may differ from
     * the pattern is measured in edit distance. The following operations
     * increase the edit distance by 1:
     *
     * 1.) leaving out a character (e.g. the edit distance between "abc" and
     * "ac"
     * is 1, the edit distance between "abc" and "a" is 2.)
     * 2.) adding a character (e.g. the edit distance between "abc" and "abcd"
     * is 1, the edit distance between "abc" and "abbcd" is 2.)
     * 3.) swap two characters (e.g. the edit distance between "abc" and "acb"
     * is 1.
     * 4.) replacing a character (e.g. the edit distance between "abc" and "bbc"
     * is 1.
     *
     * By using a higher edit distance, search operations can compensate for
     * spelling mistakes made by users but search operations get lengthy.
     *
     * In most cases an edit distance of 1 compensates over 90% of user input
     * spelling mistakes (Martin&Jurafsky). Therefore here only an edit distance
     * of 1 is used.
     *
     * @param spellcorrectionAllowed if this is true, spellcorrection mechanisms
     * are used.
     * @return a collection of search results.
     */
    public Collection<SuffixTreeSearchResult> getAllMatches(
            final boolean spellcorrectionAllowed) throws SuffixTreeException {
        //initialize
        HashSet<SuffixTreeSearchResult> results = new HashSet<>();
        Stack<MatchingState> workingStack = new Stack<>();
        MatchingState currentState = new MatchingState(0, 0, null, startNode,
                pattern.getRoot(), spellcorrectionAllowed);
        //put the initial state on the stack.
        workingStack.push(currentState);
        while (!workingStack.isEmpty()) {
            //get the current state
            currentState = workingStack.pop();
            //push all next states.
            for (MatchingState nextState : currentState.getNextStates()) {
                workingStack.push(nextState);
            }
            //get current results.
            results.addAll(currentState.getReferences());
        }
        //return the found results.
        return results;
    }
}

/**
 * This class represents a current state while matching a suffix tree against
 * a search pattern.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
class MatchingState {

    /**
     * The length of the match until now.
     */
    private final int matchLength;
    /**
     * Active length within the suffix tree (see SuffixTreeConstructor).
     */
    private final int activeLength;
    /**
     * Active edge within the suffix tree (see SuffixTreeConstructor).
     */
    private final SuffixTreeCharacter activeEdge;
    /**
     * Active node within the suffix tree (see SuffixTreeConstructor).
     */
    private final SuffixTreeSplitNode activeNode;
    /**
     * Current state within the search patterns finite state machine.
     */
    private final FiniteStateNode currentState;
    /**
     * If this is true spellcorrection mechanisms are used in case a mismatch
     * occurs.
     */
    private final boolean spellcorrection;
    /**
     * In this variable references to found word ids can be stored (if this
     * state references a SuffixTreeEndNode in the tree).
     */
    private HashSet<SuffixTreeSearchResult> references = new HashSet<>();

    /**
     * @param matchLength The length of the match until now.
     * @param activeLength Active length within the suffix tree (see
     * SuffixTreeConstructor).
     * @param activeEdge Active edge within the suffix tree (see
     * SuffixTreeConstructor).
     * @param activeNode Active node within the suffix tree (see
     * SuffixTreeConstructor).
     * @param currentState Current state within the search patterns finite state
     * machine.
     * @param spellcorrection If this is true spellcorrection mechanisms are
     * used in case a mismatch occurs.
     */
    public MatchingState(int matchLength, int activeLength, SuffixTreeCharacter activeEdge, SuffixTreeSplitNode activeNode, FiniteStateNode currentState, boolean spellcorrection) {
        this.matchLength = matchLength;
        this.activeLength = activeLength;
        this.activeEdge = activeEdge;
        this.activeNode = activeNode;
        this.currentState = currentState;
        this.spellcorrection = spellcorrection;
    }

    public HashSet<MatchingState> getNextStates() throws SuffixTreeException {
        HashSet<MatchingState> nextStates = new HashSet<>();
        //get the finite state machines next states.
        HashSet<PatternChar> nextFSMStates = currentState.getAllEdges();
        for (PatternChar nextChar : nextFSMStates) {
            switch (nextChar.getType()) {
                case END:
                    /*
                     * If this is the patterns end, we handle the matching
                     * from now on as if we had an .*
                     * Therefore all following characters of the tree match.
                     */
                    matchAll();
                    break;
                default:
                    //try to match the states against the next characters.
                    if (activeLength == 0) {
                        //if we did not go along an edge just now, we have a multi match.
                        nextStates.addAll(multiMatch(nextChar));
                    } else {
                        //else we have a single match.
                        nextStates.addAll(singleMatch(nextChar));
                    }
            }
        }
        //return the next states.
        return nextStates;
    }

    public HashSet<SuffixTreeSearchResult> getReferences() {
        if (references == null) {
            return new HashSet<>();
        } else {
            return references;
        }
    }

    /**
     * This method takes a single character from a suffix tree branch and tries
     * to match it against the given pattern character.
     *
     * THIS ONLY WORKS IF THE ACTIVE LENGTH IS NOT ZERO AND THE ACTIVE EDGE
     * IS DEFINED!
     *
     * @param nextChar the pattern char the suffix tree character shall be
     * matched against.
     * @return all following matching states (or an empty set if there are
     * none).
     */
    private HashSet<MatchingState> singleMatch(PatternChar nextChar)
            throws SuffixTreeException {
        //retrieve the current tree character.
        SuffixTreeEdge actualActiveEdge = activeNode.getEdge(activeEdge);
        String actualWord = actualActiveEdge.getWordOnEdge();
        SuffixTreeCharacter matchingChar;
        try {
            matchingChar = SuffixTreeCharacter.transformChar(actualWord.charAt(activeLength));
        } catch (StringIndexOutOfBoundsException ex) {
            //if the string index is out of bounds, the matching character is the end character.
            matchingChar = SuffixTreeCharacter.getEndChar();
        }
        if (nextChar.matchSingleCharacter(matchingChar)) {
            //if the character matches, retrieve the next states and return them.
            return constructFollowingStates(nextChar, matchingChar, actualActiveEdge);
        } else {
            //if it does not match, try to correct the mismatch by using edit options.
            if (spellcorrection) {
                return constructEditStates(matchingChar);
            } else {
                //if spellcorrection is not allowed, return an empty set.
                return new HashSet<>();
            }
        }
    }

    /**
     * This method takes all single characters that start at the currently
     * active node in the suffix tree and tries to match them against the given
     * pattern character.
     *
     * THIS ONLY WORKS IF THE ACTIVE LENGTH IS ZERO AND THE ACTIVE EDGE
     * IS NOT DEFINED!
     *
     * @param nextChar the pattern char the suffix tree character shall be
     * matched against.
     * @return all following matching states (or an empty set if there are
     * none).
     */
    private HashSet<MatchingState> multiMatch(PatternChar nextChar)
            throws SuffixTreeException {
        HashSet<MatchingState> nextStates = new HashSet<>();
        //retrieve characters starting at the current node.
        HashSet<SuffixTreeCharacter> allEdges = activeNode.getAllEdges();
        //get the matching characters.
        Tuple<Collection<SuffixTreeCharacter>, Collection<SuffixTreeCharacter>> matchingCharacters = nextChar.getMatchingCharacters(allEdges);
        for (SuffixTreeCharacter matchingChar : matchingCharacters.getFirst()) {
            //if one of the edges matches, construct the following states for it.
            SuffixTreeEdge nextActiveEdge = activeNode.getEdge(matchingChar);
            nextStates.addAll(constructFollowingStates(nextChar,
                    matchingChar, nextActiveEdge));
        }
        if (spellcorrection) {
            for (SuffixTreeCharacter mismatchingChar : matchingCharacters.getSecond()) {
                //if it does not match, try to correct the mismatch by using edit options.
                nextStates.addAll(constructEditStates(mismatchingChar));
            }
        }
        return nextStates;
    }

    /**
     * Constructs the following states for a matching pair of a pattern char and
     * a suffix tree character.
     *
     * @param nextChar the pattern char that matches the suffix tree character.
     * @param matchingCharacter the suffix tree character that matches the
     * pattern char.
     * @param actualActiveEdge the actual active tree edge for this matching
     * character.
     * @return a set of following states.
     */
    private HashSet<MatchingState> constructFollowingStates(PatternChar nextChar,
            SuffixTreeCharacter matchingChar, SuffixTreeEdge actualActiveEdge)
            throws SuffixTreeException {
        // retrieve the next FSA states.
        HashSet<FiniteStateNode> nextFSAStates = currentState.getNext(nextChar);
        if (matchingChar.isEnd()) {
            /* 
             * if the matching character is an end character, look if any of 
             * the following states is accepting.
             */
            boolean accepting = false;
            for (FiniteStateNode nextFSAState : nextFSAStates) {
                if (nextFSAState.isAccepting()) {
                    accepting = true;
                }
            }
            if (accepting) {
                /* 
                 * if at least one state is accepting, retrieve the wordIDs
                 * on the this end node of the tree.
                 */
                try {
                    SuffixTreeLeaf leaf = (SuffixTreeLeaf) actualActiveEdge.getTarget();
                    for (WordID id : leaf.getWordIDs()) {
                        references.add(new SuffixTreeSearchResult(matchLength, spellcorrection, id));
                    }
                } catch (ClassCastException ex) {
                    throw new SuffixTreeException("Edge with a $ character "
                            + "does not reference an end node.", ex);
                }
            }
            //return empty set.
            return new HashSet<>();
        } else {
            /*
             * if the matching character isn't an end character, we have
             * to calculate next states.
             */
            //recalculate active point.
            int newActiveLength = activeLength + 1;
            SuffixTreeSplitNode newActiveNode;
            SuffixTreeCharacter newActiveEdge = null;
            if (newActiveLength == actualActiveEdge.length()) {
                //if we reached the end of the current branch, jump to the next
                try {
                    newActiveNode = (SuffixTreeSplitNode) actualActiveEdge.getTarget();
                    newActiveLength = 0;
                } catch (ClassCastException ex) {
                    throw new SuffixTreeException("An edge references an end node "
                            + "without having a $ character.");
                }
            } else {
                //if we did not, take the same active edge and node as before.
                if (activeEdge != null) {
                    newActiveEdge = activeEdge;
                } else {
                    /* 
                     * ... but if we had no active edge before, take the matching
                     * char as active edge.
                     */
                    newActiveEdge = matchingChar;
                }
                newActiveNode = activeNode;
            }
            //construct new states.
            HashSet<MatchingState> nextStates = new HashSet<>();
            MatchingState nextState;
            for (FiniteStateNode nextFSAState : nextFSAStates) {
                nextState = new MatchingState(matchLength + 1,
                        newActiveLength,
                        newActiveEdge,
                        newActiveNode,
                        nextFSAState,
                        spellcorrection);
                nextStates.add(nextState);
            }
            return nextStates;
        }
    }

    /**
     * This method constructs all states that might resolve a mismatch by either
     * of the following operations:
     *
     * 1.) remove a character from the search pattern (gap)
     * 2.) insert a character in the search pattern (insert)
     * 3.) replace a character in the search pattern (replace)
     *
     * @param matchingChar the char that shall be matched.
     * @return the following states that are the result of the given operations.
     */
    private HashSet<MatchingState> constructEditStates(
            SuffixTreeCharacter matchingChar) {
        HashSet<MatchingState> editStates = new HashSet<>();
        /* 
         * gap states are constructed by jumping to each following state of the
         * current one in the search pattern.
         */
        for (PatternChar nextChar : currentState.getAllEdges()) {
            for (FiniteStateNode nextFSAState : currentState.getNext(nextChar)) {
                editStates.add(new MatchingState(matchLength, activeLength, activeEdge,
                        activeNode, nextFSAState, false));
            }
        }
        //construct insert state.
        MatchingState insertState;
        if ((insertState = constructInsertState(currentState, matchingChar)) != null) {
            editStates.add(insertState);
        }
        //construct swap states.
        editStates.addAll(contstructSwapStates(matchingChar));
        /**
         * Finally construct replace states. These are a combination of going
         * to the next FSA state and increasing the active length.
         */
        for (PatternChar nextChar : currentState.getAllEdges()) {
            for (FiniteStateNode nextFSAState : currentState.getNext(nextChar)) {
                if ((insertState = constructInsertState(nextFSAState, matchingChar)) != null) {
                    editStates.add(insertState);
                }
            }
        }
        return editStates;
    }

    /**
     * this method constructs states that result from an insert into the search
     * pattern in inexact matching.
     *
     * e.g. if we try to match the word "abc" with the query "ac" we can do that
     * by increasing the active length in the suffix tree by 1.
     *
     * @param newFSAState the state of the FSA that shall be used for
     * constructing
     * the return states.
     * @return all states that result from that insert.
     */
    private MatchingState constructInsertState(
            FiniteStateNode newFSAState, SuffixTreeCharacter mismatchChar) {
        /*
         * insert states are created by incrementing the active length.
         */
        //get the active edge.
        SuffixTreeCharacter newActiveEdge;
        if (activeEdge == null) {
            newActiveEdge = mismatchChar;
        } else {
            newActiveEdge = activeEdge;
        }
        SuffixTreeEdge actualActiveEdge = activeNode.getEdge(newActiveEdge);
        //recalculate active point.
        int newActiveLength = activeLength + 1;
        SuffixTreeSplitNode newActiveNode;
        if (newActiveLength == actualActiveEdge.length()) {
            //if we reached the end of the current branch, jump to the next
            try {
                newActiveNode = (SuffixTreeSplitNode) actualActiveEdge.getTarget();
                newActiveLength = 0;
                newActiveEdge = null;
            } catch (ClassCastException ex) {
                /* 
                 * if the new active node would be a leaf and the current state
                 * is accepting, add the references of it.
                 */
                if (currentState.isAccepting()) {
                    SuffixTreeLeaf leaf = (SuffixTreeLeaf) actualActiveEdge.getTarget();
                    for (WordID id : leaf.getWordIDs()) {
                        references.add(new SuffixTreeSearchResult(matchLength,
                                spellcorrection, id));
                    }
                }
                return null;
            }
        } else {
            //if we did not, take the same active edge and node as before.
            newActiveNode = activeNode;
        }
        //return new state
        return new MatchingState(matchLength + 1, newActiveLength, newActiveEdge,
                newActiveNode, newFSAState, false);
    }

    /**
     * This method tries to swap states of the patterns finite state machine to
     * match the tree. e.g. if the pattern is "baer" it tries to find "bear".
     *
     * Please note: As many as you like constructs are not (!) swapped.
     *
     * @param matchingChar the character that created a mismatch.
     * @return set of states that swapped around states.
     */
    private HashSet<MatchingState> contstructSwapStates(
            SuffixTreeCharacter matchingChar) {
        ArrayList<FiniteStateNode> nextFSAStates = new ArrayList<>();
        // go through all following states.
        for (FiniteStateNode followingState : currentState.getAllNextStates()) {
            if (!currentState.equals(followingState)) {
                //if it is not the current state again, get all edges.
                for (PatternChar edge : followingState.getAllEdges()) {
                    switch (edge.getType()) {
                        case END:
                            /* 
                             * if the following state is an end char,
                             * a swap would not make sense.
                             */
                            break;
                        default:
                            if (edge.matchSingleCharacter(matchingChar)) {
                                /* 
                                 * if one of the edges matches, get the 
                                 * following states of it.
                                 */
                                for (FiniteStateNode followingState2 : followingState.getNext(edge)) {
                                    if (!followingState2.equals(followingState)) {
                                        /*
                                         * if it is not the same as the state before,
                                         * add it to the list of next states.
                                         */
                                        nextFSAStates.add(followingState2);
                                    }
                                }
                            }
                    }
                }
            }
        }
        HashSet<MatchingState> swapStates = new HashSet<>(nextFSAStates.size());
        if (!nextFSAStates.isEmpty()) {
            //retrieve all edges of this state that do not lead to itself.
            ArrayList<PatternChar> edges = new ArrayList<>();
            for (PatternChar edge : currentState.getAllEdges()) {
                HashSet<FiniteStateNode> next = currentState.getNext(edge);
                next.remove(currentState);
                if (!next.isEmpty()) {
                    edges.add(edge);
                }
            }
            //go through all found states.
            for (FiniteStateNode nextFSAState : nextFSAStates) {
                //if we found anything, create a virtual FSA state
                FiniteStateNode virtualState = new FiniteStateNode(false);
                /* 
                 * add a connection to the next state for all outgoing edges 
                 * that do not reference the state itself.
                 */
                for (PatternChar edge : edges) {
                    virtualState.addEdge(edge, nextFSAState);
                }
                //increase the active length and add the insert state for it.
                swapStates.add(constructInsertState(virtualState, matchingChar));
            }
        }
        return swapStates;
    }

    /**
     * This method is used if we want to match all characters that follow the
     * current place in the suffix tree (which means that we reached the end
     * of our search pattern and handle it as .*).
     */
    private void matchAll() {
        //stack for all following split nodes for depth first search.
        Stack<SuffixTreeSplitNode> nodes = new Stack<>();
        if (activeLength == 0) {
            //if the active length is zero, take the active node as initial node.
            nodes.push(activeNode);
        } else {
            //otherwise use the target node.
            SuffixTreeNode target = activeNode.getEdge(activeEdge).getTarget();
            if (target instanceof SuffixTreeSplitNode) {
                nodes.push((SuffixTreeSplitNode) target);
            } else {
                //otherwise we have a leaf and just have to store all references.
                for (WordID id : ((SuffixTreeLeaf) target).getWordIDs()) {
                    references.add(new SuffixTreeSearchResult(matchLength,
                            spellcorrection, id));
                }
                return;
            }
        }
        SuffixTreeSplitNode currentNode;
        SuffixTreeNode nextNode;
        while (!nodes.empty()) {
            currentNode = nodes.pop();
            HashSet<SuffixTreeCharacter> allEdges = currentNode.getAllEdges();
            //analyse all following nodes.
            for (SuffixTreeCharacter edgeChar : allEdges) {
                nextNode = currentNode.getEdge(edgeChar).getTarget();
                if (nextNode instanceof SuffixTreeSplitNode) {
                    //if the next node is also a split node, push it onto the stack.
                    nodes.push((SuffixTreeSplitNode) nextNode);
                } else {
                    //otherwise it's a leaf, so add all word references to the result set.
                    for (WordID id : ((SuffixTreeLeaf) nextNode).getWordIDs()) {
                        references.add(new SuffixTreeSearchResult(matchLength,
                                spellcorrection, id));
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("active_node: ");
        builder.append(activeNode.hashCode());
        builder.append(", active_edge: ");
        if (activeEdge != null) {
            builder.append(activeEdge.toString());
        } else {
            builder.append("null");
        }
        builder.append(", active_length: ");
        builder.append(activeLength);
        builder.append(", state: ");
        builder.append(currentState.toString());
        builder.append(", spellcorrection allowed: ");
        builder.append(Boolean.toString(spellcorrection));
        return builder.toString();
    }
}
