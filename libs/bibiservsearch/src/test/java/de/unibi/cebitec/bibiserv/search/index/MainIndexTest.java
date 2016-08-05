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

import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.util.Collections;
import de.unibi.cebitec.bibiserv.search.util.MultiMap;
import de.unibi.cebitec.bibiserv.search.util.TransformationAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class MainIndexTest {

    public MainIndexTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testIndexing() throws SuffixTreeException {
        BiBiServDocument.reset();
        MainIndex.reset();
        //initialize this test.
        MultiMap<String, String> wordsAndDocs = new MultiMap<>();
        wordsAndDocs.put("word2", "bibiserv/test1.html");
        wordsAndDocs.put("word3", "bibiserv/test2.html");
        wordsAndDocs.put("word4", "bibiserv/test1.html");
        wordsAndDocs.put("word4", "bibiserv/test2.html");
        //add to index.
        HashMap<String, BiBiServDocument> processedURLs = new HashMap<>();
        int expectedSize = 0;
        for (String word : wordsAndDocs.keySet()) {
            WordID wordID = WordIndex.indexWord(word);
            for (String url : wordsAndDocs.get(word)) {
                BiBiServDocument doc = processedURLs.get(url);
                if (doc == null) {
                    doc = BiBiServDocument.createIndexedDocument(url);
                    processedURLs.put(url, doc);
                }
                MainIndex.indexOccurence(wordID, doc);
            }
            expectedSize++;
            assertTrue(MainIndex.getNumberOfWords() == expectedSize);
        }
        //trigger search.
        assertTrue(MainIndex.searchExactly("word1").getOccurences().isEmpty());
        for (String word : wordsAndDocs.keySet()) {
            ArrayList<String> actualIdents = Collections.transform(
                    MainIndex.searchExactly(word).getOccurences(), new GetDocIdentifierAction());
            //check size of result.
            HashSet<String> expectedIdents = wordsAndDocs.get(word);

            assertTrue(actualIdents.size() == expectedIdents.size());
            for (String identifier : expectedIdents) {
                assertTrue(actualIdents.contains(identifier));
            }
        }
        //check reset functionality.
        MainIndex.reset();
        assertTrue(MainIndex.getNumberOfWords() == 0);
    }
}

class GetDocIdentifierAction implements TransformationAction<Occurence, String> {

    @Override
    public String doTransformation(Occurence input) {
        DocumentID id = input.getId();
        BiBiServDocument document = BiBiServDocument.getDocumentByID(id);
        return document.getIdentifier();
    }
}