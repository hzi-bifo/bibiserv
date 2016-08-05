package de.unibi.techfak.bibiserv.biodom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.unibi.techfak.bibiserv.biodom.exception.BioDOMException;
import de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBox;

/**
 * RNAStructML -- XML representation of rna secondary structures, uses
 * dotBracket
 * 
 * Namespace: http://hobit.sourceforge.net/xsds/20060201/rnastructML.xsd Homepage:
 * http://hobit.sourceforge.net/xsds/20060201/rnastructML.xsd
 * 
 * @author Kai Loewenthal <kloewent@techfak.uni-bielefeld.de>
 *         Jan Krüger <jkrueger@techfak.uni-bielefeld.de>
 * @version 1.0
 * TODO : Current version does not support crossrefs!
 * 
 * @throws BioDOMException
 *             on failure / fatal error
 */
public class RNAStructML extends AbstractBioDOM implements RNAStructMLInterface {

	
    /**
     * Constant for structure type : structure
     */
    public static final int STRUCTURE = 1;

    /**
     * Constant for structure type : shape
     */
    public static final int SHAPE = 2;
    
    //namespace & other rnastructml finals
 
    
    // XML element tag names
    protected static final String ELEM_RNASTRUCTML = "rnastructML";
    protected static final String ELEM_RNASTRUCTURE = "rnastructure";
    protected static final String ELEM_SEQUENCE = "sequence";
    protected static final String ELEM_NAME = "name";
    protected static final String ELEM_SYNONYMS = "synonyms";
    protected static final String ELEM_DESCRIPTION = "description";
    protected static final String ELEM_NUCLEICACIDSEQUENCE = "nucleicAcidSequence";
    protected static final String ELEM_FREESEQUENCE = "freeSequence";
    protected static final String ELEM_EMPTYSEQUENCE = "emptySequence";
    protected static final String ELEM_CROSSREFS = "crossRefs";
    protected static final String ELEM_COMMENT = "comment";
    protected static final String ELEM_SHAPE = "shape";
    protected static final String ELEM_STRUCTURE = "structure";
    protected static final String ELEM_PROGRAM = "program";
    
    // XML attribute names
    protected static final String ATT_SEQID = "seqID";
    protected static final String ATT_ID = "id";
    protected static final String ATT_PROBABILITY = "probability";
    protected static final String ATT_ENERGY = "energy";
    protected static final String ATT_SHAPEREF = "shaperef";
    protected static final String ATT_VERSION = "version";
    protected static final String ATT_COMMAND = "command";
    protected static final String ATT_DATE = "date";
    
    // logger
    private static Logger log = Logger.getLogger(RNAStructML.class.toString());
    
    // hashtable
    private Hashtable<String, Hashtable> rnastructureHash = new Hashtable<String, Hashtable>();
    private static final String SHAPESHASHNAME = "shapesHash";
    private static final String SEQUENCEHASHNAME = "sequenceHash";
    
    // RegExp for Structure line
    private static final Pattern rnastructPattern = Pattern.compile("^[\\(\\)\\[\\]\\{\\}\\<\\>.]+?\\s*$");
    // RegExp for Structure with Energy line
    private static final Pattern rnastructWithEnergyPattern = Pattern.compile("^[\\(\\)\\[\\]\\{\\}\\<\\>.]+?\\s+\\(?-?\\d+\\.\\d*?\\)?\\s*$");

    
    // NO INPUT
    /**
     * creates a new RNAStructML object for processing DOM Documents as
     * NSLOCATION 
     * 
     * @exception BioDOMException
     *                on failure / fatal error
     */
    public RNAStructML() throws BioDOMException {
        this(null, null, null);
    }

    // CATALOGPATH INPUT
    /**
     * creates a new RNAStructML object for processing DOM Documents as
     * NSLOCATION
     * 
     * @param catalogpropertyfile
     *            path of Catalog.properties file
     * @exception BioDOMException
     *                on failure
     */
    public RNAStructML(String catalogpropertyfile) throws BioDOMException {
        this(catalogpropertyfile, null, null);
    }

    // DOM INPUT
    /**
     * creates a new RNAStructML object for processing DOM Documents as
     * NSLOCATION
     * 
     * @param submitted_dom
     *            DOM for processing
     * @exception BioDOMException
     *                on failure
     */
    public RNAStructML(Document submitted_dom) throws BioDOMException {
        this(null, submitted_dom, null);
    }

    // BioDOMWarningBox INPUT
    /**
     * creates a new RNAStructML object for processing DOM Documents as
     * NSLOCATION
     * 
     * @param warningBox
     *            BioDOMWarningBox for processing warnings
     * @exception BioDOMException
     *                on failure
     */
    public RNAStructML(BioDOMWarningBox warningBox) throws BioDOMException{
        this(null, null, warningBox);
    }
    
    // DOM & CATALOGPATH INPUT
    /**
     * creates a new RNAStructML object for processing DOM Documents as
     * NSLOCATION
     * 
     * @param catalogpropertyfile
     *            path of Catalog.properties file
     * @param submitted_dom
     *            DOM for processing
     * @exception BioDOMException
     *                on failure
     */
    public RNAStructML(String catalogpropertyfile, Document submitted_dom)
            throws BioDOMException {
        this(catalogpropertyfile, submitted_dom, null);
    }

    // DOM & BioDOMWarningBox INPUT
    /**
     * creates a new RNAStructML object for processing DOM Documents as
     * NSLOCATION
     * 
     * @param warningBox
     *            BioDOMWarningBox for processing warnings
     * @param submitted_dom
     *            DOM for processing
     * @exception BioDOMException
     *                on failure
     */
    public RNAStructML(Document submitted_dom, BioDOMWarningBox warningBox)
            throws BioDOMException {
        this(null, submitted_dom, warningBox);
    }

    // BioDOMWarningBox & CATALOGPATH INPUT
    /**
     * creates a new RNAStructML object for processing DOM Documents as
     * NSLOCATION
     * 
     * @param catalogpropertyfile
     *            path of Catalog.properties file
     * @param warningBox
     *            BioDOMWarningBox for processing warnings
     * @exception BioDOMException
     *                on failure
     */
    public RNAStructML(String catalogpropertyfile, BioDOMWarningBox warningBox)
            throws BioDOMException {
        this(catalogpropertyfile, null, warningBox);
    }

    
    // DOM & CATALOGPATH & BioDOMWarningBox Input
    /**
     * creates a new RNAStructML object for processing DOM Documents as
     * NSLOCATION
     * 
     * @param catalogpropertyfile
     *            path of Catalog.properties file
     * @param submitted_dom
     *            DOM for processing
     * @param warningBox
     *            BioDOMWarningBox for processing warnings
     * @exception BioDOMException
     *                on failure
     */
   public RNAStructML(String catalogpropertyfile, Document submitted_dom, 
            BioDOMWarningBox warningBox) throws BioDOMException{
        super(catalogpropertyfile);
        
        isNillable = true;
        if(submitted_dom != null){
            this.setDom(submitted_dom);
        }else{
            initDOM();
        }
        if(warningBox != null){
            this.setWarningBox(warningBox);
        }
    }

    // //////////////////////
    // getter and Setter
    // //////////////////////
 
     /*
      * 
      */
    public void setDom(final String xmlstring) throws BioDOMException {
        super.setDom(xmlstring);
        generateRnastructureHash();
    }

    /*
     *  (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#setDom(org.w3c.dom.Document)
     */
    public void setDom(final Document dom) throws BioDOMException {
        super.setDom(dom);
        generateRnastructureHash();
    }
 
    // //////////////////////
    // Converter
    // /////////////////////

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendSequencesFromSequenceML(de.unibi.techfak.bibiserv.biodom.SequenceML)
     */
    public List<String> appendSequencesFromSequenceML(final SequenceML seqML) throws BioDOMException{
        
        log.config("Appending sequences from SequenceML..." );
        
        //checking for null param!
        if(seqML == null){
            log.severe("Error! Null was submitted instead of SequenceML!");
            throw new BioDOMException("Error! Null was submitted instead of SequenceML!");
        }
        
        //checking the sequenceML
        if(!(seqML.validate())){
            log.severe("Error! Submitted SequenceML is invalid!");
            throw new BioDOMException("Error: Invalid SequenceML!");
        }
        
        //checking for aminoacid sequences...
        if(seqML.dom.getElementsByTagName("aminoAcidSequence").getLength() != 0){
            log.severe("Error! SequenceML contains aminoAcidSequences!");
            throw new BioDOMException("Error! SequenceML contains aminoAcidSequences! This is not allowed!");
        }
        
        //Grabbing all sequence elements
        final NodeList sequenceNodes = seqML.dom.getElementsByTagName(SequenceML.ELEM_SEQUENCE);
        final ArrayList<String> idList = new ArrayList<String>();
        Map curSequenceData = null;
        String curSeqID = null;
        for(int i = 0; i < sequenceNodes.getLength(); i++){
            // creating empty rnastructure element
            idList.add(appendEmptyRNAStructure());
            // getting sequence ID
            curSeqID = ((Element)sequenceNodes.item(i)).getAttributeNS(null,SequenceML.ATT_SEQID); 
            // getting data for sequence
            curSequenceData = seqML.getSequence(curSeqID);
            // appending sequence with data to structure            
            this.appendSequence((String)curSequenceData.get("sequence"), 
                    curSeqID,(String)curSequenceData.get("name"), 
                    (List<String>)curSequenceData.get("synonyms"),
                    (String)curSequenceData.get("description"), 
                    (Integer)curSequenceData.get("sequenceType") ,idList.get(i));
        }
        
        // return the list of rnastructure ids
        return idList;
     }
     
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendSequencesFromFasta(java.lang.String, int)
     */
    public List<String> appendSequencesFromFasta(final String fasta, final int sequenceType) throws BioDOMException,IOException{
        log.config("Appending sequences from Fasta. Fasta : '" + fasta + "'");
        //checking the string
        if((fasta == null)||(fasta.equals(""))){
            log.severe("Error! Empty fasta string or null detected!");
            throw new BioDOMException("Empty fasta string! Cannot append any sequences...");
        }
        
        // initialising
        final SequenceML seqML = new SequenceML();
        seqML.appendFasta(fasta, sequenceType);
        final List<String> ids = appendSequencesFromSequenceML(seqML);
        return ids;
    }
    
    
    /*
     *  (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendFromDotBracketFasta(java.io.InputStream)
     */
    public List<String> appendFromDotBracketFasta(final Reader dotBracketFasta) 
        throws BioDOMException, IOException {
        return appendFromDotBracketFasta(dotBracketFasta,AbstractBioDOM.NUCLEICACID);  
    }
    
 
    /*
     *  (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendFromDotBracketFasta(java.io.InputStream, int)
     */
    public List<String> appendFromDotBracketFasta(final Reader dotBracketFasta, final int sequenceType)
        throws BioDOMException, IOException {
        LineNumberReader br = new LineNumberReader(new BufferedReader(dotBracketFasta));
        String line = null;
        String id = null;
        String description =  null;
        String sequence = null;
        List<String> structure = new ArrayList<String>();
        List<String> idlist = new ArrayList<String>();
        
        while ((line = br.readLine())!= null){
            //System.out.println(line);
            if (line.equals("")) {
                // skip empty line! exception otherwise!
                log.warning("Empty line detected. Skipping empty line!");
                warningBox.appendWarning(this, "Empty line (line :"+br.getLineNumber()+"detected. Skipping empty line!");
            } else {
                /* if line does NOT start with ">"
                   assuming it's contains sequence or dotbracket information
                   information */  
                if (line.charAt(0) != '>') { // 
                    if (id != null) {
                        /* if Pattern rnastructPattern matches, assuming it contains
                         * RNAStructureInformation 
                         */
                        if (rnastructPattern.matcher(line).matches()) {
                            structure.add(line);
                            /* if Pattern rnastructWithEnergyPattern matches, assuming it contains
                             * RNAStructureInformation with Energy values
                             */
                        } else if (rnastructWithEnergyPattern.matcher(line).matches()){
                            structure.add(line);
                            
                            /* else it must be sequence content */    
                        }  else {               
                            if (sequence == null) {
                                sequence = line;
                            } else {
                                throw new BioDOMException("line "+br.getLineNumber()+": More than one sequence content line per id detected.");
                            }
                        }           
                    } else {
                        throw new BioDOMException("line " + br.getLineNumber() + ": No id before structure/sequence starts!");
                    }
                /* line start with ">" or it is the last line
                   if any id exists, we've parsed an sequence before and can
                   add it to current dom */
                } else { 
                    if (id != null) {
                        String ID = appendEmptyRNAStructure();
                        if (sequence != null) {
                            appendSequence(sequence,id,description,sequenceType,ID);                           
                        }
                        if (structure.size() > 0) {
                            for (String struct : structure){
                                if (rnastructWithEnergyPattern.matcher(struct).matches()){
                                    // remove leading and ending whitespaces
                                    struct = struct.trim();
                                    // remove double blanks
                                    struct.replaceAll("\\s+"," ");
                                    String [] structenergy = struct.split(" ");
                                    appendStructure(structenergy[0],Double.parseDouble(structenergy[1]),ID);
                                } else {
                                    appendStructure(struct,ID);
                                }
                            
                            
                            }  
                        } else {
                            throw new BioDOMException("line "+br.getLineNumber()+": No structure information before next structure starts!");
                        }
                        idlist.add(ID);
                    }
                    // removing leading whitespaces and get id and description
                    final String[] idDesc = line.substring(1).trim().split(" ", 2);
                    // get id
                    if (idDesc.length > 0) {
                        id = idDesc[0];
                    } else {
                        id = generateId();
                        warningBox.appendWarning(this, "line " + br.getLineNumber()
                                + ": Structure/Sequence has no ID! Creating a unique ID!\n");
                    }
                    // get description if given
                    if (idDesc.length > 1) {
                        description = idDesc[1];
                    } else {
                        description = null;
                    }
                    // clear structure list
                    structure.clear();
                    // clear sequence
                    sequence = null; 
                }
            }
        }
        br.close();
        //process last entry
        if (id != null) {
            String ID = appendEmptyRNAStructure();
            if (sequence != null) {
                appendSequence(sequence,id,description,sequenceType,ID);                           
            }
            if (structure.size() > 0) {
                for (String struct : structure){
                    if (rnastructWithEnergyPattern.matcher(struct).matches()){
                        // remove leading and ending whitespaces
                        struct = struct.trim();
                        // remove double blanks
                        struct.replaceAll("\\s+"," ");
                        String [] structenergy = struct.split(" ");
                        appendStructure(structenergy[0],Double.parseDouble(structenergy[1]),ID);
                    } else {
                        appendStructure(struct,ID);
                    }
                
                
                }  
            } else {
                throw new BioDOMException("line "+br.getLineNumber()+": No structure information before next structure starts!");
            }
            idlist.add(ID);
        }
        
        return idlist;
    }
    
    /*
     *  (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendFromDotBracketFasta(java.lang.String)
     */
    public List<String> appendFromDotBracketFasta(final String dotBracketFasta)
        throws BioDOMException, IOException{
        return appendFromDotBracketFasta(new StringReader(dotBracketFasta),AbstractBioDOM.NUCLEICACID);
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendFromDotBracketFasta(java.lang.String, int)
     */
    public List<String> appendFromDotBracketFasta(final String dotBracketFasta, final int sequenceType) 
        throws BioDOMException,IOException{
        return appendFromDotBracketFasta(new StringReader(dotBracketFasta),sequenceType);
    }
    
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#addToNewRNAStructure(java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Double, java.lang.String, java.lang.Double, java.lang.Double)
     */
    public String addToNewRNAStructure(final String rnasequence, final String seqId,
            final String description, final int sequenceType, final String program, final String command, final String version,
            final String date, final String comment, final String shape, final Double shapeProb,
            final String structure, final Double structProb, final Double energy) throws BioDOMException {
        log.config("Called method addToNewRNAStructure...");
        // creating new rnastructure element
        final String rnastructureId = appendEmptyRNAStructure();

        // sequence element
        if (rnasequence != null) {
            appendSequence(rnasequence, seqId, description, sequenceType,
                            rnastructureId);
        }

        // comment element
        if (comment != null) {
            appendComment(comment, rnastructureId);
        }

        // shape element
        String shaperef = null;
        if (shape != null) {
            shaperef = appendShape(shape, shapeProb, rnastructureId);
        }

        // structure element
        if (structure != null) {
            appendStructure(structure, structProb, energy, shaperef,
                    rnastructureId);
        }
        
        // program element
        if (program != null) {
            appendProgram(program, command, version, date, rnastructureId);
        }

        // returning rnastructure id
        log.config("Returning rnastructureId :'" + rnastructureId + "'");
        return rnastructureId;
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendShape(java.lang.String)
     */
    public String appendShape(final String shape) throws BioDOMException{
        return appendShape(shape, null, null);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendShape(java.lang.String, java.lang.Double)
     */
    public String appendShape(final String shape, final Double shapeProb) throws BioDOMException{
        return appendShape(shape, shapeProb, null);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendShape(java.lang.String, java.lang.String)
     */
    public String appendShape(final String shape, final String rnastructure_id) throws BioDOMException{
        return appendShape(shape, null, rnastructure_id);
    }

    public String appendShape(final String shape, final Double shapeProb,
            String rnastructure_id) throws BioDOMException{
        return appendShape(shape,shapeProb,rnastructure_id,null);
    }
    
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendShape(java.lang.String, java.lang.Double, java.lang.String)
     */
    public String appendShape(final String shape, final Double shapeProb,
            String rnastructure_id, String shapeId) throws BioDOMException{
        log.config("Called appendShape. Arguments : '" + shape + "'/'" + shapeProb +"'/'" + rnastructure_id + "'");
        // checking if shape is syntactical correct
        if(!checkShape(shape)){
            log.severe("Shape '" + shape + "' is invalid!");
            throw new BioDOMException("Shape + '" + shape + "' is invalid!");
        }
        // get the correct rnastructure element, create a new one if no id was
        // given
        if (rnastructure_id == null) {
            warningBox.appendWarning(this, "No rnastructure id given while appending shape '" + shape + "'! Creating new rnastructure element.");
            rnastructure_id = appendEmptyRNAStructure();
        }else{
            // check if this shape does already exist in the given rnastructure
            if(rnastructureHash.containsKey(rnastructure_id)){
                final Hashtable<String, Hashtable<String, Object>> superHash = rnastructureHash.get(rnastructure_id); 
                final Hashtable<String, Object> shapes = superHash.get(SHAPESHASHNAME);
                if(shapes.containsKey(shape)){
                    // return the id... shape is already in the rnastructure
                    return (String)shapes.get(shape);
                }
            }else{
                // fatal error
                log.severe("Rnastructure_id not contained in Hash! Fatal Error! Id : '" + rnastructure_id + "'");
                throw new BioDOMException("Referencing an invalid rnastructure : '" + rnastructure_id +"'");
            }
        }
        final Element rnastructureElem = dom.getElementById(
                rnastructure_id);

        // construct shape element
        final Element shapeElem = dom.createElementNS(NAMESPACE,ELEM_SHAPE);
        shapeElem.setTextContent(shape);

        // setting attributes that are not empty
        if (shapeProb != null) {
            shapeElem.setAttributeNS(null,ATT_PROBABILITY, shapeProb.toString());
        }
        if (shapeId == null) {
            shapeId = generateId();
            log.info("Create new shape id "+shapeId);
        }
        shapeElem.setAttributeNS(null,ATT_ID, shapeId);
        shapeElem.setIdAttributeNS(null,ATT_ID, true);

        // adding shape to rnastructure
        final NodeList tmpProgramNode = rnastructureElem.getElementsByTagName(ELEM_PROGRAM);
        if((tmpProgramNode != null) && (tmpProgramNode.getLength() > 0)){
            // there is a program element in the rnastructure element
            // elements have to be rearangend so program element is last
        	final Node removedProgram = rnastructureElem.removeChild(tmpProgramNode.item(0));
            rnastructureElem.appendChild(shapeElem);
            rnastructureElem.appendChild(removedProgram);
        }else{
            // no program element, no need to rearange elements
            rnastructureElem.appendChild(shapeElem);
        }
        
        // adding shape to hash...
        final Hashtable<String, Hashtable<String, String>> superHash = rnastructureHash.get(rnastructure_id);
        final Hashtable<String,String> elementHash = superHash.get(SHAPESHASHNAME);
        elementHash.put(shape, shapeId); //shape is the id in the hash NOT the id... ;)
        
        // returning shape_id
        log.config("Returning shapeId : '" + shapeId + "'");
        return shapeId;
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendStructure(java.lang.String)
     */
    public void appendStructure(final String dotBracket) throws BioDOMException{
        appendStructure(dotBracket, null, null, null, null);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendStructure(java.lang.String, java.lang.String)
     */
    public void appendStructure(final String dotBracket, final String rnastructure_id) throws BioDOMException{
        appendStructure(dotBracket, null, null, null, rnastructure_id);
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendStructure(java.lang.String, java.lang.Double)
     */
    public void appendStructure(final String dotBracket, final Double energy) throws BioDOMException{
        appendStructure(dotBracket, null, energy, null, null);
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendStructure(java.lang.String, java.lang.Double, java.lang.String)
     */
    public void appendStructure(final String dotBracket, final Double energy, final String rnastructure_id) throws BioDOMException{
        appendStructure(dotBracket, null, energy, null, rnastructure_id);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendStructure(java.lang.String, java.lang.Double, java.lang.Double)
     */
    public void appendStructure(final String dotBracket, final Double structProb,
    		final Double energy) throws BioDOMException{
        appendStructure(dotBracket, structProb, energy, null, null);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendStructure(java.lang.String, java.lang.Double, java.lang.Double, java.lang.String)
     */
    public void appendStructure(final String dotBracket, final Double structProb,
    		final Double energy, final String rnastructure_id) throws BioDOMException{
        appendStructure(dotBracket, structProb, energy, null,
                rnastructure_id);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendStructure(java.lang.String, java.lang.String, java.lang.String)
     */
    public void appendStructure(final String dotBracket, final String shaperef,
    		final String rnastructure_id) throws BioDOMException{
        appendStructure(dotBracket, null, null, shaperef, rnastructure_id);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendStructure(java.lang.String, java.lang.Double, java.lang.Double, java.lang.String, java.lang.String)
     */
    public void appendStructure(final String dotBracket, final Double structProb,
    		final Double energy, final String shaperef, String rnastructure_id) throws BioDOMException{
        log.config("Called appendStructure. Arguments : '" + dotBracket + "'/'" + structProb + "'/'" + energy + "'/'" + shaperef + "'/'" + rnastructure_id + "'");
        
        // check if the structure is syntactical correct
        if(!checkDotbracket(dotBracket)){
            log.severe("Error: Cannot create structure element. Dotbracket sting is not valid!");
            throw new BioDOMException("Cannot create structure element. '" + dotBracket + "' is no valid dotbracket structure!");
        }
        
        // get the correct rnastructure element, create a new one if no id was
        // given
        if (rnastructure_id == null) {
            warningBox.appendWarning(this, "No rnastructure id given while appending structure '" + dotBracket + "'! Creating new rnastructure element.");
            rnastructure_id = appendEmptyRNAStructure();
        }else{
            // check if this shape does already exist in the given rnastructure
            if(!rnastructureHash.containsKey(rnastructure_id)){
                log.severe("Error! Given rnastructure id '" + rnastructure_id +"' does not exist!");
                throw new BioDOMException("Error! Given rnastructure id '" + rnastructure_id +"' does not exist!");
            }
        }
        final Element rnastructureElem = dom.getElementById(rnastructure_id);                                   

        // construct structure element
        final Element structureElem = dom.createElementNS(NAMESPACE,ELEM_STRUCTURE);
        structureElem.setTextContent(dotBracket); // this one is required!

        // adding all attributes that are not empty
        if (structProb != null) {
            structureElem.setAttributeNS(null,ATT_PROBABILITY, structProb.toString());
        }
        if (energy != null) {
            structureElem.setAttributeNS(null,ATT_ENERGY, energy.toString());
        }
        if (shaperef != null) {
            structureElem.setAttributeNS(null,ATT_SHAPEREF, shaperef);
        }

        // adding structure to rnastructure
        final NodeList tmpProgramNode = rnastructureElem.getElementsByTagName(ELEM_PROGRAM);
        if((tmpProgramNode != null) && (tmpProgramNode.getLength() != 0)){
            // there is a program element in the rnastructure element
            // elements have to be rearangend so program element is last
        	final Node removedProgramNode = rnastructureElem.removeChild(tmpProgramNode.item(0));
            rnastructureElem.appendChild(structureElem);
            rnastructureElem.appendChild(removedProgramNode);
        }else{
            // no program element, no need to rearange elements
            rnastructureElem.appendChild(structureElem);
        }
        
        log.config("Finished appendStructure...");
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendSequence(java.lang.String, int)
     */
    public void appendSequence(final String rnasequence, final int sequenceType) throws BioDOMException{
        this.appendSequence(rnasequence, null, null, null, null, sequenceType,null);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendSequence(java.lang.String, java.lang.String, int)
     */
    public void appendSequence(final String rnasequence, final String seqId, final int sequenceType) throws BioDOMException{
        this.appendSequence(rnasequence, seqId, null, null, null, sequenceType,null);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.String, int)
     */
    public void appendSequence(final String rnasequence, final String seqId,
    		final String description, final int sequenceType) throws BioDOMException{
        this.appendSequence(rnasequence, seqId, null, null, description, sequenceType,null);
    }
 
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.String, int, java.lang.String)
     */
    public void appendSequence(final String rnasequence, final String seqId,
    		final String description, final int sequenceType, final String rnastructure_id) throws BioDOMException{
        this.appendSequence(rnasequence, seqId, null, null, description, sequenceType, rnastructure_id);
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String)
     */
    public void appendSequence(final String rnasequence, final String seqId, final String sequencename,
    		final String description, final int sequenceType, final String rnastructure_id) throws BioDOMException{
        this.appendSequence(rnasequence, seqId, sequencename, null, description, sequenceType, rnastructure_id);
    }
    
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.String, java.util.List<String>, java.lang.String, int, java.lang.String)
     */
    public void appendSequence(final String rnasequence, String seqId, 
    		final String sequencename, final List<String> synonyms, final String description, 
    		final int sequenceType, String rnastructure_id) throws BioDOMException{
        log.config("Called appendSequence. Arguments : '" + rnasequence + "'/'" + seqId + "'/'" + description + "'/'" + rnastructure_id +"'");
        
        // check for invalid sequence data
        if(((rnasequence == null)||(rnasequence.equals("")))&&(sequenceType != EMPTYSEQUENCE)){
            log.severe("Error! Empty sequence given while using sequenceType " + sequenceType + "!");
            throw new BioDOMException("Error! Empty sequence given while using sequenceType " + sequenceType + "!");
        }

        // get the correct rnastructure element, create a new one if no id was
        // given
        if (rnastructure_id == null) {
            warningBox.appendWarning(this, "No rnastructure id given while appending sequence '" + rnasequence + "'! Creating new rnastructure element.");
            rnastructure_id = appendEmptyRNAStructure();
        }else{
            // check if this shape does already exist in the given rnastructure
            if(!rnastructureHash.containsKey(rnastructure_id)){
                log.severe("Error! Given rnastructure id '" + rnastructure_id +"' does not exist!");
                throw new BioDOMException("Error! Given rnastructure id '" + rnastructure_id +"' does not exist!");
            }
        }
        final Element rnastructureElem = dom.getElementById(rnastructure_id);

        // construct sequence element
        final Element sequenceElem = dom.createElementNS(NAMESPACE,ELEM_SEQUENCE);
        
        // add the name element
        if (sequencename != null) {
        	final Element nameElem = dom.createElementNS(NAMESPACE,ELEM_NAME);
            nameElem.setTextContent(sequencename);
            sequenceElem.appendChild(nameElem);
        }
        
        // add the synonyms
        if(synonyms != null){
            for(int i = 0; i < synonyms.size(); i++){
            	final Element synonymElement = dom.createElementNS(NAMESPACE,ELEM_SYNONYMS);
                synonymElement.setTextContent(synonyms.get(i));
                sequenceElem.appendChild(synonymElement);
            }
        }
        
        // add description element
        if (description != null) {
        	final Element descriptionElem = dom.createElementNS(NAMESPACE,ELEM_DESCRIPTION);
            descriptionElem.setTextContent(description);
            sequenceElem.appendChild(descriptionElem);
        }

        // add the sequence
        if(sequenceType == NUCLEICACID){
            // nucleic Sequence
        	final Element nucleicSeqElem = dom.createElementNS(NAMESPACE,ELEM_NUCLEICACIDSEQUENCE);
            nucleicSeqElem.setTextContent(rnasequence.toUpperCase());
            sequenceElem.appendChild(nucleicSeqElem);
        }else if(sequenceType == FREE){
            // free sequence
        	final Element freeSeqElem = dom.createElementNS(NAMESPACE,ELEM_FREESEQUENCE);
            freeSeqElem.setTextContent(rnasequence.toUpperCase());
            sequenceElem.appendChild(freeSeqElem);
        }else{
            //empty sequence
        	final Element emptySeqElem = dom.createElementNS(NAMESPACE,ELEM_EMPTYSEQUENCE);
            sequenceElem.appendChild(emptySeqElem);
        }

        // adding attributes that are not empty
        if (seqId != null) {
            sequenceElem.setAttributeNS(null,ATT_SEQID, seqId);
        }else{
            warningBox.appendWarning(this, "No sequence id given while appending sequence '" + rnasequence + "'! Generating new id.");
            seqId = generateId();
            sequenceElem.setAttributeNS(null,ATT_SEQID, seqId);
        }
        final Hashtable superHash = rnastructureHash.get(rnastructure_id);
        final Hashtable seqHash = (Hashtable)superHash.get(SEQUENCEHASHNAME);
        //here we check for double sequence IDs and throw an exception if we find doubles
        if(seqHash.containsKey(seqId)){
            log.config("Error! Double seqId! '" + seqId + "'");
            throw new BioDOMException("Error! Double sequence id! SeqId: '" + seqId + "'");
        }
        //otherwise, all is well and we can add this sequence  ID
        seqHash.put(seqId, sequenceType);

        // adding sequence to rnastructure
        if(rnastructureElem.getFirstChild() != null){
            rnastructureElem.insertBefore(sequenceElem, rnastructureElem.getFirstChild());
        }else{
            rnastructureElem.appendChild(sequenceElem);
        }
        log.config("appendSequence finished successful...");
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendProgram(java.lang.String, java.lang.String)
     */
    public void appendProgram(final String program, final String rnastructure_id) throws BioDOMException{
        this.appendProgram(program, null, null, null, rnastructure_id);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendProgram(java.lang.String, java.lang.String, java.lang.String)
     */
    public void appendProgram(final String program, final String command,
    		final String rnastructure_id) throws BioDOMException{
        this.appendProgram(program, command, null, null, rnastructure_id);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendProgram(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void appendProgram(final String program, final String command, final String version,
    		final String rnastructure_id) throws BioDOMException{
        this.appendProgram(program, command, version, null, rnastructure_id);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendProgram(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void appendProgram(final String program, final String command, final String version,
    		final String date, String rnastructure_id) throws BioDOMException{
        log.config("Called appendProgram. Argument : '" + program + "'/'" + command + "'/'" + version + "'/'" + date + "'/'" +rnastructure_id + "'");
        // get the correct rnastructure element, create a new one if no id was
        // given
        if (rnastructure_id == null) {
            warningBox.appendWarning(this, "No rnastructure id given while appending program element! Creating new rnastructure element.");
            rnastructure_id = appendEmptyRNAStructure();
        }else{
            // check if this shape does already exist in the given rnastructure
            if(!rnastructureHash.containsKey(rnastructure_id)){
                log.severe("Error! Given rnastructure id '" + rnastructure_id +"' does not exist!");
                throw new BioDOMException("Error! Given rnastructure id '" + rnastructure_id +"' does not exist!");
            }
        }
        final Element rnastructureElem = dom.getElementById(rnastructure_id);

        // constructing program element
        final Element programElem = dom.createElementNS(NAMESPACE,ELEM_PROGRAM);
        programElem.setTextContent(program);

        // adding the attributes that are not empty
        if (command != null) {
            programElem.setAttributeNS(null,ATT_COMMAND, command);
        }
        if (version != null) {
            programElem.setAttributeNS(null,ATT_VERSION, version);
        }
        if (date != null) { 
            if(!checkDate(date)){
                log.severe("Date invalid : '" + date + "'");
                throw new BioDOMException("Invalid date string submitted: '" + date + "'! Format YYYY-MM-DD required!");
            }
            programElem.setAttributeNS(null,ATT_DATE, date);
        }

        // adding program to rnastructure
        rnastructureElem.appendChild(programElem);
        log.config("Finished appendProgram ...");
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendComment(java.lang.String, java.lang.String)
     */
    public void appendComment(final String comment, String rnastructure_id) throws BioDOMException{
        log.config("Called appendComment. Arguments :'" + comment + "'/'" + rnastructure_id + "'");
        // get the correct rnastructure element, create a new one if no id was
        // given
        if (rnastructure_id == null) {
            warningBox.appendWarning(this, "No rnastructure id given while appending comment '" + comment + "'! Creating new rnastructure element.");
            rnastructure_id = appendEmptyRNAStructure();
        }else{
            // check if this shape does already exist in the given rnastructure
            if(!rnastructureHash.containsKey(rnastructure_id)){
                log.severe("Error! Given rnastructure id '" + rnastructure_id +"' does not exist!");
                throw new BioDOMException("Error! Given rnastructure id '" + rnastructure_id +"' does not exist!");
            }
        }
        final Element rnastructureElem = dom.getElementById(rnastructure_id);

        // constructing the comment
        final Element commentElem = dom.createElementNS(NAMESPACE,ELEM_COMMENT);
        commentElem.setTextContent(comment);

        // adding comment to rnastructure
        if(rnastructureElem.getFirstChild() != null){
            rnastructureElem.insertBefore(commentElem, rnastructureElem.getFirstChild());
            final NodeList tmpSequenceNodeList = rnastructureElem.getElementsByTagName(ELEM_SEQUENCE);
            if((tmpSequenceNodeList!=null)&&(tmpSequenceNodeList.getLength() > 0)){
            	final Node tmpSequenceNode = rnastructureElem.removeChild(tmpSequenceNodeList.item(0));
                rnastructureElem.insertBefore(tmpSequenceNode, commentElem);
            }
        }else{        
            rnastructureElem.appendChild(commentElem);
        }
        log.config("Finished appendComment...");
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendEmptyRNAStructure()
     */
    public String appendEmptyRNAStructure() {
        log.config("Called appendEmptyRNAStructure...");
        // creates an empty rnastructure element
        final Element rnastructureElem = dom.createElementNS(NAMESPACE,ELEM_RNASTRUCTURE);
        log.config("Created rnastructure element");
        final String idString = generateId();
        log.config("Generated id");
        rnastructureElem.setAttributeNS(null,ATT_ID, idString);
        log.config("Id attribute set");
        rnastructureElem.setIdAttributeNS(null,ATT_ID, true);                
        log.config("Id attribute declared to be an id..");
        final Element rootElem = dom.getDocumentElement();
        log.config("Adding rnastructure to the rootelement");
        rootElem.appendChild(rnastructureElem);
        // adding id to hash
        final Hashtable<String, Hashtable> superHash = new Hashtable<String, Hashtable>();
        final Hashtable sequenceHash = new Hashtable();
        final Hashtable shapesHash = new Hashtable();
        superHash.put(SEQUENCEHASHNAME, sequenceHash);
        superHash.put(SHAPESHASHNAME, shapesHash);
        rnastructureHash.put(idString, superHash);
        log.config("Returning rnastructureId : '" + idString + "'");
        return idString;
    }

    /*
     *  (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#toDotBracketFasta()
     */
    public String toDotBracketFasta() throws BioDOMException{
        StringBuffer retDotBracketFasta = new StringBuffer();
        final List<String> rnastructures = getRnastructureIds();
        for(String rnastructure : rnastructures){
            final Hashtable<String, Object> sequence = getSequence(rnastructure);
            final List<Hashtable<String, Object>> structures = getStructures(rnastructure);

            // start with fasta header
            retDotBracketFasta.append('>');
            // check if this structure have a sequence;
            if (sequence != null) {
                // build complete header 
            
                StringBuffer header = new StringBuffer();
                if ( sequence.containsKey("seqID")) {
                    header.append(sequence.get("seqID"));
                }
                if ( sequence.containsKey("name")) {
                    header.append(' ').append(sequence.get("name"));
                }
                if ( sequence.containsKey("description")) {
                    header.append(' ').append(sequence.get("description"));
                }
                // and add it
                retDotBracketFasta.append(header).append(LINEBREAK);
                // add sequence data
                retDotBracketFasta.append(sequence.get("sequence"));
            }
            retDotBracketFasta.append(LINEBREAK);
            
            
            for(Hashtable structure : structures){
                if(structure.containsKey("structure")){
                    retDotBracketFasta.append(structure.get("structure"));
                    if (structure.get("energy") != null){
                        retDotBracketFasta.append(' ').append(structure.get("energy"));
                    }    
                    retDotBracketFasta.append(LINEBREAK);
                }else{
                    log.severe("Error! Missing structure key in hashtable!");
                    throw new BioDOMException("Error! Missing structure key in hashtable!");
                }
            }
            
        }
        return retDotBracketFasta.toString();
    }
    
    ////////////////////////////////////////
    //////// Getter Methods
    /////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#getLastRnastructureId()
     */
    public String getLastRnastructureId() throws BioDOMException{
        try{
            log.config("Called getLastRnastructureId...");
            final NodeList rnastructureElemNodes = dom.getElementsByTagName(ELEM_RNASTRUCTURE);
            
            //check for empty rnastructureElemNodes
            if ((rnastructureElemNodes == null) || (rnastructureElemNodes.getLength() == 0)) {
                log.config("There is no last rnastructure element! Returning null!");
                return null;
            }
            //if we reach this point, the last check was ok and we can return the attribute's ID
        	final Element rnastructureElem = (Element)rnastructureElemNodes.item(rnastructureElemNodes.getLength()-1);
            log.config("Returning id : '" + rnastructureElem.getAttributeNS(null,ATT_ID) + "'");
            return rnastructureElem.getAttributeNS(null,ATT_ID);     
        }catch(Exception e){
            log.severe("An Exception occured while trying to getLastRnastructureId :" + e.getMessage());
            throw new BioDOMException("An Exception occured while trying to getLastRnastructureId :" + e.getMessage());
        }
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#getRnastructureIds()
     */
    public List<String> getRnastructureIds(){
        log.config("Getting RNAStructure Ids ...");
        final NodeList rnastructureNodes = dom.getElementsByTagName(ELEM_RNASTRUCTURE);
        log.config("Fetched all rnastructure elements...");
        List<String> ids = null;
        if((rnastructureNodes != null)&&(rnastructureNodes.getLength() > 0)){
            log.config("Found " + rnastructureNodes.getLength() + " elements ...");
            ids = new ArrayList<String>();
            for(int i =0; i < rnastructureNodes.getLength(); i++){
                log.config("Casting item " + i);
                final Element tmpElem = (Element)rnastructureNodes.item(i);
                log.config("Fetching attribute item " + i);
                ids.add(tmpElem.getAttributeNS(null,ATT_ID));
            }
        }else{
            log.severe("Error! No rnastructure elements in DOM...");
            return null;
        }
        log.config("Done. Returning idArray...");
        return ids;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#getSequenceIds(java.lang.String)
     */
    public List<String> getSequenceIds(final String rnastructure_id) throws BioDOMException{
        if(rnastructureHash.containsKey(rnastructure_id)){
        	final Element rnastructureElem = dom.getElementById(rnastructure_id);
        	final NodeList sequenceNodes = rnastructureElem.getElementsByTagName(ELEM_SEQUENCE);
            List<String> ids = null;
            if((sequenceNodes != null)&&(sequenceNodes.getLength() > 0)){
                ids = new ArrayList<String>();
                for(int i =0; i < sequenceNodes.getLength(); i++){
                	final Element tmpElem = (Element)sequenceNodes.item(i);
                    ids.add(tmpElem.getAttributeNS(null,ATT_SEQID));
                }
            }else{
                log.config("No sequence elements in DOM...");
                ids = null;
            }
            return ids;
        }
        //if we reach this point, no fitting ID was found, so we log an errot and throw an exception
        log.severe("Error: rnastructure id not found in DOM :" + rnastructure_id);
        throw new BioDOMException("Error! Given rnastructure id not found in document! " + rnastructure_id);
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#getSequence(java.lang.String)
     */
    public Hashtable<String, Object> getSequence(final String rnastructure_id) throws BioDOMException{
        log.config("Called getSequence with rnastructure id : " + rnastructure_id);
        
        //first, we check if the given id is present. If nt, we log an error and throw an exception...
        if(!rnastructureHash.containsKey(rnastructure_id)){
            log.severe("Error: rnastructure id not found in DOM :" + rnastructure_id);
            throw new BioDOMException("Error! Given rnastructure id not found in document! " + rnastructure_id);
        }
        //...otherwise, we process the request
        
        // get the correct rnastructure_id
        final Element rnastructureElem = dom.getElementById(rnastructure_id);
        
        // get the correct sequence element
        final NodeList sequenceNodes = rnastructureElem.getElementsByTagNameNS(NAMESPACE,ELEM_SEQUENCE);
        Element sequenceElem = null;
        if((sequenceNodes != null)&&(sequenceNodes.getLength() > 0)){
        	log.config("Found a sequence element...");
        	sequenceElem = (Element)sequenceNodes.item(0);
        }else{
        	// no sequence element in rnastructml element
        	log.config("No sequence element found in rnastructure element... Are you sure, that you are refering to the correct rnastructure element?");
        	return null;
        }
        
        //extract information from the sequence element
        final Hashtable<String, Object> sequenceHash = new Hashtable<String, Object>();
        
        // attributes
        if(sequenceElem.hasAttribute(ATT_SEQID)){
        	sequenceHash.put("seqID", sequenceElem.getAttributeNS(null,ATT_SEQID));
        }
        // name element
        NodeList tmpList = sequenceElem.getElementsByTagNameNS(NAMESPACE,ELEM_NAME);
        if(tmpList.getLength() > 0){
        	sequenceHash.put("name", ((Element)tmpList.item(0)).getTextContent());
        }
        
        // synonyms
        tmpList = sequenceElem.getElementsByTagNameNS(NAMESPACE,ELEM_SYNONYMS);
        if(tmpList.getLength() > 0){
        	final ArrayList<String> synonyms = new ArrayList<String>();
        	for(int j = 0; j < tmpList.getLength(); j++){
        		synonyms.add(((Element)tmpList.item(j)).getTextContent());
        	}
        	sequenceHash.put("synonyms", synonyms.toArray(new String[synonyms.size()]));
        }
        
        // description element
        tmpList = sequenceElem.getElementsByTagNameNS(NAMESPACE,ELEM_DESCRIPTION);
        if(tmpList.getLength() > 0){
        	sequenceHash.put("description", ((Element)tmpList.item(0)).getTextContent());
        }
        
        // the sequences
        tmpList = sequenceElem.getElementsByTagNameNS(NAMESPACE,ELEM_NUCLEICACIDSEQUENCE);
        if(tmpList.getLength()>0){
        	sequenceHash.put("sequence", ((Element)tmpList.item(0)).getTextContent());
        	sequenceHash.put("sequenceType", NUCLEICACID);
        	sequenceHash.put("sequenceTypeName", NUCLEICACID_NAME);
        }else{
        	tmpList = sequenceElem.getElementsByTagNameNS(NAMESPACE,ELEM_FREESEQUENCE);
        	if(tmpList.getLength() > 0){
        		sequenceHash.put("sequence", ((Element)tmpList.item(0)).getTextContent());
        		sequenceHash.put("sequenceType", FREE);
        		sequenceHash.put("sequenceTypeName", FREESEQUENCE_NAME);
        	}else{
        		sequenceHash.put("sequenceType", EMPTYSEQUENCE);
        		sequenceHash.put("sequenceTypeName", EMPTYSEQUENCE_NAME);
        	}
        }
        
        // crossrefs
        // TODO
        
        // comment
        tmpList = sequenceElem.getElementsByTagNameNS(NAMESPACE,ELEM_COMMENT);
        if(tmpList.getLength() > 0){
        	sequenceHash.put("comment", tmpList.item(0).getTextContent());
        }
        // return the result
        return sequenceHash;
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#getProgram(java.lang.String)
     */
    public Hashtable<String, String> getProgram(final String rnastructure_id) throws BioDOMException{
    	// First, we check if the ID of the requested program is present in our document
    	// If it is not, we log an error and throw an exception...
    	if(!rnastructureHash.containsKey(rnastructure_id)){
    		log.severe("Error: rnastructure id not found in DOM :" + rnastructure_id);
            throw new BioDOMException("Error! Given rnastructure id not found in document! " + rnastructure_id);
        }
    	// ...otherwise, we can process this request: 
    	final Element rnastructureElem = dom.getElementById(rnastructure_id);
    	final NodeList programNodes = rnastructureElem.getElementsByTagName(ELEM_PROGRAM);
    	// Here, we check if there are any programNodes present in our document.
    	// If there aren't any, we log a debug message and return null...
    	if((programNodes == null) || (programNodes.getLength() == 0)){
    		log.config("No program element found in rnastructure id '" + rnastructure_id + "' ... Returning null!");
    		return null;
    	}
    	//...otherwise, we continue processing this request and return the program hash in the end
    	final Element programElem = (Element)programNodes.item(0);
    	final Hashtable<String, String> programHash = new Hashtable<String, String>();
    	// programname
    	String tmpValue = programElem.getTextContent();
    	if((tmpValue != null)&&(!tmpValue.equals(""))){
    		programHash.put("programname", tmpValue);
    	}
    	// command
    	tmpValue = programElem.getAttributeNS(null,ATT_COMMAND);
    	if((tmpValue != null)&&(!tmpValue.equals(""))){
    		programHash.put("command", tmpValue);
    	}
    	// version
    	tmpValue = programElem.getAttributeNS(null,ATT_VERSION);
    	if((tmpValue != null)&&(!tmpValue.equals(""))){
    		programHash.put("version", tmpValue);
    	}
    	// date
    	tmpValue = programElem.getAttributeNS(null,ATT_DATE);
    	if((tmpValue != null)&&(!tmpValue.equals(""))){
    		programHash.put("date", tmpValue);
    	}
    	return programHash;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#getComment(java.lang.String)
     */
    public String getComment(final String rnastructure_id) throws BioDOMException{
    	// First, we check if the ID of the requested comment is present
    	// If it is not, we log an error and throw an exception...
    	if(!rnastructureHash.containsKey(rnastructure_id)){
    		log.severe("Error: rnastructure id not found in DOM :" + rnastructure_id);
    		throw new BioDOMException("Error! Given rnastructure id not found in document! " + rnastructure_id);
    	}
    	// ...otherwise, we extract the comment as requested
    	final Element rnastructureElem = dom.getElementById(rnastructure_id);
    	final NodeList commentNodeList = rnastructureElem.getElementsByTagName(ELEM_COMMENT);
    	String comment = null;
    	if((commentNodeList != null)&&(commentNodeList.getLength()>0)){
    		final Element tmpElem = (Element)commentNodeList.item(0);
    		comment = tmpElem.getTextContent();
    	}else{
    		log.config("No comment elements in DOM...");
    	}
    	return comment;
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#getStructures(java.lang.String)
     */
    public List<Hashtable<String, Object>> getStructures(final String rnastructure_id) throws BioDOMException{
    	// First, we check if the ID of the requested comment is present
    	// If it is not, we log an error and throw an exception...
    	if(!rnastructureHash.containsKey(rnastructure_id)){
    		log.severe("Error: rnastructure id not found in DOM :" + rnastructure_id);
    		throw new BioDOMException("Error! Given rnastructure id not found in document! " + rnastructure_id);
    	}
    	// ...otherwise, we can process this request: 
    	final Element rnastructureElem = dom.getElementById(rnastructure_id);
    	final NodeList structureNodeList = rnastructureElem.getElementsByTagName(ELEM_STRUCTURE);
    	// Here, we check if there are any structureNodes present
    	// If there aren't any, we log a debug message and return null...
    	if((structureNodeList == null) || (structureNodeList.getLength() == 0)){
    		log.config("No such element: structure! rnastructure_id : '" + rnastructure_id + "'");
    		return null;
    	}
    	// ...otherwise, we continue processing this request and return the requested data
    	final List<Hashtable<String, Object>> retHashArray = new ArrayList<Hashtable<String, Object>>();
    	Element tmpStructureElem;
    	for(int i = 0; i < structureNodeList.getLength(); i++){
    		retHashArray.add(new Hashtable<String, Object>());
    		tmpStructureElem = (Element)structureNodeList.item(i);
    		// shapeType
    		(retHashArray.get(i)).put("shapeType", "structure");
    		// structure
    		String tmpString = tmpStructureElem.getTextContent();
    		if((tmpString != null)&&(!tmpString.equals(""))){
    			(retHashArray.get(i)).put("structure", tmpString);
    		}
    		// energy
    		if(tmpStructureElem.hasAttribute(ATT_ENERGY)){
    			final Double tmpDouble = Double.valueOf(tmpStructureElem.getAttributeNS(null,ATT_ENERGY));
    			if((tmpString != null)&&(!tmpString.equals(""))){
    				(retHashArray.get(i)).put("energy", tmpDouble);
    			}
    		}
    		// probability
    		if(tmpStructureElem.hasAttribute(ATT_PROBABILITY)){
    			final Double tmpDouble = Double.valueOf(tmpStructureElem.getAttributeNS(null,ATT_PROBABILITY));
    			if((tmpString != null)&&(!tmpString.equals(""))){
    				(retHashArray.get(i)).put("probability", tmpDouble);
    			}
    		}
    		// shaperef
    		if(tmpStructureElem.hasAttribute(ATT_SHAPEREF)){
    			tmpString = tmpStructureElem.getAttributeNS(null,ATT_SHAPEREF);
    			if((tmpString != null)&&(!tmpString.equals(""))){
    				(retHashArray.get(i)).put("shaperef", tmpString);
    			}
    		}
    	}
    	// return filled hasharray
    	return retHashArray;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#getShapes(java.lang.String)
     */
    public List<Hashtable<String, Object>> getShapes(final String rnastructure_id) throws BioDOMException{
    	// First, we check if the ID of the requested shapes is present
    	// If it is not, we log an error and throw an exception...
    	if(!rnastructureHash.containsKey(rnastructure_id)){    	
    		log.severe("Error: rnastructure id not found in DOM :" + rnastructure_id);
    		throw new BioDOMException("Error! Given rnastructure id not found in document! " + rnastructure_id);
    	}    	
    	// ...otherwise, we can process this request: 
    	final Element rnastructureElem = dom.getElementById(rnastructure_id);
    	final NodeList shapeNodeList = rnastructureElem.getElementsByTagName(ELEM_SHAPE);
    	// Here, we check if there are any shapeNodes present
    	// If there aren't any, we log a debug message and return null...
    	if((shapeNodeList == null) || (shapeNodeList.getLength() == 0)){
    		log.config("No shapes in rnastructure element with id : '" + rnastructure_id + "'");
    		return null;
    	}
    	// ...otherwise, we continue processing this request and return the requested data
    	final ArrayList<Hashtable<String, Object>> retHashArray = new ArrayList<Hashtable<String, Object>>();
    	Element tmpShapeElem;
    	for(int i = 0; i < shapeNodeList.getLength(); i++){
    		retHashArray.add(new Hashtable<String, Object>());
    		tmpShapeElem = (Element)shapeNodeList.item(i);
    		// shapeType
    		(retHashArray.get(i)).put("shapeType", "shape");
    		// shape
    		String tmpString = tmpShapeElem.getTextContent();
    		if((tmpString != null)&&(!tmpString.equals(""))){
    			(retHashArray.get(i)).put("shape", tmpString);
    		}
    		// probability
    		if(tmpShapeElem.hasAttribute(ATT_PROBABILITY)){
    			final Double tmpDouble = Double.valueOf(tmpShapeElem.getAttributeNS(null,ATT_PROBABILITY));
    			if((tmpString != null)&&(!tmpString.equals(""))){
    				(retHashArray.get(i)).put("probability", tmpDouble);
    			}
    		}
    		// id
    		if(tmpShapeElem.hasAttribute(ATT_ID)){
    			tmpString = tmpShapeElem.getAttributeNS(null,ATT_ID);
    			if((tmpString != null)&&(!tmpString.equals(""))){
    				(retHashArray.get(i)).put("id", tmpString);
    			}
    		}
    	}
    	// return filled hasharray
    	return retHashArray;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#getStructuresAndShapes(java.lang.String)
     */
    public Hashtable<String, List<Hashtable<String, Object>>> getStructuresAndShapes(final String rnastructure_id) throws BioDOMException{
        if(rnastructureHash.containsKey(rnastructure_id)){
        	final List<Hashtable<String, Object>> shapesHashArray = getShapes(rnastructure_id);
        	final List<Hashtable<String, Object>> structuresHashArray = getStructures(rnastructure_id);
        	final Hashtable<String, List<Hashtable<String, Object>>> shapesAndStructuresHash = new Hashtable<String, List<Hashtable<String, Object>>>();
            
            if(shapesHashArray != null){
                shapesAndStructuresHash.put("shapes", shapesHashArray);
            }
            if(structuresHashArray != null){
                shapesAndStructuresHash.put("structures", structuresHashArray);
            }
            return shapesAndStructuresHash; 
        }
        // If we reach this point, there was no fitting rnastructure_id in the rnastructureHash.
        // Therefore, we log an error and throw an exception
        log.severe("Error: rnastructure id not found in DOM :" + rnastructure_id);
        throw new BioDOMException("Error! Given rnastructure id not found in document! " + rnastructure_id);
    }
 
    //////////////////////////////////
    ////// Private Methods
    //////////////////////////////////
    /**
     * generates an empty DOM Tree
     */
    private void initDOM() {   
        dom = DB.newDocument();
        final Element root = dom.createElementNS(NAMESPACE, ELEM_RNASTRUCTML);
        root.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,"schemaLocation", NAMESPACE+" "+NSLOCATION);
        dom.appendChild(root);
        dom.normalizeDocument();
        log.config("DOM ready...");
    }
  
    /**
     * This method checks a shape for syntactical correctness
     * @param shapeString
     * @return true if not empty and syntactical correct, false otherwise
     * @throws BioDOMException
     */
    private boolean checkShape(final String shapeString) throws BioDOMException{
        return checkStructureOrShape(shapeString, SHAPE);
    }
    
    /**
     * This method checks a dotbracket string for syntactical correctness 
     * @param dotbracketString
     * @return true if not empty and syntactical correct, false otherwise 
     */
    private boolean checkDotbracket(final String dotbracketString) throws BioDOMException{
        return checkStructureOrShape(dotbracketString, STRUCTURE);
    }
    
    /**
     * This method checks a dotbracket string or a shape string for syntactical
     * correctness
     * @param checkString   - structure in dotbracket format or shape
     * @param type          - STRUCTURE for structure, SHAPE for shape
     * @return true if not empty and syntactical correct, false otherwise
     */
    private boolean checkStructureOrShape(final String checkString, final int type) throws BioDOMException{
        log.config("Checking '" + checkString +"' for syntactical correctness...");
        if((checkString == null)||(checkString.equals(""))){
            // checks if structure is empty
            log.severe("Error: dotbracket string is empty or null!");
            return false;
        }
        final char[] tokens = checkString.toCharArray();
        if((type != STRUCTURE)&&(type != SHAPE)){
            log.severe("Invalid type used in checkStructureOrShape! Type :'" + type + "'");
            throw new BioDOMException("Error! Invalid type in checkStructureOrShape! Type : '" + type + "'");
        }
        int counterSimple = 0;    // counter for '('-brackets
        int counterEdged = 0;     // counter for '['-brackets
        int counterTwisted = 0;   // counter for '{'-brackets
        int counterPointed = 0;   // counter for '<'-brackets
        String currenttoken;

        for(int i = 0; i < tokens.length; i++){
            currenttoken = "" + tokens[i];
            //log.config("current token : '" + currenttoken + "'");
            switch (tokens[i]){
                // incr opening brackets counter
                case '(' :  counterSimple++; break;
                case '[' :  counterEdged++; break;
                case '{' :  counterTwisted++; break;
                case '<' :  counterPointed++; break;
                
                // decr the brackets counter 
                case ')' :  counterSimple--;
                            if(counterSimple < 0){
                                // to few opening brackets
                                log.severe("Closing bracket detected when an opening bracket was expected... :'" + currenttoken + "'");
                                return false;
                            }
                            break;
                case ']' :  counterEdged--;
                            if(counterEdged < 0){
                                // to few opening brackets
                                log.severe("Closing bracket detected when an opening bracket was expected... :'" + currenttoken + "'");
                                return false;
                            }
                            break;
                case '}' :  counterTwisted--;
                            if(counterTwisted < 0){
                                // to few opening brackets
                                log.severe("Closing bracket detected when an opening bracket was expected... :'" + currenttoken + "'");
                                return false;
                            }
                            break;
                case '>' :  counterPointed--;
                            if(counterPointed < 0){
                                // to few opening brackets
                                log.severe("Closing bracket detected when an opening bracket was expected... :'" + currenttoken + "'");
                                return false;
                            }
                            break;
                case '.' :  // ignore case STRUCTURE
                            if(type == STRUCTURE) break; 
                            //if we reach this point, there is an error and return false...
                            log.severe("Invalid symbol detected... (Structure mode) : '" + currenttoken + "'");
                            return false;
                case '_' :  // ignore case SHAPE
                            if(type == SHAPE) break;
//                          if we reach this point, there is an error and return false...
                            log.severe("Invalid symbol detected... (Shape mode): '" + currenttoken + "'");
                            return false;
                default  :  // invalid symbol found
                            log.severe("Invalid symbol detected... : '" + currenttoken + "'");
                            return false;
            }
        }
        if(counterSimple+counterEdged+counterTwisted+counterPointed > 0){
            // check if some brackets are still open...
            log.severe("To many opening brackets... Structure is not valid!");
            return false;
        }
        log.config("Dotbracket string is valid.");
        return true;
    }
    
    /**
     * Checks if a string is a date in format YYYY-MM-DD 
     * @param datestring    - String to check   
     * @return boolean      - true if string is valid, false otherwise
     */
    private boolean checkDate(final String datestring) {
    	final Pattern datePattern = Pattern.compile("^\\d\\d\\d\\d-\\d\\d-\\d\\d$");
    	final Matcher m = datePattern.matcher(datestring);
    	
    	//If the datestring matches our pattern, we can do some more checks
        if (m.matches()) {
        	final String monthString = datestring.substring(5, 7);
        	final String daysString = datestring.substring(8, 10);
        	final int month = Integer.parseInt(monthString);
            final int days = Integer.parseInt(daysString);
            //here, we check if the datestring conforms to usual western european calendaring rules for months and days:-)!
            //If anything goes wrong here, we return 'false'
            if ((month > 12) || (days > 31)) {
                log.severe("Invalid date! Number for month or day to big :" + datestring);
                return false;
            }
            //If the string passed the last test, it should be a somewhat correct datestring, so we return 'true'
            return true;
        }
    	//...otherwise, we can log an error and return 'false'
        log.severe("Invalid date! Pattern does not match! : '" + datestring + "'");
        return false;
    }
    
    /**
     * private method that generates the rnastructureHash
     * @throws BioDOMException
     */
    private void generateRnastructureHash() throws BioDOMException{
        try{
            // adding ids to hash
        	final NodeList rnastructureNodes = dom.getElementsByTagName(ELEM_RNASTRUCTURE);
            Element tmpRnastructureElem = null;
            Element tmpSubElem = null;
            NodeList tmpNodes = null;
            String idString = null;
            // evaluating rnastructure by rnastructure
            for(int i=0; i < rnastructureNodes.getLength(); i++){
                log.config("Evaluating rnastructure element " + i);
                tmpRnastructureElem = (Element)rnastructureNodes.item(i);
                if(tmpRnastructureElem.hasAttribute("id")){
                    log.finer("rnastructure has an id");
                    idString = tmpRnastructureElem.getAttributeNS(null,ATT_ID);
                } else {
                    log.finer("rnastructure has no id");
                    idString = null;
                }
                
                // generate the hashes...
                log.config("generate the hashes ...");
                final Hashtable<String,Hashtable<String, Object>> superHash = new Hashtable<String,Hashtable<String, Object>>();
                final Hashtable<String, Object> sequenceHash = new Hashtable<String, Object>();
                final Hashtable<String, Object> shapesHash = new Hashtable<String, Object>();
                
                
                // checking the shapes
                log.config("checking the hashes");
                tmpNodes = tmpRnastructureElem.getElementsByTagName(ELEM_SHAPE);
                if((tmpNodes != null)&&(tmpNodes.getLength() > 0)){
                    for (int j = 0; j < tmpNodes.getLength(); j++){
                        tmpSubElem = (Element)tmpNodes.item(j);
                        if(tmpSubElem.hasAttribute(ATT_ID)){
                            // has id, just add it
                        	final String shapeId = tmpSubElem.getAttributeNS(null,ATT_ID);
                        	final String shape = tmpSubElem.getTextContent();
                            shapesHash.put(shape, shapeId); // shape is the key, NOT id!
                        }else{
                            // has no id, generate it...
                            warningBox.appendWarning(this, "Shape id missing in document! Generating it.");
                            final String shapeId = generateId();
                            tmpSubElem.setAttributeNS(null,ATT_ID, shapeId);
                            final String shape = tmpSubElem.getTextContent();
                            shapesHash.put(shape, shapeId); // see above
                        }
                    }
                }
                
                // checking the sequences
                log.config("checking the sequences");
                tmpNodes = tmpRnastructureElem.getElementsByTagName(ELEM_SEQUENCE);
                if((tmpNodes != null)&&(tmpNodes.getLength()>0)){
                    tmpSubElem = (Element)tmpNodes.item(0);
                    String seqID = null;
                    if(tmpSubElem.hasAttribute(ATT_SEQID)){
                        seqID = tmpSubElem.getAttributeNS(null,ATT_SEQID);
                    }else{
                        warningBox.appendWarning(this, "Sequence id missing in document! Generating it.");
                        seqID = "GENERATEDID_" + generateId();
                        tmpSubElem.setAttributeNS(null,ATT_SEQID, seqID);
                    }
                    NodeList anotherone = tmpSubElem.getElementsByTagName(ELEM_NUCLEICACIDSEQUENCE);
                    if((anotherone != null)&&(anotherone.getLength() > 0)){
                        sequenceHash.put(seqID, NUCLEICACID);
                    }else{
                        anotherone = tmpSubElem.getElementsByTagName(ELEM_FREESEQUENCE);
                        if((anotherone != null)&&(anotherone.getLength() > 0)){
                            sequenceHash.put(seqID, FREE);
                        }else{
                            sequenceHash.put(seqID, EMPTYSEQUENCE);
                        }
                    }
                }
                
                // add the subhashes to the rnastructure hash
                log.config("add the subhashes to the rnastructure hash");
                superHash.put(SEQUENCEHASHNAME, sequenceHash);
                superHash.put(SHAPESHASHNAME, shapesHash);
                if((idString != null)&&(!idString.equals(""))){
                    // id attribute given -> make it an ID
                    log.finer("found an id - use it");
                    tmpRnastructureElem.setIdAttributeNS(null,ATT_ID, true);
                    log.config("Found id for rnastructure element : '" + idString + "'. Added to Hash...");
                    rnastructureHash.put(idString, superHash);
                }else{
                    // no id attribute in rnastructure element -> generate one
                    log.finer("id is missing - generate one");
                    warningBox.appendWarning(this, "rnastructure id missing in document! Generating it.");
                    idString = generateId();
                    tmpRnastructureElem.setAttributeNS(null,ATT_ID, idString);
                    tmpRnastructureElem.setIdAttributeNS(null,ATT_ID, true);
                    log.config("Generated id for rnastructure element : '" + idString + "'. Added to Hash...");
                    rnastructureHash.put(idString, superHash);
                }
            }
        } catch (Exception e){
            log.severe("Internal Parsing Error! Message :" + e.getMessage());
            throw new BioDOMException("Internal Parsing Error! Error message: " + e.getMessage());
        }
    }
}
