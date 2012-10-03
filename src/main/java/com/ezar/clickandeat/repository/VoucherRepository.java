package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.User;
import org.springframework.data.repository.CrudRepository;

public interface VoucherRepository extends CrudRepository<User,String>, VoucherRepositoryCustom {

}
