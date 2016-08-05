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
 * Contributor(s): Benjamin Paassen, Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.search;

import de.unibi.cebitec.bibiserv.search.exceptions.InvalidWordException;
import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.helper.ExactMatcher;
import de.unibi.cebitec.bibiserv.search.helper.SearchQuery;
import de.unibi.cebitec.bibiserv.search.helper.SearchScore;
import de.unibi.cebitec.bibiserv.search.index.BiBiServDocument;
import de.unibi.cebitec.bibiserv.search.index.DocumentID;
import de.unibi.cebitec.bibiserv.search.index.MainIndex;
import de.unibi.cebitec.bibiserv.search.index.Occurence;
import de.unibi.cebitec.bibiserv.search.index.SuffixTreeAppendSession;
import de.unibi.cebitec.bibiserv.search.index.WordID;
import de.unibi.cebitec.bibiserv.search.index.WordIndex;
import de.unibi.cebitec.bibiserv.search.results.SearchResult;
import de.unibi.cebitec.bibiserv.search.util.WordPreProcessor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * This class is the main API class for outside access. For all basic needs this
 * class provides the right methods. For BiBiServ integration an extension of
 * this class (BiBiServSearch) is the right choice.
 * 
 * This class uses Apache Tika: http://tika.apache.org/
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class Search {

    /**
     * Logger for errors.
     */
    private static final Logger log = Logger.getLogger("de.unibi.cebitec.bibiserv.search.Search");
    /**
     * Singleton Search instance.
     */
    private static Search search;
    /**
     * The maximum number of characters that are read by tika in pdf documents.
     * -1 indicates that the size limit is deactivated.
     */
    private static final int writeLimit = -1;

    protected Search() {
    }

    public static Search getInstance() {
        if (search == null) {
            search = new Search();
        }
        return search;
    }

    /**
     * Reset index. Index will be empty after calling.
     *
     */
    public void reset() {
        MainIndex.reset();
    }

    /**
     * Return the number of words
     *
     * @return
     */
    public int getNumberOfWords() {
        return WordIndex.getIndexSize();
    }
    
    /**
     * 
     * @return the number of occurences of words currently stored.
     */
    public int getNumberOfOccurences(){
        return MainIndex.getNumberOfOccurences();
    }

    /**
     * Return the set of words stored in index
     *
     * @return
     */
    public List<String> getIndexKeys() {
        return WordIndex.getWords();
    }

    /**
     * Return a list all document identifiers
     *
     * @return
     */
    public List<String> getIdentList() {
        return BiBiServDocument.getIdentifiers();
    }

    /**
     * Add new documents (identified by an identifier). Content is read from
     * InputStreams using Apache Tika to extract text content from it.
     *
     * @param ident the unique identifiers of these documents (possibly URLs).
     * @param streams InputStreams pointing to the new documents. These streams
     * are closed after parsing is done.
     */
    public void addDocuments(String[] idents, InputStream[] streams) {
        SuffixTreeAppendSession session = new SuffixTreeAppendSession();
        try {
            //open session.
            session.open();
            //call session based method.
            addDocuments(idents, streams, session);
            //close the session again.
            session.close();
        } catch (SuffixTreeException ex) {
            handleException(ex);
        }
    }
    
     /**
     * Add new documents (identified by an identifier). Content is read from
     * InputStreams using Apache Tika to extract text content from it.
     *
     * @param ident the unique identifiers of these documents (possibly URLs).
     * @param streams InputStreams pointing to the new documents. These streams
     * are closed after parsing is done.
     */
    protected void addDocuments(String[] idents, InputStream[] streams,
            SuffixTreeAppendSession session) {
        //preprocess streams
        BufferedReader[] readers = new BufferedReader[streams.length];
        for (int i = 0; i < readers.length; i++) {
            try {
                readers[i] = new BufferedReader(parseDocument(streams[i]));
            } catch (IOException ex) {
                handleException(ex);
            }
        }
        //call reader method.
        addDocuments(idents, readers, session);
    }

    /**
     * Add new documents (identified by URL). Content is read from url by using
     * using Apache Tika to extract text content from the file it points to.
     *
     * @param url - Locators of documents
     *
     * @deprecated This is only used for test purposes now. Please use the other
     * methods for outside usage.
     */
    @Deprecated
    protected void addDocuments(URL[] urls) {
        String[] idents = new String[urls.length];
        InputStream[] streams = new InputStream[urls.length];
        //preprocess urls.
        for (int i = 0; i < urls.length; i++) {
            try {
                idents[i] = urls[i].toString();
                streams[i] = urls[i].openStream();
            } catch (IOException ex) {
                handleException(ex);
            }
        }
        //call stream method.
        addDocuments(idents, streams);
    }

    /**
     * Adds new documents to the index (identified by the given unique Strings).
     * Content is read from BufferedReaders.
     * The Readers are closed after read of whole document.
     *
     * @param idents - the unique identifiers of these documents (possibly URLs)
     * @param readers - BufferedReaders pointing on the content of the
     * documents.
     */
    public void addDocuments(String[] idents, BufferedReader[] readers) {
        if (idents.length != readers.length) {
            handleException(new RuntimeException("addDocuments was called with two arrays of different length."));
            return;
        }
        //get a session object.
        SuffixTreeAppendSession session = new SuffixTreeAppendSession();
        try {
            //start append session for the suffix tree.
            session.open();
            //call session method.
            addDocuments(idents, readers, session);
            //end append session for suffix tree
            session.close();
        } catch (SuffixTreeException ex) {
            handleException(ex);
        }
    }
    
     /**
     * Adds new documents to the index (identified by the given unique Strings).
     * Content is read from BufferedReaders.
     * The Readers are closed after read of whole document.
     *
     * @param idents - the unique identifiers of these documents (possibly URLs)
     * @param readers - BufferedReaders pointing on the content of the
     * documents.
     */
    protected void addDocuments(String[] idents, BufferedReader[] readers,
            SuffixTreeAppendSession session) {
        if (idents.length != readers.length) {
            handleException(new RuntimeException("addDocuments was called with two arrays of different length."));
            return;
        }
        //add all documents.
        for (int i = 0; i < idents.length; i++) {
            if (readers[i] != null) {
                addDocument(idents[i], readers[i], session);
            }
        }
    }

    /**
     * This method parses an input document using the tika AutoDetectParser.
     *
     * @param in an InputStream pointing to the new document.
     * @return a reader to pull String content from this document.
     */
    protected Reader parseDocument(InputStream in) throws IOException {
        Parser parser = new AutoDetectParser(); // Should auto-detect!
        ContentHandler handler = new BodyContentHandler(writeLimit);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        try {
            parser.parse(in, handler, metadata, context);
        } catch (IOException | SAXException | TikaException e) {
            handleException(e);
        } finally {
            in.close();
        }

        return new StringReader(handler.toString());
    }

    /**
     * Add new document (identified by the given String). Content is read from
     * BufferedReader. Reader is closed after read of whole document.
     *
     * @param ident - the unique identifier of this document (possibly an URL)
     * @param reader - BufferedRead pointing on the content of document.
     */
    protected void addDocument(String ident, BufferedReader reader,
            SuffixTreeAppendSession session) {
        //create internal document for this identifier.
        BiBiServDocument document = BiBiServDocument.createIndexedDocument(ident);
        try {
            String line;
            //temporary store for the last words occurence for indexing.
            Occurence lastWord = null;
            while ((line = reader.readLine()) != null) {
                //get all words.
                for (String word : WordPreProcessor.preProcessString(line)) {
                    //index word in WordIndex
                    WordID wordID = WordIndex.indexWord(word);
                    //append it to the suffix tree.
                    session.appendWordToTree(wordID);
                    //index the occurence.
                    lastWord = MainIndex.indexOccurence(wordID, lastWord, document);
                }
            }
        } catch (IOException | SuffixTreeException ex) {
            handleException(ex);
        }
    }

    /**
     * The suffix tree used for searching is not automatically rebuild after
     * a document is removed from the index. Therefore this method should be
     * called if many documents are removed. It completely rebuilds the suffix
     * tree for all words that are still in the index.
     */
    public void rebuildSuffixTree() {
        try {
            WordIndex.rebuildTree();
        } catch (SuffixTreeException ex) {
            handleException(ex);
        }
    }

    /**
     * Remove an document (by indentifier) from index.
     *
     * @param url - Locator/Identifier of document
     */
    public void removeDocument(String ident) {
        BiBiServDocument.removeDocumentByIdentifier(ident);
    }

    /**
     * Remove an document (identified by URL) from index.
     *
     * @param url - Locator/Identifier of document
     */
    public void removeDocument(URL url) {
        removeDocument(url.toString());
    }

    /**
     * Remove all document (identified by an identifier) matching given prefix.
     * Attention this maybe a very CPU intensive. Maybe it's better to rebuild
     * complete index instead.
     *
     * @param identprefix - ident prefix string of documents to be removed
     */
    public void removeAllDocument(String identprefix) {
        HashMap<String, DocumentID> identifierAndIDMap = BiBiServDocument.getIdentifierAndIDMap();
        Set<String> identifiers = identifierAndIDMap.keySet();
        for (String identifier : identifiers) {
            if (identifier.startsWith(identprefix)) {
                BiBiServDocument.removeDocumentByID(identifierAndIDMap.get(identifier));
            }
        }
    }

    /**
     * Actual search function. Returns a list of search results which
     * content matches the criteria of the given query string.
     * The result list is sorted by an internal scoring function.
     *
     * @param pattern - pattern to be searched for. The syntax for these queries
     * is as follows:
     *
     * 1.) "word1 word2 word3" = return all identifiers of documents which
     * contain the given words in that exact order.
     * 2.) !word1 word2 word3! = return all identifiers of documents which
     * contain words that have at least one of the given words as substring.
     * 3.) word1 word2 word3 = return all identifiers of documents which contain
     * words that have at least one of the given words or similar variations
     * of it as substring.
     *
     * In cases 2 and 3 a suffix tree based matching system is used and the
     * usage of regular expressions is allowed.
     *
     * @return list of search results.
     * @throws InvalidWordException is thrown if the user input is invalid.
     */
    public List<OutputSearchResult> search(String pattern)
            throws InvalidWordException {
        //list for search results.
        ArrayList<SearchResult> resultList = new ArrayList<>();
        //first parse the pattern.
        SearchQuery query = SearchQuery.parseQuery(pattern);
        boolean spellCorrection = true;
        switch (query.getType()) {
            case EXACT:
                //create a matcher
                ExactMatcher matcher = new ExactMatcher(query.getWords());
                //get the results.
                resultList.add(matcher.getMatches());
                break;
            case WITHOUTCORRECTION:
                /* 
                 * for the other possibilities just add the results directly
                 * to the result list.
                 */
                spellCorrection = false;
            case REGULAR:
                for (String word : query.getWords()) {
                    try {
                        resultList.addAll(MainIndex.searchSuperStrings(word, spellCorrection));
                    } catch (SuffixTreeException ex) {
                        /* 
                         * this is only thrown if the suffix tree is malformed,
                         * which is an internal error and should not be shown to
                         * the user.
                         */
                        handleException(ex);
                    }
                }
        }
        //calculate the sorted list of scored search results.
        return SearchScore.calculateScoredResults(resultList);

    }

    /**
     * General method to handle exceptions that are not shown to the user.
     *
     * @param ex the exception that has been thrown.
     */
    protected void handleException(Exception ex) {
        //just log the message.
        log.error(ex.getLocalizedMessage(), ex);
    }
}
