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
public class UniqueFolderID {

    private UserBean user;
    private String uniquefolderid;
    private String clusterid;
    private String redirect;
    private boolean active;
    private Timestamp setDate;

    public UniqueFolderID(UserBean user,
            String uniquefolderid,
            String clusterId,
            String redirect,
            boolean active,
            Timestamp setDate) {
        this.user = user;
        this.uniquefolderid = uniquefolderid;
        this.clusterid = clusterId;
        this.redirect = redirect;
        this.active = active;
        this.setDate = setDate;
    }

    public UserBean getUser() {
        return user;
    }

    public String getUsername() {
        return user.getId();
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getUniquefolderid() {
        return uniquefolderid;
    }

    public void setUniquefolderid(String uniquefolderid) {
        this.uniquefolderid = uniquefolderid;
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

    public String getClusterid() {
        return clusterid;
    }

    public void setClusterid(String clusterid) {
        this.clusterid = clusterid;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

}
