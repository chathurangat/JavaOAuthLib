package org.fosshub.oauth.http;

public enum OAuthResponseCode {

    OAUTH_RESPONSE_SUCCESS("success");

    private String code;

    private OAuthResponseCode(String code){
     this.code=code;
    }

    public String getCode() {
        return code;
    }
}
