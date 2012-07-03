package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Search;

import java.util.List;

public interface SearchRepositoryCustom {

    Search create(String location,List<String> cuisines,String sort,String dir);

    Search findBySearchId(String searchId);

}
