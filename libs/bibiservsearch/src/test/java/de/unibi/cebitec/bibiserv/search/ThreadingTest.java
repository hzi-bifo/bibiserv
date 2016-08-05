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
package de.unibi.cebitec.bibiserv.search;

import de.unibi.cebitec.bibiserv.search.exceptions.InvalidWordException;
import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.index.BiBiServDocument;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This tests if multithreaded usage of BiBiServSearch is possible.
 * 
 * PLEASE NOTE! IF THIS TEST FAILS IT MIGHT BE DUE TO THE FACT THAT THIS TEST
 * ITSELF IS NOT PROGRAMMED ENTIRELY THREAD-SAFE! Therefore please try to restart
 * the test before you panic.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class ThreadingTest {

    private static final String[] idents = new String[4];
    private static final BufferedReader[] readers = new BufferedReader[4];
    private static ReentrantLock firstThreadLock = new ReentrantLock();
    private static boolean firstThreadFinished = false;
    private static ReentrantLock secondThreadLock = new ReentrantLock();
    private static boolean secondThreadFinished = false;
    private static final Search searchInstance = Search.getInstance();
    private static Thread insertThread1;
    private static Thread insertThread2;

    public ThreadingTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        /*BiBiServDocument.reset();
        WordIndex.reset(); */
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws FileNotFoundException, SuffixTreeException {
        searchInstance.reset();
        
        //get all test files.
        File testFilesDir = new File("test/data/threadingTest");
        assertTrue(testFilesDir.exists());
        assertTrue(testFilesDir.isDirectory());
        File[] listFiles = testFilesDir.listFiles();
        //get readers and idents.
        assertTrue(listFiles.length == 4);
        for (int i = 0; i < listFiles.length; i++) {
            readers[i] = new BufferedReader(new FileReader(listFiles[i]));
            idents[i] = listFiles[i].getName();
        }
        //insert some test words to start.
        searchInstance.addDocuments(Arrays.copyOfRange(idents, 0, 1), Arrays.copyOfRange(readers, 0, 1));
        //create insert threads.
        insertThread1 = new Thread() {
            @Override
            public void run() {
                //insert documents.
                searchInstance.addDocuments(Arrays.copyOfRange(idents, 1, 3), Arrays.copyOfRange(readers, 1, 3));
                //signal that you're finished.
                firstThreadLock.lock();
                try {
                    firstThreadFinished = true;
                } finally {
                    firstThreadLock.unlock();
                }
            }
        };
        insertThread2 = new Thread() {
            @Override
            public void run() {
                //insert documents.
                searchInstance.addDocuments(Arrays.copyOfRange(idents, 3, 4), Arrays.copyOfRange(readers, 3, 4));
                /* 
                 * assert that first thread is finished first even though 
                 * this one would have been faster if it would have been used
                 * parallely => synch mechanisms work.
                 */
                firstThreadLock.lock();
                try {
                    assertTrue(firstThreadFinished);
                } finally {
                    firstThreadLock.unlock();
                }
                secondThreadLock.lock();
                try {
                    secondThreadFinished = true;
                } finally {
                    secondThreadLock.unlock();
                }
            }
        };
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testThreading() throws IOException, InterruptedException, InvalidWordException {
        //start both insert threads.
        insertThread1.start();
        Thread.sleep(50);
        insertThread2.start();
        //initialize variables
        int searchCount = 0;
        int searchResults[] = new int[3];
        int currentSearchResults;
        //when both are started start a loop and do search requests.
        while (true) {
            //test if the second thread has finished its job.
            secondThreadLock.lock();
            try {
                if (secondThreadFinished) {
                    break;
                }
            } finally {
                secondThreadLock.unlock();
            }
            /*
             * if the second thread is not done yet, inc the search counter and
             * do a search.
             */
            currentSearchResults = searchInstance.search("!aabc!").size();
            searchCount++;
            firstThreadLock.lock();
            try {
                if (!firstThreadFinished && searchResults[0] == 0) {
                    /* 
                     * if we havent stored the number of search results before the
                     * first thread is finished yet, store it.
                     */
                    assertTrue(BiBiServDocument.getIndexSize() == 1);
                    searchResults[0] = currentSearchResults;
                }
                if (firstThreadFinished && searchResults[1] == 0) {
                    /* 
                     * if we havent stored the number of search results after the
                     * first thread is finished yet, store it.
                     */
                    assertTrue(BiBiServDocument.getIndexSize() == 3);
                    searchResults[1] = currentSearchResults;
                }
            } finally {
                firstThreadLock.unlock();
            }
        }
        //store the number of search results after the second insertion is done.
        assertTrue(BiBiServDocument.getIndexSize() == 4);
        searchResults[2] = searchInstance.search("!aabc!").size();
        
        
        insertThread1.join();
        insertThread2.join();
        
        
        /*
         * if the second thread is finished we get here. By now we should have
         * been able to process a tremendous amount of search requests because
         * search requests and insertion is done parallely.
         */
        assertTrue(searchCount > 1000);
        //also check if the number of search results was right for each step.
        assertTrue(1 == searchResults[0]);
        assertTrue(3 == searchResults[1]);
        assertTrue(4 == searchResults[2]);
    }
}