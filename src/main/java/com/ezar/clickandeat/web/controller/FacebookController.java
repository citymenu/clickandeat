package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.security.OAuthServiceProvider;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.context.request.RequestAttributes.*;
import static com.ezar.clickandeat.security.OAuthServiceProvider.ATTR_OAUTH_REQUEST_TOKEN;

//@Controller
public class FacebookController {

    @Autowired
    @Qualifier("facebookServiceProvider")
    private OAuthServiceProvider facebookServiceProvider;

    private static final Token EMPTY_TOKEN = null;

    @RequestMapping(value={"/login-facebook"}, method = RequestMethod.GET)
    public String login(WebRequest request) {
        Token accessToken = (Token) request.getAttribute(ATTR_OAUTH_REQUEST_TOKEN, SCOPE_SESSION);
        if(accessToken == null) {
            OAuthService service = facebookServiceProvider.getService();
            request.setAttribute(ATTR_OAUTH_REQUEST_TOKEN, EMPTY_TOKEN, SCOPE_SESSION);
            return "redirect:" + service.getAuthorizationUrl(EMPTY_TOKEN);
        }
        return "welcomePage";
    }

    @RequestMapping(value={"/facebook-callback"}, method = RequestMethod.GET)
    public ModelAndView callback(@RequestParam(value="code", required=false) String oauthVerifier, WebRequest request) {
        OAuthService service = facebookServiceProvider.getService();
        Token requestToken = (Token) request.getAttribute(ATTR_OAUTH_REQUEST_TOKEN, SCOPE_SESSION);
        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        request.setAttribute(ATTR_OAUTH_REQUEST_TOKEN, accessToken, SCOPE_SESSION);
        OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
        service.signRequest(accessToken, oauthRequest);
        Response oauthResponse = oauthRequest.send();
        System.out.println(oauthResponse.getBody());
        return new ModelAndView("redirect:loginPage");
    }

}
