package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import org.springframework.data.repository.CrudRepository;

public interface RestaurantRepository extends CrudRepository<Restaurant,String> {

}
