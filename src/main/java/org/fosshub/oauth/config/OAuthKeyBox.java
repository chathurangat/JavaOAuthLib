package org.fosshub.oauth.config;

public final class OAuthKeyBox {

    /**
     * <p>
     *  constructor visibility should be private because the class
     *  contains only the static constants.
     * </p>
     */
    private OAuthKeyBox(){

    }

    //oauth related keys
    public static final String CODE = "code";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String GRANT_TYPE = "grant_type";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String REQUEST_TOKEN = "request_token";
    public static final String ACCESS_TOKEN = "access_token";

    //http related keys
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final int HTTP_OK = 200;

    //encoding keys
    public static final String MD5 = "MD5";
    public static final String URL_ENCODE = "UTF-8";

    //facebook related keys
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String USERNAME = "username";
    public static final String GENDER = "gender";
    public static final String HOME_TOWN = "hometown";
    public static final String LOCATION = "location";

}

