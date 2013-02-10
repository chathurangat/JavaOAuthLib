package org.fosshub.oauth.http;

import java.util.Map;

public class OAuthResponse {

    private OAuthResponseCode responseCode;
    private Map<Object,Object> responseParameters;
    private int httpResponseCode;

    public OAuthResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(OAuthResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public Map<Object, Object> getResponseParameters() {
        return responseParameters;
    }

    public void setResponseParameters(Map<Object, Object> responseParameters) {
        this.responseParameters = responseParameters;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }
}
