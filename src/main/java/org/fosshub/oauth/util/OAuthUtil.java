package org.fosshub.oauth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.fosshub.oauth.config.OAuthKeyBox.MD5;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public final class OAuthUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthUtil.class);
    private static final int BASE_16=16;
    /**
     * <p>
     *  constructor visibility should be private because the class
     *  contains only the static methods.
     * </p>
     */
    private OAuthUtil(){

    }
    /**
     *<p>
     *     generate random text(String)
     *</p>
     * @return randomly generated text as {@link String}
     */
    public static String generateRandomString(){
            return md5Encoder(Double.toString(generateRandomNumber()));
    }

    /**
     * <p>
     *     generate a random number within the given minimum and maximum range
     * </p>
     * @return the generated random number as {@link Double}
     */
    private static double generateRandomNumber(){
        return (Math.random() + (Math.random() * Math.random()));
    }

    /**
     * <p>
     *     method used to convert the given input parameter for the md5 encoded value
     * </p>
     * @param inputValue as {@link String}
     * @return the md5 encoded input parameter as {@link String}  or returns null if any exception occurs
     */
    public static String md5Encoder(String inputValue){
            try{
                //Create MessageDigest object for MD5
                MessageDigest digest = MessageDigest.getInstance(MD5);
                //Update input string in message digest
                digest.update(inputValue.getBytes(), 0, inputValue.length());
                //Converts message digest value in base 16 (hex)
                return new BigInteger(1, digest.digest()).toString(BASE_16);
            } catch (NoSuchAlgorithmException ex) {
                LOGGER.debug(" exception occurred while encoding [{}] value with MD5 algorithm.. exception message [{}]", inputValue, ex.getMessage());
                return null;
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
        LOGGER.debug(" extracting key and value pairs from the given URI Query String [{}]", uriQueryString);
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
