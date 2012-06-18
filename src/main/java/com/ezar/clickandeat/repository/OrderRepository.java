package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order,String>, OrderRepositoryCustom {

}
