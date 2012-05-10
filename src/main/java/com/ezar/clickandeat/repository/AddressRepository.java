package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.User;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address,String> {

}
