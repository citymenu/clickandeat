package com.ezar.clickandeat.validator;

import com.ezar.clickandeat.model.User;
import com.ezar.clickandeat.repository.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("userValidator")
public class UserValidator implements Validator, InitializingBean {

    private static final String EMAIL_VALIDATOR = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@(([0-9a-zA-Z])+([-\\w]*[0-9a-zA-Z])*\\.)+[a-zA-Z]{2,9})$";

    private Pattern postCodePattern;
    private Pattern emailPattern;

    @Autowired
    private UserRepository repository;
    
    private String postCodeValidationRegexp;

    @Override
    public void afterPropertiesSet() throws Exception {
        postCodePattern = Pattern.compile(postCodeValidationRegexp);
        emailPattern = Pattern.compile(EMAIL_VALIDATOR);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        User user = (User)target;

        if( StringUtils.hasText(user.getUsername())) {
            User existing = repository.findByUsername(user.getUsername());
            if( existing != null && !existing.getId().equals(user.getId())) {
                errors.rejectValue("username","email.alreadyRegistered","Email Address already registered");
            }
        }
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "person.firstName", "firstName.required", "Required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "person.lastName", "lastName.required", "Required field");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.address1", "address1.required", "Required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.postCode", "postCode.required", "Required field");
        if( !errors.hasFieldErrors("address.postCode")) {
            Matcher matcher = postCodePattern.matcher(user.getAddress().getPostCode());
            if( !matcher.matches()) {
                errors.rejectValue("address.postCode","postCode.invalid","Invalid Postcode");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "email.required", "Required field");
        if( !errors.hasFieldErrors("username")) {
            Matcher matcher = emailPattern.matcher(user.getUsername());
            if( !matcher.matches()) {
                errors.rejectValue("username","email.invalid","Invalid Email Address");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.required", "Required field");
        if( !errors.hasFieldErrors("password")) {
            if( user.getPassword().length() < 6 ) {
                errors.rejectValue("password","password.minLength","Password must be at least 6 characters");
            }
            else if( !user.getPassword().equals(user.getConfirmPassword())) {
                errors.rejectValue("confirmPassword","password.notmatch","Passwords Should Match");
            }
        }
    }

    @Required
    @Value(value="${validation.postCodeValidationRegexp}")
    public void setPostCodeValidationRegexp(String postCodeValidationRegexp ) {
        this.postCodeValidationRegexp = postCodeValidationRegexp;
    }


}
