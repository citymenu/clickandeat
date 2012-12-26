package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.User;

public interface UserRepositoryCustom {

    User findByUsername(String username);

    User saveUser(User user);

    void updatePassword (String username, String password );

    boolean authenticate(String username, String password ) throws SecurityException;
}
