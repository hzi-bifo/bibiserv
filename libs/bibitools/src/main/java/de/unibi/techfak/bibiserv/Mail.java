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

import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

/**
 * This Mail class uses javax.mail tp provide an easy sendmail function to
 * BiBiTools. The Mail properties are normally set within in the BiBiServ
 * properties file. At least two properties should be set.
 *
 * mail.host= e.g. smtp-relay.CeBiTec.Uni-Bielefeld.DE in the case the server
 * runs inside the CeBiTec network mail.from= e.g.
 * bibi-bounces@cebitec.uni-bielefeld.de default for the bibiserv2 environment
 *
 * This class is a modified copy of
 * http://www.javapractices.com/topic/TopicAction.do?Id=144
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class Mail {

    
    public static final Logger log = Logger.getLogger(Mail.class);
    
    /**
     * Send a single mail.
     *
     * @param aToEmailAddr
     * @param aSubject
     * @param aBody
     * @throws BiBiToolsException Throws a BiBiToolsException in case any goes
     * wrong.
     */
    public static void sendmail(String aToEmailAddr, String aSubject, String aBody) throws BiBiToolsException {
        sendmail(aToEmailAddr, aSubject, aBody, BiBiTools.getProperties());
    }

    /**
     * Send a single email, using individual properties file.
     * 
     * Supports the following properties :
     * 
     * <table>
     *  <tr>
     *      <th>property</th><th>description</th><th>example value</th>
     * </tr>
     * <tr> 
     *      <td>mail.from</td><td>sender</td><td>juser@mydomain.de</td>
     * </tr>
     * <tr>
     *      <td>mail.bcc</td><td>blind carbon copy</td><td>admin@mydomain.de</td>
     * </tr>
     * <tr>
     *      <td>mail.sendmail</td><td>use sendmail instead of JavaMail</td><td>/usr/sbin/sendmail -t </td>
     * </tr>
     * </table>
     * 
     * plus all Properties Java Mail supports.
     * 
     * Sendmail (or any other compatible binary that is specified) is used when property mail.sendmail is
     * set instead of JavaMail. This makes sometimes sense if JavaMail doesn't work as expected
     * 
     *
     * @param aToEmailAddr
     * @param aSubject
     * @param aBody
     * @param mailproperties
     * @throws BiBiToolsException
     */
    public static void sendmail(
            String aToEmailAddr,
            String aSubject,
            String aBody,
            Properties mailproperties) throws BiBiToolsException {

        if (mailproperties.containsKey("mail.sendmail")) {

            try {

                // call sendmail
                Process p = Runtime.getRuntime().exec(mailproperties.getProperty("mail.sendmail"));
                
                // prepare mail
                BufferedWriter bf  = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                bf.write("From:"+mailproperties.getProperty("mail.from")+"\n");
                bf.write("To:"+aToEmailAddr+"\n");
                if (mailproperties.containsKey("mail.bcc")) {
                    bf.write("Bcc:"+mailproperties.getProperty("mail.bcc")+"\n");
                }
                bf.write("Subject:"+aSubject+"\n\n");
                bf.write(aBody+"\n\n");
                bf.close();
                // wait for sendmail finished 
                if (p.waitFor() != 0) {
                    String error = "ERR:\n"+BiBiTools.i2s(new InputStreamReader(p.getErrorStream())) + "\nOUT:\n"+BiBiTools.i2s(new InputStreamReader(p.getInputStream()));
                    log.error(error);
                    throw new BiBiToolsException("Send mail using #{mail.sendmail} fails, see logfile for error message");         
                }

            } catch (IOException | InterruptedException e) {
                throw new BiBiToolsException("Send mail using #{mail.sendmail} fails : "+e.getLocalizedMessage(),e);
            }
        } else {

            Session session = Session.getDefaultInstance(mailproperties);
            MimeMessage message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(mailproperties.getProperty("mail.from")));
                message.addRecipient(
                Message.RecipientType.TO, new InternetAddress(aToEmailAddr));
                if (mailproperties.containsKey("mail.bcc")) { 
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(mailproperties.getProperty("mail.bcc")));
                }
                message.setSubject(aSubject);
                message.setText(aBody);
                Transport.send(message);
            } catch (MessagingException ex) {
                throw new BiBiToolsException("Cannot send mail (" + ex.getLocalizedMessage() + ")", ex);
            }
        }
    }
}
