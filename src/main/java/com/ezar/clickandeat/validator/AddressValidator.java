package com.ezar.clickandeat.validator;

import com.ezar.clickandeat.model.Address;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("addressValidator")
public class AddressValidator implements Validator {

    
    @Override
    public boolean supports(Class<?> clazz) {
        return Address.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address1", "address1.required", "Required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "postCode", "postCode.required", "Required field");

    }

}
