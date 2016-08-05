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
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.util.streamvalidate.StreamValidator;
import de.unibi.cebitec.bibiserv.util.validate.ValidationException;
import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import de.unibi.cebitec.bibiserv.util.validate.Validator;
import de.unibi.cebitec.bibiserv.utils.UniversalRepresentationFinder;
import de.unibi.cebitec.bibiserv.utils.UniversalValidator;
import de.unibi.cebitec.bibiserv.utils.ValidationConnection;
import de.unibi.cebitec.bibiserv.utils.connect.ServerFileConnection;
import de.unibi.techfak.bibiserv.cms.Texample.Prop;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.web.beans.Input;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.InitializingBean;

/**
 * Declaration of an abstract InputBean.
 *
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractInputBean implements InitializingBean, InputBeanInterface {

    private final static Logger log = Logger.getLogger(AbstractInputBean.class);
    protected final static String br = System.getProperty("line.separator");
    protected final static int PREVIEW_LENGTH = 300;
    protected boolean help = false;
    protected boolean uploadvisible = true;
    protected boolean textareavisible = false;
    protected String selectedInput = "1";
    protected String selectedInput_old = "1";
    protected SelectItem[] selectedItems;
    protected String uploadMessage;
    protected UploadedFile upload;
    protected String textarea;
    protected boolean validated = false;
    protected boolean valid = false;
    protected Input input;
    protected boolean showInfo = false;
    protected MessagesInterface messages;
    protected boolean skipValidation;
    protected String chosenInput;

    // own Validator, that can be used instead of  Ontology given one
    protected Validator validator;
    protected ValidationResult validationResult;

    // extra stuff for server files for toolchaining
    protected String serverfile = "";
    protected boolean chainingServerFileVisible = false;
    protected String lastToolname = "";

    public void changelistener() {

        //only if selection changed we must do anything ...
        if (!selectedInput_old.equals(selectedInput)) {

            resetValidated();

            chainingServerFileVisible = false;
            if (selectedInput.equals("1")) {
                uploadvisible = true;
                textareavisible = false;
                //log.info("FileUpload Selected ...");
            } else if (selectedInput.equals("2")) {
                uploadvisible = false;
                textareavisible = true;
                //log.info("Copy&Paste Selected ...");
            }
        }
    }

    public void resetValidated() {
        validated = false;
        showInfo = false;
    }

    public void helpAction(ActionEvent e) {
        help = !help;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public void setInput(Input input) {
        this.input = input;
    }

    public String getSelectedInput() {
        return selectedInput;
    }

    public void setSelectedInput(String selectedInput) {
        //only if selection changed we must do anything ...
        this.selectedInput_old = this.selectedInput;
        this.selectedInput = selectedInput;
    }

    public SelectItem[] getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(SelectItem[] selectedItems) {
        this.selectedItems = selectedItems;
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

    public String getTextarea() {
        return textarea;
    }

    public void setTextarea(String textarea) {
        this.textarea = textarea;
    }

    public boolean isTextareavisible() {
        return textareavisible;
    }

    public void setTextareavisible(boolean textareavisible) {
        this.textareavisible = textareavisible;
    }

    public String getUploadMessage() {
        return uploadMessage;
    }

    public void setUploadMessage(String uploadMessage) {
        this.uploadMessage = uploadMessage;
    }

    public boolean isUpload() {
        return upload != null;
    }

    public boolean isUploadvisible() {
        return uploadvisible;
    }

    public void setUploadvisible(boolean uploadvisible) {
        this.uploadvisible = uploadvisible;
    }

    public String getServerfile() {
        return serverfile;
    }

    public boolean isChainingServerFileVisible() {
        return chainingServerFileVisible;
    }

    public String getLastToolname() {
        return lastToolname;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
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
    public boolean isSkipValidation() {
        return skipValidation;
    }

    public void setSkipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
    }

    /*
     * determine source and validate
     */
    @Override
    public void determineSourceAndValidate(String[] args, OntoRepresentation target, String functionid) {

        input = new Input();
        input.setSkipValidation(skipValidation);
        valid = false;
        // determine source
        if ("1".equals(selectedInput)) {
            // file upload
            log.info(" -> work with file upload");
            if (upload != null) {
                input.setSource(args[0] + " - " + upload.getFileName());
                try {
                    String tmp = readfromFile(upload.getInputstream());
                    if (input.isSkipValidation()) {
                        // skip enabled, no validation
                        input.setInput(tmp);
                        input.setRepresentations(new ArrayList<OntoRepresentation>());
                        input.getRepresentations().add(target);
                        input.setChosen(target);
                        valid = true;
                    } else {
                        valid = validate(tmp, args, target);
                    }
                    if (valid) {
                        // hide showInfo field
                        showInfo = false;
                        log.info("-> file upload is valid!");
                    } else {
                        log.info("-> file upload is invalid!");
                    }
                } catch (IOException e) {
                    log.fatal(e.getMessage());
                } catch (OutOfMemoryError memory) {
                    input.setRepresentations(new ArrayList<OntoRepresentation>());
                    input.setMessage("File too big for heap space. Please use stream functionality.");
                    valid = false;
                }
            } else {
                input.setSource(args[0]); //i18n
                input.setMessage(args[1]); //i18n
            }
        } else if ("2".equals(selectedInput)) {
            // textarea
            log.info(" -> work with textarea");
            if (textarea != null && textarea.length() > 0) {
                input.setSource(args[2]);

                if (input.isSkipValidation()) {
                    // skip enabled, no validation
                    input.setInput(textarea);
                    input.setRepresentations(new ArrayList<OntoRepresentation>());
                    input.getRepresentations().add(target);
                    input.setChosen(target);
                    valid = true;
                } else {
                    valid = validate(textarea, args, target);
                }
                if (valid) {
                    // hide showInfo field
                    showInfo = false;
                    log.info("-> texarea is valid!");
                } else {
                    log.info("-> textarea is invalid!");
                }
            } else {
                input.setSource(args[2]); //i18n
                input.setMessage(args[3]); //i18n
            }

        } else if (chainingServerFileVisible) {
            log.info(" -> work with server file");
            input.setSource("Server File - " + serverfile);
            ValidationConnection tmp = null;

            tmp = new ServerFileConnection(serverfile);
            File testFile = new File(serverfile);
            if (testFile.exists()) {

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
                    log.info("-> server file is valid!");
                } else {
                    log.info("-> server file is invalid!");
                    input.setSource("Server File -" + serverfile); //i18n
                    input.setMessage(messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.DATANOTVALID") + " " + input.getMessage()); //i18n
                }

            } else {
                input.setSource("Server File -" + serverfile); //i18n
                input.setMessage(args[3] + " " + messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.FILENOTFOUND")); //i18n
            }
        } else {
            log.fatal("selectInput contains an unexpected value !");
        }
        // set validated flag
        if (input.getChosen() != null) {
            chosenInput = input.getChosen().getKey();
        }
        validated = true;
    }

    /**
     * Upload preview. Return first $PREVIEW_LENGTH characters from uploaded
     * file.
     *
     * @return
     */
    public String getUploadPreview() {
        if (isUpload()) {
            try {
                return readPreviewFromFile(upload.getInputstream());
            } catch (IOException e) {
                log.fatal("Cannot read file", e);
            }
        }
        return null;
    }

    /**
     * Upload Action
     *
     * @param event
     */
    public void uploadAction(FileUploadEvent event) {

        log.debug("Started file Upload...");
        upload = event.getFile();
        uploadMessage = "File \"" + upload.getFileName() + "\" successfull uploaded!";
        resetValidated();
    }

    /**
     * DI - Messages properties
     */

    public void setMessages(MessagesInterface messages) {
        this.messages = messages;
    }

    /**
     * Implementation of Interface Initializing Bean
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        selectedItems = new SelectItem[]{
            new SelectItem("1", messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.UPLOAD")),
            new SelectItem("2", messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.COPYPASTE"))
        };

        input = new Input();

    }

    /**
     * Reset input bean
     */
    @Override
    public void reset() {
        validated = false;
        selectedInput = "1";
        uploadvisible = true;
        textareavisible = false;
        textarea = "";
        skipValidation = false;

        // too chaining stuff
        chainingServerFileVisible = false;
        serverfile = "";
        lastToolname = "";
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
        textarea = "";
        skipValidation = false;

        // too chaining stuff
        chainingServerFileVisible = true;
        serverfile = filename;
        this.lastToolname = lastToolname;
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
    public void inputchange() {
        for (OntoRepresentation rep : input.getRepresentations()) {
            if (rep.getKey().equals(chosenInput)) {
                this.input.setChosen(rep);
            }
        }
    }

        // Server File Downloads for server file extension
    public StreamedContent getServerFileAsDownload() {
        File file = new File(serverfile);

        InputStream in;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            in = new ByteArrayInputStream("DownloadError".getBytes());
        }
        return new DefaultStreamedContent(in, "text/plain", file.getName());
    }

    /*
     * ######### private helper methods ############
     */
    protected String readfromFile(InputStream stream) throws IOException {
        return readFromFile(stream, FileReading.COMPLETE);
    }

    protected String readPreviewFromFile(InputStream stream) throws IOException {
        return readFromFile(stream, FileReading.PREVIEW);
    }

    protected String readFromFile(InputStream stream, FileReading fileReading) throws IOException {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder out = new StringBuilder();
        fileReading.parseFile(out, bufferedreader);
        bufferedreader.close();
        return out.toString();
    }

    protected enum FileReading {

        COMPLETE() {

                    @Override
                    public String parseFile(StringBuilder out, BufferedReader bufferedreader) throws IOException {
                        String line;
                        while ((line = bufferedreader.readLine()) != null) {
                            out.append(line).append(br);
                        }
                        return out.toString();
                    }
                },
        PREVIEW() {

                    @Override
                    public String parseFile(StringBuilder out, BufferedReader bufferedreader) throws IOException {
                        String line;
                        while ((line = bufferedreader.readLine()) != null) {
                            if (out.length() > PREVIEW_LENGTH) {
                                out.setLength(PREVIEW_LENGTH);
                                break;
                            }
                            out.append(line).append(br);
                        }
                        return out.toString();
                    }
                };

        abstract String parseFile(StringBuilder out, BufferedReader bufferedreader) throws IOException;
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
     * Validate content and return true in the case of valid content
     *
     * @param content - Content as String (remark JK: should content be of type Object
 ? TG: There there few non-text-based Bioformats. Would be a general content
 problem for BibiServ. ToolDependend could be non-Sting
     * @param args -
     * @return Return true in case of valid content, false otherwise
     */
    public boolean validate(String content, String[] args, OntoRepresentation target) {

        /* Falls ein eigener/privater Validator fuer diese Klasse gesetzt wurde dann wird dieser benutzt. 
           Andernfalls wird die Ontology gefragt, letzeres sollte der Normalfall sein.
         */
        if (validator != null) {
            validationResult = validator.validateThis(content);
            input.setInput(content); // set content to input
            input.setMessage(validationResult.getMessage()); // set message returned by validator 
            input.setChosen(target); // should be ok since we use our own validator :-)
            List<OntoRepresentation> l = new ArrayList<>();
            l.add(target);
            input.setRepresentations(l);
        } else {
            List<OntoRepresentation> found = UniversalRepresentationFinder.getOntoRepresentation(content,
                    target, this, args);
            if (found.isEmpty()) {
                validationResult = new ValidationResult(false,"No validation information available!");
            } else {
                validationResult = new ValidationResult(false,"Input is valid!");
            }         
        }
        return validationResult.isValid();
    }

    /**
     * Validate content (of type ValidationConnection) and return true in the case of valid content
     *
     * @param content -Input as ValidationConnection 
     * @param args -
     * @param target - Target as OntoRepresentation
     * @return Return true in case of valid content, false otherwise
     */
    public boolean validate(ValidationConnection content, String[] args, OntoRepresentation target) {

        /* Falls ein eigener/privater Validator fuer die Klasse gestetzt wurde und dieser vom Typ
           StreamValidator ist, dann wird dieser benutzt. Im Normallfall wird die 
            Ontology gefragt. 
         */
        if ((validator != null) && (validator instanceof StreamValidator)) {
            try {
                validationResult = ((StreamValidator)validator).validateThis(content.getReader(), UniversalValidator.partValidationLength, content);
            } catch (ValidationException e){
                validationResult = new ValidationResult(false, "ValidationException : "+e.getMessage());
            }
            input.setInput(content);
            input.setMessage(validationResult.getMessage());
            input.setChosen(target); // should be ok since we use our own validator :-)
            List<OntoRepresentation> l = new ArrayList<>();
            l.add(target);
            input.setRepresentations(l);
        } else {
            List<OntoRepresentation> found = UniversalRepresentationFinder.getOntoRepresentation(content, target, this, args);
            if (found.isEmpty()) {
                validationResult = new ValidationResult(false,"No validation information available!");
            } else {
                validationResult = new ValidationResult(true,"Input is valid!");
            }       
        }
        return validationResult.isValid();
    }

    /**
     * Set external Validator that is used instead of "default" Validator returned by 
     * Ontology.
     * 
     * @param v - StreamValidator
     */
    @Override
    public void setValidator(Validator v) {
        this.validator = v;
    }

    /**
     * @return Returns external Validator if set, "null" otherwise.
     */
    @Override
    public Validator getValidator() {
        return validator;
    }
    
    /**
     * 
     * @return Return ValidationResult from previous Validation call. Attention could
     * be 'null' in the case 
     */
    @Override
    public ValidationResult getValidationResult() {
        return validationResult;         
    }
            
    /**
     * Return Id of input represented by this InputBean.
     *
     * @return Return ID of input
     */
    public abstract String getId();
}
