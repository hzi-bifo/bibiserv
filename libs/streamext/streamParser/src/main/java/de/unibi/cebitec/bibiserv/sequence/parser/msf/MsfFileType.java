
package de.unibi.cebitec.bibiserv.sequence.parser.msf;

/**
 *
 * @author gatter
 */
public enum MsfFileType {
 
    Plain("MSF:"),
    PileUp("PileUp"),
    RichNA("!!NA_MULTIPLE_ALIGNMENT"),
    RichAA("!!AA_MULTIPLE_ALIGNMENT"),
    Unkown("");
    
    private String token;
    
    private MsfFileType(String token) {
        this.token = token;
    }
    
    static MsfFileType getType(String beginning) {
        for(MsfFileType t: values()) {
            if(beginning.startsWith(t.token)) {
                return t;
            }
        }
        return Unkown;
    }
}
