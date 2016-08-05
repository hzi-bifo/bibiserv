package de.unibi.techfak.bibiserv.biodom.resolver;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 * Implementation of LSResourceResolver, which replaces the CatalogResolver used in BioDOM 1.0
 * 
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 *
 */
public class Resolver implements LSResourceResolver, EntityResolver {
	
	private static Logger log = Logger.getLogger(Resolver.class.toString());
	
	private Properties prop;
	
	private Input input = new Input();
	
	/**
	 * Constructor
	 */
	public Resolver(){
		log.config("Initialize new ResourceResolver!");
		prop = new Properties();
		try {
			prop.loadFromXML(getClass().getResourceAsStream("/config/BioDOM.properties.xml"));
		} catch (IOException e){
			log.warning("loading of BioDOM.properties.xml failed!");
		}
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public LSInput resolveResource(String type,
			String namespaceURI,
			String publicId,
			String systemId,
			String baseURI) {
		log.finest("resolveResource(type->"+type+",namespaceURI->"+namespaceURI+",publicId->"+publicId+",systemId->"+systemId+",baseURI->"+baseURI);
		// check if namespaceURI is located in local resource
		String propkey = "BioDOM.nslocation.jar."+namespaceURI; 
		String nslocation = prop.getProperty(propkey);
		if (nslocation != null) {
			input.setByteStream(getClass().getResourceAsStream(nslocation));
			log.finest("Resolver return value is: "+input.toString());
			return input;
		}
		log.config("Property "+propkey+" not found in BioDOM.properties.xml - Trying external version...");
		propkey = "BioDOM.nslocation.remote."+namespaceURI;
		nslocation = prop.getProperty(propkey);
		try {
			EntityResolver externalresolver = getCatalogResolver();
			if (nslocation != null) {
				input.fromInputSource(externalresolver.resolveEntity(null, nslocation));
			} else {
				input.fromInputSource(externalresolver.resolveEntity(publicId, systemId));
			}
			log.finest("Resolver return value is: "+input.toString());
			return input;
		} catch (Exception e) {
			log.config("No external resolver found: "+e.getMessage());
		}
		// in case of no external Catalogresolver, return external nslocation from properties as InputStream
		try {
			if (nslocation != null) {
				input.setByteStream(new URL(nslocation).openStream());
				log.finest("Resolver return value is: "+input.toString());
				return input;
			} 
		} catch (Exception e) {
			log.config("Could not open external resource Stream: "+e.getMessage());
		}
		log.config("Couldn't resolve namespace/namespace location. If you see this, you should think about downloading the full biodom archive, which contains all necessary schemas!");
		log.finest("Resolver return value is 'null'");
		return null;
	}
    
    /*
     *  (non-Javadoc)
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (prop.containsValue(systemId)) {
            log.info("found systemId ["+systemId+"]in Properties");
            for (Object key : prop.keySet()) {
                if (prop.get(key).equals(systemId)) {
                    String NS = ((String)key).replaceFirst("BioDOM.nslocation.remote.","");
                    return new InputSource(resolveResource(null,NS,publicId,systemId,null).getByteStream());
                }
            }
        } 
        // FIXME : add external EntityResolver
        return null;
    }
	
	/**
	 * Create and return an EntityResolver based on Apache Catalog Resover using
	 * Java reflection package. Throws an Exception if creation is failed.
	 * 
	 * @return EntityResolver
	 * @throws Exception, if creation is failed
	 */
	private EntityResolver getCatalogResolver() throws Exception{
		Class cm =  Class.forName("org.apache.xml.resolver.CatalogManager");
		Object cm_o = cm.newInstance();
		
		Class cr =  Class.forName("org.apache.xml.resolver.tools.CatalogResolver");
		Constructor c = cr.getConstructor(cm);
		Field namespaceAware = cr.getField("namespaceAware");
		Field validating = cr.getField("validating");
		Object cr_o = c.newInstance(cm_o);
		namespaceAware.setBoolean(cr_o,true);
		validating.setBoolean(cr_o,true);
		return (EntityResolver)cm_o;          
	}
}
