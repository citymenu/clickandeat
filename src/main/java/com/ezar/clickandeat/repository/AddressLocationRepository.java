package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.AddressLocation;
import org.springframework.data.repository.CrudRepository;

public interface AddressLocationRepository extends CrudRepository<AddressLocation,String> {
    
    AddressLocation findByAddress(String address);

}

