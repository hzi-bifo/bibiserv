/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2013 BiBiServ Team]"
 *
 * Contributor(s): Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *
 */
package de.unibi.cebitec.bibiserv.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.ExternalContext;
import org.apache.log4j.Logger;

/**
 * Small Utility class providing some methods that provides cookie handling.
 * 
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class Cookie {

    private static Logger log = Logger.getLogger(Cookie.class);

    /**
     * Set Cookie named 'name' with value 'value' and lifetime 'maxage' in seconds.
     * 
     * @param ec - ExternalContext
     * @param name - Name of cookie
     * @param value - Value of cookie
     * @param maxAge - lifetime of cookie measured in seconds
     */
    public static void setCookie(ExternalContext ec, String name, String value, int maxAge) {
        HashMap<String, Object> hm = new HashMap();
        hm.put("path", "/");
        hm.put("maxAge", maxAge);
        ec.addResponseCookie(name, value, hm);
    }

    /**
     * Set a cookie named 'name' with value 'value' and properties.
     * 
     * 
     * @param ex - ExternalContext
     * @param name - Name of cookie
     * @param value - value of cookie
     * @param properties - cookie properties - @see http://docs.oracle.com/javaee/6/api/javax/servlet/http/Cookie.html
     */
    public static void setCookie(ExternalContext ec, String name, String value, HashMap<String,Object> properties) {
        ec.addResponseCookie(name, value, properties);
    }
    
    /**
     * Returns a cookie named 'key' from ExternalContext or null if no one exists.
     * 
     * @param ec
     * @param key
     * @return 
     */
    public static javax.servlet.http.Cookie getCookie(ExternalContext ec, String key) {
        Map<String, Object> cookiemap = ec.getRequestCookieMap();
        if (cookiemap != null && !cookiemap.isEmpty()) {
            return (javax.servlet.http.Cookie) cookiemap.get(key);
        }
        return null;
    }

    /**
     * Append a bibiservid to a cookie named 'bibiservids'.   
     * 
     * @param ec
     * @param bibiservid
     * @param expires - lifetime of bibiservid in seconds
     */
    public static void addBiBiServId(ExternalContext ec, String bibiservid, Integer expires) {
        addBiBiServId(ec, bibiservid, expires, null);
    }

    /**
     * Append a bibiservid to a cookie named 'bibiservids'.   
     * 
     * @param ec
     * @param bibiservid
     * @param expires - lifetime of bibiservid in seconds
     * @param limit - max. number id stored in the cookie.
     */
    public static void addBiBiServId(ExternalContext ec, String bibiservid, Integer expires, Integer limit) {
        Map<String, Long> map = parseBiBiServIdsCookie(getCookie(ec, "bibiservids"));

        // check if number of elements 
        if (limit != null && limit > 0 && limit < map.size()) {
            
            // sort all keys in descendign order
            List<String> l = new ArrayList();
            for (String k : map.keySet()) {
                l.add(k);
            }
            Collections.sort(l, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return -o1.compareTo(o2);
                }
            });
            // limits elements - remove oldest elements first      
            for (int c = limit; c < l.size(); c++){
                map.remove(l.get(c));
            }
        }
        // add current Id
        map.put(bibiservid, System.currentTimeMillis() + expires * 1000);
        // update cookie
        setCookie(ec, "bibiservids", generateBiBiServIdsCookieValue(map), expires);
    }

    
    /**
     * Parse the special structure of the bibiservcookie to Map, where the key is a bibiservid and the value is EOL timestamp in ms.
     * 
     * @param cookie - Cookie object which value is structured like "<String>::<Long>,<String>::<Long>,...
     * @return 
     */
    public static Map<String, Long> parseBiBiServIdsCookie(javax.servlet.http.Cookie cookie) {
        Map<String, Long> map = new HashMap();
        // get current time in ms
        long time = System.currentTimeMillis();
        if (cookie != null) {
            for (String kv : cookie.getValue().split(",")) {
                String[] t = kv.split("::");
                if (Long.parseLong(t[1]) > time) { // remove outdated id's
                    map.put(t[0], Long.parseLong(t[1]));

                } else {
                    log.debug("ignore outdated id");
                }
            }
        } else {
            log.debug("No Cookie named named bibiservsid exists ...");
        }
        return map;

    }

    /**
     * Generates a structured string from the given map "k1::v1,k2::v2,k3::v3,.."
     * 
     * @param map
     * @return 
     */
    public static String generateBiBiServIdsCookieValue(Map<String, Long> map) {
        StringBuilder sb = new StringBuilder();
        for (String id : map.keySet()) {
            sb.append(id).append("::").append(map.get(id)).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
