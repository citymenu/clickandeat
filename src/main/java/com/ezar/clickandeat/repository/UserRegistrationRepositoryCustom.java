package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.UserRegistration;
import com.ezar.clickandeat.web.controller.helper.Filter;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRegistrationRepositoryCustom {

    /**
     * @param userRegistration
     * @return
     */

    UserRegistration saveUserRegistration(UserRegistration userRegistration);


    List<UserRegistration> page(Pageable pageable, List<Filter> filters);

    long count(List<Filter> filters);

}
