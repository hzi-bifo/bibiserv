/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2015 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s): Jan Krueger 
 *
 */
package de.unibi.cebitec.bibiserv.web.controller;

import com.sun.java.jnlp.AllPermissions;
import com.sun.java.jnlp.ApplicationDesc;
import com.sun.java.jnlp.Description;
import com.sun.java.jnlp.Information;
import com.sun.java.jnlp.Jar;
import com.sun.java.jnlp.Jnlp;
import com.sun.java.jnlp.J2Se;
import com.sun.java.jnlp.Resources;
import com.sun.java.jnlp.Security;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import de.unibi.cebitec.bibiserv.web.xmlreader.BiBiServXMLReader;
import de.unibi.cebitec.bibiserv.web.xmlreader.ItemContent;
import de.unibi.cebitec.bibiserv.web.xmlreader.LinkedItemContent;
import de.unibi.cebitec.bibiserv.web.xmlreader.RunnableItemContent;
import de.unibi.techfak.bibiserv.BiBiTools;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.ViewExpiredException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * RequestFilter class is first instance that gets called after http-request to
 * BiBiServ2 (look at request-filter in web.xml). The doFilter Method is
 * invoked. The Method lets pass thru some requests to specific Patterns like
 * /xmlhttp/, or /block...
 * 
 * 
 * <b>Attention: ToolRequestFilter is a Singleton (see ApplicationContext) and therfore
 *    object variables should be used with care, they are shared between all requests. </b>
 * 
 *
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
 * @author Sven Hartmeier - shartmei(at)cebitec.uni-bielefeld.de
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * @author Armin Toepfer - atoepfer(at)cebitec.uni-bielefeld.de
 */
public final class ToolRequestFilter implements Filter, ApplicationContextAware {

    /* Logger */
    private static final Logger log = Logger.getLogger("de.unibi.cebitec.bibiserv.web.controller");
    
    /* ApplicationContext, seems to be save to use Objectvariable here */
    private ApplicationContext context;
   

    @Inject
    @Named("bibiservXmlReader")
    private BiBiServXMLReader xmlrequest;
    @Inject
    private RequestIdentifier requestIdentifier;


    // get viewer properties;
    private static final Properties viewerproperties;

    static {
        viewerproperties = new Properties();
        try {
            viewerproperties.load(ToolRequestFilter.class.getResourceAsStream("/viewer.properties"));
        } catch (IOException e) {
            log.fatal(e.getMessage(), e);
        }

    }

    /**
     * doFilter method of RequestFilter implements the required method of Filer
     * interface Filters and organizes requests to different URLs
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {

            //set character encoding
            request.setCharacterEncoding("UTF-8");

            // get the current MV-Selector from the spring context
            ModelAndViewSelector mvSelector = (ModelAndViewSelector) context.getBean("modelAndViewSelectorBean");

            HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
            HttpServletResponse httpServletResponse = ((HttpServletResponse) response);
            String url = httpServletRequest.getRequestURI();
            
            String id = "";

            String[] urlList = url.split("/");
            if (urlList.length > 0) {
                id = urlList[1]; // extract tool id from URL, should be FIRST element after / otherwise leave it blank
            } 

            HashMap<String,ItemContent> contentHash = xmlrequest.getItemContentHash();

            // handle direct spool requests
            if (id.equals("spool")) {
                handle_spool_request(httpServletRequest,httpServletResponse);
                return;
            }

            // handle jnlp (Java WebStart) requests
            if (id.equals("jnlp")) {
                handle_jnlp_request(httpServletRequest,httpServletResponse,contentHash);
                return;
            }

            // handle proxy requests  (= Linked Item)
            if (contentHash.containsKey(id) && (contentHash.get(id) != null) && contentHash.get(id) instanceof LinkedItemContent) {
                LinkedItemContent lic = (LinkedItemContent) contentHash.get(id);
                if (lic.isLocal()) {
                    request.getRequestDispatcher(lic.getURL()).forward(request,response);
                } else {    
                    httpServletResponse.sendRedirect(lic.getURL());                  
                    
                }
                return;
            }

            /*
             * handle of non-proxyable requests
             */
            if (requestIdentifier.isHit(url)) {
                /* if clauses catch specified requests and either send
                 them to other instances of FilterChain.. */
                try {
                    filterChain.doFilter(request, response);
                } catch (ViewExpiredException e) {
                    log.error("[remote "+request.getRemoteAddr()+"]"+e.getMessage());
                    mvSelector.setViewName("/error.jsf", "View expired :" + e.getMessage());
                    ((HttpServletRequest) request).getRequestDispatcher(mvSelector.getViewName()).forward(request, response);
                    
                } catch (IOException | ServletException e) {
                    log.error("[remote "+request.getRemoteAddr()+"]"+e.getMessage());
                    mvSelector.setViewName("/error.jsf", "Servlet exception occured :" + e.getMessage());
                    ((HttpServletRequest) request).getRequestDispatcher(mvSelector.getViewName()).forward(request, response);
                }
                return;
            }

            // handle installed apps (items and runnable items)
            try {
                mvSelector.selectModelAndView(httpServletRequest);
                ((HttpServletRequest) request).getRequestDispatcher(mvSelector.getViewName()).forward(request, response);
            } catch (IOException | ServletException ex) {
                mvSelector.setViewName("/error.jsf", "Page not found 404 :" + ex);
                ((HttpServletRequest) request).getRequestDispatcher(mvSelector.getViewName()).forward(request, response);
            }
            return;

        }
        throw new UnsupportedOperationException("Error in ToolRequestFilter, the server is unable to forward your request!");
    }

    /**
     * Required method of Filter interface
     *
     * @param arg0 FilterConfig
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    /**
     * Required Method for implementation of FILTER Interface
     */
    @Override
    public void destroy() {
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public void setXmlrequest(BiBiServXMLReader xmlrequest) {
        this.xmlrequest = xmlrequest;
    }

    public void setRequestIdentifier(RequestIdentifier requestIdentifier) {
        this.requestIdentifier = requestIdentifier;
    }

    /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

    /**
     * Check if access to spool param doesn't contains any 'bad' characters ...
     *
     *
     * @param id
     * @param name
     * @param tool
     * @return
     * @throws IOException
     */
    private String checkspoolparam(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String id, String name, String tool) throws IOException {

        if (name == null) {
            httpServletResponse.sendError(404, "'name' has a NULL value!");
            return null;
        }

        if (name.matches("//")) {
            httpServletResponse.sendError(404, "'name' contains an invalid character!");
            return null;
        }

        // check tool parameter
        if (tool == null) {
            httpServletResponse.sendError(404, "'tool' has a NULL value!");
            return null;
        }

        if (tool.matches("//")) {
            httpServletResponse.sendError(404, "'tool' contains an invalid character!");
            return null;
        }

        if (id == null) {
            httpServletResponse.sendError(404, "'id' has a NULL value!");
            return null;
        }
        Pattern p = Pattern.compile(".+?_\\d\\d\\d\\d-\\d\\d-(\\d\\d)_(\\d\\d)(\\d\\d)\\d\\d_.+");
        Matcher m = p.matcher(id);
        if (!m.matches() || m.groupCount() != 3) {
            httpServletResponse.sendError(404, id + " is an invalid 'id'!");
            return null;
        }
        return m.group(1) + File.separator + m.group(2) + File.separator + m.group(3) + File.separator;

    }

    /**
     *
     * private method for handling direct spool dir request
     *
     * supports the following http parameter - id - name - tool - user* -
     * contenttype*
     *
     * parameters are marked * are optional
     *
     * @throws IOException
     */
    private void handle_spool_request(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String id = httpServletRequest.getParameter("id");
        String name = httpServletRequest.getParameter("name");
        String tool = httpServletRequest.getParameter("tool");
        String user = httpServletRequest.getParameter("user");
        String contenttype = httpServletRequest.getParameter("contenttype");

        String idspool = checkspoolparam(httpServletRequest,httpServletResponse,id, name, tool);

        // idspool an error is occurred .. abbort
        if (idspool == null) {
            return;
        }

        // check for user 
        if (user == null) {
            user = "anonymous";
        }

        // build absolute filename 
        File fn = new File(BiBiTools.getProperties().getProperty("spooldir.base") + File.separator + user + File.separator + tool.toLowerCase() + File.separator + idspool + File.separator + id + File.separator + name);

        if (!(fn.isFile() && fn.canRead())) {

            httpServletResponse.sendError(404, "File '" + name + "' doesn't exists or can't be read!");
            return;
        }

        // set content type
        if (contenttype == null) {
            httpServletResponse.setContentType("text/plain");
        } else {
            httpServletResponse.setContentType(contenttype);
        }

        // read content from file and write to httpServletResponse
        FileInputStream fin = new FileInputStream(fn);

        BufferedInputStream bin = new BufferedInputStream(fin);

        BufferedOutputStream bout = new BufferedOutputStream(httpServletResponse.getOutputStream());

        byte[] buf = new byte[4096];
        int l;

        while ((l = bin.read(buf)) != -1) {
            bout.write(buf, 0, l);
        }
        bin.close();
        bout.close();

    }

    /**
     *
     * private method for handling jnlp requests : currently two different ways
     * are supported :
     *
     * 1) return a (modified) jnlp file from a runableitem - appid - wsid
     *
     * 2) return a viewer jnlp file using the following parameter - viewerid -
     * viwerarg* - viewerurl or - result_id - result_name - result_tool -
     * result_user* - result_contenttype* parameter marked * are optional
     * parameter
     *
     * Viewerid is the unique id of viewer which must be configured in
     * Web-Inf/classes/viewer.properties. See Properties files comments for more
     * detail.
     *
     * A (valid) viewerurl or combination of a result_id, result_name and
     * result_tool must be given as argument. In later case a viewerurl is build
     * like following pattern :
     *
     * PROTOCOL://HOSTNAME:PORT/spool?id=RESULT_ID&name=RESULT_NAME&tool=RESULT_TOOL[&contenttype=RESULT_CONTENTTYPE][&user=RESULT_USER]
     *
     */
    private void handle_jnlp_request(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,HashMap<String,ItemContent> contentHash) throws IOException {
        // get all possible values (coded as param) 

        String appid = httpServletRequest.getParameter("appid");
        String wsid = httpServletRequest.getParameter("wsid");

        String viewerid = httpServletRequest.getParameter("viewerid");
        String viewerurl = httpServletRequest.getParameter("viewerurl");
        String viewerarg = httpServletRequest.getParameter("viewerarg");
        String result_id = httpServletRequest.getParameter("resultid");
        String result_contenttype = httpServletRequest.getParameter("resultcontenttype");
        String result_name = httpServletRequest.getParameter("resultname");
        String result_user = httpServletRequest.getParameter("resultuser");

        
        /*
         * jnlp file for app webstart application
         * 
         * constraints : app and wsid id are given (!= null) and app is installed
         * */
        
        
        if (appid != null && contentHash.containsKey(appid)
                && wsid != null && contentHash.get(appid) instanceof RunnableItemContent
                && ((RunnableItemContent) contentHash.get(appid)).getWebstart().containsKey(wsid)) {

            // get itemcontent
            RunnableItemContent ic = ((RunnableItemContent) contentHash.get(appid));

            // get jnlp element
            Element jnlp = ic.getWebstart().get(wsid);

            // change codebase
            jnlp.setAttribute("codebase", (httpServletRequest.isSecure() ? "https://" : "http://") + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort());

            // change href
            jnlp.setAttribute("href", "/jnlp?appid=" + appid + "&wsid=" + wsid);

            // get document
            Document doc = ic.getDoc();

            // Set up the output transformer
            String xmlString = "";
            try {
                TransformerFactory transfac = TransformerFactory.newInstance();
                Transformer trans;

                trans = transfac.newTransformer(new StreamSource(getClass().getResourceAsStream("/removeNamespace.xsl")));

                trans.setOutputProperty(OutputKeys.INDENT, "yes");

                // Print the DOM node
                StringWriter sw = new StringWriter();
                StreamResult result = new StreamResult(sw);
                DOMSource source = new DOMSource(jnlp);
                trans.transform(source, result);
                xmlString = sw.toString();
            } catch (TransformerConfigurationException ex) {
                log.fatal(ex.toString(), ex);
            } catch (TransformerException ex) {
                log.fatal(ex.toString(), ex);
            }
            // set content type          
            httpServletResponse.setContentType("application/x-java-jnlp-file");
            httpServletResponse.setHeader("Content-Disposition", "inline; filename=\"" + appid + "_" + wsid + ".jnlp");

            // replace server and resources and return it
            PrintWriter out = httpServletResponse.getWriter();

            out.println(xmlString);
        }

        // 
        if (viewerid != null && (viewerurl != null || (result_id != null && result_name != null && viewerproperties.containsKey(viewerid)))
                && viewerproperties.containsKey(viewerid + ".jar")
                && viewerproperties.containsKey(viewerid + ".title")
                && viewerproperties.containsKey(viewerid + ".mainclass")) {
            try {

                if (viewerurl == null && checkspoolparam(httpServletRequest, httpServletResponse, result_id, result_name, viewerproperties.getProperty(viewerid)) == null) {
                    return;
                }

                String codebase = (httpServletRequest.isSecure() ? "https://" : "http://") + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort();

                Jnlp jnlp = new Jnlp();

                jnlp.setCodebase(codebase);
                // new Information Object
                Information info = new Information();
                info.setTitle(viewerproperties.getProperty(viewerid + ".title"));
                info.setVendor(viewerproperties.containsKey(viewerid + ".vendor") ? viewerproperties.getProperty(viewerid + ".vendor") : "BiBiServ - Bielefeld BioInformatics Service");
                Description description = new Description();
                description.setContent(viewerproperties.containsKey(viewerid + ".description") ? viewerproperties.getProperty(viewerid + ".description") : "No description available - please contact system administrator(s) for further information!");
                info.getDescription().add(description);
                jnlp.getInformation().add(info);
                // new Security object
                Security security = new Security();
                security.setAllPermissions(new AllPermissions());
                jnlp.setSecurity(security);
                // new Resources object
                Resources resources = new Resources();
                List list_of_resources = resources.getJavaOrJ2SeOrJar();
                J2Se j2se = new J2Se();
                if (viewerproperties.containsKey(viewerid + ".j2se.jvm_args")) {
                    j2se.setJavaVmArgs(viewerproperties.getProperty(viewerid + ".j2se.jvm_args"));
                }
                j2se.setVersion(viewerproperties.containsKey(viewerid + ".j2se.version") ? viewerproperties.getProperty(viewerid + ".j2se.version") : "1.5.0+");
                list_of_resources.add(j2se);

                Jar jar = new Jar();
                jar.setHref(viewerproperties.getProperty("rootpath") + "/" + viewerproperties.getProperty(viewerid + ".jar"));
                list_of_resources.add(jar);

                jnlp.getResources().add(resources);

                //new application desc object
                ApplicationDesc applicationdesc = new ApplicationDesc();
                // Mainclass 
                applicationdesc.setMainClass(viewerproperties.getProperty(viewerid + ".mainclass"));
                // Arguments 
                if (viewerurl == null) {
                    viewerurl = codebase + "/spool?id=" + result_id + "&name=" + result_name + "&tool=" + viewerproperties.getProperty(viewerid);
                    if (result_user != null) {
                        viewerurl += "&url=" + result_user;
                    }
                    if (result_contenttype != null) {
                        viewerurl += "&contenttype=" + result_contenttype;
                    }
                }
                // set url as argument with optional prefix (if set in viewer.properties)
                if (viewerproperties.containsKey(viewerid + ".urlprefix")) {
                    applicationdesc.getArgument().add(viewerproperties.getProperty(viewerid + ".urlprefix"));
                    applicationdesc.getArgument().add(viewerurl);
                } else {
                    applicationdesc.getArgument().add(viewerurl);
                }
                // set additional arguments seperated by space (if set in viewer.properties)
                if (viewerproperties.containsKey(viewerid + ".args")) {
                    String args[] = viewerproperties.getProperty(viewerid + ".args").split(" ");
                    for (String arg : args) {
                        applicationdesc.getArgument().add(arg);
                    }
                }

                // set additional viewerarg as argument (optional) 
                if (viewerarg != null) {
                    applicationdesc.getArgument().add(viewerarg);
                }

                jnlp.setApplicationDesc(applicationdesc);

                //marshal to xml string
                final JAXBContext jaxbContext = JAXBContext.newInstance(Jnlp.class);
                StringWriter writer = new StringWriter();
                jaxbContext.createMarshaller().marshal(jnlp, writer);

                // generate jnlp output
                httpServletResponse.setContentType("application/x-java-jnlp-file");
                httpServletResponse.setHeader("Content-Disposition", "inline; filename=\"" + viewerid + "_" + System.currentTimeMillis() + ".jnlp");

                // generate jnlp output
                PrintWriter out = httpServletResponse.getWriter();

                out.println(writer.toString());

                // finished
                return;
            } catch (JAXBException ex) {
                log.fatal(ex.getMessage(), ex);
                httpServletResponse.sendError(500, ex.getMessage());
                return;
            }

        }

    }
}
