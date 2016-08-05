package de.unibi.techfak.bibiserv.biodom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.unibi.techfak.bibiserv.biodom.exception.BioDOMException;
import de.unibi.techfak.bibiserv.biodom.exception.NSNotSupportedException;
import de.unibi.techfak.bibiserv.biodom.exception.StylesheetNotAvailableException;
import de.unibi.techfak.bibiserv.biodom.resolver.Resolver;
import de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBox;

public abstract class AbstractBioDOM implements AbstractBioDOMInterface {
	/**
	 * BioDOM useful methodes for validating,parsing, converting to/from DOM.
	 * 
	 * Abstract Class for extending within format classes.
	 * 
	 * @author Henning Mersch <hmersch@techfak.uni-bielefeld.de> 
     *         Jan Krueger <jkrueger@techfak.uni-bielefeld.de>
	 * @version $Revision: 1.51 $
	 */

	//
	// FINAL CONSTANTS FOR TYPES, NAMES AND ALLOWED CHARACTERS FOR DERIVED CLASSES
	
    /**
     * Nucleo acid following IUPAC norm : <br/> <table style="border:solid 1px
     * black;">
     * <tr>
     * <th>code:</th>
     * <th>description:</th>
     * </tr>
     * <tr>
     * <td>A</td>
     * <td><b>A</b>denosine</td>
     * </tr>
     * <tr>
     * <td>C</td>
     * <td><b>C</b>ytidine</td>
     * </tr>
     * <tr>
     * <td>G</td>
     * <td><b>G</b>uanine</td>
     * </tr>
     * <tr>
     * <td>T</td>
     * <td><b>T</b>hymidine</td>
     * </tr>
     * <tr>
     * <td>U</td>
     * <td><b>U</b>racil</td>
     * </tr>
     * <tr>
     * <td>R</td>
     * <td>G A (pu<b>R</b>ine)</td>
     * </tr>
     * <tr>
     * <td>Y</td>
     * <td>T C (p<b>Y</b>rimidine)</td>
     * </tr>
     * <tr>
     * <td>K</td>
     * <td>G T (<b>K</b>etone)</td>
     * </tr>
     * <tr>
     * <td>M</td>
     * <td>A C (a<b>M</b>ino group)</td>
     * </tr>
     * <tr>
     * <td>S</td>
     * <td>G C (<b>S</b>trong interaction)</td>
     * </tr>
     * <tr>
     * <td>W</td>
     * <td>A T (<b>W</b>eak interaction)</td>
     * </tr>
     * <tr>
     * <td>B</td>
     * <td>G T C (not A)</td>
     * </tr>
     * <tr>
     * <td>D</td>
     * <td>G A T (not C)</td>
     * </tr>
     * <tr>
     * <td>H</td>
     * <td>A C T (not G)</td>
     * </tr>
     * <tr>
     * <td>V</td>
     * <td>G C A (not T, not U)</td>
     * </tr>
     * <tr>
     * <td>N</td>
     * <td>A G C T (a<b>N</b>y)</td>
     * </tr>
     * </table>
     */
    public static final String NUCLEICACID_CHARS = "ACGTURYKMSWBDHVN";

    /**
     * name (string) of nuleicacid tag
     */
    public static final String NUCLEICACID_NAME = "nucleicAcidSequence";

    /**
     * name (string) of aligned nucleotide acid tag
     */
    public static final String ALIGNEDNUCLEICACID_NAME = "alignedNucleotideAcidSequence";

    /**
     * const of nucleicacid
     */
    public static final int NUCLEICACID = 0;

    /**
     * Amino acid following IUPAC norm:
     * <br>
     * <table style="border:solid 1px black;">
     * <tr>
     * <th>code:</th>
     * <th>description:</th>
     * </tr>
     * <tr>
     * <td>A</td>
     * <td>Alanine</td>
     * </tr>
     * <tr>
     * <td>B</td>
     * <td>Aspartic acid or Asparagine</td>
     * </tr>
     * <tr>
     * <td>C</td>
     * <td>Cysteine</td>
     * </tr>
     * <tr>
     * <td>D</td>
     * <td>Aspartate</td>
     * </tr>
     * <tr>
     * <td>E</td>
     * <td>Glutamate</td>
     * </tr>
     * <tr>
     * <td>F</td>
     * <td>Phenylalanine</td>
     * </tr>
     * <tr>
     * <td>G</td>
     * <td>Glycine</td>
     * </tr>
     * <tr>
     * <td>H</td>
     * <td>Histidine</td>
     * </tr>
     * <tr>
     * <td>I</td>
     * <td>Isoleucine</td>
     * </tr>
     * <tr>
     * <td>K</td>
     * <td>Lysine</td>
     * </tr>
     * <tr>
     * <td>L</td>
     * <td>Leucine</td>
     * </tr>
     * <tr>
     * <td>M</td>
     * <td>Methionine</td>
     * </tr>
     * <tr>
     * <td>N</td>
     * <td>Asparagine</td>
     * </tr>
     * <tr>
     * <td>P</td>
     * <td>Proline</td>
     * </tr>
     * <tr>
     * <td>Q</td>
     * <td>Glutamine</td>
     * </tr>
     * <tr>
     * <td>R</td>
     * <td>Arginine</td>
     * </tr>
     * <tr>
     * <td>S</td>
     * <td>Serine</td>
     * </tr>
     * <tr>
     * <td>T</td>
     * <td>Threonine</td>
     * </tr>
     * <tr>
     * <td>U</td>
     * <td>Selenocysteine</td>
     * </tr>
     * <tr>
     * <td>V</td>
     * <td>Valine</td>
     * </tr>
     * <tr>
     * <td>W</td>
     * <td>Tryptophan</td>
     * </tr>
     * <tr>
     * <td>Y</td>
     * <td>Tyrosine</td>
     * </tr>
     * <tr>
     * <td>Z</td>
     * <td>Glutamate or Glutamine</td>
     * </tr>
     * <tr>
     * <td>X</td>
     * <td>any</td>
     * </tr>
     * </table>
     */
    public static final String AMINOACID_CHARS = "ABCDEFGHIKLMNPQRSTUVWYZX";

    /**
     * name (string) of aminoacid tag
     */
    public static final String AMINOACID_NAME = "aminoAcidSequence";

    /**
     * name (string) of aligned aminoacid tag
     */
    public static final String ALIGNEDAMINOACID_NAME = "alignedAminoAcidSequence";
    
    /**
     * const of aminoacid
     */
    public static final int AMINOACID = 1;

    /**
     * name (string) of freesequence tag
     */
    public static final String FREESEQUENCE_NAME = "freeSequence";

    /** 
     * name (string) of aligned free sequence tag
     */
    public static final String ALIGNEDFREESEQUENCE_NAME = "alignedFreeSequence";
    
    /**
     * const of free sequence
     */
    public static final int FREE = 2;
    
    /**
     * name (string) of empty sequence
     */
    public static final String EMPTYSEQUENCE_NAME = "emptySequence";
    
    /**
     * const for empty sequence
     */
    public static final int EMPTYSEQUENCE = 3;

    /**
     * const of unknown sequence
     */
    public static final int UNKNOWNSEQUENCE = 4;
	
	/**
	 * String of chars for generating unique random part of bibiid
	 */
	private static final String ID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	/**
	 * private  Logger 
	 */
	private static Logger log = Logger.getLogger(AbstractBioDOM.class.toString());

	/**
	 * WarningBox for collecting warning messages
	 */
    public BioDOMWarningBox warningBox;
	
	/**
	 * Simple constant for adding of linebreaks ;-)
	 */
    public static final String LINEBREAK = System.getProperty("line.separator");
    
    /**
     * Schema factory
     */
    protected final SchemaFactory sfactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    
    /**
     * DocumentBuilder factory
     */
    protected final DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
    
	/**
	 * for catalog properties file
	 */
	private File propertiesfile = null;

	/**
	 * instance of catalogproperties;
	 */
	protected Properties properties = null;

    /**
     * Namespace. This value is set by the setproperties function
     */
	protected String NAMESPACE = null;
    
    /**
     * External Namespace location.  This value is set by the setproperties function
     */
	protected String NSLOCATION = null; 
    
    /**
     * Schema. This value is set by the setproperties function.
     */
    protected Schema SCHEMA = null;
    
    /**
     * Resolver. 
     */
    protected Resolver RESOLVER = new Resolver();
    
    /**
     * DocumentBuilder. This value is set by the setproperties function.
     */
    protected DocumentBuilder DB = null;
 
    
 
	/**
	 * current instance of the DOM Document
	 */
	protected Document dom;
    
    /**
     * this variable controlls if a xml is nillable or 
     * not. all classes inheriting from AbstractBioDOM
     * should override this variable. default is true.
     */
	protected boolean isNillable = true;
    
	/////////////////////////
	//Contructors
	////////////////////////

	/**
	 * creates a new BioDOM object for processing DOM Documents
	 * 
	 * @param catalogproperties -
	 *            String - path of a BioDOM.Properties.xml file
	 * @exception BioDOMException on failure
	 *  
	 */
	public AbstractBioDOM(String propertiesfile) throws BioDOMException {
		warningBox = new BioDOMWarningBox();
		if (propertiesfile != null) {
            this.propertiesfile = new File(propertiesfile);
        }
		setProperties();
	}
    
    /**
     * creates a new BioDOM object for processing DOM Documents
     * 
     * @param propertiesfile - File - path a BioDOM.properties.xml file
     * @throws BioDOMException BioDOMException on failure
     */
    public AbstractBioDOM(File propertiesfile) throws BioDOMException {
        warningBox = new BioDOMWarningBox();
        this.propertiesfile = propertiesfile;
        setProperties();
    }

	/////////////////////////
	// public Methodes
	////////////////////////

	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#setLogLevel(org.apache.log4j.Level)
	 */
	public void setLogLevel(final Level level) {
		log.setLevel(level);
	}

    /*
     *  (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#validate()
     */
	public boolean validate() { 
        log.config("call validate()");
        try {
        	// create new Validator
            final Validator validator = SCHEMA.newValidator();
            //  validate  
            validator.validate(new DOMSource(dom)); 
            return true; 
        } catch(IOException e){
            log.severe("BioDOM validation (IOException): Error reading internal document!");
        } catch(SAXException e){
            log.warning("BioDOM validation (SaxException): Error parsing/validating internal document:\n"+e.getMessage());

        }
        return false;   
	}
	
    /*
     *  (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#validate(java.net.URL)
     */
	public boolean validate(final URL xsd) {
		InputStream is;
		try {
			is = xsd.openStream();
		} catch (IOException e) {
			log.severe("BioDOM validation (IOException): Error reading inputstream!");
			return false;
		}
		return validate(is);
	}
	
	/*
     *  (non-Javadoc)
     * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#validate(java.io.InputStream)
	 */
	public boolean validate(final InputStream is) {
		log.config("call validate (InputStream)");
        try {
            // create new Schema
            final Schema schema = sfactory.newSchema(new StreamSource(is));
            // create new Validator
            final Validator validator = schema.newValidator();
            // validate
            validator.validate(new DOMSource(dom));
            return true;
                
            } catch (IOException e){
                log.severe("BioDOM validation (IOException): Error reading internal document");
            } catch(SAXException e){
                log.warning("BioDOM validation (SaxException): Error parsing/validating internal document!");
            }
            return false;         
	}

	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#setDom(java.lang.String)
	 */
	public void setDom(final String xmlstring) throws BioDOMException {
		try {
			final InputSource is = new InputSource(new StringReader(xmlstring));		
			dom = DB.parse(is);
		} catch (IOException e) {
			throw new BioDOMException("String unparseable (IOException)");
		} catch (SAXException e) {
			throw new BioDOMException("String unparseable (SAXException)");
		}
	}

	/* (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#setDom(org.w3c.dom.Document)
	 */
	public void setDom(final Document submitted_dom) throws BioDOMException {
	   dom = submitted_dom;
       if (!validate()) {
           log.severe("couldn't init, XML validation failed");
           throw new BioDOMException("couldnt init, XML validation failed");
       }
	}

	/*
	 * (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#getDom()
	 */
	public Document getDom() throws BioDOMException{
		return getDom(true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#getDom(boolean)
	 */
	public Document getDom( final boolean validate ) throws BioDOMException{
		if ( isNillable ) {
			final Element documentElement = dom.getDocumentElement();
	    	if ( documentElement.getChildNodes().getLength() == 0 ) {
	    		documentElement.setAttribute( "xsi:nil", "true" );
	    	}
	    	else {
	    		documentElement.removeAttribute( "xsi:nil" );
	    	}
		}
		
		// If validation is unwanted, just return the DOM
		if ( !validate ) return dom;
		//If you reach this point, validation IS wanted, so validate. If OK, return DOM
		if ( validate() ) return dom;
		//If you reach this point, validation has failed. Therefore, log an error and throw an Exception
	    log.severe("Document not valid!");
	    throw new BioDOMException("Document contains no valid Document Data!");
	}

	/**
	 * Generates a unique ID - for usage in XML documents as ID to IDREF pair
	 * 
     * @return String unique ID String
	 */
	protected String generateId() {
		//prepare random String for unique IDs
		final Random rand = new Random();
		StringBuffer myRnd = new StringBuffer();
        myRnd.append("ID");
		for (int n = 0; n < 10; n++) {//length is 10 now ... ;)
			myRnd = myRnd.append(ID_CHARS.charAt(rand.nextInt(ID_CHARS.length())));
		}
		log.config("generated ID " + myRnd.toString());
		return myRnd.toString();
	}

	/**
	 * set Properties
	 * 
	 * @throws Throws a BioDOMException on failure.
	 */
	private void setProperties() throws BioDOMException {
		properties = new Properties();
		try {
			if (propertiesfile == null) {
				log.config("load BioDOM config from default ...");
				final InputStream in = getClass().getResourceAsStream("/config/BioDOM.properties.xml");			
				properties.loadFromXML(in);
			} else {
				log.config("load BioDOM config from " + propertiesfile);
				properties.loadFromXML(new FileInputStream(propertiesfile));           
			}
			log.config("properties loaded ...");
        } catch (FileNotFoundException e) {
            log.severe("An FileNotFoundException occurred while opening "+propertiesfile);
            throw new BioDOMException("An FileNotFoundException occurred while opening "+propertiesfile);
		} catch (IOException e) {
            log.severe("An IOException occurred while reading properties!");
			throw new BioDOMException("An IOException occurred while reading properties!");
		}
        // set NAMESPACE for current BioDOM object
        NAMESPACE = properties.getProperty("BioDOM.namespace."+getClass().getSimpleName());
        if (NAMESPACE == null) {
            throw new BioDOMException("NS for BioDOM class "+getClass().getSimpleName()+" not found!");
        }
        // set remote NAMESPACELOCATION
        NSLOCATION = properties.getProperty("BioDOM.nslocation.remote."+NAMESPACE);
        if (NSLOCATION == null) {
            throw new BioDOMException("remote NSLOCATION for NS "+NSLOCATION+" not found!");
        }
        final String NSLOCALLOCATION = properties.getProperty("BioDOM.nslocation.jar."+NAMESPACE);
        if (NSLOCALLOCATION == null) {
            throw new BioDOMException("local NSLOCATION for NS "+NSLOCALLOCATION+" not found!");
        }
        // set SCHEMA for current BioDOM object
        try {
            sfactory.setResourceResolver(RESOLVER);
            sfactory.setErrorHandler(new ValidationErrorHandler());
            SCHEMA = sfactory.newSchema(new StreamSource(getClass().getResourceAsStream(NSLOCALLOCATION)));
           
            log.config("SCHEMA object: "+SCHEMA.toString());    
        } catch (SAXException e){
            throw new BioDOMException("Error parsing schema from resource ("+NSLOCALLOCATION+")");
        } 
        
        // set DocumentBuilder for current BioDOM object
        try {
            dbfactory.setNamespaceAware(true);
            dbfactory.setSchema(SCHEMA);
            DB = dbfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e){
            throw new BioDOMException("ParserConfigurationException while creating new DocumentBuilder");
        }
        
	}

 /**
  * Extracts the text from the first child node if it is a text node (type 3)
  * @param Node the parent node
  * @return text or null if the input node has no text node
  */ 
  protected String getTextContent(final Node node) {
    String text = null;

    if(node != null) {
      final NodeList children = node.getChildNodes();

      if(children != null) {
        for(int i = 0; i < children.getLength(); i++) {
          if(children.item(i).getNodeType() == Node.TEXT_NODE) {
            text = children.item(i).getNodeValue();
            break;
          }
        }
      }
    }
    return text;
  }
  
  /* (non-Javadoc)
 * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#toString()
 */
  public String toString() {
      try {
    	  final Document doc = getDom( false );
          final StringWriter strWtr = new StringWriter();
          final OutputFormat format = new OutputFormat(doc, "UTF-8", true);
          final XMLSerializer output = new XMLSerializer(strWtr, format);
          output.serialize(doc);
          return strWtr.toString();
      } catch (IOException e) {
          return "Can't serialize current object \n" + e.getMessage();
      } catch (BioDOMException e) {
		return "BioDom: " +e.getLocalizedMessage();
	}
  }
  
  /* (non-Javadoc)
   * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOM#getWarningBox()
   */
  public BioDOMWarningBox getWarningBox() {
      return warningBox;
  }

  /* (non-Javadoc)
   * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOM#setWarningBox(de.unibi.techfak.bibiserv.biodom.warning.BioDOMWarningBox)
   */
  public void setWarningBox(final BioDOMWarningBox warningBox) {
      this.warningBox = warningBox;
  }   
  
  /**
   * Transform the given document from XML namespace NS1 to namespace NS2 and
   * returns the translated document. A StylesheetNotAvailableExpception is
   * thrown if no  suitable XSLT stylesheet is available in current release.
   * 
   * Transform uses the BioDOM.properties.xml (found in the config subdir) to get
   * the shortcut names of the XML namespace identifier. The shortcuts of the
   * namespoces are used to build the file name of the suitable XSLT stylesheet.
   * A XSLT stylesheet should be stored in the resources/xsl subfolder using the 
   * following style : 
   * 'shortcut1 of NS1' + '_' + 'to' + '_' + 'shortcut of NS2' + '.xsl'<br>
   * The following table is a (uncompleted) list of available stylesheet which comes with
   * current release of BioDOM
   * 
   * <table>
   * <tr><th>FROM</th><th>TO</th><th>REMARK</th></tr>
   * <tr>
   * <td><a href="http://hobit.sourceforge.net/20060201/rnastructML.xsd">http://hobit.sourceforge.net/20060201/rnastructML</a></td>
   * <td><a href="http://hobit.sourceforge.net/20060201/sequenceML.xsd">http://hobit.sourceforge.net/20060201/sequenceML</a></td>
   * <td>extract all sequence information from RNAstructML and store the information in a SequenceML document</td>
   * </tr>
   * <tr>
   * <td><a href="http://hobit.sourceforge.net/20060515/rnastructAlignmentML">http://hobit.sourceforge.net/20060515/rnastructAlignmentML</a></td>
   * <td><a href="http://hobit.sourceforge.net/20060201/sequenceML.xsd">http:///hobit.sourceforge.net/20060201/sequenceML</a></td>
   * <td>extract all sequence information from RNAStructAlignmentML and store the information in a SequenceML document</td>
   * </tr>
   * <tr>
   * <td><a href="http://hobit.sourceforge.net/20060505/sequenceAnnotationML">http://hobit.sourceforge.net/20060505/sequenceAnnotationML</a></td>
   * <td><a href="http://hobit.sourceforge.net/20060201/sequenceML.xsd">http:///hobit.sourceforge.net/20060201/sequenceML</a></td>
   * <td>extract all sequence information from sequenceAnnotationML and store the information in a SequenceML document</td>
   * </tr>
   * </table>
   * 
   * @param NS1 - A String describing a XML namespace converting from, e.g. http://hobit.sourceforge.net/xsds/20060201/rnastructML
   * @param NS2 - A String describing a XMl namespace converting to, e.g. http://hobit.sourceforge.net/xsds/20060201/sequenceML
   * @param doc - input document
   * @return Returns the translated document
   * @throws StylesheetNotAvailableException, if no suitable XSLT stylesheet is found
   * @throws NSNotSupportedException, if no suitable NS is found in properties
   * @throws TransformerConfigurationException, if the Transformer can't be configured or no Transformer is found
   * @throws TransformerException, if transformation fails
   * @throws BioDOMException,  if BioDOM properties can't be loaded
   */
  public static Document transform(final String NS1, final String NS2, final Document doc) throws StylesheetNotAvailableException, NSNotSupportedException, TransformerConfigurationException, TransformerException, BioDOMException{
      return transform("",NS1,NS2,doc);
  }
  
  /**
   * See transfrom(String NS1, String NS2, Document doc) for a detailed description.
   * 
   * @param catalogpropertiesfile - Filename to an alternative XML Propertiesfile instead the default one
   * @param NS1 - A String describing a XML namespace converting from, e.g. http://hobit.sourceforge.net/xsds/20060201/rnastructML
   * @param NS2 - A String describing a XMl namespace converting to, e.g. http://hobit.sourceforge.net/xsds/20060201/sequenceML
   * @param doc - input document (Attention : if the document is read from a inputstream(eg. file or url) using a 
   * documentbuilderfactory/documentbuilder (JAXP-way), you should set "setNamespaceAware(true)" on the factory object!!!)
   * @return Returns the translated document
   * @throws StylesheetNotAvailableException, if no suitable XSLT stylesheet is found
   * @throws NSNotSupportedException, if no suitable NS is found in properties
   * @throws TransformerConfigurationException, if the Transformer can't be configured or no Transformer is found
   * @throws TransformerException, if tranformation fails
   * @throws BioDOMException, if BioDOM properties can't be loaded
   */
  public static Document transform(final String catalogpropertiesfile, final String NS1, final String NS2, final Document doc) throws StylesheetNotAvailableException, NSNotSupportedException, TransformerConfigurationException, TransformerException, BioDOMException{
      
      Properties catalogproperties = new Properties();
        try {
            if (catalogpropertiesfile == null || catalogpropertiesfile.equals("")) {
                log.config("load from default, catalogpropertiesfile is null...");
                final InputStream in = AbstractBioDOM.class.getResourceAsStream("/config/BioDOM.properties.xml");
                if (in != null) {
                    catalogproperties.loadFromXML(in);
                  log.config("properties loaded");
                } else {
                    log.severe("default configuration not found, catalogpropertiesfile is still null!");
                    catalogproperties = null;
                }
            } else {
                log.config("load from " + catalogpropertiesfile);
                catalogproperties.loadFromXML(new FileInputStream(catalogpropertiesfile));
              log.config("properties loaded");
            }
            log.config("End of set properties...");
        } catch (IOException e) {
            throw new BioDOMException("An IO Exception occurred while reading "
                    + catalogproperties + "!");
        }
      
     
        
      // get shortcut for NS1 from property
      final String sc1 = catalogproperties.getProperty(NS1);
      if (sc1 == null) throw new NSNotSupportedException("NS "+NS1+" is not found in properties!");
      // read Schema1 to validate input document 
      final Schema schema1 = getSchema(NS1);
      
      // get shortcut for NS2 from property
      final String sc2 = catalogproperties.getProperty(NS2);
      if (sc2 == null) throw new NSNotSupportedException("NS "+NS2+" is not found in properties!");
      // read Schema2 to validate input document 
      final Schema schema2 = getSchema(NS2);
         
      // load xsl file from resources, using name built from sc1 and sc2
      final InputStream xsltstream = AbstractBioDOM.class.getResourceAsStream("/resources/xsl/"+sc1+"_to_"+sc2+".xsl");
      

          
      // check if xsl is available
      if (xsltstream == null) {
          throw new StylesheetNotAvailableException("No stylesheet available for transform a document from "+NS1+" to "+NS1);
      }
      
      // create new input stream source from xsl stream
      final StreamSource xsltSource = new StreamSource(xsltstream);
      
      // create new input source from document
      final DOMSource xmlSource = new DOMSource(doc);
      // and validate it
      final Validator validate1 = schema1.newValidator();
      try {
          validate1.validate(xmlSource);
      } catch (Exception e){
          throw new BioDOMException("Input document is invalid against "+NS1);
      }
      
      // create new Result (DOMresult)
      final DOMResult xmlResult = new DOMResult();  
      // create new Transformer using JAXP    
      final TransformerFactory transFact =
          TransformerFactory.newInstance();
          
      final Transformer trans = transFact.newTransformer(xsltSource);
      // transform document to result
      trans.transform(xmlSource,xmlResult);
     
  
      // validate returned document against NS2
      final Validator validate2 = schema2.newValidator();
      try {
          validate2.validate(new DOMSource((Document)xmlResult.getNode()));
      } catch (Exception e){
          throw new BioDOMException("Result document is invalid against "+NS2);
      }
      
      // return transformed document
      log.config("return transformed XML document");
      return (Document)xmlResult.getNode();
  }
  
 
  
  /**
   * Private method, which reads a Schema from Resource (in future) and 
   * returns a Java Schema object representation of it
   * 
   * @param NS - namespace of the Schema
   * @return returns a Java Schema object representation of the schema
   * @throws BioDOMException if an error occurred reading this schema from file
   * @throws StylesheetNotAvailableException if the stylesheet is not available in the resources
   */
  private static Schema getSchema(String NS)throws BioDOMException {
      try {
          SchemaFactory sfactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
          Resolver reso = new Resolver();
          sfactory.setResourceResolver(reso);
          final Schema schema = sfactory.newSchema(new StreamSource(reso.resolveResource(null,NS,null,null,null).getByteStream()));
          return schema;
      } catch (SAXException e){
          throw new BioDOMException("Error occurred parsing stylesheet "+NS);
      }
  }
  
  /*
   *  (non-Javadoc)
   * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#getNS()
   */
  public String getNS(){
     return NAMESPACE;
  }
  
  /*
   *  (non-Javadoc)
   * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#getNSlocation()
   */
  public String getNSlocation(){
      return NSLOCATION;
  }
  
  /*
   *  (non-Javadoc)
   * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#getSchema()
   */
  public Schema getSchema() {
      return SCHEMA;
  }
  
  /*
   *  (non-Javadoc)
   * @see de.unibi.techfak.bibiserv.biodom.AbstractBioDOMInterface#getDocumentBuilder()
   */
  public DocumentBuilder getDocumentBuilder() {
      return DB;
  }
  
}
