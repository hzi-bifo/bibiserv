/**
* Copyright 2012 Illumina
* 
 * Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*    http://www.apache.org/licenses/LICENSE-2.0
* 
 *  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/

package de.unibi.cebitec.bibiserv.basespace.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * A BaseSpace run
 * @author tgatter
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Run extends BaseSpaceObject
{
    @JsonProperty("ExperimentName")
    private String experimentName;
    public String getExperimentName()
    {
        return experimentName;
    }
    protected void setExperimentName(String experimentName)
    {
        this.experimentName = experimentName;
    }
    @Override
    public String getName()
    {
        return getExperimentName();
    }
    @Override
    public String toString()
    {
        return "Run [experimentName=" + experimentName + ", toString()=" + super.toString() + "]";
    }

    @Override
    public String getTypeTokenString() {
        return "Run";
    }
    
    
}
