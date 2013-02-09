package org.fosshub.oauth.config;

import org.fosshub.oauth.util.OAuthUtil;

public class OAuthConfiguration {

    private String oauthProvider;
    private String applicationId;
    private String applicationSecret;
    private String redirectUrl;
    private String state;
    private String scope;

    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationSecret() {
        return applicationSecret;
    }

    public void setApplicationSecret(String applicationSecret) {
        this.applicationSecret = applicationSecret;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getState() {
        if(state==null){
            state = OAuthUtil.generateRandomString();
        }
        else{
            state = OAuthUtil.md5Encoder(state);
        }
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
