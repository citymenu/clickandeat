package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.User;
import com.ezar.clickandeat.repository.UserRepository;
import com.ezar.clickandeat.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserRegistrationController {

    @Autowired
    private UserRepository repository;
    
    @Autowired
    private UserValidator userValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SaltSource saltSource;
    
    @RequestMapping(value="/secure/doRegister.html", method = RequestMethod.POST)
    public String register( @ModelAttribute("user") User user, BindingResult result ) {

        userValidator.validate(user, result);
        if (result.hasErrors()) {
            return "register";
        }
        else {
            user.setSalt(user.makeSalt());
            user.setPassword(passwordEncoder.encodePassword(user.getPassword(),user.getSalt()));
            repository.save(user);
            return "home";
        }
    }

}
