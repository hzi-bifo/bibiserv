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

import de.unibi.cebitec.bibiserv.util.validate.ValidationException;
import de.unibi.cebitec.bibiserv.utils.ValidationConnection;
import de.unibi.cebitec.bibiserv.utils.connect.AWSUrlTransferConnection;
import de.unibi.cebitec.bibiserv.utils.connect.AWSValidationConnection;
import de.unibi.techfak.bibiserv.cms.Texample;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.web.beans.AWSFileData;
import de.unibi.techfak.bibiserv.web.beans.Input;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * This is the bean for the aws-selection only input designed to pass bucket, objects 
 * and credentials to the tool instead of using the pipeline.
 * 
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractS3TransferInputBean implements InitializingBean, InputBeanInterface {

    private static Logger log = Logger.getLogger(AbstractS3TransferInputBean.class);
    private boolean help = false;
    private boolean validated = false;
    private boolean valid = false;
    private boolean showInfo = false;
    private String loadListMsg;
    private boolean awsVisible;
    private String selectedInput;
    private String selectedInput_old;
    private AwsBeanInterface awsbean;
    private String bucket_location;
    private String selected_item_bucket;
    private String selected_item_object;
    private List<String> itemlist_objects;
    protected AWSFileData selectedData;
    protected Input input;
    protected String chosenInput;
    protected MessagesInterface messages;
    
    
        /**
     * Implementation of Interface Initializing Bean
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        
        selected_item_bucket = "";
        selected_item_object = "";
        bucket_location = messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NOLOCATION");

        input = new Input();
        
        selectedData = new AWSFileData();
        
        if (isAwsCredentialsSet()) {
            selectedInput = selectedInput_old = "1";
            awsVisible = true;
        } else {
            selectedInput = selectedInput_old = "";
            awsVisible = false;
        }

        resetValidated();
    }
    
    public void changelistener() {

        //only if selection changed we must do anything ...
        if (!selectedInput_old.equals(selectedInput)) {

            resetValidated();

            if (selectedInput.equals("1")) {
                awsVisible = true;
            } else {
                awsVisible = false;
            }
        }
    }

    public void resetValidated() {
        validated = false;
        showInfo = false;
    }

    public void displayBucketLocation() {
        bucket_location = awsbean.getBucketLocation(selectedData.getBucket());
    }

    public void getS3ObjectList() {
        resetValidated();

        //set new Data
        selectedData.setBucket(selected_item_bucket);

        if (selected_item_bucket.isEmpty()) {
            return;
        }

        // load the data, an error might occur
        HashMap<Integer, String> validationMessage = awsbean.loadS3ObjectList(selectedData.getBucket());
        throwFacesMessage(validationMessage, getId() + "_msg_browse");
        if (!validationMessage.isEmpty()) {
            return;
        }
        // no error, get data
        displayBucketLocation();
        itemlist_objects = awsbean.getS3ObjectList(selectedData.getBucket());
    }

    public void saveS3url() {
        selectedData.setBucket(selected_item_bucket);
        selectedData.setFile(selected_item_object);

        HashMap<Integer, String> browseMessage = awsbean.saveS3urlSelect(selectedData);
        throwFacesMessage(browseMessage, getId() + "_msg_browse");
        resetValidated();
    }
    
    
    @Override
    public boolean checkAndSet(Texample.Prop p) {
        // no examples available as this is impossible
        return false;
    }
    
    @Override
    public void setTextualInput(String input) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setFileInput(String filename, String lastToolname) {
        throw new UnsupportedOperationException();
    }
    
    @Override
   public void reset() {
        selected_item_bucket = "";
        selected_item_object = "";
        bucket_location = messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NOLOCATION");

        input = new Input();
        
        selectedData = new AWSFileData();
        
        if (isAwsCredentialsSet()) {
            selectedInput = selectedInput_old = "1";
            awsVisible = true;
        } else {
            selectedInput = selectedInput_old = "";
            awsVisible = false;
        }

        resetValidated();
        getS3ObjectList();
    }



    public void throwFacesMessage(HashMap<Integer, String> msg, String component_id) {
        if (component_id.isEmpty()) {
            component_id = null;

        }
        for (Integer i : msg.keySet()) {
            switch (i) {
                case 1:
                    FacesContext.getCurrentInstance().addMessage(component_id, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info ", msg.get(1)));
                    break;
                case 2:
                    FacesContext.getCurrentInstance().addMessage(component_id, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning ", msg.get(2)));
                    break;
                case 3:
                    FacesContext.getCurrentInstance().addMessage(component_id, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error ", msg.get(3)));
                    break;
                case 4:
                    FacesContext.getCurrentInstance().addMessage(component_id, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal ", msg.get(4)));
                    break;

            }
        }

    }
    
    
    /*
     * determine source and validate
     */
    @Override
    public void determineSourceAndValidate(String[] args, OntoRepresentation target, String functionid) {   
      
        input = new Input();
        input.setSkipValidation(true);
        // set validated flag
        valid = false;
        // determine source
        if (selectedInput.equals("1")) {
            // AWS Select
            log.info(" -> work with aws select");
            input.setSource("AWS Select - " + selectedData.getFile());
            ValidationConnection tmp = awsbean.getAWSObject(selectedData);
            tmp = new AWSUrlTransferConnection((AWSValidationConnection) tmp);
            if (tmp != null) {
                
                // skip enabled, no validation
                input.setInput(tmp);
                input.setRepresentations(new ArrayList<OntoRepresentation>());
                input.getRepresentations().add(target);
                input.setChosen(target);
                valid = true;
                // hide showInfo field
                showInfo = false;
                
                // validate connection is useable
                try {
                    tmp.getReader();
                    tmp.abort();
                } catch(ValidationException e) {
                    valid=false;
                    showInfo = true;
                    input.setSource("AWS Select - " + selectedData.getFile()); //i18n
                    input.setMessage(messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NODOWNLOADS3")); //i18n
                }
            } else {
                input.setSource("AWS Select - " + selectedData.getFile()); //i18n
                input.setMessage(messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NODOWNLOADS3")); //i18n
            }
        } else {
            log.fatal("selectInput contains an unexpected value !");
        }
        if (input.getChosen() != null) {
            chosenInput = input.getChosen().getKey();
        }
        validated = true;
    }

    /*
     * ######### Setter / Getter ############
     */
    public void helpAction(ActionEvent e) {
        help = !help;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isAwsCredentialsSet() {
        return awsbean.isAwsCredentialsSet();
    }

    public String getSelectedInput() {
        return selectedInput;
    }

    public void setSelectedInput(String selectedInput) {
        //only if selection changed we must do anything ...
        this.selectedInput_old = this.selectedInput;
        this.selectedInput = selectedInput;
    }

    public boolean isAwsVisible() {
        return awsVisible;
    }

    public void setAwsVisible(boolean awsVisible) {
        this.awsVisible = awsVisible;
    }

    @Override
    public boolean isValidated() {
        return validated;
    }

    @Override
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    @Override
    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }
    
    @Override
    public void showInfoAction(ActionEvent event) {
        showInfo = !showInfo;
    }

    public String getBucket_location() {
        return bucket_location;
    }

    public void setBucket_location(String bucket_location) {
        this.bucket_location = bucket_location;
    }

    public String getSelected_item_bucket() {
        return selected_item_bucket;
    }

    public void setSelected_item_bucket(String selected_item_bucket) {
        if (selected_item_bucket == null) {
            selected_item_bucket = "";
        }
        this.selected_item_bucket = selected_item_bucket;
    }

    public String getSelected_item_object() {
        return selected_item_object;
    }

    public void setSelected_item_object(String selected_item_object) {
        if (selected_item_object == null) {
            selected_item_object = "";
        }
        this.selected_item_object = selected_item_object;
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

    public void getBucketList() {
        showInfo = false;
        loadListMsg = awsbean.refresh();
    }

    public void resetBuckets() {
        selected_item_bucket = "";
        selected_item_object = "";
        itemlist_objects = new ArrayList<>();
        resetValidated();
    }
    
    public String getLoadListMsg() {
        return loadListMsg;
    }

    public void setLoadListMsg(String loadListMsg) {
        this.loadListMsg = loadListMsg;
    }
    
    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public void setInput(Input input) {
        this.input = input;
    }
    
    @Override
    public boolean isSkipValidation() {
        return true;
    }
    
    @Override
    public String getChosenInput() {
        return chosenInput;
    }

    @Override
    public void setChosenInput(String chosenInput) {
        this.chosenInput = chosenInput;
    }
    
    @Override
    public void inputchange(){
        for(OntoRepresentation rep: input.getRepresentations()) {
            if(rep.getKey().equals(chosenInput)) {
                this.input.setChosen(rep);
            }
        }
    }
    
   public void setMessages(MessagesInterface messages) {
        this.messages = messages;
    }
   
       public AwsBeanInterface getAwsbean() {
        return awsbean;
    }

    public void setAwsbean(AwsBeanInterface awsbean) {
        this.awsbean = awsbean;
    }

    @Override
    public boolean supportsStreamedInput() {
        return false;
    }
  
    @Override
    public void register(String functionid) {
            // doesn't need to regiser
    }
    @Override
    public void unregister(String functionid) {
            // doesn't need to regiser
    }
  
    /*
     * ######### abstract methods ############
     */

    /**
     * Return Id of input represented by this InputBean.
     *
     * @return Return ID of input
     */
     public abstract String getId();
}