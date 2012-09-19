package com.ezar.clickandeat.validator;

import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.AddressLocation;
import com.ezar.clickandeat.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component(value="restaurantValidator")
public class RestaurantValidator extends AbstractObjectValidator<Restaurant> {

    @Autowired
    private LocationService locationService;
    
    @Override
    public void validateObject(Restaurant restaurant, ValidationErrors errors) {
        Address address = restaurant.getAddress();

        if( !StringUtils.hasText(address.getAddress1())) {
            errors.addError("Street address is required");
        }

        if( !StringUtils.hasText(address.getPostCode())) {
            errors.addError("Postcode is required");
        }
        
        if( !errors.hasErrors()) {
            AddressLocation addressLocation = locationService.getSingleLocation(address);
            if( addressLocation == null ) {
                errors.addError("Unable to determine location, please check address");
            }
            else if( addressLocation.getRadius() > 0d ) {
                errors.addError("Unable to determine precise location, please check address");
            }
        }

    }
}
