package org.fosshub.oauth.api;

import org.fosshub.oauth.exception.OAuthException;
import org.fosshub.oauth.http.OAuthResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *     implementation class for the OAuth2 protocol.
 *
 * </p>
 */
public interface OAuth2{
    /**
     * <p>
     *     method used to generate the authorization URL for the desired social network
     * </p>
     * @return encoded authorized Url as {@link String}
     * @throws OAuthException if URL Encode is not supported
     */
    String getAuthorizationUrl() throws OAuthException;

    /**
     * <p>
     *     method to get the request token from the http servlet request
     * </p>
     * @param request as {@link HttpServletRequest}
     * @return  instance of {@link OAuthResponse} encapsulated with the request token
     * @throws OAuthException
     */
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

    /**
     * <p>
     *     accessing the resource data by providing the access token response received
     * </p>
     * @param accessTokenResponse as {@link OAuthResponse}
     * @return instance of {@link OAuthResponse} with encapsulated response parameters (including all the protected resource data requested)
     * @throws OAuthException if any exception occurs
     */
    OAuthResponse getProtectedResource(OAuthResponse accessTokenResponse) throws OAuthException;
}

