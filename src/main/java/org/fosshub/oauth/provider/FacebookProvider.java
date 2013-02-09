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
import static org.fosshub.oauth.config.OAuthKeyBox.*;
import static org.fosshub.oauth.http.OAuthResponseCode.*;
import java.io.*;
import java.net.*;
import java.util.*;

//todo exception handling
//todo log4j integration
//todo define constants for oauth request parameters
//todo handle json parsing
public class FacebookProvider extends OAuth2Impl {

    private static final Logger logger  = LoggerFactory.getLogger(FacebookProvider.class);

    private static final String REQUEST_TOKEN_ENDPOINT = "https://www.facebook.com/dialog/oauth?response_type=%s&client_id=%s&redirect_uri=%s&state=%s";
    private static final String ACCESS_TOKEN_ENDPOINT = "https://graph.facebook.com/oauth/access_token";
    private static final String PROTECTED_RESOURCE_ENDPOINT = "https://graph.facebook.com/me?access_token=%s";
    private static final String URL_ENCODE = "UTF-8";

    public FacebookProvider(OAuthConfiguration oAuthConfiguration){
        this.oauthConfiguration = oAuthConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthorizationUrl() throws OAuthException{
        try {
            return String.format(REQUEST_TOKEN_ENDPOINT, URLEncoder.encode("code", URL_ENCODE) ,URLEncoder.encode(oauthConfiguration.getApplicationId(), URL_ENCODE), URLEncoder.encode(oauthConfiguration.getRedirectUrl(), URL_ENCODE),URLEncoder.encode(oauthConfiguration.getState(), URL_ENCODE));
        } catch (UnsupportedEncodingException ex) {
            logger.debug("URL Encoder [{}] is not supported ",URL_ENCODE);
            throw new OAuthException("URL Encode ["+URL_ENCODE+"] is not supported",ex);
        }
    }

    @Override
    public OAuthResponse getRequestToken(HttpServletRequest request) {
        //todo error handling with error codes
        OAuthResponse oAuthResponse =  new OAuthResponse();
        Map<Object,Object> responseParamMap  = new HashMap<Object, Object>();
        if(request!=null){
            Enumeration headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()) {
                String headerName = (String)headerNames.nextElement();
                System.out.println(" header name ["+headerName+"] and value ["+request.getHeader(headerName)+"]");
            }
            if(request.getParameterMap().containsKey(CODE)){
                logger.info(" request token was found ");
                responseParamMap.put(REQUEST_TOKEN,request.getParameter(CODE));
                oAuthResponse.setResponseCode(OAUTH_RESPONSE_SUCCESS);
                oAuthResponse.setResponseParameters(responseParamMap);
            }
            else if(request.getParameterMap().containsKey("error")){
                responseParamMap.put(ERROR_CODE,request.getParameter("error"));
                oAuthResponse.setResponseCode(OAUTH_RESPONSE_ERROR);
                oAuthResponse.setResponseParameters(responseParamMap);
            }
            else{
                logger.info(" either request token or error code parameters are missing in HTTP GET request URL ");
                oAuthResponse.setResponseCode(OAUTH_RESPONSE_ERROR);
                //todo error messages handling
                responseParamMap.put(ERROR_CODE,"invalid_request_uri");
                oAuthResponse.setResponseParameters(responseParamMap);
            }
            return oAuthResponse;
        }
        else{
            logger.info(" HTTP request is null ");
            throw new OAuthException("Invalid HTTP Request");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public OAuthResponse getAccessToken(String requestToken) throws OAuthException{
        OAuthResponse oAuthResponse =  new OAuthResponse();
        try{
            URL u = new URL (ACCESS_TOKEN_ENDPOINT);
            HttpURLConnection con = (HttpURLConnection) u.openConnection ();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, URL_ENCODE));
            //setting up oauth request parameters
            Map<String,String> postParametersMap = new HashMap<String, String>();
            postParametersMap.put(CODE,requestToken);
            postParametersMap.put(CLIENT_ID,oauthConfiguration.getApplicationId());
            postParametersMap.put(CLIENT_SECRET,oauthConfiguration.getApplicationSecret());
            postParametersMap.put(REDIRECT_URI,oauthConfiguration.getRedirectUrl());
            postParametersMap.put(GRANT_TYPE,AUTHORIZATION_CODE);
            writer.write(getUriQueryString(postParametersMap));
            writer.close();
            os.close();
            logger.info(" connecting with facebook.com .....");
            con.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            //setting up the response code
            oAuthResponse.setHttpResponseCode(con.getResponseCode());

            if(con.getResponseCode() == OAUTH_RESPONSE_SUCCESS.getCode()){
                logger.info(" response was successfully received from the facebook.com ");
                String  responseString = reader.readLine();
                if (responseString != null)
                {
                    logger.info("extracting the response parameters and assign them to array");
                    Map<Object,Object> responseParametersMap = OAuthUtil.populateUriQueryStringToMap(responseString);
                    oAuthResponse.setResponseParameters(responseParametersMap);
                }
            }
            con.disconnect();
        }
        catch (MalformedURLException ex) {
            logger.debug(" MalformedURLException occurred with the ACCESS_TOKEN_ENDPOINT URL [{}] of the FacebookProvider class ",ACCESS_TOKEN_ENDPOINT);
            throw new OAuthException(" MalformedURLException occurred with FacebookProvider ACCESS_TOKEN_ENDPOINT URL ["+ACCESS_TOKEN_ENDPOINT+"] ",ex);
        }
        catch (UnsupportedEncodingException ex) {
            logger.debug("URL Encoder [{}] is not supported ",URL_ENCODE);
            throw new OAuthException("URL Encode ["+URL_ENCODE+"] is not supported",ex);
        }
        catch (ProtocolException ex) {
            logger.debug(" Selected HTTP Request method does not support with Access Token Endpoint [{}]",ACCESS_TOKEN_ENDPOINT);
            throw new OAuthException("Selected HTTP Request method does not support with Access Token Endpoint ["+ACCESS_TOKEN_ENDPOINT+"]",ex);
        }
        catch (IOException ex) {
            logger.debug(" IOException occurred in the FacebookProvider class and exception message [{}]",ex.getMessage());
            throw new OAuthException("IOException occurred in the FacebookProvider class",ex);
        }
        return oAuthResponse;
    }


    @Override
    public OAuthResponse getProtectedResource(String accessToken) {
        //todo implementation should go here
        OAuthResponse oAuthResponse =  new OAuthResponse();
        try{
            String url  = String.format(PROTECTED_RESOURCE_ENDPOINT,URLEncoder.encode(accessToken,URL_ENCODE));
            System.out.println(" connection established with ["+url+"]\n");
            URL u = new URL (url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection ();
            con.setDoInput(true);
            con.setRequestMethod("GET");

            System.out.println(" connecting with facebook.com for getting protected resource.....\n");
            con.connect();
            System.out.println(" connected");
            System.out.println(" response code "+con.getResponseCode());

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            //setting up the response code
            oAuthResponse.setHttpResponseCode(con.getResponseCode());

            if(con.getResponseCode() == OAUTH_RESPONSE_SUCCESS.getCode()){
                System.out.println(" getting protected resource \n");
                //if the response was successfully received
                String  responseString = reader.readLine();
                System.out.println(" response ["+responseString+"] \n");

                JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
                System.out.println(" json "+jsonObject);
                Map<Object,Object> jsonDataMap = parseJsonToMap(jsonObject);
                oAuthResponse.setResponseParameters(jsonDataMap);
            }

            System.out.println(" connected and response code ["+con.getResponseCode()+"]\n");
            con.disconnect();
            System.out.println("disconnected \n");
        }
        catch (Exception ex){
            //todo handle exceptions
            System.out.println(" exception occurred "+ex);
            System.out.println(" exception message "+ex.getMessage());
        }
        return oAuthResponse;
    }

    /**
     * <p>
     *     build the query string using the provided map of parameters.
     * </p>
     * @param paramMap will be {@link Map} and that contains set of parameters as key and value paris
     * @return  built Uri Query String as {@link String}
     */
    private String getUriQueryString(Map<String,String> paramMap){
        String url = "";
        for (Map.Entry<String, String> stringStringEntry : paramMap.entrySet()) {
            Map.Entry thisEntry = (Map.Entry) stringStringEntry;
            try {
                url = url +thisEntry.getKey()+"="+URLEncoder.encode((String)thisEntry.getValue(),URL_ENCODE)+"&";
            } catch (UnsupportedEncodingException ex) {
                logger.debug("URL Encoder [{}] is not supported ",URL_ENCODE);
                throw new OAuthException("URL Encode ["+URL_ENCODE+"] is not supported",ex);
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
        Map<Object,Object> facebookDataMap =  new HashMap<Object, Object>();
        //extracting data from json object and populate them in Map
        facebookDataMap.put("id",jsonObject.get("id"));
        facebookDataMap.put("name",jsonObject.get("name"));
        facebookDataMap.put("first_name",jsonObject.get("first_name"));
        facebookDataMap.put("last_name",jsonObject.get("last_name"));
        facebookDataMap.put("username",jsonObject.get("username"));
        facebookDataMap.put("gender",jsonObject.get("gender"));
        facebookDataMap.put("hometown",jsonObject.getJSONObject("hometown").get("name"));
        facebookDataMap.put("location",jsonObject.getJSONObject("location").get("name"));
        return facebookDataMap;
    }
}
