/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.bibtexparser;

/**
 *
 * @author tgatter
 */
public enum BibTexEntries {
    
    author("author"),
    title("title"),
    year("year"),
    journal("journal"),
    school("school"),
    institution("institution"),
    publisher("publisher"),
    note("note"),
    doi("doi"),
    url("url");
    
    private String name; 
    
    private BibTexEntries(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
}
