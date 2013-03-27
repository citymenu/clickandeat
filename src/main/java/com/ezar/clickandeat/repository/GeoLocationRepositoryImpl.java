package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.cache.ClusteredCache;
import com.ezar.clickandeat.model.GeoLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class GeoLocationRepositoryImpl implements GeoLocationRepositoryCustom {

    @Autowired
    private MongoOperations operations;

    @Autowired
    private ClusteredCache clusteredCache;

    
    @Override
    public GeoLocation findByAddress(String address) {
        if( address == null ) {
            throw new IllegalArgumentException("address must not be null");
        }
        GeoLocation geoLocation;
        geoLocation = clusteredCache.get(GeoLocation.class, address);
        if( geoLocation == null ) {
            geoLocation = operations.findOne(query(where("address").is(address)),GeoLocation.class);
            if( geoLocation != null ) {
                clusteredCache.store(GeoLocation.class,address,geoLocation);
            }
        }
        return geoLocation;
    }

    
    @Override
    public GeoLocation saveGeoLocation(GeoLocation geoLocation) {
        clusteredCache.remove(GeoLocation.class, geoLocation.getAddress());
        operations.save(geoLocation);
        return geoLocation;
    }

    
}
