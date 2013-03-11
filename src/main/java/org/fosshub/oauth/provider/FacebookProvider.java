package org.fosshub.oauth.provider;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.fosshub.oauth.api.impl.OAuth2Impl;
import org.fosshub.oauth.config.OAuthConfiguration;
import org.fosshub.oauth.exception.OAuthException;
import org.fosshub.oauth.http.OAuthResponse;
import org.fosshub.oauth.util.OAuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import static org.fosshub.oauth.config.OAuthKeyBox.*;
import static org.fosshub.oauth.exception.OAuthErrorCode.*;

public class FacebookProvider extends OAuth2Impl {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookProvider.class);

    private static final String REQUEST_TOKEN_ENDPOINT = "https://www.facebook.com/dialog/oauth?response_type=%s&client_id=%s&redirect_uri=%s&state=%s";
    private static final String ACCESS_TOKEN_ENDPOINT = "https://graph.facebook.com/oauth/access_token";
    private static final String PROTECTED_RESOURCE_ENDPOINT = "https://graph.facebook.com/me?access_token=%s";

    private Map<Object,Object> responseParamMap  = new HashMap<Object, Object>();
    private OAuthConfiguration oAuthConfiguration;

    public FacebookProvider(OAuthConfiguration oAuthConfiguration){
        this.oAuthConfiguration = oAuthConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthorizationUrl() throws OAuthException{
        try {
            return String.format(REQUEST_TOKEN_ENDPOINT, URLEncoder.encode(CODE, URL_ENCODE) ,
                    URLEncoder.encode(oAuthConfiguration.getApplicationId(), URL_ENCODE),
                    URLEncoder.encode(oAuthConfiguration.getRedirectUrl(), URL_ENCODE),
                    URLEncoder.encode(oAuthConfiguration.getState(), URL_ENCODE));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public OAuthResponse getAccessToken(OAuthResponse requestTokenResponse) throws OAuthException{
        OAuthResponse oAuthResponse =  new OAuthResponse();
        //checking whether the valid oauth request token response
        if(isTokenResponseValid(requestTokenResponse,REQUEST_TOKEN)){
            try{
                URL u = new URL (ACCESS_TOKEN_ENDPOINT);
                HttpURLConnection con = (HttpURLConnection) u.openConnection ();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestMethod(HTTP_POST);

                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, URL_ENCODE));
                //setting up oauth request parameters
                Map<String,String> postParametersMap = new HashMap<String, String>();
                postParametersMap.put(CODE,(String)requestTokenResponse.getResponseParameters().get(REQUEST_TOKEN));
                postParametersMap.put(CLIENT_ID,oAuthConfiguration.getApplicationId());
                postParametersMap.put(CLIENT_SECRET,oAuthConfiguration.getApplicationSecret());
                postParametersMap.put(REDIRECT_URI,oAuthConfiguration.getRedirectUrl());
                postParametersMap.put(GRANT_TYPE,AUTHORIZATION_CODE);

                writer.write(getUriQueryString(postParametersMap));
                writer.close();
                os.close();
                LOGGER.info(" connecting with facebook.com .....");
                con.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                //setting up the response code
                oAuthResponse.setHttpResponseCode(con.getResponseCode());

                if(con.getResponseCode() == HTTP_OK){
                    LOGGER.info(" response was successfully received from the facebook.com ");
                    String  responseString = reader.readLine();
                    if (responseString != null)
                    {
                        LOGGER.info("extracting the response parameters and assign them to array");
                        Map<Object,Object> responseParametersMap = OAuthUtil.populateUriQueryStringToMap(responseString);
                        oAuthResponse.setResponseParameters(responseParametersMap);
                    }
                }
                else{
                    LOGGER.info(" access token response was not successful and error code  [{}] received", con.getResponseCode());
                    throw new OAuthException(ACCESS_TOKEN_NOT_RECEIVED, String.format("access token response was not successful and error code  [%d] received", con.getResponseCode()));
                }
                con.disconnect();
            }
            catch (MalformedURLException ex) {
                LOGGER.debug(" MalformedURLException occurred with the ACCESS_TOKEN_ENDPOINT URL [{}] of the FacebookProvider class ", ACCESS_TOKEN_ENDPOINT);
                throw new OAuthException(" MalformedURLException occurred with FacebookProvider ACCESS_TOKEN_ENDPOINT URL ["+ACCESS_TOKEN_ENDPOINT+"] ",ex);
            }
            catch (UnsupportedEncodingException ex) {
                LOGGER.debug("URL Encoder [{}] is not supported ", URL_ENCODE);
                throw new OAuthException(String.format("URL Encode [%s] is not supported", URL_ENCODE),ex);
            }
            catch (ProtocolException ex) {
                LOGGER.debug(" Selected HTTP Request method [{}] does not support with Access Token Endpoint [{}]", HTTP_POST, ACCESS_TOKEN_ENDPOINT);
                throw new OAuthException(String.format("Selected HTTP Request method [%s] does not support with Access Token Endpoint [%s]", HTTP_POST, ACCESS_TOKEN_ENDPOINT),ex);
            }
            catch (IOException ex) {
                LOGGER.debug(" IOException occurred in the FacebookProvider class and exception message [{}]", ex.getMessage());
                throw new OAuthException("IOException occurred in the FacebookProvider class",ex);
            }
        }
        return oAuthResponse;
    }


    @Override
    public OAuthResponse getAccessTokenForRequestToken(String requestToken) throws OAuthException {
        LOGGER.info(" get the access token from the request token [{}]", requestToken);
        if(requestToken!=null){
            OAuthResponse oAuthResponse =  new OAuthResponse();
            responseParamMap.put(REQUEST_TOKEN,requestToken);
            oAuthResponse.setResponseParameters(responseParamMap);
            //reusing the implementation of the getAccessToken method
            return this.getAccessToken(oAuthResponse);
        }
        else {
            LOGGER.info(" invalid request token [{}]", requestToken);
            throw new OAuthException(" request token is null");
        }
    }

    @Override
    public OAuthResponse getProtectedResource(OAuthResponse accessTokenResponse) throws OAuthException{
        OAuthResponse oAuthResponse =  new OAuthResponse();
        if(isTokenResponseValid(accessTokenResponse,ACCESS_TOKEN)){
            try{
                String url  = String.format(PROTECTED_RESOURCE_ENDPOINT,URLEncoder.encode((String)accessTokenResponse.getResponseParameters().get(ACCESS_TOKEN),URL_ENCODE));
                URL u = new URL (url);
                HttpURLConnection con = (HttpURLConnection) u.openConnection ();
                con.setDoInput(true);
                con.setRequestMethod(HTTP_GET);
                LOGGER.info(" trying to make a connection with [{}]", url);
                con.connect();
                LOGGER.info("connected and response code [{}]", con.getResponseCode());
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                //setting up the http response code
                oAuthResponse.setHttpResponseCode(con.getResponseCode());
                if(con.getResponseCode() == HTTP_OK){
                    //if the response was successfully received
                    String  responseString = reader.readLine();
                    LOGGER.info(" received the response [{}]", responseString);
                    JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
                    Map<Object,Object> jsonDataMap = parseJsonToMap(jsonObject);
                    oAuthResponse.setResponseParameters(jsonDataMap);
                }
                else{
                    LOGGER.info("protected resource not found ");
                    throw new OAuthException(PROTECTED_RESOURCE_NOT_FOUND," protected resource data not retrieved");
                }
                con.disconnect();
                LOGGER.info(" connection terminated ");
            }
            catch (MalformedURLException ex) {
                LOGGER.debug(" MalformedURLException occurred with the ACCESS_TOKEN_ENDPOINT URL [{}] of the FacebookProvider class ", ACCESS_TOKEN_ENDPOINT);
                throw new OAuthException(" MalformedURLException occurred with FacebookProvider ACCESS_TOKEN_ENDPOINT URL ["+ACCESS_TOKEN_ENDPOINT+"] ",ex);
            }
            catch (UnsupportedEncodingException ex) {
                LOGGER.debug("URL Encoder [{}] is not supported ", URL_ENCODE);
                throw new OAuthException(String.format("URL Encode [%s] is not supported", URL_ENCODE),ex);
            }
            catch (ProtocolException ex) {
                LOGGER.debug(" Selected HTTP Request method [{}] does not support with Access Token Endpoint [{}]", HTTP_GET, ACCESS_TOKEN_ENDPOINT);
                throw new OAuthException(String.format("Selected HTTP Request method [%s] does not support with Access Token Endpoint [%s]", HTTP_GET, ACCESS_TOKEN_ENDPOINT),ex);
            }
            catch (IOException ex) {
                LOGGER.debug(" IOException occurred in the FacebookProvider class and exception message [{}]", ex.getMessage());
                throw new OAuthException("IOException occurred in the FacebookProvider class",ex);
            }
        }
        return oAuthResponse;
    }

    /**
     * <p>
     *     build the query string using the provided map of parameters.
     * </p>
     * @param paramMap will be {@link Map} and that contains set of parameters as key and value paris
     * @return  built Uri Query String as {@link String}
     * @throws OAuthException
     */
    private String getUriQueryString(Map<String,String> paramMap) throws OAuthException{
        String url = "";
        for (Map.Entry<String, String> stringStringEntry : paramMap.entrySet()) {
            Map.Entry thisEntry = (Map.Entry) stringStringEntry;
            try {
                url = url +thisEntry.getKey()+"="+URLEncoder.encode((String)thisEntry.getValue(),URL_ENCODE)+"&";
            } catch (UnsupportedEncodingException ex) {
                LOGGER.debug("URL Encoder [{}] is not supported ", URL_ENCODE);
                throw new OAuthException(String.format("URL Encode [%s] is not supported", URL_ENCODE),ex);
            }
        }
        if(url.length()!=0){
            //removing last ampersand and construct the URL
            url = url.substring(0,url.length()-1);
        }
        return url;
    }

    /**
     * <p>
     * extracting the data(as key and value) from the json object and populate those extracted data in a
     * java Map as key and value pairs
     * </p>
     * @param jsonObject  instance of  {@link JSONObject}
     * @return Map<Object,Object> that contains extracted key and value pair from the json object
     */
    private Map<Object,Object> parseJsonToMap(JSONObject jsonObject){
        LOGGER.debug(" parsing JSON response [{}] and store response data in java.util.Map ", jsonObject);
        Map<Object,Object> facebookDataMap =  new HashMap<Object, Object>();
        //extracting data from json object and populate them in Map
        facebookDataMap.put(ID,jsonObject.get(ID));
        facebookDataMap.put(NAME,jsonObject.get(NAME));
        facebookDataMap.put(FIRST_NAME,jsonObject.get(FIRST_NAME));
        facebookDataMap.put(LAST_NAME,jsonObject.get(LAST_NAME));
        facebookDataMap.put(USERNAME,jsonObject.get(USERNAME));
        facebookDataMap.put(GENDER,jsonObject.get(GENDER));
        facebookDataMap.put(HOME_TOWN,jsonObject.getJSONObject(HOME_TOWN).get(NAME));
        facebookDataMap.put(LOCATION,jsonObject.getJSONObject(LOCATION).get(NAME));
        return facebookDataMap;
    }

    /**
     *<p>
     *     check the validity of the given oauth response against the provided token type
     *</p>
     * @param oAuthResponse as {@link OAuthResponse}
     * @param tokenType as {@link String}
     * @return true if the given response is valid with the token type. otherwise returns false.
     * @throws OAuthException
     */
    private boolean isTokenResponseValid(OAuthResponse oAuthResponse,String tokenType) throws OAuthException{
        if(oAuthResponse!=null && tokenType!=null){
            //check whether the given token is available in the response parameters
            if(oAuthResponse.getResponseParameters().containsKey(tokenType)){
                return true;
            }
            else {
                LOGGER.info("response does not contain the Token [{}]", tokenType);
                throw new OAuthException(TOKEN_MISSING, String.format("response does not contain the token [%s]", tokenType));
            }
        }
        else if(tokenType==null){
            throw new OAuthException(INVALID_TOKEN_TYPE,"Invalid token type.... Token type is null");
        }
        else{
            throw new OAuthException(INVALID_OAUTH_RESPONSE,"Invalid oauth response....");
        }
    }
}
