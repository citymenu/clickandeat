package com.ezar.clickandeat.validator;

import com.ezar.clickandeat.model.MenuCategory;
import org.springframework.util.StringUtils;

public class MenuCategoryValidator extends AbstractObjectValidator<MenuCategory> {

    @Override
    public void validateObject(MenuCategory menuCategory, ValidationErrors errors) {
        if( !StringUtils.hasText(menuCategory.getName())) {
            errors.addError("Menu category type is required");
        }
        if( !StringUtils.hasText(menuCategory.getType())) {
            errors.addError("Menu category type is required");
        }
    }
}
