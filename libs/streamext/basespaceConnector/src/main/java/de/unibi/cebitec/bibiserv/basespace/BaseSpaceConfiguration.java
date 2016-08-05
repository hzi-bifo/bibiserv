package de.unibi.cebitec.bibiserv.basespace;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gatter
 */
public  class  BaseSpaceConfiguration {
    
    public static String getVersion() {
        return "v1pre3"; // The current version of the API
    }


    public static String getApiRootUri() {
        return "https://api.basespace.illumina.com"; // The API server's URI
    }


    public static String getAccessTokenUriFragment() {
        return "/oauthv2/token"; // The API call to request an Access Token
    }


    public static String getAuthorizationUriFragment() {
        return "/oauthv2/deviceauthorization";  // The API call to request a device authorization code
    }

    public static String getClientId() {
        return "d29bae79489b40fb832ec545d618e4c3"; // Our client id from My Apps
    }


    public static String getClientSecret() {
        return "7b0465865d57402cb3e8737b2edd068a"; // Our client secret from My Apps
    }

    public static int getReadTimeout() {
        return 10000;
    }

    public static int getConnectionTimeout() {
        return 10000;
    }


}
