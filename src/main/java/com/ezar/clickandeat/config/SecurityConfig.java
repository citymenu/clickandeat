package com.ezar.clickandeat.config;

import com.ezar.clickandeat.model.User;
import com.ezar.clickandeat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class SecurityConfig implements UserDetailsService {

	@Autowired
    private UserRepository repository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
	@Bean(name="userService")
	public UserDetailsService getUserDetailsService() {
		return this;
	}


	/**
	 * @param username
	 */
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        User user = repository.findByUsername(username);
		if( user == null ) {
			throw new UsernameNotFoundException(username);
		}
		return user;
	}
	
	

}
