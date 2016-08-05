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

/**
 * RNAStructAlignmentML -- XML representation of rna secondary structure
 * alignments, uses dotBracket
 * 
 * Namespace  : NAMESPACE
 * XSD-Schema : NSLOCATION
 * 
 * @author Kai Loewenthal <kloewent@techfak.uni-bielefeld.de>
 * @version 1.0 
 * TODO : 1) Current version does not implement crossrefs!
 * 
 * @throws BioDOMException
 *             on failure / fatal error
 */
public class RNAStructAlignmentML extends AbstractBioDOM implements RNAStructAlignmentMLInterface {

    // XML element tag names
    protected static final String ELEM_RNASTRUCTALIGNMENTML = "rnastructAlignmentML";
    protected static final String ELEM_RNASTRUCTUREALIGNMENT = "rnastructurealignment";
    protected static final String ELEM_SEQUENCE = "sequence";
    protected static final String ELEM_NAME = "name";
    protected static final String ELEM_SYNONYMS = "synonyms";
    protected static final String ELEM_DESCRIPTION = "description";
    protected static final String ELEM_ALIGNEDNUCLEICACIDSEQUENCE = "alignedNucleicAcidSequence";
    protected static final String ELEM_ALIGNEDFREESEQUENCE = "alignedFreeSequence";
    protected static final String ELEM_EMPTYSEQUENCE = "emptySequence";
    protected static final String ELEM_STRUCTURE = "structure";
    protected static final String ELEM_CROSSREFS = "crossRefs";
    protected static final String ELEM_COMMENT = "comment";
    protected static final String ELEM_CONSENSUS = "consensus";
    protected static final String ELEM_CONSENSUS_STRUCTURE = "structure";
    protected static final String ELEM_CONSENSUS_STRUCTUREPROBS = "structureprobabilities";
    protected static final String ELEM_CONSENSUS_STRUCTUREPROBS_PT = "pt";
    protected static final String ELEM_CONSENSUS_SEQUENCE = "sequence";
    protected static final String ELEM_CONSENSUS_PROBABILITIES = "probabilities";
    protected static final String ELEM_PROGRAM = "program";

    // XML attribute names
    protected static final String ATT_SCORE = "score";
    protected static final String ATT_ID = "id";
    protected static final String ATT_SEQID = "seqID";
    protected static final String ATT_ENERGY = "energy";
    protected static final String ATT_PROBABILITY = "probability";
    protected static final String ATT_COMMAND = "command";
    protected static final String ATT_VERSION = "version";
    protected static final String ATT_DATE = "date";
    protected static final String ATT_CONSENSUS_PROBPOSA = "a";
    protected static final String ATT_CONSENSUS_PROBPOSB = "b";
    protected static final String ATT_CONSENSUS_PROB = "probability";
    protected static final String ATT_CONSENSUS_ID = "id";
    
    // logger
    private static Logger log = Logger.getLogger(RNAStructAlignmentML.class.toString());

    // List for rnastructure ids
    private List<String> rnastructurealignmentidsList = new ArrayList<String>();

    // ///////////////////////
    // Contructors
    // //////////////////////

    // NO INPUT
    /**
     * creates a new RNAStructAlignmentML object for processing DOM Documents as
     * NSLOCATION docs
     * 
     * @exception BioDOMException
     *                on failure / fatal error
     */
    public RNAStructAlignmentML() throws BioDOMException {
    	this(null,null);
    }

    // CATALOGPATH INPUT
    /**
     * creates a new RNAStructAlignmentML object for processing DOM Documents as
     * NSLOCATION docs
     * 
     * @param catalogpropertyfile
     *            path of Catalog.properties file
     * @exception BioDOMException
     *                on failure
     */
    public RNAStructAlignmentML(String catalogpropertyfile)
            throws BioDOMException {
    	this(catalogpropertyfile, null);
    }

    // DOM INPUT
    /**
     * creates a new RNAStructAlignmentML object for processing DOM Documents as
     * NSLOCATION docs
     * 
     * @param submitted_dom
     *            DOM for processing
     * @exception BioDOMException
     *                on failure
     */
    public RNAStructAlignmentML(Document submitted_dom) throws BioDOMException {
        this(null,submitted_dom);
    }

    // DOM & CATALOGPATH INPUT
    /**
     * creates a new RNAStructAlignmentML object for processing DOM Documents as
     * NSLOCATION docs
     * 
     * @param catalogpropertyfile
     *            path of Catalog.properties file
     * @param submitted_dom
     *            DOM for processing
     * @exception BioDOMException
     *                on failure
     */
    public RNAStructAlignmentML(String catalogpropertyfile, Document submitted_dom)
            throws BioDOMException {
        super(catalogpropertyfile);      
        isNillable = true;
        if (submitted_dom != null) {
        	this.setDom(submitted_dom);
        } else {
        	initDOM();
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
        generateRnastructurealignmentHash();
    }

    /*
     *  (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#setDom(org.w3c.dom.Document)
     */
    public void setDom(final Document dom) throws BioDOMException {
        super.setDom(dom);

        generateRnastructurealignmentHash();
    }

    
    // //////////////////////
    // Converter
    // /////////////////////

    // TODO Add more stuff here!
    // TODO MARNA format converter
    
    
    public String appendFromAlignedDotBracketFasta(String alignedDotBracketFasta) throws BioDOMException, IOException {
    	return appendFromAlignedDotBracketFasta(alignedDotBracketFasta, AbstractBioDOM.NUCLEICACID);
    }
    
    public String appendFromAlignedDotBracketFasta(final String alignedDotBracketFasta, final int sequenceType) throws BioDOMException, IOException { 
    	return appendFromAlignedDotBracketFasta(new StringReader(alignedDotBracketFasta), sequenceType);
    }
    
    public String appendFromAlignedDotBracketFasta(Reader alignedDotBracketFasta) throws BioDOMException, IOException {
    	return appendFromAlignedDotBracketFasta(alignedDotBracketFasta, AbstractBioDOM.NUCLEICACID);
    }    
    
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructMLInterface#appendFromDotBracketFasta(java.lang.String, int)
     */   
    public String appendFromAlignedDotBracketFasta(Reader alignedDotBracketFasta, int sequenceType) throws BioDOMException, IOException {
    	LineNumberReader br = new LineNumberReader(new BufferedReader(alignedDotBracketFasta));
    	String current = null;
    	String currentFasta = null;
    	String currentAlignedDotBracket = null;
    	final String rnastructurealignment_id = appendEmptyRNAStructureAlignment();
    	
    	while ((current = br.readLine())!= null) {
    		if(current.charAt(0) == '>'){
    			// new fasta 
    			log.config("Found new fasta start in line '" + current + "'");
    			if(currentFasta != null){
    				if(currentAlignedDotBracket == null){
    					// error!
    					log.severe("Format Error! AlignedDotBracket string must follow sequence string! :" + alignedDotBracketFasta);
    					throw new BioDOMException("Format Error! AlignedDotBracket string must follow sequence string! :" + alignedDotBracketFasta);
    				}
    				
    				// parse Fasta using the SequenceML parser
    				final SequenceML seqML = new SequenceML();
    				seqML.appendFasta(currentFasta, SequenceML.FREE);
    				final List<String> seqIds = seqML.getIDlist();
    				
    				// checking result
    				if(seqIds.size() != 1){
    					log.severe("Error! Invalid alignedDotBracket string : " + alignedDotBracketFasta);
    					throw new BioDOMException("Error! Invalid alignedDotBracket string : " + alignedDotBracketFasta);
    				}
    				
    				// getting stuff ready
    				final Map seqHash = seqML.getSequence(seqIds.get(0));
    				final String id = (String)seqHash.get("id");
    				final String description = (String)seqHash.get("description");
    				final String sequence = (String)seqHash.get("sequence");
    				
    				// check if both are same length
    				if(sequence.length() != currentAlignedDotBracket.length()){
    					log.severe("Error! Sequence and alignedDotBracket do not have same length! : " + alignedDotBracketFasta);
    					throw new BioDOMException("Error! Sequence and alignedDotBracket do not have same length! : " + alignedDotBracketFasta);
    				}
    				
    				// appending sequence and structure to rnastructurealignment
    				appendSequence(id, null, null, description, sequence, 
    						sequenceType, currentAlignedDotBracket, null, null, 
    						null, rnastructurealignment_id);
    				
    			}
    			currentAlignedDotBracket = null;
    			currentFasta = current;
    		}else if(current.matches("^[\\.()<>\\[\\]{}-]+$")){ // test matcher!!
    			// aligneddotbracket line
    			log.config("Found new alignedDotBracket string in line '" + current + "'");
    			if(currentAlignedDotBracket == null){
    				currentAlignedDotBracket = current;
    			}else{
    				currentAlignedDotBracket = currentAlignedDotBracket + current;
    				log.config("added to previous aligneddotbracket string : '" + currentAlignedDotBracket + "'");
    			}
    		}else{
    			// some other line -> sequence
    			log.config("Found new sequence line in line '" + current + "'");
    			if (currentFasta != null){
    				// append line
    				currentFasta = currentFasta + "\n" + current;
    			}else{
    				// error!
    				log.severe("Error! AlignedDotBracketFasta is not wellformed! :" + alignedDotBracketFasta);
    				throw new BioDOMException("Error! AlignedDotBracketFasta is not wellformed! :" + alignedDotBracketFasta);
    			}
    		}
    	}
    	// process the last unprocessed fasta
    	if(currentFasta != null){
    		if(currentAlignedDotBracket == null){
    			// error!
    			log.severe("Format Error! AlignedDotBracket string must follow sequence string! :" + alignedDotBracketFasta);
    			throw new BioDOMException("Format Error! AlignedDotBracket string must follow sequence string! :" + alignedDotBracketFasta);
    		}
    		
    		// parse Fasta using the SequenceML parser
    		final SequenceML seqML = new SequenceML();
    		seqML.appendFasta(currentFasta, SequenceML.FREE);
    		final List<String> seqIds = seqML.getIDlist();
    		
    		// checking result
    		if(seqIds.size() != 1){
    			log.severe("Error! Invalid alignedDotBracket string : " + alignedDotBracketFasta);
    			throw new BioDOMException("Error! Invalid alignedDotBracket string : " + alignedDotBracketFasta);
    		}
    		
    		// getting stuff ready
    		final Map seqHash = seqML.getSequence(seqIds.get(0));
    		final String id = (String)seqHash.get("id");
    		final String description = (String)seqHash.get("description");
    		final String sequence = (String)seqHash.get("sequence");
    		
    		// check if both are same length
    		if(sequence.length() != currentAlignedDotBracket.length()){
    			log.severe("Error! Sequence and alignedDotBracket do not have same length! : " + alignedDotBracketFasta);
    			throw new BioDOMException("Error! Sequence and alignedDotBracket do not have same length! : " + alignedDotBracketFasta);
    		}
    		
    		// appending sequence and structure to rnastructurealignment
    		appendSequence(id, null, null, description, sequence, 
    				sequenceType, currentAlignedDotBracket, null, null, 
    				null, rnastructurealignment_id);
    	}
    	return rnastructurealignment_id;
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#addNewRnastructureAlignment(java.lang.Integer, java.util.List<Hashtable>, java.util.Hashtable, java.lang.String, java.util.Hashtable)
     */
    public String addNewRnastructureAlignment(final Integer score, final List<Hashtable> sequences, final Hashtable program, final String comment, final Hashtable consensus) throws BioDOMException {
        try{
            log.config("Called addNewRnastructureAlignment...");
            if((sequences == null)||(sequences.size() < 2)){
                log.severe("Error! At least two sequence elements should be submitted");
                throw new BioDOMException("Error! At least two sequence elements should be submitted");
            }
            
            // appending empty rnastructurealignment
            log.config("Step 1: Appending empty rnastructurealignment element...");
            final String rnastructalign_id = appendEmptyRNAStructureAlignment(score);
            
            // appending the sequence elements
            log.config("Step 2: Appending the sequence elements...");
            for(int i = 0; i < sequences.size(); i++){
                log.config("Appending sequence element number " + i + "...");
                // checking inputhash and appending sequence
                final String seqID = (String)sequences.get(i).get("seqID");
                if((seqID == null)||(seqID.equals(""))){
                    log.severe("Missing or empty sequence id in sequences hash! Aborting...");
                    throw new BioDOMException("Missing or empty sequence id in sequences hash! Aborting...");
                }
                final String name = (String)sequences.get(i).get("name");
                final List<String> synonyms = (List<String>)sequences.get(i).get("synonyms");
                final String description = (String)sequences.get(i).get("description");
                final String sequence = (String)sequences.get(i).get("sequence");
                final Integer seqType = (Integer)(sequences.get(i).get("sequenceType"));
                if((seqType == null)||
                  (!(seqType.equals(FREE))&&(!seqType.equals(NUCLEICACID))&&(!seqType.equals(EMPTYSEQUENCE)))){
                    log.severe("Missing or invalid sequence type in sequences hash! Aborting...");
                    throw new BioDOMException("Missing or invalid sequence type in sequences hash! Aborting...");
                }
                final String structure = (String)sequences.get(i).get("structure");
                if((structure == null)||(structure.equals(""))){
                    log.severe("Missing or empty structure in sequence hash! Aborting...");
                    throw new BioDOMException("Missing or empty structure in sequence hash! Aborting...");
                }
                Double energy = null;
                if((sequences.get(i)).containsKey("energy")){
                    energy = (Double)(sequences.get(i)).get("energy");
                }
                Double probability = null;
                if((sequences.get(i)).containsKey("probability")){
                    probability = (Double)(sequences.get(i)).get("probability");    
                }
                String seqcomment = null;
                if((sequences.get(i)).containsKey("seqcomment")){
                    seqcomment = (String)(sequences.get(i)).get("seqcomment");
                }
                appendSequence(seqID, name, synonyms, description, sequence,
                        seqType, structure, energy, probability, seqcomment,
                        rnastructalign_id);
            }
                  
            // appending comment element if not null
            log.config("Step 4: Appending comment element if not null...");
            if((comment != null)&&(!comment.equals(""))){
                log.config("Appending comment element!");
                appendComment(comment, rnastructalign_id);
            }
            
            // appending consensus element if not null
            log.config("Step 5: Appending consensus element if not null...");
            if(consensus != null){
                log.config("Appending consensus element!");
                // TODO FIXME : Casting not safe!
                // Quoted until method is implemented
                /* appendConsensus((String)consensus.get("consensusstructure"), (Double)consensus.get("csenergy"), 
                        (Double)consensus.get("csprobability"), (List<Hashtable<String, Object>>)consensus.get("structureprobs"), 
                        (Hashtable<String, Object>)consensus.get("sequence"), (String)consensus.get("consensus_id"), 
                        rnastructalign_id);
                */
                log.warning("Warning! No consensus element added! Method not yet implemented!");
            }
            
            // appending program element if not null
            log.config("Step 3: Appending program element if not null...");
            if(program != null){
                log.config("Appending program element!");
                appendProgram((String)program.get("programname"), (String)program.get("command"), (String)program.get("version"), 
                        (String)program.get("date"), rnastructalign_id);
            }
            
            log.config("Finished adding elements. addNewRnastructureAlignment finished successful!");
            return rnastructalign_id;
        }catch(ClassCastException ce){
            log.severe("Error! ClassCastException occured! Input data invalid (probably wrong data in Hashtable):" + ce.getMessage());
            throw new BioDOMException("Error! ClassCastException occured! Input data invalid (probably wrong data in Hashtable):" + ce.getMessage());
        }
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequenceFasta(java.lang.String, java.lang.String)
     */
    public void appendSequenceFasta(final String fasta, final String structure) throws BioDOMException{
        // TODO
        log.warning("appendSequenceFasta: Method not yet implemented!");
        throw new BioDOMException("appendSequenceFasta: Method not yet implemented!");
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequence(java.lang.String, java.lang.String)
     */
    public void appendSequence(final String seqID, final String structure) throws BioDOMException{
        appendSequence(seqID, null, null, null, null, EMPTYSEQUENCE, structure, null, 
                null, null, null);
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.String)
     */
    public void appendSequence(final String seqID, final String structure, final String rnastructure_id) throws BioDOMException{
        appendSequence(seqID, null, null, null, null, EMPTYSEQUENCE, structure, null, 
                null, null, rnastructure_id);
    }    

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.Integer, java.lang.String)
     */
    public void appendSequence(final String seqID, final String sequence, final Integer seqType, final String structure) throws BioDOMException{
        appendSequence(seqID, null, null, null, sequence, seqType, structure, 
                null, null, null, null);
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
     */
    public void appendSequence(final String seqID, final String sequence, final Integer seqType, final String structure, final String rnastructure_id) throws BioDOMException{
        appendSequence(seqID, null, null, null, sequence, seqType, structure, 
                null, null, null, rnastructure_id);
    }    
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void appendSequence(final String seqID, final String seqName, final String description, final String structure) throws BioDOMException{
        appendSequence(seqID, seqName, null, description, null, EMPTYSEQUENCE, structure, 
                null, null, null, null);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void appendSequence(final String seqID, final String seqName, final String description, final String structure, final String rnastructure_id) throws BioDOMException{
        appendSequence(seqID, seqName, null, description, null, EMPTYSEQUENCE, structure, 
                null, null, null, rnastructure_id);
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.String)
     */
    public void appendSequence(final String seqID, final String seqName, final String description, final String sequence, final Integer seqType, final String structure) throws BioDOMException{
        appendSequence(seqID, seqName, null, description, sequence, seqType, 
                structure, null, null, null, null);
    }
        
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
     */
    public void appendSequence(final String seqID, final String seqName, final String description, final String sequence, final Integer seqType, final String structure, final String rnastructure_id) throws BioDOMException{
        appendSequence(seqID, seqName, null, description, sequence, seqType, 
                structure, null, null, null, rnastructure_id);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequence(java.lang.String, java.lang.String, java.lang.util.List<java.lang.String>, java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.Double, java.lang.Double, java.lang.String)
     */
    public void appendSequence(final String seqID, final String seqName, final List<String> synonyms,
    		final String description, final String sequence, final Integer sequenceType,
            final String structure, final Double energy, final Double probability, final String seqcomment)
            throws BioDOMException {
        appendSequence(seqID, seqName, synonyms, description, sequence, 
                sequenceType, structure, energy, probability, seqcomment, null);
    }
        
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendSequence(java.lang.String, java.lang.String, java.util.List<java.lang.String>, java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.Double, java.lang.Double, java.lang.String, java.lang.String)
     */
    public void appendSequence(final String seqID, final String seqName, final List<String> synonyms,
    		final String description, final String sequence, final Integer sequenceType,
    		final String structure, final Double energy, final Double probability, final String seqcomment, 
            String rnastructurealign_id)
            throws BioDOMException {
        log.config("Method append sequence called...");
        if((seqID == null)
        		||(((sequence == null) || (sequence.equals(""))) && (sequenceType != EMPTYSEQUENCE))
        		||(structure == null)){
            log.equals("Invalid arguments! One required argument was either null or contained an invalid value.");
            throw new BioDOMException("Invalid arguments! One required argument was either null or contained an invalid value.");
        }
        
        // get the correct rnastructurealignment element, create a new one if no
        // id was given
        if (rnastructurealign_id == null) {
            rnastructurealign_id = appendEmptyRNAStructureAlignment();
        } else {
            // check if this shape does already exist in the given rnastructure
            if (!rnastructurealignmentidsList.contains(rnastructurealign_id)) {
                log.severe("Error! Given rnastructure alignment id '"
                        + rnastructurealign_id + "' does not exist!");
                throw new BioDOMException(
                        "Error! Given rnastructure alignment id '"
                                + rnastructurealign_id + "' does not exist!");
            }
        }
        final Element rnastructurealignElem = dom.getElementById(rnastructurealign_id);

        // constructing sequence element
        final Element sequenceElem = dom.createElementNS(NAMESPACE,ELEM_SEQUENCE);
        
        // adding the attributes that are not empty
        //TODO check if seqID is unique -> add to hash
        sequenceElem.setAttribute(ATT_SEQID, seqID);
        log.config("setting seqID to " + seqID);
        
        if((sequence != null)&&(!sequence.equals(""))){
            sequenceElem.setAttribute("size", Integer.toString(sequence.length()));
            log.config("setting size to " + sequence.length());
        }
        
        // appending name element
        if((seqName != null)&&(!seqName.equals(""))){
            log.config("creating name element. name: " + seqName);
            final Element nameElem = dom.createElementNS(NAMESPACE,ELEM_NAME);
            nameElem.setTextContent(seqName);
            sequenceElem.appendChild(nameElem);
        }
        
        //appending synonyms elements
        if(synonyms != null){
            Element synElem;
            for(int i = 0; i < synonyms.size(); i++){
                log.config("adding synonym : " + synonyms.get(i));
                synElem = dom.createElementNS(NAMESPACE,ELEM_SYNONYMS);
                synElem.setTextContent(synonyms.get(i));
                sequenceElem.appendChild(synElem);
            }
        }
        //appending description
        if((description != null)&&(!description.equals(""))){
            log.config("creating description element : " + description);
            final Element descrElem = dom.createElementNS(NAMESPACE,ELEM_DESCRIPTION);
            descrElem.setTextContent(description);
            sequenceElem.appendChild(descrElem);
        }
        
        // appending sequence
        if(((sequence != null)&&(!sequence.equals("")))||(sequenceType.equals(EMPTYSEQUENCE))){
            if(sequenceType.equals(NUCLEICACID)){
                log.config("creating NUCLEICACID sequence :" + sequence);
                final Element nucleicElem = dom.createElementNS(NAMESPACE,ELEM_ALIGNEDNUCLEICACIDSEQUENCE);
                nucleicElem.setTextContent(sequence);
                sequenceElem.appendChild(nucleicElem);
            }else if(sequenceType.equals(FREE)){
                log.config("creating FREE sequence :" + sequence);
                final Element freeElem = dom.createElementNS(NAMESPACE,ELEM_ALIGNEDFREESEQUENCE);
                freeElem.setTextContent(sequence);
                sequenceElem.appendChild(freeElem);
            }else{
                log.config("creating EMPTY sequence ...");
                final Element emptyElem = dom.createElementNS(NAMESPACE,ELEM_EMPTYSEQUENCE);
                emptyElem.setTextContent("");
                sequenceElem.appendChild(emptyElem);
            }
        }
        
        //appending structure -- This element is required!
        log.config("creating structure element");
        if(checkStructure(structure)){
        	final Element structureElem = dom.createElementNS(NAMESPACE,ELEM_STRUCTURE);
            structureElem.setTextContent(structure);
            if(energy != null){
                structureElem.setAttribute(ATT_ENERGY, Double.toString(energy));
            }
            if(probability != null){
                if((1 >= probability)&&(probability >= 0)){
                    structureElem.setAttribute(ATT_PROBABILITY, Double.toString(probability));
                }else{
                    log.severe("Error! Invalid value for probability! Must be a value between 0 and 1!");
                    throw new BioDOMException("Error! Invalid value for probability! Must be a value between 0 and 1!");
                }
            }
            sequenceElem.appendChild(structureElem);
        }else{
            log.severe("Error! Invalid structure! : '" + structure + "'");
            throw new BioDOMException("Error! Invalid structure! : '" + structure + "'");
        }
        
        //appending comment element
        if((seqcomment != null)&&(!seqcomment.equals(""))){
            log.config("creating comment element : " + seqcomment);
            final Element commentElem = dom.createElementNS(NAMESPACE,ELEM_COMMENT);
            commentElem.setTextContent(seqcomment);
            sequenceElem.appendChild(commentElem);
        }
 
        // adding sequence to rnastructurealignment element
        final NodeList tmpProgramNode = rnastructurealignElem.getElementsByTagName(ELEM_PROGRAM);
        final NodeList tmpCommentNode = rnastructurealignElem.getElementsByTagName(ELEM_COMMENT);
        final NodeList tmpConsensusNode = rnastructurealignElem.getElementsByTagName(ELEM_CONSENSUS);
        if((tmpProgramNode != null)||(tmpCommentNode != null)||(tmpConsensusNode != null))
        {
            // elements have to be rearangend so all element are in correct order
            
            // removing the other elements
            Node removedComment = null;
            if((tmpCommentNode != null)&&(tmpCommentNode.getLength() > 0)){
                // filtering comment elements that are not children to the 
                // rnastructurealignment element
                for(int i = 0; i < tmpCommentNode.getLength(); i++){
                    if(tmpCommentNode.item(i).getParentNode().equals(rnastructurealignElem)){
                        if(removedComment == null){
                            removedComment = rnastructurealignElem.removeChild(tmpCommentNode.item(i));
                        }else{
                            log.severe("Error! Double comment element in rnastructurealignment id : '" + rnastructurealign_id + "'");
                            throw new BioDOMException("Error! Double comment element in rnastructurealignment id : '" + rnastructurealign_id + "'");
                        }
                    }
                }
            }
            Node removedConsensus = null;
            if((tmpConsensusNode != null)&&(tmpConsensusNode.getLength() > 0)){
                removedConsensus = rnastructurealignElem.removeChild(tmpConsensusNode.item(0));
            }
            Node removedProgram = null;
            if((tmpProgramNode != null)&&(tmpProgramNode.getLength() > 0)){
                removedProgram = rnastructurealignElem.removeChild(tmpProgramNode.item(0));
            }
            
            // adding the sequence node
            rnastructurealignElem.appendChild(sequenceElem);
            
            // re-adding the removed elements
            if (removedComment != null){
                rnastructurealignElem.appendChild(removedComment);
            }
            if (removedConsensus != null){
                rnastructurealignElem.appendChild(removedConsensus);
            }
            if (removedProgram != null){
                rnastructurealignElem.appendChild(removedProgram);
            }
        }else{
            // no element in disorder, no need to rearange elements
            rnastructurealignElem.appendChild(sequenceElem);
        }
        log.config("Finished appendSequence ...");
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendProgram(java.lang.String, java.lang.String)
     */
    public void appendProgram(final String program, final String rnastructurealignment_id)
            throws BioDOMException {
        this.appendProgram(program, null, null, null, rnastructurealignment_id);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendProgram(java.lang.String, java.lang.String, java.lang.String)
     */
    public void appendProgram(final String program, final String command,
            final String rnastructurealign_id) throws BioDOMException {
        this.appendProgram(program, command, null, null, rnastructurealign_id);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendProgram(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void appendProgram(final String program, final String command, final String version,
    		final String rnastructurealign_id) throws BioDOMException {
        this.appendProgram(program, command, version, null,
                rnastructurealign_id);
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendProgram(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void appendProgram(final String program, final String command, final String version,
    		final String date, String rnastructurealign_id) throws BioDOMException {
        log.config("Called appendProgram. Argument : '" + program + "'/'"
                + command + "'/'" + version + "'/'" + date + "'/'"
                + rnastructurealign_id + "'");
        // get the correct rnastructurealignment element, create a new one if no
        // id was
        // given
        if (rnastructurealign_id == null) {
            rnastructurealign_id = appendEmptyRNAStructureAlignment();
        } else {
            // check if this shape does already exist in the given rnastructure
            if (!rnastructurealignmentidsList.contains(rnastructurealign_id)) {
                log.severe("Error! Given rnastructure alignment id '"
                        + rnastructurealign_id + "' does not exist!");
                throw new BioDOMException(
                        "Error! Given rnastructure alignment id '"
                                + rnastructurealign_id + "' does not exist!");
            }
        }
        final Element rnastructurealignElem = dom.getElementById(rnastructurealign_id);

        // constructing program element
        final Element programElem = dom.createElementNS(NAMESPACE,ELEM_PROGRAM);
        programElem.setTextContent(program);

        // adding the attributes that are not empty
        if ((command != null)&&(!command.equals(""))) {
            programElem.setAttribute(ATT_COMMAND, command);
        }
        if ((version != null)&&(!version.equals(""))) {
            programElem.setAttribute(ATT_VERSION, version);
        }
        if ((date != null)&&(!date.equals(""))) {
            if (!checkDate(date)) {
                log.severe("Date invalid : '" + date + "'");
                throw new BioDOMException("Invalid date string submitted: '"
                        + date + "'! Format YYYY-MM-DD required!");
            }
            programElem.setAttribute(ATT_DATE, date);
        }

        // adding program to rnastructurealignment element
        rnastructurealignElem.appendChild(programElem);
        log.config("Finished appendProgram ...");
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendComment(java.lang.String, java.lang.String)
     */
    public void appendComment(final String comment, String rnastructurealign_id)
            throws BioDOMException {
        log.config("Called appendComment. Arguments :'" + comment + "'/'"
                + rnastructurealign_id + "'");
        // get the correct rnastructurealignment element, create a new one if no
        // id was
        // given
        if (rnastructurealign_id == null) {
            rnastructurealign_id = appendEmptyRNAStructureAlignment();
        } else {
            // check if the given rnastructurealignment element exists
            if (!rnastructurealignmentidsList.contains(rnastructurealign_id)) {
                log.severe("Error! Given rnastructure alignment id '"
                        + rnastructurealign_id + "' does not exist!");
                throw new BioDOMException(
                        "Error! Given rnastructure alignment id '"
                                + rnastructurealign_id + "' does not exist!");
            }
        }
        final Element rnastructurealignElem = dom
                .getElementById(rnastructurealign_id);

        // constructing the comment
        final Element commentElem = dom.createElementNS(NAMESPACE,ELEM_COMMENT);
        commentElem.setTextContent(comment);

        // adding comment to rnastructure
        final NodeList tmpProgramNode = rnastructurealignElem.getElementsByTagName(ELEM_PROGRAM);
        final NodeList tmpConsensusNode = rnastructurealignElem.getElementsByTagName(ELEM_CONSENSUS);
        if((tmpProgramNode != null)||(tmpConsensusNode != null))
        {
            // elements have to be rearangend so all element are in correct order
            
            // removing the other elements
            Node removedConsensus = null;
            if(tmpConsensusNode.getLength() > 0){
                removedConsensus = rnastructurealignElem.removeChild(tmpConsensusNode.item(0));
            }
            Node removedProgram = null;
            if(tmpProgramNode.getLength() > 0){
                removedProgram = rnastructurealignElem.removeChild(tmpProgramNode.item(0));
            }
            
            // adding the sequence node
            rnastructurealignElem.appendChild(commentElem);
            
            // readding the removed elements
            if (removedConsensus != null){
                rnastructurealignElem.appendChild(removedConsensus);
            }
            if (removedProgram != null){
                rnastructurealignElem.appendChild(removedProgram);
            }
        }else{
            // no element in disorder, no need to rearange elements
            rnastructurealignElem.appendChild(commentElem);
        }
        log.config("Finished appendComment...");
    }

/*  // Old appendConsensus!  
     (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendConsensus(java.lang.String, java.lang.Double, java.lang.Double, java.lang.String)
     
    public void appendConsensus(final String consensus, final Double energy, final Double probability, final String rnastructurealign_id) throws BioDOMException{
        // checking arguments
        if((consensus==null)||(consensus.equals(""))||
                (rnastructurealign_id==null)||(rnastructurealign_id.equals(""))||
                (!rnastructurealignmentidsList.contains(rnastructurealign_id))){
            log.severe("Invalid argument while trying to appendConsensus!");
            throw new BioDOMException("Invalid argument while trying to appendConsensus!");
        }
        
        // creating consensus element
        final Element consensusElem = dom.createElementNS(NAMESPACE,ELEM_CONSENSUSSTRUCTURE);
        
        //checking consensus structure and adding it
        if(checkStructure(consensus)){
            consensusElem.setTextContent(consensus);
        }else{
            log.severe("Invalid consensus structure : '" + consensus + "'");
            throw new BioDOMException("Invalid consensus structure : '" + consensus + "'");
        }
        
        // adding attributes that are not null
        if(energy != null){
            log.config("adding energy :" + energy.toString());
            consensusElem.setAttribute(ATT_ENERGY, energy.toString());
        }
        
        if(probability != null){
            log.config("adding probability :" + probability.toString());
            consensusElem.setAttribute(ATT_PROBABILITY, probability.toString());
        }
        
        // adding consensus element to rnastructurealignment
        final Element rnastructurealignElem = dom.getElementById(rnastructurealign_id);
        final NodeList tmpProgramNode = rnastructurealignElem.getElementsByTagName(ELEM_PROGRAM);
        if((tmpProgramNode != null)&&(tmpProgramNode.getLength() > 0))
        {
            // elements have to be rearangend so all element are in correct order
            
            // removing the program element
        	final Node removedProgram = rnastructurealignElem.removeChild(tmpProgramNode.item(0));
        
            // adding the sequence node
            rnastructurealignElem.appendChild(consensusElem);
            
            // readding the removed elements
            rnastructurealignElem.appendChild(removedProgram);
            
        }else{
            // no element in disorder, no need to rearange elements
            rnastructurealignElem.appendChild(consensusElem);
        }
        log.config("Finished appendConsensus...");
    }
*/   
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendEmptyRNAStructureAlignment()
     */
    public String appendEmptyRNAStructureAlignment(){
        return appendEmptyRNAStructureAlignment(null);
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendEmptyRNAStructureAlignment(java.lang.Integer)
     */
    public String appendEmptyRNAStructureAlignment(final Integer score) {
        log.config("Called appendEmptyRNAStructureAlignment...");
        // creates an empty rnastructurealignment element
        final Element rnastructurealignElem = dom.createElementNS(NAMESPACE,ELEM_RNASTRUCTUREALIGNMENT);
        log.config("Created rnastructurealignment element");
        
        // generate an id and set id attribute
        final String idString = generateId();
        log.config("Generated id");
        rnastructurealignElem.setAttribute(ATT_ID, idString);
        log.config("Id attribute set");
        rnastructurealignElem.setIdAttribute(ATT_ID, true);
        log.config("Id attribute declared to be an id..");
        
        // setting the score if not null
        if(score!=null){
            log.config("Setting score :" + score.toString());
            rnastructurealignElem.setAttribute(ATT_SCORE, score.toString());
        }
        
        // getting root element and adding rnastructurealignment
        final Element rootElem = dom.getDocumentElement();
        log.config("Adding rnastructurealignment to the rootelement");
        rootElem.appendChild(rnastructurealignElem);
        
        // adding id to hash
        rnastructurealignmentidsList.add(idString);
        log.config("Returning rnastructureId : '" + idString + "'");
        
        // returning the id
        return idString;
    }
    
    /////////////////////////////////////
    /////// Output Converter
    /////////////////////////////////////
    public String toAlignedDotBracketFasta() throws BioDOMException{
        // init
        StringBuffer alignedDotBracketFasta = new StringBuffer();
        
        // for each rnastructurealignment
        for(String id : rnastructurealignmentidsList){
        	final List<Hashtable> seqList = getAllSequences(id);
        	for(Hashtable sequenceHash : seqList){
        		// Fasta header
        		alignedDotBracketFasta.append('>');
        		if (sequenceHash.containsKey("sequence")) {
        			if(sequenceHash.containsKey("seqID")){
        				//System.out.println("seqID:|"+sequenceHash.get("seqID")+"|");
        				alignedDotBracketFasta.append(sequenceHash.get("seqID"));
        			}
        			if(sequenceHash.containsKey("name")){
        				//System.out.println("name:|"+sequenceHash.get("name")+"|");
        				alignedDotBracketFasta.append(' ').append(sequenceHash.get("name"));
        			}
        			if(sequenceHash.containsKey("description")){
        				//System.out.println("description:|"+sequenceHash.get("description")+"|");
        				alignedDotBracketFasta.append(' ').append(sequenceHash.get("description"));
        			}
        			alignedDotBracketFasta.append(LINEBREAK);
        			// sequence
        			//System.out.println("sequence:|"+sequenceHash.get("sequence")+"|");
        			alignedDotBracketFasta.append(sequenceHash.get("sequence"));
        		}
        		alignedDotBracketFasta.append(LINEBREAK);
        		
        		
        		
        		// structure
        		if(sequenceHash.containsKey("structure")){
        			final Hashtable structHash = (Hashtable)sequenceHash.get("structure");
        			alignedDotBracketFasta.append(structHash.get("structure")).append(LINEBREAK);
        		}else{
        			log.severe("Error! Structure data missing in hashtable...");
        			throw new BioDOMException("Error! Structure data missing...");
        		}
        		
        	}
            // add consensus sequence
            final Hashtable consensus = getConsensus(id);
            if((consensus != null)&&(consensus.containsKey("consensusstructure"))){
                alignedDotBracketFasta.append("consensus structure:").append(LINEBREAK).append(consensus.get("consensusstructure")).append(LINEBREAK);
            }
        }
        return alignedDotBracketFasta.toString();
        // throw new BioDOMException("toAlignedDotBracketFasta: Method not yet implemented!");
    }
    

    /////////////////////////////////////
    /////// Getter methods
    /////////////////////////////////////
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#getLastRnastructureAlignmentId()
     */
    public String getLastRnastructureAlignmentId() {
        log.config("Called getLastRnastructureAlignmentId...");
        final Element rootElem = dom.getDocumentElement();
        final NodeList rnastructurealignNodes = rootElem.getElementsByTagName(ELEM_RNASTRUCTUREALIGNMENT);
        if ((rnastructurealignNodes != null)&&(rnastructurealignNodes.getLength() > 0)){
            log.config(rnastructurealignNodes.getLength() + " elements in doc... Using last element.");
            final Element rnastructurealignElem = (Element)rnastructurealignNodes.item(rnastructurealignNodes.getLength()-1);
            log.config("Returning id : '"
                    + rnastructurealignElem.getAttributeNS(null,ATT_ID) + "'");
            return rnastructurealignElem.getAttributeNS(null,ATT_ID);
        }
        //If we reach this point, there were no rnastructurealignNodes, therefore we log a message and return null
        log.config("There is no last rnastructurealignment element! Returning null!");
        return null;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#getRnastructureAlignmentIds()
     */
    public List<String> getRnastructureAlignmentIds(){
        log.config("Called getRnastructureAlignmentIds() ...");
        if(rnastructurealignmentidsList.size() > 0){
            return rnastructurealignmentidsList;
        }
        //If we reach this point, there are no rnastructurealignmentids, therefore we log a message and return null 
        log.config("No rnastructurealignment element were found. Returning null...");
        return null;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#getRnastructureAlignment(java.lang.String)
     */
    public Hashtable<String, Object> getRnastructureAlignment(final String rnastructurealignment_id) throws BioDOMException{
        log.config("Called getRnastructureAlignment with rnastructurealignment_id : " + rnastructurealignment_id);
        
        // checking if element exists
        if(!rnastructurealignmentidsList.contains(rnastructurealignment_id)){
            log.severe("Invalid rnastructurealignment id supplied : " + 
                    rnastructurealignment_id + 
                    "! There is no such element in the document...");
            throw new BioDOMException(
                    "Invalid rnastructurealignment id supplied : " 
                    + rnastructurealignment_id + 
                    "! There is no such element in the document...");
        }
        
        // building Hash
        final Hashtable<String, Object> structalignHash = new Hashtable<String, Object>();
        
        // getting rnastructurealignment element 
        final Element rnastructalignElem = dom.getElementById(rnastructurealignment_id);
        
        ///////////////////////
        // getting score and id
        ////////////////////////
        final String score = rnastructalignElem.getAttributeNS(null,ATT_SCORE);
        if((score != null)&&(!score.equals(""))){
            structalignHash.put("score", Integer.parseInt(score));
        }
        structalignHash.put("id", rnastructurealignment_id);
        
        /////////////////////
        // getting sequences
        /////////////////////
        final NodeList sequenceNodes = rnastructalignElem.getElementsByTagName(ELEM_SEQUENCE);
        if((sequenceNodes == null)||(sequenceNodes.getLength() < 2)){
            log.severe("Error! Missing sequence elements in rnastructure!");
            throw new BioDOMException("Error! Missing sequence elements in rnastructure!");
        }
        final ArrayList<Hashtable> sequencesHashList = new ArrayList<Hashtable>();
        for(int i = 0; i < sequenceNodes.getLength(); i++){
        	final Element seqElem = (Element)sequenceNodes.item(i);
            // initialising Hash
        	final Hashtable<String, Object> sequenceHash = new Hashtable<String, Object>();
            //get seqID
            final String seqID = seqElem.getAttributeNS(null,ATT_SEQID);
            if((seqID != null)&&(!seqID.equals(""))){
                sequenceHash.put("seqID", seqElem.getAttributeNS(null,ATT_SEQID));
            }else{
                log.severe("Error! Missing id attribute at sequence element!");
                throw new BioDOMException("Error! Missing id attribute at sequence element!");
            }
            //get name if existent
            final NodeList nameNodes = seqElem.getElementsByTagName(ELEM_NAME); 
            if((nameNodes != null)&&(nameNodes.getLength() > 0)){
            	final Element nameElem = (Element)nameNodes.item(0);
                sequenceHash.put("name", nameElem.getTextContent());
            }
            //get synonyms if existent
            final NodeList synonymsNodes = seqElem.getElementsByTagName(ELEM_SYNONYMS); 
            if((synonymsNodes != null)&&(synonymsNodes.getLength() > 0)){
            	final ArrayList<String> synonymsArrayList = new ArrayList<String>();
                for(int j = 0; synonymsNodes.getLength() > j; j++){
                	final Element synonymsElem = (Element)synonymsNodes.item(j);
                    synonymsArrayList.add(synonymsElem.getTextContent());
                }
                sequenceHash.put("synonyms", synonymsArrayList);
            }
            // get description if existent
            final NodeList descriptionNodes = seqElem.getElementsByTagName(ELEM_DESCRIPTION); 
            if((descriptionNodes != null)&&(descriptionNodes.getLength() > 0)){
            	final Element descriptionElem = (Element)descriptionNodes.item(0);
                sequenceHash.put("description", descriptionElem.getTextContent());
            }
            // get sequence, sequenceType & sequenceTypeName
            final NodeList nucleotideseqNodes = seqElem.getElementsByTagName(ELEM_ALIGNEDNUCLEICACIDSEQUENCE);
            if((nucleotideseqNodes != null)&&(nucleotideseqNodes.getLength() > 0)){
            	final Element nucleotideseqElem = (Element)nucleotideseqNodes.item(0);
                sequenceHash.put("sequence", nucleotideseqElem.getTextContent());
                sequenceHash.put("sequenceType", NUCLEICACID);
                sequenceHash.put("sequenceTypeName", ALIGNEDNUCLEICACID_NAME);
            }else{
            	final NodeList freeseqNodes = seqElem.getElementsByTagName(ELEM_ALIGNEDFREESEQUENCE);
                if((freeseqNodes != null)&&(freeseqNodes.getLength() > 0)){
                	final Element freeseqElem = (Element)freeseqNodes.item(0);
                    sequenceHash.put("sequence", freeseqElem.getTextContent());
                    sequenceHash.put("sequenceType", FREE);
                    sequenceHash.put("sequenceTypeName", ALIGNEDFREESEQUENCE_NAME);
                }else{
                	final NodeList emptyNodes = seqElem.getElementsByTagName(ELEM_EMPTYSEQUENCE);
                    if((emptyNodes != null)){ //)&&(emptyNodes.getLength() > 0)){
                        sequenceHash.put("sequenceType", EMPTYSEQUENCE);
                        sequenceHash.put("sequenceTypeName", EMPTYSEQUENCE_NAME);
                    }else{
                        log.severe("Error! Unexpected or missing sequence element!");
                        throw new BioDOMException("Error! Unexpected or missing sequence element!");
                    }
                }
            }
            // get structure
            final NodeList structureNodes = seqElem.getElementsByTagName(ELEM_STRUCTURE);
            if((structureNodes != null)&&(structureNodes.getLength() > 0)){
            	final Element structureElem = (Element)structureNodes.item(0);
                final Hashtable<String, Object> structureHash = new Hashtable<String, Object>();
                // build structure
                structureHash.put("structure", structureElem.getTextContent());
                final String prob = structureElem.getAttributeNS(null,ATT_PROBABILITY);
                if((prob != null)&&(!prob.equals(""))){
                    log.config("probability : '" + structureElem.getAttributeNS(null,ATT_PROBABILITY) + "'");
                    structureHash.put("probability", Double.valueOf(structureElem.getAttributeNS(null,ATT_PROBABILITY)));
                }
                final String energy = structureElem.getAttributeNS(null,ATT_ENERGY);
                if((energy != null)&&(!energy.equals(""))){
                    log.config("energy : '" + structureElem.getAttributeNS(null,ATT_ENERGY) + "'");
                    structureHash.put("energy", Double.valueOf(structureElem.getAttributeNS(null,ATT_ENERGY)));
                }
                sequenceHash.put("structure", structureHash);
            }else{
                log.severe("Error! Mission structure element in sequence element!");
                throw new BioDOMException("Error! Mission structure element in sequence element!");
            }        
            // get comment if existent
            final NodeList commentNodes = seqElem.getElementsByTagName(ELEM_COMMENT); 
            if((commentNodes != null)&&(commentNodes.getLength() > 0)){
            	final Element commentElem = (Element)commentNodes.item(0);
                sequenceHash.put("seqcomment", commentElem.getTextContent());
            }
            // adding the sequence Hash to the List<Hash> ...
            sequencesHashList.add(sequenceHash);
        }
        structalignHash.put("sequences", sequencesHashList);
        
        ////////////////////
        // getting comment
        ////////////////////
        final NodeList commentNodes = rnastructalignElem.getElementsByTagName(ELEM_COMMENT);
        if((commentNodes != null)&&(commentNodes.getLength() > 0)){
        	final Element commentElem = (Element)commentNodes.item(0);
            structalignHash.put("comment", commentElem.getTextContent());
        }   
        
        ////////////////////
        // consensusstructure
        ////////////////////
        log.warning("Consensus not implemented in current version! Skipping consensus");
        // old Consensus 
        // TODO FIXME 
/*        final NodeList consensusNodes = rnastructalignElem.getElementsByTagName(ELEM_CONSENSUSSTRUCTURE);
        if((consensusNodes != null)&&(consensusNodes.getLength() > 0)){
        	final Element consensusElem = (Element)consensusNodes.item(0);
        	final Hashtable<String, Object> consensusHash = new Hashtable<String, Object>();
            consensusHash.put("structure", consensusElem.getTextContent());
            final String prob = consensusElem.getAttributeNS(null,ATT_PROBABILITY);
            if((prob != null)&&(!prob.equals(""))){
                consensusHash.put("probability", Double.valueOf(prob));
            }
            final String energy = consensusElem.getAttributeNS(null,ATT_ENERGY);
            if((energy != null)&&(!energy.equals(""))){
                consensusHash.put("energy", Double.valueOf(energy));
            }
            structalignHash.put("consensus", consensusHash);
        }
*/        
        ////////////////////
        // getting program
        ////////////////////
        final NodeList programNodes = rnastructalignElem.getElementsByTagName(ELEM_PROGRAM);
        if((programNodes != null)&&(programNodes.getLength() > 0)){
        	final Element programElem = (Element)programNodes.item(0);
        	final Hashtable<String, String> programHash = new Hashtable<String, String>();
            programHash.put("programname", programElem.getTextContent());
            final String command = programElem.getAttributeNS(null,ATT_COMMAND);
            if((command != null)&&(!command.equals(""))){
                programHash.put("command", command);
            }
            final String version = programElem.getAttributeNS(null,"version"); 
            if((version != null)&&(!version.equals(""))){
                programHash.put("version", version);
            }
            final String date = programElem.getAttributeNS(null,"date"); 
            if((date != null)&&(!date.equals(""))){
                programHash.put("date", date);
            }
            structalignHash.put("program", programHash);
        }
        
        // returning resulting Hashtable
        return structalignHash;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#getSequenceIds(java.lang.String)
     */
    public List<String> getSequenceIds(final String rnastructurealignment_id) throws BioDOMException{
        log.config("Called getSequenceIds with rnastructurealignment_id : " + rnastructurealignment_id);
        if(!rnastructurealignmentidsList.contains(rnastructurealignment_id)){
            log.severe("Invalid rnastructurealignment id supplied : " + 
                    rnastructurealignment_id + 
                    "! There is no such element in the document...");
            throw new BioDOMException(
                    "Invalid rnastructurealignment id supplied : " 
                    + rnastructurealignment_id + 
                    "! There is no such element in the document...");
        }
        
        // getting the rnastructure alignment
        final List<Hashtable> sequencesHashList = getAllSequences(rnastructurealignment_id);
        final ArrayList<String> seqids = new ArrayList<String>();
        for(int i = 0; i < sequencesHashList.size(); i++){
            seqids.add((String)sequencesHashList.get(i).get("seqID"));
        }
        return seqids;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#getSequence(java.lang.String, java.lang.String)
     */
    public Hashtable getSequence(final String rnastructurealignment_id, final String sequence_id) throws BioDOMException{
        log.config("Called getSequence with rnastructurealignment_id : " + rnastructurealignment_id);
        if(!rnastructurealignmentidsList.contains(rnastructurealignment_id)){
            log.severe("Invalid rnastructurealignment id supplied : " + 
                    rnastructurealignment_id + 
                    "! There is no such element in the document...");
            throw new BioDOMException(
                    "Invalid rnastructurealignment id supplied : " 
                    + rnastructurealignment_id + 
                    "! There is no such element in the document...");
        }
        final List<Hashtable> sequencesHashtableList = getAllSequences(rnastructurealignment_id);
        for(int i = 0; i < sequencesHashtableList.size(); i++){
            if(((String)sequencesHashtableList.get(i).get("seqID")).equals(sequence_id)){
                log.config("Found sequence id " + sequence_id);
                return sequencesHashtableList.get(i);
            }
        }
        log.severe("Error! Sequence id " + sequence_id + 
                " not found in element " + rnastructurealignment_id + "!");
        throw new BioDOMException("Error! Sequence id " + sequence_id + 
                " not found in element " + rnastructurealignment_id + "!");
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#getAllSequences(java.lang.String)
     */
    public List<Hashtable> getAllSequences(final String rnastructurealignment_id) throws BioDOMException{
        log.config("Called getAllSequences with rnastructurealignment_id : " + rnastructurealignment_id);
        if(!rnastructurealignmentidsList.contains(rnastructurealignment_id)){
            log.severe("Invalid rnastructurealignment id supplied : " + 
                    rnastructurealignment_id + 
                    "! There is no such element in the document...");
            throw new BioDOMException(
                    "Invalid rnastructurealignment id supplied : " 
                    + rnastructurealignment_id + 
                    "! There is no such element in the document...");
        }
        final Hashtable rnastructureHash = getRnastructureAlignment(rnastructurealignment_id);
        final List<Hashtable> sequencesHash = (List<Hashtable>)rnastructureHash.get("sequences");
        return sequencesHash;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#getProgram(java.lang.String)
     */
    public Hashtable getProgram(final String rnastructurealignment_id) throws BioDOMException{
        log.config("Called getProgram with rnastructurealignment_id : " + rnastructurealignment_id);
        if(!rnastructurealignmentidsList.contains(rnastructurealignment_id)){
            log.severe("Invalid rnastructurealignment id supplied : " + 
                    rnastructurealignment_id + 
                    "! There is no such element in the document...");
            throw new BioDOMException(
                    "Invalid rnastructurealignment id supplied : " 
                    + rnastructurealignment_id + 
                    "! There is no such element in the document...");
        }
        final Hashtable rnastructureHash = getRnastructureAlignment(rnastructurealignment_id);
        if(rnastructureHash.containsKey("program")){
            final Hashtable programHash = (Hashtable)rnastructureHash.get("program");
            return programHash;
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#getConsensusStructure(java.lang.String)
     */
    public Hashtable getConsensus(final String rnastructurealignment_id) throws BioDOMException{
        log.config("Called getConsensusStructure with rnastructurealignment_id : " + rnastructurealignment_id);
        if(!rnastructurealignmentidsList.contains(rnastructurealignment_id)){
            log.severe("Invalid rnastructurealignment id supplied : " + 
                    rnastructurealignment_id + 
                    "! There is no such element in the document...");
            throw new BioDOMException(
                    "Invalid rnastructurealignment id supplied : " 
                    + rnastructurealignment_id + 
                    "! There is no such element in the document...");
        }
        final Hashtable rnastructureHash = getRnastructureAlignment(rnastructurealignment_id);
        if(rnastructureHash.containsKey("consensus")){
            final Hashtable consensusHash = (Hashtable)rnastructureHash.get("consensus");
            return consensusHash;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#getComment(java.lang.String)
     */
    public String getComment(final String rnastructurealignment_id) throws BioDOMException{
        log.config("Called getComment with rnastructurealignment_id : " + rnastructurealignment_id);
        if(!rnastructurealignmentidsList.contains(rnastructurealignment_id)){
            log.severe("Invalid rnastructurealignment id supplied : " + 
                    rnastructurealignment_id + 
                    "! There is no such element in the document...");
            throw new BioDOMException(
                    "Invalid rnastructurealignment id supplied : " 
                    + rnastructurealignment_id + 
                    "! There is no such element in the document...");
        }
        final Hashtable rnastructureHash = getRnastructureAlignment(rnastructurealignment_id);
        if(rnastructureHash.containsKey("comment")){
            final String comment = (String)rnastructureHash.get("comment");
            return comment;
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#getScore(java.lang.String)
     */
    public Integer getScore(final String rnastructurealignment_id) throws BioDOMException {
        log.config("Called getScore with rnastructurealignment_id : " + rnastructurealignment_id);
        if(!rnastructurealignmentidsList.contains(rnastructurealignment_id)){
            log.severe("Invalid rnastructurealignment id supplied : " + 
                    rnastructurealignment_id + 
                    "! There is no such element in the document...");
            throw new BioDOMException(
                    "Invalid rnastructurealignment id supplied : " 
                    + rnastructurealignment_id + 
                    "! There is no such element in the document...");
        }
        final Element rnastructurealignmentElem = dom.getElementById(rnastructurealignment_id);
        final String scoreString = rnastructurealignmentElem.getAttributeNS(null,"score");
        
        //If the scoreString is null or empty, we return a null value...
        if((scoreString == null) || (scoreString.equals(""))) return null;
        //..otherwise, all is well and we can create and return the score value
       	final Integer score = Integer.parseInt(scoreString);
       	return score;
    }
    
    //////////////////////////////////
    ////// Private Methods
    //////////////////////////////////

    /**
     * generates an empty DOM Tree
     */
    private void initDOM() {     
        dom = DB.newDocument();
        final Element root = dom.createElementNS(NAMESPACE, ELEM_RNASTRUCTALIGNMENTML);
        root.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,"schemaLocation", NAMESPACE+" "+NSLOCATION);
        dom.appendChild(root);
        dom.normalizeDocument();
        log.config("DOM ready...");
    }

    /**
     * This method checks a dotbracket string for syntactical correctness
     * 
     * @param dotbracketString
     * @return true if not empty and syntactical correct, false otherwise
     */
    private boolean checkStructure(final String dotbracketString) {
        log.info("Checking '" + dotbracketString
                + "' for syntactical correctness...");
        if ((dotbracketString == null) || (dotbracketString.equals(""))) {
            // checks if structure is empty
            log.severe("Error: dotbracket string is empty or null!");
            return false;
        }
        final char[] tokens = dotbracketString.toCharArray();
        int counterSimple = 0; // counter for '('-brackets
        int counterEdged = 0; // counter for '['-brackets
        int counterTwisted = 0; // counter for '{'-brackets
        int counterPointed = 0; // counter for '<'-brackets
        String currenttoken;

        for (int i = 0; i < tokens.length; i++) {
            currenttoken = "" + tokens[i];
            // log.config("current token : '" + currenttoken + "'");
            switch (tokens[i]) {
            // incr opening brackets counter
            case '(':
                counterSimple++;
                break;
            case '[':
                counterEdged++;
                break;
            case '{':
                counterTwisted++;
                break;
            case '<':
                counterPointed++;
                break;

            // decr the brackets counter
            case ')':
                counterSimple--;
                if (counterSimple < 0) {
                    // to few opening brackets
                    log.severe("Closing bracket detected where an opening bracket was expected... :'"
                                    + currenttoken + "'");
                    return false;
                }
                break;
            case ']':
                counterEdged--;
                if (counterEdged < 0) {
                    // to few opening brackets
                    log.severe("Closing bracket detected where an opening bracket was expected... :'"
                                    + currenttoken + "'");
                    return false;
                }
                break;
            case '}':
                counterTwisted--;
                if (counterTwisted < 0) {
                    // to few opening brackets
                    log.severe("Closing bracket detected where an opening bracket was expected... :'"
                                    + currenttoken + "'");
                    return false;
                }
                break;
            case '>':
                counterPointed--;
                if (counterPointed < 0) {
                    // to few opening brackets
                    log.severe("Closing bracket detected where an opening bracket was expected... :'"
                                    + currenttoken + "'");
                    return false;
                }
                break;
            case '.': // ignore case
                break;
            case '-': // another ignore case (gap symbol)
                break;
            default: // invalid symbol found
                log.severe("Invalid symbol detected... : '" + currenttoken
                                + "'");
                return false;
            }
        }
        if (counterSimple + counterEdged + counterTwisted + counterPointed > 0) {
            // check if some brackets are still open...
            log.severe("To many opening brackets... Structure is not valid!");
            return false;
        }
        log.config("Dotbracket string is valid.");
        return true;
    }

    /**
     * Checks if a string is a date in format YYYY-MM-DD
     * 
     * @param datestring -
     *            String to check
     * @return boolean - true if string is valid, false otherwise
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
     * private method that generates the rnastructurealignmentHash
     * @throws BioDOMException
     */
    private void generateRnastructurealignmentHash() throws BioDOMException{
        try{
            // adding ids to hash
            final NodeList rnastructurealignmentNodes = dom.getElementsByTagName(ELEM_RNASTRUCTUREALIGNMENT);
            Element tmpRnastructureAlignElem = null;
            String idString = null;
            // evaluating rnastructure by rnastructure
            for(int i=0; i < rnastructurealignmentNodes.getLength(); i++){
                log.config("Evaluating rnastructurealignment element " + i);
                tmpRnastructureAlignElem = (Element)rnastructurealignmentNodes.item(i);
                if(tmpRnastructureAlignElem.hasAttributeNS(null,ATT_ID)){
                    idString = tmpRnastructureAlignElem.getAttributeNS(null,ATT_ID);
                }
                
                if((idString != null)&&(!idString.equals(""))){
                    // id attribute given -> make it an ID
                    tmpRnastructureAlignElem.setIdAttribute(ATT_ID, true);
                    log.config("Found id for rnastructurealignment element : '" + idString + "'. Added to Hash...");
                    rnastructurealignmentidsList.add(idString);
                }else{
                    // no id attribute in rnastructure element -> generate one
                    idString = generateId();
                    tmpRnastructureAlignElem.setAttribute(ATT_ID, idString);
                    tmpRnastructureAlignElem.setIdAttribute(ATT_ID, true);
                    log.config("Generated id for rnastructurealignment element : '" + idString + "'. Added to Hash...");
                    rnastructurealignmentidsList.add(idString);
                }
            }
        }catch(Exception e){
            log.severe("Internal Parsing Error! Message :" + e.getMessage());
            throw new BioDOMException("Internal Parsing Error! Error message: " + e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.RNAStructAlignmentMLInterface#appendConsensus(java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Hashtable, java.lang.String, java.lang.String)
     */
    public void appendConsensus(String consensusstructure, Double csenergy, 
            Double csprobability, List<Hashtable<String, Object>> structureprobs, 
            Hashtable<String, Object> sequence, String consensus_id, 
            String rnastructurealign_id) throws BioDOMException {
        // TODO FIXME
        log.warning("Method appendConsensus is currently not implemented...");
    }


}
