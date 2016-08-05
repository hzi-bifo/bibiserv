/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.util.bibtexparser;

/**
 *
 * @author tgatter
 */
public enum BibTexType {
    
    article("article"),
    book("book"),
    inproceedings("inproceedings"),
    manual("manual"),
    mastersthesis("mastersthesis"),
    phdthesis("phdthesis"),
    proceedings("proceedings"),
    techreport("techreport");
    
    private String name; 
    
    private BibTexType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static BibTexType getType(String name) { 
        name = name.toLowerCase();
        for(BibTexType type: BibTexType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return article;
    }
    
}
