/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010,2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010,2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.dev"
 *
 * Contributor(s):
 *
 */
package de.unibi.cebitec.bibiserv.util.visualizer.impl;

import de.unibi.cebitec.bibiserv.util.visualizer.AbstractVisualizer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author bpaassen
 * 
 * Implements a WebService-client for the PseudoViewer-WebService to visualize
 * biological Data.
 */
public class PseudoViewer extends AbstractVisualizer {

    private String imageURL;

    /**
     * Calls the PseudoViewer-Webservice.
     * 
     * @param data Biological data to be shown
     * @return html-div-container containing a link to a .gif created by
     * PseudoViewer using the given input-data
     * @throws Exception throws different Exceptions given by the WebService
     */
    @Override
    public String showThis(Object data) throws Exception {

        String xhtml = "Error during visualization.";

        try {

            /*
             * input is send to the createSOAP-method to construct a valid
             * SOAP-message for the PseudoViewer-WebService.
             */

            String inputSOAPMessage = createSOAP(data.toString());

            URL wsdl =
                    new URL("http://165.246.44.42/WSPseudoViewer/WSPseudoViewer.asmx?WSDL");

            /*
             * An URL-connection to the PseudoViewer-WebService is created using
             * simple java.net-classes.
             */
            
            URLConnection connection = wsdl.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;
            
            /*
             * The SOAP-message is wrapped into a ByteArray.
             */
            
            ByteArrayOutputStream requestWrapperStream = new ByteArrayOutputStream();
            byte[] requestWrapperByteBuffer = new byte[inputSOAPMessage.length()];
            requestWrapperByteBuffer = inputSOAPMessage.getBytes();
            requestWrapperStream.write(requestWrapperByteBuffer);
            byte[] requestByteMessage = requestWrapperStream.toByteArray();
            String SOAPAction =
                    "http://wilab.inha.ac.kr/WSPseudoViewer/WSPVRun";
            
            /*
             * Appropriate HTTP parameters are set.
             */
            
            httpConn.setRequestProperty("Content-Length",
                    String.valueOf(requestByteMessage.length));
            httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpConn.setRequestProperty("SOAPAction", SOAPAction);
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            OutputStream requestStream = httpConn.getOutputStream();
            
            /*
             * The wrapped request-message is written into the request-stream.
             */
           
            requestStream.write(requestByteMessage);
            requestStream.close();
            
            /*
             * To read the responseMessage from the PseudoViewer-WebService,
             * a InputStreamReader is created.
             */
            
            InputStreamReader responseStreamReader =
                    new InputStreamReader(httpConn.getInputStream());
            BufferedReader responseReader = new BufferedReader(responseStreamReader);

            /*
             * Response-message is read and stored in a StringBuilder.
             */
            
            String responseTempString = "";
            
            StringBuilder responseMessage = new StringBuilder();
            
            while ((responseTempString = responseReader.readLine()) != null) {
                responseMessage.append(responseTempString);
            }
            
            responseReader.close();
            
            /*
             * response-SOAP-message is parsed using SAX.
             */
          
            String outputURL = parseOutput(responseMessage.toString());

            /*
             * Finally, the div-container is build.
             */

            xhtml = "<div>\n";
            xhtml += "<img src=\"" + outputURL + "\">\n";
            xhtml += "</div>\n";

        } catch (Exception e) {
            xhtml = "<div>\n";
            xhtml += "The PseudoViewer-WebService-Server is not avaliable right now. ";
            xhtml += "Because of that, your data can't be visualized at the moment.<br/>";
            xhtml += "We are not able to change that. Please visit http://wilab.inha.ac.kr/pseudoviewer/ for further information.";
            xhtml += "</div>\n";
        }
        return xhtml;
    }

    /**
     * wraps the FASTA-input-data in a SOAP-envelope ready to be send to the
     * PseudoViewer-WebService.
     * 
     * Note: SOAP-Envelope-format is copied from an original PseudoViewer-class-
     * created message.
     * 
     * @param input FASTA-data to be shown by PseudoViewer
     * @return SOAP-message for PseudoViewer-Webservice as string
     */
    public String createSOAP(String input) {

        String soapInputMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        soapInputMessage += "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n";
        soapInputMessage += "    <soapenv:Body>\n";
        soapInputMessage += "        <WSPVRun xmlns=\"http://wilab.inha.ac.kr/WSPseudoViewer/\">\n";
        soapInputMessage += "          <WSPVRequest xsi:type=\"ns1:WSPVRequest\" xmlns:ns1=\"http://wilab.inha.ac.kr/WSPseudoViewer/\">\n";
        soapInputMessage += "                <ns1:Option Output_name=\"PseudoViewer-Visualization\" Output_type=\"url gif\" Scale=\"0.0\" xsi:type=\"ns1:WSPVOption\">\n";
        soapInputMessage += "                    <ns1:Drawing_option xsi:type=\"xsd:string\">default</ns1:Drawing_option>\n";
        soapInputMessage += "                    <ns1:Numbering Interval=\"10\" xsi:type=\"ns1:WSPVNumbering\">\n";
        soapInputMessage += "                        <ns1:Base_numbers xsi:type=\"xsd:string\">\n";
        soapInputMessage += "                        </ns1:Base_numbers>\n";
        soapInputMessage += "                        <ns1:Numbering_option xsi:type=\"xsd:string\">default</ns1:Numbering_option>\n";
        soapInputMessage += "                    </ns1:Numbering>\n";
        soapInputMessage += "                </ns1:Option>\n";
        soapInputMessage += "                <ns1:WSPVIn_file_data xsi:type=\"ns1:WSPVInFileData\">\n";
        soapInputMessage += "                    <ns1:Option Output_name=\"PseudoViewer-Visualization\" Output_type=\"url gif\" Scale=\"0.0\" xsi:type=\"ns1:WSPVOption\">\n";
        soapInputMessage += "                        <ns1:Drawing_option xsi:type=\"xsd:string\">default</ns1:Drawing_option>\n";
        soapInputMessage += "                        <ns1:Numbering Interval=\"10\" xsi:type=\"ns1:WSPVNumbering\">\n";
        soapInputMessage += "                            <ns1:Base_numbers xsi:type=\"xsd:string\"></ns1:Base_numbers>\n";
        soapInputMessage += "                            <ns1:Numbering_option xsi:type=\"xsd:string\">default</ns1:Numbering_option>\n";
        soapInputMessage += "                        </ns1:Numbering>\n";
        soapInputMessage += "                    </ns1:Option>\n";
        soapInputMessage += "                    <ns1:PV_file xsi:type=\"xsd:string\">\n";
        soapInputMessage += input;
        soapInputMessage += "                    </ns1:PV_file>\n";
        soapInputMessage += "                </ns1:WSPVIn_file_data>\n";
        soapInputMessage += "            </WSPVRequest>\n";
        soapInputMessage += "        </WSPVRun>\n";
        soapInputMessage += "    </soapenv:Body>\n";
        soapInputMessage += "</soapenv:Envelope>\n";

        return soapInputMessage;

    }

    /**
     * searches for the requested ImageURL of the visualized biological data
     * within the response-message of the PseudoViewer-WebService
     * 
     * @param responseSoapMessage the String-representation of PseudoViewer-
     * WebServices response-SOAP-Message
     * @return String URL of the visualizes data created by the PseudoViewer-
     * WebService
     * @throws RuntimeException only throws Exception if the message does not
     * contain a URL
     */
    public String parseOutput(String responseSoapMessage) throws RuntimeException {

        try {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();

            StringReader reader = new StringReader(responseSoapMessage);
            InputSource inputSource = new InputSource(reader);

            xmlReader.setContentHandler(new PseudoViewerParser(this));

            xmlReader.parse(inputSource);

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (SAXException e) {
            System.err.println(e.getMessage());
        }

        if (imageURL == null) {
            throw new RuntimeException("ImageURL could not be read.");
        }

        return imageURL;

    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
