package com.ezar.clickandeat.validator;

public interface ObjectValidator<T> {
    
    ValidationErrors validate( T obj );
    
}
