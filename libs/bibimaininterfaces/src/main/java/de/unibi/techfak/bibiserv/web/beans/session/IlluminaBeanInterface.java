
package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.basespace.exception.BaseSpaceException;
import de.unibi.cebitec.bibiserv.basespace.json.AppResult;
import de.unibi.cebitec.bibiserv.basespace.json.File;
import de.unibi.cebitec.bibiserv.basespace.json.Project;
import de.unibi.cebitec.bibiserv.basespace.json.Run;
import de.unibi.cebitec.bibiserv.basespace.json.Sample;
import java.util.List;

/**
 * The interface for the Illumina Bean
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public interface IlluminaBeanInterface {

    public boolean authenticate(IlluminaInputBeanInterface in, String fctId);

    public void basicAuthentification();

    public String getDownloadUrl(String fileid) throws BaseSpaceException;

    public List<AppResult> listAppresults(String projectid) throws BaseSpaceException;

    public List<File> listFilesAppresults(String appresultid, int limit, int offset) throws BaseSpaceException;

    public List<File> listFilesRun(String runid, int limit, int offset) throws BaseSpaceException;

    public List<File> listFilesSample(String sampleid, int limit, int offset) throws BaseSpaceException;

    public List<Project> listProjects() throws BaseSpaceException;

    public List<Run> listRuns() throws BaseSpaceException;

    public List<Sample> listSamples(String projectid) throws BaseSpaceException;

    public void registerInput(IlluminaInputBeanInterface input, String fctId);

    public void resetCache();

    public void unregisterInput(IlluminaInputBeanInterface input, String fctId);
    
    public RequestOpenUrlQueueBeanInterface getUrlQueueBean();
    
    public boolean isBasicAuthorization();
    
}
