package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.util.Pair;

import java.util.List;
import java.util.Map;

public interface RestaurantRepositoryCustom {

    Restaurant create();
    
    Restaurant findByRestaurantId(String restaurantId);

    Restaurant saveRestaurant(Restaurant restaurant);

    List<Restaurant> getRecommended();
    
    void deleteRestaurant(Restaurant restaurant);

    Pair<List<Restaurant>,Map<String,Integer>> search(Search search);

}
