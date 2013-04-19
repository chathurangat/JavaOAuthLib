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

public class BitlyProvider extends OAuth2Impl{

    private static final Logger LOGGER = LoggerFactory.getLogger(BitlyProvider.class);

    private static final String REQUEST_TOKEN_ENDPOINT = "https://bitly.com/oauth/authorize?response_type=%s&client_id=%s&redirect_uri=%s&state=%s";
    private static final String ACCESS_TOKEN_ENDPOINT = "https://api-ssl.bitly.com/oauth/access_token";
    private static final String PROTECTED_RESOURCE_ENDPOINT = "https://api-ssl.Bitly.com/v3/user/info?access_token=%s";

    private Map<Object,Object> responseParamMap  = new HashMap<Object, Object>();
    private OAuthConfiguration oAuthConfiguration;

    //google related keys for getting protected  resource
    private static final String ID = "id";
    private static final String PICTURE = "picture";
    private static final String NAME = "name";
    private static final String FAMILY_NAME = "family_name";
    private static final String GIVEN_NAME = "given_name";

    public BitlyProvider(OAuthConfiguration oAuthConfiguration){
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
                LOGGER.info(" connecting with google.com .....");
                con.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                //setting up the response code
                oAuthResponse.setHttpResponseCode(con.getResponseCode());

                if(con.getResponseCode() == HTTP_OK){
                    LOGGER.info(" response was successfully received from the bitly.com ");
//                    String responseString = "";
//                    for (String line; (line = reader.readLine()) != null;) {
//                        responseString = responseString+line;
//                    }
//
//                    if (!responseString.equals(""))
//                    {
////                        LOGGER.info("extracting the response parameters and assign them to array");
//                        System.out.println("extracting the response parameters and assign them to array ["+responseString);
//                        JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
//                        LOGGER.info(" json response retrieved as string [{}]",responseString);
//                        Map<Object,Object> responseParametersMap = parseAccessTokenJsonResponseToMap(jsonObject);
//                        for (Map.Entry<Object, Object> entry : responseParametersMap.entrySet()) {
//                            LOGGER.info(" key [{}] and value [{}]",entry.getKey(),entry.getValue());
//                        }
//                        oAuthResponse.setResponseParameters(responseParametersMap);
//                    }

                    String responseString = "";

                    for (String line; (line = reader.readLine()) != null;) {
                        responseString = responseString+line;
                    }

                    if (!responseString.equals(""))
                    {
                        LOGGER.info("extracting the response parameters and assign them to array");
                        Map<Object,Object> responseParametersMap = OAuthUtil.populateUriQueryStringToMap(responseString);
                                                for (Map.Entry<Object, Object> entry : responseParametersMap.entrySet()) {
//                            LOGGER.info(" key [{}] and value [{}]",entry.getKey(),entry.getValue());
                                                    System.out.println(" key ["+entry.getKey()+"] and value ["+entry.getValue()+"]");
                        }
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
                throw new OAuthException(" MalformedURLException occurred with GoogleProvider ACCESS_TOKEN_ENDPOINT URL ["+ACCESS_TOKEN_ENDPOINT+"] ",ex);
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
                    String responseString = "";
                    for (String line; (line = reader.readLine()) != null;) {
                        responseString = responseString+line;
                    }
                    LOGGER.info(" received the response [{}]", responseString);
                    JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
                    Map<Object,Object> jsonDataMap = parseProtectedResourceJsonResponseToMap(jsonObject);
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
     * @param paramMap will be {@link java.util.Map} and that contains set of parameters as key and value paris
     * @return  built Uri Query String as {@link String}
     * @throws org.fosshub.oauth.exception.OAuthException
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
     *<p>
     *     check the validity of the given oauth response against the provided token type
     *</p>
     * @param oAuthResponse as {@link org.fosshub.oauth.http.OAuthResponse}
     * @param tokenType as {@link String}
     * @return true if the given response is valid with the token type. otherwise returns false.
     * @throws org.fosshub.oauth.exception.OAuthException
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


    /**
     * <p>
     * extracting the data(as key and value) from the json object and populate those extracted data in a
     * java Map as key and value pairs
     * </p>
     * @param jsonObject  instance of  {@link net.sf.json.JSONObject}
     * @return Map<Object,Object> that contains extracted key and value pair from the json object
     */
    private Map<Object,Object> parseAccessTokenJsonResponseToMap(JSONObject jsonObject){
        LOGGER.debug(" parsing JSON response [{}] and store response data in java.util.Map ", jsonObject);
        Map<Object,Object> googleAccessTokenResponseMap =  new HashMap<Object, Object>();
        //extracting data from json object and populate them in Map
        googleAccessTokenResponseMap.put(ACCESS_TOKEN,jsonObject.get(ACCESS_TOKEN));
        googleAccessTokenResponseMap.put(TOKEN_TYPE,jsonObject.get(TOKEN_TYPE));
        googleAccessTokenResponseMap.put(EXPIRES_IN,jsonObject.get(EXPIRES_IN));
        googleAccessTokenResponseMap.put(ID_TOKEN,jsonObject.get(ID_TOKEN));
        return googleAccessTokenResponseMap;
    }


    /**
     * <p>
     * extracting the data(as key and value) from the json object and populate those extracted data in a
     * java Map as key and value pairs
     * </p>
     * @param jsonObject  instance of  {@link net.sf.json.JSONObject}
     * @return Map<Object,Object> that contains extracted key and value pair from the json object
     */
    private Map<Object,Object> parseProtectedResourceJsonResponseToMap(JSONObject jsonObject){
//        LOGGER.debug(" parsing JSON response [{}] and store response data in java.util.Map ", jsonObject);
        System.out.println(" parsing JSON response ["+jsonObject+"] and store response data in java.util.Map ");
        Map<Object,Object> googleAccessTokenResponseMap =  new HashMap<Object, Object>();
        //extracting data from json object and populate them in Map
        googleAccessTokenResponseMap.put(ID,jsonObject.get(ID));
        googleAccessTokenResponseMap.put(PICTURE,jsonObject.get(PICTURE));
        googleAccessTokenResponseMap.put(NAME,jsonObject.get(NAME));
        googleAccessTokenResponseMap.put(GIVEN_NAME,jsonObject.get(GIVEN_NAME));
        googleAccessTokenResponseMap.put(FAMILY_NAME,jsonObject.get(FAMILY_NAME));
        return googleAccessTokenResponseMap;
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
