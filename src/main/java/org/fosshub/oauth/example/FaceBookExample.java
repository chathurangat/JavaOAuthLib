package org.fosshub.oauth.example;

import org.fosshub.oauth.config.OAuthConfiguration;
import org.fosshub.oauth.exception.OAuthException;
import org.fosshub.oauth.http.OAuthResponse;
import org.fosshub.oauth.provider.FacebookProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fosshub.oauth.config.OAuthKeyBox.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public abstract class FaceBookExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(FaceBookExample.class);

    private static final String APPLICATION_ID = "429200197112086";
    private static final String APPLICATION_SECRET = "9c27359552c74fa273e9085d47a72881";
    private static final String REDIRECT_URL = "http://localhost/PhpOAuthLib/OAuthLib/examples/Facebook/FacebookExample.php";
    private static final String STATE = "123";


    public static void main(String[] args) {
        LOGGER.info(" Facebook OAuth Example ");

        OAuthConfiguration oAuthConfiguration =  new OAuthConfiguration();
        oAuthConfiguration.setApplicationId(APPLICATION_ID);
        oAuthConfiguration.setApplicationSecret(APPLICATION_SECRET);
        oAuthConfiguration.setRedirectUrl(REDIRECT_URL);
        oAuthConfiguration.setState(STATE);

        FacebookProvider facebookProvider =  new FacebookProvider(oAuthConfiguration);

        System.out.println(" getting the Authorization URL for the facebook.com network\n\n");

        try {
            String authorizationUrl = facebookProvider.getAuthorizationUrl();
            System.out.println(" Authorization URL retrieved ["+authorizationUrl+"]\n\n");

            System.out.println(" Click on Authorization URL and get the Request Token\n\n");

            System.out.print(" Enter Request token here >>");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String requestToken = reader.readLine();
            System.out.println(" request token received as user input ["+requestToken+"]\n\n");
            OAuthResponse accessTokenResponse = facebookProvider.getAccessTokenForRequestToken(requestToken);

            String  accessToken = (String)accessTokenResponse.getResponseParameters().get(ACCESS_TOKEN);
            System.out.println(" Access token received ["+accessToken+"] \n\n");

            System.out.println(" getting the protected resource .....\n\n");

            OAuthResponse protectedResourceResponse = facebookProvider.getProtectedResource(accessTokenResponse);

            System.out.println("displaying the protected resources retrieved ...... \n\n");
            Map<Object,Object> protectedResourceData  =   protectedResourceResponse.getResponseParameters();
            for(Map.Entry<Object,Object> entry:protectedResourceData.entrySet()){
                System.out.println(" resource key ["+entry.getKey()+"] and resource value ["+entry.getValue()+"]");
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
