
package de.unibi.cebitec.bibiserv.basespace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.Date;


/**
 * The basic data of all Illumina objects.
 * @author bking
 *
 */
public abstract class BaseSpaceObject
{

    @JsonProperty("Id")
    private String Id;
   
    /**
     * Get the unique id associated with this entity
     * @return the id
     */
    public String getId()
    {
        return Id;
    }
    protected void setId(String id)
    {
        Id = id;
    }
    
    @JsonProperty("Href")
    private URI href;
    /**
     * Get the Href associated with this entity
     * @return the Href
     */
    public URI getHref()
    {
        return href;
    }
    protected void setHref(URI href)
    {
        this.href = href;
    }
    
    
    @JsonProperty("Name")
    private String name;
    /**
     * Get the name associated with this entity
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    protected void setName(String name)
    {
        this.name = name;
    }
    
    @JsonProperty("DateCreated")
    private Date dateCreated;

    /**
     * Get the date this entity was created
     * @return the create date
     */
    public Date getDateCreated()
    {
        return dateCreated;
    }
    protected void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }
    
    @JsonProperty("Status")
    private String status;
    public String getStatus()
    {
        return status;
    }
    protected void setStatus(String status)
    {
        this.status = status;
    }
    
    @JsonProperty("StatusSummary")
    private String statusSummary;
    public String getStatusSummary()
    {
        return statusSummary;
    }
    public void setStatusSummary(String statusSummary)
    {
        this.statusSummary = statusSummary;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((Id == null) ? 0 : Id.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BaseSpaceObject other = (BaseSpaceObject) obj;
        if (Id == null)
        {
            if (other.Id != null) return false;
        }
        else if (!Id.equals(other.Id)) return false;
        return true;
    }
    
    @Override
    public String toString()
    {
        return "BaseSpaceEntity [Id=" + Id + ", href=" + href + ", name=" + name + ", dateCreated=" + dateCreated
                + ", status=" + status + ", statusSummary=" + statusSummary + "]";
    }
    
    
    //************* Abstract ******************//


    public abstract String getTypeTokenString();


}
