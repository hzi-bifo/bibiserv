
package de.unibi.cebitec.bibiserv.basespace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.cebitec.bibiserv.basespace.exception.BaseSpaceException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.representation.Form;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import de.unibi.cebitec.bibiserv.basespace.exception.ForbiddenException;
import de.unibi.cebitec.bibiserv.basespace.json.AccessToken;
import de.unibi.cebitec.bibiserv.basespace.json.AppResult;
import de.unibi.cebitec.bibiserv.basespace.json.AuthVerificationCode;
import de.unibi.cebitec.bibiserv.basespace.json.BaseSpaceObject;
import de.unibi.cebitec.bibiserv.basespace.json.File;
import de.unibi.cebitec.bibiserv.basespace.json.FileContent;
import de.unibi.cebitec.bibiserv.basespace.json.Project;
import de.unibi.cebitec.bibiserv.basespace.json.Run;
import de.unibi.cebitec.bibiserv.basespace.json.Sample;

/**
 *
 * @author gatter
 */
public class BaseSpaceConnection implements BaseSpaceConnectionInterface {
    
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    private String accessToken;
    private Scope scope;

    public BaseSpaceConnection() {
        accessToken = "";
        scope=null;
    }
    
    //*****************************************************//
    //                 Authentification                    //
    //****************************************************//
    
    
    /**
     * Tries to authenticate using the given scope
     * @param scope The BaseSpace access scope, for example "read run [id], read appresult [id]" 
     */
    @Override
    public void authenticate(Scope scope, BrowserLaunchInterface launch) throws BaseSpaceException{
        
        if(scope== null) {
            throw new BaseSpaceException("BaseSpace access-scope cannot be null.");
        }
        
        if(scope.equals(this.scope)) { // only request new if different scope
            return;
        }
        
        this.scope = scope.copy();
        accessToken = requestAccessToken(scope.buildScopeString(), launch);
    }
    
    private String requestAccessToken(String scope, BrowserLaunchInterface launch) throws BaseSpaceException{

        try {
            Form form = new Form();
            form.add("client_id", BaseSpaceConfiguration.getClientId());
            form.add("scope", scope);
            form.add("response_type", "device_code");

            Client client = Client.create(new DefaultClientConfig());
            client.addFilter(new ClientFilter() {
                @Override
                public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
                    ClientResponse response = null;
                    try {
                        response = getNext().handle(request);
                    } catch (ClientHandlerException t) {
                        throw new BaseSpaceException(request.getURI().toString());
                    }
                    return response;
                }
            });

            WebResource resource = client.resource(UriBuilder.fromUri(BaseSpaceConfiguration.getApiRootUri())
                    .path(BaseSpaceConfiguration.getVersion())
                    .path(BaseSpaceConfiguration.getAuthorizationUriFragment())
                    .build());

            ClientResponse response = resource.accept(
                    MediaType.APPLICATION_XHTML_XML,
                    MediaType.APPLICATION_FORM_URLENCODED,
                    MediaType.APPLICATION_JSON)
                    .post(ClientResponse.class, form);
            String responseAsJSONString = response.getEntity(String.class);

            AuthVerificationCode authCode = mapper.readValue(responseAsJSONString, AuthVerificationCode.class);

            String uri = authCode.getVerificationWithCodeUri();
            launch.openURL(uri);

            //Poll for approval
            form = new Form();
            form.add("client_id", BaseSpaceConfiguration.getClientId());
            form.add("client_secret", BaseSpaceConfiguration.getClientSecret());
            form.add("code", authCode.getDeviceCode());
            form.add("grant_type", "device");

            resource = client.resource(UriBuilder.fromUri(BaseSpaceConfiguration.getApiRootUri())
                    .path(BaseSpaceConfiguration.getVersion())
                    .path(BaseSpaceConfiguration.getAccessTokenUriFragment())
                    .build());

            String accessToken = null;
            while (accessToken == null) {
                long interval = authCode.getInterval() * 1000;
                Thread.sleep(interval);
                response = resource.accept(
                        MediaType.APPLICATION_XHTML_XML,
                        MediaType.APPLICATION_FORM_URLENCODED,
                        MediaType.APPLICATION_JSON)
                        .post(ClientResponse.class, form);

                responseAsJSONString = response.getEntity(String.class);
                switch (response.getClientResponseStatus()) {
                    case BAD_REQUEST:

                        AccessToken token = mapper.readValue(responseAsJSONString, AccessToken.class);
                        if (token.getError().equalsIgnoreCase("access_denied")) {
                            throw new BaseSpaceException();
                        }
                        break;
                    case OK:
                        token = mapper.readValue(responseAsJSONString, AccessToken.class);
                        accessToken = token.getAccessToken();
                }
            }
            return accessToken;

        } catch (Throwable t) {
            throw new BaseSpaceException("Error requesting access token from BaseSpace: " + t.getMessage());
        }
    }
    
    
    //*****************************************************//
    //                 Listing                            //
    //****************************************************//
    
    @Override
    public List<Run> listRuns() throws BaseSpaceException{
        return (List) getList("/users/current/runs", Run.class, 1000, 0, "Id");
    }
    
    @Override
    public List<Project> listProjects() throws BaseSpaceException{
        return (List) getList("/users/current/projects", Project.class, 1000, 0, "Name");
    }
    
    @Override
    public List<Sample> listSamples(String projectid) throws BaseSpaceException{
        return (List) getList("/projects/"+projectid+"/samples", Sample.class, 1000, 0, "Name");
    }
    
    @Override
    public List<AppResult> listAppresults(String projectid) throws BaseSpaceException{
        return (List) getList("/projects/"+projectid+"/appresults", AppResult.class, 1000, 0, "Name");
    }
    
    @Override
    public List<File> listFilesRun(String runid, int limit, int offset) throws BaseSpaceException{
        return (List) getList("/runs/"+runid+"/files", File.class, limit, offset, "Path");
    }
    
    @Override
    public List<File> listFilesSample(String sampleid, int limit, int offset) throws BaseSpaceException{
        return (List) getList("/samples/"+sampleid+"/files", File.class, limit, offset, "Path");
    }
    
    @Override
    public List<File> listFilesAppresults(String appresultid, int limit, int offset) throws BaseSpaceException{
        return (List) getList("/appresults/"+appresultid+"/files", File.class, limit, offset, "Path");
    }
    
    private List<BaseSpaceObject> getList(String taskUrl, Class<? extends BaseSpaceObject> objectClass, int limit, int offset, String sort) throws BaseSpaceException {
        try {
            List<BaseSpaceObject> ret = new ArrayList<BaseSpaceObject>();

            // get server response
            String response = getRootApiWebResource()
                    .path(taskUrl)
                    .queryParam("Limit", Integer.toString(limit))
                    .queryParam("Offset", Integer.toString(offset))
                    .queryParam("SortBy", sort)
                    .accept(MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_JSON)
                    .get(String.class);

            JsonNode responseNode = mapper.readValue(response, JsonNode.class).findPath("Items");
            for (int i = 0; i < responseNode.size(); i++) {
                BaseSpaceObject obj = mapper.readValue(responseNode.get(i).toString(), objectClass);
                ret.add(obj);
            }
            return ret;
        } catch (Throwable t) {
            throw new BaseSpaceException(t.getMessage());
        }
    }
    
    //*****************************************************//
    //                 Get Download-Url                    //
    //****************************************************//
    
    @Override
    public String getDownloadUrl(String fileid) throws BaseSpaceException{
        try {
            String response = getRootApiWebResource()
                        .path("/files/"+fileid+"/content")
                        .queryParam("redirect", "meta")
                        .accept(MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_JSON)
                        .get(String.class);

            JsonNode responseNode = mapper.readValue(response, JsonNode.class).findPath("Response");
            FileContent ob = mapper.readValue(responseNode.toString(), FileContent.class);
           
            return ob.getUrl();
        } catch (Throwable t) {
            throw new BaseSpaceException(t.getMessage());
        }
    }
    
    
    //*****************************************************//
    //                 Client-Helper                      //
    //****************************************************//
    
    
    private WebResource getRootApiWebResource()
    {
        return getClient().resource( UriBuilder.fromUri(BaseSpaceConfiguration.getApiRootUri()).path(BaseSpaceConfiguration.getVersion()).build());
    }
    
    private Client client;
    private Client getClient() {
        if (client != null) {
            return client;
        }
        return client = createClient();
    }

    private Client createClient() {

        Client client = Client.create(new DefaultClientConfig());
        
        client.setReadTimeout(BaseSpaceConfiguration.getReadTimeout());
        client.setConnectTimeout(BaseSpaceConfiguration.getConnectionTimeout());
        
        client.addFilter(new ClientFilter() {
            @Override
            public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
                if (accessToken != null) {
                    request.getHeaders().add("x-access-token", accessToken);
                }
                ClientResponse response = null;
                try {
                    response = getNext().handle(request);
                    switch (response.getClientResponseStatus()) {
                        case FORBIDDEN:
                            throw new ForbiddenException("Access to resource denied.");
                    }
                } catch (ClientHandlerException t) {
                    throw new BaseSpaceException(request.getURI().toString(),t);
                }
                return response;
            }
        });
        return client;
    }

   
}
