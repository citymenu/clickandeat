package com.ezar.clickandeat.model;

public class NotificationOptions {

    boolean receiveNotificationCall;
    
    boolean takeOrderOverTelephone;

    boolean receiveSMSNotification;
    
    String phoneNotificationNumber;
    
    String smsNotificationNumber;
    
    String emailAddress;

    public boolean isReceiveNotificationCall() {
        return receiveNotificationCall;
    }

    public void setReceiveNotificationCall(boolean receiveNotificationCall) {
        this.receiveNotificationCall = receiveNotificationCall;
    }

    public boolean isTakeOrderOverTelephone() {
        return takeOrderOverTelephone;
    }

    public void setTakeOrderOverTelephone(boolean takeOrderOverTelephone) {
        this.takeOrderOverTelephone = takeOrderOverTelephone;
    }

    public boolean isReceiveSMSNotification() {
        return receiveSMSNotification;
    }

    public void setReceiveSMSNotification(boolean receiveSMSNotification) {
        this.receiveSMSNotification = receiveSMSNotification;
    }

    public String getPhoneNotificationNumber() {
        return phoneNotificationNumber;
    }

    public void setPhoneNotificationNumber(String phoneNotificationNumber) {
        this.phoneNotificationNumber = phoneNotificationNumber;
    }

    public String getSmsNotificationNumber() {
        return smsNotificationNumber;
    }

    public void setSmsNotificationNumber(String smsNotificationNumber) {
        this.smsNotificationNumber = smsNotificationNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
