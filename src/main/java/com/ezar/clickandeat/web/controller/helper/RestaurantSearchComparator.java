package com.ezar.clickandeat.web.controller.helper;

import com.ezar.clickandeat.model.Restaurant;

import java.util.Comparator;

public class RestaurantSearchComparator implements Comparator<Restaurant> {

    @Override
    public int compare(Restaurant restaurant1, Restaurant restaurant2) {

        //First deal with those restaurants that support phone orders only
        if( !restaurant1.getPhoneOrdersOnly() && restaurant2.getPhoneOrdersOnly()) {
            return -1;
        }
        else if( restaurant1.getPhoneOrdersOnly() && !restaurant2.getPhoneOrdersOnly()) {
            return 1;
        }

        if( restaurant1.getOpen() && !restaurant2.getOpen()) {
            return -1;
        }
        else if( !restaurant1.getOpen() && restaurant2.getOpen()) {
            return 1;
        }
        else if( restaurant1.getSearchRanking() == restaurant2.getSearchRanking()) {
            double distanceDiff = restaurant1.getDistanceToSearchLocation() - restaurant2.getDistanceToSearchLocation();
            if( distanceDiff == 0 ) {
                return restaurant1.getName().compareTo(restaurant2.getName());
            }
            else if( distanceDiff < 0 ) {
                return -1;
            }
            else {
                return 1;
            }
        }
        else {
            int rankingDifference = restaurant1.getSearchRanking() - restaurant2.getSearchRanking();
            if( rankingDifference < 0 ) {
                return 1;
            }
            else {
                return -1;
            }
        }
    }
    
}
