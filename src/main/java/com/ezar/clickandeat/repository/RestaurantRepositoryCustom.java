package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.model.User;

import java.util.List;

public interface RestaurantRepositoryCustom {

    Restaurant create();
    
    Restaurant findByRestaurantId(String restaurantId);

    Restaurant saveRestaurant(Restaurant restaurant);

    void deleteRestaurant(Restaurant restaurant);

    List<Restaurant> search(Search search);
    
}
