package org.fosshub.oauth.http;

import java.util.Map;

public class OAuthResponse {

    private int responseCode;
    private Map<String,String> responseParameters;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, String> getResponseParameters() {
        return responseParameters;
    }

    public void setResponseParameters(Map<String, String> responseParameters) {
        this.responseParameters = responseParameters;
    }
}
