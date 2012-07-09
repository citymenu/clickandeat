package com.ezar.clickandeat.validator;

import com.ezar.clickandeat.model.Person;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("personValidator")
public class PersonValidator implements Validator, InitializingBean {

    private static final String EMAIL_VALIDATOR = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@(([0-9a-zA-Z])+([-\\w]*[0-9a-zA-Z])*\\.)+[a-zA-Z]{2,9})$";

    private Pattern emailPattern;


    @Override
    public void afterPropertiesSet() throws Exception {
        emailPattern = Pattern.compile(EMAIL_VALIDATOR);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Person person = (Person)target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstName.required", "Required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastName.required", "Required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.required", "Required field");
        if( !errors.hasFieldErrors("email")) {
            Matcher matcher = emailPattern.matcher(person.getEmail());
            if( !matcher.matches()) {
                errors.rejectValue("email","email.invalid","Invalid Email Address");
            }
        }
        if( !errors.hasFieldErrors("email")) {
            if( !person.getEmail().equals(person.getConfirmEmail())) {
                errors.rejectValue("email","confirmEmail.invalid","Email Addresses Do Not Match");
            }
        }
        if( !StringUtils.hasText(person.getTelephone()) && !StringUtils.hasText(person.getMobile())) {
            errors.rejectValue("telephone","contact.invalid","Enter A Telephone Or Mobile");
            errors.rejectValue("mobile","contact.invalid","Enter A Telephone Or Mobile");
        }
    }

}
