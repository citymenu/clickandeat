package com.ezar.clickandeat.config;

import com.ezar.clickandeat.model.User;
import com.ezar.clickandeat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.PostConstruct;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Configuration
public class SecurityConfig implements UserDetailsService {

	private String adminUsername;
	
	private String adminPassword;

	@Value(value="${admin.username}")
	public void setAdminUsername(String adminUsername) {
		this.adminUsername = adminUsername;
	}

	@Value(value="${admin.password}")
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

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
	
	
	@PostConstruct
	public void prepare() throws Exception {
		User admin = repository.findByUsername(adminUsername);
        if( admin != null ) {
            repository.delete(admin);
        }
    	admin = new User();
        admin.setUsername(adminUsername);
        admin.setSalt(admin.makeSalt());
        admin.setPassword(passwordEncoder.encodePassword(adminPassword,admin.getSalt()));
		repository.save(admin);
	}
	
}
