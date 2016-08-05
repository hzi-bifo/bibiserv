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

import de.unibi.cebitec.bibiserv.search.util.Index;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a wrapper class for sites/documents on BiBiServ2.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class BiBiServDocument {

    /**
     * Index for documents.
     */
    private static final Index<DocumentID, BiBiServDocument> docIndex = new Index<>();
    /**
     * The url of this document.
     */
    private final String identifier;
    /**
     * This documents internal id.
     */
    private final DocumentID id;

    /**
     * Constructs a new document.
     *
     * @param identifier the url of this document.
     */
    private BiBiServDocument(String identifier) {
        this.identifier = identifier;
        this.id = new DocumentID();
    }

    /**
     *
     * @return the url of this document.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @return This documents internal id.
     */
    public DocumentID getId() {
        return id;
    }

    /**
     * Constructs a new document.
     *
     * @param identifier the url of this document.
     * @return the newly created document.
     */
    public static BiBiServDocument createIndexedDocument(String identifier) {
        BiBiServDocument newDocument = new BiBiServDocument(identifier);
        addDocument(newDocument);
        return newDocument;
    }

    /**
     * retrieves an indexed document by its id.
     *
     * @param id the documents id.
     * @return the document if it has been indexed or null if no document with
     * the
     * given id exists.
     */
    public static BiBiServDocument getDocumentByID(DocumentID id) {
        return docIndex.get(id);
    }

    public static void addDocument(BiBiServDocument document) {
        DocumentID id = document.getId();
        docIndex.put(id, document);
    }

    /**
     * Removes the first document with the given identifier from the index and
     * also removes all references to the document from the main index.
     *
     * Please note: This method is quite slow (linear runtime)
     * and should not be used regularly!
     *
     * @param identifier a URL that might have been indexed.
     * @return true if a document was found.
     */
    public static boolean removeDocumentByIdentifier(String identifier) {
        //id of the object that will be deleted.
        DocumentID deletionID = null;
        ArrayList<BiBiServDocument> documents = new ArrayList<>();
        // get all documents.
        docIndex.addAllValues(documents);
        // find the given identifier.
        for (BiBiServDocument document : documents) {
            if (document.getIdentifier().equals(identifier)) {
                deletionID = document.getId();
                break;
            }
        }
        // if we found one, delete it.
        if (deletionID != null) {
            if (!removeDocumentByID(deletionID)) {
                //if we could not delete the document with the given id, throw an exception.
                RuntimeException ex = new RuntimeException("Document with id " + deletionID.hashCode() + " was removed from index at runtime!");
                Logger.getLogger(BiBiServDocument.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            } else {
                return true;
            }
        }
        //if we did not find an id, return false
        return false;
    }

    /**
     * Removes a document from the index by its id and also
     * removes all references to the document from the main index.
     *
     * @param id the id of a given document that shall be removed.
     * @return true if a document was found.
     */
    public static boolean removeDocumentByID(DocumentID id) {
        if (docIndex.remove(id) != null) {
            MainIndex.removeDocumentReferencesByID(id);
            return true;
        }
        return false;
    }

    /**
     * @return all URLS currently indexed.
     */
    public static ArrayList<String> getIdentifiers() {
        HashSet<BiBiServDocument> documents = docIndex.getValues();
        ArrayList<String> urls = new ArrayList<>();
        for (BiBiServDocument document : documents) {
            urls.add(document.getIdentifier());
        }
        return urls;
    }

    /**
     * Returns a HashMap containing the document identifiers as keys and the
     * document ids as values.
     *
     * @return a HashMap containing the document identifiers as keys and the
     * document ids as values.
     */
    public static HashMap<String, DocumentID> getIdentifierAndIDMap() {
        HashMap<String, DocumentID> outputHash = new HashMap<>();
        for (Entry<DocumentID, BiBiServDocument> entry : docIndex.getEntries()) {
            outputHash.put(entry.getValue().getIdentifier(), entry.getKey());
        }
        return outputHash;
    }

    /**
     *
     * @return number of documents currently indexed.
     */
    public static int getIndexSize() {
        return docIndex.size();
    }

    /**
     * Resets document index.
     */
    public static void reset() {
        docIndex.reset();
        //also order reset of id generation.
        DocumentID.resetIDGeneration();
    }
}
