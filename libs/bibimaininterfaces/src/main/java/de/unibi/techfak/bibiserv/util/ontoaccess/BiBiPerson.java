package de.unibi.techfak.bibiserv.util.ontoaccess;

/**
 * Interface for the acces to a person object for the ontolgy, 
 * currently only giving minimal information for referencing
 * 
 * @author shartmei
 */
public interface BiBiPerson {

    String getFamily_name();
    void setFamily_name(String family_name);

    String getGivenname();
    void setGivenname(String givenname);   
    
    /**
     * returns the combination of given and family names,
     * or the specified name string, if set by 'setName(name)'
     * @return full name
     */
    String getName();
    
    /**
     * Set the full name String for this person. Set to 'null' to reset
     * specific name and get auto-generated name with 'getName()
     * @param name 
     */
    void setName(String name);
}
