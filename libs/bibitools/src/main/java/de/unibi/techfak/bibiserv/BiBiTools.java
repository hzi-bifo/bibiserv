/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted 2010 BiBiServ Curator Team"
 *
 * Contributor(s):
 *
 */
package de.unibi.techfak.bibiserv;

import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.TenumValue;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.Tparam.Max;
import de.unibi.techfak.bibiserv.cms.Tparam.Min;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import de.unibi.techfak.bibiserv.debug.DDataSource;
import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.exception.IdNotFoundException;
import de.unibi.techfak.bibiserv.util.LogShed;
import de.unibi.techfak.bibiserv.util.Pair;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import java.util.regex.PatternSyntaxException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * BiBiTools - methods which support BiBiServ WSServerApplications
 *
 * @author Henning Mersch <hmersch@techfak.uni-bielefeld.de> (first release,
 * 2004)
 * @author Jan Krueger <jkrueger@techfak.uni-bielefeld.de> (second release,
 * 2008/2009)
 * @version $Id: BiBiTools.java,v 1.69 2006/03/13 12:59:31 jkrueger Exp jkrueger
 * $
 */
public class BiBiTools {

    /**
     * static logger for proper information see log4j.properties for more
     * information
     */
    private static final Logger log = Logger.getLogger(BiBiTools.class);
    /**
     * static datasource for db connection
     */
    private static DataSource datasource;
    /**
     * global properties
     */
    private static Properties properties;
    /**
     * DOM document representing the current tool is more or less equivalent to
     * previous tool properties
     */
    private TrunnableItem runnableitem;
    /**
     * user which starts current instance -- optional, if not given in
     * constructor build an unknown user object.
     */
    private User user;
    /**
     * Spool directory for storing result
     */
    private File spoolDir;
    /**
     * String of tmp directory for storing temp results. Will be deleted after
     * request!
     */
    private File tmpDir;

    /* Status of current project */
    private Status status;

    /*XPath */
    private XPath xpath;

    /*this.separator workaround*/
    private String separator;

    /* Name of tool of current BiBiTool instance */
    private String toolname;
    /* Name if function of current BiBiTools instance */
    private String functionname;
    private final static String BR = System.getProperty("line.separator");
    private String streamExecuteablePath = "";
    // The current version of bibis3
    private final static String BIBIS3 = "bibis3-1.4.1.jar";

    /////////////////////////
    //Contructors
    ////////////////////////
    /**
     * Default Constructor, if the id and user are unknown!
     *
     * Tooldescription resource is read as getResourceAsStream from resource
     * "/runnableitem".
     *
     * @throws de.unibi.techfak.bibiserv.exception.BiBIToolsException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    public BiBiTools() throws BiBiToolsException, IdNotFoundException {
        this(null, null, (InputStream) null);
    }

    /**
     * Default Constructor, if the id is unknown!
     *
     * Tooldescription resource is read as getResourceAsStream from resource
     * "/runnableitem".
     *
     * @throws de.unibi.techfak.bibiserv.exception.BiBIToolsException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    public BiBiTools(User user) throws BiBiToolsException, IdNotFoundException {
        this(null, user, (InputStream) null);
    }

    /**
     * Constructor, if id and user are unknown and non default runnable resource
     * should be used
     *
     *
     * @param runnableitem_is - Runnableitem inputstream, which contains the
     * tooldescription
     * @throws BiBiToolsException
     * @throws IdNotFoundException
     */
    public BiBiTools(InputStream runnableitem_is) throws BiBiToolsException, IdNotFoundException {
        this(null, null, runnableitem_is);
    }

    /**
     * Constructor, if id is unknown and user is known and a non default
     * runnable resource should be used
     *
     *
     * @param runnableitem_is - Runnableitem inputstream, which contains the
     * tooldescription
     * @throws BiBiToolsException
     * @throws IdNotFoundException
     */
    public BiBiTools(User user, InputStream runnableitem_is) throws BiBiToolsException, IdNotFoundException {
        this(null, user, runnableitem_is);
    }

    /**
     * Default Constructor, if the id is known!
     *
     * @param id
     *
     * @throws de.unibi.techfak.bibiserv.exception.BiBIToolsException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    public BiBiTools(String id) throws BiBiToolsException, IdNotFoundException {
        this(id, null, (InputStream) null);
    }

    /**
     * Default Constructor, if the id and user are known!
     *
     * @param id
     * @param user
     *
     * @throws de.unibi.techfak.bibiserv.exception.BiBIToolsException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     */
    public BiBiTools(String id, User user) throws BiBiToolsException, IdNotFoundException {
        this(id, user, (InputStream) null);
    }

    /**
     * Constructor, if id is known and non default runnable resource should be
     * used
     *
     * @param id
     * @param runnableitem_is - Runnableitem inputstream, which contains the
     * tooldescription
     * @throws BiBiToolsException
     * @throws IdNotFoundException
     */
    public BiBiTools(String id, InputStream runnableitem_is) throws BiBiToolsException, IdNotFoundException {
        this(id, null, runnableitem_is);
    }

    /**
     * One-for-all constructor. This Constructor should <b>never</b> used,
     * unless you know what are you doing!
     *
     * This Constructor can be used if the BiBitools should run without an
     * application server (e.g. for the JUNIT Tests)
     *
     * @param id
     * @param runnableitem_fn - Runnableitem xml filename, which contains the
     * tooldescription
     * @param datasource - Datasource, which should be used as jdbc:DataSource
     *
     * @throws de.unibi.techfak.bibiserv.exception.BiBIToolsException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     *
     */
    @Deprecated
    public BiBiTools(String id, String runnableitem_fn) throws BiBiToolsException, IdNotFoundException, FileNotFoundException {
        this(id, null, new FileInputStream(runnableitem_fn));
    }

    /**
     * One-for-all constructor. This Constructor should <b>never</b> used,
     * unless you know what are you doing!
     *
     * This Constructor can be used if the BiBitools should run without an
     * application server (e.g. for the JUNIT Tests)
     *
     * @param id
     * @param user
     * @param runnableitem_fn - Runnableitem xml filename, which contains the
     * tooldescription
     * @param datasource - Datasource, which should be used as jdbc:DataSource
     *
     * @throws de.unibi.techfak.bibiserv.exception.BiBIToolsException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     *
     */
    @Deprecated
    public BiBiTools(String id, User user, String runnableitem_fn) throws BiBiToolsException, IdNotFoundException, FileNotFoundException {
        this(id, user, new FileInputStream(runnableitem_fn));
    }

    /**
     * One-for-all constructor. This Constructor should <b>never</b> used,
     * unless you know what are you doing!
     *
     * This Constructor can be used if the BiBitools should run without an
     * application server (e.g. for the JUNIT Tests)
     *
     * @param id
     * @param runnableitem_file - Runnableitem xml file, which contains the
     * tooldescription
     * @param datasource - Datasource, which should be used as jdbc:DataSource
     *
     * @throws de.unibi.techfak.bibiserv.exception.BiBIToolsException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     *
     */
    @Deprecated
    public BiBiTools(String id, File runnableitem_file) throws BiBiToolsException, IdNotFoundException, FileNotFoundException {
        this(id, null, new FileInputStream(runnableitem_file));
    }

    /**
     * One-for-all constructor. This Constructor should <b>never</b> used,
     * unless you know what are you doing!
     *
     * This Constructor can be used if the BiBitools should run without an
     * application server (e.g. for the JUNIT Tests)
     *
     * @param id
     * @param user
     * @param runnableitem_file - Runnableitem xml file, which contains the
     * tooldescription
     * @param datasource - Datasource, which should be used as jdbc:DataSource
     *
     * @throws de.unibi.techfak.bibiserv.exception.BiBIToolsException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     *
     */
    public BiBiTools(String id, String fct_id, User user, File runnableitem_file) throws BiBiToolsException, IdNotFoundException, FileNotFoundException {
        this(id, fct_id, user, new FileInputStream(runnableitem_file));
    }

    @Deprecated
    public BiBiTools(String id, User user, InputStream runnableitem_is) throws BiBiToolsException, IdNotFoundException {
        this(id, "unknown", user, runnableitem_is);
    }

    /**
     * One-for-all constructor. This Constructor should <b>never</b> used,
     * unless you know what are you doing!
     *
     * This Constructor can be used if the BiBitools should run without an
     * application server (e.g. for the JUNIT Tests)
     *
     * @param id
     * @param fct_id
     * @param user
     * @param runnableitem_is - Runnableitem inputstream, which contains the
     * tooldescription
     * @param datasource - Datasource, which should be used as jdbc:DataSource
     *
     * @throws de.unibi.techfak.bibiserv.exception.BiBIToolsException
     * @throws de.unibi.techfak.bibiserv.exception.IdNotFoundException
     *
     */
    public BiBiTools(String id, String fct_id, User user, InputStream runnableitem_is) throws BiBiToolsException, IdNotFoundException {

        if (user == null) {
            try {
                this.user = new User(User.ANONYMOUS);
            } catch (DBConnectionException e) {
                log.fatal("A DBConnection exception occurred while creating an anonymous user!");
            }
        } else {
            this.user = user;
        }

        /* if runnablle item is not given, load it from resource */
        if (runnableitem_is == null) {
            log.info("load runnable item from resources ...");
            runnableitem_is = BiBiTools.class.getResourceAsStream("/runnableitem.xml"); //@ToDo : it is NOT a good idea to  hardcode the resourcename
            if (runnableitem_is == null) {
                log.fatal("get resource  '/runnableitem.xml' as stream failed ... ");
                throw new BiBiToolsException(700, "get resource  '/runnableitem.xml' as stream failed ... ");
            }
        }

        /*  load runnable item from description file */
        try {
            JAXBContext jaxbc = JAXBContext.newInstance("de.unibi.techfak.bibiserv.cms", BiBiTools.class.getClassLoader());
            Unmarshaller um = jaxbc.createUnmarshaller();
            JAXBElement<TrunnableItem> jaxbe = (JAXBElement) um.unmarshal(runnableitem_is);
            runnableitem = jaxbe.getValue();
        } catch (JAXBException e) {
            throw new BiBiToolsException("A JAXB exception while unmarshalling the XML-Inpustream!", e);
        }

        /* set toolname var */
        toolname = runnableitem.getId();
        /* set functionname */
        functionname = fct_id; // JK, should we check if this fct_id exists in tooldescription


        /* create a new status object */
        try {
            if (id == null) {
                this.status = new Status(this);
            } else {
                this.status = new Status(this, id);
            }
        } catch (DBConnectionException e) {
            log.fatal("A dbconnection exception occurred while create a new status object.", e);
            throw new BiBiToolsException("A dbconnection exception occurred while create a new status object.", e);
        }

        //File separator workaround
        if (System.getProperty("os.name").contains("Windows")) {
            separator = "\\";
        } else {
            separator = File.separator;
        }
    }

    /////////////////////////
    //Getter / Setter Methods
    /////////////////////////
    /**
     * Returns Current toolname
     *
     * @return Returns current toolname
     */
    public String getToolname() {
        return toolname;
    }

    /**
     * Returns current functionname
     *
     * @return Returns current functionname
     */
    public String getFunctionname() {
        return functionname;
    }

    /**
     * Set current functionname
     *
     * @param fct_name
     */
    public void setFunctionname(String fct_name) {
        functionname = fct_name;
    }

    /**
     * Get a bibitool property.
     *
     * @param propItem - name of the property item
     * @return Returns the value of the property item
     */
    public String getProperty(String propItem) {

        // if properties not yet initialized ...
        synchronized (this) {
            if (properties == null) {
                try {
                    // load Properties
                    loadBiBiProperties();

                } catch (BiBiToolsException e) {
                    log.fatal("Fatal error occurred while initializing BiBiTools properties.\n" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }

        // only for historical reason 
        if (propItem.equalsIgnoreCase("toolname")) {
            return toolname;
        }
        if (propItem.equalsIgnoreCase("functionname")) {
            return functionname;
        }

        // add user specific information to some properties, if user is known
        if (propItem.equals("spooldir.base")) {
            return properties.getProperty(propItem) + separator + user.getId();
        }

        return properties.getProperty(propItem);
    }

    /**
     * Get a BiBiTool property.
     *
     * @param propitem - name of the property item
     * @param defaultvalue - value used if property is not available
     * @return Returns the value of the property item or default value in case
     * propertyitem not found.
     */
    public String getProperty(String propitem, String defaultvalue) {
        String tmp = getProperty(propitem);
        if (tmp == null) {
            return defaultvalue;
        }
        return tmp;
    }

    /**
     * Reset bibitools properties.
     *
     * Forces to reload properties using getProperties next time.
     */
    public synchronized static void resetProperties() {
        properties = null;
    }

    /////////////////////////
    // public methods
    ////////////////////////
    ///////////////////////////// Spool Dir //////////////////////////////////////////////////
    /**
     * Return the status object.
     *
     * @return The status Object.
     */
    public Status getStatus() {

        return status;
    }

    /**
     * Set the Spooldirbase for current BiBiTools object. Overwrites
     * BiBiTool.properties 'spooldir.base'.
     *
     * <b>Attention:</b>
     * Set the SpoolDir for current BiBiTools object, but not generally. This
     * could make some trouble when using BiBiTools in an environment like
     * BiBiMainApp, where different BiBiTools object describe the same resource
     * (using the BiBiServ ID).
     *
     * Consider using @setSpoolDirBase(File spooldirbase, boolean link) for this
     * kind of application.
     *
     * @param f - new SpoolDirBase
     * @throws FileNotFoundException
     */
    public void setSpoolDirBase(File f) throws FileNotFoundException {
        spoolDir = createToolDir(new File(f, getToolname().toLowerCase()));
    }

    /**
     * Set the Spooldirbase for current BiBiTool object and link from
     * 'spooldir.base' location to current spooldir.
     *
     * @param f
     * @param link
     * @throws FileNotFoundException
     */
    public void setSpoolDirBase(File f, boolean link) throws FileNotFoundException {
        setSpoolDirBase(f);
        if (link) {
            try {
                File lf = new File(getProperty("spooldir.base") + this.separator + getToolname().toLowerCase(), getSpecificSpoolDir());
                if (!lf.exists()) { // create symbolic link
                    lf.getParentFile().mkdirs(); //create missing dirs
                    chmodDirs(new File(getProperty("spooldir.base")), lf); //change directory permissions
                    Files.createSymbolicLink(lf.toPath(), spoolDir.toPath());
                }
            } catch (IOException | DBConnectionException | IdNotFoundException ex) {
                throw new FileNotFoundException("Could not create spooldir ... !");
            }
        }
    }

    /**
     * getSpoolDir() returns spool directory of current job
     *
     * @return returns File spoolDir if spool directory exists
     * @exception FileNotFoundException thrown if spoolDir could not be created
     * (no setSpoolDir because it is generated form Id)
     */
    public File getSpoolDir() throws FileNotFoundException {
        if (spoolDir == null) {
            spoolDir = createToolDir(new File(getProperty("spooldir.base") + this.separator + getToolname().toLowerCase()));
        }
        return spoolDir;

    }

    /**
     * Creates a tool specific dir (for spool/tmp dir) following the form
     *
     *
     * @param basedir basis directory
     * @return newly create tool specific dir
     *
     * @throws FileNotFoundException
     */
    private File createToolDir(File basedir) throws FileNotFoundException {
        File dir;

        if (!basedir.exists()) {
            if (basedir.mkdirs()) {
                chmodDir(basedir);
                log.info("Create spooldir base for tool  " + getToolname() + " ... " + basedir);
            } else {
                log.error("Could not create base spooldir for tool " + getToolname() + " ... " + basedir);
            }
        }
        try {
            dir = new File(basedir, getSpecificSpoolDir());
        } catch (DBConnectionException | IdNotFoundException ex) {
            throw new FileNotFoundException("Could not create spooldir ... !");
        }
        if (dir.mkdirs()) {
            log.debug("generated tooldir..." + dir);
            chmodDirs(basedir, dir);
        } else if (dir.isDirectory()) {
            log.debug("Tried to create existing toolDir ... " + dir + "!");
        } else {
            log.error("Could not create tooldir ... " + dir + "!");
            throw new FileNotFoundException("Could not create tooldir ... " + dir + "!");
        }
        return dir;
    }

    /**
     * Return size (in bytes) of spooldir
     *
     *
     * @return
     */
    public long getSpoolDirSize() {
        if (spoolDir == null) {
            return 0;
        }
        return getFileSize(spoolDir);
    }

    /**
     * Returns size of the given file. If the given file is a directory, the
     * function returns the size used by the directory (and its files /
     * subdirectories).
     *
     * @param file
     * @return
     */
    public long getFileSize(File file) {
        if (file.isFile()) {
            return file.length();
        } else if (file.isDirectory()) {
            long l = file.length();
            for (File f : file.listFiles()) {
                l = l + getFileSize(f);
            }
            return l;
        }
        // in any other case return 0
        return 0;
    }

    /**
     * Returns the size of a given file in the current Spool directory.
     *
     * @param filename String to the file located in spoolDir
     * @return size of the file
     * @throws FileNotFoundException if file does not exist
     */
    public long getSpoolFileSize(String filename) throws FileNotFoundException {
        return getSpoolFileSize(new File(filename));
    }

    /**
     * Returns the size of a given file in the current Spool directory.
     *
     * @param filename String to the file located in spoolDir
     * @return size of the file
     * @throws FileNotFoundException if file does not exist
     */
    public long getSpoolFileSize(File filename) throws FileNotFoundException {
        File file = new File(getSpoolDir().toString() + "/" + filename.toString());
        return getFileSize(file);
    }

    /**
     * Return a stream from file in the Spool directory.
     *
     * @param filename String to the file located in spoolDir
     * @return BufferedInputStream of the file
     * @throws FileNotFoundException if spoolfile wasn't found
     */
    public InputStream getSpoolFileStream(String filename) throws FileNotFoundException {
        return getSpoolFileStream(new File(filename));
    }

    /**
     * Return a stream from file in the Spool directory.
     *
     * @param filename File located in spoolDir
     * @return BufferedInputStream of the file
     * @throws FileNotFoundException if spoolfile wasn't found
     */
    public InputStream getSpoolFileStream(File filename) throws FileNotFoundException {
        File file = new File(getSpoolDir().toString() + "/" + filename.toString());
        try {
            FileInputStream istr = new FileInputStream(file);
            BufferedInputStream bstr = new BufferedInputStream(istr); // promote
            return bstr;
        } catch (IOException e) {
            log.error("IOexecption while reading spoolfile " + file.toString() + ": " + e.getMessage());
            throw new FileNotFoundException(e.getMessage());
        }
    }

    /**
     * read a file from spoolDir
     *
     * @param filename String the file located in spoolDir
     * @exception java.io.FileNotFoundException if spoolfile wasn't found
     * @return byte[] of filename content
     */
    public byte[] readSpoolFile(String filename) throws FileNotFoundException {
        return readSpoolFile(new File(filename));
    }

    /**
     * read a file from spoolDir
     *
     * @param file File of absolute path (prefix spoolDir)
     * @exception java.io.FileNotFoundException if spoolfile wasn't found
     * @return byte[] of filename content
     */
    public byte[] readSpoolFile(File filename) throws FileNotFoundException {
        File file = new File(getSpoolDir().toString() + "/" + filename.toString());
        try {
            FileInputStream istr = new FileInputStream(file);
            BufferedInputStream bstr = new BufferedInputStream(istr); // promote

            int size = (int) file.length();  // get the file size (in bytes)

            log.debug("read file (length " + size + ") " + file);
            byte[] data = new byte[size]; // allocate byte array of right size

            if (bstr.read(data, 0, size) == -1) {   // read into byte array

                log.error("Error while reading file " + file);
            }
            bstr.close();
            return data;
        } catch (IOException e) {
            log.error("IOexecption while reading spoolfile " + file.toString() + ": " + e.getMessage());
            throw new FileNotFoundException(e.getMessage());
        }
    }

    /**
     * Write a s3cmd-config file to spoolDir using accesskey and secret key
     *
     * @param accessKey accessKey of the user
     * @param secretkey secretKey of the user
     * @exception FileNotFoundException if file couldn't be written
     */
    public void generateS3ConfigFile(String accessKey, String secretkey) throws FileNotFoundException {

        String content = "[default]" + BR
                + "access_key = " + accessKey + BR
                + "secret_key = " + secretkey;

        writeSpoolFile(new File("s3cmdConfig"), content);
    }

    /**
     * write a file to spoolDir
     *
     * @param filename String of the file located in spoolDir
     * @param content String data for file
     * @exception FileNotFoundException if file couldn't be written
     */
    public void writeSpoolFile(String filename, String content) throws FileNotFoundException {
        writeSpoolFile(new File(filename), content);
    }

    /**
     * Write a file to spoolDir !
     *
     * @param file File located in spoolDir
     * @param content String data for file
     * @exception FileNotFoundException If file couldn't be written
     */
    public void writeSpoolFile(File file, String content) throws FileNotFoundException {
        File f = new File(getSpoolDir(), file.toString());
        try {
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            w.write(content);
            w.flush();
            w.close();
            log.debug("file " + file.toString() + " written");
        } catch (IOException e) {
            log.error("can not write " + file.toString());
            throw new FileNotFoundException("Couldn't write File: " + file.toString());
        }
    }

    /**
     * Write a file to spoolDir!
     *
     * @param file File located in spooldir
     * @param content InputStream data for file
     * @exception FileNotFoundException If file could not be written !
     */
    public void writeSpoolFile(String filename, InputStream content) throws FileNotFoundException {
        writeSpoolFile(new File(filename), content);
    }

    /**
     * Write a file to spoolDir!
     *
     * @param file File located in spooldir
     * @param content InputStream data for file
     * @exception FileNotFoundException If file could not be written !
     */
    public void writeSpoolFile(File file, InputStream content) throws FileNotFoundException {
        File f = new File(getSpoolDir(), file.toString());
        try {
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            Reader r = new InputStreamReader(content, "UTF-8");
            for (int c = r.read(); c != -1; c = r.read()) {
                w.write((char) c);
            }
            w.flush();
            w.close();
            log.debug("File " + f.toString() + " written !");
        } catch (IOException e) {
            log.error("Could not write File " + f.toString() + "!");
            throw new FileNotFoundException("Could not write File  " + f.toString() + "!");
        }
    }

    /**
     * remove a file from spoolDir
     *
     * @param filename String file to be deleted
     * @exception FileNotFoundException if spoolfile was not found
     */
    public void rmSpoolFile(String filename) throws FileNotFoundException {
        rmSpoolFile(new File(filename));
    }

    /**
     * remove a file from spoolDir
     *
     * @param file File of relativr path
     * @exception FileNotFoundException if spoolfile wasn't found
     */
    public void rmSpoolFile(File filename) throws FileNotFoundException {
        File file = new File(getSpoolDir(), filename.toString());
        if (file.delete()) {
            log.debug("File " + file + " deleted !");
        } else {
            log.error("Could not delete File  " + file.toString() + "!");
            throw new FileNotFoundException("Could not delete file  " + file.toString() + "!");
        }
    }

    ///////////////////////////// reading the output file ///////////////////////////////////
    public Object retrieveOutputData(File outputFile, String type, String implementationType) throws BiBiToolsException, DBConnectionException, FileNotFoundException {

        byte[] output = readSpoolFile(outputFile);

        if (type.equals("PRIMITIVE")) {
            if (implementationType.equals("java.lang.String")) {
                return new String(output);
            } else if (implementationType.equals("java.lang.Integer")) {
                return Integer.parseInt(new String(output));
            } else if (implementationType.equals("java.lang.Float")) {
                return Float.parseFloat(new String(output));
            } else if (implementationType.equals("java.lang.Boolean")) {
                return Boolean.parseBoolean(new String(output));
            } else {
                status.setStatuscode(700, "Unsupported Primitive " + implementationType + ".");
                log.fatal(status);
                throw new BiBiToolsException(status);
            }

        } else if (type.equals("XML")) {
            try {
                return string2Jaxb(new String(output), Class.forName(implementationType));
            } catch (ClassNotFoundException e) {
                status.setStatuscode(700, "Internal Server Error", e.getMessage());
                log.fatal(status);
                throw new BiBiToolsException(status);
            } catch (JAXBException e) {
                status.setStatuscode(700, "Internal Server Error", e.getMessage());
                log.fatal(status);
                throw new BiBiToolsException(status);
            }
        }

        status.setStatuscode(700, "Internal Server Error", "Unknown input type \"" + type + "\"!");
        log.fatal(status);
        throw new BiBiToolsException(status);
    }

    public void setTmpDirBase(File f) throws FileNotFoundException {
        tmpDir = createToolDir(new File(f, getToolname().toLowerCase()));
    }

    ///////////////////////////// Tmp Dir //////////////////////////////////////////////////
    /**
     * getTmpDir() returns tmpDir of id - this will be deleted after request()
     * finished
     *
     * @return String tmpDir if existing/created or null if couldn't created
     * @exception FileNotFoundException thrown if tmpDir couldn't be created (no
     * setTmpDir because it is generated form Id)
     */
    public File getTmpDir() throws FileNotFoundException {
        if (tmpDir == null) {

            tmpDir = createToolDir(new File(getProperty("tmpdir.base") + getToolname().toLowerCase()));
        }
        return tmpDir;
    }

    /**
     * Return the runnableitem element
     *
     * @return
     */
    public Element getToolDescription() {
        return (Element) runnableitem;
    }

    /**
     * read a tmp file from tmpDir
     *
     * @param filename String the file located in tmpDir
     * @exception java.io.FileNotFoundException if tmpfile wasn't found
     * @return byte[] of filename content
     */
    public byte[] readTmpFile(String filename) throws FileNotFoundException {
        return this.readTmpFile(new File(filename));
    }

    /**
     * read a file from tmpDir
     *
     * @param file File of relative path
     * @exception java.io.FileNotFoundException if tmpfile wasn't found
     * @return byte[] of filename content
     */
    public byte[] readTmpFile(File file) throws FileNotFoundException {
        File f = new File(getTmpDir(), file.toString());
        try {
            FileInputStream istr = new FileInputStream(f);
            BufferedInputStream bstr = new BufferedInputStream(istr); // promote
            int size = (int) f.length();  // get the file size (in bytes)
            byte[] data = new byte[size]; // allocate byte array of right size
            bstr.read(data, 0, size);   // read into byte array
            bstr.close();
            return data;
        } catch (IOException e) {
            log.error("IOexecption while reading tmpfile " + file.toString() + ": " + e.getMessage());
            throw new FileNotFoundException(e.getMessage());
        }
    }

    /**
     * write a file to tmpDir
     *
     * @param filename String the file located in tmpDir
     * @param content String data for file
     * @exception FileNotFoundException if file couldn't be written
     */
    public void writeTmpFile(String filename, String content) throws FileNotFoundException {
        writeTmpFile(new File(filename), content);
    }

    /**
     * write a file to tmpDir
     *
     * @param file File of relative path
     * @param content String data for file
     * @exception FileNotFoundException if file couldn't be written
     */
    public void writeTmpFile(File file, String content) throws FileNotFoundException {
        File f = new File(getTmpDir(), file.toString());
        try {
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            w.write(content);
            w.flush();
            w.close();
            log.debug("File " + f.toString() + " written !");
        } catch (IOException e) {
            log.error("Could not write File " + f.toString() + " !");
            throw new FileNotFoundException("Could not write File  " + f.toString() + "!");
        }
    }

    /**
     * write a file to tmpDir
     *
     * @param filename String the file located in tmpDir
     * @param content InputStream data for file
     * @exception FileNotFoundException if file couldn't be written
     */
    public void writeTmpFile(String filename, InputStream content) throws FileNotFoundException {
        writeTmpFile(new File(filename), content);
    }

    /**
     * write a file to tmpDir
     *
     * @param file File of relative path
     * @param content InputStream data for file
     * @exception FileNotFoundException if file couldn't be written
     */
    public void writeTmpFile(File file, InputStream content) throws FileNotFoundException {
        File f = new File(getTmpDir(), file.toString());
        try {
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            Reader r = new InputStreamReader(content);
            for (int c = r.read(); c != -1; c = r.read()) {
                w.write((char) c);
            }
            w.flush();
            w.close();
            log.debug("File " + f + " written !");
        } catch (IOException e) {
            log.error("Could not write File " + f + " !");
            throw new FileNotFoundException("Could not write File " + f + "!");
        }
    }

    /**
     * Static Method paramdoc2list converts a param DOM document of the form :
     *
     * <pre>
     *  &lt;param>&gt;s
     *    &lt;param_key&gt;param_value&lt;/param_key&gt;>
     *    &lt;param_key>&gt;param_value2&lt;/param_key&gt;>
     *    ...
     *    &lt;param_key2&gt;>param_value3&lt;/param_key&gt;>
     *    ...
     * </pre>
     *
     * to a list of Pair<String,String> objects.
     *
     * @param paramdoc DOM document containing parameter as key/value pairs ...
     *
     * @return Returns a list of Pair<String,String> objects
     */
    public static List<Pair<String, String>> paramdoc2List(Document paramdoc) {
        List<Pair<String, String>> list = new ArrayList<>();

        /* the param document should contain only param tag, which is the root tag */
        Element paramroot = (Element) paramdoc.getElementsByTagName("param").item(0);
        if (paramroot != null) {
            /* save all child node as HashMap Elements */
            NodeList paramlist = paramroot.getChildNodes();
            /* iterate over all element nodes */
            for (int i = 0; i < paramlist.getLength(); ++i) {
                Node param = paramlist.item(i);
                if (param.getNodeType() == Node.ELEMENT_NODE) {
                    /* the name of the tag is the name of the parameter
                 the text content is the value */
                    Pair pair = new Pair(param.getNodeName(), param.getTextContent());
                    list.add(pair);
                }
            }
        } else {
            log.warn("Document '" + paramdoc.getDocumentURI() + "' does not contain a param tag!");
        }
        return list;
    }

    /**
     * Converts a string to a jaxb element using the given jaxbclass.
     *
     * @param content - String to be converted to jaxb element
     * @param jaxbclass - class of the jaxbrootelement
     * @return Return the jaxb element (as object)
     *
     * @throws throws an JAXBException in the case of an error.
     */
    public static Object string2Jaxb(String content, Class jaxbclass) throws JAXBException {
        JAXBContext jaxbc = JAXBContext.newInstance(jaxbclass);
        Unmarshaller um = jaxbc.createUnmarshaller();
        JAXBElement jaxbe = (JAXBElement) um.unmarshal(new StringReader(content));
        return jaxbe.getValue();
    }

    /**
     * Converts a JAXBElement to a string using the given jaxbclass.
     *
     * @param content - Jaxb Element to be converted to a String
     * @param jaxbclass - class of the jaxbrootelement
     * @return Return the String representation of JAXBelement
     *
     * @throws throws an JAXBException in the case of an error.
     */
    public static String jaxb2String(Object content, Class jaxbclass) throws JAXBException {
        JAXBContext jaxbc = JAXBContext.newInstance(jaxbclass);
        Marshaller m = jaxbc.createMarshaller();
        StringWriter sw = new StringWriter();
        m.marshal(content, sw);
        return sw.toString();

    }

    /**
     * The function checkandParseParam checks and parse the parameters stored in
     * a parameterlist (a list of key/name value pairs) containing the parameter
     * description for this function request. The parameterlist can be generated
     *
     * In the case of successfull parsing and validating a hashmap is returned.
     *
     * <ul>
     * <li> key == id/name</li>
     * <li> value == <b>option?<b> <b>value_of_parameter</b>
     * </ul>
     *
     *
     * @param paramlist - to be checked and parsed parameterlist (List of (name,
     * value) pairs )
     * @param String id - id of the function the parameter document belongs to
     * @param LogShed logshed - micro logger containing remarks at different log
     * level
     * @return HashMap<String,String> containg alle parameters currently used
     * including all default and not set parameter (key = name/id, value =
     * parameterstring)
     */
    public HashMap<String, String> checkAndParseParam(List<Pair<String, String>> paramlist, String id, LogShed logshed) {

        HashMap<String, String> paramhash = new HashMap<>();

        Tfunction function = search_for_function(id);

        if (function == null) {
            logshed.error("Did not found any function matching id = '" + id + "!");
        } else {

            if (!function.isSetParamGroup()) {
                logshed.info("No Paramgroup in this function!");
                return paramhash;
            }

            // get a list of parameter and enum parameter referenced by functions parameter group
            List<Object> list_of_param_and_enum = new ArrayList<Object>();
            getParamAndEnumListfromParamGroup(function.getParamGroup(), list_of_param_and_enum);

            /* test all child nodes, which elements are parameters */
            for (Pair<String, String> param : paramlist) {

                /* the name of the tag is the name of the parameter
                 the text content is the value */
                String param_name = param.getKey();
                String param_value = param.getValue();
                String ename = "[" + param_name + "] ";

                // search for parameter matching the name
                Tparam param_JAXB = (Tparam) search_for(Tparam.class, param_name, list_of_param_and_enum);
                if (param_JAXB != null) {
                    // check type
                    if (!param_JAXB.isSetType()) { //this should never occur
                        logshed.fatal("No type set for param \"" + param_name + "\"!");

                    } else {
                        // type of param is int
                        if (param_JAXB.getType().value().equalsIgnoreCase("int")) {
                            try {
                                int value = Integer.parseInt(param_value);
                                // test for constraint 'minimum'
                                if (param_JAXB.isSetMin()) {
                                    Min min = param_JAXB.getMin();
                                    if (min.isIncluded() && (value < min.getValue())) {
                                        logshed.error(ename + " Value of parameter does not fulfill constraint 'min'"
                                                + " : " + value + " >= " + min.getValue() + "!");
                                    } else if (value <= min.getValue()) {
                                        logshed.error(ename + " Value of parameter does not fulfill constraint 'min'"
                                                + " : " + value + " > " + min.getValue() + "!");
                                    }
                                }
                                // test for constraint 'maximum'
                                if (param_JAXB.isSetMax()) {
                                    Max max = param_JAXB.getMax();
                                    if (max.isIncluded() && (value > max.getValue())) {
                                        logshed.error(ename + " Value of parameter does not fulfill constraint 'max'"
                                                + " : " + value + " <= " + max.getValue() + "!");
                                    } else if (value >= max.getValue()) {
                                        logshed.error(ename + " Value of parameter does not fulfill constraint 'min'"
                                                + " : " + value + " < " + max.getValue() + "!");
                                    }
                                }
                            } catch (NumberFormatException e) {
                                logshed.error(ename + " can't be parsed as Integer value.");
                            }
                        } else // type of param is a float
                        {
                            if (param_JAXB.getType().value().equalsIgnoreCase("float")) {
                                try {
                                    float value = Float.parseFloat(param_value);
                                    // test for constraint 'minimum'
                                    if (param_JAXB.isSetMin()) {
                                        Min min = param_JAXB.getMin();
                                        if (min.isIncluded() && (value < min.getValue())) {
                                            logshed.error(ename + " Value of parameter does not fulfill constraint 'min'"
                                                    + " : " + value + " >= " + min.getValue() + "!");
                                        } else if (value <= min.getValue()) {
                                            logshed.error(ename + " Value of parameter does not fulfill constraint 'min'"
                                                    + " : " + value + " > " + min.getValue() + "!");
                                        }
                                    }
                                    // test for constraint 'maximum'
                                    if (param_JAXB.isSetMax()) {
                                        Max max = param_JAXB.getMax();
                                        if (max.isIncluded() && (value > max.getValue())) {
                                            logshed.error(ename + " Value of parameter does not fulfill constraint 'max'"
                                                    + " : " + value + " <= " + max.getValue() + "!");
                                        } else if (value >= max.getValue()) {
                                            logshed.error(ename + " Value of parameter does not fulfill constraint 'min'"
                                                    + " : " + value + " < " + max.getValue() + "!");
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    logshed.error(ename + " can't be parsed as Float value.");
                                }

                            } else // type of param is a string ...
                            {
                                if (param_JAXB.getType().value().equalsIgnoreCase("string")) {
                                    // test for constraint 'minlength'
                                    if (param_JAXB.isSetMinLength()) {
                                        Integer minlength = param_JAXB.getMinLength();
                                        if (param_value.length() < minlength) {
                                            logshed.error(ename + " Value of parameter does not fulfill constraint 'minlength'"
                                                    + " : length(" + param_value + ") >= " + minlength + "!");
                                        }

                                    }
                                    // test for constraint 'maxlength'
                                    if (param_JAXB.isSetMaxLength()) {
                                        Integer maxlength = param_JAXB.getMaxLength();
                                        if (param_value.length() > maxlength) {
                                            logshed.error(ename + " Value of parameter does not fulfill constraint 'maxlength'"
                                                    + " : length(" + param_value + ") >= " + maxlength + "!");
                                        }
                                    }
                                    // test for constraint 'regular expression'
                                    if (param_JAXB.isSetRegexp()) {
                                        String regexp = param_JAXB.getRegexp();
                                        try {
                                            if (!param_value.matches(regexp)) {
                                                logshed.error(ename + " Value of parameter does not fulfill constraint 'regexp' "
                                                        + " : regexp (" + regexp + ") does match '" + param_value + "'");
                                            }
                                        } catch (PatternSyntaxException e) {
                                            logshed.error(ename + " contains a invalid 'regexp' element."
                                                    + System.getProperty("line.separator") + e.getMessage());
                                        }
                                    }
                                } else // type of param is an boolean
                                {
                                    if (param_JAXB.getType().value().equalsIgnoreCase("boolean")) {
                                        if (!param_JAXB.isSetOption()) {
                                            logshed.warn(ename + " is a boolean parameter without set option ... since boolean parameter are"
                                                    + "understanded as 'switch', a boolean parameter without set option make not sense! ");
                                        }
                                    } else // type of param is datetime which isnt yet supported
                                    {
                                        if (param_JAXB.getType().value().equalsIgnoreCase("datetime")) {
                                            logshed.warn(ename + " has type 'datetime' which is NYI!");
                                        } else { // otherwise the type is not known/ unsupported
                                            logshed.fatal(ename + " has unknown/unsupported type :" + param_JAXB.getType().value());
                                        }
                                    }
                                }
                            }
                        }

                        // After validation add parameter to param_hash ...
                        // we must distinguish between boolean other types based  parameter
                        // boolean parameter are understanded as switch
                        if (param_JAXB.getType().value().equalsIgnoreCase("boolean")) {
                            boolean b_value = Boolean.parseBoolean(param_value);
                            paramhash.put(param_name, b_value ? (param_JAXB.isSetOption() ? param_JAXB.getOption() : "") : "");
                        } else {
                            paramhash.put(param_name, (param_JAXB.isSetOption() ? param_JAXB.getOption() : "") + param_value);
                        }
                    }
                    //otherwise search for an enum parameter matching the name
                } else {
                    TenumParam enumparam_JAXB = (TenumParam) search_for(TenumParam.class, param_name, list_of_param_and_enum);
                    if (enumparam_JAXB != null) {

                        String listseparator = enumparam_JAXB.isSetSeparator() ? enumparam_JAXB.getSeparator() : ",";
                        StringBuilder sb = new StringBuilder();
                        // double entries are not allowed, remove double entries ...
                        List<String> param_value_list = Arrays.asList(param_value.split(" "));
                        Set<String> param_value_set = new TreeSet<>(param_value_list);
                        // ... feed logshed with a warning if double keys occurrs
                        if (param_value_set.size() != param_value_list.size()) {
                            logshed.warn("Remove " + (param_value_list.size() - param_value_set.size()) + " double key entries from EnumParameter '" + ename + "'.");
                        }
                        // param_value could be single value or a space separated list of keys
                        for (String k : param_value_set) {
                            String value = getValueforKeyfromEnumParam(enumparam_JAXB.getValues(), k);
                            if (value == null) {
                                logshed.warn("EnumParameter '" + ename + "' contains an invalid valuekey '" + k + "', skip the key. Valid Keys are : " + EnumParamList2String(enumparam_JAXB.getValues()));
                            } else {
                                if (sb.length() != 0) {
                                    sb.append(listseparator);
                                }
                                sb.append(value);
                            }

                        }
                        // warn if no or no valid valuekeys (sb.length == 0) available
                        if (sb.length() == 0) {
                            logshed.warn("EnumParameter '" + ename + "' contains no or no valid valuekeys, ignore it.");
                        } else {
                            paramhash.put(param_name, (enumparam_JAXB.isSetOption() ? enumparam_JAXB.getOption() : "")
                                    + (enumparam_JAXB.isSetPrefix() ? enumparam_JAXB.getPrefix() : "")
                                    + sb.toString()
                                    + (enumparam_JAXB.isSetSuffix() ? enumparam_JAXB.getSuffix() : ""));
                        }

                    } else {
                        //the given (parameter) name does not match any parameter - do nothing but print a warnung into the logshed
                        logshed.warn("Parameter '" + ename + "' does not match any parameter from the tooldesciption function with id '" + id + "', ignore it");
                    }

                }

            }


            /*
             * search for any default parameter given by the tools parameter description.
             * Add them if they are not given by the call/parameter list
             * */
            // iterate over list  ...
            for (Object t : list_of_param_and_enum) {
                if (t instanceof Tparam) {
                    Tparam param = (Tparam) t;
                    // if key exists we can skip further processing ...
                    if (!paramhash.containsKey(param.getId())) {
                        if (param.isSetDefaultValue()) {

                            // We must distinguish between boolean other types based  parameter
                            // boolean parameter are understanded as switch
                            if (param.getType().value().equalsIgnoreCase("boolean")) {
                                boolean b_value = Boolean.parseBoolean(param.getDefaultValue());
                                paramhash.put(param.getId(), b_value ? (param.isSetOption() ? param.getOption() : "") : "");
                            } else {
                                paramhash.put(param.getId(), (param.isSetOption() ? param.getOption() : "") + param.getDefaultValue());
                            }

                        }
                    }

                } else if (t instanceof TenumParam) {
                    TenumParam enum_param = (TenumParam) t;
                    // if key exists we can skip further processing ...
                    if (!paramhash.containsKey(enum_param.getId())) {
                        // check if default values are set in enum specification
                        String value = null;
                        for (TenumValue enum_value : enum_param.getValues()) {
                            if (enum_value.isDefaultValue()) {
                                if (value == null) {
                                    value = enum_value.getValue();
                                } else {
                                    value = value + (enum_param.isSetSeparator() ? enum_param.getSeparator() : ",") + enum_value.getValue();
                                }
                            }
                        }
                        // if enumParam has some default values and no values are given by then parameter description ...
                        if (value != null) {
                            // ... add defaults to parameter hash
                            paramhash.put(enum_param.getId(), (enum_param.isSetOption() ? enum_param.getOption() : "")
                                    + (enum_param.isSetPrefix() ? enum_param.getPrefix() : "")
                                    + value
                                    + (enum_param.isSetSuffix() ? enum_param.getSuffix() : ""));
                        }
                    }
                }
            }
        }

        return paramhash;
    }

    /**
     * Creates List of all existing files in the given folder matching the
     * wildcard.
     *
     * @param folder String of the folder(s)
     * @param wildcard Wildcard matching all files
     * @return List of all existing files in the given folder matching the
     * wildcard.
     */
    public List<String> getAllFilesInSpoolfirMatchingWildcard(String folder, String wildcard) {
        List<String> result = new ArrayList<String>();
        try {
            File search = new File(getSpoolDir(), folder);
            FileFilter fileFilter = new WildcardFileFilter(wildcard);
            getAllFilesInSpooldirMatchingFilterRecursion(search, fileFilter, result);
        } catch (FileNotFoundException ex) {
        }
        return result;
    }

    private void getAllFilesInSpooldirMatchingFilterRecursion(File search, FileFilter fileFilter, List<String> found) {

        try {
            File[] children = search.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isDirectory()) {
                        getAllFilesInSpooldirMatchingFilterRecursion(child, fileFilter, found);
                    }
                }
            }
            children = search.listFiles(fileFilter);
            if (children != null) {
                for (File child : children) {
                    if (child.isFile()) {
                        found.add(getSpoolDir().toURI().relativize(child.toURI()).getPath());
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            log.error("Error while searching existing files.");
        }

    }

    private void initStreamExecuteablePath() {
        streamExecuteablePath = getProperty("aws.bin");
        if (!streamExecuteablePath.endsWith(separator)) {
            streamExecuteablePath += this.separator;
        }
    }

    /**
     * A helpher class getting the spooldir with trailing separator and error
     * handling.
     *
     * @return
     */
    private String getSpoolDirWithSeparator() throws BiBiToolsException, DBConnectionException {
        String spool;
        try {
            spool = getSpoolDir().toString() + this.separator;
        } catch (FileNotFoundException e) {
            log.fatal("Spool directory could not be created.");
            status.setStatuscode(701, "Spool directory could not be created.");
            throw new BiBiToolsException(status.toString(), e);
        }
        return spool;
    }

    @Deprecated
    public void parseUpload(String awsBucket, String awsFolder, String accessKey, String secretKey, String localFolder, String wildcard, CmdLineInfo info) throws DBConnectionException, BiBiToolsException {
        Pair<String, String> folderAndWilcard = new Pair<String, String>(localFolder, wildcard);
        List<Pair<String, String>> folderAndWildcardList = new ArrayList<Pair<String, String>>();
        folderAndWildcardList.add(folderAndWilcard);
        parseUpload(awsBucket, awsFolder, accessKey, secretKey, "", folderAndWildcardList, info);
    }

    public void parseUpload(String awsBucket, String awsFolder, String accessKey, String secretKey, String sessiontoken, List<Pair<String, String>> localFoldersAndWildcards, CmdLineInfo info) throws DBConnectionException, BiBiToolsException {

        initStreamExecuteablePath();

        // remove possible trailing slash of bucket
        if (awsBucket.endsWith("/")) {
            awsBucket = awsBucket.substring(0, awsBucket.length() - 1);
        }

        // make fitting aws folder, make sure folder is non-empty and front and trailing slash is placed
        if (!awsFolder.replaceAll("/", "").isEmpty()) {
            if (!awsFolder.startsWith("/")) {
                awsFolder = "/" + awsFolder;
            }
            if (!awsFolder.endsWith("/")) {
                awsFolder = awsFolder + "/";
            }
        } else {
            awsFolder = "";
        }

        String spool = getSpoolDirWithSeparator();

        // create a new empty file to store pathes to files to be uploaded
        info.getAfterBody().append("echo -n \"\" > uploadfiles").append(BR);

        for (Pair<String, String> pair : localFoldersAndWildcards) {
            String localFolder = pair.getKey();
            String wildcard = pair.getValue();

            // make fitting local folder, make sure folder is non-empty and no front and trailing slash is placed
            if (!localFolder.replaceAll("/", "").isEmpty()) {
                if (localFolder.startsWith("/")) {
                    localFolder = localFolder.substring(1);
                }
                if (localFolder.endsWith("/")) {
                    localFolder = localFolder.substring(0, localFolder.length() - 1);
                }
            } else {
                localFolder = "";
            }

            info.getAfterBody().append("find ")
                    .append(spool).append(localFolder).append(" -type f -name '")
                    .append(wildcard).append("' >> uploadfiles").append(BR);
        }

        info.getAfterBody().append("cat uploadfiles | ");
        info.getAfterBody().append("java -jar \"").append(streamExecuteablePath).append(BIBIS3).append("\" --upload-list-stdin ")
                .append("--access-key ").append(accessKey)
                .append(" --secret-key ").append(secretKey);
        if (!sessiontoken.isEmpty()) {
            info.getAfterBody().append(" --session-token ").append(sessiontoken);
        }
        info.getAfterBody().append(" -u ")
                .append("s3://").append(awsBucket).append(awsFolder);

        info.getAfterBody().append(" | java -jar \"")
                .append(streamExecuteablePath).append("downAndUploadLogger.jar\"")
                .append(" ").append(getProperty("db.host"))
                .append(" ").append(getProperty("db.port"))
                .append(" ").append(getProperty("db.user"))
                .append(" ").append(getProperty("db.pwd"))
                .append(" \"Uploading results...").append("\"")
                .append(" awsUpload")
                .append(" ").append(status.getId())
                .append(" ").append(this.getToolname())
                .append(" ").append(info.getInputNumber()).append(BR);

        info.incInputNumber();

        info.getAfterBody().append("if [ `echo \"${PIPESTATUS[@]}\" | tr -s ' ' + | bc` -ne 0 ]; then").append(BR);
        info.getAfterBody().append("\texit 5").append(BR);
        info.getAfterBody().append("fi").append(BR);
    }

    public String parseInputAWS(String id, Map<String, String> inputhash, String bucket,
            String file, String accessKey, String secretKey, CmdLineInfo info,
            String validator, List<String> converter, String content,
            String strictness, String cardinality, boolean alignment, boolean streamsSupported) throws BiBiToolsException, DBConnectionException {
        return parseInputAWS(id, inputhash, bucket, file, accessKey, secretKey, "", info, validator, converter, content, strictness, cardinality, alignment, streamsSupported);
    }

    /**
     * Create the execution code (downloading, validation, conversion and
     * managing very step) and put usage string into hashmap for generating the
     * tool commandline.
     *
     * @param id Id of the Input
     * @param inputhash hash to put usage string for the tool commandline
     * @param bucket s3 bucket to download from
     * @param file s3 file in the bucket to download
     * @param accessKey s3 accesskey
     * @param secretKey s3 secretkey
     * @param sessiontoken s3 session token, empty if none is available
     * @param info an object containing the generated execution code
     * @param validator Implementing class of the validator, null for ignore
     * validation and conversion
     * @param converter List of Implementing Classes in order of needed
     * Conversions
     * @param content Content of the Representation-
     * @param strictness Strictness of the Representation.
     * @param cardinality Cardinality of the Representation.
     * @param streamsSupported Does the tool support streams as Input?
     * @return postfix needed for tool commandline generation
     * @throws BiBiToolsException
     * @throws DBConnectionException
     */
    public String parseInputAWS(String id, Map<String, String> inputhash, String bucket,
            String file, String accessKey, String secretKey, String sessiontoken, CmdLineInfo info,
            String validator, List<String> converter, String content,
            String strictness, String cardinality, boolean alignment, boolean streamsSupported) throws BiBiToolsException, DBConnectionException {

        StringBuilder init;
        if (streamsSupported) {
            init = info.getInitStream();
        } else {
            init = info.getInitDownload();
        }

        // get jar path
        initStreamExecuteablePath();

        // get the spool directory
        String spool = getSpoolDirWithSeparator();

        // create download
        String base = "input_" + info.getInputNumber() + "_download";
        String baseLog = base + "_log";
        // create log pipe
        init.append("mkfifo ").append(spool).append(baseLog).append(BR);

        if (validator == null && !streamsSupported) {

            // download command
            init.append("java -jar \"").append(streamExecuteablePath).append(BIBIS3).append("\"")
                    .append(" --access-key ").append(accessKey)
                    .append(" --secret-key ").append(secretKey);
            if (!sessiontoken.isEmpty()) {
                init.append(" --session-token ").append(sessiontoken);
            }
            init.append(" -d ")
                    .append("s3://").append(bucket).append("/").append(file).append(" ")
                    .append(spool).append(base)
                    .append(" > ").append(spool).append(baseLog).append(" &").append(BR);
            // management
            generateHandler(base, spool, 3, info, streamsSupported);

            init.append("java -jar \"").append(streamExecuteablePath).append("downAndUploadLogger.jar\"")
                    .append(" ").append(getProperty("db.host"))
                    .append(" ").append(getProperty("db.port"))
                    .append(" ").append(getProperty("db.user"))
                    .append(" ").append(getProperty("db.pwd"))
                    .append(" ").append(file)
                    .append(" awsDownloadMultiThread")
                    .append(" ").append(status.getId())
                    .append(" ").append(this.getToolname())
                    .append(" ").append(info.getInputNumber())
                    .append(" < ").append(spool).append(baseLog).append(" &").append(BR);

            generateHandler(baseLog, spool, 3, info, streamsSupported);

            info.incInputNumber();
            return setInputHash(id, inputhash, spool, base);
        }

        // create download_pipe
        init.append("mkfifo ").append(spool).append(base).append(BR);

        //write command
        // download command
        init.append("java -jar \"").append(streamExecuteablePath).append(BIBIS3).append("\" --streaming-download ")
                .append("--access-key ").append(accessKey)
                .append(" --secret-key ").append(secretKey);
        if (!sessiontoken.isEmpty()) {
            init.append(" --session-token ").append(sessiontoken);
        }
        init.append(" -d ")
                .append("s3://").append(bucket).append("/").append(file).append(" ")
                .append(spool).append(base)
                .append(" > ").append(spool).append(baseLog).append(" &").append(BR);
        // management
        generateHandler(base, spool, 3, info, streamsSupported);

        init.append("java -jar \"").append(streamExecuteablePath).append("downAndUploadLogger.jar\"")
                .append(" ").append(getProperty("db.host"))
                .append(" ").append(getProperty("db.port"))
                .append(" ").append(getProperty("db.user"))
                .append(" ").append(getProperty("db.pwd"))
                .append(" ").append(file)
                .append(" awsDownload")
                .append(" ").append(status.getId())
                .append(" ").append(this.getToolname())
                .append(" ").append(info.getInputNumber())
                .append(" < ").append(spool).append(baseLog).append(" &").append(BR);

        generateHandler(baseLog, spool, 3, info, streamsSupported);

        return generateValidatorAndConverter(id, inputhash, info, validator, converter, content, strictness, cardinality, alignment, spool, streamsSupported, file);
    }

    /**
     * Create the execution code (downloading, validation, conversion and
     * managing very step) and put usage string into hashmap for generating the
     * tool commandline.
     *
     * @param id Id of the Input
     * @param inputhash hash to put usage string for the tool commandline
     * @param url Url to download from
     * @param info an object containing the generated execution code
     * @param validator Implementing class of the validator, null for ignore
     * validation and conversion
     * @param converter List of Implementing Classes in order of needed
     * Conversions
     * @param content Content of the Representation-
     * @param strictness Strictness of the Representation.
     * @param cardinality Cardinality of the Representation.
     * @param streamsSupported Does the tool support streams as Input?
     * @return postfix needed for tool commandline generation
     * @throws BiBiToolsException
     * @throws DBConnectionException
     */
    public String parseInputURL(String id, Map<String, String> inputhash, String url,
            CmdLineInfo info, String validator, List<String> converter, String content,
            String strictness, String cardinality, boolean alignment, boolean streamsSupported) throws BiBiToolsException, DBConnectionException {

        StringBuilder init;
        if (streamsSupported) {
            init = info.getInitStream();
        } else {
            init = info.getInitDownload();
        }

        // get jar path
        initStreamExecuteablePath();

        // get the spool directory
        String spool = getSpoolDirWithSeparator();
        // get filename from url
        String filename = url;
        int questionIndex = filename.indexOf('?');
        if (questionIndex != -1) {
            filename = filename.substring(0, questionIndex);
        }
        int slashIndex = filename.lastIndexOf('/');
        filename = filename.substring(slashIndex + 1);

        // create download
        String base = "input_" + info.getInputNumber() + "_download";
        String baseLog = base + "_log";
        // create log pipe
        init.append("mkfifo ").append(spool).append(baseLog).append(BR);

        if (validator == null && !streamsSupported) {

            // download command
            init.append("java -jar \"").append(streamExecuteablePath).append(BIBIS3).append("\"");
            init.append(" -g \"")
                    .append(url).append("\" ")
                    .append(spool).append(base)
                    .append(" > ").append(spool).append(baseLog).append(" &").append(BR);
            // management
            generateHandler(base, spool, 3, info, streamsSupported);

            init.append("java -jar \"").append(streamExecuteablePath).append("downAndUploadLogger.jar\"")
                    .append(" ").append(getProperty("db.host"))
                    .append(" ").append(getProperty("db.port"))
                    .append(" ").append(getProperty("db.user"))
                    .append(" ").append(getProperty("db.pwd"))
                    .append(" ").append(filename)
                    .append(" awsDownloadMultiThread")
                    .append(" ").append(status.getId())
                    .append(" ").append(this.getToolname())
                    .append(" ").append(info.getInputNumber())
                    .append(" < ").append(spool).append(baseLog).append(" &").append(BR);

            generateHandler(baseLog, spool, 3, info, streamsSupported);

            info.incInputNumber();
            return setInputHash(id, inputhash, spool, base);
        }

        // create download pipe
        init.append("mkfifo ").append(spool).append(base).append(BR);

        // download command
        init.append("java -jar \"").append(streamExecuteablePath).append(BIBIS3).append("\" --streaming-download ");
        init.append(" -g \"")
                .append(url).append("\" ")
                .append(spool).append(base)
                .append(" > ").append(spool).append(baseLog).append(" &").append(BR);
        // management
        generateHandler(base, spool, 3, info, streamsSupported);

        init.append("java -jar \"").append(streamExecuteablePath).append("downAndUploadLogger.jar\"")
                .append(" ").append(getProperty("db.host"))
                .append(" ").append(getProperty("db.port"))
                .append(" ").append(getProperty("db.user"))
                .append(" ").append(getProperty("db.pwd"))
                .append(" ").append(filename)
                .append(" urlDownload")
                .append(" ").append(status.getId())
                .append(" ").append(this.getToolname())
                .append(" ").append(info.getInputNumber())
                .append(" < ").append(spool).append(baseLog).append(" &").append(BR);

        generateHandler(baseLog, spool, 3, info, streamsSupported);

        return generateValidatorAndConverter(id, inputhash, info, validator, converter, content, strictness, cardinality, alignment, spool, streamsSupported, filename);
    }

    /**
     * Create the execution code (validation, conversion and managing very step)
     * and put usage string into hashmap for generating the tool commandline.
     *
     * @param id Id of the Input
     * @param inputhash hash to put usage string for the tool commandline
     * @param uri URI of the file localized on the server
     * @param info an object containing the generated execution code
     * @param validator Implementing class of the validator, null for ignore
     * validation and conversion
     * @param converter List of Implementing Classes in order of needed
     * Conversions
     * @param content Content of the Representation-
     * @param strictness Strictness of the Representation.
     * @param cardinality Cardinality of the Representation.
     * @param streamsSupported Does the tool support streams as Input?
     * @return postfix needed for tool commandline generation
     * @throws BiBiToolsException
     * @throws DBConnectionException
     */
    public String parseInputLocalFile(String id, Map<String, String> inputhash, String uri,
            CmdLineInfo info, String validator, List<String> converter, String content,
            String strictness, String cardinality, boolean alignment, boolean streamsSupported) throws BiBiToolsException, DBConnectionException {

        // get the spool directory
        String spool = getSpoolDirWithSeparator();

        initStreamExecuteablePath();

        // if no validation is needed nothing needs to be done, disregarding of streamsupport
        if (validator == null) {
            return setInputHash(id, inputhash, "", uri);
        }

        StringBuilder init;
        if (streamsSupported) {
            init = info.getInitStream();
        } else {
            init = info.getInitDownload();
        }

        // get filename from url
        int slashIndex = uri.lastIndexOf('/');
        String filename = uri.substring(slashIndex + 1);

        // create download
        String base = "input_" + info.getInputNumber() + "_download";
        // create pipe
        init.append("mkfifo ").append(spool).append(base).append(BR);
        // download command
        init.append("cat ").append(uri).append(" > ").append(spool).append(base).append(" &").append(BR);
        // management
        generateHandler(base, spool, 3, info, streamsSupported);

        return generateValidatorAndConverter(id, inputhash, info, validator, converter, content, strictness, cardinality, alignment, spool, streamsSupported, filename);
    }

    /**
     * A private helper class that generates execution code for validation and
     * conversion.
     *
     * @param id Id of the Input
     * @param inputhash hash to put usage string for the tool commandline
     * @param info an object containing the generated execution code
     * @param validator Implementing class of the validator, null for ignore
     * validation
     * @param converter List of Implementing Classes in order of needed
     * Conversions
     * @param content Content of the Representation-
     * @param strictness Strictness of the Representation.
     * @param cardinality Cardinality of the Representation.
     * @param spool the spool directory to use
     * @param streamsSupported Does the Tool support input as stream?
     * @param filename file name to pass to validator and converter for display
     * only
     * @return postfix needed for tool commandline generation
     * @throws DBConnectionException
     * @throws BiBiToolsException
     */
    private String generateValidatorAndConverter(String id, Map<String, String> inputhash,
            CmdLineInfo info, String validator, List<String> converter, String content,
            String strictness, String cardinality, boolean alignment, String spool, boolean streamsSupported,
            String filename) throws DBConnectionException, BiBiToolsException {

        StringBuilder init;
        if (streamsSupported) {
            init = info.getInitStream();
        } else {
            init = info.getInitDownload();
        }

        String pipe;
        if (validator == null) {
            //short case ignore validation and conversion
            pipe = "input_" + info.getInputNumber() + "_download";

        } else { // normal case, create everything

            // create Validation
            String validatorBase = "input_" + info.getInputNumber() + "_validation";
            // create pipe
            init.append("mkfifo ").append(spool).append(validatorBase).append(BR);
            //write command
            init.append("java -jar \"").append(streamExecuteablePath).append("streamValidator.jar\" ")
                    .append(validator).append(" ").append(content)
                    .append(" ").append(strictness)
                    .append(" ").append(cardinality)
                    .append(" ").append(alignment)
                    .append(" ").append(spool).append(validatorBase)
                    .append(" ").append(info.getReturnError())
                    .append(" ").append(spool)
                    .append(" ").append(filename)
                    .append(" < ").append(spool).append("input_").append(info.getInputNumber()).append("_download &").append(BR);
            generateHandler(validatorBase, spool, info.getReturnError(), info, streamsSupported);
            info.incReturnError();

            int j = 0;
            for (String converterImpl : converter) {
                // create Validation
                String converterBase = "input_" + info.getInputNumber() + "_convert_" + j;
                // create pipe
                init.append("mkfifo ").append(spool).append(converterBase).append(BR);
                //write command
                init.append("java -jar \"").append(streamExecuteablePath).append("streamConverter.jar\" ")
                        .append(converterImpl).append(" ").append(content)
                        .append(" ").append(spool).append(converterBase)
                        .append(" ").append(info.getReturnError())
                        .append(" ").append(spool)
                        .append(" ").append(filename)
                        .append(" < ");
                if (j == 0) {
                    init.append(spool).append(validatorBase).append(" &").append(BR);
                } else {
                    init.append(spool).append("input_").append(info.getInputNumber()).append("_convert_").append(j - 1).append(" &").append(BR);
                }

                generateHandler(converterBase, spool, info.getReturnError(), info, streamsSupported);
                info.incReturnError();
                j++;
            }

            if (j == 0) {
                // no converter
                pipe = validatorBase;
            } else {
                // at least one converter
                pipe = "input_" + info.getInputNumber() + "_convert_" + (j - 1);
            }

        }

        if (!streamsSupported) {
            String file = "input_" + info.getInputNumber() + "_file";
            init.append("cat ").append(spool).append(pipe).append(" > ").append(spool).append(file).append(" &").append(BR);
            generateHandler(file, spool, 3, info, streamsSupported);
            pipe = file;
        }

        // set to next input number
        info.incInputNumber();
        return setInputHash(id, inputhash, spool, pipe);

    }

    /**
     *
     * @param id
     * @param inputhash
     * @param spool
     * @param pipe
     * @return
     * @throws DBConnectionException
     * @throws BiBiToolsException
     */
    private String setInputHash(String id, Map<String, String> inputhash, String spool, String pipe) throws DBConnectionException, BiBiToolsException {

        TinputOutput input = search_for_input(id);
        /* Check, how the input should be handled */

        if (input.getHandling().equalsIgnoreCase("FILE")) {
            inputhash.put(input.getId(), (input.isSetOption() ? input.getOption() : "") + spool + pipe);
        } else if (input.getHandling().equalsIgnoreCase("STDIN")) {
            /**
             * write content into a file named "<id>.stdin" and add hash key
             * entry : - key == <id> - value == <option?> return <
             * <spooldir>/<id>.stdin (cmdline part of this input)
             */
            inputhash.put(input.getId(), (input.isSetOption() ? input.getOption() : ""));
            return "<" + spool + pipe;
        } else if (input.getHandling().equalsIgnoreCase("ARGUMENT")) {
            log.fatal("Pipes cannot be used as ARGUMENT ...");
            status.setStatuscode(701, "Pipes cannot be used as ARGUMENT ...");
            throw new BiBiToolsException(status.toString());
        } else if (input.getHandling().equalsIgnoreCase("NONE")) {
            /**
             * add hash key entry : - key = <id> - value = "" NONE :-)
             */
            inputhash.put(input.getId(), "");
        } else {
            log.fatal("Unknown input handling type ...");
            status.setStatuscode(701, "Unknown input handling type ...");
            throw new BiBiToolsException(status.toString());
        }

        return "";
    }

    /**
     * Private helper class that generates to managing code for one process.
     *
     * @param base name of the pipe / basename for variables
     * @param spool spooldirectory to use
     * @param returnErrorCode Code to return id process ended with error
     * @param streamsSupported Does the Tool support input as stream?
     * @param info an object containing the generated execution code
     */
    private void generateHandler(String base, String spool, int returnErrorCode, CmdLineInfo info, boolean streamsSupported) {
        generateHandler(base, spool, returnErrorCode, info, streamsSupported, true);
    }

    /**
     * Private helper class that generates to managing code for one process.
     *
     * @param base name of the pipe / basename for variables
     * @param spool spooldirectory to use
     * @param returnErrorCode Code to return id process ended with error
     * @param info an object containing the generated execution code
     * @param streamsSupported Does the Tool support input as stream?
     * @param remove remove file on cleanup
     */
    private void generateHandler(String base, String spool, int returnErrorCode, CmdLineInfo info, boolean streamsSupported, boolean remove) {

        StringBuilder init;
        StringBuilder header;
        StringBuilder body;
        if (streamsSupported) {
            init = info.getInitStream();
            header = info.getWhileheaderStream();
            body = info.getWhilebodyStream();
        } else {
            init = info.getInitDownload();
            header = info.getWhileheaderDownload();
            body = info.getWhilebodyDownload();
        }

        String file = base;
        base = base.replaceAll("[.]*", "");
        // init pid and boolean
        init.append(base).append("_pid=$!").append(BR);
        init.append(base).append("_bool=false").append(BR).append(BR);

        // cleaning
        info.getClean().append("\tif ! $").append(base).append("_bool").append(BR);
        info.getClean().append("\tthen").append(BR);
        info.getClean().append("\t\tkill $").append(base).append("_pid > /dev/null  2> /dev/null").append(BR);
        info.getClean().append("\tfi").append(BR);

        if (remove) {
            info.getClean().append("\trm ").append(spool).append(file).append(" > /dev/null  2> /dev/null").append(BR).append(BR);
        }

        // while header
        header.append(" || ! $").append(base).append("_bool");

        // while body
        if (returnErrorCode != 4) { // not the tool call
            body.append("\tif ! $").append(base).append("_bool  && ! kill -0 $").append(base).append("_pid > /dev/null  2> /dev/null").append(BR);
            body.append("\tthen").append(BR);
            body.append("\t\t").append(base).append("_bool=true").append(BR);
            body.append("\t\twait $").append(base).append("_pid").append(BR);
            body.append("\t\ta=$?").append(BR);
            body.append("\t\tif [ \"$a\" -eq \"100\" ]").append(BR);
            body.append("\t\tthen").append(BR);
            body.append("\t\t\tbroken_pipe=true").append(BR);
            body.append("\t\tfi").append(BR);

            body.append("\t\tif [ \"$a\" -gt \"0\" ] && [ \"$a\" -ne \"100\" ]").append(BR);
            body.append("\t\tthen").append(BR);
            body.append("\t\t\tclean").append(BR);
            body.append("\t\t\texit ").append(returnErrorCode).append(BR);
            body.append("\t\tfi").append(BR);
            body.append("\tfi").append(BR).append(BR);
        } else { // the tool call
            body.append("\tif ! $").append(base).append("_bool  && ! kill -0 $").append(base).append("_pid > /dev/null  2> /dev/null").append(BR);
            body.append("\tthen").append(BR);
            body.append("\t\t").append(base).append("_bool=true").append(BR);
            body.append("\t\tif ! wait $").append(base).append("_pid").append(BR);
            body.append("\t\tthen").append(BR);
            body.append("\t\t\tclean").append(BR);
            body.append("\t\t\texit ").append(returnErrorCode).append(BR);
            body.append("\t\tfi").append(BR);
            body.append("\tfi").append(BR).append(BR);
        }

    }

    /**
     * Deprecated method, replaced by method 'parseInput' with same
     * functionality.
     *
     * @param id
     * @param inputhash
     * @param inputobject
     * @param type
     * @param implementationType
     * @return
     * @throws BiBiToolsException
     * @throws DBConnectionException
     * @deprecated
     */
    @Deprecated
    public String parseandvalidateInput(String id, Map<String, String> inputhash, Object inputobject, String type, String implementationType) throws BiBiToolsException, DBConnectionException {
        return parseInput(id, inputhash, inputobject, type, implementationType);
    }

    /**
     * The function parseInput validates the input with the given validator
     * object and add a key in the hashmap in the case of success (see list
     * below):
     *
     * <p>
     * <b>input handling :<b> Currently three different kinds of handling inputs
     * are known.
     * <ol>
     * <li><b>FILE ::</b> write content into a file named
     * "<b>id</b>.<b>FILEHANDLE</b>" and add hash entry :
     * <ul>
     * <li> key == id </li>
     * <li> value == <b>option?</b> <b>spooldir</b>/<b>id</b>.<b>FILEHANDLE</b>
     * (cmdline part of this input)</li>
     * <li> return "" (empty string) </li>
     * </ul>
     * </li>
     * <li><b>STDIN ::</b> write content into a file named
     * "<b>id</b>.<b>FILEHANDLE</b>" and add hash entry :
     * <ul>
     * <li> key == id </li>
     * <li> value == <b>option?</b> </li>
     * <li> return "&lt; <b>spooldir</b>/<b>id</b>.<b>FILEHANDLE</b>" (cmdline
     * part of this input)</li>
     * </ul>
     * </li>
     * <li><b>ARGUMENT ::</b> add hash key entry :
     * <ul>
     * <li> key == id </li>
     * <li> value == <b>option?</b> content_of_input </li>
     * <li> return "" (empty string) </li>
     * </ul: </li> </ol>
     * </p>
     *
     * @param id - input id
     * @param inputhash - an initalized (and maybe empty) HashMap
     * @param inputobject
     * @param type
     * @param implementationType
     *
     *
     *
     */
    public String parseInput(String id, Map<String, String> inputhash, Object inputobject, String type, String implementationType) throws BiBiToolsException, DBConnectionException {

        TinputOutput input = search_for_input(id);

        if (input == null) {
            status.setStatuscode(701, "Did not found any input matching id = '" + id + "'!");
            log.error(status);
            throw new BiBiToolsException(status);
        }

        String input_content = null;

        /* Get a string representation of the input object,
         * wich can be saved in a file or .. */
        if (type.equals("PRIMITIVE")) {
            try {
                input_content = Class.forName(implementationType).cast(inputobject).toString();
            } catch (ClassNotFoundException e) {
                status.setStatuscode(700, "Internal Server Error", e.getMessage());
                log.fatal(status);
                throw new BiBiToolsException(status);
            } catch (ClassCastException e) {
                status.setStatuscode(700, "Internal Server Error", e.getMessage());
                log.fatal(status);
                throw new BiBiToolsException(status);
            }
        } else if (type.equals("XML")) {
            try {
                input_content = jaxb2String(inputobject, Class.forName(implementationType));
            } catch (ClassNotFoundException e) {
                status.setStatuscode(700, "Internal Server Error", e.getMessage());
                log.fatal(status);
                throw new BiBiToolsException(status);
            } catch (JAXBException e) {
                status.setStatuscode(700, "Internal Server Error", e.getMessage());
                log.fatal(status);
                throw new BiBiToolsException(status);
            }

        } else if (type.equals("MOBY")) {
            status.setStatuscode(700, "Internal Server Error", "Known input type \"MOBY\" is not yet supported ...");
            log.fatal(status);
            throw new BiBiToolsException(status);

        } else {
            status.setStatuscode(700, "Internal Server Error", "Unknown input type \"" + type + "\"!");
            log.fatal(status);
            throw new BiBiToolsException(status);
        }

        /* Check, how the input should be handled */
        try {
            if (input.getHandling().equalsIgnoreCase("FILE")) {
                /**
                 * write content into a file named "<id>.input" and add hash
                 * entry : - key == id - value == <option?>
                 * <spooldir>/<id>.input (cmdline part of this input)
                 */
                writeSpoolFile(input.getId() + "." + input.getHandling(), input_content.toString());
                inputhash.put(input.getId(), (input.isSetOption() ? input.getOption() : "") + getSpoolDir().toString() + this.separator + input.getId() + "." + input.getHandling());
            } else if (input.getHandling().equalsIgnoreCase("STDIN")) {
                /**
                 * write content into a file named "<id>.stdin" and add hash key
                 * entry : - key == <id>
                 * - value == <option?>
                 * return < <spooldir>/<id>.stdin (cmdline part of this input)
                 */
                writeSpoolFile(input.getId() + "." + input.getHandling(), input_content.toString());
                inputhash.put(input.getId(), (input.isSetOption() ? input.getOption() : ""));
                return "<" + getSpoolDir().toString() + this.separator + input.getId() + "." + input.getHandling();
            } else if (input.getHandling().equalsIgnoreCase("ARGUMENT")) {
                /**
                 * add hash key entry : - key == <id>
                 * - value == <option?> content_of_input
                 */
                inputhash.put(input.getId(), (input.isSetOption() ? input.getOption() : "") + input_content.toString());
            } else if (input.getHandling().equalsIgnoreCase("NONE")) {
                /**
                 * add hash key entry : - key = <id>
                 * - value = "" NONE :-)
                 */
                inputhash.put(input.getId(), "");
            } else {
                log.fatal("Unknown input handling type ...");
                status.setStatuscode(701, "Unknown input handling type ...");
                throw new BiBiToolsException(status.toString());
            }
        } catch (FileNotFoundException e) {
            log.fatal("Content of input '" + input.getId() + "' can not be written into a file (mode :" + input.getHandling() + ").");
            status.setStatuscode(701, "Content of input '" + input.getId() + "' can not be written into a file (mode : " + input.getHandling() + ").");
            throw new BiBiToolsException(status.toString(), e);
        }
        return "";

    }

    /**
     * Generate a cmdline string according to execinfo and
     * paramANDInputOutputOrder description.
     *
     * See getExecCmd to optain the strategy about the executable path to
     * generation.
     *
     * @param id - function id
     * @param paramhash - a hash containing the parameter/ id as key and "real"
     * cmdline as value
     * @param prefix - string that would put at the front the generated string
     * @param postfix - String that would put at the end of the generated string
     * @return string representation of the cmdline call
     */
    public String generateCmdLineString(String id, Map<String, String> hash, String prefix, String postfix) throws BiBiToolsException, DBConnectionException, IdNotFoundException {
        return generateCmdLineString(id, hash, prefix, postfix, true);
    }

    /**
     * Generate a cmdline string according to execinfo and
     * paramANDInputOutputOrder description with switch for check if executable
     * exists or not. Should only used for test purpose!
     *
     * See getExecCmd to optain the strategy about the executable path to
     * generation.
     *
     * @param id - function id
     * @param paramhash - a hash containing the parameter/ id as key and "real"
     * cmdline as value
     * @param prefix - string that would put at the front the generated string
     * @param postfix - String that would put at the end of the generated string
     * @param testexec - Check if executable is available and can be executed
     * @return string representation of the cmdline call
     */
    public String generateCmdLineString(String id, Map<String, String> hash, String prefix, String postfix, boolean testexec) throws BiBiToolsException, DBConnectionException, IdNotFoundException {
        Tfunction function = search_for_function(id);

        if (function == null) {
            status.setStatuscode(701, "Did not found any function matching id = '" + id + "!");
            log.error(status);
            throw new BiBiToolsException(status.toString());
        }

        StringBuffer cmdline = new StringBuffer(getExecCmd());

        // test if cmdline describe a valid executable (only if 'UseDocker' is unset)
        if (getProperty("UseDocker") == null || !getProperty("UseDocker").equalsIgnoreCase("true")) {

            File test = new File(cmdline.toString());
            if (testexec && !(test.exists() && test.isFile() && test.canExecute())) {
                status.setStatuscode(720, "Internal Server Error (Executable)", "Executable '" + cmdline + "' does not exists or is not executable!");
                log.fatal(status.toString());
                throw new BiBiToolsException(status);
            }
        }

        String DELIMITER = " ";


        /* iterate over every  paramAndInputOutputOrder list */
        for (JAXBElement<?> e : function.getParamAndInputOutputOrder().getReferenceOrAdditionalString()) {

            if (e.getValue() instanceof Tparam) {
                String key = ((Tparam) e.getValue()).getId();
                if (hash.containsKey(key)) {
                    cmdline.append(DELIMITER).append(hash.get(key));
                }
            } else if (e.getValue() instanceof TenumParam) {
                String key = ((TenumParam) e.getValue()).getId();
                if (hash.containsKey(key)) {
                    cmdline.append(DELIMITER).append(hash.get(key));
                }
            } else if (e.getValue() instanceof TinputOutput) {
                String key = ((TinputOutput) e.getValue()).getId();
                if (hash.containsKey(key)) {
                    // must be an input type
                    cmdline.append(DELIMITER).append(hash.get(key));
                } else {
                    //can be an output type
                    TinputOutput output = (TinputOutput) function.getOutputref().getRef();
                    if (output.getId().equals(key)) {
                        // use getOutputFile function to get an outputfilename
                        cmdline.append(DELIMITER).append((output.isSetOption() ? output.getOption() : ""));
                        if (output.getHandling().equalsIgnoreCase("STDOUT")) {
                            postfix = postfix + DELIMITER + ">" + getOutputFile(id, true);
                        } else {
                            cmdline.append((getOutputFile(id, true) == null ? "" : getOutputFile(id, true).toString()));
                        }
                    }
                }
            } else if (e.getValue() instanceof String) {
                cmdline.append(DELIMITER).append((String) e.getValue());
            } else {
                status.setStatuscode(701, "Unsupported type '" + e.getValue().getClass().getName() + "' in list of paramAndInputOutputOrder.");
                throw new BiBiToolsException(status.toString()); //@TODO : search for a better error code
            }
        }
        return prefix + DELIMITER + cmdline.toString() + DELIMITER + postfix;
    }

    /**
     * Generates and returns the full generated script;
     *
     * @param id Id of the function
     * @param hash Hash containing input handling
     * @param prefix Prefix of tool command line
     * @param postfix postfix of tool command line
     * @param info Up till now generated script parts for all inputs.
     * @return The full generated script
     * @throws BiBiToolsException
     * @throws DBConnectionException
     */
    public String generateStreamCmdScriptString(String id, Map<String, String> hash,
            String prefix, String postfix, CmdLineInfo info) throws BiBiToolsException, DBConnectionException, IdNotFoundException {

        File file = getOutputFile(id);
        return generateStreamCmdScriptString(id, hash, prefix, postfix, info, file);
    }

    /**
     * Generates and returns the full generated script.;
     *
     * @param id Id of the function
     * @param hash Hash containing input handling
     * @param prefix Prefix of tool command line
     * @param postfix postfix of tool command line
     * @param info Up till now generated script parts for all inputs.
     * @param file the file the result is written to by the tool
     * @return The full generated script
     * @throws BiBiToolsException
     * @throws DBConnectionException
     */
    public String generateStreamCmdScriptString(String id, Map<String, String> hash,
            String prefix, String postfix, CmdLineInfo info, File file) throws BiBiToolsException, DBConnectionException, IdNotFoundException {

        // get execution data
        String cmdLine = generateCmdLineString(id, hash, prefix, postfix);
        generateStreamToolExecution(info, file, cmdLine);

        return generateStreamCmdScriptString(info);
    }

    public String generateStreamCmdScriptString(String cmdLine, CmdLineInfo info, File file) throws BiBiToolsException, DBConnectionException {

        generateStreamToolExecution(info, file, cmdLine);

        return generateStreamCmdScriptString(info);
    }

    /**
     * Helper class to generate to tool execution.
     *
     * @throws BiBiToolsException
     * @throws DBConnectionException
     */
    private void generateStreamToolExecution(CmdLineInfo info, File file, String cmdLine) throws BiBiToolsException, DBConnectionException {

        // download command
        info.getInitStream().append(cmdLine).append(" &").append(BR);
        // management
        generateHandler(file.getName(), file.getParent(), 4, info, true, false);
    }

    /**
     * Generates to actual script from info.
     *
     * #clean up clean(){ [clean] }
     *
     * [initDownload]
     *
     * while false [whileheaderDownload] do [whilebodyDownload] sleep 1 done
     *
     * [initStream]
     *
     * while false [whileheaderStream] do [whilebodyStream] sleep 1 done
     *
     * [afterBody]
     *
     * clean true || exit set stuff
     *
     * @param info containing data of script
     * @return
     */
    private String generateStreamCmdScriptString(CmdLineInfo info) {

        StringBuilder script = new StringBuilder();
        script.append("clean(){").append(BR);
        script.append(info.getClean());
        script.append("}").append(BR).append(BR);

        script.append("broken_pipe=false").append(BR).append(BR);

        script.append(info.getInitDownload());

        script.append("while ").append(info.getWhileheaderDownload()).append(BR);
        script.append("do ").append(BR);
        script.append(info.getWhilebodyDownload());
        script.append("\tsleep 1").append(BR);
        script.append("done").append(BR).append(BR);

        script.append("if $broken_pipe").append(BR);
        script.append("then").append(BR);
        script.append("\texit 2").append(BR);
        script.append("fi").append(BR).append(BR);

        script.append(info.getInitStream());

        script.append("while ").append(info.getWhileheaderStream()).append(BR);
        script.append("do ").append(BR);
        script.append(info.getWhilebodyStream());
        script.append("\tsleep 1").append(BR);
        script.append("done").append(BR).append(BR);

        script.append("if $broken_pipe").append(BR);
        script.append("then").append(BR);
        script.append("\texit 2").append(BR);
        script.append("fi").append(BR).append(BR);

        script.append(info.getAfterBody());

        script.append("clean").append(BR);
        script.append("true");

        return script.toString();
    }

    /**
     * Return a list filenames (as File) belonging to every input of function
     * with given id if the handling equals "FILE" or "STDIN". In the case of
     * handling as "ARGUMENT" a NULL object will be returned.
     *
     * @param id - the function id
     * @param absolute - determine if the return list of File objects describes
     * an absolute or relative path
     *
     * @return Returns a list of input filenames
     * @throws de.unibi.techfak.bibiserv.exception.BiBiToolsException
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     */
    public List<File> getInputFileList(String id, boolean absolute) throws BiBiToolsException, DBConnectionException, IdNotFoundException {
        List<File> list_of_files = new ArrayList<File>();
        Tfunction function = search_for_function(id);
        if (function == null) {
            status.setStatuscode(701, "Did not found any function matching id = '" + id + "!");
            log.error(status);
            throw new BiBiToolsException(status.toString());
        }
        for (Tfunction.Inputref ref : function.getInputref()) {
            TinputOutput input = (TinputOutput) ref.getRef();
            if (input.getHandling().equals("FILE") || input.getHandling().equals("STDIN")) {
                try {
                    list_of_files.add(new File((absolute ? getSpoolDir() : new File("")), input.getId() + "." + input.getHandling()));
                } catch (FileNotFoundException e) {
                    status.setStatuscode(720, "Internal Resource Error", "Can't access spooldir.");
                    throw new BiBiToolsException(status.getInternalDescription(), e);
                }
            } else {
                list_of_files.add(null); // Mmm, if this is a good idea ???
            }
        }

        return list_of_files;
    }

    /**
     * Returns the filename(as File belonging to output of the function with
     * given id, relative to current spooldir.
     *
     * @see getOutputFile(String id, boolean absolute)
     *
     * @param id - the function id
     * @return Returns output filename reltive to current spooldir as File
     * object
     * @throws de.unibi.techfak.bibiserv.exception.BiBiToolsException
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     */
    public File getOutputFile(String id) throws BiBiToolsException, DBConnectionException, IdNotFoundException {
        return getOutputFile(id, false);
    }

    /**
     * Return the filename (as File) belonging to the output of function with
     * given id if the handling equals "FILE" or "STDOUT". In every other case
     * the filename depends on the executable itself and can't generated
     * automatically, a NULL object will be returned.
     *
     * @param id - the function id
     * @param absolute - determine if the return File object return an absolute
     * path or relative to the spooldir
     * @return Returns output filename as File object.
     *
     * @throws de.unibi.techfak.bibiserv.exception.BiBiToolsException
     * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
     */
    public File getOutputFile(String id, boolean absolute) throws BiBiToolsException, DBConnectionException, IdNotFoundException {
        Tfunction function = search_for_function(id);
        if (function == null) {
            status.setStatuscode(701, "Did not found any function matching id = '" + id + "!");
            log.error(status);
            throw new BiBiToolsException(status.toString());
        }
        TinputOutput output = ((TinputOutput) function.getOutputref().getRef());
        if (output.getHandling().equalsIgnoreCase("FILE") || output.getHandling().equalsIgnoreCase("STDOUT")) {
            try {
                return new File((absolute ? getSpoolDir() : new File("")), output.getId() + "." + output.getHandling());
            } catch (FileNotFoundException e) {
                status.setStatuscode(720, "Internal Resource Error", "Can't access spooldir.");
                throw new BiBiToolsException(status.getInternalDescription(), e);
            }
        }
        status.setStatuscode(701, "Invalid output type declared!");
        log.error(status);
        throw new BiBiToolsException(status.toString());
    }

    /**
     * Returns current user object
     */
    public User getUser() {
        return user;
    }

    ///////////////////////////// finalize WS Call //////////////////////////////////////////////////
    /**
     * finalize one WS call, which includes: -tidy up TmpDir -chmod all spool
     * data, so we can access (clean) later
     */
    @Override
    public void finalize() throws Throwable {

        //clean up recursivly tmp dir
        if (tmpDir != null) {
            tmpDir.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        pathname.listFiles(this);
                    }
                    if (!pathname.delete()) {
                        log.error("Deletion of '" + pathname + "' failed.");
                    }
                    return false;
                }
            });
        }
        //chmod recursivly all directories within the tools spool directory (if they exists ...)
        if (spoolDir != null) {
            spoolDir.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        pathname.listFiles(this);
                        if (!chmodDir(pathname)) {
                            log.error("Chmod of '" + pathname + "' failed.");
                        }
                    }
                    return false;
                }
            });
        }
        super.finalize();
    }
    //////////////////////////
    // public static methods
    //////////////////////////

    /**
     * Return a reference to BiBiTools properties
     *
     * @return Return a refernce to static BiBiTools properties
     *
     */
    public static Properties getProperties() {
        if (properties == null) {
            try {
                loadBiBiProperties();
            } catch (BiBiToolsException e) {
                log.fatal("Fatal error occurred whil initializing BiBiTools properties.\n" + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return properties;
    }

    /**
     * Return datasource used by this BiBiTools class.
     *
     * If (BiBiTool Property ('useDebugDataSource') is set to true, then a
     * DebugDataSource is used instead of a normal one.
     *
     * @see DDataSource
     *
     * @return
     */
    public static DataSource getDataSource() throws DBConnectionException {
        if (BiBiTools.datasource == null) {
            try {
                Context ctx = new InitialContext();
                if (ctx == null) {
                    log.error("context is null");
                    throw new DBConnectionException();
                }
                // @todo: Hardcoded DataSource !!!
                if (Boolean.parseBoolean(getProperties().getProperty("useDebugDataSource", "false"))) {
                    BiBiTools.datasource = new DDataSource((DataSource) ctx.lookup("jdbc/bibiserv2"));
                } else {
                    BiBiTools.datasource = (DataSource) ctx.lookup("jdbc/bibiserv2");
                }
            } catch (NamingException ex) {
                log.fatal("An NamingException occurred : " + ex.getMessage());
                throw new DBConnectionException();
            }
        }
        return BiBiTools.datasource;
    }

    /**
     * Set the datasource used by this object.
     *
     * @param datasource
     */
    public static void setDataSource(DataSource datasource) {
        BiBiTools.datasource = datasource;
    }

    /**
     * public helper method ; converts an Inputstream to a String
     *
     * @param in
     * @return String represention of Readers content stream
     * @throws IOException
     *
     */
    public static String i2s(Reader in) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[4096];
        int c_r;
        while ((c_r = in.read(buf)) != -1) {
            sb.append(buf, 0, c_r);
        }
        return sb.toString();
    }

    /**
     * Returns an unspecified SOAPFaultException.
     *
     * @return Returns an unspecified SOAPFaultException.
     */
    public static SOAPFaultException createSOAPFaultException() {
        return createSOAPFaultException("server", "An unspecified SOAPFaultException occurred!");
    }

    /**
     * Returns a SOAPFaultException according the wsstools status object.
     *
     * @param status - status object as information base for SOAPFaultException.
     * @return Returns a SOAPFaultException according the wsstools status
     * object.
     */
    public static SOAPFaultException createSOAPFaultException(Status status) throws DBConnectionException, IdNotFoundException {
        return createSOAPFaultException("server", status.getStatuscode() + " : " + status.getDescription(), Integer.toString(status.getStatuscode()), status.getDescription());
    }

    /**
     * Returns a SOAPFaultException according a BiBiToolsException.
     *
     * @param e - BiBiToolsException
     * @return Return a BiBiToolsException accordinf a BiBiToolsException
     */
    public static SOAPFaultException createSOAPFaultException(BiBiToolsException e) {
        return createSOAPFaultException(String.valueOf(e.returnFaultCode()), e.returnFaultString());
    }

    /**
     * Returns a SOAPFaultException with specified faultcode and faultstring.
     *
     * @param faultcode
     * @param faultstring
     * @return Returns a SOAPFaultException with specified faultcode and
     * faultstring.
     */
    public static SOAPFaultException createSOAPFaultException(String faultcode, String faultstring) {
        return createSOAPFaultException(faultcode, faultstring, null, null);
    }

    /**
     * Returns a SOAPFaultException with specified
     *
     * @param faultcode
     * @param faultstring
     * @param hobitstatuscode
     * @param hobitstatusdescription
     * @return
     */
    public static SOAPFaultException createSOAPFaultException(String faultcode, String faultstring, String hobitstatuscode, String hobitstatusdescription) {
        SOAPFault fault = null;
        try {
            SOAPFactory sfi = SOAPFactory.newInstance();
            fault = sfi.createFault();

            fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", faultcode, "soap"));
            fault.setFaultString(faultstring);
            if (hobitstatuscode != null && hobitstatusdescription != null) {
                Detail detail = fault.addDetail();
                DetailEntry detailentry = detail.addDetailEntry(new QName("http://hobit.sourceforge.net/xsds/hobitStatuscode.xsd", "hobitStatuscode", "status"));

                SOAPElement statuscode = detailentry.addChildElement(new QName("http://hobit.sourceforge.net/xsds/hobitStatuscode.xsd", "statuscode", "status"));
                statuscode.addTextNode(hobitstatuscode);

                SOAPElement description = detailentry.addChildElement(new QName("http://hobit.sourceforge.net/xsds/hobitStatuscode.xsd", "description", "status"));
                description.addTextNode(hobitstatusdescription);
            }

        } catch (SOAPException e) {
            log.fatal("SOAPException occured : " + e.getMessage());
        }

        return new SOAPFaultException(fault);

    }
    /////////////////////////
    // private methods
    ////////////////////////

    /**
     * chmodDir() sets perms of a dir as defined in bibiprops.
     *
     * @return boolean true if successfully exec of chmod is succesful, doesnt
     * matter if changed or not, false on failure
     */
    private boolean chmodDir(File dir) {
        Runtime runtime = Runtime.getRuntime();
        try {
            String chmodCmd = properties.getProperty("chmod.bin") + " " + properties.getProperty("chmod.param") + " " + dir.toString();
            Process process = runtime.exec(chmodCmd);
            if (process.waitFor() != 0) {
                log.error("could not chmod '" + chmodCmd + "'!\n " + i2s(new InputStreamReader(process.getErrorStream())));

                process.getErrorStream().close();
                return false;
            }

            log.debug("chmod for dir '" + dir.toString() + "' done: " + dir.toString());
        } catch (InterruptedException e) {
            log.error("could not chmod dir '" + dir.toString() + "' : " + e.getMessage());
            return false;
        } catch (IOException e) {
            log.error("could not chmod dir '" + dir.toString() + "' : " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * private helper method, which chmodDir on each subdir, which is a suffix
     * of base and dir.
     *
     * @param base - Basedir - must be a prefix of dir
     * @param dir - dir(s) which access should be change
     * @return true in case of success, false otherwise;
     */
    private boolean chmodDirs(File base, File dir) {
        // get suffix of dir/base
        String base_s = base.toString();
        String dir_s = dir.toString();
        if (dir_s.startsWith(base_s)) {
            String rel_s = dir_s.substring(base_s.length());
            StringBuilder dirpath = new StringBuilder();

            // split die into subdir and call chmodDir on each subdir
            for (String t : rel_s.split(separator)) {
                dirpath.append(separator);
                dirpath.append(t);
                if (!chmodDir(new File(base, dirpath.toString()))) {
                    return false;
                }
            }

        } else {
            log.error("\"base\" must be prefix of \"dir\"");
            return false;
        }
        return true;
    }

    /**
     * private helper method; generate a relative spooldir path from a status
     * object. in the following manner :
     *
     * dd/kk/mm/<bibiserv_id>, where "dd" is the day in month, "kk" the hour in
     * day and "mm" the minute in hour.
     *
     * @return String representation of a relative spooldir corresponding to
     * current status object.
     */
    private String getSpecificSpoolDir() throws DBConnectionException, IdNotFoundException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/kk/mm");
        return sdf.format(status.getCreatedDate()) + "/" + status.getId();
    }

    /**
     * private helper method; converts a DomElement to a String
     *
     * @param domelement
     * @return String representation of the dom element
     * @throws javax.xml.transform.TransformerConfigurationException
     * @throws javax.xml.transform.TransformerException
     */
    private String d2s(Element domelement) throws TransformerConfigurationException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        StringWriter sw = new StringWriter();
        trans.transform(new DOMSource(domelement), new StreamResult(sw));
        return sw.toString();
    }

    /**
     * Generics private helper method that search a given list of param and
     * enumparam
     *
     * @param Class, can be Tparam or TenumParam
     * @param name of parameter
     * @param List of param/enum parameter
     *
     * @return found parameter or null
     */
    private Object search_for(Class c, String name, List list) {
        try {
            for (Object o : list) {
                if ((o.getClass().equals(c)) && (((String) c.getMethod("getId").invoke(o, new Object[]{})).equals(name))) {
                    return o;
                }
            }
        } catch (Exception e) {
            log.error("Exception occurred while calling 'search_for'. Message was " + e.getMessage());
        }
        return null;
    }

    /**
     * private helper method; Search the runnableitem(tool) description for any
     * function matching the id.
     *
     * @param id - id of the searched function
     * @return a Tfunction
     *
     *
     */
    private Tfunction search_for_function(String id) {
        List<Tfunction> list_of_function = runnableitem.getExecutable().getFunction();
        for (Tfunction function : list_of_function) {
            if (function.getId().equals(id)) {
                return function;
            }
        }
        return null;

    }

    /**
     * private helper method; Search the runnableitem(tool) description for any
     * input matching the id.
     *
     * @param id - id of the searched input
     * @return a TinputOutput
     */
    private TinputOutput search_for_input(String id) {
        List<TinputOutput> list_of_inputs = runnableitem.getExecutable().getInput();
        for (TinputOutput input : list_of_inputs) {
            if (input.getId().equals(id)) {
                return input;
            }
        }
        return null;
    }

    /**
     * private helper method that return a String representation of element
     * execinfo.
     *
     * Changes (2009/09/01) :
     *
     * Uses bibiserv.properties 'executable.rootpath' [1] and
     * 'executable.path.isrelativ' [2] and runnable.executable.execinfo.Path [3]
     * and runnable.executable.execinfo.CallingInformation [4] to generate a
     * path to executable in the following manner :
     *
     * <table>
     * <tr><td>
     * [1] given <b>and</b> [2] set (eq 'true')
     * </td><td>
     * [1] + this.separator + [3] + this.separator + [4]
     * </td></tr>
     *
     * <tr><td>
     * [1] given <b> and </b> [2] not set (ne 'true') or not given
     * </td><td>
     * [1] + this.separator + [4]
     * </td></tr>
     *
     * <tr><td>
     * [1] not given
     * </td><td>
     * [3] + this.separator + [4]
     * </td></tr>
     * </table>
     *
     * @return Returns a String containing the path to the executable
     */
    private String getExecCmd() {
        StringBuffer cmdbuf = new StringBuffer();
        if (getProperty("UseDocker") == null || !getProperty("UseDocker").equalsIgnoreCase("true")) {
            if (getProperty("executable.rootpath") != null) {
                cmdbuf.append(getProperty("executable.rootpath"));
                if (!endWithFileSeparator(cmdbuf)) {
                    cmdbuf.append(this.separator);
                }
                if (getProperty("executable.path.isrelativ").equalsIgnoreCase("true")) {
                    cmdbuf.append(runnableitem.getExecutable().getExecInfo().getPath());
                    if (!endWithFileSeparator(cmdbuf)) {
                        cmdbuf.append(this.separator);
                    }
                }
            } else {
                cmdbuf.append(runnableitem.getExecutable().getExecInfo().getPath());
                if (!endWithFileSeparator(cmdbuf)) {
                    cmdbuf.append(this.separator);
                }
            }
        } else {
            String orga = getProperty("DockerHubOrganization");
            if (orga != null) {
                cmdbuf.append("docker run ");
                cmdbuf.append(" --rm=true");
                cmdbuf.append(" -v ").append(getProperty("spooldir.base")).append(":").append(getProperty("spooldir.base"));
                cmdbuf.append(" ").append(orga).append("/").append(runnableitem.getId());
                cmdbuf.append(" /usr/local/bin/");
            } else {
                cmdbuf.append("# BiBiTool.properties doesn't contain a property named 'DockerHubOrganization'!");
            }
        }
        cmdbuf.append(runnableitem.getExecutable().getExecInfo().getCallingInformation());
        return cmdbuf.toString();
    }

    /**
     * private helper method that check if teh StringBUffer end with and file
     * separator
     *
     * @param StringBuffer to be checked
     * @return True if param end with an file separator.
     */
    private boolean endWithFileSeparator(StringBuffer buf) {
        return (buf != null)
                && (buf.length() - separator.length() > 0)
                && (buf.substring(buf.length() - this.separator.length()).equals(this.separator));
    }

    /**
     * publiv static helper method that search and load (if found) bibiserv
     * properties.
     *
     * - Used by getProperty to load static properties variable with content,
     * during 1st time call. - Could be used to "reload" Properties content
     * during runtime (e.g. for reconfiguration).
     *
     * @return a Properties object
     *
     */
    public static void loadBiBiProperties() throws BiBiToolsException {
        properties = new Properties();
        InputStream rin = null;

        /* first check if a System Property bibiserv2.property.location is set */
        log.info("Check for property 'bibiserv2.property.location'.");
        if (System.getProperty("de.unibi.techfak.bibiserv.config") != null) {
            try {
                rin = new FileInputStream(System.getProperty("de.unibi.techfak.bibiserv.config"));
            } catch (FileNotFoundException e) {
                // do nothing, in the case this exception occurs try second possibility
                log.warn("Property 'de.unibi.techfak.bibiserv.config' is set, but property value doesn't point to a xml configuration.");
            }
        }

        /* second, check if bibiserv property is located in domain root folder ${catalina.home}*/
        if (rin == null) {
            log.info("Check for ${catalina.home}/bibiserv_properties.xml");
            if (System.getProperty("catalina.home") != null) {
                try {
                    rin = new FileInputStream(System.getProperty("catalina.home") + "/bibiserv_properties.xml");
                } catch (FileNotFoundException e) {
                    // do nothing, in the case this exception occure try third possibility
                }
            }
        }

        /* third, check for bibiserv property in classpath (load as resource) */
        if (rin == null) {
            log.info("Check for bibiserv.properties in classpath!");
            rin = ClassLoader.getSystemResourceAsStream("bibiserv_properties.xml");
        }

        /* load properties from Inputstream */
        if (rin != null) {
            try {
                properties.loadFromXML(rin);
            } catch (IOException e) {
                log.fatal("Can't read BiBiServ Properties file!", e);
                throw new BiBiToolsException("Can't read BiBiServ Properties file!", e);
            }
        } else {
            throw new BiBiToolsException("BiBiServ Properties file not found!\n"
                    + "1) set Java system property 'de.unibi.techfak.bibiserv.config'\n"
                    + "2) place bibiserv_properties.xml in ${catalina.home} base folder\n"
                    + "3) place bibiserv_properties.xml in Java classpath!");
        }

        // add Hostname of localhost to properties
        try {
            InetAddress addr = InetAddress.getLocalHost();
            properties.setProperty("hostname", addr.getHostName());
            log.info("Hostname is " + addr.getHostName() + " !");
        } catch (UnknownHostException e) {
            log.fatal("Fatal error occurred when detecting hostname of local machine\n" + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    /**
     * private helper method that fills a given list with param and enum_params
     * referenced by this parameter group. Called recursivly if given paramGroup
     * contains another parameter group.
     *
     * @param tpg - ParameterGroup to work on
     * @param l_param_enum - List, enum and parameter referenced by parameter
     * group to be added.
     */
    private void getParamAndEnumListfromParamGroup(TparamGroup tpg, List<Object> l_param_enum) {
        if (tpg.isSetParamrefOrParamGroupref()) {
            for (Object o : tpg.getParamrefOrParamGroupref()) {
                if (o.getClass().equals(TparamGroup.ParamGroupref.class)) {
                    getParamAndEnumListfromParamGroup((TparamGroup) ((TparamGroup.ParamGroupref) o).getRef(), l_param_enum);
                } else if (o.getClass().equals(TparamGroup.Paramref.class)) {
                    l_param_enum.add(((TparamGroup.Paramref) o).getRef());
                }
            }
        }
    }

    /**
     * private helper method return the value for a key from enum parameter.
     *
     * @param list of type TenumValue for an enum parameter
     * @param key key to search.
     * @return Return the value belonging to the given key or Null in the case
     * of invalid key.
     */
    private String getValueforKeyfromEnumParam(List<TenumValue> list, String key) {
        for (TenumValue t : list) {
            if (t.isSetKey() && t.getKey().equals(key)) {
                return t.getValue();
            }
        }
        return null;
    }

    /**
     *
     * @param list
     * @return
     */
    private static String EnumParamList2String(List<TenumValue> list) {
        StringBuilder sb = new StringBuilder();
        for (TenumValue t : list) {
            sb.append(t.getKey()).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}
