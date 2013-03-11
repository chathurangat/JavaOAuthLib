package org.fosshub.oauth.exception;
//todo  implement error handler
public final class OAuthErrorCode {
    /**
     * <p>
     *     this class cannot be instantiated
     * </p>
     */
    private OAuthErrorCode(){

    }
    public static final String INVALID_HTTP_REQUEST = "invalid_http_request";
    public static final String ACCESS_TOKEN_NOT_RECEIVED = "access_token_not_received";
    public static final String INVALID_REQUEST_URI = "invalid_request_uri";
    public static final String PROTECTED_RESOURCE_NOT_FOUND = "protected_resource_not_found";
    public static final String INVALID_OAUTH_RESPONSE = "invalid_oauth_response";
    // provided token type is invalid. neither request token or access token
    public static final String INVALID_TOKEN_TYPE = "invalid_token_type";
    // either request or access token is missing in the response parameters
    public static final String TOKEN_MISSING = "token_missing";
}
