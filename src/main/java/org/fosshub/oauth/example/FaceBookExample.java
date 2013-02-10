package org.fosshub.oauth.example;

import org.fosshub.oauth.config.OAuthConfiguration;
import org.fosshub.oauth.exception.OAuthException;
import org.fosshub.oauth.http.OAuthResponse;
import org.fosshub.oauth.provider.FacebookProvider;
import static org.fosshub.oauth.http.OAuthResponseCode.*;
import static org.fosshub.oauth.config.OAuthKeyBox.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class FaceBookExample {

    private static final String APPLICATION_ID = "429200197112086";
    private static final String APPLICATION_SECRET = "9c27359552c74fa273e9085d47a72881";
    private static final String REDIRECT_URL = "http://localhost/PhpOAuthLib/OAuthLib/examples/Facebook/FacebookExample.php";
    private static final String STATE = "123";


    public static void main(String[] args) {
        System.out.println(" Facebook OAuth Example ");

        OAuthConfiguration oAuthConfiguration =  new OAuthConfiguration();
        oAuthConfiguration.setApplicationId(APPLICATION_ID);
        oAuthConfiguration.setApplicationSecret(APPLICATION_SECRET);
        oAuthConfiguration.setRedirectUrl(REDIRECT_URL);
        oAuthConfiguration.setState(STATE);

        FacebookProvider facebookProvider =  new FacebookProvider(oAuthConfiguration);

        System.out.println(" getting the Authorization URL for the facebook.com network\n\n");

        String authorizationUrl = null;
        try {
            authorizationUrl = facebookProvider.getAuthorizationUrl();
            System.out.println(" Authorization URL retrieved ["+authorizationUrl+"]\n\n");

            System.out.println(" Click on Authorization URL and get the Request Token\n\n");

            System.out.println(" Enter Request token here ");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String requestToken = reader.readLine();
            System.out.println(" request token received as user input ["+requestToken+"]\n\n");
            OAuthResponse accessTokenResponse = facebookProvider.getAccessToken(requestToken);

            if(accessTokenResponse.getResponseCode()==OAUTH_RESPONSE_SUCCESS){
                //getting the access token

                String  accessToken = (String)accessTokenResponse.getResponseParameters().get(ACCESS_TOKEN);
                System.out.println(" Access token received ["+accessToken+"] \n\n");

                System.out.println(" getting the protected resource .....\n\n");

                OAuthResponse protectedResourceResponse = facebookProvider.getProtectedResource(accessToken);

                if(protectedResourceResponse.getResponseCode()==OAUTH_RESPONSE_SUCCESS){
                    System.out.println("displaying the protected resources retrieved ...... \n\n");
                    Map<Object,Object> protectedResourceData  =   protectedResourceResponse.getResponseParameters();
                    for(Map.Entry<Object,Object> entry:protectedResourceData.entrySet()){
                        System.out.println(" resource key ["+entry.getKey()+"] and esource value ["+entry.getValue()+"]");
                    }
                }

            }
            else {
                //print the error message
                String errorMessage = (String)accessTokenResponse.getResponseParameters().get(ERROR_CODE);
                System.out.println("Error is "+errorMessage);
            }

        } catch (OAuthException e) {
            System.out.println("oauth exception occurred ["+e.getExceptionMessage()+"]\n\n");
            System.out.println(" exception occurred ["+e.getMessage()+"]\n\n");
        }
        catch (IOException e) {
            System.out.println("exception occurred while getting request token from user ["+e.getMessage()+"]");
        }

    }
}
