package org.fosshub.oauth.api;

import org.fosshub.oauth.http.OAuthResponse;

public interface OAuth2 {

    String getAuthorizationUrl();

    String getRequestToken();

    OAuthResponse getAccessToken(String requestToken);

    OAuthResponse getProtectedResource(String accessToken);
}

