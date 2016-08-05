/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.basespace;

import de.unibi.cebitec.bibiserv.basespace.exception.BaseSpaceException;
import de.unibi.cebitec.bibiserv.basespace.json.AppResult;
import de.unibi.cebitec.bibiserv.basespace.json.File;
import de.unibi.cebitec.bibiserv.basespace.json.Project;
import de.unibi.cebitec.bibiserv.basespace.json.Run;
import de.unibi.cebitec.bibiserv.basespace.json.Sample;
import java.util.List;

/**
 *
 * @author gatter
 */
public interface BaseSpaceConnectionInterface {

    /**
     * Tries to authenticate using the given scope
     * @param scope The BaseSpace access scope, for example "read run [id], read appresult [id]"
     * @param launch The object that handles launching the url.
     */
    public void authenticate(Scope scope, BrowserLaunchInterface launch) throws BaseSpaceException;

    //*****************************************************//
    //                 Get Download-Url                    //
    //****************************************************//
    public String getDownloadUrl(String fileid) throws BaseSpaceException;

    public List<AppResult> listAppresults(String projectid) throws BaseSpaceException;

    public List<File> listFilesAppresults(String appresultid, int limit, int offset) throws BaseSpaceException;

    public List<File> listFilesRun(String runid, int limit, int offset) throws BaseSpaceException;

    public List<File> listFilesSample(String sampleid, int limit, int offset) throws BaseSpaceException;

    public List<Project> listProjects() throws BaseSpaceException;

    //*****************************************************//
    //                 Listing                            //
    //****************************************************//
    public List<Run> listRuns() throws BaseSpaceException;

    public List<Sample> listSamples(String projectid) throws BaseSpaceException;
    
}
