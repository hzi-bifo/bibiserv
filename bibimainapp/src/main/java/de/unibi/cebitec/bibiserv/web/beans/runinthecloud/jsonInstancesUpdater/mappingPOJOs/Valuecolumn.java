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

public class Valuecolumn {
    
    private String name;
    private Price prices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Price getPrices() {
        return prices;
    }

    public void setPrices(Price prices) {
        this.prices = prices;
    }

}
