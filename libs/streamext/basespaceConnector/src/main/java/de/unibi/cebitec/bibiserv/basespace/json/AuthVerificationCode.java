package de.unibi.cebitec.bibiserv.basespace.json;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 *
 * @author tgatter
 *
 */
public class AuthVerificationCode {

    @JsonProperty("verification_uri")
    private String verificationUri;

    public String getVerificationUri() {
        return verificationUri;
    }

    public void setVerificationUri(String verificationUri) {
        this.verificationUri = verificationUri;
    }
    
    @JsonProperty("verification_with_code_uri")
    private String verificationWithCodeUri;

    public String getVerificationWithCodeUri() {
        return verificationWithCodeUri;
    }

    public void setVerificationWithCodeUri(String verificationWithCodeUri) {
        this.verificationWithCodeUri = verificationWithCodeUri;
    }
    
    @JsonProperty("expires_in")
    private int expiresIn;

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    @JsonProperty("user_code")
    private String userCode;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    @JsonProperty("device_code")
    private String deviceCode;

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
    
    @JsonProperty("interval")
    private int interval;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "AuthVerificationCode [verificationUri=" + verificationUri + ", verificationWithCodeUri="
                + verificationWithCodeUri + ", expiresIn=" + expiresIn + ", userCode=" + userCode + ", deviceCode="
                + deviceCode + ", interval=" + interval + "]";
    }
}
