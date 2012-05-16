
package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.util.StringUtils;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

public class UserRepositoryImpl implements UserRepositoryCustom {

    private static final Logger LOGGER = Logger.getLogger(UserRepositoryImpl.class);
    
    @Autowired
    private MongoOperations operations;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LocationService locationService;


    @Override
    public User findByUsername(String username) {
        return operations.findOne(query(where("username").is(username)),User.class);
    }


    @Override
    public User saveUser(User user) {

        if( user.getAddress() != null && StringUtils.hasText(user.getAddress().getPostCode())) {
            double[] location = locationService.getLocation(user.getAddress().getPostCode());
            user.getAddress().setLocation(location);
        }

        if( user.getId() == null ) {
            user.setSalt(user.makeSalt());
            user.setPassword(passwordEncoder.encodePassword(user.getPassword(),user.getSalt()));
            operations.save(user);
        }
        else {
            operations.updateFirst(query(where("id").is(user.getId())),
                    update("username",user.getUsername())
                    .addToSet("address",user.getAddress())
                    .addToSet("person",user.getPerson()),
                    User.class);
        }
        return user;
    }


    @Override
    public void updatePassword(String username, String password) {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating password for username: " + username);
        }
        
        User user = findByUsername(username);
        String encodedPassword = passwordEncoder.encodePassword(password,user.getSalt());
        operations.updateFirst(query(where("id").is(user.getId())),update("password",encodedPassword),User.class);
    }

}
