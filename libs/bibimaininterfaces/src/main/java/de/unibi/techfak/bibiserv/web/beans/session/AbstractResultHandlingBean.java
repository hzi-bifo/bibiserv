
package de.unibi.techfak.bibiserv.web.beans.session;

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.springframework.beans.factory.InitializingBean;
import javax.faces.model.SelectItem;

/**
 * This bean is responsible for saving the handling type of the result and managing
 * the toolresulthandling tag.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractResultHandlingBean implements InitializingBean {
    
    
    private List<SelectItem> resultItems;
    protected MessagesInterface messages;
    
    private boolean s3uploadVisible;
    private boolean downloadVisible;
    private boolean errorPageVisible;
    
    private String selected_item_bucket;
    private String loadListMsg;
    
    private AwsBeanInterface awsbean;
    
    private String subfolder;
    
      public void changelistener() {
      
        //only if selection changed we must do something ...
        if (!resultHandlingOld.equals(resultHandling)) {
            switch(resultHandling) {
                case s3upload:
                    s3uploadVisible = true;
                    downloadVisible = false;
                    errorPageVisible = false;
                    break;
                case spoolDirectory:
                    s3uploadVisible = false;
                    downloadVisible = true;
                    errorPageVisible = false;
                    break;
            }
        }
        resultHandlingOld = resultHandling;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reset();
    }
    
    public void reset() {
        resultItems = new ArrayList<SelectItem>();
        
        SelectItem download = new SelectItem(ResultHandling.spoolDirectory, messages.property("de.unibi.techfak.bibiserv.bibimainapp.result.DOWNLOAD"));
        download.setDisabled(isDownloadDisabled());
        resultItems.add(download);
        
        SelectItem upload = new SelectItem(ResultHandling.s3upload, messages.property("de.unibi.techfak.bibiserv.bibimainapp.result.UPLOAD"));
        upload.setDisabled(isUploadDisabled() || !awsbean.isAwsCredentialsSet());
        upload.setDescription(messages.property("de.unibi.techfak.bibiserv.bibimainapp.result.UPLOAD_DESCRIPTION"));
        resultItems.add(upload);
        
        if((isUploadDisabled() || !awsbean.isAwsCredentialsSet() ) && !isDownloadDisabled()){
            resultHandling = ResultHandling.spoolDirectory;
            resultHandlingOld = ResultHandling.spoolDirectory;
        
            s3uploadVisible = false;
            downloadVisible = true;
            errorPageVisible = false;
        } else if(isDownloadDisabled() && !(isUploadDisabled() || !awsbean.isAwsCredentialsSet() )){
            resultHandling = ResultHandling.s3upload;
            resultHandlingOld = ResultHandling.s3upload;
        
            s3uploadVisible = true;
            downloadVisible = false;
            errorPageVisible = false;
        } else {
            resultHandling = ResultHandling.none;
            resultHandlingOld = ResultHandling.none;

            s3uploadVisible = false;
            downloadVisible = false;
            errorPageVisible = true;
        }

        subfolder = "";
        selected_item_bucket = "";
    }
    
    public void getBucketList() {
        loadListMsg = awsbean.refresh();
    }
    
    /**
     * Validates if the current input is correct and tool can start.
     * @return true: input is correct
     */
    public boolean validate(){
        switch(resultHandling) {
            case spoolDirectory:
                // no input, nothign can be wrong!
                return true;
            case s3upload:
                if(selected_item_bucket.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(getId()+"_msg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error ", messages.property("de.unibi.techfak.bibiserv.bibimainapp.result.NO_BUCKET_EXCEPTION")));
                    return false;
                }
                if(!subfolder.isEmpty() && !awsbean.validateObjectName(subfolder)) {
                    FacesContext.getCurrentInstance().addMessage(getId()+"_msg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error ", messages.property("de.unibi.techfak.bibiserv.bibimainapp.result.INVALID_FOLDER_EXCEPTION")));
                    return false;
                }
                
                String testfile = subfolder;
                if(!testfile.endsWith("/")){
                    testfile += "/";
                }
                testfile += "bibiserv_test_upload_file";
                if(!awsbean.testUpload(selected_item_bucket, testfile)) {
                    FacesContext.getCurrentInstance().addMessage(getId()+"_msg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error ", messages.property("de.unibi.techfak.bibiserv.bibimainapp.result.UPLOADTEST_FAILED")+": "+selected_item_bucket+", "+testfile));
                    return false;
                }
                return true;
        }
        FacesContext.getCurrentInstance().addMessage(getId()+"_msg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error ", messages.property("de.unibi.techfak.bibiserv.bibimainapp.result.NOHANDLEREXCEPTION")));
        return false;
    }
    
    
    //######### resultHandling ##############
    
    protected ResultHandling resultHandling;
    
    // only for change event
    private ResultHandling resultHandlingOld;
    
    public enum ResultHandling {
        none,
        s3upload,
        spoolDirectory
    }

    public ResultHandling getResultHandling() {
        return resultHandling;
    }

    public void setResultHandling(ResultHandling resultHandling) {
        if(resultHandling==null){
            resultHandling = ResultHandling.none;
        }
        this.resultHandling = resultHandling;
    }
    

    //########## Getter and Setter ############

    public List<SelectItem> getResultItems() {
        return resultItems;
    }
    
    public void setMessages(MessagesInterface messages) {
        this.messages = messages;
    }

    public void setAwsbean(AwsBeanInterface awsbean) {
        this.awsbean = awsbean;
    }
    
    public boolean isS3uploadVisible() {
        return s3uploadVisible;
    }

    public boolean isDownloadVisible() {
        return downloadVisible;
    }

    public boolean isErrorPageVisible() {
        return errorPageVisible;
    }

    public String getLoadListMsg() {
        return loadListMsg;
    }

    public String getSelected_item_bucket() {
        return selected_item_bucket;
    }

    public void setSelected_item_bucket(String selected_item_bucket) {
        if(selected_item_bucket==null){
            selected_item_bucket="";
        }
        this.selected_item_bucket = selected_item_bucket;
    }

    public String getSubfolder() {
        return subfolder;
    }

    public void setSubfolder(String subfolder) {
        this.subfolder = subfolder;
    }
    
    public List<String> getItemlist_buckets() {
        return awsbean.getBuckets();
    }
    
    public boolean isAwsCredentialsSet() {
        return awsbean.isAwsCredentialsSet();
    }
    
    public String getAccessKey(){
        return awsbean.getAccessKey();
    }
    
    public String getSecretKey(){
        return awsbean.getSecretKey();
    }
    
     public String getSessionToken(){
        return awsbean.getSessionToken();
    }
    
    //########### interaction for request
    
    //########### Abstract ##############
    
     /**
     * Return Id of input represented by this InputBean.
     *
     * @return Return ID of input
     */
    public abstract String getId();
    
    
    /**
     * Return if the function to keep the file on the server is disabled.
     * @return 
     */
    public abstract boolean isDownloadDisabled();
    
    
     /**
     * Return if the function to upload the file to s3 is disabled.
     * @return 
     */
    public abstract boolean isUploadDisabled();
    
}
