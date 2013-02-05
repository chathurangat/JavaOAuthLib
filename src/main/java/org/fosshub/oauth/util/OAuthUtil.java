package org.fosshub.oauth.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

//todo log4j integration
public class OAuthUtil {

    public static String generateRandomString(){
        //todo implementation should be changed
        return getMD5EncodedValue(Long.toString(12 + (int)(Math.random()*1000)));
    }

    public static String getMD5EncodedValue(String input){
        String md5 = null;
        if(input!=null){
            try{
                //Create MessageDigest object for MD5
                MessageDigest digest = MessageDigest.getInstance("MD5");
                //Update input string in message digest
                digest.update(input.getBytes(), 0, input.length());
                //Converts message digest value in base 16 (hex)
                md5 = new BigInteger(1, digest.digest()).toString(16);
            } catch (NoSuchAlgorithmException e) {
                //todo exception handling
                e.printStackTrace();
            }
            return md5;
        }
        else{
            return md5;
        }
    }


    public static Map<String,String>  populateUriQueryStringToMap(String uriQueryString){

        Map<String,String> responseParameterMap = new HashMap<String, String>();
        if(uriQueryString!=null){
            String responseArray[] = uriQueryString.split("&");
            for(String responseElement:responseArray){
                String responseParamArray[] = responseElement.split("=");
                System.out.println(" response key ["+responseParamArray[0]+"] and value ["+responseParamArray[1]+"]");
                responseParameterMap.put(responseParamArray[0],responseParamArray[1]);
            }
        }
        return responseParameterMap;
    }
}
