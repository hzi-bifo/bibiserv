
package de.unibi.cebitec.bibiserv.basespace.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * data of a file
 * @author tgatter
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class File extends BaseSpaceObject
{
    @JsonProperty("ContentType")
    private String contentType;
    public String getContentType()
    {
        return contentType;
    }
    protected void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    @JsonProperty("Size")
    private Long size;
    
    public Long getSize()
    {
        return size;
    }
    protected void setSize(Long size)
    {
        this.size = size;
    }
    
    @JsonProperty("Path")
    private String path;

    public String getPath()
    {
        return path;
    }
    protected void setPath(String path)
    {
        this.path = path;
    }
    
    @JsonProperty("OriginalScope")
    private String originalScope;
    public String getOriginalScope()
    {
        return originalScope;
    }
    public void setOriginalScope(String originalScope)
    {
        this.originalScope = originalScope;
    }
    
    @JsonProperty("HrefVariants")
    private String hrefVariants;
    public String getHrefVariants()
    {
        return hrefVariants;
    }
    protected void setHrefVariants(String hrefVariants)
    {
        this.hrefVariants = hrefVariants;
    }
    
    @JsonProperty("HrefCoverage")
    private String hrefCoverage;
    public String getHrefCoverage()
    {
        return hrefCoverage;
    }
    public void setHrefCoverage(String hrefCoverage)
    {
        this.hrefCoverage = hrefCoverage;
    }

    @JsonProperty("HrefContent")
    private String hrefContent;
    public String getHrefContent()
    {
        return hrefContent;
    }
    protected void setHrefContent(String hrefContent)
    {
        this.hrefContent = hrefContent;
    }
    
    @JsonProperty("UploadStatus")
    private String uploadStatus;
    public String getUploadStatus()
    {
        return uploadStatus;
    }
    public void setUploadStatus(String uploadStatus)
    {
        this.uploadStatus = uploadStatus;
    }

    
    @Override
    public String toString()
    {
        return "File [contentType=" + contentType + ", size=" + size + ", path=" + path + ", toString()="
                + super.toString() + "]";
    }

    @Override
    public String getTypeTokenString() {
        return "File";
    }


    
    
    
    
    
}
