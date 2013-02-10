package org.fosshub.oauth.provider;

import org.fosshub.oauth.config.OAuthConfiguration;
import org.fosshub.oauth.exception.OAuthException;
import org.fosshub.oauth.http.OAuthResponse;
import org.testng.annotations.Test;

import java.util.Map;

import static org.fosshub.oauth.http.OAuthResponseCode.OAUTH_RESPONSE_SUCCESS;

public class FacebookProviderTest {

    @Test
    public void testGetAuthorizationUrl(){
        OAuthConfiguration oAuthConfiguration =  new OAuthConfiguration();
        oAuthConfiguration.setApplicationId("429200197112086");
        oAuthConfiguration.setApplicationSecret("9c27359552c74fa273e9085d47a72881");
        oAuthConfiguration.setRedirectUrl("http://localhost/PhpOAuthLib/OAuthLib/examples/Facebook/FacebookExample.php");
        oAuthConfiguration.setState("1234");

        try{
        FacebookProvider facebookProvider = new FacebookProvider(oAuthConfiguration);
        String url = facebookProvider.getAuthorizationUrl();

        System.out.println("Authorization URL ["+url+"]");

        OAuthResponse oAuthResponse = facebookProvider.getProtectedResource("AAAGGWvVJURYBAEwZBcxx9EWAUC7KdL416J2jbffpXPAcxzK31fqkFtN8pPBS8iZAoch3OrZAZAQvT0gyfQU8iPFxWz8dfsghqV9MGBNfjQZDZD");

            if(oAuthResponse.getResponseCode()== OAUTH_RESPONSE_SUCCESS){
            Map<Object,Object> resourceDataMap = oAuthResponse.getResponseParameters();

            for(Map.Entry<Object,Object> entry:resourceDataMap.entrySet()){

                System.out.println(" key ["+entry.getKey()+"] and value ["+entry.getValue()+"]");
            }
        }
        }
        catch (OAuthException e) {
            System.out.println(" exception received and ex ["+e+"]");
            System.out.println(" exception message ["+e.getMessage()+"]");
            System.out.println(" oauth exception message ["+e.getExceptionMessage()+"]");
        }

    }
}
