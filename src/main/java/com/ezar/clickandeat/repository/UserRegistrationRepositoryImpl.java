package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.UserRegistration;
import com.ezar.clickandeat.repository.util.FilterUtils;
import com.ezar.clickandeat.web.controller.helper.Filter;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.QueryUtils;

import java.util.List;

public class UserRegistrationRepositoryImpl implements UserRegistrationRepositoryCustom {

    private static final Logger LOGGER = Logger.getLogger(UserRegistrationRepositoryImpl.class);
    
    @Autowired
    private MongoOperations operations;

    @Override
    public UserRegistration saveUserRegistration(UserRegistration userRegistration) {
        if( userRegistration.getCreated() == null ) {
            userRegistration.setCreated(new DateTime());
        }
        operations.save(userRegistration);
        return userRegistration;
    }


    @Override
    public List<UserRegistration> page(Pageable pageable, List<Filter> filters) {
        Query query = new Query();
        FilterUtils.applyFilters(query, filters);
        QueryUtils.applyPagination(query, pageable);
        return operations.find(query,UserRegistration.class);

    }

    @Override
    public long count(List<Filter> filters) {
        Query query = new Query();
        FilterUtils.applyFilters(query,filters);
        return operations.count(query,UserRegistration.class);
    }
}
