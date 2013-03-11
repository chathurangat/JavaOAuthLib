package org.fosshub.oauth.provider;

import org.fosshub.oauth.api.impl.OAuth2Impl;
import org.fosshub.oauth.config.OAuthConfiguration;
import org.fosshub.oauth.exception.OAuthException;
import org.fosshub.oauth.http.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static org.fosshub.oauth.config.OAuthKeyBox.CODE;
import static org.fosshub.oauth.config.OAuthKeyBox.REQUEST_TOKEN;
import static org.fosshub.oauth.config.OAuthKeyBox.URL_ENCODE;
import static org.fosshub.oauth.exception.OAuthErrorCode.INVALID_HTTP_REQUEST;
import static org.fosshub.oauth.exception.OAuthErrorCode.INVALID_REQUEST_URI;

public class GoogleProvider extends OAuth2Impl{

    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookProvider.class);

    private static final String REQUEST_TOKEN_ENDPOINT = "https://accounts.google.com/o/oauth2/auth?response_type=%s&client_id=%s&redirect_uri=%s&state=%s&scope=%s";
    private static final String ACCESS_TOKEN_ENDPOINT = "https://accounts.google.com/o/oauth2/token";
    private static final String PROTECTED_RESOURCE_ENDPOINT = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=%s";

    private Map<Object,Object> responseParamMap  = new HashMap<Object, Object>();
    private OAuthConfiguration oAuthConfiguration;

    public GoogleProvider(OAuthConfiguration oAuthConfiguration){
        this.oAuthConfiguration = oAuthConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthorizationUrl() throws OAuthException {
        try {
            return String.format(REQUEST_TOKEN_ENDPOINT, URLEncoder.encode(CODE, URL_ENCODE) ,
                    URLEncoder.encode(oAuthConfiguration.getApplicationId(), URL_ENCODE),
                    URLEncoder.encode(oAuthConfiguration.getRedirectUrl(), URL_ENCODE),
                    URLEncoder.encode(oAuthConfiguration.getState(), URL_ENCODE),
                    URLEncoder.encode(oAuthConfiguration.getScope(), URL_ENCODE));
        } catch (UnsupportedEncodingException ex) {
            LOGGER.debug("URL Encoder [{}] is not supported ", URL_ENCODE);
            throw new OAuthException(String.format("URL Encode [%s] is not supported", URL_ENCODE),ex);
        }
    }

    @Override
    public OAuthResponse getRequestToken(HttpServletRequest request) throws OAuthException{
        OAuthResponse oAuthResponse =  new OAuthResponse();
        if(request!=null){
            if(request.getParameterMap().containsKey(CODE)){
                LOGGER.info(" request token was found ");
                responseParamMap.put(REQUEST_TOKEN,request.getParameter(CODE));
                oAuthResponse.setResponseParameters(responseParamMap);
            }
            else if(request.getParameterMap().containsKey("error")){
                String error = request.getParameter("error");
                LOGGER.info(" request token was not received due to error [{}]", error);
                throw new OAuthException(error," request token was not received due to error ["+error+"]");
            }
            else{
                LOGGER.info(" either request token or error code parameters are missing in HTTP GET request URL ");
                throw new OAuthException(INVALID_REQUEST_URI," either request token or error code parameters are missing in HTTP GET request URL");
            }
            return oAuthResponse;
        }
        else{
            LOGGER.info(" HTTP request is null ");
            throw new OAuthException(INVALID_HTTP_REQUEST,"HTTP Request is null");
        }
    }

    @Override
    public OAuthResponse getAccessToken(OAuthResponse requestTokenResponse) throws OAuthException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OAuthResponse getAccessTokenForRequestToken(String requestToken) throws OAuthException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OAuthResponse getProtectedResource(OAuthResponse accessTokenResponse) throws OAuthException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map<Object, Object> getResponseParamMap() {
        return responseParamMap;
    }

    public void setResponseParamMap(Map<Object, Object> responseParamMap) {
        this.responseParamMap = responseParamMap;
    }

    public OAuthConfiguration getoAuthConfiguration() {
        return oAuthConfiguration;
    }

    public void setoAuthConfiguration(OAuthConfiguration oAuthConfiguration) {
        this.oAuthConfiguration = oAuthConfiguration;
    }
}
