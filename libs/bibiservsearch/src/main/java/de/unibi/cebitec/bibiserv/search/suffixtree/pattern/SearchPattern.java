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

import de.unibi.cebitec.bibiserv.search.exceptions.MalformedRegularExpressionException;
import de.unibi.cebitec.bibiserv.search.util.Tuple;
import java.util.ArrayList;
import java.util.Stack;

/**
 * This class represents a Pattern that shall be matched in a search on
 * BiBiServ.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SearchPattern {

    private FiniteStateNode root;

    private SearchPattern(FiniteStateNode root) {
        this.root = root;
    }

    public FiniteStateNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    /**
     * Takes a regular expression string and converts it into a finite state
     * automaton. The regular expression language used here is not the full
     * Java syntax. The following constructs are allowed:
     *
     * *: any number of occurences of the char before
     * [abc]: one of the characters a,b or c
     * [^abc]: not one of the characters a,b or c
     * .: any character
     *
     * @param regex the regular expression string.
     * @return SearchPattern referencing the finite state automaton for this
     * regex.
     */
    public static SearchPattern compile(String regex)
            throws MalformedRegularExpressionException {
        //validate.
        if (!validate(regex)) {
            throw new MalformedRegularExpressionException(regex,
                    "Regular expression would match all words.");
        }
        //prepare converter.
        RegexToAutomatonConverter converter = new RegexToAutomatonConverter(regex);
        converter.parse();
        //create SearchPattern
        return new SearchPattern(converter.getRoot());
    }

    /**
     * Checks if a regular expression is valid according to internal standard.
     *
     * The internal standard only requests that a minimum match for this regex
     * is at least one character long.
     *
     * @param regex a regular expression.
     * @return true if the regex is valid.
     */
    public static boolean validate(String regex) throws MalformedRegularExpressionException {
        //prepare parser.
        SimpleValidationParser validationParser = new SimpleValidationParser(regex);
        //parse
        validationParser.parse();
        //return result.
        return validationParser.isValid();

    }
}

/**
 * This parser does a fairly simple validation for input regexes: The criterion
 * for a valid regex here is, that it contains at least one PatternChar that is
 * not an as-many-as-you-like-char, because if it does, all words are allowed
 * as matches.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
class SimpleValidationParser extends RegexParser {

    private boolean valid = false;

    public SimpleValidationParser(String regex) {
        super(regex);
    }

    @Override
    public void handleNewChar(PatternChar newCharacter) {
        /*
         * if we got here, the regex is not trivial.
         */
        endParsing();
    }

    @Override
    public void handleAsManyAsYouLike(PatternChar newCharacter) {
        //do nothing.
    }

    private void endParsing() {

        super.suspendParsing();
        valid = true;
    }

    public boolean isValid() {
        return valid;
    }
}

/**
 * This class converts a given regular expression to a non-deterministic
 * finite state automaton.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
class RegexToAutomatonConverter extends RegexParser {

    /**
     * The length of the input regex.
     */
    private int length;
    /**
     * The root node of the automaton.
     */
    private final FiniteStateNode root = new FiniteStateNode(false);
    /**
     * the currently handled node.
     */
    private FiniteStateNode currentNode = root;
    /**
     * The last characters in a row that were referenced in an "as many as you
     * like" sequence.
     * This is stored to ensure that the automaton is deterministic.
     */
    private Stack<PatternChar> asManyAsYouLikeStore = new Stack<>();
    /**
     * This boolean is true if we are still at the start of the search pattern
     * compilation.
     *
     * This information is needed because AsManyAsYouLike-constructs will
     * be deleted at the beginning of the search pattern. These constructs are
     * not needed because a search in the suffix tree will handle .* at the
     * beginning implicitly.
     */
    boolean start = true;

    public RegexToAutomatonConverter(String regex) {
        super(regex);
        this.length = regex.length();
    }

    @Override
    public void handleNewChar(PatternChar newCharacter) {
        start = false;
        //create a new edge to a new state.
        FiniteStateNode newNode = new FiniteStateNode(false);
        currentNode.addEdge(newCharacter, newNode);
        if (!asManyAsYouLikeStore.isEmpty()) {
            //empty the stack.
            constructAsManyAsYouLikeChain(newCharacter, newNode, false);
        }
        //append the finishing node if this is the end.
        if (lastState()) {
            FiniteStateNode finishingNode = new FiniteStateNode(true);
            newNode.addEdge(new PatternEndChar(), finishingNode);
        } else {
            //the new node is the new current node.
            currentNode = newNode;
        }
    }

    @Override
    public void handleAsManyAsYouLike(PatternChar newCharacter) {
        if (lastState()) {
            //if this is the last state, ignore the stack and append the finishing node.
            currentNode.addEdge(new PatternEndChar(), new FiniteStateNode(true));
            return;
        }
        if (start) {
            //if we are still at the start (and not at the end), do nothing.
            return;
        }
        /*
         * Do optimization of asManyAsYouLike sequences.
         * Optimizations are possible if one of two asManyAsYouLike characters that
         * follow each other is a superset of the other one. In that case
         * the subset can be deleted without changing the function of the pattern.
         * 
         * e.g.: The pattern-substring .*a*b*c* can be optimized to .*
         */
        PatternChar otherChar;
        PatternChar superSet = newCharacter;
        while (superSet != null && !asManyAsYouLikeStore.empty()) {
            otherChar = asManyAsYouLikeStore.pop();
            superSet = newCharacter.findSuperSet(otherChar);
            if (superSet == null) {
                //if no optimization is possible, push the other char back.
                asManyAsYouLikeStore.push(otherChar);
            } else {
                //if optimization could be done, reset the new character.
                newCharacter = superSet;
            }
        }
        //push the new character on the stack.
        asManyAsYouLikeStore.push(newCharacter);
    }

    /**
     * Constructs a chain for the stored "as many as you like" sequences that
     * occured up to this point. Why this needs a special handling function can
     * be illustrated in the following example:
     *
     * Regex: a*b*c
     *
     * Finite state automaton:
     *
     * 0:
     * a -> 0
     * b -> 1
     * c -> 2
     *
     * 1:
     * b -> 1
     * c -> 2
     *
     * 2 (accepting)
     *
     * @param lastCharacter The character that leads to the last state.
     * @param lastState the last node of this automaton.
     * @param lastStateIsAsManyAsYouLike This should be set to true if the last
     * state is an asManyAsYouLike state as well (which means that all previous
     * asManyAsYouLike-states also are accepting states).
     * Please note: In this implementation this is always false. However this
     * method could also support this.
     */
    private void constructAsManyAsYouLikeChain(PatternChar lastCharacter,
            FiniteStateNode lastState, boolean lastStateIsAsManyAsYouLike) {
        PatternChar currentChar;
        FiniteStateNode newNode;
        ArrayList<Tuple<PatternChar, FiniteStateNode>> followingStates =
                new ArrayList<>();
        //add the last state to the list.
        followingStates.add(new Tuple<>(lastCharacter, lastState));
        while (asManyAsYouLikeStore.size() > 0) {
            currentChar = asManyAsYouLikeStore.pop();
            //create a new node for each character on the stack.
            if (asManyAsYouLikeStore.size() == 0) {
                // ... if it is not the last node that wasn't an as many as you like node.
                newNode = currentNode;
                newNode.setAccepting(lastStateIsAsManyAsYouLike);
            } else {
                newNode = new FiniteStateNode(lastStateIsAsManyAsYouLike);
            }
            //add a connection to itself.
            newNode.addEdge(currentChar, newNode);
            //add a connection to all previously constructed states.
            for (Tuple<PatternChar, FiniteStateNode> state : followingStates) {
                newNode.addEdge(state.getFirst(), state.getSecond());
            }
            //add the node itself to the list of created states.
            followingStates.add(new Tuple<>(currentChar, newNode));
        }
    }

    /**
     *
     * @return true if the current state is the last one.
     */
    private boolean lastState() {
        return length == super.getCharIndex() + 1;
    }

    /**
     *
     * @return the root node of the finished FiniteStateAutomaton.
     */
    public FiniteStateNode getRoot() {
        return root;
    }
}