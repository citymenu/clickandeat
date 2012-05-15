package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

public class AddressRepositoryImpl implements AddressRepositoryCustom {

    private static final Logger LOGGER = Logger.getLogger(AddressRepositoryImpl.class);
    
    @Autowired
    private LocationService locationService;

    @Autowired
    private MongoOperations operations;
    
    private String region;
    
    @Override
    public Address saveWithLocationLookup(Address address) {
        if(StringUtils.hasText(address.getPostCode())) {
            double[] location = locationService.getLocation(address.getPostCode(),region);
            address.setLocation(location);
        }
        operations.save(address);
        return address;
    }


    @Required
    @Value(value="${location.region}")
    public void setRegion(String region) {
        this.region = region;
    }
}
