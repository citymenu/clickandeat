package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Sequence;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Component(value="sequenceRepository")
public class SequenceRepository {

    private static final Logger LOGGER = Logger.getLogger(SequenceRepository.class);
    
    private static final String REDIS_KEY = "sequence";
    
    @Autowired
    private MongoOperations operations;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * @param name
     * @return
     */
    
    public Long getNextCounter(final String name) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting next sequence counter for name " + name);
        }

        Sequence sequence = operations.findOne(query(where("name").is(name)),Sequence.class);
        if( sequence == null ) {
            sequence = new Sequence(name,1l);
            operations.insert(sequence);
        }

        Long nextCounter = sequence.getCounter() + 1;
        operations.updateFirst(query(where("name").is(name)), update("counter", nextCounter),Sequence.class);
        
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Returning counter " + nextCounter + " for name " + name);
        }
        
        return nextCounter;
    }
    
}
