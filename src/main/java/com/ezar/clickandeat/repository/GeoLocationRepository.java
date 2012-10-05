package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.GeoLocation;
import org.springframework.data.repository.CrudRepository;

public interface GeoLocationRepository extends CrudRepository<GeoLocation,String>, GeoLocationRepositoryCustom {

}

