package com.ezar.clickandeat.validator.excel;

import com.ezar.clickandeat.validator.AbstractObjectValidator;
import com.ezar.clickandeat.validator.ValidationErrors;

import java.util.Arrays;
import java.util.List;

public class ExcelObjectValidatorImpl<T> implements ExcelObjectValidator<T> {

    private final AbstractObjectValidator<T> internalValidator;

    private static final List<String> columnNames = Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");
    
    /**
     * @param internalValidator
     */

    public ExcelObjectValidatorImpl(AbstractObjectValidator<T> internalValidator) {
        this.internalValidator = internalValidator;
    }

    @Override
    public void validate(T in, ValidationErrors errors, String sheetName, int rowIndex, int colIndex ) {
        String className = in.getClass().getSimpleName();
        ValidationErrors internalErrors = internalValidator.validate(in);
        for( String error: internalErrors.getErrors()) {
            errors.addError("[" + sheetName + " - " + columnNames.get(colIndex) + rowIndex + "] " + className + " -> " + error);
        }
    }
}
