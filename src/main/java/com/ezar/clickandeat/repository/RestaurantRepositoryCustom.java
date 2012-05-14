package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RestaurantRepositoryCustom {

    List<Restaurant> findRestaurantsServingLocation(double[] location);

}
