package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person,String> {
}

