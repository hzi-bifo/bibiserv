/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.basespace;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Used to create access scopes for baseSpace
 * 
 * @author tgatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class Scope {
    
    private boolean browseGlobal = false;
    private Set<String> runs = new HashSet<>();
    private Set<String> samples = new HashSet<>();
    private Set<String> appresults = new HashSet<>();
    private Set<String> projects = new HashSet<>();
  

    public void addBrowseGlobal() {
        browseGlobal = true;
    }
    
    public void removeBrowseGlobal(){
        browseGlobal = false;
    }

    public void addReadRun(String id) {
        runs.add(id);
    }
    
    public void delReadRun(String id) {
        runs.remove(id);
    }
    
    public void addReadSample(String id) {
        samples.add(id);
    }
    
    public void delReadSample(String id) {
        samples.remove(id);
    }
    
    public void addReadAppresult(String id) {
        appresults.add(id);
    }
    
    public void delReadAppresult(String id) {
        appresults.remove(id);
    }
  
    public void addReadProject(String id) {
        projects.add(id);
    }
    
    public void delReadProject(String id) {
        projects.remove(id);
    }
     
     
     public String buildScopeString()  {
           
        StringBuilder builder = new StringBuilder();
        
        if(browseGlobal) {
            addSeperator(builder);
            builder.append("browse global");
        }
        
        for(String id: runs ){
            addSeperator(builder);
            builder.append("read run ").append(id);
        }
        
        for(String id: samples ){
            addSeperator(builder);
            builder.append("read sample ").append(id);
        }
        
        for(String id: appresults ){
            addSeperator(builder);
            builder.append("read appresult ").append(id);
        }
        
        for(String id: projects ){
            addSeperator(builder);
            builder.append("read project ").append(id);
        }
       
         return builder.toString();
     }
     
     private void addSeperator(StringBuilder builder) {
        if(builder.length()!=0) {
            builder.append(", ");
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.browseGlobal ? 1 : 0);
        hash = 17 * hash + Objects.hashCode(this.runs);
        hash = 17 * hash + Objects.hashCode(this.samples);
        hash = 17 * hash + Objects.hashCode(this.appresults);
        hash = 17 * hash + Objects.hashCode(this.projects);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Scope other = (Scope) obj;
        if (this.browseGlobal != other.browseGlobal) {
            return false;
        }
        if (!Objects.equals(this.runs, other.runs)) {
            return false;
        }
        if (!Objects.equals(this.samples, other.samples)) {
            return false;
        }
        if (!Objects.equals(this.appresults, other.appresults)) {
            return false;
        }
        if (!Objects.equals(this.projects, other.projects)) {
            return false;
        }
        return true;
    }
    
    public Scope copy(){
        Scope copy = new Scope();
        copy.browseGlobal = this.browseGlobal;
        copy.appresults.addAll(this.appresults);
        copy.runs.addAll(this.runs);
        copy.samples.addAll(this.samples);
        copy.projects.addAll(this.projects);
        return copy;
    }
     
}
