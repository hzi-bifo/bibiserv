/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud.jsonInstancesUpdater.mappingPOJOs;

import java.util.List;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class Instancetype {

    private String type;
    private List<Size> sizes;

    public Instancetype() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }
}
