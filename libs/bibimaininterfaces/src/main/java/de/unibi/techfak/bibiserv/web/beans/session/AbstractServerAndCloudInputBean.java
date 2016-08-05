/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 BiBiServ Curator Team"
 *
 * Contributor(s): Adrian Frischkorn,
 *                 Thomas Gatter
 *
 */
package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.utils.ValidationConnection;
import de.unibi.cebitec.bibiserv.utils.connect.ServerFileConnection;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.web.beans.Input;
import de.unibi.techfak.bibiserv.cms.Texample.Prop;
import java.io.File;
import org.apache.commons.codec.binary.Base64;
import java.util.ArrayList;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;

/**
 *
 * @author afrischk
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractServerAndCloudInputBean extends AbstractCloudInputBean {

    private static Logger log = Logger.getLogger(AbstractServerAndCloudInputBean.class);

    //server file stuff
    private boolean serverfilevisible;

    @Override
    public void afterPropertiesSet() throws Exception {        
        super.afterPropertiesSet();

        // add the server selection to array of selectedItems
        SelectItem serverfile = new SelectItem("4", messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.SERVERFILE"));
        serverfile.setDisabled(isServerFileDisabled());
        SelectItem[] newselectedItems = new SelectItem[selectedItems.length+1];
        System.arraycopy(selectedItems, 0, newselectedItems, 0, selectedItems.length);
        newselectedItems[newselectedItems.length-1] = serverfile;
        selectedItems = newselectedItems;
    }
    

    /*
     * ######### getter/setter ############
     */


    public void setServerfile(String serverfile) {
        this.serverfile = serverfile;
    }

    public boolean isServerfilevisible() {
        return serverfilevisible;
    }

    public void setServerfilevisible(boolean serverfilevisible) {
        this.serverfilevisible = serverfilevisible;
    }
    
        @Override
    public void setSelectedInput(String selectedInput) {
        
        if(selectedInput==null){ // choosing disabled items causes weird behaviour otherwise
            return;
        }
        
        //only if selection changed we must do anything ...
        this.selectedInput_old = this.selectedInput;
        this.selectedInput = selectedInput;
    }
    

//#######################################################
//############## AbstractInputBean Stuff ################
//####################################################### 
    @Override
    public void changelistener() {

        //only if selection changed we must do anything ...
        if (!selectedInput_old.equals(selectedInput)) {
            
            resetGUI();
            showPublicObjects = false;

            if(selectedInput.equals("4")) {
                uploadvisible = false;
                textareavisible = false;
                cloudsupportvisible = false;
                cloud_upload_option_1_is_visible = false;
                cloud_upload_option_2_is_visible = false;
                basespace_visible = false;
                serverfilevisible = true;
            } else {
                serverfilevisible = false;
                super.changelistener();
            }
        }
    }
    
        /**
     * Check if given example Property (set in tool description) is associated
     * with current Input.
     *
     * @param p - a example property
     *
     * @return Return true of property matches current input id
     */
    @Override
    public boolean checkAndSet(Prop p) {
        if (p.getIdref().equals(getId())) {

            validated = false;
            uploadvisible = false;
            cloudsupportvisible = false;
            cloud_upload_option_1_is_visible = false;
            cloud_upload_option_2_is_visible = false;
            selected_cloud_upload_option = "1";
            selected_cloud_upload_option_tmp = "1";

            // too chaining stuff
            chainingServerFileVisible = false;
            serverfile = "";
            lastToolname = "";

            if (p.isSetFile() && p.isFile()) {
                selectedInput = "4";
                serverfilevisible = true;
                textareavisible = false;
                serverfile = p.getValue();
            } else {
                selectedInput = "2";
                textareavisible = true;
                serverfilevisible = false;
                textarea = new String(Base64.decodeBase64(p.getValue().getBytes()));
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void setTextualInput(String input) {
        validated = false;
        uploadvisible = false;
        cloudsupportvisible = false;
        cloud_upload_option_1_is_visible = false;
        cloud_upload_option_2_is_visible = false;
        selected_cloud_upload_option = "1";
        selected_cloud_upload_option_tmp = "1";

        selectedInput = "2";
        textareavisible = true;
        serverfilevisible = false;
        textarea = input;
        
        // too chaining stuff
        chainingServerFileVisible = false;
        serverfile = "";
        lastToolname = "";
    }
    
        @Override
    public void setFileInput(String filename, String lastToolname) {
        validated = false;
        uploadvisible = false;
        cloudsupportvisible = false;
        cloud_upload_option_1_is_visible = false;
        cloud_upload_option_2_is_visible = false;
        selected_cloud_upload_option = "1";
        selected_cloud_upload_option_tmp = "1";

        textareavisible = false;
        textarea = "";
        
        // too chaining stuff
        selectedInput = "4";
        serverfilevisible = true;
        chainingServerFileVisible = true;
        serverfile = filename;  
        this.lastToolname = lastToolname;
    }

    /*
     * determine source and validate
     */
    @Override
    public void determineSourceAndValidate(String[] args, OntoRepresentation target, String functionid) {   
        
        input = new Input();
        input.setSkipValidation(skipValidation);
        // set validated flag
        valid = false;
        // determine source
        if (selectedInput.equals("4")) {
            log.info(" -> work with server file");     
                input.setSource("Server File - " + serverfile);
                ValidationConnection tmp = null ;

                tmp = new ServerFileConnection(serverfile);
                File testFile = new File(serverfile);   
                if (testFile.exists()) {
                    
                    if(input.isSkipValidation()) {
                        // skip enabled, no validation
                        input.setInput(tmp);
                        input.setRepresentations(new ArrayList<OntoRepresentation>());
                        input.getRepresentations().add(target);
                        input.setChosen(target);
                        valid = true;
                    } else {
                        // normal validation
                        valid = validate(tmp, args, target);
                    }
                    if (valid) {
                        // hide showInfo field
                        showInfo = false;
                        log.info("-> server file is valid!");
                    } else {
                        log.info("-> server file is invalid!");
                        input.setSource("Server File -" + selectedData.getFile()); //i18n
                        input.setMessage(messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.DATANOTVALID") + " "+input.getMessage()); //i18n
                    }

                } else {
                    input.setSource("Server File -" + selectedData.getFile()); //i18n
                    input.setMessage(args[3] + " "+ messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.FILENOTFOUND")); //i18n
                }
        } else {
            super.determineSourceAndValidate(args, target, functionid);
        }

        if(input.getChosen()!=null) {
            chosenInput = input.getChosen().getKey();
        }
        validated = true;
    }

    /**
     * Reset input bean
     */
    @Override
    public void reset() {

        super.reset();

        serverfilevisible = false;
    }
    
    
     /*
     * ######### abstract methods ############
     */

    /**
     * Returns if user defined files from server side are allowed. 
     * @return Returns if user defined files from server side are allowed. 
     */
    public abstract boolean isServerFileDisabled();
    
}