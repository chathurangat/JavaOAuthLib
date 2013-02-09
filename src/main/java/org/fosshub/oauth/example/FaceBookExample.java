package org.fosshub.oauth.example;

import org.fosshub.oauth.config.OAuthConfiguration;
import org.fosshub.oauth.provider.FacebookProvider;

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

        String authorizationUrl = facebookProvider.getAuthorizationUrl();

        System.out.println(" Authorization URL retrieved ["+authorizationUrl+"]\n\n");

        System.out.println(" Click on Authorization URL and get the Request Token\n\n");

    }
}
