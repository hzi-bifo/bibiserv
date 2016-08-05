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
public class Size {

    private String size;
    private int vcpu;
    private String ecu;
    private double memorygib;
    private String storagegb;
    private List<Valuecolumn> valuecolumns;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getVcpu() {
        return vcpu;
    }

    public void setVcpu(int vcpu) {
        this.vcpu = vcpu;
    }

    public double getMemorygib() {
        return memorygib;
    }

    public void setMemorygib(double memorygib) {
        this.memorygib = memorygib;
    }

    public String getStoragegb() {
        return storagegb;
    }

    public void setStoragegb(String storagegb) {
        this.storagegb = storagegb;
    }

    public String getEcu() {
        return ecu;
    }

    public void setEcu(String ecu) {
        this.ecu = ecu;
    }

    public List<Valuecolumn> getValuecolumns() {
        return valuecolumns;
    }

    public void setValuecolumns(List<Valuecolumn> valuecolumns) {
        this.valuecolumns = valuecolumns;
    }
}
