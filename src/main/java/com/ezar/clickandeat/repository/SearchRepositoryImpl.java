package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.util.SequenceGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class SearchRepositoryImpl implements SearchRepositoryCustom {

    private static final Logger LOGGER = Logger.getLogger(SearchRepositoryImpl.class);
    
    @Autowired
    private MongoOperations operations;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Override
    public Search create(String location,List<String> cuisines,String sort,String dir) {
        Search search = new Search(location,cuisines,sort,dir);
        search.setSearchId(sequenceGenerator.getNextSequence());
        return search;
    }

    @Override
    public Search findBySearchId(String searchId) {
        return operations.findOne(query(where("searchId").is(searchId)),Search.class);
    }

}
