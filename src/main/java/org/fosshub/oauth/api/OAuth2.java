package org.fosshub.oauth.api;

import org.fosshub.oauth.exception.OAuthException;
import org.fosshub.oauth.http.OAuthResponse;

import javax.servlet.http.HttpServletRequest;

public interface OAuth2{
    /**
     * <p>
     *     method used to generate the authorization URL for the desired social network
     * </p>
     * @return encoded authorized Url as {@link String}
     * @throws OAuthException if URL Encode is not supported
     */
    String getAuthorizationUrl() throws OAuthException;

    OAuthResponse getRequestToken(HttpServletRequest request) throws OAuthException;

    /**
     * <p>
     *     method used to get the access token by providing the request token response retrieved.
     * </p>
     * @param requestTokenResponse as {@link OAuthResponse}
     * @return instance of {@link OAuthResponse} with encapsulated response parameters (including access token)
     * @throws OAuthException if any exception occurs
     */
    OAuthResponse getAccessToken(OAuthResponse requestTokenResponse) throws OAuthException;

    /**
     * <p>
     *     method used to get the access token by providing the request token retrieved.
     *     this method was implemented to get the access token by providing only the request token as String param
     * </p>
     * @param requestToken as {@link String}
     * @return instance of {@link OAuthResponse} with encapsulated response parameters (including access token)
     * @throws OAuthException if any exception occurs
     */
    OAuthResponse getAccessTokenForRequestToken(String requestToken) throws OAuthException;

    OAuthResponse getProtectedResource(OAuthResponse accessTokenResponse) throws OAuthException;
}

