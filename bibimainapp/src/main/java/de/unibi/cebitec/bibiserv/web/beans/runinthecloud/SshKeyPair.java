/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud;

import de.unibi.cebitec.bibiserv.web.beans.session.UserBean;
import java.sql.Timestamp;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class SshKeyPair {

    private UserBean user;
    private String keypairName;
    private String region;
    private String identityFile;
    private int fileSize;
    private boolean active;
    private Timestamp setDate;

    public SshKeyPair() {

    }

    public SshKeyPair(UserBean user,
            String keypairName,
            String region,
            String identityfile,
            boolean active,
            Timestamp setDate,
            int filesize) {
        this.user = user;
        this.keypairName = keypairName;
        this.region = region;
        this.identityFile = identityfile;
        this.active = active;
        this.setDate = setDate;
        this.fileSize = filesize;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getUsername() {
        return user.getId();
    }

    public String getKeypairName() {
        return keypairName;
    }

    public void setKeypairName(String keypairName) {
        this.keypairName = keypairName;
    }

    public String getIdentityFile() {
        return identityFile;
    }

    public void setIdentityFile(String identityFile) {
        this.identityFile = identityFile;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getSetDate() {
        return setDate;
    }

    public void setSetDate(Timestamp setDate) {
        this.setDate = setDate;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}
