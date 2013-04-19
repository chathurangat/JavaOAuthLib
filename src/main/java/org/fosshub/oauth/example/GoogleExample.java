package org.fosshub.oauth.example;

import org.fosshub.oauth.config.OAuthConfiguration;
import org.fosshub.oauth.exception.OAuthException;
import org.fosshub.oauth.http.OAuthResponse;
import org.fosshub.oauth.provider.GoogleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import static org.fosshub.oauth.config.OAuthKeyBox.ACCESS_TOKEN;

public class GoogleExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(FaceBookExample.class);

    private static final String APPLICATION_ID = "669970197155-3f88p9gdvedb7taola18tr1af9k5rf29.apps.googleusercontent.com";
    private static final String APPLICATION_SECRET = "mFHSRYrLaD7EGeV9x1zoXEnz";
    private static final String REDIRECT_URL = "http://localhost:8080/oauth-webapp/google";
    private static final String STATE = "123";
    private static final String SCOPE = "https://www.googleapis.com/auth/userinfo.profile";

    public static void main(String[] args) {

        System.out.println("Google OAuth work flow");

        OAuthConfiguration oAuthConfiguration =  new OAuthConfiguration();
        oAuthConfiguration.setApplicationId(APPLICATION_ID);
        oAuthConfiguration.setApplicationSecret(APPLICATION_SECRET);
        oAuthConfiguration.setRedirectUrl(REDIRECT_URL);
        oAuthConfiguration.setScope(SCOPE);
        oAuthConfiguration.setState(STATE);

        System.out.println("Getting Authorization URL for the Google network ");

        GoogleProvider googleProvider = new GoogleProvider(oAuthConfiguration);

        try {
            String googleAuthorizationUrl = googleProvider.getAuthorizationUrl();
            System.out.println("Authorization URL for the google network ["+googleAuthorizationUrl+"]");

            System.out.println("Enter request token here >>");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String requestToken = reader.readLine();
            System.out.println(" request token received as user input ["+requestToken+"]\n\n");

            OAuthResponse accessTokenResponse = googleProvider.getAccessTokenForRequestToken(requestToken);

            String  accessToken = (String)accessTokenResponse.getResponseParameters().get(ACCESS_TOKEN);
            System.out.println(" Access token received ["+accessToken+"] \n\n");

            System.out.println(" getting the protected resource .....\n\n");

            OAuthResponse protectedResourceResponse = googleProvider.getProtectedResource(accessTokenResponse);

            System.out.println("displaying the protected resources retrieved ...... \n\n");
            Map<Object,Object> protectedResourceData  =   protectedResourceResponse.getResponseParameters();
            for(Map.Entry<Object,Object> entry:protectedResourceData.entrySet()){
                System.out.println(" resource key ["+entry.getKey()+"] and resource value ["+entry.getValue()+"]");
            }

        } catch (OAuthException e) {
            LOGGER.error(" OAuthException occurred and oauth exception message [{}]",e.getExceptionMessage());
            LOGGER.error(" OAuthException occurred and exception message [{}]", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("exception occurred while getting request token from user [{}]",e.getMessage());
        }
    }
}
