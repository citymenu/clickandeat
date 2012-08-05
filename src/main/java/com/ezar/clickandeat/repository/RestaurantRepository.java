package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RestaurantRepository extends PagingAndSortingRepository<Restaurant,String>, RestaurantRepositoryCustom {

}
