/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud.jsonInstancesUpdater.mappingPOJOs;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class Version {
    
    private double vers;
    private Config config;

    public double getVers() {
        return vers;
    }

    public void setVers(double vers) {
        this.vers = vers;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
    
    
    
}
