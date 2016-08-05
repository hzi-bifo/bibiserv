/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted 2014 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */

package de.unibi.cebitec.bibiserv.statistics.logging;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import java.net.InetAddress;
import com.maxmind.geoip2.model.CountryResponse;
import de.unibi.techfak.bibiserv.BiBiTools;
import java.io.IOException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * BeanWrapper for GeoIP.  Make sure that GeoIP init and dispose 
 * work out of box. Provide one public method to analyze an given IP and return 
 * a CountryResponse object.
 * 
 * @ToDo:
 *  - support md5 generation and comparision to check data integrity
 *  - update database in defined period of time (e.g. once a month
* 
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class GeoIP implements InitializingBean, DisposableBean{
    
    private final static Logger log = Logger.getLogger(GeoIP.class.getName());
   
    private DatabaseReader reader;
    
    
    /**
     * Return a CountryResponse object
     * 
     * @param ia
     * @return
     * @throws IOException
     * @throws GeoIp2Exception 
     */
    public CountryResponse getCountryResponse(InetAddress ia) throws IOException, GeoIp2Exception{
        if (reader == null){
            throw new GeoIp2Exception("Database object is not initialized!");
        }
        return reader.country(ia);
    }
    
    /**
     * Return a two letter country code for given IP Address
     * 
     * @param ia InetAddress
     * 
     * @return Return a two letter country in a case of success, a "??" in case the IP-Address is not found in database
     * and a "!!" in case of an error. See log file for a stacktrace.
     */
    public String getCountry(InetAddress ia) {
        try {
            CountryResponse cr = getCountryResponse(ia);
            return cr.getCountry().getIsoCode();
        } catch (AddressNotFoundException e){
            return "??";
        } catch (IOException | GeoIp2Exception e) {
            log.info(e.getMessage(), e);
            return "!!";
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // get URL from BiBiTools Properties or use geolite.maxmind as fallback
        URL geoip_url = new URL(BiBiTools.getProperties().getProperty("GeoIP2.database.url","http://geolite.maxmind.com/download/geoip/database/GeoLite2-Country.mmdb.gz"));
        // get databasereader from URL
        reader = new DatabaseReader.Builder(new GZIPInputStream(geoip_url.openStream())).build();
    }

    @Override
    public void destroy() throws Exception {
        reader.close();
    }
    
    
    
}
