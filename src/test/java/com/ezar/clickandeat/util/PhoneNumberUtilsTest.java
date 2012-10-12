package com.ezar.clickandeat.util;

import org.junit.Assert;
import org.junit.Test;

public class PhoneNumberUtilsTest {
    
    @Test
    public void testConvertMobileNumber() throws Exception {
        String phoneNumber = "07881 626584";
        String internationalNumber = PhoneNumberUtils.getInternationalNumber(phoneNumber);
        Assert.assertEquals("+447881626584",internationalNumber);
    }


    @Test
    public void testIsMobileNumber() throws Exception {
        String phoneNumber = "07881 626584";
        Assert.assertTrue(PhoneNumberUtils.isMobileNumber(phoneNumber));
        String internationalPhoneNumber = "+447881 626 584";
        Assert.assertTrue(PhoneNumberUtils.isMobileNumber(internationalPhoneNumber));
    }

    @Test
    public void testIsNotMobileNumber() throws Exception {
        String phoneNumber = "0208 505 7191";
        Assert.assertFalse(PhoneNumberUtils.isMobileNumber(phoneNumber));
    }

}
