package org.fosshub.oauth.api;

import org.fosshub.oauth.exception.OAuthException;
import org.fosshub.oauth.http.OAuthResponse;

public interface OAuth2{

    /**
     * <p>
     *     method used to generate the authorization URL for the desired social network
     * </p>
     * @return encoded authorized Url as {@link String}
     * @throws OAuthException if URL Encode is not supported
     */
    String getAuthorizationUrl() throws OAuthException;

    String getRequestToken();

    /**
     * <p>
     *     method used to get the access token by providing the request token retrieved.
     *     access token will be used to access the protected resource.
     * </p>
     * @param requestToken as {@link String}
     * @return instance of {@link OAuthResponse} with encapsulated response parameters (including access token)
     * @throws OAuthException if any exception occurs
     */
    OAuthResponse getAccessToken(String requestToken) throws OAuthException;

    OAuthResponse getProtectedResource(String accessToken);
}

