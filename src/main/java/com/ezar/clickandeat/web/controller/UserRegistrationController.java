package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.User;
import com.ezar.clickandeat.repository.UserRepository;
import com.ezar.clickandeat.validator.UserValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;

@Controller
public class UserRegistrationController {

    private static final Logger LOGGER = Logger.getLogger(UserRegistrationController.class);
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserValidator userValidator;


    @RequestMapping(value="/secure/doRegister.html", method = RequestMethod.POST)
    public String register( @ModelAttribute("user") User user, BindingResult result ) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registering user");
        }
        
        userValidator.validate(user, result);
        if (result.hasErrors()) {
            return "register";
        }
        else {

            // Add the 'USER' role to this user
            user.addRole(User.ROLE_USER);
            
            // Save the user and update the password
            userRepository.saveUser(user);

            // Auto-authenticate user
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword(),new ArrayList<GrantedAuthority>());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Return to home page
            return "redirect:/home.html";
        }
    }

}
