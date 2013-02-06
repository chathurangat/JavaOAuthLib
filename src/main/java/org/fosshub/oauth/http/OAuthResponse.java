package org.fosshub.oauth.http;

import java.util.Map;

public class OAuthResponse {

    private int responseCode;
    private Map<Object,Object> responseParameters;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Map<Object, Object> getResponseParameters() {
        return responseParameters;
    }

    public void setResponseParameters(Map<Object, Object> responseParameters) {
        this.responseParameters = responseParameters;
    }
}
