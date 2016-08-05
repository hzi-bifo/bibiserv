/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 BiBiServ Curator Team"
 *
 * Contributor(s): Benjamin Paassen
 *
 */
package de.unibi.cebitec.bibiserv.search;

import de.unibi.cebitec.bibiserv.search.exceptions.InvalidWordException;
import de.unibi.cebitec.bibiserv.search.exceptions.SuffixTreeException;
import de.unibi.cebitec.bibiserv.search.index.MainIndex;
import de.unibi.cebitec.bibiserv.search.index.Occurence;
import de.unibi.cebitec.bibiserv.search.index.SuffixTreeAppendSession;
import de.unibi.cebitec.bibiserv.search.index.WordID;
import de.unibi.cebitec.bibiserv.search.index.WordIndex;
import de.unibi.cebitec.bibiserv.search.util.Tuple;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * PLEASE NOTE! THIS CLASS IS ONLY FOR TESTING PURPOSES AND BY NO MEANS PART
 * OF THE APPLICATION!
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class ProfilingTestClass {

    /**
     * TEST BEHAVIOUR VARIABLES! PLEASE CHANGE TO MANIPULATE TEST BEHAVIOUR!
     */
    private static final TestMode testMode = TestMode.RAM;
    private static final ProfilingMode mode = ProfilingMode.MIXED;
    /**
     * number of word occurences that shall be indexed for a RAM test.
     * This variable will be ignored in all other tests.
     */
    private static final int RAMTESTOCCLIMIT = 360000;
    /**
     * This variable has to be a number that is greater or equal than the text
     * length (in words) contained in the mixed folder.
     */
    private static final int ALLFILES = 369325;
    /**
     * TEST CONTENT VARIABLES! PLEASE DO NOT CHANGE ANYTHING BELOW HERE!
     */
    private Search search;
    private static File[] files;
    private int fileIndex = 0;
    private static final String[] exactQueriesQithDifferentWordLength = {"\"mit\"",//3
        "\"immer\"",//5
        "\"graphen\"",//7
        "\"teilmenge\"",//9
        "\"polynomiell\""//11
    };
    private static final String[] exactQueriesWithDifferentLength = {"\"einzurichtenden\"",//1
        "\"Bohemian nobleman\"",//2
        "\"friend and colleague\"",//3
        "\"Er erinnert sich dunkel\"",//4
        "\"betrieblichen Erträge der fortgeführten Aktivitäten\"",//5
        "\"Die insgesamt eingetretene Verschlechterung des Finanzergebnisses\"",//6
        "\"die Sachanlagen des Stahlwerks in Brasilien betrafen\"",//7
        "\"Remarkable as being the scene of the death\""};//8
    private static final String[] partWordQueriesWithDifferentWordLength = {"!wer!",//3
        "!wertp!",//5
        "!hwertpr!",//7
        "!chwertpro!",//9
        "!chwertprobl!"//11
    };
    private static final String[] partWordQueriesWithDifferentLength = {"!chwertprobl!",//1
        "!ositione akaienschu!",//2
        "!ositione akaienschu urzerha!",//3
        "!chwertprobl ositione akaienschu urzerha!",//4
        "!chwertprobl ositione akaienschu urzerha amiltonk!"//5
    };
    private static final String[] inexactQueriesWithDifferentWordLength = {"wre",//3
        "werp",//4
        "werpt",//5
        "whertpr",//7
        "chwretpro",//9
        "chwerptrobl"//11
    };
    private static final String[] inexactQueriesWithDifferentLength = {"chwerptrobl",//1
        "osiitone akzienschu",//2
        "osiitone akzienschu urzrha",//3
        "chwerptrobl osiatione akzienschu urzrha",//4
        "chwerptrobl osiatione akzienschu urzrha amiltonk"//5
    };
    private static final String regexQuery = "![abc][^z]*[abc]!";

    private ProfilingTestClass() {
        if (files == null) {
            File testdir = new File("test/data" + mode.getPathSuffix());
            files = testdir.listFiles();
        }
        // get instance of search class
        search = Search.getInstance();
    }

    /**
     * Adds files to the index until a given limit of occurences is reached.
     *
     * @param occurenceLimit limit of occurences.
     * @return false if all files were indexed and the limit still could not be
     * reached.
     */
    private boolean addFilesUntilLimit(final int occurenceLimit) throws SuffixTreeException {
        SuffixTreeAppendSession session = new SuffixTreeAppendSession();
        session.open();
        int numberOfOccs = MainIndex.getNumberOfOccurences();
        while (numberOfOccs < occurenceLimit
                && fileIndex < files.length) {
            try {
                BufferedReader reader = new BufferedReader(
                        search.parseDocument(
                        new FileInputStream(files[fileIndex])));
                String ident = files[fileIndex].getName();
                search.addDocument(ident, reader, session);
                numberOfOccs = MainIndex.getNumberOfOccurences();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            fileIndex++;
        }
        session.close();
        System.out.println("files;" + fileIndex
                + ";occurences;" + numberOfOccs
                + ";words;" + WordIndex.getIndexSize());
        return fileIndex < files.length;
    }

    private void testExactQueryWordLength1() {
        findAndPrintResult(exactQueriesQithDifferentWordLength[0]);
    }

    private void testExactQueryWordLength2() {
        findAndPrintResult(exactQueriesQithDifferentWordLength[1]);
    }

    private void testExactQueryWordLength3() {
        findAndPrintResult(exactQueriesQithDifferentWordLength[2]);
    }

    private void testExactQueryWordLength4() {
        findAndPrintResult(exactQueriesQithDifferentWordLength[3]);
    }

    private void testExactQueryWordLength5() {
        findAndPrintResult(exactQueriesQithDifferentWordLength[4]);
    }

    private void testExactQueryLength1() {
        findAndPrintResult(exactQueriesWithDifferentLength[0]);
    }

    private void testExactQueryLength2() {
        findAndPrintResult(exactQueriesWithDifferentLength[1]);
    }

    private void testExactQueryLength3() {
        findAndPrintResult(exactQueriesWithDifferentLength[2]);
    }

    private void testExactQueryLength4() {
        findAndPrintResult(exactQueriesWithDifferentLength[3]);
    }

    private void testExactQueryLength5() {
        findAndPrintResult(exactQueriesWithDifferentLength[4]);
    }

    private void testExactQueryLength6() {
        findAndPrintResult(exactQueriesWithDifferentLength[5]);
    }

    private void testExactQueryLength7() {
        findAndPrintResult(exactQueriesWithDifferentLength[6]);
    }

    private void testExactQueryLength8() {
        findAndPrintResult(exactQueriesWithDifferentLength[7]);
    }

    private void testPartWordQueryWordLength1() {
        findAndPrintResult(partWordQueriesWithDifferentWordLength[0]);
    }

    private void testPartWordQueryWordLength2() {
        findAndPrintResult(partWordQueriesWithDifferentWordLength[1]);
    }

    private void testPartWordQueryWordLength3() {
        findAndPrintResult(partWordQueriesWithDifferentWordLength[2]);
    }

    private void testPartWordQueryWordLength4() {
        findAndPrintResult(partWordQueriesWithDifferentWordLength[3]);
    }

    private void testPartWordQueryWordLength5() {
        findAndPrintResult(partWordQueriesWithDifferentWordLength[4]);
    }

    private void testPartWordQueryLength1() {
        findAndPrintResult(partWordQueriesWithDifferentLength[0]);
    }

    private void testPartWordQueryLength2() {
        findAndPrintResult(partWordQueriesWithDifferentLength[1]);
    }

    private void testPartWordQueryLength3() {
        findAndPrintResult(partWordQueriesWithDifferentLength[2]);
    }

    private void testPartWordQueryLength4() {
        findAndPrintResult(partWordQueriesWithDifferentLength[3]);
    }

    private void testPartWordQueryLength5() {
        findAndPrintResult(partWordQueriesWithDifferentLength[4]);
    }

    private void testInexactQueryWordLength1() {
        findAndPrintResult(inexactQueriesWithDifferentWordLength[0]);
    }

    private void testInexactQueryWordLength2() {
        findAndPrintResult(inexactQueriesWithDifferentWordLength[1]);
    }

    private void testInexactQueryWordLength3() {
        findAndPrintResult(inexactQueriesWithDifferentWordLength[2]);
    }

    private void testInexactQueryWordLength4() {
        findAndPrintResult(inexactQueriesWithDifferentWordLength[3]);
    }

    private void testInexactQueryWordLength5() {
        findAndPrintResult(inexactQueriesWithDifferentWordLength[4]);
    }

    private void testInexactQueryLength1() {
        findAndPrintResult(inexactQueriesWithDifferentLength[0]);
    }

    private void testInexactQueryLength2() {
        findAndPrintResult(inexactQueriesWithDifferentLength[1]);
    }

    private void testInexactQueryLength3() {
        findAndPrintResult(inexactQueriesWithDifferentLength[2]);
    }

    private void testInexactQueryLength4() {
        findAndPrintResult(inexactQueriesWithDifferentLength[3]);
    }

    private void testInexactQueryLength5() {
        findAndPrintResult(inexactQueriesWithDifferentLength[4]);
    }

    private void testRegexQuery() {
        findAndPrintResult(regexQuery);
    }

    private void testremoveURL() {
        for (int i = 0; i < fileIndex; i++) {
            search.removeDocument(files[i].getName());
        }
    }

    private void findAndPrintResult(String pattern) {
        List<OutputSearchResult> results = new ArrayList<>();
        try {
            results = search.search(pattern);
        } catch (InvalidWordException ex) {
            throw new RuntimeException(ex);
        }
//        System.out.println("Found " + pattern + " in " + results.size() + "files:");
//        for (OutputSearchResult result : results) {
//            System.out.println(result.getIdent());
//        }
    }

    private static <X> void incMap(HashMap<X, Tuple<Integer, Double>> map, X key, Double value) {
        Tuple<Integer, Double> tuple = map.get(key);
        if (tuple == null) {
            tuple = new Tuple<>(1, value);
        } else {
            tuple = new Tuple<>(tuple.getFirst() + 1, tuple.getSecond() + value);
        }
        map.put(key, tuple);
    }

    private void cputTestTemplate(int limit) throws SuffixTreeException {
        addFilesUntilLimit(limit);
        /**
         * functions without notable length.
         */
        testExactQueryLength1();
        testExactQueryLength2();
        testExactQueryLength3();
        testExactQueryLength4();
        testExactQueryLength5();
        testExactQueryLength6();
        testExactQueryLength7();
        testExactQueryLength8();
        testExactQueryWordLength1();
        testExactQueryWordLength2();
        testExactQueryWordLength3();
        testExactQueryWordLength4();
        testExactQueryWordLength5();
        testPartWordQueryLength1();
        testPartWordQueryLength2();
        testPartWordQueryLength3();
        testPartWordQueryLength4();
        testPartWordQueryLength5();
        testPartWordQueryWordLength1();
        testPartWordQueryWordLength2();
        testPartWordQueryWordLength3();
        testPartWordQueryWordLength4();
        testPartWordQueryWordLength5();
        testInexactQueryLength1();
        testInexactQueryLength2();
        testInexactQueryLength3();
        testInexactQueryLength4();
        testInexactQueryLength5();
        testInexactQueryWordLength3();
        testInexactQueryWordLength4();
        testInexactQueryWordLength5();
        /**
         * interesting functions.
         */
        testInexactQueryWordLength1();
        testInexactQueryWordLength2();
        testRegexQuery();
        testremoveURL();
        fileIndex = 0;
    }

    private void cpuTest20000() throws SuffixTreeException {
        cputTestTemplate(20000);
    }

    private void cpuTest40000() throws SuffixTreeException {
        cputTestTemplate(40000);
    }

    private void cpuTest60000() throws SuffixTreeException {
        cputTestTemplate(60000);
    }

    private void cpuTest80000() throws SuffixTreeException {
        cputTestTemplate(80000);
    }

    private void cpuTest100000() throws SuffixTreeException {
        cputTestTemplate(100000);
    }

    private void cpuTest120000() throws SuffixTreeException {
        cputTestTemplate(120000);
    }

    private void cpuTest140000() throws SuffixTreeException {
        cputTestTemplate(140000);
    }

    private void cpuTest160000() throws SuffixTreeException {
        cputTestTemplate(160000);
    }

    private void cpuTest180000() throws SuffixTreeException {
        cputTestTemplate(180000);
    }

    private void cpuTest200000() throws SuffixTreeException {
        cputTestTemplate(200000);
    }

    private void cpuTest220000() throws SuffixTreeException {
        cputTestTemplate(220000);
    }

    private void cpuTest240000() throws SuffixTreeException {
        cputTestTemplate(240000);
    }

    private void cpuTest260000() throws SuffixTreeException {
        cputTestTemplate(260000);
    }

    private void cpuTest280000() throws SuffixTreeException {
        cputTestTemplate(280000);
    }

    private void cpuTest300000() throws SuffixTreeException {
        cputTestTemplate(300000);
    }

    private void cpuTest320000() throws SuffixTreeException {
        cputTestTemplate(320000);
    }

    private void cpuTest340000() throws SuffixTreeException {
        cputTestTemplate(340000);
    }

    private void cpuTest360000() throws SuffixTreeException {
        cputTestTemplate(360000);
    }

    private void cpuTest380000() throws SuffixTreeException {
        cputTestTemplate(380000);
    }

    private void cpuTest() throws SuffixTreeException {
        cpuTest20000();
        cpuTest40000();
        cpuTest60000();
        cpuTest80000();
        cpuTest100000();
        cpuTest120000();
        cpuTest140000();
        cpuTest160000();
        cpuTest180000();
        cpuTest200000();
        cpuTest220000();
        cpuTest240000();
        cpuTest260000();
        cpuTest280000();
        cpuTest300000();
        cpuTest320000();
        cpuTest340000();
        cpuTest360000();
        cpuTest380000();
    }

    private void RAMTest() throws SuffixTreeException {
        addFilesUntilLimit(RAMTESTOCCLIMIT);
        System.gc();
        try {
            //you should take a profiling snapshot after the sleep.
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            //ignore
        }
    }

    private void uniquenessTest() throws SuffixTreeException {
        addFilesUntilLimit(ALLFILES);
        /*
         * idee: für alle möglichen 10-wort-langen queries schauen, wie gross
         * der anteil der übrigbleibenden vorkommen ist, d.h. wie stark eine
         * grössere Wortlänge im schnitt die anzahl der möglichkeiten eingrenzt.
         */
        final int queryLength = 10;
        HashMap<Integer, Tuple<Integer, Double>> lengthToMultiplicityMap = new HashMap<>();
        Collection<WordID> iDs = WordIndex.getIDs();
        for (WordID currentStartID : iDs) {
            Collection<Occurence> startOccs = MainIndex.getOccurencesForWordID(currentStartID);
            Stack<OccCheckingState> stack = new Stack<>();
            stack.push(new OccCheckingState(0, startOccs));
            while (!stack.isEmpty()) {
                OccCheckingState currentState = stack.pop();
                HashMap<WordID, ArrayList<Occurence>> nextWords = new HashMap<>();
                for (Occurence occ : currentState.occs) {
                    Occurence next = occ.getNext();
                    if (next != null) {
                        ArrayList<Occurence> currentList = nextWords.get(next.getWordID());
                        if (currentList == null) {
                            currentList = new ArrayList<>();
                            currentList.add(next);
                            nextWords.put(next.getWordID(), currentList);
                        } else {
                            currentList.add(next);
                        }
                    }
                }
                for (ArrayList<Occurence> currentList : nextWords.values()) {
                    double relation = (double) currentList.size()
                            / (double) currentState.occs.size();
                    incMap(lengthToMultiplicityMap, currentState.depth + 1, relation);
                    if (currentState.depth + 1 < queryLength) {
                        stack.push(new OccCheckingState(
                                currentState.depth + 1, currentList));
                    }
                }
            }
        }
        double averageRemainingSize = 1;
        ArrayList<Integer> lengths = new ArrayList<>(lengthToMultiplicityMap.keySet());
        Collections.sort(lengths);
        for (Integer length : lengths) {
            Tuple<Integer, Double> multAndRelationTuple = lengthToMultiplicityMap.get(length);
            averageRemainingSize *=
                    multAndRelationTuple.getSecond()
                    / multAndRelationTuple.getFirst().doubleValue();
            double averageRelation = multAndRelationTuple.getSecond()
                    / multAndRelationTuple.getFirst().doubleValue();
            System.out.println(length.toString()
                    + " : " + averageRemainingSize
                    + " : " + multAndRelationTuple.getFirst()
                    + " : " + averageRelation);

        }
    }

    private void frequentnessAnalysis() throws SuffixTreeException {
        addFilesUntilLimit(ALLFILES);
        HashMap<Integer, Tuple<Integer, Integer>> lengthToMultiplicityMap = new HashMap<>();
        Collection<WordID> iDs = WordIndex.getIDs();
        Iterator<WordID> iterator = iDs.iterator();
        while (iterator.hasNext()) {
            WordID currentID = iterator.next();
            int length = currentID.getWordReference().getContent().length();
            int occs = MainIndex.getOccurencesForWordID(currentID).
                    size();
            Tuple<Integer, Integer> multAndOccsTuple = lengthToMultiplicityMap.get(length);
            if (multAndOccsTuple != null) {
                multAndOccsTuple = new Tuple<>(multAndOccsTuple.getFirst() + 1, multAndOccsTuple.getSecond() + occs);
            } else {
                multAndOccsTuple = new Tuple<>(1, occs);
            }
            lengthToMultiplicityMap.put(length, multAndOccsTuple);
        }
        ArrayList<Integer> lengths = new ArrayList<>(lengthToMultiplicityMap.keySet());
        Collections.sort(lengths);
        for (Integer length : lengths) {
            Tuple<Integer, Integer> multAndOccsTuple = lengthToMultiplicityMap.get(length);
            double averageOccurences = multAndOccsTuple.getSecond().doubleValue() / multAndOccsTuple.getFirst().doubleValue();
            System.out.println(length.toString() + " : " + averageOccurences);
        }
    }

    public static void main(String[] args) {
        try {
            ProfilingTestClass test = new ProfilingTestClass();
            switch (testMode) {
                case CPU:
                    test.cpuTest();
                    break;
                case RAM:
                    test.RAMTest();
                    break;
                case FREQ:
                    test.frequentnessAnalysis();
                    break;
                case UNIQUE:
                    test.uniquenessTest();
                    break;
            }
        } catch (SuffixTreeException ex) {
            ex.printStackTrace();
        }
    }
}

enum ProfilingMode {

    PDF("/pdf"), XML("/xml"), MIXED("/mixed");
    private final String pathSuffix;

    private ProfilingMode(String pathSuffix) {
        this.pathSuffix = pathSuffix;
    }

    public String getPathSuffix() {
        return pathSuffix;
    }
}

class OccCheckingState {

    final int depth;
    final Collection<Occurence> occs;

    public OccCheckingState(int depth, Collection<Occurence> occs) {
        this.depth = depth;
        this.occs = occs;
    }
}

enum TestMode {

    CPU, RAM, FREQ, UNIQUE;
}