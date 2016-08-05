/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class Ec2Instance {

    private String instanceName;
    private int vCPU;
    private double memory;
    private String storage;
    private String ecu;
    private String price;
    private String region;

    public Ec2Instance() {

    }

    /**
     * Create new Ec2-Instance-Type.
     *
     * @param instanceType
     * @param vCPU
     * @param memory
     * @param storage
     * @param ecu
     * @param price
     * @param region
     */
    public Ec2Instance(String instanceType,
            int vCPU,
            double memory,
            String storage,
            String ecu,
            String price,
            String region) {
        this.instanceName = instanceType;
        this.vCPU = vCPU;
        this.memory = memory;
        this.storage = storage;
        this.ecu = ecu;
        this.price = price;
        this.region = region;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public int getvCPU() {
        return vCPU;
    }

    public void setvCPU(int vCPU) {
        this.vCPU = vCPU;
    }

    public double getMemory() {
        return memory;
    }

    public void setMemory(double memory) {
        this.memory = memory;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getEcu() {
        return ecu;
    }

    public void setEcu(String ecu) {
        this.ecu = ecu;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}
