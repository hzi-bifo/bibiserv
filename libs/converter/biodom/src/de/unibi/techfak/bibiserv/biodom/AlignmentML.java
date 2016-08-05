package de.unibi.techfak.bibiserv.biodom;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.unibi.techfak.bibiserv.biodom.exception.BioDOMException;
import de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBox;

public class AlignmentML extends AbstractBioDOM implements AlignmentMLInterface {
    /**
     * AlignmentML -- a XML variant of FASTA representing (multiple) alignments
     * Namespace: http://hobit.sourceforge.net/xsds/20060602/alignmentML
     * Homepage http://hobit.sourceforge.net/xsds/20060602/alignmentML.xsd
     * 
     * @author Henning Mersch <hmersch@techfak.uni-bielefeld.de>, Kai Löwenthal
     *         <kloewent@techfak.uni-bielefeld.de>, Jan Krüger
     *         <jkrueger@techfak.uni-bielefeld.de>, Sven Hartmeier
     *         <shartmei@techfak.uni-bielefeld.de>
     * @version 1.1
     */

    /** private instance of a logger */
    private static final Logger log = Logger.getLogger(AlignmentML.class.toString());

    // xml elements
    protected static final String ELEM_ALIGNMENTML = "alignmentML";
    protected static final String ELEM_ALIGNMENT = "alignment";
    protected static final String ELEM_SEQUENCE = "sequence";
    protected static final String ELEM_NAME = "name";
    protected static final String ELEM_SYNONYMS = "synonyms";
    protected static final String ELEM_DESCRIPTION = "description";
    protected static final String ELEM_ALIGNEDAMINOACIDSEQUENCE = "alignedAminoAcidSequence";
    protected static final String ELEM_ALIGNEDNUCLEICACIDSEQUENCE = "alignedNucleicAcidSequence";
    protected static final String ELEM_ALIGNEDFREESEQUENCE = "alignedFreeSequence";
    protected static final String ELEM_CROSSREFS = "crossrefs";
    protected static final String ELEM_COMMENT = "comment";

    // xml attributes
    protected static final String ATT_VERSION = "version";
    protected static final String ATT_SCORE = "score";
    protected static final String ATT_SEQID = "seqID";

    // string patterns
    protected static final String PATTERN_LOWERCASE = "[a-z]+";
    protected static final String PATTERN_NUCLEICACID = "^[A|C|G|U|T|R|Y|M|K|S|W|H|B|V|D|N|X|-]+$";
    protected static final String PATTERN_AMINOACID = "^[A|R|N|D|C|Q|E|G|H|I|L|K|M|F|P|S|T|U|W|Y|V|-]+$";
    protected static final String PATTERN_FREE = ".+";

    // prepare patterns for parsing input
    protected static final Pattern lcPattern = Pattern.compile(PATTERN_LOWERCASE);
    protected static final Pattern naPattern = Pattern.compile(PATTERN_NUCLEICACID);
    protected static final Pattern aaPattern = Pattern.compile(PATTERN_AMINOACID);
    protected static final Pattern fsPattern = Pattern.compile(PATTERN_FREE);

    // ///////////////////////
    // Contructors
    // ///////////////////////
    // NO INPUT
    /**
     * creates a new AlignmentML object for processing DOM Documents as
     * http://hobit.sourceforge.net/xsds/2005/alignmentML.xsd (logger will be
     * created locally for console useage)
     * 
     * @exception BioDOMException
     *                on failure
     */
    public AlignmentML() throws BioDOMException {
        this(null, null, null);
    }

    /**
     * creates a new AlignmentML object for processing DOM Documents as
     * http://hobit.sourceforge.net/xsds/2005/alignmentML.xsd (logger will be
     * created locally for console useage)
     * 
     * @param warningBox
     *            BioDOMWarningBox for processing warnings
     * @exception BioDOMException
     *                on failure
     */
    public AlignmentML(BioDOMWarningBox warningBox) throws BioDOMException {
        this(null, null, warningBox);
    }

    /**
     * creates a new AlignmentML object for processing DOM Documents
     * 
     * @param catalogproperties
     *            path of Catalog.properties file
     * @exception BioDOMException
     *                on failure
     */
    public AlignmentML(String catalogpropertyfile) throws BioDOMException {
        this(catalogpropertyfile, null, null);
    }

    /**
     * creates a new AlignmentML object for processing DOM Documents
     * 
     * @param catalogproperties
     *            path of Catalog.properties file
     * @param warningBox
     *            BioDOMWarningBox for processing warnings
     * @exception BioDOMException
     *                on failure
     */
    public AlignmentML(String catalogpropertyfile, BioDOMWarningBox warningBox) throws BioDOMException {
        this(catalogpropertyfile, null, warningBox);
    }

    // DOM INPUT
    /**
     * creates a new AlignmentML object for processing DOM Documents
     * 
     * @param submitted_dom
     *            DOM for processing
     * @exception BioDOMException
     *                on failure
     */
    public AlignmentML(Document submitted_dom) throws BioDOMException {
        this(null, submitted_dom, null);
    }

    /**
     * creates a new AlignmentML object for processing DOM Documents
     * 
     * @param submitted_dom
     *            DOM for processing
     * @param warningBox
     *            BioDOMWarningBox for processing warnings
     * @exception BioDOMException
     *                on failure
     */
    public AlignmentML(Document submitted_dom, BioDOMWarningBox warningBox) throws BioDOMException {
        this(null, submitted_dom, warningBox);
    }

    /**
     * creates a new AlignmentML object for processing DOM Documents
     * 
     * @param catalogproperties
     *            path of Catalog.properties file
     * @param submitted_dom
     *            DOM for processing
     * @exception BioDOMException
     *                on failure
     */
    public AlignmentML(String catalogpropertyfile, Document submitted_dom) throws BioDOMException {
        this(catalogpropertyfile, submitted_dom, null);
    }

    /**
     * creates a new AlignmentML object for processing DOM Documents
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
    public AlignmentML(String catalogpropertyfile, Document submitted_dom, BioDOMWarningBox warningBox)
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
    // Converter
    // /////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendClustal(java.lang.String)
     */
    public void appendClustal(final String clustal) throws BioDOMException {
        appendClustal(clustal, null, FREE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendClustal(java.io.Reader)
     */
    public void appendClustal(final Reader clustal) throws BioDOMException {
        appendClustal(clustal, null, UNKNOWNSEQUENCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendClustal(java.lang.String,
     *      java.lang.Integer)
     */
    public void appendClustal(final String clustal, final Integer score) throws BioDOMException {
        appendClustal(clustal, score, UNKNOWNSEQUENCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendClustal(java.io.Reader,
     *      java.lang.Integer)
     */
    public void appendClustal(final Reader clustal, final Integer score) throws BioDOMException {
        appendClustal(clustal, score, UNKNOWNSEQUENCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendClustal(java.lang.String,
     *      int)
     */
    public void appendClustal(final String clustal, final int seqtype) throws BioDOMException {
        appendClustal(clustal, null, seqtype);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendClustal(java.io.Reader,
     *      int)
     */
    public void appendClustal(final Reader reader, final int seqtype) throws BioDOMException {
        appendClustal(reader, null, seqtype);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendClustal(java.lang.String,
     *      java.lang.Integer, int)
     */
    public void appendClustal(final String clustal, final Integer score, final int seqtype) throws BioDOMException {
        appendClustal(new StringReader(clustal), score, seqtype);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendClustal(java.io.Reader,
     *      java.lang.Integer, int)
     */
    public void appendClustal(final Reader clustal, final Integer score, final int seqtype) throws BioDOMException {
        try {
            // some local variables
            boolean first = true;
            final HashMap<String, StringBuffer> aln = new HashMap<String, StringBuffer>();
            final LineNumberReader br = new LineNumberReader(clustal);
            String current = null;

            // parse clustal String
            while ((current = br.readLine()) != null) {

                // first line must starts with CLUSTAL
                if (first) {
                    if (current.startsWith("CLUSTAL")) {
                        // this is correct
                        first = false;
                    } else {
                        // error! first line must start with CLUSTAL
                        log.severe("Error (line:" + br.getLineNumber() + ")! Invalid clustal string :'" + current
                                + "'!");
                        throw new BioDOMException("Error(line:" + br.getLineNumber() + ")! Invalid clustal string :'"
                                + current + "'!");
                    }
                } else {
                    if (current.trim().matches("\\w+\\s+[a-zA-Z[-]]+")) {
                        log.config("matching line (" + br.getLineNumber() + "):'" + current + "'");
                        // remove duplicate spaces
                        final String[] l = current.split("\\s+");
                        if (aln.containsKey(l[0])) {
                            aln.get(l[0]).append(l[1]);
                        } else {
                            aln.put(l[0], new StringBuffer(l[1]));
                        }
                    } else {
                        log.config("line (" + br.getLineNumber() + ") does not match:'" + current + "'");
                    }
                }
            }

            // generate DOMTree
            final Element alignment = dom.createElementNS(NAMESPACE, ELEM_ALIGNMENT);
            if (score != null) {
                alignment.setAttributeNS(null, ATT_SCORE, score.toString());
            }
            for (String id : aln.keySet()) {
                final Element sequence = addAlignment(id, null, aln.get(id).toString(), seqtype);
                alignment.appendChild(sequence);
            }
            final Element root = dom.getDocumentElement();
            root.appendChild(alignment);
            // close stream
            br.close();
        } catch (IOException e) {
            log.severe("IOException occurred while reading from clustal reader" + System.getProperty("line.separator")
                    + e.getMessage());
            throw new BioDOMException("IOException occurred while reading from clustal reader"
                    + System.getProperty("line.separator") + e.getMessage());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendFastaAsAlignment(java.lang.String)
     */
    public void appendFastaAsAlignment(final String fasta) throws BioDOMException {
        appendFastaAsAlignment(new StringReader(fasta), null, UNKNOWNSEQUENCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendFastaAsAlignment(java.io.Reader)
     */
    public void appendFastaAsAlignment(final Reader fasta) throws BioDOMException {
        appendFastaAsAlignment(fasta, null, UNKNOWNSEQUENCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendFastaAsAlignment(java.lang.String,
     *      java.lang.Integer)
     */
    public void appendFastaAsAlignment(final String fasta, final Integer score) throws BioDOMException {
        this.appendFastaAsAlignment(new StringReader(fasta), score, UNKNOWNSEQUENCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendFastaAsAlignment(java.io.Reader,
     *      java.lang.Integer)
     */
    public void appendFastaAsAlignment(final Reader fasta, final Integer score) throws BioDOMException {
        appendFastaAsAlignment(fasta, score, UNKNOWNSEQUENCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendFastaAsAlignment(java.lang.String,
     *      int)
     */
    public void appendFastaAsAlignment(final String fasta, final int seqType) throws BioDOMException {
        this.appendFastaAsAlignment(new StringReader(fasta), null, seqType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendFastaAsAlignment(java.io.Reader,
     *      int)
     */
    public void appendFastaAsAlignment(final Reader fasta, final int seqType) throws BioDOMException {
        appendFastaAsAlignment(fasta, null, seqType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendFastaAsAlignment(java.lang.String,
     *      java.lang.Integer, int)
     */
    public void appendFastaAsAlignment(final String fasta, final Integer score, final int seqType)
            throws BioDOMException {
        appendFastaAsAlignment(new StringReader(fasta), score, seqType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#appendFastaAsAlignment(java.io.Reader,
     *      java.lang.Integer, int)
     */
    public void appendFastaAsAlignment(final Reader fasta, final Integer score, final int seqType)
            throws BioDOMException {

        // create empty alignment element
        final Element alignmentML = dom.getDocumentElement();
        final Element alignment = dom.createElementNS(NAMESPACE, ELEM_ALIGNMENT);
        alignmentML.appendChild(alignment);

        // adding alignment score (if given)
        if (score != null) {
            alignment.setAttributeNS(null, ATT_SCORE, Integer.toString(score));
        }
        // read Fasta line by line using a BufferedReader
        final LineNumberReader br = new LineNumberReader(fasta);
        String line = null;
        StringBuffer sequenceBuffer = new StringBuffer();
        String id = null;
        String description = null;

        try {
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {
                    // skip empty line! exception otherwise!
                    log.warning("Empty line detected. Skipping empty line!");
                    warningBox.appendWarning(this, "Empty line detected. Skipping empty line!");
                } else {
                    if (line.charAt(0) != '>') { // if line does NOT start
                                                    // with ">"
                        // assuming it's contains sequence
                        // information
                        if (id != null) {
                            sequenceBuffer = sequenceBuffer.append(line);
                        } else {
                            throw new BioDOMException("line " + br.getLineNumber()
                                    + ": No id before aligned sequence starts!");
                        }
                    } else { // line start with ">" or it is the last line
                        // if any id exists, we've parsed an sequence before and
                        // can
                        // add it to current dom
                        if (id != null) {
                            alignment.appendChild(addAlignment(id, description, sequenceBuffer.toString(), seqType));
                            sequenceBuffer = new StringBuffer();
                        }
                        // removing leading whitespaces and get id and
                        // description
                        final String[] idDesc = line.substring(1).trim().split(" ", 2);
                        // get id
                        if (idDesc.length > 0) {
                            id = idDesc[0];
                        } else {
                            id = generateId();
                            warningBox.appendWarning(this, "line " + br.getLineNumber()
                                    + ": aligned sequence has no ID! Creating a unique ID!\n");
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
            /* if sequenceBuffer ist empty, we have an empty sequence and
               therefore throw an exception... */
            if (sequenceBuffer.length() == 0)
                throw new BioDOMException("aligned sequence contains no sequence information (empty sequence)!");
            /* ...otherwise, the last sequence contains sequence information, so
            we add the last sequence */
            alignment.appendChild(addAlignment(id, description, sequenceBuffer.toString(), seqType));
            // close reader
            br.close();
        } catch (IOException e) {
            log.severe("IOException occurred while reading from fasta reader" + System.getProperty("line.separator")
                    + e.getMessage());
            throw new BioDOMException("IOException occurred while reading from fasta reader"
                    + System.getProperty("line.separator") + e.getMessage());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#toFasta()
     */
    public String toFasta() throws BioDOMException {
        return toFasta(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.unibi.techfak.bibiserv.biodom.AlignmentMLInterface#toFasta(int)
     */
    public String toFasta(final int no) throws BioDOMException {
        // try {
        if (!this.validate()) {
            return null;
        }
        // parse to fasta
        StringBuffer fasta = new StringBuffer();
        final Element alignment = (Element) dom.getElementsByTagName(ELEM_ALIGNMENT).item(no);
        final NodeList sequences = alignment.getElementsByTagName(ELEM_SEQUENCE);
        char [] tmpfasta;
        for (int i = 0; i < sequences.getLength(); i++) {
            final Element sequence = (Element) sequences.item(i);

            // adding sequence id
            fasta = fasta.append(">").append(sequence.getAttributeNS(null, ATT_SEQID).replaceAll(" ", ""));

            // adding description
            if (sequence.getElementsByTagName(ELEM_DESCRIPTION).getLength() > 0) {
                final String description = getTextContent(sequence.getElementsByTagName(ELEM_DESCRIPTION).item(0));
                if (description != null) {
                    fasta = fasta.append(" ").append(description);
                }
            }
            // adding newline
            fasta = fasta.append(System.getProperty("line.separator"));

            // adding sequence
            if (sequence.getElementsByTagName(ELEM_ALIGNEDNUCLEICACIDSEQUENCE).getLength() > 0) {
                tmpfasta = getTextContent(sequence.getElementsByTagName(ELEM_ALIGNEDNUCLEICACIDSEQUENCE)
                        .item(0)).toCharArray();

            } else if (sequence.getElementsByTagName(ELEM_ALIGNEDAMINOACIDSEQUENCE).getLength() > 0) {
                tmpfasta = getTextContent(sequence.getElementsByTagName(ELEM_ALIGNEDAMINOACIDSEQUENCE).item(0)).toCharArray();
 
            } else  {
                tmpfasta = getTextContent(sequence.getElementsByTagName(ELEM_ALIGNEDFREESEQUENCE).item(0)).toCharArray();
        
            } 
            
            /* TODO: Add a more efficient method for line-wrapping the sequences (than char-by-char)!! */
            //define line length
            final int wrapwidth = 72;
            for (int cp = 0; cp < tmpfasta.length; cp++) {
                // append next character
                fasta.append(tmpfasta[cp]);
                // if we've appended the linebreaking number of characters,
                // append a newline...
                if (((cp + 1) % wrapwidth) == 0) {
                    fasta = fasta.append(System.getProperty("line.separator"));
                }
            }
            
            if (i != sequences.getLength() - 1) {// a new seq will be
                                                    // appended
                fasta = fasta.append(System.getProperty("line.separator"));
            }
        }
        return fasta.toString();
    }

    // ////////////////////////////////
    // //// Private Methods
    // ////////////////////////////////
    /**
     * generates an empty DOM Tree
     */
    private void initDOM() {
        dom = DB.newDocument();
        final Element root = dom.createElementNS(NAMESPACE, ELEM_ALIGNMENTML);
        root
                .setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation", NAMESPACE + " "
                        + NSLOCATION);
        dom.appendChild(root);
        dom.normalizeDocument();
        log.config("DOM ready...");
    }

    /**
     * Create a new aligned sequence element from the given parameter, if
     * seqType is unknown (==4) this function uses a heuristic to determine the
     * sequenceType
     * 
     * @param id
     * @param description
     * @param sequence
     * @param seqType
     * @return Returns an aligned sequence element
     * @throws BioDOMException
     */
    private Element addAlignment(final String id, final String description, String sequence, final int seqType)
            throws BioDOMException {

        // check for lowercase and warn&convert on match
        if (lcPattern.matcher(sequence).find()) {
            log.warning("Lowercase characters not valid in AlignmentML - converting to Uppercase");
            warningBox.appendWarning(this, "Lowercase characters not valid in AlignmentML - converting to Uppercase");
            sequence = sequence.toUpperCase();
        }

        // creating sequence element
        final Element sequenceElem = dom.createElementNS(NAMESPACE, ELEM_SEQUENCE);
        // setting id
        sequenceElem.setAttributeNS(null, ATT_SEQID, id);

        // adding description element
        if (description != null) {
            final Element descriptionElem = dom.createElementNS(NAMESPACE, ELEM_DESCRIPTION);
            descriptionElem.setTextContent(description);
            sequenceElem.appendChild(descriptionElem);
        }
        // checking sequence type and adding sequence
        // AlignedAminoAcidSequence
        if (seqType == AMINOACID) {
            if (!aaPattern.matcher(sequence).matches()) {
                throw new BioDOMException("Error! Unallowed char (allowed pattern: " + PATTERN_AMINOACID
                        + " ) in aminoacidsequence '" + sequence + "'!");
            }
            final Element alignedAminoSeqElem = dom.createElementNS(NAMESPACE, ELEM_ALIGNEDAMINOACIDSEQUENCE);
            alignedAminoSeqElem.setTextContent(sequence);
            sequenceElem.appendChild(alignedAminoSeqElem);
            // AlignedNucleicAcidSequence
        } else if (seqType == NUCLEICACID) {
            if (!naPattern.matcher(sequence).matches()) {
                throw new BioDOMException("Error! Unallowed char (allowed pattern: " + PATTERN_NUCLEICACID
                        + " ) in nucleicacidsequence '" + sequence + "'!");
            }
            final Element alignedNucleicSeqElem = dom.createElementNS(NAMESPACE, ELEM_ALIGNEDNUCLEICACIDSEQUENCE);
            alignedNucleicSeqElem.setTextContent(sequence);
            sequenceElem.appendChild(alignedNucleicSeqElem);
            // AlignedFreeSequence
        } else if (seqType == FREE) {
            if (!fsPattern.matcher(sequence).matches()) {
                throw new BioDOMException("Error! Unallowed char (allowed pattern: " + PATTERN_FREE
                        + " ) in freesequence '" + sequence + "'!");
            }
            final Element alignedFreeSeqElem = dom.createElementNS(NAMESPACE, ELEM_ALIGNEDFREESEQUENCE);
            alignedFreeSeqElem.setTextContent(sequence);
            sequenceElem.appendChild(alignedFreeSeqElem);
        } else if (seqType == UNKNOWNSEQUENCE) {
            if (!aaPattern.matcher(sequence).matches()) { // unallowed char ->
                                                            // free sequence
                final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_ALIGNEDFREESEQUENCE);
                sequenceElem.appendChild(eSeq);
                eSeq.appendChild(dom.createTextNode(sequence));
            } else if (!naPattern.matcher(sequence).matches()) { // aminoacid
                                                                    // (contains
                                                                    // chars
                                                                    // that are
                                                                    // only
                                                                    // allowed
                                                                    // in
                                                                    // aminoacid
                final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_ALIGNEDAMINOACIDSEQUENCE);
                sequenceElem.appendChild(eSeq);
                eSeq.appendChild(dom.createTextNode(sequence));
            } else { // aminoacid or nucleicacid? -> depends on percentage of
                        // ACGTU (current >= 90%)
                final String uSeq = sequence.toUpperCase();
                int cnt = 0, length = uSeq.length();
                for (int j = 0; j < uSeq.length(); j++) {
                    final char c = uSeq.charAt(j);
                    switch (c) {
                    case '-':
                        length--;
                        break;
                    case 'A':
                    case 'C':
                    case 'G':
                    case 'T':
                    case 'U':
                        cnt++;
                        break;
                    }
                }
                if ((double) cnt / (double) length >= 0.9) { // over 90% of
                                                                // sequence are
                                                                // ACGTU ->
                                                                // probably
                                                                // nucleicacid
                    final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_ALIGNEDNUCLEICACIDSEQUENCE);
                    sequenceElem.appendChild(eSeq);
                    eSeq.appendChild(dom.createTextNode(sequence));
                    warningBox.appendWarning(this, "No sequence type defined. Determined sequence as Nucleicacid");
                } else { // over 10% of sequence are not ACGTU -> probably
                            // aminoacid
                    final Element eSeq = dom.createElementNS(NAMESPACE, ELEM_ALIGNEDAMINOACIDSEQUENCE);
                    sequenceElem.appendChild(eSeq);
                    eSeq.appendChild(dom.createTextNode(sequence));
                    warningBox.appendWarning(this, "No sequence type defined. Determined sequence as Aminoacid");
                }
            }

            // Error -> Invalid sequence type
        } else {
            log.config("appendFastaAsAlignment: unknown sequence type!");
            throw new BioDOMException("Error! Invalid sequence type commited!");
        }
        return sequenceElem;
    }

}