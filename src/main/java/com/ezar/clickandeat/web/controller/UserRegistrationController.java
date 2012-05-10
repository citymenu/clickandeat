package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.User;
import com.ezar.clickandeat.repository.AddressRepository;
import com.ezar.clickandeat.repository.PersonRepository;
import com.ezar.clickandeat.repository.UserRepository;
import com.ezar.clickandeat.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
public class UserRegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private LocationService locationService;
    
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

            // Get geolocation of user from password
            double[] location = locationService.getLocation(user.getAddress().getPostCode(),"en");
            user.getAddress().setLocation(location);

            // Pre-save person and address
            user.setPerson(personRepository.save(user.getPerson()));
            user.setAddress(addressRepository.save(user.getAddress()));

            // Encrypt user password and save user record
            user.setSalt(user.makeSalt());
            user.setPassword(passwordEncoder.encodePassword(user.getPassword(),user.getSalt()));
            userRepository.save(user);

            // Auto-authenticate user
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword(),new ArrayList<GrantedAuthority>());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Return to home page
            return "redirect:/home.html";
        }
    }

}
