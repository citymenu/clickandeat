package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.PostCodeLocation;
import org.springframework.data.repository.CrudRepository;

public interface PostCodeLocationRepository extends CrudRepository<PostCodeLocation,String> {
    
    PostCodeLocation findByPostCode(String postCode);

}

