package org.fosshub.oauth.util;

import org.fosshub.oauth.exception.OAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class OAuthUtil {

    private static final Logger logger = LoggerFactory.getLogger(OAuthUtil.class);
    /**
     *<p>
     *     generate random text(String)
     *</p>
     * @return randomly generated text as {@link String}
     */
    public static String generateRandomString(){
        return md5Encoder(Long.toString(generateRandomNumber(10,10000)));
    }

    /**
     * <p>
     *     generate a random number within the given minimum and maximum range
     * </p>
     * @param minRange  minimum value range as {@link Integer}
     * @param maxRange  maximum value range as {@link Integer}
     * @return the generated random number as {@link Integer}
     */
    private static int generateRandomNumber(int minRange,int maxRange){
        return (minRange + (int) (Math.random() * maxRange));
    }

    /**
     * <p>
     *     method used to convert the given input parameter for the md5 encoded value
     * </p>
     * @param inputValue as {@link String}
     * @return the md5 encoded input parameter as {@link String}
     */
    public static String md5Encoder(String inputValue){
        if(inputValue!=null){
            try{
                //Create MessageDigest object for MD5
                MessageDigest digest = MessageDigest.getInstance("MD5");
                //Update input string in message digest
                digest.update(inputValue.getBytes(), 0, inputValue.length());
                //Converts message digest value in base 16 (hex)
                return new BigInteger(1, digest.digest()).toString(16);
            } catch (NoSuchAlgorithmException ex) {
                logger.debug(" exception occurred while encoding [{}] value with MD5 algorithm.. exception message [{}]", inputValue, ex.getMessage());
                throw new OAuthException("MD5 encoding algorithm is not supported",ex);
            }
        }
        else{
            throw new OAuthException("Invalid input value for the MD5 encoding");
        }
    }

    /**
     * <p>
     *     extract the key and value pair(s) from the given URI string and store those
     *     extracted key and value pairs making use of {@link Map}
     * </p>
     * @param uriQueryString  valid URI query string as {@link String}
     * @return extracted key and value pair populated {@link Map}
     */
    public static Map<Object,Object>  populateUriQueryStringToMap(String uriQueryString){
        Map<Object,Object> responseParameterMap = new HashMap<Object, Object>();
        logger.debug(" extracting key and value pairs from the given URI Query String [{}]",uriQueryString);
        if(uriQueryString!=null){
            String responseArray[] = uriQueryString.split("&");
            for(String responseElement:responseArray){
                String responseParamArray[] = responseElement.split("=");
                responseParameterMap.put(responseParamArray[0],responseParamArray[1]);
            }
        }
        return responseParameterMap;
    }
}
