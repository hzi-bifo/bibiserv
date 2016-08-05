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
public interface RNAStructMLInterface extends AbstractBioDOMInterface {

    // //////////////////////
    // getter and Setter
    // //////////////////////


    /////////////////////////
    // methods
    /////////////////////////
    /**
     * This method adds a rnastructure containing sequence informations to the 
     * rnastructml for each sequence contained in the SequenceML Document. It returns
     * a String-Array containing the ids of all created rnastructure elements in the 
     * same order as in the SequenceML document.
     * @param seqML             - SequenceML Document containing the sequences
     * @return List<String>     - ids of all generated rnastructure elements in order of creation
     * @throws BioDOMException  - a BioDOMException is thrown on invalid data. 
     *                            Attention: SequenceML documents containing aminoacid sequences are considered invalid!
     */
    public abstract List<String> appendSequencesFromSequenceML(SequenceML seqML)
            throws BioDOMException;

    /**
     * This method adds a rnastructure containing sequence informations to
     * the rnastructml for each sequence included in the fasta string. It returns 
     * a String-Array containing the ids of all created rnastructure elements
     * in the same order as in the fasta string.
     * @param fasta - String in FASTA format
     * @param sequenceType - either NUCLEIC_SEQUENCE or FREE_SEQUENCE
     * @return List<String> - Contains the id attributes of all generated rnastructure element in order of creation
     * @throws BioDOMException
     */
    public abstract List<String> appendSequencesFromFasta(String fasta,
            int sequenceType) throws BioDOMException, IOException;

    /**
     * This method adds a new rnastructure element to the dom and returns the 
     * corrosponding id. several elements can be defined. id both a shape & a
     * structure are supplied, then it is assumed that the structure refers to 
     * the shape. You can leave arguments empty : use null !
     * At least one shape or one structure should be supplied.
     * The function returns the id of the generated rnastructure. This can be
     * used to add additional elements to the structure.
     * @param rnasequence - String, should contain only 'A' 'G' 'C' and 'U' chars : The rna sequence
     * @param seqId       - String, id for the rna sequence
     * @param description - String, description for the rna sequence
     * @param sequenceType- int,    NUCLEIC_SEQUENCE or FREE_SEQUENCE final
     * @param program     - String, Name of the program used to generate the data
     * @param command     - String, command used
     * @param version     - String, version of the program
     * @param date        - String, date of the program run, format: YYYY-MM-DD
     * @param comment     - String, comment
     * @param shape       - String, shape, allowed chars: '[',']','(',')','{','}','<','>' and '_' 
     * @param shapeProb   - Double, probability for the shape
     * @param structure   - String, structure, allowed chars: '[',']','(',')','{','}','<','>' and '.'
     * @param structProb  - Double, probability for the structure
     * @param energy      - Double, energy value for the structure
     * @return String     - id of the generated rnastructure element
     * @throws BioDOMException
     */
    public abstract String addToNewRNAStructure(String rnasequence,
            String seqId, String description, int sequenceType, String program,
            String command, String version, String date, String comment,
            String shape, Double shapeProb, String structure,
            Double structProb, Double energy) throws BioDOMException;

    /**
     * method that appends sequence and structure information to RNAStructML
     * by extracting the data from a DotBracketFasta string. DotBracketFasta
     * must follow the following format rules:
     * >IDSTRING DESCRIPTION
     * SEQUENCESTRING
     * DOTBRACKETSTRING
     * >IDSTRING2 DESCRIPTION2
     * SEQUENCESTRING2
     * DOTBRACKETSTRING2
     * ...
     * 
     * @param dotbracketFasta       - DotBracketFasta String, format see above
     * @param sequenceType          - sequence type - one of NUCLEICACID or FREESEQUENCE
     * @return List<String>         - Contains the id attributes of all 
     *                                generated rnastructure element in order 
     *                                of creation
     * @throws BioDOMException      - on error
     * @throws IOException          - on IOError
     */
    public abstract List<String> appendFromDotBracketFasta(
            String dotbracketFasta, int sequenceType) 
            throws BioDOMException, IOException;
    
    
    /**
     * method that appends sequence and structure information to RNAStructML
     * by extracting the data from a DotBracketFasta string. DotBracketFasta
     * must follow the following format rules:
     * >IDSTRING DESCRIPTION
     * SEQUENCESTRING
     * DOTBRACKETSTRING
     * >IDSTRING2 DESCRIPTION2
     * SEQUENCESTRING2
     * DOTBRACKETSTRING2
     * ...
     * 
     * @param dotbracketFasta       - DotBracketFasta String, format see above
     * @return List<String>         - Contains the id attributes of all 
     *                                generated rnastructure element in order 
     *                                of creation
     * @throws BioDOMException      - on error
     * @throws IOException          - on IOError
     */
    public abstract List<String> appendFromDotBracketFasta(
            String dotbracketFasta)
            throws BioDOMException, IOException;
    
    
    
    /** method that appends sequence and structure information to RNAStructML
     *  by extracting the data from a DotBracketFasta string. DotBracketFasta must
     *  follow the following format rules:
     *  >IDSTRING DESCRIPTION
     *  SEQUENCESTRING
     *  DOTBRACKETSTRING ENERGY(optional)
     *  >IDSTRING2 DESCRIPTION2
     *  SEQUENCESTRING2
     *  DOTBRACKETSTRING2
     *  
     * @param dotbracketFasta        - DotBracketFasta ReaderO, see format above
     * @param sequenceTye            - sequence type - one NUCLEICACID or FREESEQUENCE 
     * @return List<String>          - Contains the id attributes of all 
     *                                 generated rnastructure element in order 
     * @throws BioDOMException       - on error
     * @throws IOException           - on IOError
     */
    public abstract List<String> appendFromDotBracketFasta(
            Reader dotbracketFasta, int sequenceType)
            throws BioDOMException, IOException;
    
    
    /** method that appends sequence and structure information to RNAStructML
     *  by extracting the data from a DotBracketFasta string. DotBracketFasta must
     *  follow the following format rules:
     *  >IDSTRING DESCRIPTION
     *  SEQUENCESTRING
     *  DOTBRACKETSTRING ENERGY(optional)  - this line can occurs one or more
     *  >IDSTRING2 DESCRIPTION2
     *  SEQUENCESTRING2
     *  DOTBRACKETSTRING2 ENERGY(optional) - this line can occurs one or more
     *  
     * @param dotbracketFasta        - DotBracketFasta Reader, see format above
     * @return List<String>          - Contains the id attributes of all 
     *                                 generated rnastructure element in order 
     * @throws BioDOMException       - on error
     * @throws IOException           - on IOError
     */
    public abstract List<String> appendFromDotBracketFasta(
            Reader dotbracketFasta)
            throws BioDOMException, IOException;
    
    
    /**
     * method that appends a given shape to a new rnastructure element. returns 
     * the shape (!) id, use getLastRnaStructureId if you want to add more elements
     * to the rnastructure 
     * @param shape     - String shape, see above for format
     * @return String   - id of the shape (!)
     * @throws BioDOMException
     */
    public abstract String appendShape(String shape) throws BioDOMException;

    /**
     * method that appends a given shape to a new rnastructure element. returns 
     * the shape (!) id, use getLastRnaStructureId if you want to add more elements
     * to the rnastructure
     * @param shape     - String shape, see above for format
     * @param shapeProb - Double shape probability
     * @return String   - id of the shape (!)
     * @throws BioDOMException 
     */
    public abstract String appendShape(String shape, Double shapeProb)
            throws BioDOMException;

    /**
     * method that appends a given shape to the referenced rnastructure element.
     * returns the shape (!) id
     * @param shape             - String shape, see above for format
     * @param rnastructure_id   - String id of the rnastructure element
     * @return String           - id of the shape (!)
     * @throws BioDOMException
     */
    public abstract String appendShape(String shape, String rnastructure_id)
            throws BioDOMException;

    /**
     * method that appends a given shape to the referenced rnastructure element.
     * returns the shape (!) id
     * @param shape             - String shape, see above for format
     * @param shapeProb         - Double probability of the shape
     * @param rnastructure_id   - String id of the rnastructure element
     * @return String           - id of the shape (!)
     * @throws BioDOMException
     */
    public abstract String appendShape(String shape, Double shapeProb,
            String rnastructure_id) throws BioDOMException;
    
    /**
     * method that appends a given shape to the referenced rnastructure element.
     * returns the shape (!) id
     * @param shape             - String shape, see above for format
     * @param shapeProb         - Double probability of the shape
     * @param rnastructure_id   - String id of the rnastructure element
     * @param shape_id          - Shape id of this Shape (generated if null)
     * @return String           - id of the shape (!)
     * @throws BioDOMException
     */
    public abstract String appendShape(String shape, Double shapeProb,
            String rnastructure_id, String shape_id) throws BioDOMException;
    /**
     * method that adds a new structure (in dotbracket format) to a new rnastructure
     * element, use getLastRnaStructureId if you want to add more elements
     * to the rnastructure
     * @param dotBracket    - String in dotbracket format
     * @throws BioDOMException
     */
    public abstract void appendStructure(String dotBracket)
            throws BioDOMException;

    /**
     * method that adds a new structure (in dotbracket format) to a rnastructure
     * element
     * @param dotBracket        - String in dotbracket format
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendStructure(String dotBracket,
            String rnastructure_id) throws BioDOMException;

    /**
     * method that adds a new structure (in dotbracket format) to a new rnastructure
     * element, use getLastRnaStructureId if you want to add more elements
     * to the rnastructure
     * @param dotBracket        - String in dotbracket format
     * @param energy            - Double energy value
     * @throws BioDOMException
     */
    public abstract void appendStructure(String dotBracket, Double energy)
            throws BioDOMException;

    /**
     * method that adds a new structure (in dotbracket format) to a rnastructure
     * element
     * @param dotBracket        - String in dotbracket format
     * @param energy            - Double energy value
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendStructure(String dotBracket, Double energy,
            String rnastructure_id) throws BioDOMException;

    /**
     * method that adds a new structure (in dotbracket format) to a new rnastructure
     * element, use getLastRnaStructureId if you want to add more elements
     * to the rnastructure
     * @param dotBracket    - String in dotbracket format
     * @param structProb    - Double probability of the structure
     * @param energy        - Double energy of the structure
     * @throws BioDOMException
     */
    public abstract void appendStructure(String dotBracket, Double structProb,
            Double energy) throws BioDOMException;

    /**
     * method that adds a new structure (in dotbracket format) to a rnastructure
     * element
     * @param dotBracket        - String in dotbracket format
     * @param structProb        - Double probability of the structure
     * @param energy            - Double energy of the structure
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendStructure(String dotBracket, Double structProb,
            Double energy, String rnastructure_id) throws BioDOMException;

    /**
     * method that adds a new structure (in dotbracket format) to a rnastructure
     * element
     * @param dotBracket        - String in dotbracket format
     * @param shaperef          - String reference to a shape, must be the shape id (!)
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendStructure(String dotBracket, String shaperef,
            String rnastructure_id) throws BioDOMException;

    /**
     * method that adds a new structure (in dotbracket format) to a rnastructure
     * element
     * @param dotBracket        - String in dotbracket format
     * @param structProb        - Double probability of the structure
     * @param energy            - Double energy of the structure
     * @param shaperef          - reference to a shape, must be the shape id (!)
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendStructure(String dotBracket, Double structProb,
            Double energy, String shaperef, String rnastructure_id)
            throws BioDOMException;

    /**
     * method that adds a rnasequence to a new rnastructure element
     * @param rnasequence   - String rna sequence, only rna chars are allowed
     * @param sequenceType  - final NUCLEICACID or FREE
     * @throws BioDOMException 
     */
    public abstract void appendSequence(String rnasequence, int sequenceType)
            throws BioDOMException;

    /**
     * method that adds a rnasequence to a new rnastructure element
     * @param rnasequence   - String rna sequence, only rna chars are allowed
     * @param seqId         - String sequence id, e.g. from fasta
     * @param sequenceType  - final NUCLEICACID or FREE
     * @throws BioDOMException
     */
    public abstract void appendSequence(String rnasequence, String seqId,
            int sequenceType) throws BioDOMException;

    /**
     * method that adds a rnasequence to a new rnastructure element
     * @param rnasequence   - String rna sequence, only rna chars are allowed
     * @param seqId         - String sequence id, e.g. from fasta
     * @param description   - String that describes the sequence, e.g. fasta comment
     * @param sequenceType  - final NUCLEICACID or FREE
     * @throws BioDOMException
     */
    public abstract void appendSequence(String rnasequence, String seqId,
            String description, int sequenceType) throws BioDOMException;

    /**
     * method that adds a rnasequence to a rnastructure element
     * @param rnasequence       - String rna sequence, only rna chars are allowed
     * @param seqId             - String sequence id, e.g. from fasta
     * @param description       - String that describes the sequence, e.g. fasta comment
     * @param sequenceType      - either NUCLEICACID or FREE
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendSequence(String rnasequence, String seqId,
            String description, int sequenceType, String rnastructure_id)
            throws BioDOMException;

    /**
     * method that adds a rnasequence to a rnastructure element
     * @param rnasequence       - String rna sequence, only rna chars are allowed
     * @param seqId             - String sequence id, e.g. from fasta
     * @param sequencename      - String name of sequence
     * @param description       - String that describes the sequence, e.g. fasta comment
     * @param sequenceType      - either NUCLEICACID or FREE
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendSequence(String rnasequence, String seqId,
            String sequencename, String description, int sequenceType,
            String rnastructure_id) throws BioDOMException;

    /**
     * method that adds a rnasequence to a rnastructure element
     * @param rnasequence       - String rna sequence, only rna chars are allowed
     * @param seqId             - String sequence id, e.g. from fasta
     * @param sequencename      - String name of sequence
     * @param synonyms          - List<String> containing synonyms to name
     * @param description       - String that describes the sequence, e.g. fasta comment
     * @param sequenceType      - either NUCLEICACID or FREE
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendSequence(String rnasequence, String seqId,
            String sequencename, List<String> synonyms, String description,
            int sequenceType, String rnastructure_id) throws BioDOMException;

    /**
     * method that adds a program element to a rnastructure element
     * @param program           - String programname
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendProgram(String program, String rnastructure_id)
            throws BioDOMException;

    /**
     * method that adds a program element to a rnastructure element
     * @param program           - String programname
     * @param command           - String command
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendProgram(String program, String command,
            String rnastructure_id) throws BioDOMException;

    /**
     * method that adds a program element to a rnastructure element
     * @param program           - String programname
     * @param command           - String command
     * @param version           - String program version 
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendProgram(String program, String command,
            String version, String rnastructure_id) throws BioDOMException;

    /**
     * method that adds a program element to a rnastructure element
     * @param program           - String programname
     * @param command           - String command
     * @param version           - String program version
     * @param date              - String date, must be in format YYYY-MM-DD
     * @param rnastructure_id   - String id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendProgram(String program, String command,
            String version, String date, String rnastructure_id)
            throws BioDOMException;

    /**
     * method that adds a comment element to a rnastructure element
     * @param comment           - String comment content
     * @param rnastructure_id   - id of the rnastructure element
     * @throws BioDOMException
     */
    public abstract void appendComment(String comment, String rnastructure_id)
            throws BioDOMException;

    /**
     * method that adds an empty rnastructure element to the root element (rnastructml).
     * the method returns the id of the added element.
     * @return String   - id of the added rnastructure element 
     */
    public abstract String appendEmptyRNAStructure();

    
    /**
     * method that returns a DotBracketFasta representation of the current
     * RNAStructML document
     * @return String           - DotBracketFasta String
     * @throws BioDOMException  - on error
     */
    public abstract String toDotBracketFasta() throws BioDOMException;
    
    
    /**
     * method that returns the id of the rnastructure element that was added 
     * last to the document. method returns null if there is no such element!
     * @return  - id of the last rnastructure element added or null
     */
    public abstract String getLastRnastructureId() throws BioDOMException;

    /**
     * getter method. returns the ids of all rnastructure elements in the DOM
     * as a List<String>.
     * @return List<String> containing rnastructure ids, null if there is no such element
     */
    public abstract List<String> getRnastructureIds();

    /**
     * getter method. returns the ids of all sequences contained in the rnastructure
     * refered by the rnastructure_id.
     * @param rnastructure_id           - id of the rnastructure element
     * @return List<String>                 - contains the ids of all sequence elements
     *                                    in the rnastructure or null if there are 
     *                                    no such elements
     * @throws BioDOMException          - on error
     */
    public abstract List<String> getSequenceIds(String rnastructure_id)
            throws BioDOMException;

    /**
     * getter method. returns a Hashtable containing the sequence information.
     * Keys in the Hash are generated only for information items contained 
     * in the sequence element!
     * key/values:
     * seqID / String                   - id of the sequence
     * name / String                    - name of the sequence
     * synonyms / List<String>          - synonyms to the name
     * description / String             - description of the sequence
     * sequence / String                - the sequence string, this element is 
     *                                    not in the Hash, if the sequenceType 
     *                                    is EMPTYSEQUENCE
     * sequenceType / Integer           - NUCLEIC, FREE or EMPTYSEQUENCE
     * sequenceTypeName / String        - NUCLEICSEQUENCE_NAME, FREESEQUENCE_NAME
     *                                    or EMPTYSEQUENCE_NAME
     * comment / String                 - String with comment
     *              
     * @param rnastructure_id           - id of the rnastructure element
     * @return Hashtable<String,Object> - contains the sequence information
     * @throws BioDOMException          - on error
     */
    public abstract Hashtable<String, Object> getSequence(String rnastructure_id)
            throws BioDOMException;

    /**
     * getter method. returns a Hashtable containing the program information
     * key/values:
     * programname / String             - Name of the program
     * command / String                 - command line call
     * version / String                 - version of program
     * date / String                    - String with date of program run
     *                                    format YYYY-MM-DD
     *              
     * @param rnastructure_id           - id of the rnastructure element
     * @return Hashtable<String, String>- contains the program information, 
     *                                    null if there is no such element
     * @throws BioDOMException          - on error
     */
    public abstract Hashtable<String, String> getProgram(String rnastructure_id)
            throws BioDOMException;

    /**
     * getter method. returns a String with the comment
     *              
     * @param rnastructure_id           - id of the rnastructure element
     * @return String                   - contains the comment text
     * @throws BioDOMException          - on error
     */
    public abstract String getComment(String rnastructure_id)
            throws BioDOMException;

    /**
     * getter method. returns a List<Hashtable<String, Object>> containing the structure information
     * key/values (per Hashtable):
     * structure / String                   - The structure in dotbracket format
     * energy / Double                      - The energy value of the structure
     * probability / Double                 - The probability of the structure
     * shaperef / String                    - Reference to a shape (same as shape id)
     * shapeType / String                   - "structure"
     *              
     * @param rnastructure_id               - id of the rnastructure element
     * @return List<Hashtable<String, Object>  - contains the structure information, 
     *                                        null if there are no such elements
     * @throws BioDOMException              - on error
     */
    public abstract List<Hashtable<String, Object>> getStructures(
            String rnastructure_id) throws BioDOMException;

    /**
     * getter method. returns a List<Hashtable<String, Object>> containing the shape information
     * key/values (per shape):
     * shape / String                       - The shape
     * probability / Double                 - probability of shape
     * id / String                          - shape id for references to shape
     * shapeType / String                   - "shape"
     *              
     * @param rnastructure_id               - id of the rnastructure element
     * @return List<Hashtable<String, Object>>  - contains the shape information
     * @throws BioDOMException              - on error
     */
    public abstract List<Hashtable<String, Object>> getShapes(String rnastructure_id)
            throws BioDOMException;

    /**
     * getter method. returns a Hashtable containing the structure and shape information
     * 
     * key/values:
     * shapes / List<Hashtable<String, Object>>     - @see getShapes() method
     * structures / List<Hashtable<String, Object>> - @see getStructures() method
     *              
     * @param rnastructure_id                   - id of the rnastructure element
     * @return Hashtable<String, List<Hashtable>>   - contains the structure and shape information
     * @throws BioDOMException                  - on error
     */
    public abstract Hashtable<String, List<Hashtable<String, Object>>> getStructuresAndShapes(
            String rnastructure_id) throws BioDOMException;

}