package com.ezar.clickandeat.security;

public class OAuthServiceConfig {

    private String apiKey;

    private String apiSecret;

    private String callback;

    private Class apiClass;


    public OAuthServiceConfig() {
    }


    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public Class getApiClass() {
        return apiClass;
    }

    public void setApiClass(Class apiClass) {
        this.apiClass = apiClass;
    }
}
