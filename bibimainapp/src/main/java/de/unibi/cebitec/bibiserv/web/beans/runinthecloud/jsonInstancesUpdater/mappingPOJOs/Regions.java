/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud.jsonInstancesUpdater.mappingPOJOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.unibi.cebitec.bibiserv.web.beans.runinthecloud.Ec2InstanceWizard;
import java.util.List;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class Regions {

    private String region;
    private List<Instancetype> instancetypes;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<Instancetype> getInstancetypes() {
        return instancetypes;
    }

    public void setInstancetypes(List<Instancetype> instancetypes) {
        this.instancetypes = instancetypes;
    }

}
