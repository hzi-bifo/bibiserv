/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted 2010 BiBiServ Curator Team"
 *
 * Contributor(s):
 *
 */
package de.unibi.techfak.bibiserv;

/**
 * Java representation of SQL table resource
 *
 * create table resources (
 *   id varchar(100),
 *   runs int,               -- concurrently possible runs
 *   cputime int,            -- used cputime during a period of time (one week), measured in minutes
 *   diskspace int,          -- used diskspace, measured in MB
 *   memory  int             -- used memory
 * );
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class Resource {

   
    private String id;
    /* parallel job runs */
    private int runs = 1;
    /* maximal cputime in minutes a job can consume */
    private int cputime = 30;
    /* max diskspace a user can occupied for long term storage of jobs/job results
       in MB, a negative value means no long term storage of jobs */
    private int diskspace = -1 ;
    /* max memory (in MB) a user can occupied per job, a negative value means no
       limitation */
    private int memory =  -1;
 

    public Resource(String id) {
        this.id = id;
    }

    public Resource(String id, int runs, int cputime, int diskspace, int memory) {
        this.id = id;
        this.runs = runs;
        this.cputime = cputime;
        this.diskspace = diskspace;
        this.memory = memory;
    }

    public int getCputime() {
        return cputime;
    }

    public void setCputime(int cputime) {
        this.cputime = cputime;
    }

    public int getDiskspace() {
        return diskspace;
    }

    public void setDiskspace(int diskspace) {
        this.diskspace = diskspace;
    }

    public String getId() {
        return id;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    @Override
    protected Object clone(){
        return new Resource(id,runs, cputime, diskspace, memory);
    }

    @Override
    public boolean equals(Object obj) {
        Resource resource = (Resource)obj;

        return id.equals(resource.id) && 
                runs == resource.runs &&
                cputime == cputime &&
                diskspace == diskspace &&
                memory == memory;

    }
}
