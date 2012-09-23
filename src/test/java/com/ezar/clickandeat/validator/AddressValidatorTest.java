package com.ezar.clickandeat.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddressValidatorTest {

    private AddressValidator validator;
    
    private static final String regexp = "^([A-PR-UWYZ](([0-9](([0-9]|[A-HJKSTUW])?)?)|([A-HK-Y][0-9]([0-9]|[ABEHMNPRVWXY])?)) [0-9][ABD-HJLNP-UW-Z]{2})|GIR 0AA$";

    @Before
    public void setup() throws Exception {
        validator = new AddressValidator();
        validator.setRegexp(regexp);
        validator.afterPropertiesSet();
    }

    @Test
    public void testValidAddress() throws Exception {
        String address = "E18 2LG";
        ValidationErrors errors = validator.validate(address);
        Assert.assertFalse("Should not be any errors", errors.hasErrors());
    }

    @Test
    public void testInvalidAddress() throws Exception {
        String address = "59 Bailen";
        ValidationErrors errors = validator.validate(address);
        Assert.assertTrue("Should be errors", errors.hasErrors());
    }

}
