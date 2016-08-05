
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.basespace.BaseSpaceConnection;
import de.unibi.cebitec.bibiserv.basespace.Scope;
import de.unibi.cebitec.bibiserv.basespace.exception.BaseSpaceException;
import de.unibi.cebitec.bibiserv.basespace.json.AppResult;
import de.unibi.cebitec.bibiserv.basespace.json.File;
import de.unibi.cebitec.bibiserv.basespace.json.Project;
import de.unibi.cebitec.bibiserv.basespace.json.Run;
import de.unibi.cebitec.bibiserv.basespace.json.Sample;
import de.unibi.techfak.bibiserv.web.beans.session.IlluminaBeanInterface;
import de.unibi.techfak.bibiserv.web.beans.session.IlluminaInputBeanInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
/**
 *
 * @author Thomas Gatter - tgatter(at)techfak.uni-bielefeld.de
 */
public class IlluminaBean  implements InitializingBean, IlluminaBeanInterface {

    // logger for messages
     private static final Logger log = Logger.getLogger(IlluminaBean.class);
     private Map<String, Map<String,IlluminaInputBeanInterface>> registeredinputs;
     
     // basespaceconnection
     private BaseSpaceConnection connection;
     
     // open url hack
     private RequestOpenUrlQueueBean urlQueueBean;
     
     // caches for illumina data
     private Map<String,List<AppResult>> appresults;
     private Map<String,List<Sample>> samples;
     
     
     private List<Project> projects;
     private List<Run> runs;
     
     // is basic authorization set
     private boolean basicAuth = false;
     
    @Override
    public void afterPropertiesSet() throws Exception {
        registeredinputs = new HashMap<>();
        connection = new BaseSpaceConnection();
        resetCache();
    }
    
    @Override
    public void resetCache() {
        appresults = new HashMap<>();
        projects = null;
        runs = null;
        samples = new HashMap<>();

    }

    
    @Override
    public void registerInput(IlluminaInputBeanInterface input, String fctId) {
        String id = input.getId();
        
        Map<String,IlluminaInputBeanInterface> inputsInFunction;
        if(registeredinputs.containsKey(fctId)){
            inputsInFunction = registeredinputs.get(fctId);
        } else {
            inputsInFunction = new HashMap<>();
        }
        inputsInFunction.put(id, input);
        registeredinputs.put(fctId, inputsInFunction);
    }
    
    @Override
    public void unregisterInput(IlluminaInputBeanInterface input, String fctId) {
        String id = input.getId();

        Map<String,IlluminaInputBeanInterface> inputsInFunction;
        
        if(!registeredinputs.containsKey(fctId)){
            return;
        } 
        inputsInFunction = registeredinputs.get(fctId);
        if(!inputsInFunction.containsKey(id)) {
            return;
        }
        
        inputsInFunction.remove(id);
        
        if(inputsInFunction.isEmpty()) { // remove from toplist if no more entries
            registeredinputs.remove(fctId);
        }
    }
    
    @Override
     public boolean authenticate(IlluminaInputBeanInterface in, String fctId) throws BaseSpaceException{

        Map<String, IlluminaInputBeanInterface> inputsInFunction;
  
        if (!registeredinputs.containsKey(fctId)) {
            return false;
        }
        inputsInFunction = registeredinputs.get(fctId);

        Scope scope = new Scope();
        scope.addBrowseGlobal();
        for (IlluminaInputBeanInterface input : inputsInFunction.values()) {
            if (!input.addToScope(scope)) {
                return false;
            }
        }

        connection.authenticate(scope, urlQueueBean);
        
        basicAuth = true;
        return true;
    }
     
    @Override
     public void basicAuthentification(){
        Scope scope = new Scope();
        scope.addBrowseGlobal();
        connection.authenticate(scope, urlQueueBean);
        basicAuth = true;
     }


    @Override
    public String getDownloadUrl(String fileid) throws BaseSpaceException {
        return connection.getDownloadUrl(fileid);
    }

    @Override
    public List<AppResult> listAppresults(String projectid) throws BaseSpaceException {
        if(!appresults.containsKey(projectid)) {
            appresults.put(projectid, connection.listAppresults(projectid));
        }
        return appresults.get(projectid);
    }

    @Override
    public List<File> listFilesAppresults(String appresultid, int limit, int offset) throws BaseSpaceException {
        return connection.listFilesAppresults(appresultid, limit, offset);
    }

    @Override
    public List<File> listFilesRun(String runid, int limit, int offset) throws BaseSpaceException {
        return connection.listFilesRun(runid, limit, offset);
    }

    @Override
    public List<File> listFilesSample(String sampleid, int limit, int offset) throws BaseSpaceException {
        return connection.listFilesSample(sampleid, limit, offset);
    }

    @Override
    public List<Project> listProjects() throws BaseSpaceException {
        if (projects == null) {
            projects = connection.listProjects();
        }
        return projects;
    }

    @Override
    public List<Run> listRuns() throws BaseSpaceException {
        if (runs == null) {
            runs = connection.listRuns();
        }
        return runs;
    }

    @Override
    public List<Sample> listSamples(String projectid) throws BaseSpaceException {
        if(!samples.containsKey(projectid)) {
            samples.put(projectid, connection.listSamples(projectid));
        }
        return samples.get(projectid);
    }

    /* Setter and Getter for Other Beans */
    
    @Override
    public RequestOpenUrlQueueBean getUrlQueueBean() {
        return urlQueueBean;
    }

    public void setUrlQueueBean(RequestOpenUrlQueueBean urlQueueBean) {
        this.urlQueueBean = urlQueueBean;
    }

    @Override
    public boolean isBasicAuthorization() {
        return basicAuth;
    }
      
}
