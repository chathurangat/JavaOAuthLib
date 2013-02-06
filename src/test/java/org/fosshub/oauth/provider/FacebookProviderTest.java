package org.fosshub.oauth.provider;

import org.apache.http.HttpStatus;
import org.fosshub.oauth.config.OAuthConfiguration;
import org.fosshub.oauth.http.OAuthResponse;
import org.testng.annotations.Test;

import java.util.Map;

public class FacebookProviderTest {

    @Test
    public void testGetAuthorizationUrl() throws Exception {
        OAuthConfiguration oAuthConfiguration =  new OAuthConfiguration();
        oAuthConfiguration.setApplicationId("429200197112086");
        oAuthConfiguration.setApplicationSecret("9c27359552c74fa273e9085d47a72881");
        oAuthConfiguration.setRedirectUrl("http://localhost/PhpOAuthLib/OAuthLib/examples/Facebook/FacebookExample.php");
        oAuthConfiguration.setState("1234");

        FacebookProvider facebookProvider = new FacebookProvider(oAuthConfiguration);
        String url = facebookProvider.getAuthorizationUrl();
        System.out.println("Authorzation URL ["+url+"]");

//        OAuthResponse oAuthResponse = facebookProvider.getAccessToken("AQDwFGeijYMCDQc2MsGVLu6jKlnCrWw33xziWC3Dcu0AmskvtwRGIqQtlNU7pzKc-O_O4sI1vjSgTM0VYsZQbR6YXIkVBbfGOlxIoDOteHsZB5jCFpVHNRAI_LFY41ju84UtSJz_ycm3DQn9oMeHSAwDwChIMvyZNJPDoNhHr9nRFt6Q8H00-Mgj0-8Ee__ZNvBF1e-alXVoMGa9e1NzAQwz");

//        if(oAuthResponse.getResponseCode()== HttpStatus.SC_OK){
//            System.out.println(" Access Token ["+oAuthResponse.getResponseParameters().get("access_token")+"]\n");

//            facebookProvider.getProtectedResource(oAuthResponse.getResponseParameters().get("access_token"));
           OAuthResponse oAuthResponse = facebookProvider.getProtectedResource("AAAGGWvVJURYBAEwZBcxx9EWAUC7KdL416J2jbffpXPAcxzK31fqkFtN8pPBS8iZAoch3OrZAZAQvT0gyfQU8iPFxWz8dfsghqV9MGBNfjQZDZD");

        if(oAuthResponse.getResponseCode()== HttpStatus.SC_OK){
            Map<Object,Object> resourceDataMap = oAuthResponse.getResponseParameters();

            for(Map.Entry<Object,Object> entry:resourceDataMap.entrySet()){

                System.out.println(" key ["+entry.getKey()+"] and value ["+entry.getValue()+"]");
            }
        }
//        }
//        else{
//            System.out.println(" error occurred and error code ["+oAuthResponse.getResponseCode()+"]");
//        }
    }
}
