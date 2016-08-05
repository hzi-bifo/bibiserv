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
import de.unibi.cebitec.bibiserv.search.index.BiBiServDocument;
import de.unibi.cebitec.bibiserv.search.util.Collections;
import de.unibi.cebitec.bibiserv.search.util.TransformationAction;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * JUnit Test class for class BiBiServSearch
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class BiBiServSearchTest {

    private BiBiServSearch search;
    private static final File testAppDir = new File("test/data");

    public BiBiServSearchTest() {

        search = BiBiServSearch.getInstance();
        search.reset();

        // documents
        item = XML2Document(new File("test/data/item.xml"));
        linkeditem = XML2Document(new File("test/data/linkeditem.xml"));
        category = XML2Document(new File("test/data/structure.xml"));
        runnableitem = XML2Document(new File("test/data/runnableitem.xml"));

    }
    private Document item;
    private Document linkeditem;
    private Document category;
    private Document runnableitem;

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

    /**
     * Test of addItem method, of class BiBiServSearch.
     *
     * @throws de.unibi.cebitec.bibiserv.search.exceptions.InvalidWordException
     */
    @Test
    public void testAddItem() throws InvalidWordException {

        System.out.println("addItem");
        
        addSingleDocument(item);
        System.out.println("add linkedItem (Index : " + search.getNumberOfWords() + ")");
       
        addSingleDocument(linkeditem);
        System.out.println("add category (Index : " + search.getNumberOfWords() + ")");
        addSingleDocument(category);
        System.out.println("add runnableitem (Index : " + search.getNumberOfWords() + ")");
        addSingleDocument(runnableitem);

        System.out.println("Index contains " + search.getNumberOfWords() + " words !");

        System.out.print("Enfolded identifiers :");
        for (String id : search.getIdentList()) {
            System.out.print(id + " ");
        }
        System.out.println("!");
        // ONLYFORTESTEXECUTABLE should be found within dialign?viewType=submission
        List<String> results = getIdentResults("!ONLYFORTESTEXECUTABLE!");

        // result should only contain one URL
        assertEquals(1, results.size());
        // and this should be submission viewtype from dialign
        assertTrue(results.contains("/dialign?viewType=submission"));

        // ONLYFORTESTEXECUTABLE should be found within dialign?viewType=submission
        results = getIdentResults("!ONLYFORTESTMANUAL!");

        // result should only contain one URL
        assertEquals(1, results.size());
        // and this should be manual viewtype from dialign
        assertTrue(results.contains("/dialign?viewType=manual"));

        // "genome comparison" should be found in structure.xml.
        results = getIdentResults("\"genome comparison\"");

        // result should only contain one URL
        assertEquals(1, results.size());
        // and this should be the genome comparison category.
        assertTrue(results.contains("/genomeComparison"));
    }

    /**
     * Test of removeItem method, of class BiBiServSearch.
     */
    @Test
    public void testRemoveItem() {
        System.out.println("removeItem");
        search.removeItem(item);
        search.removeItem(linkeditem);
        search.removeItem(runnableitem);
        search.removeItem(category);
        assertTrue(search.getNumberOfWords() == 0);
    }

    private void addSingleDocument(Document doc) {
        search.addItem(doc, testAppDir);
    }

    private List<String> getIdentResults(String pattern) throws InvalidWordException {
        return Collections.transform(
                search.search(pattern),
                new ExtractStringFromSearchResultTransformation());
    }

    private Document XML2Document(File f) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);
            return doc;
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(BiBiServSearchTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String Document2String(Document doc) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }

    }
}

class ExtractStringFromSearchResultTransformation
        implements TransformationAction<OutputSearchResult, String> {

    @Override
    public String doTransformation(OutputSearchResult input) {
        return input.getIdent();
    }

}
