package de.unibi.techfak.bibiserv.biodom;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import de.unibi.techfak.bibiserv.biodom.exception.BioDOMException;

public interface SequenceMLInterface extends AbstractBioDOMInterface {



	// //////////////////////
	// methodes
	// //////////////////////
	/**
	 * Convert whole SequenceML Object to a string in Fasta format.
	 * 
	 * @return sequences from current object in Fasta format.
	 * @throws Throws
	 *             BioDOMException if error on DOM
	 */
	public abstract String toFasta() throws BioDOMException;

    
    /**
     * Convert whole SequenceML Object to a string in Fasta format.
     * 
     * @params wrapwidth defines the linelength; must be > 0 or -1 for non wrapped lines
     * @return sequences from current object in Fasta format.
     * @throws Throws
     *             BioDOMException if error on DOM
     */
    public abstract String toFasta(int wrapwidth) throws BioDOMException;
    
    
	/**
	 * Appends a File in Fasta format to current SequenceML DOM. SequenceType
	 * will be from type FREE (freeSequence)
	 * 
	 * @param fasta
	 *            File containing one or more sequences in Fasta format.
	 * @throws BioDOMException
	 *             on failure
	 */
	public abstract void appendFasta(File fasta) 
            throws BioDOMException, IOException;

	/**
	 * Appends a String in Fasta format to current SequenceML DOM. SequenceType
	 * will be from type FREE (freeSequence)
	 * 
	 * @param fasta
	 *            String containing one or more sequences in Fasta format.
	 * @throws BioDOMException
	 *             on failure
	 */
	public abstract void appendFasta(String fasta) throws BioDOMException, IOException;

    /**
     * Appends a String in Fasta format to current SequenceML DOM. 
     * 
     * @param fasta
     *            String containing one or more sequences in Fasta format.
     * @param seqType
     *            int determining the sequence type. Use one of the finals
     *            FREE, AMINOACID or NUCLEICACID
     * @throws BioDOMException
     *             on failure
     */
	public abstract void appendFasta(String fasta, int seqType)
			throws BioDOMException, IOException;

    /**
     * Appends a File in Fasta format to current SequenceML DOM. 
     * 
     * @param fasta
     *            String containing one or more sequences in Fasta format.
     * @param seqType
     *            int determining the sequence type. Use one of the finals
     *            FREE, AMINOACID or NUCLEICACID
     * @throws BioDOMException
     *             on failure
     */
    public abstract void appendFasta(File fasta, int seqType)
            throws BioDOMException, IOException;

	/**
	 * Appends Fasta format data to current SequenceML DOM. SequenceType
	 * will be of type FREE (freeSequence)
	 * 
	 * @param fasta
	 *            Reader representing a data source containing one or more sequences in Fasta format.
	 * @throws BioDOMException
	 *             on failure
	 */
	public abstract void appendFasta(Reader fasta) 
            throws BioDOMException, IOException;
    
    /**
     * Appends a File in Fasta format to current SequenceML DOM. 
     * 
     * @param fasta
     *            Reader representing a data source containing one or more sequences in Fasta format.
     * @param seqType
     *            int determining the sequence type. Use one of the finals
     *            FREE, AMINOACID or NUCLEICACID
     * @throws BioDOMException
     *             on failure
     */
    public abstract void appendFasta(Reader fasta, int seqType)
            throws BioDOMException, IOException;
	
    /**
     * Append a sequence to the DOM
     * @param sequence          - the sequence string
     * @param seqType           - the sequence type. must be one of the following finals:
     *                            FREE, AMINOACID or NUCLEICACID
     * @throws BioDOMException  - on error
     */
    public abstract void appendSequence(String sequence, int seqType)
            throws BioDOMException, IOException;

    /**
     * Append a sequence to the DOM
     * @param id                - the sequence id
     * @param sequence          - the sequence string
     * @param seqType           - the sequence type. must be one of the following finals:
     *                            FREE, AMINOACID or NUCLEICACID
     * @throws BioDOMException  - on error
     */
    public abstract void appendSequence(String id, String sequence, 
            int seqType) throws BioDOMException;

    /**
     * Append a sequence to the DOM
     * @param id                - the sequence id
     * @param description       - description of the sequence
     * @param sequence          - the sequence string
     * @param seqType           - the sequence type. must be one of the following finals:
     *                            FREE, AMINOACID or NUCLEICACID
     * @throws BioDOMException  - on error
     */
    public abstract void appendSequence(String id, String description,
            String sequence, int seqType) throws BioDOMException;
    
	/**
	 * Returns a Map containing sequenceinformation belong to given sequence id from current object.
	 * <table>
	 *  <tr>
	 *     <th>KEY</th><th>description</th>
	 *  </tr>
	 *  <tr>
	 *      <td>id&lt;String&gt;</td><td>id&lt;String&gt; of this sequence (same as parameter)</td>
	 *  </tr>
	 *  <tr>
	 *      <td>id&lt;String&gt;</td><td>name&lt;String&gt; of this sequence</td>
	 *  </tr>
	 *  <tr>
	 *      <td>synonyms&lt;List&lt;String&gt;&gt;</td><td>synonyms&lt;List&lt;String&gt;&gt; to name of this sequence</td>
	 *  </tr>
	 *  <tr>
	 *      <td>description&lt;String&gt;</td><td>description&lt;String&gt; of this sequence</td>
	 *  </tr>
	 *  <tr>
	 *      <td>sequence&lt;String&gt;</td><td>raw sequence&lt;String&gt;</td>
	 *  </tr>
	 *  <tr>
	 *      <td>sequenceType&lt;String&gt;</td><td>sequence type &lt;Integer&gt;, one of NUCLEICACID, AMINOACID or FREE)</td>
	 *  </tr>
	 * <tr>
	 *      <td>sequenceType_name&lt;String&gt;</td><td>sequence type name &lt;String&gt;, one of NUCLEICACID, AMINOACID or FREE;</td>
	 *  </tr>
	 * </table>
	 *  
	 * @param id -
	 *            the id of the sequenceinformation to be returned.
	 * @return Returns a Map containing sequenceinformation.
	 * @throws Throws
	 *             BioDOMException if sequence with given id is not found
	 */
	public abstract Map getSequence(String id) throws BioDOMException;

	/**
	 * Return a List containing all sequence id's of this object.
	 * 
	 * @return Return a list of sequence id's.
	 */
	public abstract List<String> getIDlist();
}