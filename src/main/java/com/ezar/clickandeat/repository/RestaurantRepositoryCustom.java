package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.util.Pair;
import com.ezar.clickandeat.web.controller.helper.Filter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

public interface RestaurantRepositoryCustom {

    Restaurant create();
    
    Restaurant findByRestaurantId(String restaurantId);

    Restaurant findByName(String name);

    Restaurant findByExternalId(String externalId);

    Restaurant saveRestaurant(Restaurant restaurant);

    void addRestaurantUpdate(String restaurantId, String restaurantUpdate);

    List<Restaurant> getRecommended();
    
    void deleteRestaurant(Restaurant restaurant);

    List<Restaurant> getPage(Pageable pageable);

    List<Restaurant> pageByRestaurantName(Pageable pageable, String restaurantName, List<Filter> filters);

    List<Restaurant> quickLaunch();

    List<Restaurant> listAllActive();

    long count(String restaurantName, List<Filter> filters);

    long countActive();

    Pair<List<Restaurant>,SortedMap<String,Integer>> search(Search search);

    Map<String,Integer> getRestaurantLocationCount();

    Map<String,List<String>> getCuisinesByLocation();

}
