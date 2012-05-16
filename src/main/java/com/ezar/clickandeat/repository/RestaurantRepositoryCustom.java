package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.User;

import java.util.List;

public interface RestaurantRepositoryCustom {

    Restaurant findByRestaurantId(String restaurantId);

    Restaurant saveRestaurant(Restaurant restaurant);
    
    List<Restaurant> findRestaurantsServingPostCode(String postCode);

}
