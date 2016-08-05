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
import de.unibi.cebitec.bibiserv.search.results.ExactSearchResult;
import de.unibi.cebitec.bibiserv.search.results.RegularSearchResult;
import de.unibi.cebitec.bibiserv.search.results.SearchResult;
import de.unibi.cebitec.bibiserv.search.results.WithoutCorrectionSearchResult;
import de.unibi.cebitec.bibiserv.search.suffixtree.SuffixTreeSearchResult;
import de.unibi.cebitec.bibiserv.search.util.Collections;
import de.unibi.cebitec.bibiserv.search.util.Index;
import de.unibi.cebitec.bibiserv.search.util.TransformationAction;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains the main index that maps word ids to various IDs that are
 * used to identify actual sites/documents on BiBiServ2.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public final class MainIndex {

    /**
     * The actual index that is used.
     */
    private static Index<WordID, OccurenceSet> mainIndex = new Index<>();

    /**
     * Private constructor to prevent outside instantiation.
     */
    private MainIndex() {
    }

    /**
     * Adds the occurence of a word to the index.
     *
     * @param wordID id of the word that occured.
     * @param document the document the word occured in.
     */
    public static void indexOccurence(WordID wordID, BiBiServDocument document)
            throws SuffixTreeException {
        indexOccurence(wordID, null, document);
    }

    /**
     * Adds the occurence of a word to the index.
     *
     * @param wordID id of the word that occured.
     * @param last the last words occurence that came in the text before this
     * one.
     * @param document the document the word occured in.
     * @return this words occurence object.
     */
    public static Occurence indexOccurence(WordID wordID, Occurence last,
            BiBiServDocument document) throws SuffixTreeException {
        //build an occurence.
        Occurence occurence = new Occurence(document.getId(), wordID);
        //set the last words follower to this words occurence.
        if (last != null) {
            last.setNext(occurence);
            //and the predecessor of this word to the last one.
            occurence.setPrevious(last);
        }
        //retrieve the current set of occurences.
        OccurenceSet occSet = mainIndex.get(wordID);
        if (occSet == null) {
            //if the word did not occur before, create a new set.
            occSet = new OccurenceSet();
            occSet.addOccurence(occurence);
            mainIndex.put(wordID, occSet);
        } else {
            //if we got here, we got the old occurenceSet and can add the occurence.
            occSet.addOccurence(occurence);
        }
        return occurence;
    }

    /**
     * Retrieves all exact (!) occurences of a given word.
     *
     * @param word the word to search for.
     * @return a list of document identifiers that contain this word.
     */
    public static ExactSearchResult searchExactly(String word) {
        //retrieve word id.
        WordID wordID = WordIndex.getID(word);
        //return the search result.
        return new ExactSearchResult(1, wordID);
    }

    /**
     * Retrieves all occurences of words that contain the given word as
     * substring.
     *
     * @param word the word to search for.
     * @param withSpellCorrection set this to true if you also want to use
     * spell correction features. For details please have a look at the
     * SuffixTreeMatcher class.
     * @return a set of occurences words that contain the given word as
     * substring as well as the information if spell correction was needed to
     * find the word.
     */
    public static Collection<SearchResult> searchSuperStrings(
            String word, boolean withSpellCorrection)
            throws MalformedRegularExpressionException, SuffixTreeException {
        //get all ids of words that contain the given word as substring.
        Collection<SuffixTreeSearchResult> allMatches =
                WordIndex.getTreeMatches(
                word, withSpellCorrection);
        return Collections.transform(
                allMatches, new SearchResultTransformation(withSpellCorrection));
    }

    /**
     * Returns all occurences of a word with the given ID.
     *
     * @param wordID the wordID.
     * @return all occurences of the word with that id.
     */
    public static Collection<Occurence> getOccurencesForWordID(WordID wordID) {
        if (wordID != null) {
            //retrieve set of occurences.
            OccurenceSet occSet = mainIndex.get(wordID);
            if (occSet != null) {
                return occSet.getAllOccurences();
            } else {
                RuntimeException ex = new RuntimeException("Word"
                        + wordID.getWordReference().getContent()
                        + " was indexed but there was no entry for it in the main index!");
                Logger.getLogger(MainIndex.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        }
        return null;
    }

    /**
     * Returns the number of occurences in the index. Please note that this
     * method has linear runtime for the number of words stored!
     * 
     * @return the number of occurences in the index.
     */
   public static int getNumberOfOccurences(){
       int size = 0;
       for(OccurenceSet occSet : mainIndex.getValues()){
           size += occSet.size();
       }
       return size;
   }
   
   /**
    * 
    * @return the current number of words stored in the index.
    */
   public static int getNumberOfWords(){
       return mainIndex.size();
   }

    /**
     * Removes all references to a document with the given id in the index.
     * Please note: This method is quite slow (linear runtime)
     * and should not be used regularly!
     *
     * @param id the id that shall be removed.
     */
    public static void removeDocumentReferencesByID(DocumentID id) {
        HashSet<WordID> keys = mainIndex.getKeys();
        //the current occurence set.
        OccurenceSet occSet;
        for (WordID key : keys) {
            occSet = mainIndex.get(key);
            //this should never be null but check to be sure
            if (occSet != null) {
                occSet.removeReferenceByID(id);
                if (occSet.isEmpty()) {
                    //if the set is empty, remove the word from the index.
                    mainIndex.remove(key);
                    WordIndex.removeWordByID(key);
                }
            } else {
                //if it really is null, throw an exception.
                RuntimeException ex = new RuntimeException("Word with id " + key + " was removed from index at runtime!");
                Logger.getLogger(MainIndex.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        }
    }

    /**
     * Resets main AND word AND document index!
     */
    public static void reset() {
        mainIndex.reset();
        //also reset word index.
        WordIndex.reset();
        // also order reset of document index.
        BiBiServDocument.reset();
    }
}

class SearchResultTransformation implements TransformationAction<SuffixTreeSearchResult, SearchResult> {

    private boolean spellCorrection;

    public SearchResultTransformation(boolean spellCorrection) {
        this.spellCorrection = spellCorrection;
    }

    @Override
    public SearchResult doTransformation(SuffixTreeSearchResult input) {
        if (spellCorrection) {
            return new RegularSearchResult(input);
        } else {
            return new WithoutCorrectionSearchResult(input);
        }
    }
}