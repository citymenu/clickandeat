package com.ezar.clickandeat.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(value="addressValidator")
public class AddressValidator extends AbstractObjectValidator<String> implements InitializingBean {

    private String regexp;

    private Pattern pattern;

    @Override
    public void afterPropertiesSet() throws Exception {
        pattern = Pattern.compile(regexp);
    }

    @Override
    public void validateObject(String obj, ValidationErrors errors) {
        if( !StringUtils.hasText(obj)) {
            errors.addError("Address is required");
        }
        else {
            Matcher matcher = pattern.matcher(obj);
            if( !matcher.matches()) {
                errors.addError("Invalid location");
            }
        }
    }


    @Required
    @Value(value="${location.validationRegexp}")
    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }
}
