package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Search;

import java.util.List;
import java.util.Map;

public interface RestaurantRepositoryCustom {

    Restaurant create();
    
    Restaurant findByRestaurantId(String restaurantId);

    Restaurant saveRestaurant(Restaurant restaurant);

    void deleteRestaurant(Restaurant restaurant);

    List<Restaurant> search(Search search);

    Map<String,Integer> getCuisineCountByLocation(String location);

    Map<String,Integer> getLocationCountByCuisine(String cuisine);
    
    boolean willDeliverToLocationOrPostCode(Restaurant restaurant, double[] location, String postCode);
    
}
