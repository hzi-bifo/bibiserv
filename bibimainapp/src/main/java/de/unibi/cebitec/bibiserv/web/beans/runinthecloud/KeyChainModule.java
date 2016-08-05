/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud;

import de.unibi.cebitec.bibiserv.web.beans.session.AwsBean;
import de.unibi.cebitec.bibiserv.web.beans.session.UserBean;
import de.unibi.techfak.bibiserv.BiBiTools;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class KeyChainModule implements InitializingBean {

    private static final Logger log = Logger.getLogger(KeyChainModule.class);

    private DatabaseConnect dc;
    private UserBean user;

    /**
     * Upload/Storing variables.
     */
    private UploadedFile fileToUpload;
    private String keypairName;
    private String region;
    private List<String> availableRegions;

    private AwsBean awsbean;

    private boolean active;
    private String uniqueFolderID;

    private List<SshKeyPair> sshKeys;
    private List<UniqueFolderID> uniqueFolderIDs;

    private SshKeyPair selectedKeyPair;
    private UniqueFolderID selectedUniqueFolderID;

    private StringBuilder result = new StringBuilder();
    private boolean activeStreaming = false;
    private boolean finishedStreaming = false;

    public KeyChainModule() {

    }

    /**
     * Store the created keypair to the db.
     */
    public void saveSshKey() {
        /**
         * We should set all other sshFiles to not-active, if the new keypair is
         * set to active.
         */
        if (!sshKeys.isEmpty() && active) {
            for (SshKeyPair ssh : sshKeys) {
                ssh.setActive(false);
                dc.updateSshKey(ssh);
            }
        }
        FacesMessage m;
        int k;
        switch (k = dc.insertSshKey(user.getId(), keypairName, region, fileToUpload, active)) {
            case 0:
                m = new FacesMessage("Keypair successfuly saved in KeyChain!");
                FacesContext.getCurrentInstance().addMessage(null, m);
                break;
            case 555:
                m = new FacesMessage("Keypair successfuly updated in KeyChain!");
                FacesContext.getCurrentInstance().addMessage(null, m);
                break;
            case -1:
                m = new FacesMessage("FATAL Error...");
                FacesContext.getCurrentInstance().addMessage(null, m);
                break;
            default:
                m = new FacesMessage("Error while saving keypair in KeyChain!", "ErrorCode: " + k);
                FacesContext.getCurrentInstance().addMessage(null, m);
                break;
        }
        sshKeys = dc.retrieveSshKeyFile(user);

    }

    /**
     * Possibility to reconnect to an running instance.
     */
    public void connectToInstance() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(selectedUniqueFolderID.getRedirect());
        } catch (IOException iox) {
            log.error(iox.getMessage(), iox);
        }
    }

    /**
     * Active scrolling of the stop-grid-dialog.
     */
    public void scroll() {
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.execute("PF('scroller').scrollY(1000)");
    }

    /**
     * stopCluster() stops the running cluster in the Cloud.
     */
    public void stopCluster() {

        String clusterID = this.selectedUniqueFolderID.getClusterid();
        final String uniqueFolderId = this.selectedUniqueFolderID.getUniquefolderid();
        String rootpath = BiBiTools.getProperties().getProperty("tmpdir.base") + "/";
        String uniqueFolderPath = rootpath + uniqueFolderId;
        String bibigridPath = BiBiTools.getProperties().getProperty("bibigrid.bin");

        File UFI = new File(uniqueFolderPath);
        File bibiGrid = new File(bibigridPath);

        final FacesContext context = FacesContext.getCurrentInstance();
        final String username = user.getId();

        try {
            Runtime r = Runtime.getRuntime();
            /**
             * Execute the bibigrid script located in
             * bibiserv/bibidomain/bin/bibigrid/ from inside the users unique
             * folder id located at bibiserv/spool/tmp/ritc_xxxxxxxxx
             */
            final Process p = r.exec("/bin/bash " + bibigridPath + "/bibigrid -o bibigrid.properties -t " + clusterID, new String[]{"CLASSPATH=" + bibigridPath + ""}, UFI.getAbsoluteFile());
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    activeStreaming = true;
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String read;
                        /**
                         * Write read script output to result. result gets
                         * called from the JSF page and automatically rendered,
                         * too.
                         */
                        while ((read = br.readLine()) != null) {
                            result.append(read).append("<br />");
                        }
                        finishedStreaming = true;
                        activeStreaming = false;
                        /**
                         * Store cluster_active=false to the db.
                         */
                        dc.updateUniqueFolderID(username, uniqueFolderId, false);

                    } catch (IOException iox) {
                        log.error(iox.getMessage(), iox);
                        context.addMessage(null, new FacesMessage("Failure", "Stopping Grid fatal failed!"));
                    }
                }
            });
            t.start();

        } catch (IOException iox) {
            log.error(iox.getMessage(), iox);
        }
    }

    /**
     * Reset the output panel result.
     */
    public void resetStream() {
        activeStreaming = false;
        finishedStreaming = false;
        result.delete(0, result.length());
    }

    /**
     * Updates an existing keypair to the db.
     */
    public void updateSshKey() {
        FacesMessage m;
        int k;
        switch (k = dc.updateSshKey(selectedKeyPair)) {
            case 0:
                m = new FacesMessage("Keypair successfuly updated");
                FacesContext.getCurrentInstance().addMessage(null, m);
                break;
            default:
                m = new FacesMessage("Error while updating keypair!", "Problem: " + k);
                FacesContext.getCurrentInstance().addMessage(null, m);
                break;
        }
        sshKeys = dc.retrieveSshKeyFile(user);
        /**
         * if the updated keypair is now active, set all other keypairs to
         * inactive.
         */
        if (selectedKeyPair.isActive()) {
            for (SshKeyPair ssh : sshKeys) {
                if (!ssh.getSetDate().equals(selectedKeyPair.getSetDate())) {
                    ssh.setActive(false);
                    dc.updateSshKey(ssh);
                }
            }
        }
        /**
         * reload all keypairs.
         */
        sshKeys = dc.retrieveSshKeyFile(user);
    }

    /**
     * delete an existing keypair.
     */
    public void deleteSshKey() {
        FacesMessage m;
        int k;
        switch (k = dc.deleteSshKey(selectedKeyPair)) {
            case 0:
                m = new FacesMessage("Keypair successfuly deleted");
                FacesContext.getCurrentInstance().addMessage(null, m);
                break;
            default:
                m = new FacesMessage("Error while deleting keypair!", "Problem: " + k);
                FacesContext.getCurrentInstance().addMessage(null, m);
                break;
        }
        /**
         * reload keypairs.
         */
        sshKeys = dc.retrieveSshKeyFile(user);
    }

    public StringBuilder getResult() {
        return result;
    }

    public void setResult(StringBuilder result) {
        this.result = result;
    }

    public void updateUFI() {
        uniqueFolderIDs = dc.retrieveUniqueFolderID(user);
    }

    public UploadedFile getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(UploadedFile fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public String getUniqueFolderID() {
        return uniqueFolderID;
    }

    public void setUniqueFolderID(String uniqueFolderID) {
        this.uniqueFolderID = uniqueFolderID;
    }

    public DatabaseConnect getDc() {
        return dc;
    }

    public SshKeyPair getSelectedKeyPair() {
        return selectedKeyPair;
    }

    public void setSelectedKeyPair(SshKeyPair selectedKeyPair) {
        this.selectedKeyPair = selectedKeyPair;
    }

    public void setDc(DatabaseConnect dc) {
        this.dc = dc;
    }

    public List<SshKeyPair> getSshKeys() {
        return sshKeys;
    }

    public void setSshKeys(List<SshKeyPair> sshKeys) {
        this.sshKeys = sshKeys;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<UniqueFolderID> getUniqueFolderIDs() {
        return uniqueFolderIDs = dc.retrieveUniqueFolderID(user);
    }

    public void setUniqueFolderIDs(List<UniqueFolderID> uniqueFolderIDs) {
        this.uniqueFolderIDs = uniqueFolderIDs;
    }

    public UniqueFolderID getSelectedUniqueFolderID() {
        return selectedUniqueFolderID;
    }

    public void setSelectedUniqueFolderID(UniqueFolderID selectedUniqueFolderID) {
        this.selectedUniqueFolderID = selectedUniqueFolderID;
    }

    public boolean isActiveStreaming() {
        return activeStreaming;
    }

    public void setActiveStreaming(boolean activeStreaming) {
        this.activeStreaming = activeStreaming;
    }

    public boolean isFinishedStreaming() {
        return finishedStreaming;
    }

    public void setFinishedStreaming(boolean finishedStreaming) {
        this.finishedStreaming = finishedStreaming;
    }

    public String getKeypairName() {
        return keypairName;
    }

    public void setKeypairName(String keypairName) {
        this.keypairName = keypairName;
    }

    public AwsBean getAwsbean() {
        return awsbean;
    }

    public void setAwsbean(AwsBean awsbean) {
        this.awsbean = awsbean;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<String> getAvailableRegions() {
        return availableRegions;
    }

    public void setAvailableRegions(List<String> availableRegions) {
        this.availableRegions = availableRegions;
    }

    /**
     * stringToList converts a comma-seperated list of n elements to an
     * arraylist with n elements.
     *
     * @param s - Comma separated list
     * @return ArrayList
     */
    private ArrayList stringToList(String s) {
        ArrayList<String> a = new ArrayList<>();
        String[] elements = s.trim().split(",");
        a.addAll(Arrays.asList(elements));
        return a;
    }
    
    public void reloadLists() {
        uniqueFolderIDs = dc.retrieveUniqueFolderID(user);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        active = false;
        sshKeys = dc.retrieveSshKeyFile(user);
        /**
         * Receive all UFIs who got an jobid != null! This allows that only used
         * UFIs will get displayed.
         */
        uniqueFolderIDs = dc.retrieveUniqueFolderID(user);
        final URL propFileDest = new URL("http://bibiserv.cebitec.uni-bielefeld.de/resources/ritc.properties");
        Properties prop_file = new Properties();

        prop_file.load(propFileDest.openStream());

        /**
         * Get all working regions.
         */
        availableRegions = stringToList(prop_file.getProperty("regions"));
    }

}
