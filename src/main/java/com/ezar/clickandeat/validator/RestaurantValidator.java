package com.ezar.clickandeat.validator;

import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.AddressLocation;
import com.ezar.clickandeat.model.Restaurant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(value="restaurantValidator")
public class RestaurantValidator extends AbstractObjectValidator<Restaurant> implements InitializingBean {

    @Autowired
    private LocationService locationService;

    private int maxRadiusMetres;

    private String regexp;

    private Pattern pattern;

    @Override
    public void afterPropertiesSet() throws Exception {
        pattern = Pattern.compile(regexp);
    }

    
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
            String location = address.getAddress1() + " " + address.getPostCode();
            Matcher matcher = pattern.matcher(location);
            if( !matcher.matches()) {
                errors.addError("Please ensure a valid postcode is entered");
            }
        }
            
            
        
        if( !errors.hasErrors()) {
            AddressLocation addressLocation = locationService.getSingleLocation(address,true);
            if( addressLocation == null ) {
                errors.addError("Unable to determine location, please check address");
            }
            else if( addressLocation.getRadius() > maxRadiusMetres ) {
                errors.addError("Unable to determine precise location, please check address");
            }
        }
    }


    @Required
    @Value(value="${location.maxRadiusMetres}")
    public void setMaxRadiusMetres(int maxRadiusMetres) {
        this.maxRadiusMetres = maxRadiusMetres;
    }

    @Required
    @Value(value="${location.validationRegexp}")
    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

}
