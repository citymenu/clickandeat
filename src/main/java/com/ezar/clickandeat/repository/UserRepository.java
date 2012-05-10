package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,String> {

    User findByUsername(String username);
    
}
