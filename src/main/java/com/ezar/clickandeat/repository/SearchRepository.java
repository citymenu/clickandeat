package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Search;
import org.springframework.data.repository.CrudRepository;

public interface SearchRepository extends CrudRepository<Search,String>, SearchRepositoryCustom {

}
