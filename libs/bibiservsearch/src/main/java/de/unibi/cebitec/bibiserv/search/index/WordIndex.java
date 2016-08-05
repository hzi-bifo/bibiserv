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
package de.unibi.cebitec.bibiserv.search.index;

import de.unibi.cebitec.bibiserv.search.exceptions.MalformedRegularExpressionException;
import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTree;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeSearchResult;
import de.unibi.cebitec.bibiserv.search.util.Index;
import de.unibi.cebitec.bibiserv.search.util.StringPointer;
import de.unibi.cebitec.bibiserv.search.util.TransformationAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Index that maps word IDs to actual words. This is, so to speak, a dictionary.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public final class WordIndex {

    /**
     * The inverted index that contains word -> wordID entries.
     */
    private static Index<StringPointer, WordID> idIndex = new Index<>();
    /**
     * The suffix tree used for search queries.
     */
    private static SuffixTree mainTree = new SuffixTree();
    /**
     * This locks the suffix tree if words are appended or if a rebuild is
     * finished.
     */
    private static ReentrantReadWriteLock treeLock = new ReentrantReadWriteLock();
    /**
     * This locks the tree to prevent rebuilding and indexing from making each
     * other trouble.
     */
    private static ReentrantLock treeBuildLock = new ReentrantLock();
    /**
     * A temporary tree that is used for rebuilding the main tree or appending
     * words to it.
     */
    private static SuffixTree tmpTree;

    /**
     * Private constructor to prevent outside instantiation.
     */
    private WordIndex() {
    }

    /**
     * Starts an append session. This means that a copy of the suffix tree is
     * constructed that can be edited without having to stop users from doing
     * search requests at the same time.
     *
     * AFTER YOU ARE FINISHED YOU SHOULD CALL endAppendSession()!
     */
    protected static void startAppendSession() throws SuffixTreeException {
        treeBuildLock.lock();
        tmpTree = copyMainTree();
    }

    /**
     * This adds a word with the given id to a temporary tree.
     *
     * YOU HAVE TO USE start- AND endAppendSession() FOR THIS!
     *
     * @param id - id of the word that shall be appended.
     */
    protected static void appendWordToTree(WordID id) throws SuffixTreeException {
        if (tmpTree == null) {
            throw new SuffixTreeException("startAppendSession() was not called "
                    + "before calling appendWordToTree()!");
        }
        tmpTree.appendToTree(id);
    }

    /**
     * Ends an append session. This means that the temporary copy of the suffix
     * tree that was constructed during startAppendSession() is now the new
     * suffix tree used for search requests send by users.
     */
    protected static void endAppendSession() throws SuffixTreeException {
        if (tmpTree == null) {
            throw new SuffixTreeException("startAppendSession() was not called "
                    + "before calling endAppendSession()!");
        }
        treeLock.writeLock().lock();
        try {
            mainTree = tmpTree;
        } finally {
            treeLock.writeLock().unlock();
        }
        tmpTree = null;
        treeBuildLock.unlock();
    }

    /**
     * Adds a new word to the index and returns the newly generated word id for
     * this word. If the word has been in the index before, only the id is
     * returned.
     *
     * @param word new word for the index.
     * @return id associated with this word.
     */
    public static WordID indexWord(String word) throws SuffixTreeException {
        word = word.toUpperCase();
        WordID id = getID(word);
        if (id == null) {
            //if the word is new, add it to the index.
            StringPointer wordPtr = new StringPointer(word);
            id = new WordID(wordPtr);
            idIndex.put(wordPtr, id);
        }
        return id;
    }

    /**
     * The suffix tree used for searching is not automatically rebuild after
     * a document is removed from the index. Therefore this method should be
     * called if many documents are removed. It completely rebuilds the suffix
     * tree for all words that are still in the index.
     */
    public static void rebuildTree() throws SuffixTreeException {
        //prevent others from appending to the tree at the same time.
        treeBuildLock.lock();
        try {
            tmpTree = copyMainTree();
            treeLock.writeLock().lock();
            try {
                mainTree = tmpTree;
            } finally {
                treeLock.writeLock().unlock();
            }
        } finally {
            tmpTree = null;
            treeBuildLock.unlock();
        }
    }

    /**
     * constructs a copy of the main tree. This is a quite lengthy operation
     * because all words have to be appended anew.
     *
     * @return a copy of the main tree.
     */
    private static SuffixTree copyMainTree() throws SuffixTreeException {
        SuffixTree newTree = new SuffixTree();
        //append all words.
        for (WordID id : idIndex.getValues()) {
            newTree.appendToTree(id);
        }
        return newTree;
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
    public static Collection<SuffixTreeSearchResult> getTreeMatches(String pattern,
            boolean spellcorrectionAllowed)
            throws MalformedRegularExpressionException, SuffixTreeException {
        treeLock.readLock().lock();
        try {
            return mainTree.getAllMatches(pattern, spellcorrectionAllowed);
        } finally {
            treeLock.readLock().unlock();
        }
    }

    /**
     * Returns the given words wordID
     *
     * @param word a word that might be indexed.
     * @return the words ID or null if the word wasn't indexed.
     */
    public static WordID getID(String word) {
        word = word.toUpperCase();
        StringPointer wordPtr = new StringPointer(word);
        return idIndex.get(wordPtr);
    }

    /**
     *
     * @param id id of the word that shall be removed.
     * @return true if the word was contained in the index.
     */
    public static boolean removeWordByID(WordID id) {
        return removeWordByReference(id.getWordReference());
    }

    /**
     *
     * @param word the word that shall be removed
     * @return true if the word was contained in the index.
     */
    public static boolean removeWord(String word) {
        return removeWordByReference(new StringPointer(word));
    }

    /**
     *
     * @param wordRef reference to the word that shall be removed
     * @return true if the word was contained in the index.
     */
    public static boolean removeWordByReference(StringPointer wordRef) {
        return idIndex.remove(wordRef) != null;
    }

    /**
     * Resets the word index.
     */
    public static void reset() {
        idIndex.reset();
    }

    /**
     *
     * @return copy of all words that are currently indexed.
     */
    public static ArrayList<String> getWords() {
        return idIndex.transformKeySet(new RetrieveWordAction());
    }

    public static Collection<WordID> getIDs() {
        return idIndex.getValues();
    }

    /**
     *
     * @return the number of words.
     */
    public static int getIndexSize() {
        return idIndex.size();
    }
}

/**
 * Nested class to retrieve words from pointers.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
class RetrieveWordAction implements TransformationAction<StringPointer, String> {

    @Override
    public String doTransformation(StringPointer input) {
        return input.getContent();
    }
}
