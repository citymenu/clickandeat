package com.ezar.clickandeat.util;

import org.springframework.stereotype.Component;

@Component
public class ScrapedNumberProvider {
    
    private static String defaultPhone = "+34971156089";
    private static String defaultSms = "+34648798100";
    private static String defaultEmail = "restaurante.sincorreo@llamarycomer.com";
    
    private String notificationPhoneNumber = defaultPhone;
    
    private String notificationSMSNumber = defaultSms;

    private String notificationEmail = defaultEmail;


    public String getNotificationPhoneNumber() {
        return notificationPhoneNumber;
    }

    public void setNotificationPhoneNumber(String notificationPhoneNumber) {
        this.notificationPhoneNumber = notificationPhoneNumber;
    }

    public String getNotificationSMSNumber() {
        return notificationSMSNumber;
    }

    public void setNotificationSMSNumber(String notificationSMSNumber) {
        this.notificationSMSNumber = notificationSMSNumber;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }
}
