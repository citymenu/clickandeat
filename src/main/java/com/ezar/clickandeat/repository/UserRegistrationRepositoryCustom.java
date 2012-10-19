package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.UserRegistration;

public interface UserRegistrationRepositoryCustom {

    /**
     * @param userRegistration
     * @return
     */

    UserRegistration saveUserRegistration(UserRegistration userRegistration);
}
