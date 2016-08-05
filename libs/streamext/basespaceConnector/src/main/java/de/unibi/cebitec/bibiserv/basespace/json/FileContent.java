
package de.unibi.cebitec.bibiserv.basespace.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * Url of file content.
 * @author tgatter
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileContent
{
    @JsonProperty("HrefContent")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
   
    @JsonProperty("SupportsRange")
    private boolean supportsRange;

    public boolean isSupportsRange() {
        return supportsRange;
    }

    public void setSupportsRange(boolean supportsRange) {
        this.supportsRange = supportsRange;
    }


    @Override
    public String toString()
    {
        return "File content [url=" + url + ", range=" + supportsRange + "]";
    }   
}
