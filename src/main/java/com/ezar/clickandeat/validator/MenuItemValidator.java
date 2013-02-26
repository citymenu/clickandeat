package com.ezar.clickandeat.validator;

import com.ezar.clickandeat.model.MenuCategory;
import com.ezar.clickandeat.model.MenuItem;
import org.springframework.util.StringUtils;

public class MenuItemValidator extends AbstractObjectValidator<MenuItem> {

    @Override
    public void validateObject(MenuItem menuItem, ValidationErrors errors) {
        if( !StringUtils.hasText(menuItem.getTitle())) {
            errors.addError("Menu item name is required");
        }
    }
}
