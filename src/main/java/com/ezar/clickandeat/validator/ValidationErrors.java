package com.ezar.clickandeat.validator;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrors {
    
    private final List<String> errors = new ArrayList<String>();
    
    public void addError(String error) {
        errors.add(error);
    }
    
    public String getErrorSummary() {
        return StringUtils.collectionToDelimitedString(errors, ", ");
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }
    
    
}
