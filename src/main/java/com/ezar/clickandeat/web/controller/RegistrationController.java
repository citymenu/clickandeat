package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.model.UserRegistration;
import com.ezar.clickandeat.repository.UserRegistrationRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class RegistrationController {

    private static final Logger LOGGER = Logger.getLogger(RegistrationController.class);
    
    @Autowired
    private UserRegistrationRepository userRegistrationRepository;
    

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/register/registerLocation.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> registerLocation(HttpServletRequest request, @RequestParam(value="email") String email,
                                                   @RequestParam(value="discount") Double discount ) throws Exception {

        try {
            HttpSession session = request.getSession(true);
            Search search = (Search)session.getAttribute("search");
            UserRegistration userRegistration = new UserRegistration();
            userRegistration.setEmailAddress(email);
            userRegistration.setRequestedDiscount(discount);
            userRegistration.setRemoteIpAddress(request.getRemoteAddr());
            if( search != null ) {
                userRegistration.setLocation(search.getLocation());
            }
            userRegistrationRepository.saveUserRegistration(userRegistration);
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>("".getBytes("utf-8"), headers, HttpStatus.OK);    
    }
    
}
