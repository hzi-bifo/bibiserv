package de.unibi.techfak.bibiserv.biodom.resolver;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;

/**
 * Implementation of interface LSInput - mainly used as return type for
 * LSEntityResolver.
 * 
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 *
 */
public class Input implements LSInput {
    
    private Reader characterstream = null;
    private InputStream bytestream = null;
    private String stringdata = null;
    private String systemId = null;
    private String publicId = null;
    private String baseURI = null;
    private String encoding = null;
    private boolean certifiedText;
    
    /**
     * default constructor
     *
     */
    public Input(){
    }
    

    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#getCharacterStream()
     */
    public Reader getCharacterStream() {
        return characterstream;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#setCharacterStream(java.io.Reader)
     */
    public void setCharacterStream(Reader arg0) {
        characterstream = arg0;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#getByteStream()
     */
    public InputStream getByteStream() {
      return bytestream;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#setByteStream(java.io.InputStream)
     */
    public void setByteStream(InputStream arg0) {
        bytestream = arg0;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#getStringData()
     */
    public String getStringData() {
        return stringdata;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#setStringData(java.lang.String)
     */
    public void setStringData(String arg0) {
        stringdata = arg0;

    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#getSystemId()
     */
    public String getSystemId() {
        return systemId;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#setSystemId(java.lang.String)
     */
    public void setSystemId(String arg0) {
      systemId = arg0;

    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#getPublicId()
     */
    public String getPublicId() {
       return publicId;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#setPublicId(java.lang.String)
     */
    public void setPublicId(String arg0) {
        publicId = arg0;
       
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#getBaseURI()
     */
    public String getBaseURI() {
        return baseURI;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#setBaseURI(java.lang.String)
     */
    public void setBaseURI(String arg0) {
       baseURI = arg0;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#getEncoding()
     */
    public String getEncoding() {
       return encoding;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#setEncoding(java.lang.String)
     */
    public void setEncoding(String arg0) {
       encoding = arg0;

    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#getCertifiedText()
     */
    public boolean getCertifiedText() {
       return certifiedText;
    }
    /*
     *  (non-Javadoc)
     * @see org.w3c.dom.ls.LSInput#setCertifiedText(boolean)
     */
    public void setCertifiedText(boolean arg0) {
       certifiedText = arg0;
    }
    
    /** 
     * set important internal values according to external InputSource
     */
    public void fromInputSource(InputSource is) {
    	setByteStream(is.getByteStream());
    	setCharacterStream(is.getCharacterStream());
    	setEncoding(is.getEncoding());
    	setPublicId(is.getPublicId());
    	setSystemId(is.getSystemId());
    }

    public String toString() {
    	return "\nByteStream: "+getByteStream()+"\n"+
    	"CharacterStream: "+getCharacterStream()+"\n"+
    	"Encoding: "+getEncoding()+"\n"+
    	"PublicId: "+getPublicId()+"\n"+
    	"SystemId: "+getSystemId()+"\n";
    }
}
