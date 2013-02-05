package org.fosshub.oauth.provider;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.http.HttpStatus;
import org.fosshub.oauth.api.impl.OAuth2Impl;
import org.fosshub.oauth.config.OAuthConfiguration;
import org.fosshub.oauth.http.OAuthResponse;
import org.fosshub.oauth.util.OAuthUtil;

import java.io.*;
import java.net.*;
import java.util.*;

//todo exception handling
//todo log4j integration
public class FacebookProvider extends OAuth2Impl {

    private static final String REQUEST_TOKEN_ENDPOINT = "https://www.facebook.com/dialog/oauth?response_type=%s&client_id=%s&redirect_uri=%s&state=%s";
    private static final String ACCESS_TOKEN_ENDPOINT = "https://graph.facebook.com/oauth/access_token";
    private static final String PROTECTED_RESOURCE_ENDPOINT = "https://graph.facebook.com/me?access_token=%s";
    private static final String URL_ENCODE = "UTF-8";

    public FacebookProvider(OAuthConfiguration oAuthConfiguration){
        this.oauthConfiguration = oAuthConfiguration;
    }

    @Override
    public String getAuthorizationUrl() {
        String authorizationUrl=null;
        try {
            authorizationUrl = String.format(REQUEST_TOKEN_ENDPOINT, URLEncoder.encode("code", URL_ENCODE) ,URLEncoder.encode(oauthConfiguration.getApplicationId(), URL_ENCODE), URLEncoder.encode(oauthConfiguration.getRedirectUrl(), URL_ENCODE),URLEncoder.encode(oauthConfiguration.getState(), URL_ENCODE));

        } catch (UnsupportedEncodingException e) {
            //todo exception handling
            e.printStackTrace();
        }
        return authorizationUrl;
    }

    @Override
    public String getRequestToken() {
        //todo implementation should go here
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //todo implement this method
    public String getRequestToken(String authorizedUrl){
        try{
            URL u = new URL (authorizedUrl);
            HttpURLConnection con = (HttpURLConnection) u.openConnection ();
            con.setDoInput(true);
            con.setRequestMethod("POST");
            System.out.println(" connecting .....");
            con.connect();
            System.out.println(" connected");
            String response =  con.getResponseMessage();
            con.disconnect ();
            System.out.println("disconnected ["+response+"]");
        }
        catch (Exception ex){
            System.out.println(" exception occured request token ");
        }
        return null;
    }



    @Override
    public OAuthResponse getAccessToken(String requestToken) {
        //todo implementation should go here
        OAuthResponse oAuthResponse =  new OAuthResponse();
        try{
            URL u = new URL (ACCESS_TOKEN_ENDPOINT);
            HttpURLConnection con = (HttpURLConnection) u.openConnection ();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            Map<String,String> postParametersMap = new HashMap<String, String>();

            postParametersMap.put("code",requestToken);
            postParametersMap.put("client_id",oauthConfiguration.getApplicationId());
            postParametersMap.put("client_secret",oauthConfiguration.getApplicationSecret());
            postParametersMap.put("redirect_uri",oauthConfiguration.getRedirectUrl());
            postParametersMap.put("grant_type","authorization_code");
            writer.write(getUriQueryString(postParametersMap));
            writer.close();
            os.close();

            System.out.println(" connecting with facebook.com .....");
            con.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            //setting up the response code
            oAuthResponse.setResponseCode(con.getResponseCode());

            if(con.getResponseCode() == HttpStatus.SC_OK){
                //if the response was successfully received
                String  responseString = reader.readLine();
                if (responseString != null)
                {
                    //extracting the response parameters and assign them to array
                    Map<String,String> responseParametersMap = OAuthUtil.populateUriQueryStringToMap(responseString);
                    oAuthResponse.setResponseParameters(responseParametersMap);
                }
            }

            System.out.println(" connected and response code ["+con.getResponseCode()+"]");
            con.disconnect ();
            System.out.println("disconnected \n");
        }
        catch (Exception ex){
//            //todo handle exceptions
            System.out.println(" exception occurred "+ex);
            System.out.println(" exception message "+ex.getMessage());

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
            oAuthResponse.setResponseCode(con.getResponseCode());

            if(con.getResponseCode() == HttpStatus.SC_OK){
                System.out.println(" getting protected resource \n");
                //if the response was successfully received
                String  responseString = reader.readLine();
                System.out.println(" response ["+responseString+"] \n");

                JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
                System.out.println(" json "+jsonObject);

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


    private String getUriQueryString(Map<String,String> paramMap){
        String url = "";
        for (Map.Entry<String, String> stringStringEntry : paramMap.entrySet()) {
            Map.Entry thisEntry = (Map.Entry) stringStringEntry;
            try {
                url = url +thisEntry.getKey()+"="+URLEncoder.encode((String)thisEntry.getValue(),URL_ENCODE)+"&";
            } catch (UnsupportedEncodingException e) {
                //todo handle error message
                e.printStackTrace();
            }
        }
        if(url.length()!=0){
            //removing last ampersand
            url = url.substring(0,url.length()-1);
        }
        System.out.println(" url ["+url+"]");
        return url;
    }
}
