package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.UserRegistration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

public class UserRegistrationRepositoryImpl implements UserRegistrationRepositoryCustom {

    private static final Logger LOGGER = Logger.getLogger(UserRegistrationRepositoryImpl.class);
    
    @Autowired
    private MongoOperations operations;

    @Override
    public UserRegistration saveUserRegistration(UserRegistration userRegistration) {
        operations.save(userRegistration);
        return userRegistration;
    }

}
