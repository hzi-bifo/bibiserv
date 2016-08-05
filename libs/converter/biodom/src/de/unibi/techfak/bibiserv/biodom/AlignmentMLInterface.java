package de.unibi.techfak.bibiserv.biodom;

import java.io.Reader;

import de.unibi.techfak.bibiserv.biodom.exception.BioDOMException;
/**
 * AlignmentML -- a XML variant representing (multiple) alignments
 * Namespace: http://hobit.sourceforge.net/xsds/20060602/alignmentML
 * Homepage http://hobit.sourceforge.net/xsds/20060602/alignmentML.xsd
 * 
 * @author Henning Mersch <hmersch@techfak.uni-bielefeld.de>, 
 *         Kai Löwenthal <kloewent@techfak.uni-bielefeld.de>,
 *         Jan Krüger <jkrueger@techfak.uni-bielefeld.de>,
 *         Sven Hartmeier <shartmei@techfak.uni-bielefeld.de>
 *         
 * @version 1.2
 */
public interface AlignmentMLInterface extends AbstractBioDOMInterface {


	////////////////////////
	// Converters
	////////////////////////
    /**
     * Append Clustal alignment to DOM
     * 
     * @param clustal       - String in Clustal format
     * @throws BioDOMException on failure
     */
	public abstract void appendClustal(String clustal) throws BioDOMException;
    
    /**
     * Append Clustal alignment to DOM
     * 
     * @param clustal - Reader containing alignment content in Clustal format
     * @throws BioDOMException on failure
     */
    public abstract void appendClustal(Reader clustal) throws BioDOMException;

    /**
     * Append Clustal alignment to DOM
     * 
     * @param clustal       - String in Clustal format
     * @param score         - Integer score value of the alignment
     */
	public abstract void appendClustal(String clustal, Integer score) throws BioDOMException;
    
    /**
     * Append Clustal alignment to DOM
     * 
     * @param clustal - Reader containing alignment content in Clustal format
     * @param score - Integer score value of the alignment
     * @throws BioDOMException on failure
     */
    public abstract void appendClustal(Reader clustal, Integer score) throws BioDOMException;

    /**
     * Append Clustal alignment to DOM
     * 
     * @param clustal       - String in Clustal format
     * @param seqtype       - int sequence type, use finals from AbstractBioDOM
     * @throws BioDOMException on failure
     */
	public abstract void appendClustal(String clustal, int seqtype) throws BioDOMException;
    
    /**
     * Append Clustal alignment to DOM
     * 
     * @param clustal       - reader containing alignment content in Clustal format
     * @param seqtype       - int sequence type, use finals from AbstractBioDOM
     * @throws BioDOMException on failure
     */  
    public abstract void appendClustal(Reader clustal, int seqtype) throws BioDOMException;

    
    /**
     * Append Clustal alignment to DOM
     * 
     * @param clustal       - String in Clustal format
     * @param score         - Integer score value of the alignment
     * @param seqtype       - int sequence type, use finals from AbstractBioDOM
     */
	public abstract void appendClustal(Reader clustal, Integer score,
			int seqtype) throws BioDOMException;
    
    /**
     * Append Clustal alignment to DOM
     * 
     * @param clustal   - reader containing alignment content in Clustal format
     * @param score     - Integer score value of the alignment
     * @param seqtype
     * @throws BioDOMException
     */
    public abstract void appendClustal(String clustal, Integer score, int seqtype) throws BioDOMException;

	/**
	 * Appends from FASTA to DOM (without a score and with guessing the sequence type)
	 * 
	 * @param fasta
	 *            String sequences as FASTA
	 * @exception BioDOMException
	 *                on failure
	 */
	public abstract void appendFastaAsAlignment(String fasta)
			throws BioDOMException;
    
    
    /**
     * Appends from FASTA to DOM (without a score and with guessing the sequence type)
     * 
     * @param fasta - Reader containing alignment content in Fasta format 
     * @throws BioDOMException
     */
    public abstract void appendFastaAsAlignment(Reader fasta) throws BioDOMException;

	/**
	 * Appends from FASTA to DOM (guessing the alignedFreeSequence)
	 * 
	 * @param fasta
	 *            String sequences as FASTA
	 * @param score
	 *            alignment score
	 * @exception BioDOMException
	 *                on failure
	 */
	public abstract void appendFastaAsAlignment(String fasta, Integer score)
			throws BioDOMException;
    
    /**
     * Appends from FASTA to DOM (guessing the sequenceType)
     * 
     * @param fasta - Reader containing alignment content in Fasta format
     * @param score - alignment score
     * @throws BioDOMException
     */
    public abstract void appendFastaAsAlignment(Reader fasta, Integer score) throws BioDOMException;

	/**
	 * Appends from FASTA to DOM (without a score)
	 * 
	 * @param fasta
	 *            String sequences as FASTA
	 * @param seqType - int defining the sequence type 
	 *            
	 * @exception BioDOMException
	 *                on failure
	 */
	public abstract void appendFastaAsAlignment(String fasta, int seqType)
			throws BioDOMException;
    
    
    /**
     * Appends from FASTA to DOM (without a score)
     * 
     * @param fasta - Reader containing alignment in Fasta format
     * @param seqType - int defining the sequence type 
     * @throws BioDOMException
     */
    public abstract void appendFastaAsAlignment(Reader fasta, int seqType) throws BioDOMException;

	/**
	 * Appends from FASTA to DOM
	 * 
	 * @param fasta
	 *            String sequences as FASTA
	 * @param seqType 
     *            int defining the sequence type 
	 * @param score
	 *            Integer of alignment, null for no score
	 * @exception BioDOMException
	 *                on failure
	 */
	public abstract void appendFastaAsAlignment(String fasta, Integer score,
			int seqType) throws BioDOMException;
    
    
    /**
     * Appends from FASTA to DOM
     * 
     * @param fasta
     *            Reader containing alignment information
     * @param seqType 
     *            int defining the sequence type 
     * @param score
     *            Integer of alignment, null for no score
     * @exception BioDOMException
     *                on failure
     */
    public abstract void appendFastaAsAlignment(Reader fasta, Integer score, int seqType) throws BioDOMException;

	/**
	 * Converts the 1st alignment to FASTA
	 * 
	 * @return String sequences as FASTA
	 * @exception BioDOMException
	 *                if error on DOM
	 */
	public abstract String toFasta() throws BioDOMException;

	/**
	 * Converts a by-number selected alignment to FASTA
	 * 
	 * @param no
	 *            int no of alignment
	 * @return String sequences as FASTA
	 * @exception BioDOMException
	 *                if error on DOM
	 */
	public abstract String toFasta(int no) throws BioDOMException;

}