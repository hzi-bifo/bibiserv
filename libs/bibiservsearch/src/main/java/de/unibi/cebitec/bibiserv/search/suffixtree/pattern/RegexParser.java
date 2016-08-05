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
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeCharacter;
import java.util.HashSet;

/**
 * This class defines general methods for the parsing of regular expressions
 * as handled here in the BiBiServ search. The regex syntax here is not the
 * complete usual regex syntax. The following constructs are allowed:
 *
 * . : any character
 * [...] : any of the characters inclucded in brackets.
 * [^...] : all characters but the ones included in brackets.
 * * : as many occurences of the previous character as you like.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public abstract class RegexParser {

    /**
     * Character that marks the start of a "one of" sequence, which means that
     * one of the characters embedded in the sequence are allowed as match.
     */
    private static final char STARTONEOF = '[';
    /**
     * Character that marks the end of a "one of" sequence.
     */
    private static final char ENDONEOF = ']';
    /**
     * Characters that mark the start of a "not one of" sequence, which means
     * that
     * all characters are allowed as match but the ones included in the
     * sequence.
     *
     * Please note that the "ENDONEOF" sign has to come before this one.
     */
    private static final char STARTNOTONEOF = '^';
    /**
     * Character that allows any character as match.
     */
    private static final char ANYCHAR = '.';
    /**
     * Character that marks an escape sequence that forces the literal
     * interpretation of the following character. e.g. \. means that . is not
     * interpreted as "any character" but as .
     */
    private static final char ESCAPE = '\\';
    /**
     * allow as many occurencec of the character before as you like.
     */
    private static final char ASMANYASYOULIKE = '*';
    /**
     * Actual parsed content.
     */
    private final char[] content;
    /**
     * The following variables mark the actual state of this parser.
     */
    private AutomatonState currentState = AutomatonState.LITERAL;
    /**
     * This stores the current index while parsing.
     */
    private int charIndex = 0;
    /**
     * If this is true the parser is ordered to stop the current parsing
     * (this can be resumed by calling "parse" again).
     * PLEASE NOTE! This is not thread safe and is only meant to by called by
     * subclasses!
     */
    private boolean suspendParsing;

    /**
     * Constructs a new regex parser.
     *
     * @param regex a regular expression that shall be parsed.
     */
    public RegexParser(String regex) {
        this.content = regex.toCharArray();
    }

    public void parse() throws MalformedRegularExpressionException {
        //initialize variables
        char currentChar;
        HashSet<SuffixTreeCharacter> sequenceCharacters = new HashSet<>();
        suspendParsing = false;
        PatternChar foundChar = null;
        //start the actual parsing
        try {
            while (charIndex < content.length && !suspendParsing) {
                currentChar = content[charIndex];
                /**
                 * Change behaviour according to current state.
                 */
                switch (currentState) {
                    case LITERAL:
                        /*
                         * if we currently are just reading characters,
                         * look for special ones that might lead us to another
                         * state.
                         */
                        switch (currentChar) {
                            case STARTONEOF:
                                /**
                                 * look what the next sign is.
                                 */
                                switch (content[charIndex + 1]) {
                                    case STARTNOTONEOF:
                                        /*
                                         * if a "not one of" sequence is
                                         * started, set
                                         * the state accordingly.
                                         */
                                        currentState = AutomatonState.NOTONEOF;
                                        charIndex++;
                                        break;
                                    default:
                                        /*
                                         * otherwise set the state to "one of"
                                         */
                                        currentState = AutomatonState.ONEOF;
                                }
                                break;
                            case ANYCHAR:
                                /**
                                 * just create an any pattern char.
                                 */
                                foundChar = new AnyPatternChar();
                                break;
                            case ESCAPE:
                                /**
                                 * start escape mode.
                                 */
                                currentState = AutomatonState.ESCAPE;
                                break;
                            default:
                                /**
                                 * per default: read the sign as exact.
                                 */
                                foundChar = new ExactPatternChar(
                                        SuffixTreeCharacter.transformChar(currentChar));
                        }
                        break;
                    case NOTONEOF:
                    case ONEOF:
                        /**
                         * look for an end of the sequence.
                         */
                        switch (currentChar) {
                            case ENDONEOF:
                                if (content[charIndex - 1] != ESCAPE) {
                                    //create a character according to respective state.
                                    switch (currentState) {
                                        case NOTONEOF:
                                            foundChar = new NotOneOfPatternChar(sequenceCharacters);
                                            break;
                                        case ONEOF:
                                            foundChar = new OneOfPatternChar(sequenceCharacters);
                                            break;
                                        default:
                                        //this can't happen, obviously.
                                    }
                                    // reset the set
                                    sequenceCharacters = new HashSet<>();
                                    //end the mode.
                                    currentState = AutomatonState.LITERAL;
                                } else {
                                    //otherwise, take the character as literal.
                                    sequenceCharacters.add(SuffixTreeCharacter.transformChar(currentChar));
                                }
                                break;
                            default:
                                //add the character to the set.
                                sequenceCharacters.add(SuffixTreeCharacter.transformChar(currentChar));
                        }
                        break;
                    case ESCAPE:
                        /**
                         * take the character as literal.
                         */
                        foundChar = new ExactPatternChar(
                                SuffixTreeCharacter.transformChar(currentChar));
                        //reset state
                        currentState = AutomatonState.LITERAL;
                        break;
                }
                /**
                 * If we got here, check if we found a character.
                 */
                if (foundChar != null) {
                    //finalize it.
                    finalizeFoundCharacter(foundChar);
                    //set back to null.
                    foundChar = null;
                }
                //increment index.
                charIndex++;
                //proceed with loop.
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new MalformedRegularExpressionException(new String(content),
                    "Content ended with an invalid character.", ex);
        }
    }

    /**
     * Internal method for last checks on found characters and how to handle
     * them.
     *
     * @param foundCharacter the character that was found.
     */
    private void finalizeFoundCharacter(PatternChar foundCharacter) {
        //check for an as many as you like sequence.
        if (charIndex < content.length - 1
                && content[charIndex + 1] == ASMANYASYOULIKE) {
            charIndex++;
            handleAsManyAsYouLike(foundCharacter);
        } else {
            //if there isn't such a sequence, call the regular handling.
            handleNewChar(foundCharacter);
        }
    }

    /**
     *
     * @return the current char index while parsing.
     */
    public int getCharIndex() {
        return charIndex;
    }

    /**
     * This is called whenever the parser found a new character.
     *
     * @param literalChar the character that was found.
     */
    public abstract void handleNewChar(PatternChar newCharacter);

    /**
     * This is called whenever the parser finds a "as many as you like"
     * sequence.
     *
     * @param newCharacter the character that may occur as often as desired.
     */
    public abstract void handleAsManyAsYouLike(PatternChar newCharacter);

    protected void suspendParsing() {
        suspendParsing = true;
    }
}

enum AutomatonState {

    LITERAL, ESCAPE, ONEOF, NOTONEOF;
}
