package com.ezar.clickandeat.validator;

public abstract class AbstractObjectValidator<T> implements ObjectValidator<T> {

    @Override
    public final ValidationErrors validate(T obj) {
        ValidationErrors errors = new ValidationErrors();
        validateObject(obj, errors);
        return errors;
    }

    /**
     * @param obj
     * @param errors
     */

    public abstract void validateObject(T obj, ValidationErrors errors );
    
}
