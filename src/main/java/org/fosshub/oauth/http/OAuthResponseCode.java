package org.fosshub.oauth.http;

public enum OAuthResponseCode {

    OAUTH_RESPONSE_SUCCESS(200),
    OAUTH_RESPONSE_ERROR(300);

    private int code;

    private OAuthResponseCode(int code){
     this.code=code;
    }

    public int getCode() {
        return code;
    }
}
