/**
 * 
 */
package de.unibi.techfak.bibiserv.biodom;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import java.util.List;

import de.unibi.techfak.bibiserv.biodom.exception.BioDOMException;

/**
 * @author Kai Loewenthal <kloewent@techfak.uni-bielefeld.de>
 *
 */
public interface RNAStructAlignmentMLInterface extends AbstractBioDOMInterface{

    // //////////////////////
    // getter and Setter
    // //////////////////////


    // ///////////////////////
    // methods
    // ///////////////////////
    /**
     * method that adds new rnastructure alignment along with all subelements
     * 
     * @param score         - Integer score value, may be null
     * @param sequences     - List of Hashtables, submit one Hashtable for 
     *                        each sequence element -> at least two!, required 
     *                        Key/Value (+ means required!) : 
     *                        seqID/String              - (+) sequence id
     *                        name/String               - sequence name
     *                        synonyms/List<String>     - synonyms of name
     *                        description/String        - description
     *                        sequence/String           - the sequence
     *                        seqType/int               - (+) FREE or NUCLEICACID
     *                        structure/String          - (+) structure in gapped dotbracket
     *                        energy/Double             - energy value for the structure
     *                        probability/Double        - probability for the structure
     *                        seqcomment/String         - comment string
     * @param program       - Hashtable with program information, may be null
     *                        Key/Value (+ means required!):
     *                        programname/String        - (+) program name
     *                        command/String            - commandline call
     *                        version/String            - version of program
     *                        date/String               - format YYYY-MM-DD
     * @param comment       - String with comment, may be null
     * @param consensus     - Hashtable with consensus shape information, may be null
     *                        Key/Value (+ means required!) :
     *                        structure/String          - consensus structure in gapped dotbracket
     *                        energy/Double             - energy value
     *                        probability/Double        - probability value
     *                        structureprobabilities/List<Hashtable<String, Object> with
     *                              a/Integer           - 1. Position
     *                              b/Integer           - 2. Position
     *                              probability/Double  - Probability
     *                        sequence/Hashtable<String,Object> with
     *                              seqID/String              - (+) sequence id
     *                              name/String               - sequence name
     *                              synonyms/List<String>     - synonyms of name
     *                              description/String        - description
     *                              sequence/String           - the sequence
     *                              seqType/int               - (+) FREE or NUCLEICACID
     *                              structure/String          - (+) structure in gapped dotbracket
     *                              energy/Double             - energy value for the structure
     *                              probability/Double        - probability for the structure
     *                              seqcomment/String         - comment string
     *                              probabilitylist/List<Double> 
     *                                                        - Probabilities
     *                        id/String                 - identifier                           
     * @return String       - id of generated rnastructurealignment element
     * @throws BioDOMException on error                       
     */
    public abstract String addNewRnastructureAlignment(Integer score,
            List<Hashtable> sequences, Hashtable program, String comment,
            Hashtable consensus) throws BioDOMException;

    /**
     * Method that appends a alignedDotBracketFasta string to a new rnastructalignment
     * element. Returns id of the new element.
     * 
     * @param alignedDotBracketFasta    - the aligned DotBracketFasta string
     * @param sequenceType              - final int defining the sequence type
     * @return String                   - id of the rnastructalignment element
     * @throws BioDOMException on error
     * @throws IOException on IOError
     */
    public String appendFromAlignedDotBracketFasta(final String alignedDotBracketFasta, final int sequenceType) 
            throws BioDOMException,IOException;
        
    /**
     * Method that appends a alignedDotBracketFasta string to a new rnastructalignment
     * element. Returns id of the new element.
     * 
     * @param alignedDotBracketFasta    - the aligned DotBracketFasta string
     * @return String                   - id of the rnastructalignment element
     * @throws BioDOMException on error
     * @throws IOException on IOError
     */
    public String appendFromAlignedDotBracketFasta(final String alignedDotBracketFasta) 
            throws BioDOMException,IOException;
    
    /**
     * Method that appends the content of a alignedDotBracketFasta Reader
     * to a new rnastructalignment element. Returns id of the new element.
     * 
     * @param alignedDotBracketFasta    - the aligned DotBracketFasta Reader
     * @param sequenceType              - final int defining the sequence type
     * @return String                   - id of the rnastructalignment element
     * @throws BioDOMException on error
     * @throws IOException on IOError
     */
    public String appendFromAlignedDotBracketFasta(Reader alignedDotBracketFasta, final int sequenceType) 
            throws BioDOMException,IOException;
    
    /**
     * Method that appends the content of a alignedDotBracketFasta Reader
     * to a new rnastructalignment element. Returns id of the new element.
     * 
     * @param alignedDotBracketFasta    - the aligned DotBracketFasta Reader
     * @return String                   - id of the rnastructalignment element
     * @throws BioDOMException on error
     * @throws IOException on IOError
     */
    public String appendFromAlignedDotBracketFasta(Reader alignedDotBracketFasta) 
            throws BioDOMException,IOException;
    
    /**
     * method that appends a new sequence element to a new rnastructurealignment.
     *
     * @param seqID                         - String sequence ID
     * @param structure                     - String with the structure in gapped dotbracket format
     * @throws BioDOMException              - is thrown on invalid data
     */
    public abstract void appendSequence(String seqID, String structure)
            throws BioDOMException;

    /**
     * method that appends a new sequence element to an existing rnastructurealignment
     * element. Creates a new rnastructurealignment if rnastructurealignment_id is
     * null.
     * @param seqID                         - String sequence ID
     * @param sequence                      - String with the pure (gapped) sequence
     * @param sequenceType                  - FREE or NUCLEICACID
     * @param structure                     - String with the structure in gapped dotbracket format
     * @param rnastructurealign_id          - id of the rnastructurealignment
     * @throws BioDOMException              - is thrown on invalid data
     */
    public abstract void appendSequence(String seqID, String structure,
            String rnastructure_id) throws BioDOMException;

    /**
     * method that appends a new sequence element to a new rnastructurealignment.
     *
     * @param seqID                         - String sequence ID
     * @param sequence                      - String with the pure (gapped) sequence
     * @param sequenceType                  - FREE or NUCLEICACID or EMPTYSEQUENCE
     * @param structure                     - String with the structure in gapped dotbracket format
     * @throws BioDOMException              - is thrown on invalid data
     */
    public abstract void appendSequence(String seqID, String sequence,
            Integer seqType, String structure) throws BioDOMException;

    /**
     * method that appends a new sequence element to an existing rnastructurealignment
     * element. Creates a new rnastructurealignment if rnastructurealignment_id is
     * null.
     * @param seqID                         - String sequence ID
     * @param sequence                      - String with the pure (gapped) sequence
     * @param sequenceType                  - FREE or NUCLEICACID or EMPTYSEQUENCE
     * @param structure                     - String with the structure in gapped dotbracket format
     * @param rnastructurealign_id          - id of the rnastructurealignment
     * @throws BioDOMException              - is thrown on invalid data
     */
    public abstract void appendSequence(String seqID, String sequence,
            Integer seqType, String structure, String rnastructure_id)
            throws BioDOMException;

    /**
     * method that appends a new sequence element to a new rnastructurealignment.
     *
     * @param seqID                         - String sequence ID
     * @param seqName                       - String sequence Name
     * @param description                   - String with description
     * @param structure                     - String with the structure in gapped dotbracket format
     * @throws BioDOMException              - is thrown on invalid data
     */
    public abstract void appendSequence(String seqID, String seqName,
            String description, String structure) throws BioDOMException;

    /**
     * method that appends a new sequence element to an existing rnastructurealignment
     * element. Creates a new rnastructurealignment if rnastructurealignment_id is
     * null.
     * @param seqID                         - String sequence ID
     * @param seqName                       - String sequence Name
     * @param description                   - String with description
     * @param structure                     - String with the structure in gapped dotbracket format
     * @param rnastructurealign_id          - id of the rnastructurealignment
     * @throws BioDOMException              - is thrown on invalid data
     */
    public abstract void appendSequence(String seqID, String seqName,
            String description, String structure, String rnastructure_id)
            throws BioDOMException;

    /**
     * method that appends a new sequence element to a new rnastructurealignment.
     *
     * @param seqID                         - String sequence ID
     * @param seqName                       - String sequence Name
     * @param description                   - String with description
     * @param sequence                      - String with the pure (gapped) sequence
     * @param sequenceType                  - FREE or NUCLEICACID or EMPTYSEQUENCE
     * @param structure                     - String with the structure in gapped dotbracket format
     * @throws BioDOMException              - is thrown on invalid data
     */
    public abstract void appendSequence(String seqID, String seqName,
            String description, String sequence, Integer seqType,
            String structure) throws BioDOMException;

    /**
     * method that appends a new sequence element to an existing rnastructurealignment
     * element. Creates a new rnastructurealignment if rnastructurealignment_id is
     * null.
     * @param seqID                         - String sequence ID
     * @param seqName                       - String sequence Name
     * @param description                   - String with description
     * @param sequence                      - String with the pure (gapped) sequence
     * @param sequenceType                  - FREE or NUCLEICACID or EMPTYSEQUENCE
     * @param structure                     - String with the structure in gapped dotbracket format
     * @param rnastructurealign_id          - id of the rnastructurealignment
     * @throws BioDOMException              - is thrown on invalid data
     */
    public abstract void appendSequence(String seqID, String seqName,
            String description, String sequence, Integer seqType,
            String structure, String rnastructure_id) throws BioDOMException;

    /**
     * method that appends a new sequence element to a new rnastructurealignment.
     * 
     * @param seqID                         - String sequence ID
     * @param seqName                       - String sequence Name
     * @param synonyms                      - List<String> with synomyms
     * @param description                   - String with description
     * @param sequence                      - String with the pure (gapped) sequence
     * @param sequenceType                  - FREE or NUCLEICACID or EMPTYSEQUENCE
     * @param structure                     - String with the structure in gapped dotbracket format
     * @param energy                        - energy of the structure
     * @param probability                   - probability of the structure
     * @param seqcomment                    - String comment to sequence
     * @throws BioDOMException              - is thrown on invalid data
     */
    public abstract void appendSequence(String seqID, String seqName,
            List<String> synonyms, String description, String sequence,
            Integer sequenceType, String structure, Double energy,
            Double probability, String seqcomment) throws BioDOMException;

    /**
     * method that appends a new sequence element to an existing rnastructurealignment
     * element. Creates a new rnastructurealignment if rnastructurealignment_id is
     * null.
     * @param seqID                         - String sequence ID
     * @param seqName                       - String sequence Name
     * @param synonyms                      - List<String> with synomyms
     * @param description                   - String with description
     * @param sequence                      - String with the pure (gapped) sequence
     * @param sequenceType                  - FREE or NUCLEICACID or EMPTYSEQUENCE
     * @param structure                     - String with the structure in gapped dotbracket format
     * @param energy                        - energy of the structure
     * @param probability                   - probability of the structure
     * @param seqcomment                    - String comment for sequence 
     * @param rnastructurealign_id          - id of the rnastructurealignment
     * @throws BioDOMException              - is thrown on invalid data
     */
    public abstract void appendSequence(String seqID, String seqName,
            List<String> synonyms, String description, String sequence,
            Integer sequenceType, String structure, Double energy,
            Double probability, String seqcomment, String rnastructurealign_id)
            throws BioDOMException;

    /**
     * method that adds a program element to a rnastructurealignment element
     * 
     * @param program -
     *            String programname
     * @param rnastructurealign_id -
     *            String id of the rnastructurealignment element
     * @throws BioDOMException
     */
    public abstract void appendProgram(String program,
            String rnastructurealignment_id) throws BioDOMException;

    /**
     * method that adds a program element to a rnastructurealignment element
     * 
     * @param program -
     *            String programname
     * @param command -
     *            String command
     * @param rnastructurealign_id -
     *            String id of the rnastructurealignment element
     * @throws BioDOMException
     */
    public abstract void appendProgram(String program, String command,
            String rnastructurealign_id) throws BioDOMException;

    /**
     * method that adds a program element to a rnastructurealignment element
     * 
     * @param program -
     *            String programname
     * @param command -
     *            String command
     * @param version -
     *            String program version
     * @param rnastructurealign_id -
     *            String id of the rnastructurealignment element
     * @throws BioDOMException
     */
    public abstract void appendProgram(String program, String command,
            String version, String rnastructurealign_id) throws BioDOMException;

    /**
     * method that adds a program element to a rnastructurealignment element
     * 
     * @param program -
     *            String programname
     * @param command -
     *            String command
     * @param version -
     *            String program version
     * @param date -
     *            String date, must be in format YYYY-MM-DD
     * @param rnastructurealign_id -
     *            String id of the rnastructurealignment element
     * @throws BioDOMException
     */
    public abstract void appendProgram(String program, String command,
            String version, String date, String rnastructurealign_id)
            throws BioDOMException;

    /**
     * method that adds a comment element to a rnastructurealignment element
     * 
     * @param comment -
     *            String comment content
     * @param rnastructurealign_id -
     *            id of the rnastructurealignment element
     * @throws BioDOMException
     */
    public abstract void appendComment(String comment,
            String rnastructurealign_id) throws BioDOMException;

    /**
     * method that appends a consensus element to an existing rnastructurealignment
     * element. throws an biodom exception if invalid arguments are given
     * 
     * @param consensusstructure/String - consensus structure in gapped dotbracket
     * @param csenergy/Double           - energy value
     * @param csprobability/Double      - probability value
     * @param structureprobs/List<Hashtable<String, Object> with
     *              a/Integer                       - 1. Position
     *              b/Integer                       - 2. Position
     *              probability/Double              - Probability
     * @param sequence/Hashtable<String,Object> with
     *              seqID/String                    - (+) sequence id
     *              name/String                     - sequence name
     *              synonyms/List<String>           - synonyms of name
     *              description/String              - description
     *              sequence/String                 - the sequence
     *              seqType/int                     - (+) FREE or NUCLEICACID
     *              structure/String                - (+) structure in gapped dotbracket
     *              energy/Double                   - energy value for the structure
     *              probability/Double              - probability for the structure
     *              seqcomment/String               - comment string
     *              probabilitylist/List<Double>    - Probabilities
     * @param consensus_id/String        - identifier
     * @param rnastructurealign_id       - String id of the rnastructurealignment to add the consensus to
     * @throws BioDOMException           - thrown on error
     */
    public abstract void appendConsensus(String consensusstructure, Double csenergy, 
            Double csprobability, List<Hashtable<String,Object>> structureprobs, 
            Hashtable<String,Object> sequence, String consensus_id, 
            String rnastructurealign_id) throws BioDOMException;

    /**
     * method that adds an empty rnastructurealignment element to the root
     * element (rnastructalignmentml). the method returns the id of the added
     * element.
     * 
     * @return String - id of the added rnastructurealignment element
     */
    public abstract String appendEmptyRNAStructureAlignment();

    /**
     * method that adds an empty rnastructurealignment element to the root
     * element (rnastructalignmentml). the method returns the id of the added
     * element.
     *
     * @param score     - Integer score value, may be null
     * @return String   - id of the added rnastructurealignment element
     */
    public abstract String appendEmptyRNAStructureAlignment(Integer score);

    /**
     * Method that returns a aligned DotBracketFasta representation of the 
     * RNAStructureAlignmentML.
     * 
     * @return String   - aligned DotBracketFasta
     * @throws BioDOMException on error
     */
    public String toAlignedDotBracketFasta() throws BioDOMException;
    
    /**
     * method that returns the id of the rnastructurealignment element that was
     * added last to the document. method returns null if there is no such
     * element!
     * 
     * @return - id of the last rnastructurealignment element added or null
     */
    public abstract String getLastRnastructureAlignmentId();

    /**
     * getter method. returns all ids of the rnastructurealignment elements in 
     * a List<String>
     * @return List<String> ids or null if there is no such element
     */
    public abstract List<String> getRnastructureAlignmentIds();

    /**
     * getter method. returns a complete rnastructurealignment element in a 
     * Hashtable containing several subtables...:
     * key/values:
     * sequences / List<Hashtables>     - Hashtables containing the sequence information
     *                                    @see getAllSequences()
     * comment   / String               - A comment
     * consensus / Hashtable            - Hashtable containing consensus data
     * score     / Integer              - the score value
     * id        / String               - the rnastructurealignment_id
     * program   / Hashtable            - program information
     * 
     * @param rnastructurealignment_id  - Id of the rnastructurealignment element
     * @return  Hashtable               - see above
     * @throws BioDOMException on error
     */
    public abstract Hashtable<String, Object> getRnastructureAlignment(
            String rnastructurealignment_id) throws BioDOMException;

    /**
     * getter method. returns the ids of all sequences contained in the 
     * rnastructurealignment element
     * @param rnastructurealignment_id  - id of the rnastructurealignment element
     * @return List<String>                 - ids of sequence elements
     * @throws BioDOMException on error
     */
    public abstract List<String> getSequenceIds(String rnastructurealignment_id)
            throws BioDOMException;

    /**
     * getter method. returns a Hashtable containing sequence information.
     * Key/Value:
     * seqID            / String        - sequence id
     * name             / String        - name of the sequence
     * synonyms         / List<String>  - synonyms to the name
     * description      / String        - description of sequence
     * sequence         / String        - the sequence string
     * sequenceType     / Integer       - FREE, EMPTYSEQUENCE or NUCLEIC
     * sequenceTypeName / String        - ALIGNEDFREESEQUENCE_NAME, 
     *                                    ALIGNEDNUCLEICACIDSEQUENCE_NAME or 
     *                                    EMPTYSEQUENCE_NAME
     * structure        / Hashtable     - structure information
     *                                    key / value same as in 
     *                                    @see getConsensusStructure()
     * seqcomment       / String        - comment to sequence
     * 
     * @param rnastructurealignment_id  - id of the rnastructurealignment element
     * @param sequence_id               - id of the sequence element
     * @return Hashtable                - sequence data, see above
     * @throws BioDOMException on error
     */
    public abstract Hashtable getSequence(String rnastructurealignment_id,
            String sequence_id) throws BioDOMException;

    /**
     * getter method. returns a List<Hashtable> containing sequence information for
     * all sequences in the rnastructurealignment element.
     * Key/Value:
     * seqID            / String        - sequence id
     * name             / String        - name of the sequence
     * synonyms         / List<String>  - synonyms to the name
     * description      / String        - description of sequence
     * sequence         / String        - the sequence string
     * sequenceType     / Integer       - FREE, EMPTYSEQUENCE or NUCLEIC
     * sequenceTypeName / String        - ALIGNEDFREESEQUENCE_NAME, 
     *                                    ALIGNEDNUCLEICACIDSEQUENCE_NAME or 
     *                                    EMPTYSEQUENCE_NAME
     * structure        / Hashtable     - structure information
     *                                    key / value same as in 
     *                                    @see getConsensusStructure()
     * seqcomment       / String        - comment to sequence
     * 
     * @param rnastructurealignment_id  - id of the rnastructurealignment element
     * @return List<Hashtable>              - list containing sequence data, see above
     * @throws BioDOMException on error
     */
    public abstract List<Hashtable> getAllSequences(String rnastructurealignment_id)
            throws BioDOMException;

    /**
     * getter method. returns a Hashtable containing program information.
     * Key/Value:
     * programname      / String        - name of program
     * command          / String        - command line call
     * version          / String        - version of program
     * date             / String        - date in format YYYY-MM-DD
     * 
     * @param rnastructurealignment_id  - id of the rnastructurealignment element
     * @return Hashtable                - program information, see above
     * @throws BioDOMException on error
     */
    public abstract Hashtable getProgram(String rnastructurealignment_id)
            throws BioDOMException;

    /**
     * getter method. returns a Hashtable containing consensus information.
     * Key/Value:
     * consensusstructure/String - consensus structure in gapped dotbracket
     * csenergy/Double           - energy value
     * csprobability/Double      - probability value
     * structureprobs/List<Hashtable<String, Object> with
     *              a/Integer                       - 1. Position
     *              b/Integer                       - 2. Position
     *              probability/Double              - Probability
     * sequence/Hashtable<String,Object> with
     *              seqID/String                    - (+) sequence id
     *              name/String                     - sequence name
     *              synonyms/List<String>           - synonyms of name
     *              description/String              - description
     *              sequence/String                 - the sequence
     *              seqType/int                     - (+) FREE or NUCLEICACID
     *              structure/String                - (+) structure in gapped dotbracket
     *              energy/Double                   - energy value for the structure
     *              probability/Double              - probability for the structure
     *              seqcomment/String               - comment string
     *              probabilitylist/List<Double>    - Probabilities
     * consensus_id/String       - identifier
     * rnastructurealign_id      - String id of the rnastructurealignment
     *
     * @param rnastructurealignment_id  - id of the rnastructurealignment element
     * @return Hashtable<String,Object> - consensus structure data, see above
     * @throws BioDOMException on error
     */
    public abstract Hashtable<String, Object> getConsensus(String rnastructurealignment_id) 
        throws BioDOMException;

    /**
     * getter method. returns a String with the comment text.
     *
     * @param rnastructurealignment_id  - id of the rnastructurealignment element
     * @return String                   - comment text
     * @throws BioDOMException on error
     */
    public abstract String getComment(String rnastructurealignment_id)
            throws BioDOMException;

    /**
     * getter method. returns the score value of the given
     * rnastructurealignment. returns null if no score value is
     * set. throws a BioDOMException if the rnastructurealignment id 
     * does not exist in document.
     * 
     * @param rnastructurealignment_id  - id of the refered 
     *                                    rnastructurealignment element
     * @return Integer                  - the score value or null
     * @throws BioDOMException          - on invalid rnastructurealignment_id
     */
    public abstract Integer getScore(String rnastructurealignment_id)
            throws BioDOMException;
}