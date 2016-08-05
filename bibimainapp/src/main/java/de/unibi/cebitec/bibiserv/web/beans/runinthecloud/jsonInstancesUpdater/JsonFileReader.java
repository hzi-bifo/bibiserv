/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud.jsonInstancesUpdater;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.cebitec.bibiserv.web.beans.runinthecloud.jsonInstancesUpdater.mappingPOJOs.Version;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author jsteiner
 */
public class JsonFileReader {

    
    /**
     * Parses static url http://a0.awsstatic.com/pricing/1/ec2/linux-od.min.js
     * @return Version object representing json objects.
     * @throws MalformedURLException
     * @throws IOException
     */
    public static Version parse() throws MalformedURLException, IOException {
        final URL awspricing = new URL(
                "http://a0.awsstatic.com/pricing/1/ec2/linux-od.min.js");
        JsonFormatReader jfr = new JsonFormatReader(
                new InputStreamReader(awspricing.openStream()));
        jfr.read();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jfr.getFinalString(), Version.class);
    }
    

    /**
     * Parses user specific url path.
     * @return Version object representing json objects.
     *
     * @param path - A alternative URL to parse from
     * @throws MalformedURLException
     * @throws IOException
     */
    public static Version parse(final String path) throws MalformedURLException, IOException {
        URL awspricing;
        if (path.isEmpty()) {
            awspricing = new URL(
                    "http://a0.awsstatic.com/pricing/1/ec2/linux-od.min.js");
        } else {
            awspricing = new URL(path);
        }
        JsonFormatReader jfr = new JsonFormatReader(
                new InputStreamReader(awspricing.openStream()));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jfr.getFinalString(), Version.class);
    }
}
