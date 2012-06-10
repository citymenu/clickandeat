package com.ezar.clickandeat.model;

public class NotificationOptions {

    boolean receiveNotificationCall;
    
    boolean takeOrderOverTelephone;

    boolean receiveSMSNotification;
    
    String notificationPhoneNumber;
    
    String notificationSMSNumber;
    
    String notificationEmailAddress;

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

    public String getNotificationEmailAddress() {
        return notificationEmailAddress;
    }

    public void setNotificationEmailAddress(String notificationEmailAddress) {
        this.notificationEmailAddress = notificationEmailAddress;
    }
}
