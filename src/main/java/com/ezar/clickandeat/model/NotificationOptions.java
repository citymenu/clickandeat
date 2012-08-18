package com.ezar.clickandeat.model;

public class NotificationOptions {

    boolean receiveNotificationCall;
    
    boolean receiveSMSNotification;
    
    String notificationPhoneNumber;
    
    String notificationSMSNumber;
    
    String notificationEmailAddress;
    
    String printerEmailAddress;

    public boolean isReceiveNotificationCall() {
        return receiveNotificationCall;
    }

    public void setReceiveNotificationCall(boolean receiveNotificationCall) {
        this.receiveNotificationCall = receiveNotificationCall;
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

    public String getPrinterEmailAddress() {
        return printerEmailAddress;
    }

    public void setPrinterEmailAddress(String printerEmailAddress) {
        this.printerEmailAddress = printerEmailAddress;
    }
}
