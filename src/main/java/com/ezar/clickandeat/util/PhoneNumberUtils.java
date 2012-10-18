package com.ezar.clickandeat.util;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.model.Person;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberUtils {
    
    private static final Logger LOGGER = Logger.getLogger(PhoneNumberUtils.class);

    private static final Map<String,String> mobileNumberRegexpMap = new HashMap<String,String>();

    private static final Map<String,String> internationalNumberPrefixMap = new HashMap<String, String>();

    private static final Map<String,Integer> internationalNumberTrimMap = new HashMap<String, Integer>();
    
    static {
        mobileNumberRegexpMap.put("en_UK", "07[0-9]{9}");
        mobileNumberRegexpMap.put("es_ES", "[6|7][0-9]{8}");

        internationalNumberPrefixMap.put("en_UK", "+44");
        internationalNumberPrefixMap.put("es_ES", "+34");

        internationalNumberTrimMap.put("en_UK", 1);
        internationalNumberTrimMap.put("es_ES", 0);
    }


    /**
     * @param phoneNumber
     * @return
     */

    public static boolean isMobileNumber(String phoneNumber) {

        if( !StringUtils.hasText(phoneNumber)) {
            return false;
        }

        String locale = MessageFactory.getLocaleString();
        String trimmedPhoneNumber = phoneNumber.replace(" ","").replace("-","");
        String prefix = internationalNumberPrefixMap.get(locale);
        int trim = internationalNumberTrimMap.get(locale);
        if( trimmedPhoneNumber.startsWith(prefix)) {
            trimmedPhoneNumber = trimmedPhoneNumber.replace(prefix,"");
            trimmedPhoneNumber = "0000".substring(0,trim) + trimmedPhoneNumber;
        }

        String regexp = mobileNumberRegexpMap.get(MessageFactory.getLocaleString());
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(trimmedPhoneNumber);
        return matcher.matches();
    }


    /**
     * @param phoneNumber
     * @return
     */

    public static String getInternationalNumber(String phoneNumber) {
        String locale = MessageFactory.getLocaleString();
        int trim = internationalNumberTrimMap.get(locale);
        String prefix = internationalNumberPrefixMap.get(locale);
        String trimmedPhoneNumber = phoneNumber.replace(" ","").replace("-","");
        if( trimmedPhoneNumber.startsWith(prefix)) {
            return trimmedPhoneNumber;
        }
        String internationalNumber = prefix + trimmedPhoneNumber.substring(trim);
        LOGGER.info("Generated international number: " + internationalNumber);
        return internationalNumber;
    }

}
