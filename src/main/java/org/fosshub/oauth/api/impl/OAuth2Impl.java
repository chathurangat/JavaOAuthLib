package org.fosshub.oauth.api.impl;

import org.fosshub.oauth.api.OAuth2;
import org.fosshub.oauth.config.OAuthConfiguration;

public abstract class OAuth2Impl implements OAuth2 {

    private OAuthConfiguration oauthConfiguration;

    public OAuthConfiguration getOAuthConfiguration() {
        return oauthConfiguration;
    }

    public void setOAuthConfiguration(OAuthConfiguration oauthConfiguration) {
        this.oauthConfiguration = oauthConfiguration;
    }
}
