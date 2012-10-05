package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.model.Order;

import java.util.List;

public interface GeoLocationRepositoryCustom {

    GeoLocation findByAddress(String address);

    GeoLocation saveGeoLocation(GeoLocation geoLocation);
    
}
