package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.util.Pair;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public interface RestaurantRepositoryCustom {

    Restaurant create();
    
    Restaurant findByRestaurantId(String restaurantId);

    Restaurant findByName(String name);

    Restaurant saveRestaurant(Restaurant restaurant);

    void addRestaurantUpdate(String restaurantId, String restaurantUpdate);

    List<Restaurant> getRecommended();
    
    void deleteRestaurant(Restaurant restaurant);

    List<Restaurant> getPage(Pageable pageable);

    List<Restaurant> quickLaunch();

    long countActive();

    Pair<List<Restaurant>,SortedMap<String,Integer>> search(Search search);

    Map<String,List<String>> getCuisinesByLocation();
    
}
