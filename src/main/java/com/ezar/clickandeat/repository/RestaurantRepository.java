package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RestaurantRepository extends PagingAndSortingRepository<Restaurant,String>, RestaurantRepositoryCustom {

}
