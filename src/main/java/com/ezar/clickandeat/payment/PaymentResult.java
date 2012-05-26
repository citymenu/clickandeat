package com.ezar.clickandeat.payment;

public class PaymentResult {
    
    private String transactionId;

    private String authorizationCode;
    
    private boolean approved;
    
    private boolean declined;
    
    private boolean error;
    
    private boolean review;
    
    private String responseText;
    
    private int responseCode;
    
    private String responseDescription;
    
    private String reasonResponseNotes;
    
    private int reasonResponseCode;
    
    private String reasonResponseText;
    
    private int reasonResponseResponseCode;
    
    private String reasonResponseResponseDescription;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined(boolean declined) {
        this.declined = declined;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getReasonResponseNotes() {
        return reasonResponseNotes;
    }

    public void setReasonResponseNotes(String reasonResponseNotes) {
        this.reasonResponseNotes = reasonResponseNotes;
    }

    public int getReasonResponseCode() {
        return reasonResponseCode;
    }

    public void setReasonResponseCode(int reasonResponseCode) {
        this.reasonResponseCode = reasonResponseCode;
    }

    public String getReasonResponseText() {
        return reasonResponseText;
    }

    public void setReasonResponseText(String reasonResponseText) {
        this.reasonResponseText = reasonResponseText;
    }

    public int getReasonResponseResponseCode() {
        return reasonResponseResponseCode;
    }

    public void setReasonResponseResponseCode(int reasonResponseResponseCode) {
        this.reasonResponseResponseCode = reasonResponseResponseCode;
    }

    public String getReasonResponseResponseDescription() {
        return reasonResponseResponseDescription;
    }

    public void setReasonResponseResponseDescription(String reasonResponseResponseDescription) {
        this.reasonResponseResponseDescription = reasonResponseResponseDescription;
    }

    @Override
    public String toString() {
        return "PaymentResult{" +
                "transactionId='" + transactionId + '\'' +
                ", authorizationCode='" + authorizationCode + '\'' +
                ", approved=" + approved +
                ", declined=" + declined +
                ", error=" + error +
                ", review=" + review +
                ", responseText='" + responseText + '\'' +
                ", responseCode=" + responseCode +
                ", responseDescription='" + responseDescription + '\'' +
                ", reasonResponseNotes='" + reasonResponseNotes + '\'' +
                ", reasonResponseCode=" + reasonResponseCode +
                ", reasonResponseText='" + reasonResponseText + '\'' +
                ", reasonResponseResponseCode=" + reasonResponseResponseCode +
                ", reasonResponseResponseDescription='" + reasonResponseResponseDescription + '\'' +
                '}';
    }
}
