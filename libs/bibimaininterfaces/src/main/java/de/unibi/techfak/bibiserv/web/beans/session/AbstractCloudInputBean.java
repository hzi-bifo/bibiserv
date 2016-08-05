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

import de.unibi.cebitec.bibiserv.basespace.Scope;
import de.unibi.cebitec.bibiserv.basespace.exception.BaseSpaceException;
import de.unibi.cebitec.bibiserv.basespace.json.AppResult;
import de.unibi.cebitec.bibiserv.basespace.json.BaseSpaceObject;
import de.unibi.cebitec.bibiserv.basespace.json.Project;
import de.unibi.cebitec.bibiserv.basespace.json.Run;
import de.unibi.cebitec.bibiserv.basespace.json.Sample;
import de.unibi.cebitec.bibiserv.utils.ValidationConnection;
import de.unibi.cebitec.bibiserv.utils.connect.URLValidationConnection;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.web.beans.Input;
import de.unibi.techfak.bibiserv.cms.Texample.Prop;
import de.unibi.techfak.bibiserv.web.beans.AWSFileData;
import org.apache.commons.codec.binary.Base64;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author afrischk
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractCloudInputBean extends AbstractInputBean implements IlluminaInputBeanInterface {

    private static Logger log = Logger.getLogger(AbstractCloudInputBean.class);

    protected AWSFileData selectedData;
    
    protected String bucket_location;
    
    protected String selected_item_bucket;
    protected String selected_item_object;
    
    protected String selected_cloud_upload_option;
    protected String selected_cloud_upload_option_tmp;
    protected String loadListMsg;
    protected String s3url_to_object;
    protected boolean cloud_upload_option_1_is_visible;
    protected boolean cloud_upload_option_2_is_visible;

    protected boolean cloudsupportvisible = false;
    protected boolean showPublicObjects = false;
    protected AwsBeanInterface awsbean;
    
    protected List<String> itemlist_public_objects;
    protected List<String> itemlist_objects;
    
    //illumina Basespace
    protected IlluminaBeanInterface illuminabean;
    protected boolean basespace_visible;
    
    protected BaseSpaceObject selected_runproject;
    protected int selected_runproject_index;
    protected List<BaseSpaceItem> itemlist_runsprojects;
    
    protected boolean renderSampleAppresult;
    protected BaseSpaceObject selected_sampleappresult;
    protected int selected_sampleappresult_index;
    protected List<BaseSpaceItem> itemlist_samplesappresults;
    
    protected BaseSpaceObject selected_basespacefile;
    protected List<BaseSpaceItem> itemlist_basespacefiles;
    
    protected LazyBaseSpaceObjectModel baseSpaceFileModel;
    
    protected int model_index = -1;
    protected static int RUN_INDEX = 1;
    protected static int SAMPLE_INDEX = 2;
    protected static int APPRESULT_INDEX = 3;

    @Override
    public void afterPropertiesSet() throws Exception {
        
        selected_item_bucket = "";
        selected_item_object = "";
        bucket_location = messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NOLOCATION");

        s3url_to_object = "";

        selected_cloud_upload_option = "1";
        selected_cloud_upload_option_tmp = "1";

        cloud_upload_option_1_is_visible = false;
        cloud_upload_option_2_is_visible = false;
        basespace_visible = false;
        

        //############## AbstractInputBean Stuff ################


        selectedItems = new SelectItem[]{
            new SelectItem("1", messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.UPLOAD")),
            new SelectItem("2", messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.COPYPASTE")),
            new SelectItem("3", messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.AWSSUPPORT"))};

        input = new Input();
        
        selectedData = new AWSFileData();
        initBaseSpace();
        baseSpaceFileModel = new LazyBaseSpaceObjectModel();
    }
        
     //########################### Public URL  ###########################
    
     public void validate_s3_url() {
        HashMap<Integer, String> validationMessage = awsbean.validate_s3_url_awsbean(s3url_to_object, selectedData);
        throwFacesMessage(validationMessage, getId() + "_msg_validation");
        
        resetValidated();
        showPublicObjects = false;
        
        loadListMsg = messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.VALIDATINGEND")+".<br/>";
        
        // this is bucket url
        if(!selectedData.getBucket().isEmpty() && selectedData.getFile().isEmpty()) {
            showPublicObjects = true;
            
            validationMessage = awsbean.loadS3ObjectListForced(selectedData.getBucket());
            if(validationMessage.isEmpty()) {
                loadListMsg += messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.LISTOBJECTS")+".";
            } else {
                loadListMsg = "";
                for(String str: validationMessage.values()) {
                    loadListMsg += str + ".<br/>";
                }
            }
            itemlist_public_objects = awsbean.getS3ObjectList(selectedData.getBucket());
        }
    }
     
    public void saveS3PublicUrl() {
        selectedData.setFile(selected_item_object);
        HashMap<Integer, String> saveMessages = awsbean.saveS3urlSelect(selectedData);
        throwFacesMessage(saveMessages, getId() + "_msg_validation");
        resetValidated();
    }
     
     
    //########################### Bucked And File  ###########################

    public void getBucketList() {
        showInfo = false;
        loadListMsg = awsbean.refresh();
    }
    
    public void resetBuckets(){
        selected_item_bucket = "";
        selected_item_object = "";
       itemlist_objects = new ArrayList<String>();
       resetValidated();
    }

    
    public void getS3ObjectList() {
        resetValidated();
        
        //set new Data
        selectedData.setBucket(selected_item_bucket);

        if(selected_item_bucket.isEmpty()) {
            return;
        }
        
        // load the data, an error might occur
        HashMap<Integer, String> validationMessage = awsbean.loadS3ObjectList(selectedData.getBucket());
        throwFacesMessage(validationMessage, getId() + "_msg_browse");
        if(!validationMessage.isEmpty()) {
            return;
        }
        // no error, get data
        displayBucketLocation();
        itemlist_objects = awsbean.getS3ObjectList(selectedData.getBucket());
    }

    public void displayBucketLocation() {
        bucket_location = awsbean.getBucketLocation(selectedData.getBucket());
    }

    public void saveS3url() {
        selectedData.setBucket(selected_item_bucket);
        selectedData.setFile(selected_item_object);
        
        HashMap<Integer, String> browseMessage = awsbean.saveS3urlSelect(selectedData);
        throwFacesMessage(browseMessage, getId() + "_msg_browse");
        resetValidated();
    }


    
    //########################### General options  ###########################

    public void changelistener_cloud_upload_option() {
        if (!selected_cloud_upload_option_tmp.equals(selected_cloud_upload_option)) {
            resetGUI();
            switch (selected_cloud_upload_option.toCharArray()[0]) {

                case '1':
                    cloud_upload_option_1_is_visible = true;
                    cloud_upload_option_2_is_visible = false;
                    basespace_visible = false;
                    break;
                case '2':
                    cloud_upload_option_1_is_visible = false;
                    cloud_upload_option_2_is_visible = true;
                    basespace_visible = false;
                    break;
                case '3':
                    cloud_upload_option_1_is_visible = false;
                    cloud_upload_option_2_is_visible = false;
                    basespace_visible = true;
                    break;
            }

        }

    }



    public void throwFacesMessage(HashMap<Integer, String> msg, String component_id) {
        if(component_id.isEmpty()){
            component_id = null;
        
        }
        for (Integer i : msg.keySet()) {
            switch (i.intValue()) {
                case 1:
                    FacesContext.getCurrentInstance().addMessage(component_id, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info ", msg.get(new Integer(1))));
                    break;
                case 2:
                    FacesContext.getCurrentInstance().addMessage(component_id, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning ", msg.get(new Integer(2))));
                    break;
                case 3:
                    FacesContext.getCurrentInstance().addMessage(component_id, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error ", msg.get(new Integer(3))));
                    break;
                case 4:
                    FacesContext.getCurrentInstance().addMessage(component_id, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal ", msg.get(new Integer(4))));
                    break;

            }
        }

    }
    /*
     * ######### getter/setter ############
     */

    public AwsBeanInterface getAwsbean() {
        return awsbean;
    }

    public void setAwsbean(AwsBeanInterface awsbean) {
        this.awsbean = awsbean;
    }

    public List<String> getItemlist_buckets() {
        return awsbean.getBuckets();
    }


    public List<String> getItemlist_objects() {
        return itemlist_objects;
    }

    public void setItemlist_objects(List<String> itemlist_objects) {
        this.itemlist_objects = itemlist_objects;
    }

    public boolean isShowPublicObjects() {
        return showPublicObjects;
    }

    public void setShowPublicObjects(boolean showPublicObjects) {
        this.showPublicObjects = showPublicObjects;
    }

    public String getS3url_to_object() {
        return s3url_to_object;
    }


    public void setS3url_to_object(String s3url_to_object) {
        this.s3url_to_object = s3url_to_object;
    }

    public String getSelected_item_bucket() {
        return selected_item_bucket;
    }

    public void setSelected_item_bucket(String selected_item_bucket) {
        if(selected_item_bucket==null) {
            selected_item_bucket = "";
        }
        this.selected_item_bucket = selected_item_bucket;
    }

    public String getSelected_item_object() {
        return selected_item_object;
    }

    public void setSelected_item_object(String selected_item_object) {
        if(selected_item_object==null) {
            selected_item_object = "";
        }
        this.selected_item_object = selected_item_object;
    }

    public List<String> getItemlist_public_objects() {
        return itemlist_public_objects;
    }

    public void setItemlist_public_objects(List<String> itemlist_public_objects) {
        this.itemlist_public_objects = itemlist_public_objects;
    }


    public boolean isCloud_upload_option_2_is_visible() {
        return cloud_upload_option_2_is_visible;
    }

    public void setCloud_upload_option_2_is_visible(boolean cloud_upload_option_2_is_visible) {
        this.cloud_upload_option_2_is_visible = cloud_upload_option_2_is_visible;
    }

    public String getSelected_cloud_upload_option() {
        return selected_cloud_upload_option;
    }

    public void setSelected_cloud_upload_option(String selected_cloud_upload_option) {
        this.selected_cloud_upload_option_tmp = this.selected_cloud_upload_option;
        this.selected_cloud_upload_option = selected_cloud_upload_option;
    }

    public String getBucket_location() {
        return bucket_location;
    }

    public void setBucket_location(String bucket_location) {
        this.bucket_location = bucket_location;
    }

    public boolean isCloud_upload_option_1_is_visible() {
        return cloud_upload_option_1_is_visible;
    }

    public void setCloud_upload_option_1_is_visible(boolean cloud_upload_option_1_is_visible) {
        this.cloud_upload_option_1_is_visible = cloud_upload_option_1_is_visible;
    }

    public String getSelected_item_public_bucket() {
        return selected_item_bucket;
    }

    public void setSelected_item_public_bucket(String selected_item_public_bucket) {
        if(selected_item_public_bucket==null){
            selected_item_public_bucket = "";
        }
        this.selected_item_bucket = selected_item_public_bucket;
    }

//#######################################################
//############## new getter and setter ##################
//####################################################### 
    public boolean isCloudsupportvisible() {
        return cloudsupportvisible;
    }

    public void setCloudsupportvisible(boolean cloudsupportvisible) {
        this.cloudsupportvisible = cloudsupportvisible;
    }

    public String getLoadListMsg() {
        return loadListMsg;
    }

    public void setLoadListMsg(String loadListMsg) {
        this.loadListMsg = loadListMsg;
    }
    
    
    public boolean isAwsCredentialsSet(){
        return awsbean.isAwsCredentialsSet();
    }
    
    public boolean isShowStreamFormats(){
        return !uploadvisible && !textareavisible;
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

            if (selectedInput.equals("1")) {
                uploadvisible = true;
                textareavisible = false;
                cloudsupportvisible = false;
                cloud_upload_option_1_is_visible = false;
                cloud_upload_option_2_is_visible = false;
                basespace_visible = false;

            } else if (selectedInput.equals("2")) {
                uploadvisible = false;
                textareavisible = true;
                cloudsupportvisible = false;
                cloud_upload_option_1_is_visible = false;
                cloud_upload_option_2_is_visible = false;
                basespace_visible = false;

            } else if (selectedInput.equals("3")) {
                uploadvisible = false;
                textareavisible = false;
                cloudsupportvisible = true;
                cloud_upload_option_1_is_visible = true;
                cloud_upload_option_2_is_visible = false;
                basespace_visible = false;
                selected_cloud_upload_option = "1";
                selected_cloud_upload_option_tmp = "1";

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
            selectedInput = "2";
            uploadvisible = false;
            textareavisible = true;
            cloudsupportvisible = false;
            cloud_upload_option_1_is_visible = false;
            cloud_upload_option_2_is_visible = false;
            basespace_visible = false;
            selected_cloud_upload_option = "1";
            selected_cloud_upload_option_tmp = "1";
            textarea = new String(Base64.decodeBase64(p.getValue().getBytes()));
            
            // too chaining stuff
            chainingServerFileVisible = false;
            serverfile = "";
            lastToolname = "";
            
            return true;
        }
        return false;
    }
    
    @Override
    public void setTextualInput(String input) {
        validated = false;
        selectedInput = "2";
        uploadvisible = false;
        textareavisible = true;
        cloudsupportvisible = false;
        cloud_upload_option_1_is_visible = false;
        cloud_upload_option_2_is_visible = false;
        basespace_visible = false;
        selected_cloud_upload_option = "1";
        selected_cloud_upload_option_tmp = "1";
        textarea = input;

        // too chaining stuff
        chainingServerFileVisible = false;
        serverfile = "";
        lastToolname = "";
    }
    
    @Override
    public void setFileInput(String filename, String lastToolname) {
        validated = false;
        selectedInput = "";
        uploadvisible = false;
        textareavisible = false;
        cloudsupportvisible = false;
        cloud_upload_option_1_is_visible = false;
        cloud_upload_option_2_is_visible = false;
        basespace_visible = false;
        selected_cloud_upload_option = "1";
        selected_cloud_upload_option_tmp = "1";
        textarea = "";
        
        // too chaining stuff
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
        if ("3".equals(selectedInput) && selected_cloud_upload_option.equals("2")) {
                // AWS Select
                log.info(" -> work with aws select");
                input.setSource("AWS Select - " + selectedData.getFile());
                ValidationConnection tmp = awsbean.getAWSObject(selectedData);
                if (tmp != null) {
                    
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
                        log.info("-> aws selected file is valid!");
                    } else {
                        log.info("-> aws selected file is invalid!");                      
                        input.setSource("AWS Select - " + selectedData.getFile()); //i18n
                        input.setMessage(messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.DATANOTVALID") +" "+ input.getMessage() + " " + messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.ABORTEDDOWNLOAD")); //i18n
                    }
                } else {
                    input.setSource("AWS Select - " + selectedData.getFile()); //i18n
                    input.setMessage(args[3] + " " + messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.ABORTEDDOWNLOAD")); //i18n
                }


        } else if ("3".equals(selectedInput) && selected_cloud_upload_option.equals("1")) {
                // AWS Url
                log.info(" -> work with aws link");     
                input.setSource("AWS Url - " + selectedData.getFile());
                ValidationConnection tmp = null ;
                if(!awsbean.isAwsCredentialsSet()){
                    tmp = awsbean.getConnectionObject(selectedData);
                }else{
                    tmp = awsbean.getAWSObject(selectedData);
                }
                if (tmp != null) {
                    
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
                        log.info("-> aws url is valid!");
                    } else {
                        log.info("-> aws url is invalid!");
                        input.setSource("AWS Url -" + selectedData.getFile()); //i18n
                        input.setMessage(messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.DATANOTVALID") + " "+input.getMessage() + " "+ messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.ABORTEDDOWNLOADWITHMENTION")); //i18n
                    }

                } else {
                    input.setSource("AWS Url -" + selectedData.getFile()); //i18n
                    input.setMessage(args[3] + " "+ messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.ABORTEDDOWNLOADWITHMENTION")); //i18n
                }

        } else if ("3".equals(selectedInput) && selected_cloud_upload_option.equals("3")) {
                // Illumina BaseSpace
                log.info(" -> work with Illumina BaseSpace");
                  
                if (selected_basespacefile != null) {
                    
                    input.setSource("Illumina BaseSpace - " + selected_basespacefile.getName()+" "+selected_basespacefile.getId());
                    
                    try {
                        
                        // request rights from illumina
                        boolean authworked = illuminabean.authenticate(this,functionid);
                        if (authworked) {
                            // get URL and ValidationConnectoin
                            String url = illuminabean.getDownloadUrl(selected_basespacefile.getId().toString());
                            URL urlObject = new URL(url);
                            ValidationConnection tmp = new URLValidationConnection(urlObject);

                            if (input.isSkipValidation()) {
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
                                log.info("-> aws selected file is valid!");
                            } else {
                                log.info("-> aws selected file is invalid!");
                                input.setMessage(messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.DATANOTVALID") + " " + input.getMessage() + " " + messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.ABORTEDDOWNLOADILLUMINA")); //i18n
                            }
                        } else {
                            input.setMessage(messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.DATANOTVALID") +" " + messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NOAUTHILLUMINA")); //i18n
                        }
                    } catch(BaseSpaceException | MalformedURLException e) {
                         input.setMessage(messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.DATANOTVALID") +" "+ e.getMessage()+ " " + messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.ABORTEDDOWNLOADILLUMINA")); //i18n
                    }
                } else {
                    input.setSource("AWS Select - " + selectedData.getFile()); //i18n
                    input.setMessage(messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NOBASESPACEFILE")); //i18n
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

        //#######################################################
        //#################### AWS Stuff ########################
        //####################################################### 

        resetGUI();

        selectedInput = "2";
        uploadvisible = false;
        textareavisible = true;
        cloudsupportvisible = false;
        cloud_upload_option_1_is_visible = false;
        cloud_upload_option_2_is_visible = false;
        basespace_visible = false;
        selected_cloud_upload_option = "1";
        selected_cloud_upload_option_tmp = "1";
    }

    protected void resetGUI() {
        resetValidated();
        valid = false;
        showPublicObjects = false;
        selected_item_bucket = "";
        s3url_to_object = "";
        getS3ObjectList();
        bucket_location = messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NOLOCATION");
        selected_item_object = "";
                
        selectedData = new AWSFileData();
        
        chainingServerFileVisible = false;
        
        // too chaining stuff
        chainingServerFileVisible = false;
        serverfile = "";
        lastToolname = "";
        
        initBaseSpace();
    }
    
    
     /*
     * ######### Illumina BaseSpace############
     */
    
    @Override
    public void register(String functionid) {
            illuminabean.registerInput(this, functionid);
    }
    @Override
    public void unregister(String functionid) {
            illuminabean.unregisterInput(this, functionid);
    }

    protected boolean resetdone = false;
    public void resetBasespace() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    illuminabean.basicAuthentification();
                } catch(BaseSpaceException e) {
                    FacesContext.getCurrentInstance().addMessage(getId()+"_msg_basespace", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error has occured while communicating with Illumina BaseSpace:" + e.getMessage()));
                }
                resetdone = true;
            }
        }).start();
        
        resetValidated();
    }
    
    public void baseSpaceCallback(){
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("done",resetdone);
        
        // check for urls from basespace that have to be opened
        illuminabean.getUrlQueueBean().requestNextLaunch();
        
        if(!resetdone) {
            return;
        }
        
        resetdone = false;
        illuminabean.resetCache();
        initBaseSpace();
        
    }

    private void initBaseSpace(){
        model_index = -1;
        selected_runproject = null;
        selected_sampleappresult = null;
        selected_basespacefile = null;
        selected_runproject_index = -1;
        selected_sampleappresult_index = -1;
        
        renderSampleAppresult = false;
        
        itemlist_runsprojects = new ArrayList<>(); 
        itemlist_samplesappresults = new ArrayList<>();
        itemlist_basespacefiles = new ArrayList<>();
        if(illuminabean.isBasicAuthorization()) { // only populate if basic authorization is available
            try {
                populateBaseSpaceDropDown(itemlist_runsprojects, illuminabean.listRuns());
                populateBaseSpaceDropDown(itemlist_runsprojects, illuminabean.listProjects());
            } catch (BaseSpaceException e) {
                FacesContext.getCurrentInstance().addMessage(getId() + "_msg_basespace", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error has occured while communicating with Illumina BaseSpace:" + e.getMessage()));
            }
        }
        resetValidated();
    }
    
    public void getProjectcontentOrBasespaceFiles() {
        resetValidated();
        
        // case null selection
        if (selected_runproject == null) {
            renderSampleAppresult = false;
            itemlist_basespacefiles = new ArrayList<>();
            selected_sampleappresult = null;
            selected_basespacefile = null;
            selected_sampleappresult_index =-1;
            model_index = -1;
            return;
        }
        
        // test of selected if run
        if(selected_runproject instanceof Run) {
             // runs have direct file
            renderSampleAppresult = false;
            itemlist_basespacefiles = new ArrayList<>();
            selected_sampleappresult = null;
            selected_basespacefile = null;
            selected_sampleappresult_index =-1;
            
            model_index = RUN_INDEX;
            
        } else if (selected_runproject instanceof Project) {
            // projects contain samples and
            renderSampleAppresult = true;
            itemlist_basespacefiles = new ArrayList<>();
            itemlist_samplesappresults = new ArrayList<>();
            selected_sampleappresult = null;
            selected_basespacefile = null;
            selected_sampleappresult_index =-1;
            
            model_index = -1;
            
            try {
                populateBaseSpaceDropDown(itemlist_samplesappresults, illuminabean.listAppresults(selected_runproject.getId()));
                populateBaseSpaceDropDown(itemlist_samplesappresults, illuminabean.listSamples(selected_runproject.getId()));
            } catch (BaseSpaceException e) {
                FacesContext.getCurrentInstance().addMessage(getId() + "_msg_basespace", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error has occured while communicating with Illumina BaseSpace:" + e.getMessage()));
            }
        }
    }
    
    public void getBasespaceFiles() {
        
       resetValidated(); 
       itemlist_basespacefiles = new ArrayList<>();
       selected_basespacefile = null;
       
        if (selected_sampleappresult == null) {
            model_index = -1;
            return;
        }
        
        if(selected_sampleappresult instanceof AppResult) {
             model_index = APPRESULT_INDEX;
        } else if(selected_sampleappresult instanceof Sample) {
            model_index = SAMPLE_INDEX;
        }
    }
    
    @Override
    public boolean addToScope(Scope scope){
         
        // first test if this input really is in auth mode
        if (!selectedInput.equals("3") || !selected_cloud_upload_option.equals("3")) { 
            return true;
        }
        
        if(selected_basespacefile == null) {
            return false;
        }
        
        if(selected_sampleappresult != null) { // file is from sample or appresult
            if(selected_sampleappresult instanceof AppResult) {
                scope.addReadAppresult(selected_sampleappresult.getId());
                return true;
            } else if(selected_sampleappresult instanceof Sample){
                scope.addReadSample(selected_sampleappresult.getId());
                return true;
            }
        } else if(selected_runproject != null) { // file should be a run than
            if(selected_runproject instanceof Run) {
                scope.addReadRun(selected_runproject.getId());
                return true;
            }
        }
        // this should not happen
        return false;
    }
    
    
    private void populateBaseSpaceDropDown(List<BaseSpaceItem> dropdown, List<? extends BaseSpaceObject> data){
        for(BaseSpaceObject ob:data) {
             dropdown.add(new BaseSpaceItem(ob, dropdown.size(), ob.getTypeTokenString()+": "+ob.getName()+" "+ob.getId()));
        }
    }

    public int getSelected_runproject_index() {
        return selected_runproject_index;
    }

    public void setSelected_runproject_index(int selected_runproject_index) {
        this.selected_runproject_index = selected_runproject_index;
        if (this.selected_runproject_index==-1) {
            this.selected_runproject = null;
        } else {
            this.selected_runproject = itemlist_runsprojects.get(this.selected_runproject_index).getObject();
        }
    }

    public int getSelected_sampleappresult_index() {
        return selected_sampleappresult_index;
    }

    public void setSelected_sampleappresult_index(int selected_sampleappresult_index) {
        this.selected_sampleappresult_index = selected_sampleappresult_index;
         if (this.selected_sampleappresult_index==-1) {
            this.selected_sampleappresult = null;
        } else {
            this.selected_sampleappresult = itemlist_samplesappresults.get(this.selected_sampleappresult_index).getObject();
        }
    }

    public BaseSpaceObject getSelected_basespacefile() {
        return selected_basespacefile;
    }

    public void setSelected_basespacefile(BaseSpaceObject selected_basespacefile) {
        this.selected_basespacefile = selected_basespacefile;
    }
 
    public boolean isBasespace_visible() {
        return basespace_visible;
    }

    public List<BaseSpaceItem> getItemlist_runsprojects() {
        return itemlist_runsprojects;
    }

    public boolean isRenderSampleAppresult() {
        return renderSampleAppresult;
    }

    public List<BaseSpaceItem> getItemlist_samplesappresults() {
        return itemlist_samplesappresults;
    }

    public List<BaseSpaceItem> getItemlist_basespacefiles() {
        return itemlist_basespacefiles;
    }

    public IlluminaBeanInterface getIlluminaBean() {
        return illuminabean;
    }

    public void setIlluminaBean(IlluminaBeanInterface illuminabean) {
        this.illuminabean = illuminabean;
    }

    public LazyBaseSpaceObjectModel getBaseSpaceFileModel() {
        return baseSpaceFileModel;
    }
    
     /*
     * ######### abstract methods ############
     */    

    /**
     * Returns if the file can handle this input as a stream (STDIN or Named Pipe)
     * @return if the file can handle this input as a stream 
     */
    @Override
    public abstract boolean supportsStreamedInput();
    
    
    
    public class LazyBaseSpaceObjectModel extends LazyDataModel<BaseSpaceObject>  {
    
        @Override
        public List<BaseSpaceObject> load(int first, int pagesize, String string, SortOrder so, Map<String, Object> map) {
            this.setRowCount(10000);
           try {
               if (model_index == RUN_INDEX) {
                   return (List) illuminabean.listFilesRun(selected_runproject.getId(), pagesize, first);
               } else if (model_index == SAMPLE_INDEX) {
                   return (List) illuminabean.listFilesSample(selected_sampleappresult.getId(), pagesize, first);
               } else if (model_index == APPRESULT_INDEX) {
                   return (List) illuminabean.listFilesAppresults(selected_sampleappresult.getId(), pagesize, first);
               }
            } catch (BaseSpaceException e) {
                FacesContext.getCurrentInstance().addMessage(getId() + "_msg_basespace", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error has occured while communicating with Illumina BaseSpace:" + e.getMessage()));
            }
           
           return new ArrayList<>();
        }
    
    }
}