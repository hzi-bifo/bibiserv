/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl;

import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiObjectProperty;
import java.util.Objects;

/**
 * Implements BiBiObjectProperty.
 * @author Thomas gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class BiBiObjectPropertyImplementation implements BiBiObjectProperty{

    private String key;
    private String label;
    
    
    public BiBiObjectPropertyImplementation(String key, String label) {
        this.key = key;
        this.label = label;
    }
    
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BiBiObjectPropertyImplementation other = (BiBiObjectPropertyImplementation) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.key);
        hash = 17 * hash + Objects.hashCode(this.label);
        return hash;
    }
}
