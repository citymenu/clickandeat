package com.ezar.clickandeat.validator.excel;

import com.ezar.clickandeat.validator.ValidationErrors;

public interface ExcelObjectValidator<T> {

    void validate(T in, ValidationErrors errors, String sheetName, int rowIndex, int colIndex );
    
}
