package org.fosshub.oauth.api;

import org.fosshub.oauth.exception.OAuthException;
import org.fosshub.oauth.http.OAuthResponse;

public interface OAuth2{

    /**
     * <p>
     *     method used to generate the authorization URL for the desired social network
     * </p>
     * @return encoded authorized Url as {@link String}
     * @throws OAuthException ifRL Encode is not supported
     */
    String getAuthorizationUrl() throws OAuthException;

//    String getRequestToken();

    OAuthResponse getAccessToken(String requestToken) throws OAuthException;

    OAuthResponse getProtectedResource(String accessToken);
}

