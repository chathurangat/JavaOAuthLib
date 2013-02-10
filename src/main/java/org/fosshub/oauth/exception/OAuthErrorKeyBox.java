package org.fosshub.oauth.exception;

public final class OAuthErrorKeyBox {

    /**
     * <p>
     *  constructor visibility should be private because the class
     *  contains only the static constants.
     * </p>
     */
    private OAuthErrorKeyBox(){

    }

    public static final String INVALID_REQUEST_URI = "invalid_request_uri";
    public static final String ACCESS_TOKEN_NOT_RECEIVED = "access_token_not_received";
    public static final String PROTECTED_RESOURCE_NOT_FOUND = "protected_resource_not_found";
}
