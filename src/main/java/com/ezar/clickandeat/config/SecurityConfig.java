package com.ezar.clickandeat.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import com.ezar.clickandeat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Configuration
public class SecurityConfig implements UserDetailsService {

	private String adminUsername;
	
	private String adminPassword;
	
	private String adminRoles;
	
	@Value(value="${admin.username}")
	public void setAdminUsername(String adminUsername) {
		this.adminUsername = adminUsername;
	}

	@Value(value="${admin.password}")
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	@Value(value="${admin.roles}")
	public void setAdminRoles(String adminRoles) {
		this.adminRoles = adminRoles;
	}


	private @Autowired MongoOperations mongoTemplate;

	
	@Bean(name="userService")
	public UserDetailsService getUserDetailsService() {
		return this;
	}
	
	
	/**
	 * @param username
	 */
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		User user = mongoTemplate.findOne(query(where("username").is(username)), User.class);
		if( user == null ) {
			throw new UsernameNotFoundException(username);
		}
		return user;
	}
	
	
	@PostConstruct
	public void prepare() throws Exception {
		User admin = mongoTemplate.findOne(query(where("username").is(adminUsername)), User.class);
		if( admin == null ) {
			String[] roles = StringUtils.commaDelimitedListToStringArray(adminRoles);
			admin = new User(adminUsername, adminPassword,Arrays.asList(roles));
			mongoTemplate.save(admin);
		}
	}
	
}
