package de.unibi.techfak.bibiserv.biodom;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.unibi.techfak.bibiserv.biodom.exception.BioDOMException;
import de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBox;

public class SequenceML extends AbstractBioDOM implements SequenceMLInterface {
    /**
     * SequenceML -- a XML variant of FASTA namespace:
     * http://hobit.sourceforge.net/xsds/20060102/sequenceML schemalocation :
     * http://hobit.sourceforge.net/xsds/20060102/sequenceML.xsd
     * 
     * @author jan Krueger <jkrueger@techfak.uni-bielefeld.de>, Henning Mersch
     *         <hmersch@techfak.uni-bielefeld.de> (first release), Kai
     *         Loewenthal <kloewent@techfak.uni-bielefeld.de>, Sven Hartmeier <shartmei@techfak.uni-bielefeld.de>
     * 
     * @version
     */

    // The Logger
    private static Logger log = Logger.getLogger(SequenceML.class.toString());

    // a list containing unique sequence id's
    private List<String> idlist = new ArrayList<String>();

    // xml elements
    protected static final String ELEM_SEQUENCEML = "sequenceML";
    protected static final String ELEM_SEQUENCE = "sequence";
    protected static final String ELEM_NAME = "name";
    protected static final String ELEM_SYNONYMS = "synonyms";
    protected static final String ELEM_DESCRIPTION = "description";
    protected static final String ELEM_AMINOACIDSEQUENCE = "aminoAcidSequence";
    protected static final String ELEM_NUCLEICACIDSEQUENCE = "nucleicAcidSequence";
    protected static final String ELEM_FREESEQUENCE = "freeSequence";
    protected static final String ELEM_EMPTYSEQUENCE = "emptySequence";
    protected static final String ELEM_CROSSREFS = "crossrefs";
    protected static final String ELEM_COMMENT = "comment";

    // xml attributes
    protected static final String ATT_SEQID = "seqID";
    protected static final String ATT_VERSION = "version";

    // ///////////////////////
    // Contructors
    // //////////////////////
    // NO INPUT
    /**
     * creates a new SequenceML object for processing DOM Documents as
     * http://hobit.sourceforge.net/xsds/2005/sequenceML.xsd (logger will be
     * created locally for console useage)
     * 
     * @exception BioDOMException
     *                on failure
     */
    public SequenceML() throws BioDOMException {
        this(null, null, null);
    }

    // catalogproperties INPUT
    /**
     * creates a new SequenceML object for processing DOM Documents as
     * http://hobit.sourceforge.net/xsds/2005/sequenceML.xsd (logger will be
     * created locally for console useage)
     * 
     * @param catalogproperties
     *            path of Catalog.properties file
     * @exception BioDOMException
     *                on failure
     */
    public SequenceML(String catalogpropertyfile) throws BioDOMException {
        this(catalogpropertyfile, null, null);
    }

    // DOM INPUT
    /**
     * creates a new SequenceML object for processing DOM Documents as
     * http://hobit.sourceforge.net/xsds/2005/sequenceML.xsd (logger will be
     * created locally for console useage)
     * 
     * @param submitted_dom
     *            DOM for processing
     * @exception BioDOMException
     *                on failure
     */
    public SequenceML(Document submitted_dom) throws BioDOMException {
        this(null, submitted_dom, null);
    }

    /**
     * creates a new SequenceML object for processing DOM Documents as
     * http://hobit.sourceforge.net/xsds/2005/sequenceML.xsd
     * 
     * @param catalogproperties
     *            path of Catalog.properties file
     * @param submitted_dom
     *            DOM for processing
     * @exception BioDOMException
     *                on failure
     */
    public SequenceML(String catalogpropertyfile, Document submitted_dom) throws BioDOMException {
        this(catalogpropertyfile, submitted_dom, null);
    }

    // BioDOMWarningBox INPUT
    /**
     * creates a new SequenceML object for processing DOM Documents as
     * http://hobit.sourceforge.net/xsds/2005/sequenceML.xsd (logger will be
     * created locally for console useage)
     * 
     * @exception BioDOMException
     *                on failure
     */
    public SequenceML(BioDOMWarningBox warningBox) throws BioDOMException {
        this(null, null, warningBox);
    }

    // catalogproperties & BioDOMWarningBox INPUT
    /**
     * creates a new SequenceML object for processing DOM Documents as
     * http://hobit.sourceforge.net/xsds/2005/sequenceML.xsd (logger will be
     * created locally for console useage)
     * 
     * @param catalogproperties
     *            path of Catalog.properties file
     * @exception BioDOMException
     *                on failure
     */
    public SequenceML(String catalogpropertyfile, BioDOMWarningBox warningBox) throws BioDOMException {
        this(catalogpropertyfile, null, warningBox);
    }

    // DOM & BioDOMWarningBox INPUT
    /**
     * creates a new SequenceML object for processing DOM Documents as
     * http://hobit.sourceforge.net/xsds/2005/sequenceML.xsd (logger will be
     * created locally for console useage)
     * 
     * @param submitted_dom
     *            DOM for processing
     * @param warningBox
     *            BioDOMWarningBox for processing warnings
     * @exception BioDOMException
     *                on failure
     */
    public SequenceML(Document submitted_dom, BioDOMWarningBox warningBox) throws BioDOMException {
        this(null, submitted_dom, warningBox);
    }

    // catalogproperties & DOM & BioDOMWarningBox INPUT
    /**
     * creates a new SequenceML object for processing DOM Documents as
     * http://hobit.sourceforge.net/xsds/2005/sequenceML.xsd
     * 
     * @param catalogproperties
     *            path of Catalog.properties file
     * @param submitted_dom
     *            DOM for processing
     * @param warningBox
     *            BioDOMWarningBox for processing warnings
     * @exception BioDOMException
     *                on failure
     */
    public SequenceML(String catalogpropertyfile, Document submitted_dom, BioDOMWarningBox warningBox)
            throws BioDOMException {
        super(catalogpropertyfile);
        isNillable = true;
        if (warningBox != null) {
            this.setWarningBox(warningBox);
        }
        if (submitted_dom != null) {
            this.setDom(submitted_dom);
        } else {
            initDOM();
        }
    }

    // //////////////////////
    // getter and Setter
    // //////////////////////
    
    /**
     * Overwrites AbstractBioDOM.setdom.
     * 
     * @param Document to be set
     */
    public void setDom(Document submitted_dom) throws BioDOMException{
        super.setDom(submitted_dom);
        initIDLIST();
        
    }
    
    /**
     * Overwrites AbstractBioDOM.setdom.
     * 
     * @param Document in String representation to be set
     */
    public void setDom(String documentasstring) throws BioDOMException{
        super.setDom(documentasstring);
        initIDLIST();
    }

    // //////////////////////
    // Converter
    // /////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#toFasta()
     */
    public String toFasta()throws BioDOMException {
        return toFasta(72);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#toFasta()
     */
    public String toFasta(final int wrapwidth) throws BioDOMException {
        // validate current SequenceML
        if (!validate()) {
            throw new BioDOMException("DOM validation failed");
        }
        StringBuffer fasta = new StringBuffer();
        char[] tmpfasta;
        // log.debug("trying to get all Elements 'sequence'");
        final NodeList sequences = dom.getDocumentElement().getElementsByTagNameNS(NAMESPACE,ELEM_SEQUENCE);
        for (int i = 0; i < sequences.getLength(); i++) {
            // log.debug("processing 'sequence' " + i);
            final Element sequence = (Element) sequences.item(i);
            // log.debug("trying to get attribute 'seqID' from 'sequence'");
            // every sequence must have an ID
            fasta = fasta.append(">" + sequence.getAttributeNS(null,ATT_SEQID).replaceAll(" ", ""));
            // add description if defined
            if (sequence.getElementsByTagNameNS(NAMESPACE,ELEM_DESCRIPTION).getLength() > 0) {
                // log.debug("trying to get first child Element 'description'");
                final String description = getTextContent(sequence.getElementsByTagNameNS(NAMESPACE,ELEM_DESCRIPTION).item(0));
                if (description != null) {
                    fasta = fasta.append(" " + description);
                }
            }
            fasta = fasta.append(System.getProperty("line.separator"));
            if (sequence.getElementsByTagNameNS(NAMESPACE,ELEM_NUCLEICACIDSEQUENCE).getLength() > 0) {
                tmpfasta = (getTextContent(sequence.getElementsByTagNameNS(NAMESPACE,ELEM_NUCLEICACIDSEQUENCE).item(0)))
                        .toCharArray();
            } else if (sequence.getElementsByTagNameNS(NAMESPACE,ELEM_AMINOACIDSEQUENCE).getLength() > 0) {
                tmpfasta = (getTextContent(sequence.getElementsByTagNameNS(NAMESPACE,ELEM_AMINOACIDSEQUENCE).item(0)))
                        .toCharArray();
            } else {
                tmpfasta = (getTextContent(sequence.getElementsByTagNameNS(NAMESPACE,ELEM_FREESEQUENCE).item(0))).toCharArray();
            }

            /* TODO: Add a more efficient method for line-wrapping the sequences (than char-by-char)!! */
             //define line length
            if (wrapwidth == -1) {
                fasta.append(tmpfasta);
            } else if (wrapwidth > 0) {
            
                for (int cp = 0; cp < tmpfasta.length; cp++) {
                    // append next character
                    fasta.append(tmpfasta[cp]);
                    // if we've appended the linebreaking number of characters,
                    // append a newline...
                    if (((cp + 1) % wrapwidth) == 0) {
                        fasta = fasta.append(System.getProperty("line.separator"));
                    }
                }
            } else {
                throw new BioDOMException("SequenceML.toFasta(int wrapwidth) : wrapwidth must be -1 or > 0");
            }

            if (i != sequences.getLength() - 1) {// a new seq will be
                // appended
                fasta = fasta.append(System.getProperty("line.separator"));
            }
        }
        // SH: add a newline after all sequences have been written
        fasta = fasta.append(System.getProperty("line.separator"));
        return (fasta.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#appendFasta(java.io.File)
     */
    public void appendFasta(final File fasta) throws BioDOMException, IOException {
        appendFasta(fasta, UNKNOWNSEQUENCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#appendFasta(java.lang.String)
     */
    public void appendFasta(final String fasta) throws BioDOMException, IOException {
        appendFasta(fasta, UNKNOWNSEQUENCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#appendFasta(java.io.Reader)
     */
    public void appendFasta(final Reader fasta) throws BioDOMException, IOException {
        appendFasta(fasta, UNKNOWNSEQUENCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#appendFasta(java.io.File,
     *      java.lang.String)
     */
    public void appendFasta(final File fasta, final int seqType) throws BioDOMException, IOException {
        appendFasta(new FileReader(fasta), seqType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#appendFasta(java.lang.String,
     *      java.lang.String)
     */
    public void appendFasta(final String fasta, final int seqType) throws BioDOMException, IOException {
        appendFasta(new StringReader(fasta), seqType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#appendFasta(java.io.Reader,
     *      int)
     */
    public void appendFasta(final Reader fasta, final int seqType) throws BioDOMException, IOException {
        // read from Fasta Reader to StringBuffer ...
        final LineNumberReader br = new LineNumberReader(fasta);

        String id = null;
        String description = null;
        StringBuffer sequenceBuffer = new StringBuffer();
        String s;

        while ((s = br.readLine()) != null) {
            if (s.equals("")) {
                // skip empty line! exception otherwise!
                log.warning("Empty line detected. Skipping empty line!");
                warningBox.appendWarning(this, "Empty line detected. Skipping empty line!");
            } else {
                if (s.charAt(0) != '>') { // if line does NOT start with ">"
                                            // assuming it's contains sequence
                                            // information
                    if (id != null) {
                        sequenceBuffer = sequenceBuffer.append(s);
                    } else {
                        throw new BioDOMException("line " + br.getLineNumber() + ": No id before sequence starts!");
                    }
                } else { // line start with ">" or it is the last line
                    // if any id exists, we've parsed an sequence before and can
                    // add it to current dom
                    if (id != null) {
                        appendSequence(id, description, sequenceBuffer.toString(), seqType);
                        sequenceBuffer = new StringBuffer();
                    }
                    // removing leading whitespaces and get id and description
                    final String[] idDesc = s.substring(1).trim().split(" ", 2);
                    // get id
                    if (idDesc.length > 0) {
                        id = idDesc[0];
                        if (!unique(id)) {
                            id = id + "_" + generateId();
                            warningBox.appendWarning(this, "line " + br.getLineNumber()
                                    + ": Sequence has a non unique ID! Creating a unique ID!\n");
                        }
                    } else {
                        id = generateId();
                        warningBox.appendWarning(this, "line " + br.getLineNumber()
                                + ": Sequence has no ID! Creating a unique ID!\n");
                    }
                    // get description if given
                    if (idDesc.length > 1) {
                        description = idDesc[1];
                    } else {
                        description = null;
                    }
                }
            }
        }
        // if sequenceBuffer ist empty, we have an empty sequence and therefore
        // throw an exception...
        if (sequenceBuffer.length() == 0)
            throw new BioDOMException("Sequence contains no sequence information (empty sequence)!");
        // ...otherwise, the last sequence contains sequence information, so we
        // add the last sequence
        appendSequence(id, description, sequenceBuffer.toString(), seqType);
        //close Reader
        br.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#appendSequence(java.lang.String,
     *      int)
     */
    public void appendSequence(final String sequence, final int seqType) throws BioDOMException {
        appendSequence(generateId(), null, sequence, seqType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#appendSequence(java.lang.String,
     *      java.lang.String, int)
     */
    public void appendSequence(final String id, final String sequence, final int seqType) throws BioDOMException {
        appendSequence(id, null, sequence, seqType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#appendSequence(java.lang.String,
     *      java.lang.String, java.lang.String, int)
     */
    public void appendSequence(final String id, final String description, final String sequence, final int seqType)
            throws BioDOMException {
        // prepare patterns for parsing input
        final Pattern nucleoacidaPattern = Pattern.compile("^[" + NUCLEICACID_CHARS + "]+$");
        final Pattern aminoacidPattern = Pattern.compile("^[" + AMINOACID_CHARS + "]+$");
        // get root element
        final Element sequences = dom.getDocumentElement();
        // create a new Sequence element
        final Element eSequence = dom.createElementNS(NAMESPACE, ELEM_SEQUENCE);
        sequences.appendChild(eSequence);
        // / ... and add id element to dom and ...
        eSequence.setAttributeNS(null, ATT_SEQID, id);
        // add id to internal id list to determine uniques of id's
        idlist.add(id);
        // add description if set
        if (description != null) {
            final Element eDescription = dom.createElementNS(NAMESPACE, ELEM_DESCRIPTION);
            eSequence.appendChild(eDescription);
            eDescription.appendChild(dom.createTextNode(description));
        }
        // add content
        if (seqType == AMINOACID) {
            final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_AMINOACIDSEQUENCE);
            eSequence.appendChild(eSeq);
            if (!aminoacidPattern.matcher(sequence).matches()) {
                throw new BioDOMException("unallowed char (allowed: " + AMINOACID_CHARS + ") in aminoaacid-sequence "
                        + sequence);
            }
            eSeq.appendChild(dom.createTextNode(sequence));
        } else if (seqType == NUCLEICACID) {
            final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_NUCLEICACIDSEQUENCE);
            eSequence.appendChild(eSeq);
            if (!nucleoacidaPattern.matcher(sequence).matches()) {
                throw new BioDOMException("unallowed char (allowed: " + NUCLEICACID_CHARS
                        + ") in nucleicacid-sequence " + sequence);
            }
            eSeq.appendChild(dom.createTextNode(sequence));
        } else if (seqType == UNKNOWNSEQUENCE) {
            if (!aminoacidPattern.matcher(sequence).matches()) { // unallowed
                                                                    // char ->
                                                                    // free
                                                                    // sequence
                final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_FREESEQUENCE);
                eSequence.appendChild(eSeq);
                eSeq.appendChild(dom.createTextNode(sequence));
            } else if (!nucleoacidaPattern.matcher(sequence).matches()) { // aminoacid
                                                                            // (contains
                                                                            // chars
                                                                            // that
                                                                            // are
                                                                            // only
                                                                            // allowed
                                                                            // in
                                                                            // aminoacid
                final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_AMINOACIDSEQUENCE);
                eSequence.appendChild(eSeq);
                eSeq.appendChild(dom.createTextNode(sequence));
            } else { // aminoacid or nucleicacid? -> depends on percentage of
                        // ACGTU (current >= 90%)
                final String uSeq = sequence.toUpperCase();
                int cnt = 0, length = uSeq.length();
                for (int i = 0; i < uSeq.length(); i++) {
                    final char c = uSeq.charAt(i);
                    if (c == 'A' || c == 'C' || c == 'G' || c == 'T' || c == 'U')
                        cnt++;
                }
                if ((double) cnt / (double) length >= 0.9) { // over 90% of
                                                                // sequence are
                                                                // ACGTU ->
                                                                // probably
                                                                // nucleicacid
                    final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_NUCLEICACIDSEQUENCE);
                    eSequence.appendChild(eSeq);
                    eSeq.appendChild(dom.createTextNode(sequence));
                    warningBox.appendWarning(this, "No sequence type defined. Determined sequence as Nucleicacid");
                } else { // over 10% of sequence are not ACGTU -> probably
                            // aminoacid
                    final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_AMINOACIDSEQUENCE);
                    eSequence.appendChild(eSeq);
                    eSeq.appendChild(dom.createTextNode(sequence));
                    warningBox.appendWarning(this, "No sequence type defined. Determined sequence as Aminoacid");
                }
            }
        } else {
            final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_FREESEQUENCE);
            eSequence.appendChild(eSeq);
            eSeq.appendChild(dom.createTextNode(sequence));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#getSequence(java.lang.String)
     */
    public Map getSequence(final String id) throws BioDOMException {
        if (unique(id)) {
            throw new BioDOMException("The given sequence ID (" + id + ")is not in current sequenceML object");
        }
        final Map<String, Object> hm = new Hashtable<String, Object>();
        hm.put("id", id);
        final NodeList nl = dom.getElementsByTagNameNS(NAMESPACE,ELEM_SEQUENCE);
        for (int counter = 0; counter < nl.getLength(); ++counter) {
            final Element seq = (Element) nl.item(counter);
            if (seq.getAttributeNS(null,ATT_SEQID).equals(id)) {
                // check for name and add it
                if (seq.getElementsByTagNameNS(NAMESPACE,ELEM_NAME).getLength() > 0) {
                    hm.put("name", seq.getElementsByTagNameNS(NAMESPACE,ELEM_NAME).item(0).getTextContent());
                }

                // check for synonyms
                if (seq.getElementsByTagNameNS(NAMESPACE,ELEM_SYNONYMS).getLength() > 0) {
                    final NodeList synonymNodes = seq.getElementsByTagNameNS(NAMESPACE,ELEM_SYNONYMS);
                    final List<String> synonyms = new ArrayList<String>();
                    for (int i = 0; i < synonymNodes.getLength(); i++) {
                        synonyms.add(((Element) synonymNodes.item(i)).getTextContent());
                    }
                    hm.put("synonyms", synonyms);
                }

                // check for description and add it
                if (seq.getElementsByTagNameNS(NAMESPACE,ELEM_DESCRIPTION).getLength() > 0) {
                    hm.put("description", seq.getElementsByTagNameNS(NAMESPACE,ELEM_DESCRIPTION).item(0).getTextContent());
                }
                // check for nucleicacid, aminoacod or freesequence or empty!
                if (seq.getElementsByTagNameNS(NAMESPACE,ELEM_NUCLEICACIDSEQUENCE).getLength() > 0) {
                    hm.put("sequence", seq.getElementsByTagNameNS(NAMESPACE,ELEM_NUCLEICACIDSEQUENCE).item(0).getTextContent());
                    hm.put("sequenceType_name", NUCLEICACID_NAME);
                    hm.put("sequenceType", NUCLEICACID);
                } else if (seq.getElementsByTagNameNS(NAMESPACE,ELEM_AMINOACIDSEQUENCE).getLength() > 0) {
                    hm.put("sequence", seq.getElementsByTagNameNS(NAMESPACE,ELEM_AMINOACIDSEQUENCE).item(0).getTextContent());
                    hm.put("sequenceType_name", AMINOACID_NAME);
                    hm.put("sequenceType", AMINOACID);
                } else if (seq.getElementsByTagNameNS(NAMESPACE,ELEM_FREESEQUENCE).getLength() > 0) {
                    hm.put("sequence", seq.getElementsByTagNameNS(NAMESPACE,ELEM_FREESEQUENCE).item(0).getTextContent());
                    hm.put("sequenceType_name", FREESEQUENCE_NAME);
                    hm.put("sequenceType", FREE);
                } else {
                    hm.put("sequenceType_name", EMPTYSEQUENCE_NAME);
                    hm.put("sequenceType", EMPTYSEQUENCE);
                }
            }
        }
        return hm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.SequenceMLInterface#getIDlist()
     */
    public List<String> getIDlist() {
        return idlist;
    }

    // ////////////////////////////////
    // //// Private Methods
    // ////////////////////////////////
    /**
     * generates an empty DOM Tree
     */
    private void initDOM() {
        dom = DB.newDocument();
        final Element root = dom.createElementNS(NAMESPACE, ELEM_SEQUENCEML);
        root.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,"schemaLocation", NAMESPACE+" "+NSLOCATION);
        dom.appendChild(root);
        dom.normalizeDocument();
        log.config("DOM ready...");
    }

    
    /**
     * initialize the idlist after set a dom to current object
     *
     */
    private void initIDLIST(){
        //      clear idlist
        idlist.clear();
        
        // get id from dom
        NodeList sequences = dom.getElementsByTagNameNS(NAMESPACE,ELEM_SEQUENCE);
        for (int i = 0; i < sequences.getLength(); ++i){
            Element sequence = (Element)sequences.item(i);
            //append id's to idlist
            idlist.add(sequence.getAttributeNS(null,ATT_SEQID));
        }
    }
    
    /**
     * 
     * @param id -
     *            id to be test
     * @return true if the id is unqiue in this object, false otherwise
     */
    private boolean unique(final String id) {
        for (String i : idlist) {
            if (i.equals(id)) {
                return false;
            }
        }
        return true;
    }
}
