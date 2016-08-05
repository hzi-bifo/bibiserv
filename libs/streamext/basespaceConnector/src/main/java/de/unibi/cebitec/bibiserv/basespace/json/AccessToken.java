
package de.unibi.cebitec.bibiserv.basespace.json;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * 
 * @author tgatter
 *
 */
public class AccessToken
{
    @JsonProperty("access_token")
    private String accessToken;

    public String getAccessToken()
    {
        return accessToken;
    }
    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    @JsonProperty("expires_in")
    private int expiresIn;
    public int getExpiresIn()
    {
        return expiresIn;
    }
    public void setExpiresIn(int expiresIn)
    {
        this.expiresIn = expiresIn;
    }

    @JsonProperty("error")
    private String error;
    public String getError()
    {
        return error;
    }
    public void setError(String error)
    {
        this.error = error;
    }
    
    @JsonProperty("error_description")
    private String errorDescription;
    public String getErrorDescription()
    {
        return errorDescription;
    }
    public void setErrorDescription(String errorDescription)
    {
        this.errorDescription = errorDescription;
    }
    @Override
    public String toString()
    {
        return "AccessToken [accessToken=" + accessToken + ", expiresIn=" + expiresIn + ", error=" + error
                + ", errorDescription=" + errorDescription + "]";
    }
    
    
    

    
    
    

    
    
    
}
