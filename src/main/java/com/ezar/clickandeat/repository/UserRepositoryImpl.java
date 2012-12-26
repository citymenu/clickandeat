
package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.User;
import com.ezar.clickandeat.security.PasswordEncoder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.Date;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

public class UserRepositoryImpl implements UserRepositoryCustom {

    private static final Logger LOGGER = Logger.getLogger(UserRepositoryImpl.class);
    
    @Autowired
    private MongoOperations operations;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public User findByUsername(String username) {
        return operations.findOne(query(where("username").is(username)),User.class);
    }


    @Override
    public User saveUser(User user) {
        user.setSalt(generateSalt());
        user.setPassword(passwordEncoder.encodePassword(user.getPassword(),user.getSalt()));
        operations.save(user);
        return user;
    }


    @Override
    public void updatePassword(String username, String password) {
        User user = findByUsername(username);
        String encodedPassword = passwordEncoder.encodePassword(password,user.getSalt());
        operations.updateFirst(query(where("id").is(user.getId())),update("password",encodedPassword),User.class);
    }


    @Override
    public boolean authenticate(String username, String password) throws SecurityException {
        User user = findByUsername(username);
        if( user == null ) {
            throw new SecurityException("User not found");
        }
        return passwordEncoder.isPasswordValid(user.getPassword(), password,  user.getSalt());
    }

    private String generateSalt() {
        return "" + Math.round((new Date().getTime() * Math.random()));
    }

}
