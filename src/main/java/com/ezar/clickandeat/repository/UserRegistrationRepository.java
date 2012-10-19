package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.UserRegistration;
import org.springframework.data.repository.CrudRepository;

public interface UserRegistrationRepository  extends CrudRepository<UserRegistration,String>, UserRegistrationRepositoryCustom {
}
