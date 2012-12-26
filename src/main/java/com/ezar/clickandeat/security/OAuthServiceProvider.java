package com.ezar.clickandeat.security;

import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;

public class OAuthServiceProvider {

    public static final String ATTR_OAUTH_REQUEST_TOKEN = "OAUTH_ACCESS_TOKEN";
    
    private final OAuthServiceConfig config;

    public OAuthServiceProvider(OAuthServiceConfig config) {
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public OAuthService getService() {
        return new ServiceBuilder().provider(config.getApiClass())
                .apiKey(config.getApiKey())
                .apiSecret(config.getApiSecret())
                .callback(config.getCallback())
                .build();
    }

}
